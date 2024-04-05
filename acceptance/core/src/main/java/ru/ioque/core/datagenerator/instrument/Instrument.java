package ru.ioque.core.datagenerator.instrument;

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

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = Id.DEDUCTION)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Stock.class, name = "Stock"),
    @JsonSubTypes.Type(value = Index.class, name = "Index"),
    @JsonSubTypes.Type(value = Futures.class, name = "Futures"),
    @JsonSubTypes.Type(value = CurrencyPair.class, name = "CurrencyPair") }
)
public abstract class Instrument {
    String ticker;
    String shortName;
    String name;
}
