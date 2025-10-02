package com.example.bankcards.config;

import com.example.bankcards.entity.card.CardRequestType;
import com.example.bankcards.exception.api.ConflictException;
import com.example.bankcards.service.card_request.handler.CardRequestHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class HandlerConfig {

    @Bean
    public Map<CardRequestType, CardRequestHandler> cardRequestHandlerMap(List<CardRequestHandler> handlers) {
        return handlers.stream()
                .collect(Collectors.toMap(CardRequestHandler::getType, Function.identity(),
                        (ex, rep) -> {
                            throw new ConflictException("Duplicate handler for " + ex.getType());
                        })
                );
    }
}
