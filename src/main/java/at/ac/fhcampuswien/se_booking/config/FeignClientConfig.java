package at.ac.fhcampuswien.se_booking.config;


import feign.Client;
import feign.RequestInterceptor;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = {"at.ac.fhcampuswien.se_booking.client"})
public class FeignClientConfig {

    @Bean
    public Client feignClient() {
        return new Client.Default(null, null);
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> requestTemplate.header("Accept", "application/json");
    }
}
