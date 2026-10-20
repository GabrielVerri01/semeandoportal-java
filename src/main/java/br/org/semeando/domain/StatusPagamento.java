package br.org.semeando.domain;

/** Situação gravada da cobrança. VENCIDO também é derivado na exibição (Encargos.situacao). */
public enum StatusPagamento {
    ABERTO, PROCESSANDO, PAGO, VENCIDO, CANCELADO
}
