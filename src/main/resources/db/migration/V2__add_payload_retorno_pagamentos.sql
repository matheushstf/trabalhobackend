-- Armazena o payload da integração mock de pagamento
ALTER TABLE pagamentos ADD COLUMN payload_retorno TEXT;