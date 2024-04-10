create table if not exists archived_history_value
(
    close_price   double precision not null,
    high_price    double precision,
    low_price     double precision,
    open_price    double precision not null,
    trade_date    date             not null,
    value         double precision not null,
    wa_price      double precision,
    id            bigserial
        primary key,
    ticker        varchar(255)     not null,
    datasource_id uuid             not null,
    unique(datasource_id, trade_date, ticker)
);

alter table archived_history_value
    owner to postgres;

create table if not exists archived_intraday_value
(
    is_buy              boolean,
    price               double precision not null,
    qnt                 integer,
    value               double precision,
    date_time           timestamp(6)     not null,
    id                  bigserial
        primary key,
    number              bigint           not null,
    intraday_value_type varchar(255)     not null,
    ticker              varchar(255)     not null,
    datasource_id       uuid             not null,
    unique(number, datasource_id, ticker)
);

alter table archived_intraday_value
    owner to postgres;

create table if not exists datasource
(
    id          uuid not null
        primary key,
    description varchar(255),
    name        varchar(255),
    url         varchar(255)
);

alter table datasource
    owner to postgres;

create table if not exists history_value
(
    close_price   double precision not null,
    high_price    double precision,
    low_price     double precision,
    open_price    double precision not null,
    trade_date    date             not null,
    value         double precision not null,
    wa_price      double precision,
    id            bigserial
        primary key,
    ticker        varchar(255)     not null,
    datasource_id uuid             not null,
    unique(datasource_id, trade_date, ticker)
);

alter table history_value
    owner to postgres;

create table if not exists instrument
(
    annual_high         double precision,
    annual_low          double precision,
    high_limit          double precision,
    initial_margin      double precision,
    last_history_date   date,
    list_level          integer,
    lot_size            integer,
    lot_volume          integer,
    low_limit           double precision,
    updatable           boolean,
    last_trading_number bigint,
    datasource_id       uuid
        constraint fko12pnddmjd9f5v3u21t3ydro8
            references datasource,
    id                  uuid not null primary key,
    instrument_type     varchar(255) not null,
    asset_code          varchar(255),
    face_unit           varchar(255),
    isin                varchar(255),
    name                varchar(255) not null,
    reg_number          varchar(255),
    short_name          varchar(255) not null,
    ticker              varchar(255) not null,
    unique(ticker, datasource_id)
);

alter table instrument
    owner to postgres;

create table if not exists intraday_value
(
    is_buy              boolean,
    price               double precision not null,
    qnt                 integer,
    value               double precision not null,
    date_time           timestamp(6)     not null,
    id                  bigserial primary key,
    number              bigint    not null,
    intraday_value_type varchar(255)     not null,
    ticker              varchar(255)     not null,
    datasource_id       uuid             not null,
    unique(number, datasource_id, ticker)
);

alter table intraday_value
    owner to postgres;

create table if not exists scanner
(
    futures_overnight_scale  double precision,
    history_period           integer,
    history_scale            double precision,
    intraday_scale           double precision,
    scale_coefficient        double precision,
    spread_param             double precision,
    stock_overnight_scale    double precision,
    work_period_in_minutes   integer,
    last_execution_date_time timestamp(6),
    id                       uuid         not null primary key,
    scanner_type             varchar(255) not null,
    description              varchar(255),
    futures_ticker           varchar(255),
    index_ticker             varchar(255),
    datasource_id            uuid         not null
);

alter table scanner
    owner to postgres;

create table if not exists scanner_log
(
    date_time  timestamp(6),
    id         bigserial
        primary key,
    scanner_id uuid,
    message    varchar(255)
);

alter table scanner_log
    owner to postgres;

create table if not exists scanner_entity_tickers
(
    scanner_entity_id uuid not null
        constraint fk5bt9iv5oxecq3mh7u0pes3xkl
            references scanner,
    tickers           varchar(255)
);

alter table scanner_entity_tickers
    owner to postgres;

create table if not exists signal
(
    is_buy     boolean not null,
    date_time  timestamp(6),
    id         bigserial
        primary key,
    scanner_id uuid    not null
        constraint fkequgd386jasnr67d0w1b1y1sb
            references scanner,
    ticker     varchar(255)
);

alter table signal
    owner to postgres;

CREATE INDEX IF NOT EXISTS idx_instrument_ticker ON instrument (ticker);
CREATE INDEX IF NOT EXISTS idx_instrument_datasource_id ON instrument (datasource_id);
CREATE INDEX IF NOT EXISTS idx_intraday_value_ticker ON intraday_value (ticker);
CREATE INDEX IF NOT EXISTS idx_intraday_value_datasource_id ON intraday_value (datasource_id);
CREATE INDEX IF NOT EXISTS idx_history_value_ticker ON history_value (ticker);
CREATE INDEX IF NOT EXISTS idx_history_value_datasource_id ON history_value (datasource_id);

create or replace procedure archiving_intraday_values()
    language plpgsql
as
$$
declare
    lastDateTime timestamp;
begin
    select into lastDateTime from archived_intraday_value order by date_time desc limit 1;
    if lastDateTime is not null then
        insert into archived_intraday_value (select *
                                             from intraday_value
                                             where intraday_value.date_time > lastDateTime);
    else
        insert into archived_intraday_value (select * from intraday_value);
    end if;
    commit;
end;
$$;