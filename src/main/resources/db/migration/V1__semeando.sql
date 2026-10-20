-- Copiado de portal-aluno/prisma/migrations/20261013151811_semeando (schema idêntico ao da versão Next.js).
-- CreateEnum
CREATE TYPE "Role" AS ENUM ('ALUNO', 'PROFESSOR', 'SECRETARIA');

-- CreateEnum
CREATE TYPE "StatusMatricula" AS ENUM ('PENDENTE', 'EM_ANALISE', 'ATIVA', 'CANCELADA');

-- CreateEnum
CREATE TYPE "Turno" AS ENUM ('MANHA', 'TARDE', 'NOITE');

-- CreateEnum
CREATE TYPE "Segmento" AS ENUM ('EDUCACAO_INFANTIL', 'ENSINO_FUNDAMENTAL');

-- CreateEnum
CREATE TYPE "Serie" AS ENUM ('MATERNAL_I', 'MATERNAL_II', 'PRE_I', 'PRE_II', 'ANO_1', 'ANO_2', 'ANO_3', 'ANO_4', 'ANO_5');

-- CreateEnum
CREATE TYPE "ModalidadeCobranca" AS ENUM ('TAXA_ASSOCIADO', 'MENSALIDADE');

-- CreateEnum
CREATE TYPE "OrigemPlano" AS ENUM ('SISTEMA', 'LEGADO');

-- CreateEnum
CREATE TYPE "StatusPlano" AS ENUM ('ATIVO', 'ENCERRADO', 'CANCELADO');

-- CreateEnum
CREATE TYPE "MetodoPagamento" AS ENUM ('PIX', 'CARTAO', 'BOLETO', 'DINHEIRO');

-- CreateEnum
CREATE TYPE "StatusPagamento" AS ENUM ('ABERTO', 'PROCESSANDO', 'PAGO', 'VENCIDO', 'CANCELADO');

-- CreateEnum
CREATE TYPE "TipoCobranca" AS ENUM ('TAXA_ASSOCIADO', 'MENSALIDADE', 'OUTRO');

