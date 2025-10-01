package com.example.bankcards.entity.filter.builder.user;

import com.example.bankcards.dto.user.UserFilterDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.filter.builder.BaseFilterBuilder;
import com.example.bankcards.entity.filter.filter_item.user.UserFilterInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class UserFilterBuilder extends BaseFilterBuilder<User, UserFilterDto>
        implements UserFilterBuilderInterface {
    private final List<UserFilterInterface> filters;

    @Override
    public List<UserFilterInterface> getFilters() {
        return filters;
    }
}
