package ru.ioque.apitest.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = Id.DEDUCTION)
@JsonSubTypes({
    @JsonSubTypes.Type(value = SignalRegisteredEvent.class, name = "SignalRegisteredEvent"),
    @JsonSubTypes.Type(value = TradingDataIntegratedEvent.class, name = "TradingDataIntegratedEvent"),
    @JsonSubTypes.Type(value = ScanningFinishedEvent.class, name = "ScanningFinishedEvent")
}
)
public interface IntegrationEvent {
    default boolean isSignalRegisteredEvent() {
        return false;
    }
    default boolean isTradingDataIntegratedEvent() {
        return false;
    }
    default boolean isScanningFinishedEvent() {
        return false;
    }
}
