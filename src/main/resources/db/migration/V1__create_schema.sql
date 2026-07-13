-- V1: Schema Simplificado - Sistema de Lanchonete
-- 8 tabelas essenciais conforme requisitos mínimos

-- 1. Usuários (com role ENUM)
CREATE TABLE usuarios (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_CLIENTE',
    consentimento_lgpd BOOLEAN NOT NULL DEFAULT 0,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. Unidades
CREATE TABLE unidades (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome VARCHAR(100) NOT NULL
);

-- 3. Produtos (categoria como String)
CREATE TABLE produtos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome VARCHAR(100) NOT NULL,
    preco DECIMAL(10,2) NOT NULL,
    categoria VARCHAR(50),
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 4. Estoque por unidade
CREATE TABLE estoque_produtos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    produto_id INTEGER NOT NULL,
    unidade_id INTEGER NOT NULL,
    quantidade_disponivel INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (produto_id) REFERENCES produtos(id),
    FOREIGN KEY (unidade_id) REFERENCES unidades(id),
    UNIQUE (produto_id, unidade_id)
);

-- 5. Pedidos (com multicanalidade obrigatória)
CREATE TABLE pedidos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    unidade_id INTEGER NOT NULL,
    usuario_id INTEGER NOT NULL,
    canal_pedido VARCHAR(20) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'AGUARDANDO_PAGAMENTO',
    valor_total DECIMAL(10,2) NOT NULL DEFAULT 0,
    data_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (unidade_id) REFERENCES unidades(id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- 6. Itens de Pedido
CREATE TABLE itens_pedido (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    pedido_id INTEGER NOT NULL,
    produto_id INTEGER NOT NULL,
    quantidade INTEGER NOT NULL,
    preco_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id),
    FOREIGN KEY (produto_id) REFERENCES produtos(id)
);

-- 7. Pagamentos (sem transacaoId)
CREATE TABLE pagamentos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    pedido_id INTEGER NOT NULL UNIQUE,
    valor DECIMAL(10,2) NOT NULL,
    metodo_pagamento VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id)
);

-- 8. Programa de Fidelidade (apenas saldo atual)
CREATE TABLE programa_fidelidade (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    usuario_id INTEGER NOT NULL UNIQUE,
    saldo INTEGER NOT NULL DEFAULT 0,
    consentimento BOOLEAN NOT NULL DEFAULT 0,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- Índices para performance
CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_usuarios_role ON usuarios(role);
CREATE INDEX idx_produtos_categoria ON produtos(categoria);
CREATE INDEX idx_pedidos_canal ON pedidos(canal_pedido);
CREATE INDEX idx_pedidos_status ON pedidos(status);
CREATE INDEX idx_pedidos_usuario ON pedidos(usuario_id);
CREATE INDEX idx_pedidos_unidade ON pedidos(unidade_id);
CREATE INDEX idx_estoque_produto_unidade ON estoque_produtos(produto_id, unidade_id);
CREATE INDEX idx_itens_pedido ON itens_pedido(pedido_id);
CREATE INDEX idx_pagamentos_pedido ON pagamentos(pedido_id);
CREATE INDEX idx_fidelidade_usuario ON programa_fidelidade(usuario_id);

-- Dados iniciais básicos
INSERT INTO unidades (nome) VALUES 
    ('Unidade Centro'),
    ('Unidade Shopping');

INSERT INTO produtos (nome, preco, categoria) VALUES 
    ('X-Burguer', 15.00, 'Lanches'),
    ('X-Bacon', 18.00, 'Lanches'),
    ('X-Tudo', 22.00, 'Lanches'),
    ('Coca-Cola', 5.00, 'Bebidas'),
    ('Suco Natural', 7.00, 'Bebidas'),
    ('Batata Frita', 10.00, 'Acompanhamentos');

-- Estoque inicial para ambas as unidades
INSERT INTO estoque_produtos (produto_id, unidade_id, quantidade_disponivel)
SELECT p.id, u.id, 100
FROM produtos p
CROSS JOIN unidades u;
