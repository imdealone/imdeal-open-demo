package com.imdealone.open.demo.config;

import com.imdealone.open.demo.utils.ImdealClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("config")
@Data
public class ImdealConfiguration {


    private String clientId;

    private String clientSecret;

    private String apiGateway;


    @Bean("imdealClient")
    public ImdealClient client() {
        return new ImdealClient(this);
    }


}
