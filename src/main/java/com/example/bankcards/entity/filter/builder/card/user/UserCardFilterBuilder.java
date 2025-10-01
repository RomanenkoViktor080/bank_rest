package com.example.bankcards.entity.filter.builder.card.user;

import com.example.bankcards.dto.card.UserCardFilterDto;
import com.example.bankcards.entity.card.Card;
import com.example.bankcards.entity.filter.builder.BaseFilterBuilder;
import com.example.bankcards.entity.filter.filter_item.card.user.UserCardFilterInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserCardFilterBuilder extends BaseFilterBuilder<Card, UserCardFilterDto>
        implements UserCardFilterBuilderInterface {
    private final List<UserCardFilterInterface> filters;

    @Override
    public List<UserCardFilterInterface> getFilters() {
        return filters;
    }
}
