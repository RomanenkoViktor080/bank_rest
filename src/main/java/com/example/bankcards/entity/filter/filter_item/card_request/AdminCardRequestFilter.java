package com.example.bankcards.entity.filter.filter_item.card_request;

import com.example.bankcards.dto.card_request.FilterCardRequestDto;
import com.example.bankcards.entity.card.CardRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AdminCardRequestFilter implements AdminCardRequestFilterInterface {
    @Override
    public boolean isApplicable(FilterCardRequestDto dto) {
        return dto.status() != null;
    }

    @Override
    public Specification<CardRequest> apply(Specification<CardRequest> specification, FilterCardRequestDto dto) {
        return specification.and((root, query, cb) ->
                cb.equal(root.get("status"), dto.status())
        );
    }
}
