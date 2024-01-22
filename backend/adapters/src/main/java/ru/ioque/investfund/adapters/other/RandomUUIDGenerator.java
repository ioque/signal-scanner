package ru.ioque.investfund.adapters.other;

import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.UUIDProvider;

import java.util.UUID;

@Component
public class RandomUUIDGenerator implements UUIDProvider {
    @Override
    public UUID generate() {
        return UUID.randomUUID();
    }
}
