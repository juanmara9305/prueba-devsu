package com.devsu.account_service.domain.usecase;

@FunctionalInterface
public interface UseCase<I, O> {
    O execute(I input);
}
