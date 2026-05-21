
-- ═══════════════════════════════════════════════════════════════════
-- SEED — dados iniciais para desenvolvimento
-- ═══════════════════════════════════════════════════════════════════

INSERT INTO terminal (nome, codigo, cidade, estado) VALUES
    ('Terminal Barra Funda', 'TBF-01', 'São Paulo', 'SP'),
    ('Terminal Bandeira',    'TBD-01', 'São Paulo', 'SP'),
    ('Terminal Santana',     'TSN-01', 'São Paulo', 'SP');

INSERT INTO onibus (prefixo, tipo, padrao_motor, combustivel, tem_ar_condicionado, km_anuais, ano_fabricacao, terminal_id) VALUES
    ('SP-7001', 'PADRON',         'P7_EURO_V',   'DIESEL_B15',  TRUE,  72300, 2020, 1),
    ('SP-7002', 'PADRON',         'P5_EURO_III', 'DIESEL_B15',  FALSE, 72300, 2015, 1),
    ('SP-7003', 'ARTICULADO',     'P7_EURO_V',   'DIESEL_B15',  TRUE,  63600, 2021, 1),
    ('SP-7004', 'BASICO',         'P5_EURO_III', 'DIESEL_B15',  FALSE, 72200, 2014, 1),
    ('SP-7005', 'TROLEBUS',       'ELETRICO',    'ELETRICIDADE', FALSE, 51800, 2019, 1),
    ('SP-8001', 'MIDIBUS',        'P7_EURO_V',   'DIESEL_B15',  TRUE,  70400, 2022, 2),
    ('SP-8002', 'PADRON_15M',     'P5_EURO_III', 'DIESEL_B15',  FALSE, 61800, 2016, 2),
    ('SP-8003', 'ARTICULADO_23M', 'P7_EURO_V',   'DIESEL_B15',  TRUE,  64700, 2021, 2),
    ('SP-8004', 'BIARTICULADO',   'P5_EURO_III', 'DIESEL_B15',  FALSE, 36600, 2013, 2),
    ('SP-9001', 'MINIBUS',        'P7_EURO_V',   'DIESEL_B15',  FALSE, 70100, 2023, 3),
    ('SP-9002', 'BASICO',         'P8_EURO_VI',  'DIESEL_B15',  TRUE,  72200, 2023, 3),
    ('SP-9003', 'PADRON',         'P7_EURO_V',   'DIESEL_B15',  TRUE,  72300, 2022, 3);
	