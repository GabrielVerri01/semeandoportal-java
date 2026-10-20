package br.org.semeando.domain.cobranca;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Boleto bancário (padrão Febraban): código de barras de 44 dígitos e linha
 * digitável de 47, com os dígitos verificadores corretos. Usado para gerar boletos
 * simulados e para exibir/desenhar boletos vindos de qualquer gateway.
 */
public final class Boleto {

    /** Código de banco usado nos boletos simulados (não corresponde a banco real). */
    public static final String BANCO_FICTICIO = "999";

    private static final LocalDate BASE_FATOR = LocalDate.of(1997, 10, 7);

    private Boleto() {
    }

    /** Fator de vencimento (dias desde 07/10/1997; reinicia em 1000 após 9999, a partir de 22/02/2025). */
    public static String fatorVencimento(LocalDate vencimento) {
        long dias = ChronoUnit.DAYS.between(BASE_FATOR, vencimento);
        long fator = dias <= 9999 ? dias : ((dias - 10000) % 9000) + 1000;
        return "%04d".formatted(fator);
    }

    static int modulo10(String numero) {
        int soma = 0;
        int peso = 2;
        for (int i = numero.length() - 1; i >= 0; i--) {
            int produto = digito(numero, i) * peso;
            soma += produto > 9 ? produto / 10 + produto % 10 : produto;
            peso = peso == 2 ? 1 : 2;
        }
        return (10 - soma % 10) % 10;
    }

    static int modulo11(String numero) {
        int soma = 0;
        int peso = 2;
        for (int i = numero.length() - 1; i >= 0; i--) {
            soma += digito(numero, i) * peso;
            peso = peso == 9 ? 2 : peso + 1;
        }
        int dv = 11 - soma % 11;
        return dv == 0 || dv == 10 || dv == 11 ? 1 : dv;
    }

    /** Monta o código de barras (44 dígitos). {@code campoLivre} tem 25 dígitos, definidos pelo banco. */
    public static String montarCodigoBarras(String banco, int valorCentavos, LocalDate vencimento, String campoLivre) {
        if (!banco.matches("\\d{3}") || !campoLivre.matches("\\d{25}")) {
            throw new IllegalArgumentException("Banco ou campo livre inválido.");
        }
        String semDv = banco + "9" + fatorVencimento(vencimento) + "%010d".formatted(valorCentavos) + campoLivre;
        return semDv.substring(0, 4) + modulo11(semDv) + semDv.substring(4);
    }

    /** Converte o código de barras (44) na linha digitável formatada (47 dígitos). */
    public static String codigoParaLinha(String codigo) {
        String campoLivre = codigo.substring(19);
        String c1 = codigo.substring(0, 4) + campoLivre.substring(0, 5);
        String c2 = campoLivre.substring(5, 15);
        String c3 = campoLivre.substring(15, 25);
        String campo1 = c1 + modulo10(c1);
        String campo2 = c2 + modulo10(c2);
        String campo3 = c3 + modulo10(c3);
        return campo1.substring(0, 5) + "." + campo1.substring(5) + " "
                + campo2.substring(0, 5) + "." + campo2.substring(5) + " "
                + campo3.substring(0, 5) + "." + campo3.substring(5) + " "
                + codigo.charAt(4) + " " + codigo.substring(5, 19);
    }

    /** Caminho inverso: da linha digitável (com ou sem máscara) ao código de barras. */
    public static String linhaParaCodigo(String linha) {
        String d = linha.replaceAll("\\D", "");
        if (d.length() != 47) {
            throw new IllegalArgumentException("Linha digitável inválida.");
        }
        return d.substring(0, 4) + d.charAt(32) + d.substring(33, 47)
                + d.substring(4, 9) + d.substring(10, 20) + d.substring(21, 31);
    }

    /** Confere os quatro dígitos verificadores de uma linha digitável. */
    public static boolean linhaValida(String linha) {
        String d = linha.replaceAll("\\D", "");
        if (d.length() != 47) {
            return false;
        }
        String codigo = linhaParaCodigo(d);
        return modulo10(d.substring(0, 9)) == digito(d, 9)
                && modulo10(d.substring(10, 20)) == digito(d, 20)
                && modulo10(d.substring(21, 31)) == digito(d, 31)
                && modulo11(codigo.substring(0, 4) + codigo.substring(5)) == digito(codigo, 4);
    }

    private static int digito(String s, int i) {
        return s.charAt(i) - '0';
    }
}
