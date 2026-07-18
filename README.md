# Sistema de Lanchonete - Backend API 
 Sistema de gerenciamento de lanchonetes.
## Tecnologias

- **Java 17**
- **Spring Boot 4.1.0**
- **Flyway** para migrations de banco de dados
- **Swagger/OpenAPI** para documentação da API
- **BCrypt** para hash de senhas
- **Lombok** para redução de boilerplate

## Arquitetura

- **API / Controllers**: expõe os endpoints HTTP, recebe requests e devolve responses. Ex.: `controller/`.
- **Application / Services**: concentra os casos de uso e a orquestração do fluxo. Ex.: `service/`.
- **Domain / Model**: contém as entidades e parte das regras do domínio. Ex.: `model/`, `enums/`, `dto/`.
- **Infrastructure**: cuida da persistência, integrações e detalhes técnicos. Ex.: `repository/`, `config/`, `security/`, `db/migration/`.

Essa separação foi mantida de forma simples usando um estilo próximo de MVC com serviços de aplicação e infraestrutura bem delimitada.