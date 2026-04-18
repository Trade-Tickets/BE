-- ============================================================
-- 1. DỌN DẸP SẠCH (TRÁNH LỖI ĐỤNG HÀNG)
-- ============================================================
DROP TABLE IF EXISTS public.trade_history CASCADE;

DROP TABLE IF EXISTS public.wallet_profiles CASCADE;

DROP TABLE IF EXISTS public.transactions CASCADE;

DROP TABLE IF EXISTS public.listings CASCADE;

DROP TABLE IF EXISTS public.tickets CASCADE;

DROP TABLE IF EXISTS public.events CASCADE;

DROP TABLE IF EXISTS public.comments CASCADE;

DROP TABLE IF EXISTS public.wallet_trades CASCADE;

DROP TABLE IF EXISTS public.profiles CASCADE;

DROP TYPE IF EXISTS ticket_status CASCADE;

-- ============================================================
-- 2. TẠO SCHEMA MỚI NHẤT
-- ============================================================
CREATE TYPE ticket_status AS ENUM ('available', 'sold', 'listing');

-- Lưu thông tin tổng hợp của từng ví SUI
CREATE TABLE public.wallet_profiles (
    wallet_address TEXT PRIMARY KEY,
    first_seen_at TIMESTAMPTZ DEFAULT NOW(),
    last_active_at TIMESTAMPTZ DEFAULT NOW(),
    total_buy_volume NUMERIC(18, 9) DEFAULT 0,
    total_sell_volume NUMERIC(18, 9) DEFAULT 0,
    total_trades INTEGER DEFAULT 0
);

-- Lưu lịch sử từng lệnh trade của ví
CREATE TABLE public.trade_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
    wallet_address TEXT NOT NULL REFERENCES public.wallet_profiles (wallet_address) ON DELETE CASCADE,
    event_id TEXT,
    event_title TEXT,
    ticket_class TEXT,
    trade_type TEXT NOT NULL, -- 'buy' hoặc 'sell'
    price_sui NUMERIC(18, 9),
    quantity INTEGER DEFAULT 1,
    total_cost NUMERIC(18, 9),
    platform_fee NUMERIC(18, 9),
    sell_tax NUMERIC(18, 9),
    sui_tx_digest TEXT,
    status TEXT DEFAULT 'filled', -- 'filled' | 'open' | 'cancelled'
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_trade_history_wallet ON public.trade_history (wallet_address);

CREATE INDEX idx_trade_history_created ON public.trade_history (created_at DESC);

CREATE TABLE public.profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
    username TEXT,
    full_name TEXT,
    avatar_url TEXT,
    sui_wallet_address TEXT
);

CREATE TABLE public.comments (
    id TEXT PRIMARY KEY,
    event_id TEXT NOT NULL,
    parent_id TEXT,
    author TEXT NOT NULL,
    avatar TEXT NOT NULL,
    content TEXT NOT NULL,
    timestamp TEXT NOT NULL,
    likes INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE public.wallet_trades (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
    wallet_address TEXT NOT NULL,
    client_order_id TEXT NOT NULL,
    event_id TEXT NOT NULL,
    event_title TEXT NOT NULL,
    ticket_class TEXT NOT NULL,
    trade_type TEXT NOT NULL,
    price_sui NUMERIC(18, 9) NOT NULL,
    quantity INTEGER NOT NULL,
    total_cost NUMERIC(18, 9) NOT NULL,
    platform_fee NUMERIC(18, 9) NOT NULL DEFAULT 0,
    sell_tax NUMERIC(18, 9) NOT NULL DEFAULT 0,
    sui_tx_digest TEXT,
    status TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (
        wallet_address,
        client_order_id
    )
);

CREATE TABLE public.events (
  id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  created_at          TIMESTAMPTZ DEFAULT NOW(),
  title               TEXT,
  image_url           TEXT,
  location            TEXT,
  start_time          TIMESTAMPTZ,
  description         TEXT,
  about               TEXT,
  lineup              TEXT[],
  organizer_id        UUID,
  organizer_name      TEXT,
  tags                TEXT[],
  trading_status      TEXT,
  settlement_date     TIMESTAMPTZ,
  market_stats        JSONB DEFAULT '[]'::jsonb
);

CREATE TABLE public.tickets (
  id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  event_id            UUID,
  owner_id            UUID,
  sui_object_id       TEXT, 
  status              ticket_status,
  price_sui           NUMERIC(18, 9),
  metadata            JSONB DEFAULT '{}'::jsonb
);

CREATE TABLE public.listings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
    ticket_id UUID,
    seller_id UUID,
    price NUMERIC(18, 9)
);

