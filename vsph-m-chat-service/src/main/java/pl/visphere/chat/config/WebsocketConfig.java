/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.chat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import pl.visphere.chat.network.InboundUserInterceptor;

import java.util.List;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {
    private final ObjectMapper objectMapper;
    private final InboundUserInterceptor inboundUserInterceptor;
    private final WebsocketProperties websocketProperties;
    private final ExternalServiceConfig externalServiceConfig;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry
            .enableStompBrokerRelay("/topic")
            .setRelayHost(websocketProperties.getRelayHost());
        registry
            .setApplicationDestinationPrefixes("/vsph");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
            .addEndpoint("/chat/ws")
            .setAllowedOrigins(externalServiceConfig.getClientUrl());
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        final DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
        final MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);
        converter.setObjectMapper(objectMapper);
        converter.setContentTypeResolver(resolver);
        messageConverters.add(converter);
        return false;
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(inboundUserInterceptor);
    }
}
