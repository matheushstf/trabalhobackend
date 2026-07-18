CREATE TABLE promocoes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    titulo VARCHAR(100) NOT NULL,
    descricao TEXT,
    regra_aplicacao TEXT,
    percentual_desconto DECIMAL(5,2) NOT NULL,
    ativa BOOLEAN NOT NULL,
    data_inicio TIMESTAMP,
    data_fim TIMESTAMP,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE audit_logs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    evento VARCHAR(80) NOT NULL,
    entidade VARCHAR(80) NOT NULL,
    entidade_id INTEGER,
    usuario_email VARCHAR(120),
    detalhes TEXT,
    data_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO promocoes (titulo, descricao, regra_aplicacao, percentual_desconto, ativa, data_inicio, data_fim)
VALUES (
    'Combo da Semana',
    'Desconto simples para teste da API.',
    'Aplicar 10% no valor total enquanto estiver ativa.',
    10.00,
    1,
    CURRENT_TIMESTAMP,
    NULL
);