CREATE TABLE public.transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    buyer_id UUID,
    seller_id UUID,
    ticket_id UUID,
    amount NUMERIC(18, 9),
    sui_transaction_digest TEXT
);

-- ============================================================
-- 3. FILL TOÀN BỘ MOCK DATA
-- ============================================================
DO $$
DECLARE
    -- Khởi tạo UUID để link các bảng với nhau
    u_seller UUID := gen_random_uuid();
    u_buyer UUID := gen_random_uuid();
    
    evt_1 UUID := gen_random_uuid();
    evt_2 UUID := gen_random_uuid();
    evt_3 UUID := gen_random_uuid();
    evt_4 UUID := gen_random_uuid();
    evt_5 UUID := gen_random_uuid();
    evt_6 UUID := gen_random_uuid();
    
    t_id UUID;
BEGIN
    -- --- TẠO USERS ---
    INSERT INTO public.profiles (id, username, full_name) VALUES 
    (u_seller, 'seller_whale', 'Whale Seller'),
    (u_buyer, 'buyer_fan', 'EDM Fanboy');

    -- --- TẠO 6 EVENTS CHUẨN MOCK_EVENTS ---
    
    -- Event 1
    INSERT INTO public.events (id, title, image_url, location, start_time, description, about, lineup, organizer_name, tags, trading_status, settlement_date, market_stats)
    VALUES (evt_1, 'Sui Builder House & Hackathon', 'https://images.unsplash.com/photo-1540039155732-684735035727?q=80&w=2070&auto=format&fit=crop', 'Tokyo, Japan', '2026-10-15 09:00:00+09', 'Join the largest Sui builder gathering in Asia. Connect, hack, and win.', 'Step into the epicenter of Web3 innovation at the Sui Builder House Tokyo. Experience 3 days of intense hacking...', ARRAY['Evan Cheng', 'Adeniyi Abiodun', 'Mysten Labs Team'], 'Mysten Labs', ARRAY['CRYPTO', 'HOUSE'], 'Live', '2026-10-13 00:00:00',
    '[{"ticketClass": "Early Bird", "originalPrice": 15, "floorPrice": 45, "change24h": 12.5, "volume24h": "1.2K SUI"}, {"ticketClass": "Regular", "originalPrice": 25, "floorPrice": 30, "change24h": -5.2, "volume24h": "800 SUI"}, {"ticketClass": "VIP", "originalPrice": 100, "floorPrice": 250, "change24h": 42.0, "volume24h": "5.5K SUI"}]'::jsonb);

    -- Event 2
    INSERT INTO public.events (id, title, image_url, location, start_time, description, about, lineup, organizer_name, tags, trading_status, settlement_date, market_stats)
    VALUES (evt_2, 'Cyberpunk Music Festival', 'https://images.unsplash.com/photo-1514525253161-7a46d19cd819?q=80&w=1974&auto=format&fit=crop', 'Neo-Seoul Arena', '2026-11-20 18:00:00+09', 'A night of synthwave and holographic performances.', 'Get ready to upload your consciousness into the rhythm. The Cyberpunk Music Festival brings together...', ARRAY['The Midnight', 'Kavinsky', 'Gunship', 'Holo-Band X'], 'Neon Nights Inc.', ARRAY['MUSIC', 'LIVE'], 'Live', '2026-11-18 00:00:00',
    '[{"ticketClass": "Regular", "originalPrice": 50, "floorPrice": 55, "change24h": 2.1, "volume24h": "400 SUI"}, {"ticketClass": "VIP", "originalPrice": 150, "floorPrice": 140, "change24h": -10.5, "volume24h": "1.1K SUI"}]'::jsonb);

    -- Event 3
    INSERT INTO public.events (id, title, image_url, location, start_time, description, about, lineup, organizer_name, tags, trading_status, settlement_date, market_stats)
    VALUES (evt_3, 'Web3 Gaming Summit', 'https://images.unsplash.com/photo-1542751371-adc38448a05e?q=80&w=2070&auto=format&fit=crop', 'Virtual & Dubai', '2026-12-05 10:00:00+04', 'The future of gaming meets blockchain technology.', 'Explore how blockchain is redefining digital ownership, game economies...', ARRAY['Yield Guild Games', 'Animoca Brands', 'Gala Games'], 'Blockchain Gaming Alliance', ARRAY['GAMING', 'TRENDING'], 'Live', '2026-12-03 00:00:00',
    '[{"ticketClass": "Online Standard", "originalPrice": 5, "floorPrice": 8, "change24h": 60.0, "volume24h": "2.3K SUI"}, {"ticketClass": "In-Person Pass", "originalPrice": 80, "floorPrice": 110, "change24h": 15.4, "volume24h": "3.4K SUI"}]'::jsonb);

    -- Event 4
    INSERT INTO public.events (id, title, image_url, location, start_time, description, about, lineup, organizer_name, tags, trading_status, settlement_date, market_stats)
    VALUES (evt_4, 'Devcon 8 - Global Hacker Space', 'https://images.unsplash.com/photo-1504384308090-c894fdcc538d?q=80&w=2070&auto=format&fit=crop', 'Berlin, Germany', '2027-02-12 08:00:00+01', 'Annual gathering for protocols, cryptography, and cypherpunks.', 'Devcon 8 is bringing the core developers of the decentralized web together...', ARRAY['Vitalik Buterin', 'Zooko Wilcox', 'Gavin Wood'], 'Decentralized Foundation', ARRAY['CRYPTO', 'DEV'], 'Live', '2027-02-10 00:00:00',
    '[{"ticketClass": "Hacker Pass", "originalPrice": 10, "floorPrice": 120, "change24h": 300.0, "volume24h": "15K SUI"}]'::jsonb);

    -- Event 5
    INSERT INTO public.events (id, title, image_url, location, start_time, description, about, lineup, organizer_name, tags, trading_status, settlement_date, market_stats)
    VALUES (evt_5, 'Art Block: Generative Exhibition', 'https://images.unsplash.com/photo-1561214115-f2f134cc4912?q=80&w=1909&auto=format&fit=crop', 'Paris, France', '2027-03-20 14:00:00+01', 'An immersive gallery of code-generated, on-chain artworks.', 'Experience art dictated by algorithms and immortalized on the blockchain...', ARRAY['Tyler Hobbs', 'Snowfro', 'Dmitri Cherniak'], 'OnChain Arts', ARRAY['ART', 'NFT', 'MINT'], 'Settled', '2027-03-18 00:00:00',
    '[{"ticketClass": "Gallery Entry", "originalPrice": 20, "floorPrice": 20, "change24h": 0, "volume24h": "0 SUI"}]'::jsonb);

    -- Event 6
    INSERT INTO public.events (id, title, image_url, location, start_time, description, about, lineup, organizer_name, tags, trading_status, settlement_date, market_stats)
    VALUES (evt_6, 'Startup Demo Day: Batch 44', 'https://images.unsplash.com/photo-1559136555-e4671a4f00d8?q=80&w=2069&auto=format&fit=crop', 'San Francisco, CA', '2027-04-10 09:30:00-07', 'Watch 50+ startups pitch to top-tier VCs and angel investors.', 'The culmination of a rigorous 12-week accelerator program...', ARRAY['Y Combinator Partners', 'Sequoia Capital', 'A16Z'], 'Valley Accelerators', ARRAY['STARTUP', 'PITCH'], 'Live', '2027-04-08 00:00:00',
    '[{"ticketClass": "Investor Pass", "originalPrice": 500, "floorPrice": 450, "change24h": -10.0, "volume24h": "2.5K SUI"}]'::jsonb);


    -- --- TẠO 11 TICKETS CHUẨN MOCK_TICKETS ---
    
    -- [Evt 1] t_101, t_102, t_103, t_104
    t_id := gen_random_uuid();
    INSERT INTO public.tickets (id, event_id, owner_id, sui_object_id, status, price_sui, metadata) VALUES (t_id, evt_1, u_seller, 't_101', 'available', 45, '{"ticketClass": "Early Bird"}');
    INSERT INTO public.listings (ticket_id, seller_id, price) VALUES (t_id, u_seller, 45);

    t_id := gen_random_uuid();
    INSERT INTO public.tickets (id, event_id, owner_id, sui_object_id, status, price_sui, metadata) VALUES (t_id, evt_1, u_seller, 't_102', 'available', 30, '{"ticketClass": "Regular"}');
    INSERT INTO public.listings (ticket_id, seller_id, price) VALUES (t_id, u_seller, 30);

    t_id := gen_random_uuid();
    INSERT INTO public.tickets (id, event_id, owner_id, sui_object_id, status, price_sui, metadata) VALUES (t_id, evt_1, u_seller, 't_103', 'available', 250, '{"ticketClass": "VIP"}');
    INSERT INTO public.listings (ticket_id, seller_id, price) VALUES (t_id, u_seller, 250);

    t_id := gen_random_uuid();
    INSERT INTO public.tickets (id, event_id, owner_id, sui_object_id, status, price_sui, metadata) VALUES (t_id, evt_1, u_seller, 't_104', 'listing', 255, '{"ticketClass": "VIP"}');
    INSERT INTO public.listings (ticket_id, seller_id, price) VALUES (t_id, u_seller, 255);

    -- [Evt 2] t_201, t_202
    t_id := gen_random_uuid();
    INSERT INTO public.tickets (id, event_id, owner_id, sui_object_id, status, price_sui, metadata) VALUES (t_id, evt_2, u_seller, 't_201', 'available', 55, '{"ticketClass": "Regular"}');
    INSERT INTO public.listings (ticket_id, seller_id, price) VALUES (t_id, u_seller, 55);

    t_id := gen_random_uuid();
    INSERT INTO public.tickets (id, event_id, owner_id, sui_object_id, status, price_sui, metadata) VALUES (t_id, evt_2, u_seller, 't_202', 'available', 140, '{"ticketClass": "VIP"}');
    INSERT INTO public.listings (ticket_id, seller_id, price) VALUES (t_id, u_seller, 140);

    -- [Evt 3] t_301, t_302
    t_id := gen_random_uuid();
    INSERT INTO public.tickets (id, event_id, owner_id, sui_object_id, status, price_sui, metadata) VALUES (t_id, evt_3, u_seller, 't_301', 'available', 8, '{"ticketClass": "Online Standard"}');
    INSERT INTO public.listings (ticket_id, seller_id, price) VALUES (t_id, u_seller, 8);

    t_id := gen_random_uuid();
    INSERT INTO public.tickets (id, event_id, owner_id, sui_object_id, status, price_sui, metadata) VALUES (t_id, evt_3, u_seller, 't_302', 'available', 110, '{"ticketClass": "In-Person Pass"}');
    INSERT INTO public.listings (ticket_id, seller_id, price) VALUES (t_id, u_seller, 110);

    -- [Evt 4] t_401
    t_id := gen_random_uuid();
    INSERT INTO public.tickets (id, event_id, owner_id, sui_object_id, status, price_sui, metadata) VALUES (t_id, evt_4, u_seller, 't_401', 'available', 120, '{"ticketClass": "Hacker Pass"}');
    INSERT INTO public.listings (ticket_id, seller_id, price) VALUES (t_id, u_seller, 120);

    -- [Evt 5] t_501 (Sold - KHÔNG chèn vào bảng listings)
    t_id := gen_random_uuid();
    INSERT INTO public.tickets (id, event_id, owner_id, sui_object_id, status, price_sui, metadata) VALUES (t_id, evt_5, u_buyer, 't_501', 'sold', 20, '{"ticketClass": "Gallery Entry"}');
    -- Ghi nhận giao dịch mua bán lịch sử
    INSERT INTO public.transactions (buyer_id, seller_id, ticket_id, amount, sui_transaction_digest) VALUES (u_buyer, u_seller, t_id, 20, 'tx_mock_t501_sold');

    -- [Evt 6] t_601
    t_id := gen_random_uuid();
    INSERT INTO public.tickets (id, event_id, owner_id, sui_object_id, status, price_sui, metadata) VALUES (t_id, evt_6, u_seller, 't_601', 'available', 450, '{"ticketClass": "Investor Pass"}');
    INSERT INTO public.listings (ticket_id, seller_id, price) VALUES (t_id, u_seller, 450);

