package cn.ysmerp.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

// 第三方 SDK（open-sdk-2.1.11.jar）中的 WeiGoodsApiService 标注为 @Component，
// 但包名 cn.qihangerp 不在 cn.ysmerp 扫描范围内，需显式加入，否则 GoodsApiController 注入失败
@ComponentScan(basePackages={"cn.ysmerp","cn.qihangerp.open.wei"})
//@MapperScan("cn.ysmerp.sys.api.mapper")
@SpringBootApplication(scanBasePackages = {"cn.ysmerp.api"})
public class ApiApplication
{
    public static void main( String[] args )
    {
        System.out.println( "Hello ysmERP!" );
        SpringApplication.run(ApiApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
