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
    id                  bigserial primary key,
    daily_value_type    varchar(255)     not null,
    ticker              varchar(255)     not null
);

alter table daily_value owner to postgres;


create table if not exists intraday_value
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

alter table intraday_value owner to postgres;