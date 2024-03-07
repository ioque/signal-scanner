create table if not exists archived_daily_value
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

alter table archived_daily_value
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
    ticker              varchar(255)     not null
);

alter table archived_intraday_value
    owner to postgres;

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
    updatable       boolean,
    id              uuid         not null
        primary key,
    instrument_type varchar(255) not null,
    asset_code      varchar(255),
    face_unit       varchar(255),
    isin            varchar(255),
    name            varchar(255) not null,
    reg_number      varchar(255),
    short_name      varchar(255) not null,
    ticker          varchar(255) not null
);

alter table instrument
    owner to postgres;

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


create table if not exists instrument_statistic
(
    id                       uuid             not null primary key,
    instrument_id            uuid             not null,
    today_value              double precision not null,
    history_median_value     double precision not null,
    today_last_price         double precision not null,
    today_open_price         double precision not null,
    buy_to_sell_values_ratio double precision not null
);

alter table instrument_statistic
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

create table if not exists signal_scanner
(
    futures_overnight_scale double precision,
    history_period          integer,
    history_scale           double precision,
    intraday_scale          double precision,
    scale_coefficient       double precision,
    spread_param            double precision,
    stock_overnight_scale   double precision,
    last_work_date_time     timestamp(6),
    id                      uuid         not null
        primary key,
    signal_scanner_type     varchar(255) not null,
    description             varchar(255),
    futures_ticker          varchar(255),
    index_ticker            varchar(255)
);

alter table signal_scanner
    owner to postgres;

create table if not exists signal
(
    is_buy        boolean not null,
    date_time     timestamp(6),
    id            bigserial
        primary key,
    instrument_id uuid,
    scanner_id    uuid    not null
        constraint fk8d7bbd3drg5o11f0nu4x8rp3w
            references signal_scanner
);

alter table signal
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

create or replace procedure archiving_daily_values()
    language plpgsql
as
$$
declare
    lastDate date;
begin
    select into lastDate from archived_daily_value order by trade_date desc limit 1;
    if lastDate is not null then
        insert into archived_daily_value (select * from daily_value where daily_value.trade_date > lastDate);
    else
        insert into archived_daily_value (select * from daily_value);
    end if;
    commit;
end;
$$;

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