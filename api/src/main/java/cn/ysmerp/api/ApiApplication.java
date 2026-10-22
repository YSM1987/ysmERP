package cn.ysmerp.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

@ComponentScan(basePackages={"cn.ysmerp"})
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
