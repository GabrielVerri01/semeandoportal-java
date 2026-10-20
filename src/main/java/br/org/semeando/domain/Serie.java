package br.org.semeando.domain;

import java.util.Optional;

/**
 * Séries atendidas pelo Centro Educacional Semeando (Maternal I ao 5º ano), na ordem da renovação.
 * O segmento é DERIVADO da série — nunca informado à mão (RN-03).
 */
public enum Serie {
    MATERNAL_I("Maternal I", Segmento.EDUCACAO_INFANTIL),
    MATERNAL_II("Maternal II", Segmento.EDUCACAO_INFANTIL),
    PRE_I("Pré I", Segmento.EDUCACAO_INFANTIL),
    PRE_II("Pré II", Segmento.EDUCACAO_INFANTIL),
    ANO_1("1º Ano", Segmento.ENSINO_FUNDAMENTAL),
    ANO_2("2º Ano", Segmento.ENSINO_FUNDAMENTAL),
    ANO_3("3º Ano", Segmento.ENSINO_FUNDAMENTAL),
    ANO_4("4º Ano", Segmento.ENSINO_FUNDAMENTAL),
    ANO_5("5º Ano", Segmento.ENSINO_FUNDAMENTAL);

    private final String nome;
    private final Segmento segmento;

    Serie(String nome, Segmento segmento) {
        this.nome = nome;
        this.segmento = segmento;
    }

    public String nome() {
        return nome;
    }

    public Segmento segmento() {
        return segmento;
    }

    public boolean infantil() {
        return segmento == Segmento.EDUCACAO_INFANTIL;
    }

    /** Série do ano letivo seguinte na renovação (vazio = conclui o 5º ano e sai). */
    public Optional<Serie> proxima() {
        int i = ordinal() + 1;
        return i < values().length ? Optional.of(values()[i]) : Optional.empty();
    }

    /** Descrição completa: "Ensino Fundamental — 3º Ano". */
    public String descricao() {
        return segmento.rotulo() + " — " + nome;
    }

    /** RN-04: a modalidade de cobrança decorre do segmento (Infantil → taxa de associado; Fundamental → mensalidade). */
    public ModalidadeCobranca modalidade() {
        return infantil() ? ModalidadeCobranca.TAXA_ASSOCIADO : ModalidadeCobranca.MENSALIDADE;
    }

    /** Educação Infantil é avaliada por parecer descritivo (BNCC), sem nota numérica. */
    public boolean temNotaNumerica() {
        return segmento == Segmento.ENSINO_FUNDAMENTAL;
    }
}
