# Semeando Digital — versão Java

Porte para Java da gestão escolar do Centro Educacional Semeando. A versão de referência (Next.js)
está em `../portal-aluno`; o **`../portal-aluno/MODELO.md` continua sendo a fonte de verdade** das
regras de negócio (RN-01…RN-17) e o `../portal-aluno/CLAUDE.md` das regras invioláveis.

**Stack:** Java 21 · Spring Boot 4.1 · Spring Data JPA (Hibernate 7) · Flyway · PostgreSQL 16 ·
Spring Security · Thymeleaf · JUnit 5 + Testcontainers. Build com Maven Wrapper (`mvnw`, não precisa instalar Maven).

## Como rodar

Pré-requisitos: JDK 21 e Docker.

```bash
docker compose up -d                                   # PostgreSQL local na porta 5434
./mvnw spring-boot:run -Dspring-boot.run.profiles=seed # dados sintéticos (imprime os logins) e encerra
./mvnw spring-boot:run                                 # aplicação em http://localhost:8080
./mvnw test                                            # testes (sobem um PostgreSQL via Testcontainers)
```

No Windows (PowerShell/cmd), use `mvnw.cmd` no lugar de `./mvnw`.

## Arquitetura

Monólito modular, pacotes por agregado (MODELO.md §4.1):

```
br.org.semeando
├─ domain/        regras PURAS, sem Spring nem JPA (Serie, ModalidadeCobranca, Encargos, Parcelas,
│                 Boleto, TabelaOficial, Checklist…) — o que era lib/regras/* e lib/checklist.ts
├─ matricula/     Matricula, HistoricoMatricula, PlanoCobranca, Cobranca (+ MatriculaService, CobrancaService)
├─ aluno/         Aluno, PessoaAutorizada, Documento, FichaSaude (dado sensível)
├─ academico/     Professor, Turma, Disciplina, Aula, Horario, Nota, Frequencia
├─ financeiro/    TabelaPreco, Socio
├─ conta/ aviso/ auditoria/
├─ comum/         Ids (formato cuid), EnumPostgres
└─ seed/          SeedSintetico (perfis seed e test)
```

Como as regras invioláveis viram código:

- **Status, checklist, plano e parcelas só mudam no MatriculaService** — os métodos que alteram
  `Matricula`, `PlanoCobranca` e `Cobranca` são *package-private*; fora do pacote `matricula` o
  compilador não deixa chamá-los.
- **Valor nunca é digitado (RN-05)** — `PlanoCobranca` não tem setter de valor; o plano do sistema
  copia da `TabelaPreco`. Estado arbitrário só entra pelos métodos `importar*`, reservados à carga
  inicial (seed / migração dos contratos do Word).
- **FichaSaude** não é mapeada dentro de `Aluno`: só é lida sob demanda, pelo repositório próprio.
- **Dinheiro em centavos (`int`)**, datas em UTC.

## Banco de dados

O schema é **idêntico** ao da versão Next.js: as migrations do Prisma viraram `V1__semeando.sql` e
`V2__anexos.sql` (Flyway), com as mesmas tabelas, colunas camelCase entre aspas e enums nativos.
O Hibernate só valida (`ddl-auto=validate`). Num banco já criado pelo Prisma, o Flyway assume a
versão 2 sem reexecutar nada (`baseline-on-migrate`). Os ids seguem o formato cuid e as senhas são
BCrypt — compatíveis nos dois sentidos.

O seed é um porte fiel do `prisma/seed.ts` (mesma semente e mesma ordem de sorteios): gera a mesma
escola sintética — alunos, CPFs, parcelas, linhas digitáveis, notas e grade horária.

## Andamento da migração

| Fase | Conteúdo | Situação |
|---|---|---|
| 1 | Projeto, Flyway, entidades JPA, seed sintético | ✅ concluída |
| 2 | Domínio puro + testes portados de `tests/unit` | ⏳ |
| 3 | MatriculaService e CobrancaService + testes de integração | ⏳ |
| 4 | Spring Security e autorização por perfil | ⏳ |
| 5 | Telas da secretaria, da família e do professor (Thymeleaf) | ⏳ |
| 6 | README e MODELO.md §9 atualizados | ⏳ |
