package org.crochet.config;

import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.crochet.constant.AppConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Slf4j
@Configuration
public class WebClientConfig {
    @Bean
    @Scope(SCOPE_PROTOTYPE)
    public WebClient.Builder webClientBuilder() {
        HttpClient httpClient = HttpClient.create().doOnRequest((httpClientRequest, connection) -> {
            connection.addHandlerLast(new ReadTimeoutHandler(AppConstant.READ_TIMEOUT_SECONDS))
                    .addHandlerLast(new WriteTimeoutHandler(
                            AppConstant.WRITE_TIMEOUT_SECONDS)).channel().config().setConnectTimeoutMillis(
                            AppConstant.CONNECT_TIMEOUT_MILLIS);
        });
        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
        return WebClient.builder()
                .clientConnector(connector);
    }
}
