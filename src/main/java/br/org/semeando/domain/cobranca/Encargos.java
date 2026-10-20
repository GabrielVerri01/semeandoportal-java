package br.org.semeando.domain.cobranca;

import br.org.semeando.domain.StatusPagamento;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Valor devido de uma parcela (RN-10 e RN-11), em centavos:
 * <ul>
 *   <li>até o vencimento: valor com desconto de pontualidade, se a tabela tiver (Anexo II: 10%);</li>
 *   <li>depois do vencimento: valor cheio + multa de 2% + juros de mora de 0,033% ao dia
 *       (juros simples sobre o valor cheio, contados a partir do dia seguinte ao vencimento).</li>
 * </ul>
 */
public final class Encargos {

    public static final double MULTA_ATRASO = 0.02;
    public static final double JUROS_AO_DIA = 0.00033;

    private Encargos() {
    }

    public record ValorDevido(
            long diasAtraso,
            int valorCentavos,
            int descontoCentavos,
            int multaCentavos,
            int jurosCentavos,
            int totalCentavos) {
    }

    public static long diasDeAtraso(LocalDate vencimento, LocalDate referencia) {
        return Math.max(0, ChronoUnit.DAYS.between(vencimento, referencia));
    }

    /** @param valorComDescontoCentavos null = sem desconto de pontualidade */
    public static ValorDevido valorDevido(int valorCentavos, Integer valorComDescontoCentavos, LocalDate vencimento,
            LocalDate referencia) {
        long diasAtraso = diasDeAtraso(vencimento, referencia);
        if (diasAtraso == 0) {
            int comDesconto = valorComDescontoCentavos != null ? valorComDescontoCentavos : valorCentavos;
            return new ValorDevido(0, valorCentavos, valorCentavos - comDesconto, 0, 0, comDesconto);
        }
        int multa = (int) Math.round(valorCentavos * MULTA_ATRASO);
        int juros = (int) Math.round(valorCentavos * JUROS_AO_DIA * diasAtraso);
        return new ValorDevido(diasAtraso, valorCentavos, 0, multa, juros, valorCentavos + multa + juros);
    }

    /** Situação exibida: ABERTO com vencimento passado é VENCIDO (sem precisar de rotina noturna). */
    public static StatusPagamento situacao(StatusPagamento status, LocalDate vencimento, LocalDate referencia) {
        return status == StatusPagamento.ABERTO && diasDeAtraso(vencimento, referencia) > 0
                ? StatusPagamento.VENCIDO
                : status;
    }
}
