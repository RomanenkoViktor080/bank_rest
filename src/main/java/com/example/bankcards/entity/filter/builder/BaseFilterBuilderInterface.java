package com.example.bankcards.entity.filter.builder;

import org.springframework.data.jpa.domain.Specification;

import java.util.function.Function;

public interface BaseFilterBuilderInterface<T, U> {
    Specification<T> buildSpecification(
            U params,
            Function<Specification<T>, Specification<T>> customizer
    );
}
