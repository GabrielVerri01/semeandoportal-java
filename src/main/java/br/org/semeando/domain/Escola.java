package br.org.semeando.domain;

/**
 * Identidade institucional — FONTE ÚNICA (RN-01 do MODELO.md).
 * Todo documento, boleto, contrato e tela lê daqui; nada é digitado de novo.
 *
 * DADOS SINTÉTICOS: CNPJ, endereço e telefone são fictícios por regra do projeto
 * (proibido dado real no repositório). Em produção, vêm da configuração da instituição.
 */
public final class Escola {

    public static final String PRODUTO = "Semeando Digital";

    /** Nome pelo qual a escola é conhecida (nome fantasia). */
    public static final String NOME = "Centro Educacional Semeando";
    /** Pessoa jurídica mantenedora (titular do CNPJ, credora dos contratos e das taxas). */
    public static final String RAZAO_SOCIAL = "Instituto Educacional Semeando";
    public static final String CNPJ = "12.345.678/0001-95"; // fictício (DV válido)
    public static final String SIGLA = "SE";
    public static final String TELEFONE = "(98) 3000-0000"; // fictício
    public static final String ENDERECO = "Rua das Sementes, 100 — Pontal da Ilha, São Luís/MA"; // fictício
    public static final String CIDADE = "São Luís";
    public static final String DOMINIO_EMAIL = "semeando.edu.br"; // fictício

    private Escola() {
    }
}
