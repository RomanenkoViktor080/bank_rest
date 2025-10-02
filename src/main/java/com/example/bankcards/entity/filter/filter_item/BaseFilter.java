package com.example.bankcards.entity.filter.filter_item;

import org.springframework.data.jpa.domain.Specification;

public interface BaseFilter<T, U> {
    boolean isApplicable(U dto);

    Specification<T> apply(Specification<T> specification, U dto);
}
