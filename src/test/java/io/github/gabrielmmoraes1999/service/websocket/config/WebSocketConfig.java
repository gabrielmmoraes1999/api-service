package io.github.gabrielmmoraes1999.service.websocket.config;

import io.github.gabrielmmoraes1999.apiservice.annotation.Bean;
import io.github.gabrielmmoraes1999.apiservice.annotation.Configuration;
import io.github.gabrielmmoraes1999.apiservice.annotation.EnableWebSocket;
import io.github.gabrielmmoraes1999.apiservice.websocket.annotation.WebSocketConfigurer;
import io.github.gabrielmmoraes1999.apiservice.websocket.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(
                new WebSocketHandlerConfig(),
                "/websocket")
                .addInterceptor(webSocketAuthInterceptor());
    }

    @Bean
    public WebSocketAuthInterceptor webSocketAuthInterceptor() {
        return new WebSocketAuthInterceptor();
    }

}
