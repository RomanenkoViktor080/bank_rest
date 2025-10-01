package com.example.bankcards.entity.filter.filter_item.card.user;

import com.example.bankcards.dto.card.CardFilterDto;
import com.example.bankcards.entity.card.Card;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class UserCardStatusFilter implements UserCardFilterInterface {
    @Override
    public boolean isApplicable(CardFilterDto dto) {
        return dto.status() != null;
    }

    @Override
    public Specification<Card> apply(Specification<Card> specification, CardFilterDto dto) {
        return specification.and((root, query, cb) ->
                cb.equal(root.get("status"), dto.status())
        );
    }
}
