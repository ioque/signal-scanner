package ru.ioque.investfund.adapters.persistence.filter;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import ru.ioque.investfund.adapters.persistence.specification.Specifications;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.InstrumentEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ToString
@EqualsAndHashCode
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class InstrumentFilterParams {
    UUID datasourceId;
    String ticker;
    String type;
    String shortName;
    Integer pageNumber;
    Integer pageSize;
    String orderDirection;
    String orderField;

    @Builder
    public InstrumentFilterParams(
        UUID datasourceId,
        String ticker,
        String type,
        String shortName,
        Integer pageNumber,
        Integer pageSize,
        String orderDirection,
        String orderField
    ) {
        this.datasourceId = datasourceId;
        this.ticker = ticker;
        this.type = type;
        this.shortName = shortName;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.orderDirection = orderDirection;
        this.orderField = orderField;
    }

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
        Optional.ofNullable(datasourceId).ifPresent(value -> specifications.add(Specifications.datasourceIdEqual(value)));
        Optional.ofNullable(ticker).ifPresent(value -> specifications.add(Specifications.tickerLike(value)));
        Optional.ofNullable(type).ifPresent(value -> specifications.add(Specifications.typeEqual(value)));
        Optional.ofNullable(shortName).ifPresent(value -> specifications.add(Specifications.shortNameLike(value)));
        return specifications;
    }

    public PageRequest pageRequest() {
        if (pageRequestIsEmpty()) {
            throw new RuntimeException();
        }
        if (orderDirection == null || orderField == null) {
            return PageRequest.of(pageNumber, pageSize);
        }
        return PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.valueOf(orderDirection), orderField));
    }
}
