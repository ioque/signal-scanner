package ru.ioque.investfund.adapters.persistence.entity.datasource.intradayvalue;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class IntradayPk {
    private Long number;
    private String ticker;
}
