#!/usr/bin/env bash
# =====================================================================
# ysmERP —— 一键安装脚本（部署体系对齐 3x-ui 项目实现方案）
#
# 用法（root 执行）：
#   交互安装：
#     bash install.sh
#   非交互安装（云初始化 / curl | bash / cloud-init）：
#     YSMERP_NONINTERACTIVE=1 GITHUB_REPO=you/ysmERP DOMAIN=oms.example.com bash install.sh
#     （stdin 非 TTY 时自动进入非交互模式，与 3x-ui XUI_NONINTERACTIVE 机制一致）
#
# 私有仓库需先放置只读 PAT（classic，勾选 repo）：
#   echo "ghp_xxxxxxxx" > /root/.ysmERP_deploy_token
#
# 目录布局（对齐 3x-ui /usr/local/x-ui + /etc/x-ui 模式）：
#   /usr/local/ysmERP            主目录（jar、web/dist、脚本、日志、备份）
#   /etc/ysmERP                  配置目录（application.yml、install-result.env）
#   /etc/default/ysmERP          systemd 环境文件（JAVA_OPTS）
#   /etc/systemd/system/ysmERP.service
#   /usr/bin/ysmERP              管理命令软链
# 该脚本可重复执行：重复执行即视为"升级 + 重装依赖检查"，数据不受影响。
# =====================================================================
set -euo pipefail

red='\033[0;31m'
green='\033[0;32m'
blue='\033[0;34m'
yellow='\033[0;33m'
plain='\033[0m'

ysmERP_folder="${YSMERP_MAIN_FOLDER:=/usr/local/ysmERP}"
ysmERP_conf="${YSMERP_CONF_FOLDER:=/etc/ysmERP}"
ysmERP_service_dir="${YSMERP_SERVICE:=/etc/systemd/system}"

# ---------------- 检查 root（对齐 3x-ui） ----------------
[[ $EUID -ne 0 ]] && echo -e "${red}Fatal error: ${plain} Please run this script with root privilege\n" && exit 1

# ---------------- 检查 OS（对齐 3x-ui） ----------------
if [[ -f /etc/os-release ]]; then
    source /etc/os-release
    release=$ID
else
    echo "Failed to check the system OS, please contact the author!" >&2
    exit 1
fi
echo "The OS release is: ${release} ${VERSION_ID:-}"

# ---------------- 检查架构（Java 产物跨架构，仅提示） ----------------
arch() {
    case "$(uname -m)" in
        x86_64 | x64 | amd64) echo 'amd64' ;;
        i*86 | x86) echo '386' ;;
        armv8* | armv8 | arm64 | aarch64) echo 'arm64' ;;
        *) echo 'unknown' ;;
    esac
}
echo "Arch: $(arch) (Java/静态资源跨架构通用)"

# ---------------- 非交互模式（对齐 3x-ui NONINTERACTIVE 机制） ----------------
if [[ "${YSMERP_NONINTERACTIVE:-0}" == "1" ]] || [[ ! -t 0 ]]; then
    NONINTERACTIVE=1
else
    NONINTERACTIVE=0
fi
export NONINTERACTIVE

gen_random_string() {
    local length="$1"
    # head 提前关闭管道会让上游收到 SIGPIPE，pipefail 下会变成非零退出并触发 set -e
    { openssl rand -base64 $((length * 2)) 2> /dev/null | tr -dc 'a-zA-Z0-9' | head -c "$length"; } || true
}

is_port_in_use() {
    local port="$1" out=""
    if command -v ss > /dev/null 2>&1; then
        out=$(ss -ltn 2> /dev/null || true)
    elif command -v netstat > /dev/null 2>&1; then
        out=$(netstat -lnt 2> /dev/null || true)
    else
        return 1
    fi
    awk -v p=":${port}$" '$4 ~ p {found=1} END {exit found ? 0 : 1}' <<< "$out"
}

# ---------------- GitHub 仓库与下载（对齐 3x-ui resolve_latest_tag / checksum 机制） ----------------
GITHUB_REPO="${GITHUB_REPO:-YSM1987/ysmERP}"
GITHUB_TOKEN="${GITHUB_TOKEN:-}"
if [ -z "$GITHUB_TOKEN" ] && [ -f /root/.ysmERP_deploy_token ]; then
    GITHUB_TOKEN=$(tr -d '[:space:]' < /root/.ysmERP_deploy_token)
fi

gh_curl() { # gh_curl <url> [输出文件]
    local url="$1" out="${2:-}"
    local args=(-fL --retry 5 --retry-delay 3 --connect-timeout 15 --max-time 600
                --speed-limit 1 --speed-time 300
                -H "Accept: application/vnd.github+json")
    [ -n "$GITHUB_TOKEN" ] && args+=(-H "Authorization: Bearer ${GITHUB_TOKEN}")
    [ -n "$out" ] && args+=(-o "$out")
    curl "${args[@]}" "$url"
}

