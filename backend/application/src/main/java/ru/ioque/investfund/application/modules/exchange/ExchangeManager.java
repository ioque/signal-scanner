package ru.ioque.investfund.application.modules.exchange;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.ConfigureProvider;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.EventBus;
import ru.ioque.investfund.application.adapters.ExchangeProvider;
import ru.ioque.investfund.application.adapters.ExchangeRepository;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.application.modules.SystemModule;
import ru.ioque.investfund.application.share.exception.ApplicationException;
import ru.ioque.investfund.application.share.logger.LoggerFacade;
import ru.ioque.investfund.domain.exchange.entity.Exchange;
import ru.ioque.investfund.domain.exchange.event.TradingDataUpdatedEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ExchangeManager implements SystemModule {
    DateTimeProvider dateTimeProvider;
    ConfigureProvider configureProvider;
    ExchangeProvider exchangeProvider;
    ExchangeRepository repository;
    UUIDProvider uuidProvider;
    LoggerFacade loggerFacade;
    EventBus eventBus;

    @Override
    public void execute() {
        if (repository.getBy(dateTimeProvider.nowDate()).isPresent()) {
            integrateTradingData();
        }
    }

    public void enableUpdate(List<UUID> ids) {
        final Exchange exchange = getExchangeFromRepo();
        exchange.enableUpdate(ids);
        repository.save(exchange);
    }

    public void disableUpdate(List<UUID> ids) {
        final Exchange exchange = getExchangeFromRepo();
        exchange.disableUpdate(ids);
        repository.save(exchange);
    }

    //Публикует данные в топик "Инструменты"
    public void integrateWithDataSource() {
        final Exchange exchange =
            repository.getBy(dateTimeProvider.nowDate()).orElseGet(this::newExchange);
        loggerFacade.logRunSynchronizeWithDataSource(exchange.getName(), dateTimeProvider.nowDateTime());
        exchangeProvider.fetchInstruments().forEach(exchange::saveInstrument);
        repository.save(exchange);
        loggerFacade.logFinishSynchronizeWithDataSource(exchange.getName(), dateTimeProvider.nowDateTime());
    }

    //Публикует данные в два топика - внутридневные данные и исторические данные.
    //Пока просто событие для частичного ухода от шедулера
    public void integrateTradingData() {
        final Exchange exchange = getExchangeFromRepo();
        exchange.getUpdatableInstruments().forEach(instrument -> {
            loggerFacade.logRunUpdateMarketData(instrument, dateTimeProvider.nowDateTime());
            exchangeProvider
                .fetchIntradayValuesBy(instrument)
                .stream()
                .filter(row -> row.getDateTime().toLocalDate().isEqual(dateTimeProvider.nowDate()))
                .forEach(instrument::addNewIntradayValue);
            exchangeProvider
                .fetchDailyTradingResultsBy(instrument)
                .forEach(instrument::addNewDailyValue);
            loggerFacade.logFinishUpdateMarketData(instrument, dateTimeProvider.nowDateTime());
        });
        repository.save(exchange);
        eventBus.publish(new TradingDataUpdatedEvent(uuidProvider.generate(), dateTimeProvider.nowDateTime()));
    }

    private Exchange getExchangeFromRepo() {
        return repository.getBy(dateTimeProvider.nowDate()).orElseThrow(exchangeNotFound());
    }

    private Supplier<ApplicationException> exchangeNotFound() {
        return () -> new ApplicationException("Биржа не зарегистрирована.");
    }

    private Exchange newExchange() {
        return Exchange
            .builder()
            .id(uuidProvider.generate())
            .name(configureProvider.exchangeName())
            .url(configureProvider.exchangeServerUrl())
            .description(configureProvider.exchangeDescription())
            .updatable(new HashSet<>())
            .instruments(new ArrayList<>())
            .build();
    }
}
