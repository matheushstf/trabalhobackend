-- Compatibiliza a tabela unidades com a entidade Unidade já usada pela aplicação
ALTER TABLE unidades ADD COLUMN endereco VARCHAR(255);
ALTER TABLE unidades ADD COLUMN telefone VARCHAR(20);
ALTER TABLE unidades ADD COLUMN ativa BOOLEAN NOT NULL DEFAULT 1;
ALTER TABLE unidades ADD COLUMN data_criacao TIMESTAMP;
ALTER TABLE unidades ADD COLUMN data_atualizacao TIMESTAMP;

UPDATE unidades
SET
    endereco = COALESCE(endereco, 'Endereco nao informado'),
    telefone = COALESCE(telefone, ''),
    ativa = COALESCE(ativa, 1),
    data_criacao = COALESCE(data_criacao, CURRENT_TIMESTAMP),
    data_atualizacao = COALESCE(data_atualizacao, CURRENT_TIMESTAMP);