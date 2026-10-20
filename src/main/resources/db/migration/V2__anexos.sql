-- Copiado de portal-aluno/prisma/migrations/20261013152040_anexos.
-- CreateEnum
CREATE TYPE "Sexo" AS ENUM ('FEMININO', 'MASCULINO');

-- CreateEnum
CREATE TYPE "TipoSocio" AS ENUM ('FUNDADOR', 'EFETIVO', 'MANTENEDOR_CONTRIBUINTE', 'MANTENEDOR_APOIADOR', 'MANTENEDOR_VOLUNTARIO');

-- AlterTable
ALTER TABLE "Aluno" ADD COLUMN     "autorizaUsoImagem" BOOLEAN NOT NULL DEFAULT false,
ADD COLUMN     "bairro" TEXT,
ADD COLUMN     "filiacao1" TEXT,
ADD COLUMN     "filiacao2" TEXT,
ADD COLUMN     "origemEscolaParticular" BOOLEAN NOT NULL DEFAULT false,
ADD COLUMN     "sexo" "Sexo";

-- AlterTable
ALTER TABLE "Cobranca" ADD COLUMN     "valorComDescontoCentavos" INTEGER;

-- AlterTable
ALTER TABLE "PlanoCobranca" ADD COLUMN     "socioId" TEXT,
ADD COLUMN     "valorComDescontoCentavos" INTEGER;

-- AlterTable
ALTER TABLE "TabelaPreco" ADD COLUMN     "valorComDescontoCentavos" INTEGER;

-- CreateTable
CREATE TABLE "FichaSaude" (
    "id" TEXT NOT NULL,
    "alunoId" TEXT NOT NULL,
    "deficiencia" TEXT,
    "dificuldadeFala" BOOLEAN NOT NULL DEFAULT false,
    "dificuldadeVisao" BOOLEAN NOT NULL DEFAULT false,
    "dificuldadeLocomocao" BOOLEAN NOT NULL DEFAULT false,
    "acompanhamentos" TEXT[],
    "acompanhamentoOutro" TEXT,
    "restricaoAlimentar" TEXT,
    "alergia" TEXT,
    "observacoes" TEXT,
    "atualizadoEm" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "FichaSaude_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "Socio" (
    "id" TEXT NOT NULL,
    "nome" TEXT NOT NULL,
    "cpf" TEXT NOT NULL,
    "dataNascimento" DATE,
    "estadoCivil" TEXT,
    "profissao" TEXT,
    "endereco" TEXT,
    "bairro" TEXT,
    "contato" TEXT,
    "tipo" "TipoSocio" NOT NULL DEFAULT 'EFETIVO',
    "filhosCriancas" INTEGER NOT NULL DEFAULT 0,
    "filhosAdolescentes" INTEGER NOT NULL DEFAULT 0,
    "observacoes" TEXT,
    "dataAssociacao" DATE NOT NULL,
    "dataAfastamento" DATE,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "Socio_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE UNIQUE INDEX "FichaSaude_alunoId_key" ON "FichaSaude"("alunoId");

-- CreateIndex
CREATE UNIQUE INDEX "Socio_cpf_key" ON "Socio"("cpf");

-- AddForeignKey
ALTER TABLE "FichaSaude" ADD CONSTRAINT "FichaSaude_alunoId_fkey" FOREIGN KEY ("alunoId") REFERENCES "Aluno"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "PlanoCobranca" ADD CONSTRAINT "PlanoCobranca_socioId_fkey" FOREIGN KEY ("socioId") REFERENCES "Socio"("id") ON DELETE SET NULL ON UPDATE CASCADE;
