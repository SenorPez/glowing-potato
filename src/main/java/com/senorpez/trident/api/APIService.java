package com.senorpez.trident.api;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Service
class APIService {
    <T> T findOne(final Collection<T> collection, final Predicate<T> predicate, final Supplier<RuntimeException> exceptionSupplier) {
        return collection.stream()
                .filter(predicate)
                .findFirst()
                .orElseThrow(exceptionSupplier);
    }

    <T> Collection<T> findAll(final Collection<T> collection) {
        return collection;
    }
}
