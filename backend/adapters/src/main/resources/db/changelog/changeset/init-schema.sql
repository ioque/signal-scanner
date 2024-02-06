create table if not exists daily_value
(
    capitalization      double precision,
    close_price         double precision not null,
    max_price           double precision not null,
    min_price           double precision not null,
    num_trades          double precision,
    open_position_value double precision,
    open_price          double precision not null,
    trade_date          date             not null,
    value               double precision not null,
    volume              double precision,
    wa_price            double precision,
    id                  bigserial
        primary key,
    daily_value_type    varchar(255)     not null,
    ticker              varchar(255)     not null
);

alter table daily_value
    owner to postgres;

create table if not exists exchange
(
    start_time  time(6),
    stop_time   time(6),
    id          uuid not null
        primary key,
    description varchar(255),
    name        varchar(255),
    url         varchar(255)
);

alter table exchange
    owner to postgres;

create table if not exists exchange_updatable
(
    exchange_id uuid not null
        constraint fkmnq54ih58xe10001qbpwi1oqg
            references exchange,
    updatable   uuid
);

alter table exchange_updatable
    owner to postgres;

create table if not exists instrument
(
    annual_high     double precision,
    annual_low      double precision,
    high_limit      double precision,
    initial_margin  double precision,
    list_level      integer,
    lot_size        integer,
    lot_volume      integer,
    low_limit       double precision,
    id              uuid         not null
        primary key,
    instrument_type varchar(255) not null,
    asset_code      varchar(255),
    face_unit       varchar(255),
    isin            varchar(255),
    name            varchar(255) not null,
    reg_number      varchar(255),
    short_name      varchar(255) not null,
    ticker          varchar(255) not null,
    updatable boolean
);

alter table instrument owner to postgres;

create table if not exists intraday_value
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
    ticker              varchar(255)     not null
);

alter table intraday_value
    owner to postgres;

create table if not exists report
(
    id         bigserial
        primary key,
    time       timestamp(6),
    scanner_id uuid
);

alter table report
    owner to postgres;

create table if not exists report_log
(
    id        bigserial
        primary key,
    report_id bigint
        constraint fkm8xu1ksrkbh0bq6bm5i51iq5g
            references report,
    time      timestamp(6) with time zone,
    message   varchar(255)
);

alter table report_log
    owner to postgres;

create table if not exists schedule_unit
(
    priority                 integer      not null,
    start_time               time(6)      not null,
    stop_time                time(6)      not null,
    last_execution_date_time timestamp(6),
    id                       uuid         not null
        primary key,
    system_module            varchar(255) not null
        constraint schedule_unit_system_module_check
            check ((system_module)::text = ANY
                   ((ARRAY ['EXCHANGE'::character varying, 'SIGNAL_SCANNER'::character varying])::text[]))
);

alter table schedule_unit
    owner to postgres;

create table if not exists signal
(
    is_buy        boolean not null,
    id            bigserial
        primary key,
    report_id     bigint
        constraint fkoivbnnu5f0u4qk9nmy9wtgdyl
            references report,
    instrument_id uuid
);

alter table signal
    owner to postgres;

create table if not exists signal_scanner
(
    futures_overnight_scale double precision,
    history_period          integer,
    history_scale           double precision,
    intraday_scale          double precision,
    spread_param            double precision,
    start_scale_coefficient double precision,
    stock_overnight_scale   double precision,
    id                      uuid         not null
        primary key,
    signal_scanner_type     varchar(255) not null,
    description             varchar(255),
    futures_ticker          varchar(255),
    index_ticker            varchar(255)
);

alter table signal_scanner
    owner to postgres;

create table if not exists signal_scanner_entity_object_ids
(
    object_ids               uuid,
    signal_scanner_entity_id uuid not null
        constraint fk39xvcm0wyskpyfmvbukkyrajk
            references signal_scanner
);

alter table signal_scanner_entity_object_ids
    owner to postgres;