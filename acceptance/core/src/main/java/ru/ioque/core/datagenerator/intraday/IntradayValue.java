package ru.ioque.core.datagenerator.intraday;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = Id.DEDUCTION, defaultImpl = Delta.class)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Deal.class, name = "Deal"),
    @JsonSubTypes.Type(value = Delta.class, name = "Delta"),
    @JsonSubTypes.Type(value = Contract.class, name = "Contract") }
)
public abstract class IntradayValue {
    Long number;
    LocalDateTime dateTime;
    String ticker;
    Double value;
    Double price;
}
