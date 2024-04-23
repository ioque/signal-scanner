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
    @JsonSubTypes.Type(value = DatasourceScanned.class, name = "DatasourceScanned"),
    @JsonSubTypes.Type(value = SignalRegistered.class, name = "SignalRegistered"),
    @JsonSubTypes.Type(value = TradingDataIntegrated.class, name = "TradingDataIntegrated"),
    @JsonSubTypes.Type(value = TradingStateChanged.class, name = "TradingStateChanged"),
}
)
public interface IntegrationEvent {
    default boolean isDatasourceScanned() {
        return false;
    }
    default boolean isSignalRegistered() {
        return false;
    }
    default boolean isTradingDataIntegrated() {
        return false;
    }
    default boolean isTradingStateChanged() {
        return false;
    }
}
