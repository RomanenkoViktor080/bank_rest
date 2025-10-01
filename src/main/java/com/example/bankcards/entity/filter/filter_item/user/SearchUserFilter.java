package com.example.bankcards.entity.filter.filter_item.user;

import com.example.bankcards.dto.user.UserFilterDto;
import com.example.bankcards.entity.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class SearchUserFilter implements UserFilterInterface {
    @Override
    public boolean isApplicable(UserFilterDto dto) {
        return dto.search() != null && !dto.search().isBlank();
    }

    @Override
    public Specification<User> apply(Specification<User> specification, UserFilterDto dto) {
        return specification.and((root, query, cb) ->
                cb.like(root.get("username"), "%" + dto.search() + "%")
        );
    }
}
