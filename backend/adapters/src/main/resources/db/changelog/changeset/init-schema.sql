create table if not exists archived_history_value
(
    close_price         double precision not null,
    max_price           double precision not null,
    min_price           double precision not null,
    num_trades          double precision,
    open_price          double precision not null,
    trade_date          date             not null,
    value               double precision not null,
    wa_price            double precision,
    id                  bigserial primary key,
    ticker              varchar(255)     not null
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
    id                  bigserial primary key,
    number              bigint           not null,
    intraday_value_type varchar(255)     not null,
    ticker              varchar(255)     not null
);

alter table archived_intraday_value
    owner to postgres;

create table if not exists history_value
(
    close_price         double precision not null,
    max_price           double precision not null,
    min_price           double precision not null,
    num_trades          double precision,
    open_price          double precision not null,
    trade_date          date             not null,
    value               double precision not null,
    wa_price            double precision,
    id                  bigserial  primary key,
    ticker              varchar(255)     not null
);

alter table history_value
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

create table if not exists signal_scanner
(
    futures_overnight_scale  double precision,
    history_period           integer,
    history_scale            double precision,
    intraday_scale           double precision,
    scale_coefficient        double precision,
    spread_param             double precision,
    stock_overnight_scale    double precision,
    work_period_in_minutes   int,
    last_execution_date_time timestamp(6),
    id                       uuid         not null
        primary key,
    signal_scanner_type      varchar(255) not null,
    description              varchar(255),
    futures_ticker           varchar(255),
    index_ticker             varchar(255)
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

create or replace procedure archiving_history_values()
    language plpgsql
as
$$
declare
    lastDate date;
begin
    select into lastDate from archived_history_value order by trade_date desc limit 1;
    if lastDate is not null then
        insert into archived_history_value (select * from history_value where history_value.trade_date > lastDate);
    else
        insert into archived_history_value (select * from history_value);
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