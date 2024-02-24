package ru.ioque.investfund.adapters.storage.jpa.filter;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.instrument.InstrumentEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.ioque.investfund.adapters.storage.jpa.specification.Specifications.shortNameLike;
import static ru.ioque.investfund.adapters.storage.jpa.specification.Specifications.tickerLike;
import static ru.ioque.investfund.adapters.storage.jpa.specification.Specifications.typeEqual;

@ToString
@EqualsAndHashCode
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class InstrumentFilterParams {
    String ticker;
    String type;
    String shortName;
    Integer pageNumber;
    Integer pageSize;
    String orderDirection;
    String orderField;

    @Builder
    public InstrumentFilterParams(
        String ticker,
        String type,
        String shortName,
        Integer pageNumber,
        Integer pageSize,
        String orderDirection,
        String orderField
    ) {
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
        return pageNumber == null || pageSize == null || orderDirection == null || orderField == null;
    }

    private List<Specification<InstrumentEntity>> specifications() {
        List<Specification<InstrumentEntity>> specifications = new ArrayList<>();
        Optional.ofNullable(ticker).ifPresent(value -> specifications.add(tickerLike(value)));
        Optional.ofNullable(type).ifPresent(value -> specifications.add(typeEqual(value)));
        Optional.ofNullable(shortName).ifPresent(value -> specifications.add(shortNameLike(value)));
        return specifications;
    }

    public PageRequest pageRequest() {
        if (pageRequestIsEmpty()) {
            throw new RuntimeException();
        }
        return PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.valueOf(orderDirection), orderField));
    }
}