END $$;

-- ============================================================
-- LÀM SẠCH DỮ LIỆU CŨ ĐỂ NẠP DỮ LIỆU MỚI (DƯỚI 2 SUI)
-- ============================================================
TRUNCATE TABLE public.comments,
public.transactions,
public.listings,
public.tickets,
public.events,
public.profiles CASCADE;

-- ============================================================
-- FILL TOÀN BỘ MOCK DATA (GIÁ < 2 SUI DÀNH CHO DEVNET/TESTNET)
-- ============================================================
DO $$
DECLARE
    u_seller UUID := gen_random_uuid();
    u_buyer UUID := gen_random_uuid();
    
    evt_1 UUID := gen_random_uuid(); evt_2 UUID := gen_random_uuid();
    evt_3 UUID := gen_random_uuid(); evt_4 UUID := gen_random_uuid();
    evt_5 UUID := gen_random_uuid(); evt_6 UUID := gen_random_uuid();
    
    t_id UUID;
    i INTEGER;
BEGIN
    -- --- TẠO USERS ---
    INSERT INTO public.profiles (id, username, full_name) VALUES 
    (u_seller, 'seller_whale', 'Whale Seller'),
    (u_buyer, 'buyer_fan', 'EDM Fanboy');

    -- --- TẠO 6 EVENTS CHUẨN MOCK_EVENTS (ĐÃ SỬA GIÁ MARKET_STATS) ---
    
    INSERT INTO public.events (id, title, image_url, location, start_time, description, about, lineup, organizer_name, tags, trading_status, settlement_date, market_stats)
    VALUES (evt_1, 'Sui Builder House & Hackathon', 'https://images.unsplash.com/photo-1540039155732-684735035727?q=80&w=2070&auto=format&fit=crop', 'Tokyo, Japan', '2026-10-15 09:00:00+09', 'Join the largest Sui builder gathering in Asia...', 'Step into the epicenter of Web3 innovation...', ARRAY['Evan Cheng', 'Adeniyi Abiodun'], 'Mysten Labs', ARRAY['CRYPTO', 'HOUSE'], 'Live', '2026-10-13 00:00:00',
    '[{"ticketClass": "Early Bird", "originalPrice": 0.15, "floorPrice": 0.45, "change24h": 12.5, "volume24h": "12.5 SUI"}, {"ticketClass": "Regular", "originalPrice": 0.25, "floorPrice": 0.30, "change24h": -5.2, "volume24h": "8.0 SUI"}, {"ticketClass": "VIP", "originalPrice": 1.00, "floorPrice": 1.50, "change24h": 42.0, "volume24h": "55.0 SUI"}]'::jsonb);

    INSERT INTO public.events (id, title, image_url, location, start_time, description, about, lineup, organizer_name, tags, trading_status, settlement_date, market_stats)
    VALUES (evt_2, 'Cyberpunk Music Festival', 'https://images.unsplash.com/photo-1514525253161-7a46d19cd819?q=80&w=1974&auto=format&fit=crop', 'Neo-Seoul Arena', '2026-11-20 18:00:00+09', 'A night of synthwave and holographic performances.', 'Get ready to upload your consciousness...', ARRAY['The Midnight', 'Kavinsky'], 'Neon Nights Inc.', ARRAY['MUSIC', 'LIVE'], 'Live', '2026-11-18 00:00:00',
    '[{"ticketClass": "Regular", "originalPrice": 0.50, "floorPrice": 0.55, "change24h": 2.1, "volume24h": "4.0 SUI"}, {"ticketClass": "VIP", "originalPrice": 1.50, "floorPrice": 1.40, "change24h": -10.5, "volume24h": "11.0 SUI"}]'::jsonb);

    INSERT INTO public.events (id, title, image_url, location, start_time, description, about, lineup, organizer_name, tags, trading_status, settlement_date, market_stats)
    VALUES (evt_3, 'Web3 Gaming Summit', 'https://images.unsplash.com/photo-1542751371-adc38448a05e?q=80&w=2070&auto=format&fit=crop', 'Virtual & Dubai', '2026-12-05 10:00:00+04', 'The future of gaming meets blockchain technology.', 'Explore how blockchain is redefining digital ownership...', ARRAY['Yield Guild Games', 'Animoca Brands'], 'Blockchain Gaming Alliance', ARRAY['GAMING', 'TRENDING'], 'Live', '2026-12-03 00:00:00',
    '[{"ticketClass": "Online Standard", "originalPrice": 0.05, "floorPrice": 0.08, "change24h": 60.0, "volume24h": "2.3 SUI"}, {"ticketClass": "In-Person Pass", "originalPrice": 0.80, "floorPrice": 1.10, "change24h": 15.4, "volume24h": "3.4 SUI"}]'::jsonb);

    INSERT INTO public.events (id, title, image_url, location, start_time, description, about, lineup, organizer_name, tags, trading_status, settlement_date, market_stats)
    VALUES (evt_4, 'Devcon 8 - Global Hacker Space', 'https://images.unsplash.com/photo-1504384308090-c894fdcc538d?q=80&w=2070&auto=format&fit=crop', 'Berlin, Germany', '2027-02-12 08:00:00+01', 'Annual gathering for protocols, cryptography...', 'Devcon 8 is bringing the core developers...', ARRAY['Vitalik Buterin', 'Gavin Wood'], 'Decentralized Foundation', ARRAY['CRYPTO', 'DEV'], 'Live', '2027-02-10 00:00:00',
    '[{"ticketClass": "Hacker Pass", "originalPrice": 0.10, "floorPrice": 1.20, "change24h": 300.0, "volume24h": "15.0 SUI"}]'::jsonb);

    INSERT INTO public.events (id, title, image_url, location, start_time, description, about, lineup, organizer_name, tags, trading_status, settlement_date, market_stats)
    VALUES (evt_5, 'Art Block: Generative Exhibition', 'https://images.unsplash.com/photo-1561214115-f2f134cc4912?q=80&w=1909&auto=format&fit=crop', 'Paris, France', '2027-03-20 14:00:00+01', 'An immersive gallery of code-generated, on-chain artworks.', 'Experience art dictated by algorithms...', ARRAY['Tyler Hobbs', 'Snowfro'], 'OnChain Arts', ARRAY['ART', 'NFT', 'MINT'], 'Settled', '2027-03-18 00:00:00',
    '[{"ticketClass": "Gallery Entry", "originalPrice": 0.20, "floorPrice": 0.20, "change24h": 0, "volume24h": "0 SUI"}]'::jsonb);

    INSERT INTO public.events (id, title, image_url, location, start_time, description, about, lineup, organizer_name, tags, trading_status, settlement_date, market_stats)
    VALUES (evt_6, 'Startup Demo Day: Batch 44', 'https://images.unsplash.com/photo-1559136555-e4671a4f00d8?q=80&w=2069&auto=format&fit=crop', 'San Francisco, CA', '2027-04-10 09:30:00-07', 'Watch 50+ startups pitch to top-tier VCs...', 'The culmination of a rigorous 12-week accelerator...', ARRAY['Y Combinator Partners', 'Sequoia Capital'], 'Valley Accelerators', ARRAY['STARTUP', 'PITCH'], 'Live', '2027-04-08 00:00:00',
    '[{"ticketClass": "Investor Pass", "originalPrice": 1.50, "floorPrice": 1.90, "change24h": -10.0, "volume24h": "2.5 SUI"}]'::jsonb);

    -- --- THÊM 20 EVENTS MỚI ---
    FOR i IN 1..20 LOOP
        INSERT INTO public.events (
            id, title, image_url, location, start_time, description, about,
            lineup, organizer_name, tags, trading_status, settlement_date, market_stats
        ) VALUES (
            gen_random_uuid(),
            'Community Event #' || i,
            'https://picsum.photos/seed/fairticket' || i || '/1200/600',
            CASE WHEN i % 5 = 0 THEN 'Singapore' WHEN i % 5 = 1 THEN 'Seoul' WHEN i % 5 = 2 THEN 'Bangkok' WHEN i % 5 = 3 THEN 'Hanoi' ELSE 'Tokyo' END,
            NOW() + (i || ' days')::interval,
            'Dummy event #' || i || ' for FE/BE pagination testing.',
            'Extended description for dummy event #' || i || '. This event is seeded for integration test.',
            ARRAY['Speaker ' || i, 'Guest ' || i],
            'FairTicket Labs',
            CASE WHEN i % 3 = 0 THEN ARRAY['CRYPTO','DEV'] WHEN i % 3 = 1 THEN ARRAY['MUSIC','LIVE'] ELSE ARRAY['GAMING','TRENDING'] END,
            CASE WHEN i % 7 = 0 THEN 'Halted' WHEN i % 11 = 0 THEN 'Settled' ELSE 'Live' END,
            (NOW() + ((i + 2) || ' days')::interval),
            (
              '[{"ticketClass":"Regular","originalPrice":0.30,"floorPrice":' || to_char(0.25 + (i * 0.03), 'FM999999990.00') || ',"change24h":' || to_char((i % 9) - 4, 'FM999999990.0') || ',"volume24h":"' || (10 + i) || ' SUI"}]'
            )::jsonb
        );
    END LOOP;

    -- --- TẠO 11 TICKETS (ĐÃ SỬA GIÁ LISTINGS VÀ TICKETS < 2 SUI) ---
    
    -- [Evt 1]
    t_id := gen_random_uuid();
    INSERT INTO public.tickets (id, event_id, owner_id, sui_object_id, status, price_sui, metadata) VALUES (t_id, evt_1, u_seller, 't_101', 'available', 0.45, '{"ticketClass": "Early Bird"}');
    INSERT INTO public.listings (ticket_id, seller_id, price) VALUES (t_id, u_seller, 0.45);

    t_id := gen_random_uuid();
    INSERT INTO public.tickets (id, event_id, owner_id, sui_object_id, status, price_sui, metadata) VALUES (t_id, evt_1, u_seller, 't_102', 'available', 0.30, '{"ticketClass": "Regular"}');
    INSERT INTO public.listings (ticket_id, seller_id, price) VALUES (t_id, u_seller, 0.30);

    t_id := gen_random_uuid();
    INSERT INTO public.tickets (id, event_id, owner_id, sui_object_id, status, price_sui, metadata) VALUES (t_id, evt_1, u_seller, 't_103', 'available', 1.50, '{"ticketClass": "VIP"}');
    INSERT INTO public.listings (ticket_id, seller_id, price) VALUES (t_id, u_seller, 1.50);

    t_id := gen_random_uuid();
    INSERT INTO public.tickets (id, event_id, owner_id, sui_object_id, status, price_sui, metadata) VALUES (t_id, evt_1, u_seller, 't_104', 'listing', 1.55, '{"ticketClass": "VIP"}');
    INSERT INTO public.listings (ticket_id, seller_id, price) VALUES (t_id, u_seller, 1.55);

    -- [Evt 2]
    t_id := gen_random_uuid();
    INSERT INTO public.tickets (id, event_id, owner_id, sui_object_id, status, price_sui, metadata) VALUES (t_id, evt_2, u_seller, 't_201', 'available', 0.55, '{"ticketClass": "Regular"}');
    INSERT INTO public.listings (ticket_id, seller_id, price) VALUES (t_id, u_seller, 0.55);

    t_id := gen_random_uuid();
    INSERT INTO public.tickets (id, event_id, owner_id, sui_object_id, status, price_sui, metadata) VALUES (t_id, evt_2, u_seller, 't_202', 'available', 1.40, '{"ticketClass": "VIP"}');
    INSERT INTO public.listings (ticket_id, seller_id, price) VALUES (t_id, u_seller, 1.40);

    -- [Evt 3]
    t_id := gen_random_uuid();
    INSERT INTO public.tickets (id, event_id, owner_id, sui_object_id, status, price_sui, metadata) VALUES (t_id, evt_3, u_seller, 't_301', 'available', 0.08, '{"ticketClass": "Online Standard"}');
    INSERT INTO public.listings (ticket_id, seller_id, price) VALUES (t_id, u_seller, 0.08);

    t_id := gen_random_uuid();
    INSERT INTO public.tickets (id, event_id, owner_id, sui_object_id, status, price_sui, metadata) VALUES (t_id, evt_3, u_seller, 't_302', 'available', 1.10, '{"ticketClass": "In-Person Pass"}');
    INSERT INTO public.listings (ticket_id, seller_id, price) VALUES (t_id, u_seller, 1.10);

    -- [Evt 4]
    t_id := gen_random_uuid();
    INSERT INTO public.tickets (id, event_id, owner_id, sui_object_id, status, price_sui, metadata) VALUES (t_id, evt_4, u_seller, 't_401', 'available', 1.20, '{"ticketClass": "Hacker Pass"}');
    INSERT INTO public.listings (ticket_id, seller_id, price) VALUES (t_id, u_seller, 1.20);

    -- [Evt 5]
    t_id := gen_random_uuid();
    INSERT INTO public.tickets (id, event_id, owner_id, sui_object_id, status, price_sui, metadata) VALUES (t_id, evt_5, u_buyer, 't_501', 'sold', 0.20, '{"ticketClass": "Gallery Entry"}');
    INSERT INTO public.transactions (buyer_id, seller_id, ticket_id, amount, sui_transaction_digest) VALUES (u_buyer, u_seller, t_id, 0.20, 'tx_mock_t501_sold');

    -- [Evt 6]
    t_id := gen_random_uuid();
    INSERT INTO public.tickets (id, event_id, owner_id, sui_object_id, status, price_sui, metadata) VALUES (t_id, evt_6, u_seller, 't_601', 'available', 1.90, '{"ticketClass": "Investor Pass"}');
    INSERT INTO public.listings (ticket_id, seller_id, price) VALUES (t_id, u_seller, 1.90);

    -- --- COMMENTS cho FE DiscussionSection (dữ liệu thật từ DB) ---
    INSERT INTO public.comments (id, event_id, parent_id, author, avatar, content, timestamp, likes)
    VALUES
    ('c1', evt_1::text, NULL, '0xKrypto', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Felix', 'Prices for this event are pumping right now! Anyone selling VIP passes under 3 SUI?', '2 hours ago', 14),
    ('c1-r1', evt_1::text, 'c1', 'WhaleSui', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Jack', 'I''m dumping 5 VIPs if it hits 4 SUI. Hold your horses.', '1 hour ago', 3),
    ('c1-r2', evt_1::text, 'c1', 'SuiApe', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Ape', 'Bro the floor is already at 3.5, you''re dreaming', '45 mins ago', 8),
    ('c1-r2-r1', evt_1::text, 'c1-r2', 'WhaleSui', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Jack2', 'Oops, haven''t checked the board. Thanks for the alpha.', '30 mins ago', 2),
    ('c2', evt_1::text, NULL, 'NFT_Degen', 'https://api.dicebear.com/7.x/shapes/svg?seed=0x888', 'Is this event going to have a token airdrop for attendees? Rumors say yes.', '5 hours ago', 42);

END $$;