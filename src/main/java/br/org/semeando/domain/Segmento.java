package br.org.semeando.domain;

/** Segmento de ensino. Sempre DERIVADO da série (RN-03) — nunca informado à mão. */
public enum Segmento {
    EDUCACAO_INFANTIL("Educação Infantil"),
    ENSINO_FUNDAMENTAL("Ensino Fundamental");

    private final String rotulo;

    Segmento(String rotulo) {
        this.rotulo = rotulo;
    }

    public String rotulo() {
        return rotulo;
    }
}
