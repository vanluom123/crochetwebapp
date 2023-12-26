package org.crochet.config;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.xiaofeng.webclient.module.WebClientModule;
import org.xiaofeng.webclient.service.WebClientService;
import org.xiaofeng.webclient.service.WebClientServiceImpl;

@Configuration
public class WebClientConfig {
    @Bean
    @Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public WebClientService webClientService() {
        Injector injector = Guice.createInjector(new WebClientModule());
        return injector.getInstance(WebClientServiceImpl.class);
    }
}