# releases/latest 重定向优先（免 API 60次/小时限流），失败回退 API —— 与 3x-ui 相同
resolve_latest_tag() {
    local url tag
    url=$(curl -sSLI -o /dev/null -w '%{url_effective}' --retry 5 --retry-delay 3 \
          --connect-timeout 15 --max-time 60 \
          "https://github.com/${GITHUB_REPO}/releases/latest" 2> /dev/null || true)
    tag=${url##*/tag/}
    if [[ "$tag" != "$url" && -n "$tag" && "$tag" != "latest" ]]; then
        echo "$tag"
        return 0
    fi
    curl -Ls --retry 5 --retry-delay 3 --connect-timeout 15 --max-time 60 \
        "https://api.github.com/repos/${GITHUB_REPO}/releases/latest" \
        | grep '"tag_name":' | sed -E 's/.*"([^"]+)".*/\1/' || true
}

# 每个产物旁发布 <asset>.sha256 sidecar；校验失败即中止，404（旧版 Release）容忍并告警 —— 与 3x-ui 相同
verify_release_checksum() {
    local url="$1" file="$2" sums="$2.sha256" code expected actual
    rm -f "${sums}"
    if [ -n "${GITHUB_TOKEN}" ]; then
        code=$(curl -sL --retry 3 --retry-delay 3 --connect-timeout 15 --max-time 60 \
               -o "${sums}" -w '%{http_code}' -H "Authorization: Bearer ${GITHUB_TOKEN}" "${url}.sha256")
    else
        code=$(curl -sL --retry 3 --retry-delay 3 --connect-timeout 15 --max-time 60 \
               -o "${sums}" -w '%{http_code}' "${url}.sha256")
    fi
    if [[ "${code}" == "404" ]]; then
        rm -f "${sums}"
        echo -e "${yellow}No checksum published for this release, skipping verification${plain}"
        return 0
    fi
    if [[ "${code}" != "200" ]]; then
        rm -f "${sums}" "${file}"
        echo -e "${red}Failed to download the checksum for $(basename "${file}") (HTTP ${code})${plain}"
        exit 1
    fi
    expected=$(awk 'NR == 1 {print $1}' "${sums}")
    actual=$(sha256sum "${file}" | awk '{print $1}')
    rm -f "${sums}"
    if [[ ! "${expected}" =~ ^[0-9a-f]{64}$ || "${expected}" != "${actual}" ]]; then
        rm -f "${file}"
        echo -e "${red}Checksum mismatch for $(basename "${file}"): expected ${expected:-<none>}, got ${actual}${plain}"
        exit 1
    fi
    echo -e "${green}Checksum verified: ${actual}${plain}"
}

# 部署配套文件（管理脚本 / service / nginx 模板）：本地同目录 → GitHub raw → codeload 源码包三级回退
fetch_support_file() { # fetch_support_file <name> <dest>
    local name="$1" dest="$2" script_dir tarball tmpdir
    script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" 2> /dev/null && pwd || true)"
    if [[ -n "$script_dir" && -s "${script_dir}/${name}" ]]; then
        cp -f "${script_dir}/${name}" "${dest}"
        return 0
    fi
    # raw 直连下载：走 gh_curl（无 token 时不会发送空的 Authorization 头——
    # 空的 Bearer 会被 GitHub 视为无效认证并返回 404，导致公开仓库也判定为不可达）
    if gh_curl "https://raw.githubusercontent.com/${GITHUB_REPO}/master/deploy/${name}" "${dest}" \
        && [ -s "${dest}" ]; then
        return 0
    fi
    # raw.githubusercontent.com 在部分云区域（如甲骨文新加坡）被阻断，回退 codeload.github.com（与 github.com 同域，Release 下载已验证可达）
    tarball="/tmp/ysmERP-deploy-src.tar.gz"
    if [[ ! -s "${tarball}" ]]; then
        echo -e "${yellow}raw.githubusercontent.com 不可达，改从 codeload 源码包获取支持文件...${plain}"
        local cargs=(-fsSL --retry 3 --connect-timeout 15)
        [ -n "${GITHUB_TOKEN}" ] && cargs+=(-H "Authorization: Bearer ${GITHUB_TOKEN}")
        curl "${cargs[@]}" \
             "https://codeload.github.com/${GITHUB_REPO}/tar.gz/refs/heads/master" \
             -o "${tarball}" || { rm -f "${tarball}"; return 1; }
    fi
    tmpdir=$(mktemp -d)
    if tar -xzf "${tarball}" -C "${tmpdir}" --strip-components=2 \
           "ysmERP-master/deploy/${name}" 2> /dev/null && [[ -s "${tmpdir}/${name}" ]]; then
        cp -f "${tmpdir}/${name}" "${dest}"
        rm -rf "${tmpdir}"
        return 0
    fi
    rm -rf "${tmpdir}"
    return 1
}

# ---------------- 安装基础软件 ----------------
install_base() {
    echo -e "${green}Installing base packages...${plain}"
    case "${release}" in
        ubuntu | debian | armbian)
            # Java17/MySQL8/Redis 依赖要求：Ubuntu 20.04+ / Debian 11+
            local v
            v=$(. /etc/os-release && echo "${VERSION_ID%%.*}")
            if { [ "${release}" = "ubuntu" ] && [ "${v}" -lt 20 ]; } || \
               { [ "${release}" = "debian" ] && [ "${v}" -lt 11 ]; }; then
                echo -e "${red}检测到 ${release} ${v}：本系统仓库缺少 Java 17 / MySQL 8 软件包。${plain}"
                echo -e "${red}请使用 Ubuntu 20.04+（推荐 24.04）或 Debian 11+。${plain}"
                exit 1
            fi
            export DEBIAN_FRONTEND=noninteractive
            apt-get update -y
            apt-get install -y -q openjdk-17-jre-headless mysql-server redis-server nginx \
                cron curl jq tar openssl ca-certificates
            ;;
        centos | rocky | almalinux | rhel | ol)
            local v
            v=$(. /etc/os-release && echo "${VERSION_ID%%.*}")
            if [ "${v}" -lt 8 ]; then
                echo -e "${red}检测到 ${release} ${v}：CentOS 7 已 EOL 且无 Java 17 软件包，不受支持。${plain}"
                echo -e "${red}请使用 Rocky / AlmaLinux / CentOS Stream 8+，或 Ubuntu 20.04+。${plain}"
                exit 1
            fi
            dnf install -y -q java-17-openjdk-headless mysql-server redis nginx jq tar openssl \
                crontabs curl || yum install -y -q java-17-openjdk-headless mysql-server redis nginx \
                jq tar openssl crontabs curl
            # el 系默认开启 SELinux，需放行 nginx 反代网络连接
            if command -v getenforce > /dev/null 2>&1 && [ "$(getenforce 2> /dev/null)" != "Disabled" ]; then
                setsebool -P httpd_can_network_connect 1 2> /dev/null || true
            fi
            systemctl enable --now mysqld 2> /dev/null || systemctl enable --now mysql 2> /dev/null || true
            ;;
        *)
            echo -e "${red}Unsupported OS: ${release}. 本脚本支持 Ubuntu 20.04+ / Debian 11+ / Rocky / AlmaLinux / CentOS Stream 8+。${plain}"
            exit 1
            ;;
    esac
}

