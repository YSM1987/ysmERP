#!/usr/bin/env bash
# =====================================================================
# ysmERP —— 从 GitHub Release 更新到指定/最新版本（对齐 3x-ui update.sh）
# 用法（root 执行）：
#   bash update.sh            # 更新到最新 Release
#   bash update.sh v3.0.2     # 更新到指定 tag
# 说明：
#   - 保留最近 5 个旧 jar 用于回滚（backup/ 目录）
#   - 数据库为空时自动导入 ysmERP.sql（首次部署场景）
#   - 每个产物带 .sha256 sidecar 校验，校验失败立即中止
# =====================================================================
set -euo pipefail

red='\033[0;31m'
green='\033[0;32m'
yellow='\033[0;33m'
plain='\033[0m'

ysmERP_folder="${YSMERP_MAIN_FOLDER:=/usr/local/ysmERP}"
ysmERP_conf="${YSMERP_CONF_FOLDER:=/etc/ysmERP}"

[[ $EUID -ne 0 ]] && echo -e "${red}Fatal error: ${plain} Please run this script with root privilege\n" && exit 1

# ---- 读取安装结果（install.sh 生成） ----
if [ -f "${ysmERP_conf}/install-result.env" ]; then
    # shellcheck disable=SC1090
    source "${ysmERP_conf}/install-result.env"
fi
GITHUB_REPO="${GITHUB_REPO:-${1:-}}"
if [ -z "$GITHUB_REPO" ]; then
    echo -e "${red}无法确定 GitHub 仓库。请先运行 install.sh，或执行: bash update.sh yourname/ysmERP${plain}"
    exit 1
fi

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

verify_release_checksum() {
    local url="$1" file="$2" sums="$2.sha256" code expected actual
    rm -f "${sums}"
    local cargs=(-sL --retry 3 --retry-delay 3 --connect-timeout 15 --max-time 60)
    [ -n "${GITHUB_TOKEN}" ] && cargs+=(-H "Authorization: Bearer ${GITHUB_TOKEN}")
    code=$(curl "${cargs[@]}" -o "${sums}" -w '%{http_code}' "${url}.sha256")
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

APP_PORT="${APP_PORT:-8086}"

echo -e "Repo   : ${GITHUB_REPO}"
echo -e "Current: ${VERSION:-unknown}"

if [ $# -gt 0 ] && [[ "$1" == */* ]]; then
    GITHUB_REPO="$1"
    TAG_VERSION=$(resolve_latest_tag)
else
    TAG_VERSION="$1"
fi
if [ -z "${TAG_VERSION:-}" ]; then
    TAG_VERSION=$(resolve_latest_tag)
fi
if [ -z "${TAG_VERSION:-}" ]; then
    echo -e "${red}Failed to fetch latest version, it may be due to GitHub API restrictions, please try it later${plain}"
    exit 1
fi
echo -e "Target : ${TAG_VERSION}"

if [ "${TAG_VERSION}" = "${VERSION:-}" ]; then
    echo -e "${green}Already on ${TAG_VERSION}. Use 'bash update.sh <tag>' to force a version.${plain}"
    exit 0
fi

TMP=$(mktemp -d)
# shellcheck disable=SC2064
trap "rm -rf ${TMP}" EXIT

BASE_URL="https://github.com/${GITHUB_REPO}/releases/download/${TAG_VERSION}"

echo "==> [1/5] 下载产物"
gh_curl "${BASE_URL}/ysmERP-backend.tar.gz" "${TMP}/backend.tar.gz" || {
    echo -e "${red}下载后端产物失败，请确认 ${TAG_VERSION} 的 Release 已发布${plain}"; exit 1
}
verify_release_checksum "${BASE_URL}/ysmERP-backend.tar.gz" "${TMP}/backend.tar.gz"
gh_curl "${BASE_URL}/ysmERP-frontend.tar.gz" "${TMP}/frontend.tar.gz" || {
    echo -e "${red}下载前端产物失败${plain}"; exit 1
}
verify_release_checksum "${BASE_URL}/ysmERP-frontend.tar.gz" "${TMP}/frontend.tar.gz"

echo "==> [2/5] 部署后端 jar（保留最近 5 个旧版本）"
tar -xzf "${TMP}/backend.tar.gz" -C "${TMP}"
if [ -f "${ysmERP_folder}/ysmERP.jar" ]; then
    cp "${ysmERP_folder}/ysmERP.jar" "${ysmERP_folder}/backup/ysmERP.jar.$(date +%Y%m%d%H%M%S)"
    ls -t "${ysmERP_folder}/backup" 2> /dev/null | tail -n +6 | xargs -I{} rm -f "${ysmERP_folder}/backup/{}" || true
fi
install -m 644 "${TMP}/ysmERP.jar" "${ysmERP_folder}/ysmERP.jar"
install -m 644 "${TMP}/ysmERP.sql" "${ysmERP_folder}/sql/ysmERP.sql"

echo "==> [3/5] 部署前端静态文件（原子替换）"
rm -rf "${ysmERP_folder}/web/dist.new"
install -d -m 755 -o ysmERP -g ysmERP "${ysmERP_folder}/web/dist.new"
tar -xzf "${TMP}/frontend.tar.gz" -C "${ysmERP_folder}/web/dist.new"
rm -rf "${ysmERP_folder}/web/dist.old"
[ -d "${ysmERP_folder}/web/dist" ] && mv "${ysmERP_folder}/web/dist" "${ysmERP_folder}/web/dist.old"
mv "${ysmERP_folder}/web/dist.new" "${ysmERP_folder}/web/dist"
chown -R ysmERP:ysmERP "${ysmERP_folder}"

echo "==> [4/5] 数据库为空时自动导入"
TABLE_CNT=$(mysql --protocol=socket -uroot -N -e \
    "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='ysmERP';" 2> /dev/null || echo 0)
if [ "${TABLE_CNT}" = "0" ]; then
    mysql --protocol=socket -uroot "ysmERP" < "${ysmERP_folder}/sql/ysmERP.sql"
fi

echo "==> [5/5] 重启服务并健康检查"
systemctl restart ysmERP
nginx -t 2> /dev/null && systemctl reload nginx 2> /dev/null || true

for i in $(seq 1 90); do
    if curl -fs -o /dev/null --max-time 3 "http://127.0.0.1:${APP_PORT}/api/sys-api/captchaImage"; then
        # 更新 install-result.env 中的版本号
        if [ -f "${ysmERP_conf}/install-result.env" ]; then
            sed -i "s|^VERSION=.*|VERSION=${TAG_VERSION}|" "${ysmERP_conf}/install-result.env"
        fi
        echo -e "${green}✅ 更新完成（${TAG_VERSION}），后端已就绪${plain}"
        exit 0
    fi
    sleep 2
done
echo -e "${red}⚠️ 后端 180 秒内未响应。回滚方法: 把 ${ysmERP_folder}/backup/ 最新 jar 复制为 ${ysmERP_folder}/ysmERP.jar 后 systemctl restart ysmERP${plain}"
echo -e "${red}   查看日志: journalctl -u ysmERP -n 100${plain}"
exit 1
