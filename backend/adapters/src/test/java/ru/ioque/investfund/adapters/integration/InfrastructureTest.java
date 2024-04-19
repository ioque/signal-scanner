package ru.ioque.investfund.adapters.integration;

import org.springframework.boot.test.context.SpringBootTest;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.Ticker;

@SpringBootTest
public abstract class InfrastructureTest {
    protected static final InstrumentId COMP_ID = new InstrumentId(new Ticker("COMP"));
    protected static final InstrumentId APPLP_ID = new InstrumentId(new Ticker("APPLP"));
    protected static final InstrumentId APPL_ID = new InstrumentId(new Ticker("APPL"));
    protected static final InstrumentId AFKS_ID = new InstrumentId(new Ticker("AFKS"));
    protected static final InstrumentId SBER_ID = new InstrumentId(new Ticker("SBER"));
    protected static final InstrumentId SBERP_ID = new InstrumentId(new Ticker("SBERP"));
    protected static final InstrumentId TGKN_ID = new InstrumentId(new Ticker("TGKN"));
    protected static final InstrumentId TGKB_ID = new InstrumentId(new Ticker("TGKB"));
    protected static final InstrumentId IMOEX_ID = new InstrumentId(new Ticker("IMOEX"));
    protected static final InstrumentId BRF4_ID = new InstrumentId(new Ticker("BRF4"));
}
