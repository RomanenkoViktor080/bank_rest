package com.example.bankcards.entity.filter.builder.card.admin;

import com.example.bankcards.dto.card.AdminCardFilterDto;
import com.example.bankcards.entity.card.Card;
import com.example.bankcards.entity.filter.builder.BaseFilterBuilder;
import com.example.bankcards.entity.filter.filter_item.card.admin.AdminCardFilterInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminCardFilterBuilder extends BaseFilterBuilder<Card, AdminCardFilterDto>
        implements AdminCardFilterBuilderInterface {
    private final List<AdminCardFilterInterface> filters;

    @Override
    public List<AdminCardFilterInterface> getFilters() {
        return filters;
    }
}