-- CreateTable
CREATE TABLE "User" (
    "id" TEXT NOT NULL,
    "email" TEXT,
    "senhaHash" TEXT NOT NULL,
    "senhaProvisoria" BOOLEAN NOT NULL DEFAULT false,
    "role" "Role" NOT NULL,
    "nome" TEXT NOT NULL,
    "ativo" BOOLEAN NOT NULL DEFAULT true,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "User_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "Aluno" (
    "id" TEXT NOT NULL,
    "userId" TEXT NOT NULL,
    "numeroMatricula" TEXT NOT NULL,
    "cpf" TEXT,
    "dataNascimento" DATE NOT NULL,
    "telefone" TEXT,
    "cep" TEXT,
    "endereco" TEXT,
    "responsavelNome" TEXT,
    "responsavelParentesco" TEXT,
    "responsavelCpf" TEXT,
    "responsavelTelefone" TEXT,
    "responsavelEmail" TEXT,
    "hospitalEmergencia" TEXT,
    "consentimentoLgpdEm" TIMESTAMP(3),
    "consentimentoVersao" TEXT,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "Aluno_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "PessoaAutorizada" (
    "id" TEXT NOT NULL,
    "alunoId" TEXT NOT NULL,
    "nome" TEXT NOT NULL,
    "parentesco" TEXT NOT NULL,
    "documento" TEXT NOT NULL,
    "telefone" TEXT NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "PessoaAutorizada_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "Professor" (
    "id" TEXT NOT NULL,
    "userId" TEXT NOT NULL,
    "formacao" TEXT,

    CONSTRAINT "Professor_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "Turma" (
    "id" TEXT NOT NULL,
    "nome" TEXT NOT NULL,
    "serie" "Serie" NOT NULL,
    "turno" "Turno" NOT NULL,
    "periodo" TEXT NOT NULL,
    "vagas" INTEGER NOT NULL,

    CONSTRAINT "Turma_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "Disciplina" (
    "id" TEXT NOT NULL,
    "nome" TEXT NOT NULL,

    CONSTRAINT "Disciplina_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "Aula" (
    "id" TEXT NOT NULL,
    "turmaId" TEXT NOT NULL,
    "disciplinaId" TEXT NOT NULL,
    "professorId" TEXT NOT NULL,

    CONSTRAINT "Aula_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "Horario" (
    "id" TEXT NOT NULL,
    "aulaId" TEXT NOT NULL,
    "diaSemana" INTEGER NOT NULL,
    "horaInicio" TEXT NOT NULL,
    "horaFim" TEXT NOT NULL,
    "sala" TEXT,

    CONSTRAINT "Horario_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "Matricula" (
    "id" TEXT NOT NULL,
    "alunoId" TEXT NOT NULL,
    "turmaId" TEXT NOT NULL,
    "periodo" TEXT NOT NULL,
    "status" "StatusMatricula" NOT NULL DEFAULT 'PENDENTE',
    "checklist" JSONB NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "Matricula_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "HistoricoMatricula" (
    "id" TEXT NOT NULL,
    "matriculaId" TEXT NOT NULL,
    "de" "StatusMatricula",
    "para" "StatusMatricula" NOT NULL,
    "descricao" TEXT NOT NULL,
    "autorId" TEXT,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "HistoricoMatricula_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "TabelaPreco" (
    "id" TEXT NOT NULL,
    "anoLetivo" INTEGER NOT NULL,
    "serie" "Serie" NOT NULL,
    "modalidade" "ModalidadeCobranca" NOT NULL,
    "valorParcelaCentavos" INTEGER NOT NULL,
    "quantidadeParcelas" INTEGER NOT NULL,
    "primeiroMes" INTEGER NOT NULL,
    "diaVencimento" INTEGER NOT NULL,
    "atualizadoEm" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "TabelaPreco_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "PlanoCobranca" (
    "id" TEXT NOT NULL,
    "matriculaId" TEXT NOT NULL,
    "modalidade" "ModalidadeCobranca" NOT NULL,
    "numeroDocumento" TEXT NOT NULL,
    "tabelaPrecoId" TEXT,
    "valorParcelaCentavos" INTEGER NOT NULL,
    "quantidadeParcelas" INTEGER NOT NULL,
    "origem" "OrigemPlano" NOT NULL DEFAULT 'SISTEMA',
    "status" "StatusPlano" NOT NULL DEFAULT 'ATIVO',
    "assinadoEm" TIMESTAMP(3),
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "PlanoCobranca_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "Cobranca" (
    "id" TEXT NOT NULL,
    "alunoId" TEXT NOT NULL,
    "matriculaId" TEXT,
    "planoId" TEXT,
    "parcela" INTEGER,
    "tipo" "TipoCobranca" NOT NULL,
    "referencia" TEXT NOT NULL,
    "valorCentavos" INTEGER NOT NULL,
    "vencimento" DATE NOT NULL,
    "status" "StatusPagamento" NOT NULL DEFAULT 'ABERTO',
    "metodo" "MetodoPagamento",
    "gatewayId" TEXT,
    "pixCopiaCola" TEXT,
    "boletoLinha" TEXT,
    "boletoPdfUrl" TEXT,
    "pagoEm" TIMESTAMP(3),
    "valorPagoCentavos" INTEGER,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "Cobranca_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "Nota" (
    "id" TEXT NOT NULL,
    "alunoId" TEXT NOT NULL,
    "aulaId" TEXT NOT NULL,
    "bimestre" INTEGER NOT NULL,
    "valor" DECIMAL(4,2) NOT NULL,

    CONSTRAINT "Nota_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "Frequencia" (
    "id" TEXT NOT NULL,
    "alunoId" TEXT NOT NULL,
    "aulaId" TEXT NOT NULL,
    "data" DATE NOT NULL,
    "presente" BOOLEAN NOT NULL,

    CONSTRAINT "Frequencia_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "Documento" (
    "id" TEXT NOT NULL,
    "alunoId" TEXT NOT NULL,
    "tipo" TEXT NOT NULL,
    "url" TEXT NOT NULL,
    "nomeArquivo" TEXT,
    "conferido" BOOLEAN NOT NULL DEFAULT false,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "Documento_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "Aviso" (
    "id" TEXT NOT NULL,
    "titulo" TEXT NOT NULL,
    "texto" TEXT NOT NULL,
    "publicoRole" "Role",
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "Aviso_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "Auditoria" (
    "id" TEXT NOT NULL,
    "usuarioId" TEXT,
    "usuarioNome" TEXT,
    "acao" TEXT NOT NULL,
    "entidade" TEXT NOT NULL,
    "entidadeId" TEXT,
    "detalhes" JSONB,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "Auditoria_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE UNIQUE INDEX "User_email_key" ON "User"("email");

-- CreateIndex
CREATE UNIQUE INDEX "Aluno_userId_key" ON "Aluno"("userId");

-- CreateIndex
CREATE UNIQUE INDEX "Aluno_numeroMatricula_key" ON "Aluno"("numeroMatricula");

-- CreateIndex
CREATE UNIQUE INDEX "Aluno_cpf_key" ON "Aluno"("cpf");

-- CreateIndex
CREATE INDEX "PessoaAutorizada_alunoId_idx" ON "PessoaAutorizada"("alunoId");

-- CreateIndex
CREATE UNIQUE INDEX "Professor_userId_key" ON "Professor"("userId");

-- CreateIndex
CREATE UNIQUE INDEX "Turma_nome_periodo_key" ON "Turma"("nome", "periodo");

-- CreateIndex
CREATE UNIQUE INDEX "Disciplina_nome_key" ON "Disciplina"("nome");

-- CreateIndex
CREATE INDEX "Aula_professorId_idx" ON "Aula"("professorId");

-- CreateIndex
CREATE UNIQUE INDEX "Aula_turmaId_disciplinaId_key" ON "Aula"("turmaId", "disciplinaId");

-- CreateIndex
CREATE INDEX "Horario_aulaId_idx" ON "Horario"("aulaId");

-- CreateIndex
CREATE INDEX "Matricula_turmaId_periodo_idx" ON "Matricula"("turmaId", "periodo");

-- CreateIndex
CREATE INDEX "Matricula_status_idx" ON "Matricula"("status");

-- CreateIndex
CREATE UNIQUE INDEX "Matricula_alunoId_periodo_key" ON "Matricula"("alunoId", "periodo");

-- CreateIndex
CREATE INDEX "HistoricoMatricula_matriculaId_idx" ON "HistoricoMatricula"("matriculaId");

-- CreateIndex
CREATE UNIQUE INDEX "TabelaPreco_anoLetivo_serie_key" ON "TabelaPreco"("anoLetivo", "serie");

-- CreateIndex
CREATE UNIQUE INDEX "PlanoCobranca_matriculaId_key" ON "PlanoCobranca"("matriculaId");

-- CreateIndex
CREATE UNIQUE INDEX "PlanoCobranca_numeroDocumento_key" ON "PlanoCobranca"("numeroDocumento");

-- CreateIndex
CREATE UNIQUE INDEX "Cobranca_gatewayId_key" ON "Cobranca"("gatewayId");

-- CreateIndex
CREATE INDEX "Cobranca_alunoId_idx" ON "Cobranca"("alunoId");

-- CreateIndex
CREATE INDEX "Cobranca_status_vencimento_idx" ON "Cobranca"("status", "vencimento");

-- CreateIndex
CREATE UNIQUE INDEX "Cobranca_planoId_parcela_key" ON "Cobranca"("planoId", "parcela");

-- CreateIndex
CREATE INDEX "Nota_aulaId_idx" ON "Nota"("aulaId");

-- CreateIndex
CREATE UNIQUE INDEX "Nota_alunoId_aulaId_bimestre_key" ON "Nota"("alunoId", "aulaId", "bimestre");

-- CreateIndex
CREATE INDEX "Frequencia_aulaId_data_idx" ON "Frequencia"("aulaId", "data");

-- CreateIndex
CREATE UNIQUE INDEX "Frequencia_alunoId_aulaId_data_key" ON "Frequencia"("alunoId", "aulaId", "data");

-- CreateIndex
CREATE INDEX "Documento_alunoId_idx" ON "Documento"("alunoId");

-- CreateIndex
CREATE INDEX "Auditoria_entidade_entidadeId_idx" ON "Auditoria"("entidade", "entidadeId");

-- CreateIndex
CREATE INDEX "Auditoria_createdAt_idx" ON "Auditoria"("createdAt");

-- AddForeignKey
ALTER TABLE "Aluno" ADD CONSTRAINT "Aluno_userId_fkey" FOREIGN KEY ("userId") REFERENCES "User"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "PessoaAutorizada" ADD CONSTRAINT "PessoaAutorizada_alunoId_fkey" FOREIGN KEY ("alunoId") REFERENCES "Aluno"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Professor" ADD CONSTRAINT "Professor_userId_fkey" FOREIGN KEY ("userId") REFERENCES "User"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Aula" ADD CONSTRAINT "Aula_turmaId_fkey" FOREIGN KEY ("turmaId") REFERENCES "Turma"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Aula" ADD CONSTRAINT "Aula_disciplinaId_fkey" FOREIGN KEY ("disciplinaId") REFERENCES "Disciplina"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Aula" ADD CONSTRAINT "Aula_professorId_fkey" FOREIGN KEY ("professorId") REFERENCES "Professor"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Horario" ADD CONSTRAINT "Horario_aulaId_fkey" FOREIGN KEY ("aulaId") REFERENCES "Aula"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Matricula" ADD CONSTRAINT "Matricula_alunoId_fkey" FOREIGN KEY ("alunoId") REFERENCES "Aluno"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Matricula" ADD CONSTRAINT "Matricula_turmaId_fkey" FOREIGN KEY ("turmaId") REFERENCES "Turma"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "HistoricoMatricula" ADD CONSTRAINT "HistoricoMatricula_matriculaId_fkey" FOREIGN KEY ("matriculaId") REFERENCES "Matricula"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "PlanoCobranca" ADD CONSTRAINT "PlanoCobranca_matriculaId_fkey" FOREIGN KEY ("matriculaId") REFERENCES "Matricula"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "PlanoCobranca" ADD CONSTRAINT "PlanoCobranca_tabelaPrecoId_fkey" FOREIGN KEY ("tabelaPrecoId") REFERENCES "TabelaPreco"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Cobranca" ADD CONSTRAINT "Cobranca_alunoId_fkey" FOREIGN KEY ("alunoId") REFERENCES "Aluno"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Cobranca" ADD CONSTRAINT "Cobranca_matriculaId_fkey" FOREIGN KEY ("matriculaId") REFERENCES "Matricula"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Cobranca" ADD CONSTRAINT "Cobranca_planoId_fkey" FOREIGN KEY ("planoId") REFERENCES "PlanoCobranca"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Nota" ADD CONSTRAINT "Nota_alunoId_fkey" FOREIGN KEY ("alunoId") REFERENCES "Aluno"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Nota" ADD CONSTRAINT "Nota_aulaId_fkey" FOREIGN KEY ("aulaId") REFERENCES "Aula"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Frequencia" ADD CONSTRAINT "Frequencia_alunoId_fkey" FOREIGN KEY ("alunoId") REFERENCES "Aluno"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Frequencia" ADD CONSTRAINT "Frequencia_aulaId_fkey" FOREIGN KEY ("aulaId") REFERENCES "Aula"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Documento" ADD CONSTRAINT "Documento_alunoId_fkey" FOREIGN KEY ("alunoId") REFERENCES "Aluno"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