# ---------------- 小内存 swap（甲骨文 AMD 免费机 1GB 内存必须） ----------------
ensure_swap() {
    local mem_mb swap_mb
    mem_mb=$(free -m | awk '/^Mem:/{print $2}')
    swap_mb=$(free -m | awk '/^Swap:/{print $2}')
    if [ "${mem_mb}" -lt 2048 ] && [ "${swap_mb}" -lt 512 ]; then
        echo -e "${yellow}Low memory (${mem_mb}MB), creating 2G swap...${plain}"
        fallocate -l 2G /swapfile
        chmod 600 /swapfile
        mkswap /swapfile
        swapon /swapfile
        grep -q '/swapfile' /etc/fstab || echo '/swapfile none swap sw 0 0' >> /etc/fstab
        echo "vm.swappiness=20" > /etc/sysctl.d/99-ysmERP-swap.conf
        sysctl --system > /dev/null
    fi
}

# ---------------- MySQL 初始化 + 低内存调优 ----------------
DB_PASSWORD="${DB_PASSWORD:-$(gen_random_string 20)}"
init_mysql() {
    echo -e "${green}Initializing MySQL...${plain}"
    case "${release}" in
        centos | rocky | almalinux | rhel | ol)
            # el 系：root 有临时密码（/var/log/mysqld.log）且默认启用密码强度策略
            local tmproot
            tmproot=$(grep 'temporary password' /var/log/mysqld.log 2> /dev/null | tail -1 | awk '{print $NF}')
            if [ -n "${tmproot}" ]; then
                mysql -uroot -p"${tmproot}" --connect-expired-password <<SQL 2> /dev/null
