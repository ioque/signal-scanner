package ru.ioque.investfund.application.modules.pipeline.core;

public interface Transformer<FROM, TO> {
    TO transform(FROM from);
}
