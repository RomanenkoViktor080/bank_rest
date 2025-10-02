package com.example.bankcards.entity.filter.filter_item.card.admin;

import com.example.bankcards.dto.card.AdminCardFilterDto;
import com.example.bankcards.entity.card.Card;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AdminCardUserFilter implements AdminCardFilterInterface {
    @Override
    public boolean isApplicable(AdminCardFilterDto dto) {
        return dto.status() != null;
    }

    @Override
    public Specification<Card> apply(Specification<Card> specification, AdminCardFilterDto dto) {
        return specification.and((root, query, cb) ->
                cb.equal(root.get("user").get("id"), dto.userId())
        );
    }
}