SET GLOBAL validate_password.policy = LOW;
ALTER USER 'root'@'localhost' IDENTIFIED BY '${DB_PASSWORD}';
CREATE DATABASE IF NOT EXISTS \`ysmERP\` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE USER IF NOT EXISTS 'ysmERP'@'localhost' IDENTIFIED BY '${DB_PASSWORD}';
GRANT ALL PRIVILEGES ON \`ysmERP\`.* TO 'ysmERP'@'localhost';
FLUSH PRIVILEGES;
SQL
            else
                mysql --protocol=socket -uroot <<SQL
CREATE DATABASE IF NOT EXISTS \`ysmERP\` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE USER IF NOT EXISTS 'ysmERP'@'localhost' IDENTIFIED BY '${DB_PASSWORD}';
ALTER USER 'ysmERP'@'localhost' IDENTIFIED BY '${DB_PASSWORD}';
GRANT ALL PRIVILEGES ON \`ysmERP\`.* TO 'ysmERP'@'localhost';
FLUSH PRIVILEGES;
SQL
            fi
            # el 系 MySQL 低内存调优
            cat > /etc/my.cnf.d/zz-ysmERP-lowmem.cnf <<'CNF'
[mysqld]
# ---- 低内存精简 ----
innodb_buffer_pool_size = 128M
performance_schema = OFF
max_connections = 60
table_open_cache = 200
thread_cache_size = 16
tmp_table_size = 16M
max_heap_table_size = 16M
innodb_log_buffer_size = 8M
key_buffer_size = 8M
skip-name-resolve = 1
CNF
            systemctl restart mysqld
            ;;
        *)
            mysql --protocol=socket -uroot <<SQL
CREATE DATABASE IF NOT EXISTS \`ysmERP\` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE USER IF NOT EXISTS 'ysmERP'@'localhost' IDENTIFIED BY '${DB_PASSWORD}';
ALTER USER 'ysmERP'@'localhost' IDENTIFIED BY '${DB_PASSWORD}';
GRANT ALL PRIVILEGES ON \`ysmERP\`.* TO 'ysmERP'@'localhost';
FLUSH PRIVILEGES;
SQL
            # 1GB 内存机 MySQL 低内存调优（不改任何业务数据结构）
            cat > /etc/mysql/mysql.conf.d/zz-ysmERP-lowmem.cnf <<'CNF'
[mysqld]
# ---- 低内存精简（甲骨文 1GB 免费机）----
innodb_buffer_pool_size = 128M
performance_schema = OFF
max_connections = 60
table_open_cache = 200
thread_cache_size = 16
tmp_table_size = 16M
max_heap_table_size = 16M
innodb_log_buffer_size = 8M
key_buffer_size = 8M
skip-name-resolve = 1
CNF
            systemctl restart mysql
            ;;
    esac
}

# ---------------- Redis 配置（仅本机 + 密码） ----------------
REDIS_PASSWORD="${REDIS_PASSWORD:-$(gen_random_string 20)}"
init_redis() {
    echo -e "${green}Configuring Redis...${plain}"
    local rconf rsvc
    case "${release}" in
        centos | rocky | almalinux | rhel | ol)
            rconf=/etc/redis.conf; rsvc=redis
            ;;
        *)
            rconf=/etc/redis/redis.conf; rsvc=redis-server
            ;;
    esac
    sed -i "s|^# *requirepass .*|requirepass ${REDIS_PASSWORD}|" "${rconf}"
    sed -i "s|^requirepass .*|requirepass ${REDIS_PASSWORD}|" "${rconf}"
    sed -i "s|^bind .*|bind 127.0.0.1 -::1|" "${rconf}"
    systemctl restart "${rsvc}"
}

# ---------------- 应用目录 / 配置 ----------------
APP_PORT="${APP_PORT:-8086}"
WEB_PORT="${WEB_PORT:-30545}"   # 对外访问端口（Nginx 监听），甲骨文安全列表需放行该端口
write_config() {
    id -u ysmERP > /dev/null 2>&1 || useradd -r -m -s /usr/sbin/nologin ysmERP
    install -d -m 755 "${ysmERP_folder}" "${ysmERP_folder}/web" "${ysmERP_folder}/sql" "${ysmERP_folder}/backup"
    install -d -m 700 -o ysmERP -g ysmERP "${ysmERP_conf}"
    install -d -m 755 -o ysmERP -g ysmERP "${ysmERP_folder}/logs" "${ysmERP_folder}/web/dist"

    # 外置 application.yml（Spring Boot additional-location 加载，更新 jar 不受影响）
    # 连接池 / Undertow 线程 / banner 均按 1GB 内存机轻量化，业务逻辑零改动
    cat > "${ysmERP_conf}/application.yml" <<YML
server:
  port: ${APP_PORT}
  undertow:
    threads:
      io: 2
      worker: 8
spring:
  main:
    banner-mode: off
  messages:
    basename: i18n/messages
  application:
    name: ysmERP
  data:
    redis:
      host: 127.0.0.1
      port: 6379
      database: 0
      password: ${REDIS_PASSWORD}
      timeout: 10s
      lettuce:
        pool:
          min-idle: 0
          max-idle: 4
          max-active: 8
          max-wait: -1ms
  datasource:
    driverClassName: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/ysmERP?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8
    username: ysmERP
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 5
      min-idle: 2
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
ysmerp:
  captchaType: math
user:
  password:
    maxRetryCount: 5
    lockTime: 10
mybatis-plus:
  mapper-locations: classpath*:mapper/**/*Mapper.xml
  type-aliases-package: cn.ysmerp.module.sys.domain;cn.ysmerp.domain;
YML
    chown ysmERP:ysmERP "${ysmERP_conf}/application.yml"
    chmod 600 "${ysmERP_conf}/application.yml"
}

# write_install_result <tag> —— 持久化可解析的安装结果（对齐 3x-ui write_install_result）
write_install_result() {
    local tag="$1"
    local result_file="${ysmERP_conf}/install-result.env"
    local install_time
    install_time=$(date '+%Y-%m-%d %H:%M:%S')
    {
        printf 'GITHUB_REPO=%q\n' "${GITHUB_REPO}"
        printf 'DOMAIN=%q\n' "${DOMAIN}"
        printf 'APP_PORT=%q\n' "${APP_PORT}"
        printf 'WEB_PORT=%q\n' "${WEB_PORT}"
        printf 'DB_PASSWORD=%q\n' "${DB_PASSWORD}"
        printf 'REDIS_PASSWORD=%q\n' "${REDIS_PASSWORD}"
        printf 'VERSION=%q\n' "${tag}"
        printf 'INSTALL_TIME=%q\n' "${install_time}"
    } > "${result_file}"
    chmod 600 "${result_file}"
    chown root:root "${result_file}"
}

# ---------------- 安装 systemd 服务 ----------------
install_service() {
    echo -e "${green}Installing systemd service...${plain}"
    # 环境文件：JVM 参数可在此直接调整（1GB 内存机轻量化参数）
    cat > /etc/default/ysmERP <<'ENVF'
# ysmERP JVM 参数（轻量化：小堆 + SerialGC + 限制元空间；机器内存更大可调高 -Xmx）
JAVA_OPTS="-Xms128m -Xmx512m -Xss512k -XX:MaxMetaspaceSize=160m -XX:+UseSerialGC -XX:MaxGCPauseMillis=200 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/usr/local/ysmERP/logs -Xlog:gc:file=/usr/local/ysmERP/logs/gc.log:time,uptime:filecount=2,filesize=5m -Djava.security.egd=file:/dev/./urandom -Dfile.encoding=UTF-8"
ENVF
    chmod 644 /etc/default/ysmERP

    fetch_support_file "ysmERP.service" "${ysmERP_service_dir}/ysmERP.service.tmp" || {
        echo -e "${red}获取 ysmERP.service 失败（本地目录与 GitHub raw 均不可达）${plain}"
        exit 1
    }
    sed "s#__GITHUB_REPO__#${GITHUB_REPO}#g" "${ysmERP_service_dir}/ysmERP.service.tmp" \
        > "${ysmERP_service_dir}/ysmERP.service"
    rm -f "${ysmERP_service_dir}/ysmERP.service.tmp"
    systemctl daemon-reload
    systemctl enable ysmERP

    # journald 日志限额，防小盘被日志吃满
    install -d -m 755 /etc/systemd/journald.conf.d
    cat > /etc/systemd/journald.conf.d/ysmERP.conf <<'JCONF'
[Journal]
SystemMaxUse=100M
JCONF
    systemctl try-restart systemd-journald 2> /dev/null || true
}

# ---------------- 安装管理脚本（对齐 3x-ui x-ui.sh + /usr/bin/x-ui） ----------------
install_manage_script() {
    echo -e "${green}Installing management script...${plain}"
    fetch_support_file "ysmERP.sh" "${ysmERP_folder}/ysmERP.sh" || {
        echo -e "${red}获取 ysmERP.sh 失败${plain}"
        exit 1
    }
    fetch_support_file "update.sh" "${ysmERP_folder}/update.sh" || {
        echo -e "${red}获取 update.sh 失败${plain}"
        exit 1
    }
    chmod +x "${ysmERP_folder}/ysmERP.sh" "${ysmERP_folder}/update.sh"
    ln -sf "${ysmERP_folder}/ysmERP.sh" /usr/bin/ysmERP
}

# ---------------- 下载并部署 Release 产物 ----------------
deploy_artifacts() {
    local tag="$1" tmp
    tmp=$(mktemp -d)
    # shellcheck disable=SC2064
    trap "rm -rf ${tmp}" EXIT

    echo -e "${green}Downloading release artifacts (${tag})...${plain}"
    local base_url="https://github.com/${GITHUB_REPO}/releases/download/${tag}"
    gh_curl "${base_url}/ysmERP-backend.tar.gz" "${tmp}/backend.tar.gz" || {
        echo -e "${red}下载后端产物失败，请确认仓库已发布 Release${plain}"; exit 1
    }
    verify_release_checksum "${base_url}/ysmERP-backend.tar.gz" "${tmp}/backend.tar.gz"
    gh_curl "${base_url}/ysmERP-frontend.tar.gz" "${tmp}/frontend.tar.gz" || {
        echo -e "${red}下载前端产物失败，请确认仓库已发布 Release${plain}"; exit 1
    }
    verify_release_checksum "${base_url}/ysmERP-frontend.tar.gz" "${tmp}/frontend.tar.gz"

    # 后端 jar：保留最近 5 个旧版本用于回滚
    tar -xzf "${tmp}/backend.tar.gz" -C "${tmp}"
    if [ -f "${ysmERP_folder}/ysmERP.jar" ]; then
        cp "${ysmERP_folder}/ysmERP.jar" "${ysmERP_folder}/backup/ysmERP.jar.$(date +%Y%m%d%H%M%S)"
        ls -t "${ysmERP_folder}/backup" 2> /dev/null | tail -n +6 | xargs -I{} rm -f "${ysmERP_folder}/backup/{}" || true
    fi
    install -m 644 "${tmp}/ysmERP.jar" "${ysmERP_folder}/ysmERP.jar"
    install -m 644 "${tmp}/ysmERP.sql" "${ysmERP_folder}/sql/ysmERP.sql"

    # 前端：dist.new -> dist 原子替换
    rm -rf "${ysmERP_folder}/web/dist.new"
    install -d -m 755 -o ysmERP -g ysmERP "${ysmERP_folder}/web/dist.new"
    tar -xzf "${tmp}/frontend.tar.gz" -C "${ysmERP_folder}/web/dist.new"
    rm -rf "${ysmERP_folder}/web/dist.old"
    [ -d "${ysmERP_folder}/web/dist" ] && mv "${ysmERP_folder}/web/dist" "${ysmERP_folder}/web/dist.old"
    mv "${ysmERP_folder}/web/dist.new" "${ysmERP_folder}/web/dist"
    chown -R ysmERP:ysmERP "${ysmERP_folder}"
}

# ---------------- 首次导入数据库 ----------------
init_sql() {
    local table_cnt
    table_cnt=$(mysql --protocol=socket -uroot -N -e \
        "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='ysmERP';" 2> /dev/null || echo 0)
    if [ "${table_cnt}" = "0" ]; then
        echo -e "${green}Empty database, importing schema...${plain}"
        mysql --protocol=socket -uroot "ysmERP" < "${ysmERP_folder}/sql/ysmERP.sql"
    fi
}

# ---------------- Nginx 站点 ----------------
setup_nginx() {
    echo -e "${green}Configuring Nginx (${DOMAIN})...${plain}"
    local tmp_conf
    tmp_conf=$(mktemp)
    fetch_support_file "nginx-ysmERP.conf" "${tmp_conf}" || {
        echo -e "${red}获取 nginx-ysmERP.conf 失败${plain}"; exit 1
    }
    case "${release}" in
        centos | rocky | almalinux | rhel | ol)
            # el 系：conf.d 目录 + auto.conf include 机制
            sed "s/__DOMAIN__/${DOMAIN}/g; s/__PORT__/${APP_PORT}/g; s/__WEB_PORT__/${WEB_PORT}/g; s#__ROOT__#${ysmERP_folder}#g" \
                "${tmp_conf}" > /etc/nginx/conf.d/ysmERP.conf
            ;;
        *)
            # Debian 系：sites-available + sites-enabled 机制
            sed "s/__DOMAIN__/${DOMAIN}/g; s/__PORT__/${APP_PORT}/g; s/__WEB_PORT__/${WEB_PORT}/g; s#__ROOT__#${ysmERP_folder}#g" \
                "${tmp_conf}" > /etc/nginx/sites-available/ysmERP
            ln -sf /etc/nginx/sites-available/ysmERP /etc/nginx/sites-enabled/ysmERP
            rm -f /etc/nginx/sites-enabled/default
            ;;
    esac
    rm -f "${tmp_conf}"
    nginx -t
    systemctl reload nginx
}

# ---------------- 甲骨文系统防火墙放行对外端口（默认 WEB_PORT=80，可设 30545 等） ----------------
setup_firewall() {
    if [ -f /etc/iptables/rules.v4 ]; then
        if ! grep -q "dport ${WEB_PORT}" /etc/iptables/rules.v4; then
            iptables -I INPUT 5 -p tcp --dport "${WEB_PORT}" -j ACCEPT
            if command -v netfilter-persistent > /dev/null 2>&1; then
                netfilter-persistent save
            else
                iptables-save > /etc/iptables/rules.v4
            fi
        fi
        # 80 模式额外放行 443，为 certbot HTTPS 预留
        if [ "${WEB_PORT}" = "80" ] && ! grep -q 'dport 443' /etc/iptables/rules.v4; then
            iptables -I INPUT 6 -p tcp --dport 443 -j ACCEPT
            if command -v netfilter-persistent > /dev/null 2>&1; then
                netfilter-persistent save
            else
                iptables-save > /etc/iptables/rules.v4
            fi
        fi
    else
        iptables -I INPUT -p tcp --dport "${WEB_PORT}" -j ACCEPT 2> /dev/null || true
        [ "${WEB_PORT}" = "80" ] && iptables -I INPUT -p tcp --dport 443 -j ACCEPT 2> /dev/null || true
        echo -e "${yellow}/etc/iptables/rules.v4 不存在，规则仅运行时生效，建议安装 iptables-persistent 持久化${plain}"
    fi
    echo -e "${yellow}注意：还需在甲骨文云控制台『虚拟云网络 -> 安全列表』放行入站 TCP ${WEB_PORT}${plain}"
}

# ---------------- 健康检查 ----------------
wait_healthy() {
    echo -e "${green}Waiting for backend...${plain}"
    local i
    # 1C1G 机器首次启动 Spring Boot 可能需要 1-2 分钟，给足 180s
    for i in $(seq 1 90); do
        if curl -fs -o /dev/null --max-time 3 "http://127.0.0.1:${APP_PORT}/captchaImage"; then
            echo -e "${green}Backend is ready.${plain}"
            return 0
        fi
        sleep 2
    done
    echo -e "${red}Backend did not respond in 180s. Check: journalctl -u ysmERP -n 100${plain}"
    return 1
}

# ---------------- 交互信息收集 ----------------
is_repo_format() {
    [[ "$1" =~ ^[A-Za-z0-9_.-]+/[A-Za-z0-9_.-]+$ ]]
}

is_domain_format() {
    [[ "$1" == "_" ]] || [[ "$1" =~ ^([A-Za-z0-9](-*[A-Za-z0-9])*\.)+[A-Za-z]{2,}$ ]]
}

is_number() {
    [[ "$1" =~ ^[0-9]+$ ]]
}

show_welcome() {
    echo -e "${blue}======================================================${plain}"
    echo -e "${blue}        ysmERP 一键安装向导（Ubuntu 24.04）${plain}"
    echo -e "${blue}======================================================${plain}"
    echo "本向导将自动完成以下内容（约 3-8 分钟）："
    echo "  1. 安装基础软件：OpenJDK 17 / MySQL 8 / Redis / Nginx"
    echo "  2. 小内存服务器自动创建 2G swap（1GB 免费机必须）"
    echo "  3. 初始化 MySQL 数据库与 Redis，自动生成随机强密码"
    echo "  4. 从 GitHub Release 下载最新产物并做 sha256 完整性校验"
    echo "  5. 安装 systemd 服务（开机自启 / 崩溃自恢复 / 内存限额）"
    echo "  6. 配置 Nginx 站点并放行系统防火墙对外端口（默认 30545）"
    echo ""
    echo "您只需准备两项信息："
    echo "  * GitHub 仓库：格式 用户名/仓库名，且仓库已发布 Release（含产物）"
    echo "  * 访问域名  ：已解析到本服务器公网 IP；暂无域名可输入 _ （用 IP 访问）"
    echo ""
    echo -e "数据库 / Redis 密码自动生成，安装后见 ${green}${ysmERP_conf}/application.yml${plain}"
    echo -e "私有仓库请提前放置只读 PAT：${green}echo ghp_xxx > /root/.ysmERP_deploy_token${plain}"
    if [[ "$NONINTERACTIVE" == "1" ]]; then
        echo ""
        echo -e "${yellow}当前为非交互模式：以下选项全部取自环境变量（参考 deploy/env.example）${plain}"
    fi
    echo ""
}

ask_inputs() {
    if [[ "$NONINTERACTIVE" == "1" ]]; then
        # 非交互：环境变量直读，校验失败立即退出（便于 cloud-init / CI 快速失败）
        [[ -n "${GITHUB_REPO}" ]] || { echo -e "${red}非交互模式缺少环境变量 GITHUB_REPO（格式: 用户名/仓库名）${plain}"; exit 1; }
        is_repo_format "${GITHUB_REPO}" || { echo -e "${red}GITHUB_REPO 格式不正确: ${GITHUB_REPO}（应形如 yourname/ysmERP）${plain}"; exit 1; }
        DOMAIN="${DOMAIN:-_}"
        is_domain_format "${DOMAIN}" || { echo -e "${red}DOMAIN 格式不正确: ${DOMAIN}（应为域名，或输入 _ 用 IP 访问）${plain}"; exit 1; }
        is_number "${APP_PORT}" || { echo -e "${red}APP_PORT 必须为数字: ${APP_PORT}${plain}"; exit 1; }
        is_number "${WEB_PORT}" || { echo -e "${red}WEB_PORT 必须为数字: ${WEB_PORT}${plain}"; exit 1; }
        return 0
    fi

    # ---- [1/4] GitHub 仓库 ----
    while true; do
        echo -e "${green}[1/4] GitHub 仓库${plain} —— 将从该仓库的 Release 下载安装产物"
        read -rp "      请输入（回车默认 YSM1987/ysmERP）: " GITHUB_REPO
        GITHUB_REPO="${GITHUB_REPO:-YSM1987/ysmERP}"
        if is_repo_format "${GITHUB_REPO}"; then break; fi
        echo -e "${red}      格式不正确，应形如 yourname/ysmERP，请重新输入${plain}"
    done

    # ---- [2/4] 访问域名 ----
    while true; do
        echo -e "${green}[2/4] 访问域名${plain} —— 用于 Nginx 站点 server_name"
        read -rp "      请输入（例: oms.example.com；无域名直接回车，用 _ 表示 IP 访问）: " DOMAIN
        DOMAIN="${DOMAIN:-_}"
        if is_domain_format "${DOMAIN}"; then break; fi
        echo -e "${red}      域名格式不正确（如 oms.example.com；无域名请直接回车），请重新输入${plain}"
    done

    # ---- [3/4] 后端端口 ----
    while true; do
        echo -e "${green}[3/4] 后端端口${plain} —— Spring Boot 监听端口，仅本机 Nginx 访问，一般保持默认"
        read -rp "      请输入（回车默认 8086）: " APP_PORT
        APP_PORT="${APP_PORT:-8086}"
        if is_number "${APP_PORT}" && [[ "${APP_PORT}" -ge 1024 && "${APP_PORT}" -le 65535 ]]; then break; fi
        echo -e "${red}      请输入 1024-65535 之间的数字，请重新输入${plain}"
    done

    # ---- [4/4] 对外访问端口 ----
    while true; do
        echo -e "${green}[4/4] 对外访问端口${plain} —— Nginx 监听端口，需与甲骨文安全列表放行端口一致"
        read -rp "      请输入（回车默认 30545）: " WEB_PORT
        WEB_PORT="${WEB_PORT:-30545}"
        if is_number "${WEB_PORT}" && [[ "${WEB_PORT}" -ge 1 && "${WEB_PORT}" -le 65535 ]]; then break; fi
        echo -e "${red}      请输入 1-65535 之间的数字，请重新输入${plain}"
    done
    echo ""
}

# ---------------- 主流程 ----------------
show_welcome
ask_inputs

if is_port_in_use "${APP_PORT}"; then
    echo -e "${red}端口 ${APP_PORT} 已被占用，请更换后重试${plain}"
    exit 1
fi

if [ "${WEB_PORT}" = "80" ]; then
    SITE_URL="http://${DOMAIN}"
else
    SITE_URL="http://${DOMAIN}:${WEB_PORT}"
fi
echo -e "安装确认 -> 仓库: ${green}${GITHUB_REPO}${plain}  域名: ${green}${DOMAIN}${plain}  对外端口: ${green}${WEB_PORT}${plain}  后端端口: ${green}${APP_PORT}${plain}"
if [[ "$NONINTERACTIVE" != "1" ]]; then
    read -rp "确认无误并开始安装? [y/n，回车为 y]: " confirm
    confirm="${confirm:-y}"
    [[ "${confirm}" == "y" || "${confirm}" == "Y" ]] || { echo "已取消安装"; exit 0; }
fi
echo ""

install_base
ensure_swap
init_mysql
init_redis
write_config

rm -f /tmp/ysmERP-deploy-src.tar.gz   # 清除 codeload 支持文件缓存，确保本次运行取最新
TAG=$(resolve_latest_tag)
[[ -n "$TAG" ]] || { echo -e "${red}无法获取最新版本（GitHub 不可达或仓库无 Release）${plain}"; exit 1; }
echo -e "Latest release: ${TAG}"

systemctl stop ysmERP 2> /dev/null || true
deploy_artifacts "${TAG}"
install_service
install_manage_script
init_sql
setup_nginx
setup_firewall
write_install_result "${TAG}"

systemctl restart ysmERP
wait_healthy || exit 1

echo ""
echo -e "${green}======================================================================${plain}"
echo -e "${green} 安装完成！${plain}"
echo -e " 访问地址   : ${SITE_URL}"
echo -e " 默认账号   : admin / Andy@2025（登录后请立即修改密码）"
echo -e " MySQL 密码 : 已写入 ${ysmERP_conf}/application.yml 与 install-result.env"
echo -e " Redis 密码 : 已写入 ${ysmERP_conf}/application.yml"
echo -e " 安装版本   : ${TAG}"
echo -e " 管理命令   : ysmERP（start/stop/restart/status/log/update/uninstall/settings）"
echo -e " 后续更新   : ysmERP update"
if [ "${WEB_PORT}" = "80" ]; then
    echo -e " HTTPS      : 参考《ysmERP甲骨文云服务器部署教程.md》第 5 步 certbot 命令"
else
    echo -e " HTTPS      : 对外端口非 80，HTTP-01 验证不可用，需 DNS 验证（详见教程第 5 步说明）"
fi
echo -e "${green}======================================================================${plain}"
echo ""
echo -e "${green}部署验证（依次执行以下命令确认服务正常）：${plain}"
echo -e "  1. ysmERP status                 # 服务应为 ${green}active (running)${plain}"
echo -e "  2. curl -s -o /dev/null -w '%{http_code}' http://127.0.0.1:${APP_PORT}/captchaImage"
echo -e "     # 应输出 200（后端接口正常）"
echo -e "  3. curl -s -o /dev/null -w '%{http_code}' ${SITE_URL}/captchaImage"
echo -e "     # 应输出 200（Nginx 反代正常）；再在浏览器打开 ${SITE_URL} 应看到登录页"
echo -e "  4. reboot 后再次执行 ysmERP status 可确认开机自启生效"
echo -e "  异常排查: ysmERP log（应用日志）| journalctl -u ysmERP -n 50（系统日志）| nginx -t（站点配置）"
