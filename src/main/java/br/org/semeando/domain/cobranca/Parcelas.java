package br.org.semeando.domain.cobranca;

import br.org.semeando.domain.ModalidadeCobranca;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

/** Parcelas, referências e numeração dos planos de cobrança (RN-04/RN-05/RN-09). */
public final class Parcelas {

    private static final String[] MESES = {
        "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
        "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro",
    };

    private Parcelas() {
    }

    /** @param primeiroMes 1–12 */
    public record Parametros(int anoLetivo, int quantidadeParcelas, int primeiroMes, int diaVencimento) {
    }

    /** Datas de vencimento das parcelas (mês e dia excedentes avançam, como no calendário). */
    public static List<LocalDate> vencimentos(Parametros p) {
        LocalDate inicioDoAno = LocalDate.of(p.anoLetivo(), 1, 1);
        return IntStream.range(0, p.quantidadeParcelas())
                .mapToObj(i -> inicioDoAno.plusMonths(p.primeiroMes() - 1L + i).plusDays(p.diaVencimento() - 1L))
                .toList();
    }

    /** "Mensalidade 03/11 — Abril/2026". */
    public static String referencia(ModalidadeCobranca modalidade, int parcela, int total, LocalDate vencimento) {
        return "%s %02d/%02d — %s/%d".formatted(
                modalidade.rotulo(), parcela, total, MESES[vencimento.getMonthValue() - 1], vencimento.getYear());
    }

    /** "CT-2027-0001" (contrato) ou "FS-2027-0001" (ficha de sócio). */
    public static String numeroDocumento(ModalidadeCobranca modalidade, int anoLetivo, int sequencial) {
        return "%s-%d-%04d".formatted(modalidade.prefixo(), anoLetivo, sequencial);
    }

    /**
     * RN-09: divergência = plano cujo valor de parcela difere da tabela oficial.
     * Impacto positivo = a escola deixa de receber; negativo = cobra a mais.
     */
    public static int impactoDivergencia(int valorPlano, int valorTabela, int parcelas) {
        return (valorTabela - valorPlano) * parcelas;
    }
}
