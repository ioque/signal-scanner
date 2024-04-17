package ru.ioque.apitest.kafka;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonInclude(Include.NON_NULL)
@JsonTypeInfo(use = Id.DEDUCTION)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ScanningFinishedEvent.class, name = "ScanningFinishedEvent"),
    @JsonSubTypes.Type(value = SignalFoundEvent.class, name = "SignalFoundEvent"),
    @JsonSubTypes.Type(value = TradingDataIntegratedEvent.class, name = "TradingDataIntegratedEvent") }
)
public interface DomainEvent {
}
