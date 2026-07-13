# ✅ Flyway Migration - Implementação Completa

## 🎯 Resumo da Implementação

Implementei com sucesso o **Flyway** para gerenciar as migrations de banco de dados do sistema de lanchonetes.

## 📦 O Que Foi Criado

### 1. **Dependências** (build.gradle)
```gradle
implementation 'org.flywaydb:flyway-core:10.10.0'
```

### 2. **Configuração** (application.properties)
```properties
spring.jpa.hibernate.ddl-auto=none
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
spring.flyway.locations=classpath:db/migration
spring.flyway.validate-on-migrate=true
spring.flyway.url=jdbc:sqlite:trabalhobackend.db
```

### 3. **FlywayConfig.java**
Classe de configuração que força a execução das migrations no startup:
```java
@Configuration
public class FlywayConfig {
    @Bean(initMethod = "migrate")
    public Flyway flyway(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .validateOnMigrate(true)
                .load();
    }
}
```

### 4. **Migrations SQL**

#### **V1__create_schema.sql**
Cria todo o schema do banco de dados:
- ✅ 14 tabelas (perfis, usuarios, unidades, categorias, produtos, estoque_produtos, pedidos, etc.)
- ✅ Relacionamentos com foreign keys
- ✅ Constraints e índices para performance
- ✅ Nomes em **plural** compatíveis com as entidades JPA

#### **V2__insert_initial_data.sql**
Insere dados iniciais:
- ✅ 5 perfis (ROLE_ADMIN, ROLE_GERENTE, ROLE_ATENDENTE, ROLE_COZINHA, ROLE_CLIENTE)
- ✅ Usuário admin (admin@lanchonete.com / admin123)
- ✅ 2 unidades (Lanchonete Centro e Shopping)
- ✅ 4 categorias (Lanches, Bebidas, Acompanhamentos, Sobremesas)
- ✅ 12 produtos variados
- ✅ Estoque inicial para ambas as unidades

## ✅ Resultados

### **Testes Realizados**

1. **Banco de Dados Criado**: ✅
   ```
   trabalhobackend.db: 164 KB
   ```

2. **Migrations Executadas**: ✅
   ```
   Successfully validated 2 migrations
   Current version of schema "main": 2
   Schema "main" is up to date
   ```

3. **API Funcionando**: ✅
   ```bash
   GET http://localhost:8080/api/unidades
   
   Response: 200 OK
   [
     {
       "id": 1,
       "nome": "Lanchonete Centro",
       "endereco": "Rua Principal, 123 - Centro",
       "telefone": "(11) 3333-1111",
       "ativa": true
     },
     {
       "id": 2,
       "nome": "Lanchonete Shopping",
       "endereco": "Av. Shopping, 456 - Loja 78",
       "telefone": "(11) 3333-2222",
       "ativa": true
     }
   ]
   ```

4. **Swagger Disponível**: ✅
   ```
   http://localhost:8080/swagger-ui/index.html
   ```

## 🚀 Como Usar

### **Primeira Execução**
```bash
# Build do projeto
.\gradlew build -x test

# Executar aplicação (Flyway roda automaticamente)
.\gradlew bootRun
```

O Flyway irá:
1. Detectar que o banco não existe
2. Executar V1__create_schema.sql (criar tabelas)
3. Executar V2__insert_initial_data.sql (inserir dados)
4. Registrar histórico na tabela `flyway_schema_history`

### **Execuções Subsequentes**
O Flyway verifica se as migrations já foram executadas e pula automaticamente.

## 🔑 Credenciais de Teste

```json
{
  "email": "admin@lanchonete.com",
  "senha": "admin123"
}
```

Este usuário tem perfil **ROLE_ADMIN** com acesso total.

## 📊 Estrutura de Dados Inserida

### Unidades
- **ID 1**: Lanchonete Centro (Rua Principal, 123)
- **ID 2**: Lanchonete Shopping (Av. Shopping, 456)

### Produtos (12 itens)
- **Lanches**: X-Burger (R$ 18,90), X-Bacon (R$ 22,90), X-Salada (R$ 20,90), X-Tudo (R$ 28,90)
- **Bebidas**: Refrigerante (R$ 5,00), Suco Natural (R$ 8,50), Água (R$ 3,50)
- **Acompanhamentos**: Batata P (R$ 8,90), Batata M (R$ 12,90), Nuggets (R$ 15,90)
- **Sobremesas**: Milkshake (R$ 12,90), Sorvete (R$ 9,90)

### Estoque
Todos os produtos têm estoque disponível em ambas as unidades com quantidades diferentes.

## 🎯 Benefícios da Solução

1. **Versionamento de BD**: Cada mudança no schema é versionada e rastreável
2. **Reprodutibilidade**: Qualquer desenvolvedor pode recriar o banco executando `.\gradlew bootRun`
3. **Automação**: Não precisa executar scripts SQL manualmente
4. **Auditoria**: Histórico completo em `flyway_schema_history`
5. **Segurança**: Migrations validadas antes de executar

## 📁 Arquivos Criados

```
src/main/resources/db/migration/
├── V1__create_schema.sql          (6.5 KB - Schema completo)
└── V2__insert_initial_data.sql    (8.2 KB - Dados iniciais)

src/main/java/.../config/
└── FlywayConfig.java               (Bean de configuração)
```

## ✨ Próximos Passos (Opcional)

Se precisar adicionar novas features:
1. Criar V3__nome_da_alteracao.sql
2. Executar `.\gradlew bootRun`
3. Flyway detecta e aplica automaticamente

**Exemplo**: V3__add_horario_funcionamento_to_unidades.sql

---

**Status**: ✅ **IMPLEMENTAÇÃO COMPLETA E FUNCIONAL**
