package ru.ioque.investfund.adapters.persistence;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.ioque.investfund.adapters.persistence.entity.scanner.ScannerEntity;
import ru.ioque.investfund.adapters.persistence.repositories.JpaScannerRepository;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.domain.core.EntityNotFoundException;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PsqlScannerRepository implements ScannerRepository {
    JpaScannerRepository signalScannerEntityRepository;

    @Transactional(readOnly = true)
    public Optional<SignalScanner> findBy(ScannerId id) {
        return signalScannerEntityRepository
            .findById(id.getUuid())
            .map(ScannerEntity::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SignalScanner> findAllBy(DatasourceId datasourceId) {
        return signalScannerEntityRepository
            .findAllByDatasourceId(datasourceId.getUuid())
            .stream()
            .map(ScannerEntity::toDomain)
            .toList();
    }

    @Override
    @Transactional
    public void save(SignalScanner dataScanner) {
        signalScannerEntityRepository.save(ScannerEntity.fromDomain(dataScanner));
    }

    @Override
    @Transactional(readOnly = true)
    public SignalScanner getBy(ScannerId scannerId) throws EntityNotFoundException {
        return findBy(scannerId)
            .orElseThrow(
                () -> new EntityNotFoundException(
                    String.format("Сканер[id=%s] не существует.", scannerId)
                )
            );
    }

    @Override
    public ScannerId nextId() {
        return ScannerId.from(UUID.randomUUID());
    }

    @Override
    public void removeBy(ScannerId scannerId) {
        signalScannerEntityRepository.deleteById(scannerId.getUuid());
    }
}
