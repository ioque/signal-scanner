package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.UUIDProvider;

import java.util.UUID;

public class FakeUUIDProvider implements UUIDProvider {
    @Override
    public UUID generate() {
        return UUID.randomUUID();
    }
}
