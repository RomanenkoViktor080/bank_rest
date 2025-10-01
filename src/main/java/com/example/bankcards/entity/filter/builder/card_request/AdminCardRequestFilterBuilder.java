package com.example.bankcards.entity.filter.builder.card_request;

import com.example.bankcards.dto.card_request.FilterCardRequestDto;
import com.example.bankcards.entity.card.CardRequest;
import com.example.bankcards.entity.filter.builder.BaseFilterBuilder;
import com.example.bankcards.entity.filter.filter_item.card_request.AdminCardRequestFilterInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class AdminCardRequestFilterBuilder extends BaseFilterBuilder<CardRequest, FilterCardRequestDto>
        implements AdminCardRequestFilterBuilderInterface {
    private final List<AdminCardRequestFilterInterface> filters;

    @Override
    public List<AdminCardRequestFilterInterface> getFilters() {
        return filters;
    }
}
