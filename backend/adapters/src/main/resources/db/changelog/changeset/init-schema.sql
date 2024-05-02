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

create table if not exists instrument
(
    updatable     boolean,
    datasource_id uuid
        constraint fko12pnddmjd9f5v3u21t3ydro8
            references datasource,
    id            uuid         not null
        primary key,
    ticker        varchar(255) not null,
    type          varchar(255)
        constraint instrument_type_check
            check ((type)::text = ANY
                   ((ARRAY ['STOCK'::character varying, 'FUTURES'::character varying, 'INDEX'::character varying, 'CURRENCY_PAIR'::character varying])::text[]))
);

alter table instrument
    owner to postgres;

create table if not exists aggregated_history
(
    close_price   double precision not null,
    date          date             not null,
    high_price    double precision,
    low_price     double precision,
    open_price    double precision not null,
    value         double precision not null,
    wa_price      double precision,
    instrument_id uuid             not null
        constraint fk8vp22v6o2tyxjwg98xl69840a
            references instrument,
    primary key (date, instrument_id)
);

alter table aggregated_history
    owner to postgres;

create table if not exists instrument_details
(
    annual_high    double precision,
    annual_low     double precision,
    high_limit     double precision,
    initial_margin double precision,
    list_level     integer,
    lot_size       integer,
    lot_volume     integer,
    low_limit      double precision,
    instrument_id  uuid         not null
        primary key
        constraint fk4i211x487jo4io9eocnsaldro
            references instrument,
    dtype          varchar(31)  not null,
    asset_code     varchar(255),
    face_unit      varchar(255),
    isin           varchar(255),
    name           varchar(255) not null,
    reg_number     varchar(255),
    short_name     varchar(255) not null
);

alter table instrument_details
    owner to postgres;

create table if not exists intraday_value
(
    is_buy              boolean,
    price               double precision not null,
    qnt                 integer,
    value               double precision not null,
    date_time           timestamp(6)     not null,
    number              bigint           not null,
    intraday_value_type varchar(255)     not null,
    ticker              varchar(255)     not null,
    primary key (number, ticker)
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
    datasource_id            uuid,
    id                       uuid         not null
        primary key,
    scanner_type             varchar(255) not null,
    description              varchar(255),
    futures_ticker           varchar(255),
    index_ticker             varchar(255)
);

alter table scanner
    owner to postgres;

create table if not exists emulated_position
(
    close_price   double precision,
    is_open       boolean,
    last_price    double precision,
    open_price    double precision,
    profit        double precision,
    id            uuid not null
        primary key,
    instrument_id uuid
        constraint fkabiupmcmeb2ltnh34i6hs4pu2
            references instrument,
    scanner_id    uuid
        constraint fkik2gca5dwn872vkfnt217mf93
            references scanner,
    unique (instrument_id, scanner_id)
);

alter table emulated_position
    owner to postgres;

create table if not exists scanner_entity_instrument_ids
(
    instrument_ids    uuid,
    scanner_entity_id uuid not null
        constraint fkhrf0bdyiaweq6nwfx9wav6xic
            references scanner
);

alter table scanner_entity_instrument_ids
    owner to postgres;

create table if not exists signal
(
    is_buy        boolean not null,
    price         double precision,
    date_time     timestamp(6),
    instrument_id uuid    not null,
    scanner_id    uuid    not null
        constraint fkequgd386jasnr67d0w1b1y1sb
            references scanner,
    summary       varchar(255),
    primary key (is_buy, instrument_id, scanner_id)
);

alter table signal
    owner to postgres;

create table if not exists telegram_chat
(
    chat_id    bigint not null
        primary key,
    created_at timestamp(6),
    name       varchar(255)
);

alter table telegram_chat
    owner to postgres;

create table if not exists trading_state
(
    today_first_price    double precision,
    today_last_price     double precision,
    today_value          double precision,
    date_time            timestamp(6),
    last_intraday_number bigint,
    instrument_id        uuid not null
        primary key
        constraint fkcrssymi0qdlwvy67fxlh067hy
            references instrument
);

alter table trading_state
    owner to postgres;