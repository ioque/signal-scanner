package ru.ioque.investfund.adapters.query.filter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import ru.ioque.investfund.adapters.persistence.entity.risk.EmulatedPositionEntity;
import ru.ioque.investfund.adapters.query.specification.EmulatedPositionSpecifications;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class EmulatedPositionFilterParams {
    UUID scannerId;
    String ticker;
    Boolean isOpen;
    Integer pageNumber;
    Integer pageSize;
    String orderDirection;
    String orderField;

    public Specification<EmulatedPositionEntity> specification() {
        return Specification.allOf(specifications());
    }

    public boolean specificationIsEmpty() {
        return specifications().isEmpty();
    }

    public boolean pageRequestIsEmpty() {
        return pageNumber == null || pageSize == null;
    }

    private List<Specification<EmulatedPositionEntity>> specifications() {
        List<Specification<EmulatedPositionEntity>> specifications = new ArrayList<>();
        Optional.ofNullable(scannerId).ifPresent(value -> specifications.add(EmulatedPositionSpecifications.scannerIdEqual(value)));
        Optional.ofNullable(ticker).ifPresent(value -> specifications.add(EmulatedPositionSpecifications.tickerLike(value)));
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
