-- Bybit parser tables and policies (TimescaleDB PG17)

-- =========================
-- BYBIT LAUNCH POOL (LPL)
-- =========================

create TABLE IF NOT EXISTS crypto_scout.bybit_lpl (
    return_coin TEXT NOT NULL,
    return_coin_icon TEXT NOT NULL,
    description TEXT NOT NULL,
    website TEXT NOT NULL,
    whitepaper TEXT NOT NULL,
    rules TEXT NOT NULL,
    stake_begin_time TIMESTAMP WITH TIME ZONE NOT NULL,
    stake_end_time TIMESTAMP WITH TIME ZONE NOT NULL,
    trade_begin_time TIMESTAMP WITH TIME ZONE,
    CONSTRAINT bybit_lpl_pkey PRIMARY KEY (return_coin, stake_begin_time)
);

alter table crypto_scout.bybit_lpl OWNER TO crypto_scout_db;
create index IF NOT EXISTS idx_bybit_lpl_stake_end_time ON crypto_scout.bybit_lpl(stake_end_time DESC);
create index IF NOT EXISTS idx_bybit_lpl_trade_begin_time ON crypto_scout.bybit_lpl(trade_begin_time DESC);
select public.create_hypertable('crypto_scout.bybit_lpl', 'stake_begin_time', chunk_time_interval => INTERVAL '1 year', if_not_exists => TRUE);

alter table crypto_scout.bybit_lpl set (
    timescaledb.compress,
    timescaledb.compress_segmentby = 'return_coin',
    timescaledb.compress_orderby = 'stake_begin_time DESC'
);

select public.add_compression_policy('crypto_scout.bybit_lpl', interval '35 days');

-- =========================
-- CMC FEAR & GREED INDEX (FGI)
-- =========================

create TABLE IF NOT EXISTS crypto_scout.cmc_fgi (
    value INTEGER NOT NULL,
    value_classification TEXT NOT NULL CHECK (value_classification IN ('Extreme Fear','Fear','Neutral','Greed','Extreme Greed')),
    update_time TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fgi_pkey PRIMARY KEY (update_time)
);

alter table crypto_scout.cmc_fgi OWNER TO crypto_scout_db;
create index IF NOT EXISTS idx_cmc_fgi_update_time ON crypto_scout.cmc_fgi(update_time DESC);
select public.create_hypertable('crypto_scout.cmc_fgi', 'update_time', chunk_time_interval => INTERVAL '1 month', if_not_exists => TRUE);

alter table crypto_scout.cmc_fgi set (
    timescaledb.compress,
    timescaledb.compress_orderby = 'update_time DESC'
);

select public.add_compression_policy('crypto_scout.cmc_fgi', INTERVAL '35 days');
select public.add_reorder_policy('crypto_scout.cmc_fgi', 'idx_cmc_fgi_update_time');

-- =========================
-- KLINE TABLES (1d/1w)
-- Schema is identical across intervals.
-- =========================

create TABLE IF NOT EXISTS crypto_scout.cmc_kline_1d (
    symbol TEXT NOT NULL,
    time_open TIMESTAMP WITH TIME ZONE NOT NULL,
    time_close TIMESTAMP WITH TIME ZONE NOT NULL,
    time_high TIMESTAMP WITH TIME ZONE NOT NULL,
    time_low  TIMESTAMP WITH TIME ZONE NOT NULL,
    open DOUBLE PRECISION NOT NULL,
    high DOUBLE PRECISION NOT NULL,
    low DOUBLE PRECISION NOT NULL,
    close DOUBLE PRECISION NOT NULL,
    volume DOUBLE PRECISION NOT NULL,
    market_cap DOUBLE PRECISION NOT NULL,
    circulating_supply BIGINT NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT cmc_kline_1d_pkey PRIMARY KEY (symbol, timestamp)
);

alter table crypto_scout.cmc_kline_1d OWNER TO crypto_scout_db;
create index IF NOT EXISTS idx_cmc_kline_1d_symbol_time ON crypto_scout.cmc_kline_1d(symbol, timestamp DESC);
select public.create_hypertable('crypto_scout.cmc_kline_1d', 'timestamp', chunk_time_interval => INTERVAL '1 month', if_not_exists => TRUE);

alter table crypto_scout.cmc_kline_1d set (
    timescaledb.compress,
    timescaledb.compress_segmentby = 'symbol',
    timescaledb.compress_orderby = 'timestamp DESC'
);

select public.add_compression_policy('crypto_scout.cmc_kline_1d', INTERVAL '35 days');
select public.add_reorder_policy('crypto_scout.cmc_kline_1d', 'idx_cmc_kline_1d_symbol_time');

create TABLE IF NOT EXISTS crypto_scout.cmc_kline_1w (
    symbol TEXT NOT NULL,
    time_open TIMESTAMP WITH TIME ZONE NOT NULL,
    time_close TIMESTAMP WITH TIME ZONE NOT NULL,
    time_high TIMESTAMP WITH TIME ZONE NOT NULL,
    time_low  TIMESTAMP WITH TIME ZONE NOT NULL,
    open DOUBLE PRECISION NOT NULL,
    high DOUBLE PRECISION NOT NULL,
    low DOUBLE PRECISION NOT NULL,
    close DOUBLE PRECISION NOT NULL,
    volume DOUBLE PRECISION NOT NULL,
    market_cap DOUBLE PRECISION NOT NULL,
    circulating_supply BIGINT NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT cmc_kline_1w_pkey PRIMARY KEY (symbol, timestamp)
);

alter table crypto_scout.cmc_kline_1w OWNER TO crypto_scout_db;
create index IF NOT EXISTS idx_cmc_kline_1w_symbol_time ON crypto_scout.cmc_kline_1w(symbol, timestamp DESC);
select public.create_hypertable('crypto_scout.cmc_kline_1w', 'timestamp', chunk_time_interval => INTERVAL '3 months', if_not_exists => TRUE);

alter table crypto_scout.cmc_kline_1w set (
    timescaledb.compress,
    timescaledb.compress_segmentby = 'symbol',
    timescaledb.compress_orderby = 'timestamp DESC'
);

select public.add_compression_policy('crypto_scout.cmc_kline_1w', INTERVAL '35 days');
select public.add_reorder_policy('crypto_scout.cmc_kline_1w', 'idx_cmc_kline_1w_symbol_time');