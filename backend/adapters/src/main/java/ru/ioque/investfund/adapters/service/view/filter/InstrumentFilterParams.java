package ru.ioque.investfund.adapters.service.view.filter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import ru.ioque.investfund.adapters.psql.entity.datasource.instrument.InstrumentEntity;
import ru.ioque.investfund.adapters.service.view.specification.InstrumentSpecifications;
import ru.ioque.investfund.domain.datasource.value.types.InstrumentType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class InstrumentFilterParams {
    UUID datasourceId;
    String ticker;
    InstrumentType type;
    String shortName;
    Integer pageNumber;
    Integer pageSize;
    String orderDirection;
    String orderField;

    public Specification<InstrumentEntity> specification() {
        return Specification.allOf(specifications());
    }

    public boolean specificationIsEmpty() {
        return specifications().isEmpty();
    }

    public boolean pageRequestIsEmpty() {
        return pageNumber == null || pageSize == null;
    }

    private List<Specification<InstrumentEntity>> specifications() {
        List<Specification<InstrumentEntity>> specifications = new ArrayList<>();
        Optional.ofNullable(datasourceId).ifPresent(value -> specifications.add(InstrumentSpecifications.datasourceIdEqual(value)));
        Optional.ofNullable(ticker).ifPresent(value -> specifications.add(InstrumentSpecifications.tickerLike(value)));
        Optional.ofNullable(type).ifPresent(value -> specifications.add(InstrumentSpecifications.typeEqual(value)));
        Optional.ofNullable(shortName).ifPresent(value -> specifications.add(InstrumentSpecifications.shortNameLike(value)));
        return specifications;
    }

    public PageRequest pageRequest() {
        if (pageRequestIsEmpty()) {
            throw new RuntimeException();
        }
        if (orderDirection == null || prepareOrderField() == null) {
            return PageRequest.of(pageNumber, pageSize);
        }
        return PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.valueOf(orderDirection), prepareOrderField()));
    }

    private String prepareOrderField() {
        if (orderField.equals("shortName")) {
            return "details.shortName";
        }
        if (orderField.equals("todayValue") || orderField.equals("todayLastPrice")) {
            return "tradingState." + orderField;
        }
        return orderField;
    }
}
