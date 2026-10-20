package br.org.semeando.domain.cobranca;

import br.org.semeando.domain.ModalidadeCobranca;
import br.org.semeando.domain.Serie;
import java.util.Arrays;
import java.util.List;

/**
 * Tabela oficial — Anexo II (valores de referência, turnos matutino e vespertino iguais).
 * Usada só para POPULAR a TabelaPreco no banco (seed/implantação); em execução,
 * o sistema lê sempre do banco, onde a secretaria mantém a tabela de cada ano letivo.
 *
 * <pre>
 *   Educação Infantil ........ taxa de associado R$ 125,00
 *   Fundamental 1º ano ....... R$ 310,00 (R$ 279,00 até o vencimento — 10%)
 *   Fundamental 2º ao 5º ano . R$ 375,00 (R$ 337,00 até o vencimento — 10%)
 *   Jiu-Jitsu / Ballet ....... R$ 50,00 (atividade extra — fora do escopo desta etapa)
 * </pre>
 *
 * ⚠ HIPÓTESES (não estão nos anexos): 11 parcelas, de fevereiro a dezembro,
 * vencimento no dia 10; taxa de associado mensal, por criança, sem desconto.
 */
public final class TabelaOficial {

    public static final int QUANTIDADE_PARCELAS = 11;
    public static final int PRIMEIRO_MES = 2;
    public static final int DIA_VENCIMENTO = 10;

    private TabelaOficial() {
    }

    public record Linha(
            Serie serie,
            ModalidadeCobranca modalidade,
            int valorParcelaCentavos,
            Integer valorComDescontoCentavos,
            int quantidadeParcelas,
            int primeiroMes,
            int diaVencimento) {
    }

    public static List<Linha> anexoII() {
        return Arrays.stream(Serie.values()).map(TabelaOficial::linha).toList();
    }

    private static Linha linha(Serie serie) {
        ModalidadeCobranca modalidade = serie.modalidade();
        int valor;
        Integer comDesconto;
        if (modalidade == ModalidadeCobranca.TAXA_ASSOCIADO) {
            valor = 12500;
            comDesconto = null;
        } else if (serie == Serie.ANO_1) {
            valor = 31000;
            comDesconto = 27900;
        } else {
            valor = 37500;
            comDesconto = 33700;
        }
        return new Linha(serie, modalidade, valor, comDesconto, QUANTIDADE_PARCELAS, PRIMEIRO_MES, DIA_VENCIMENTO);
    }
}
