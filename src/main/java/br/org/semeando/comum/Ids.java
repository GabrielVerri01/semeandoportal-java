package br.org.semeando.comum;

import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Ids textuais no formato do cuid usado pela versão Next.js (Prisma {@code @default(cuid())}):
 * "c" + instante + contador + aleatório, em base 36. Mantém o schema e os dados compatíveis.
 */
public final class Ids {

    private static final SecureRandom ALEATORIO = new SecureRandom();
    private static final AtomicInteger CONTADOR = new AtomicInteger(ALEATORIO.nextInt());
    private static final int BLOCO = 36 * 36 * 36 * 36;

    private Ids() {
    }

    public static String novo() {
        return "c"
                + Long.toString(System.currentTimeMillis(), 36)
                + bloco(Math.floorMod(CONTADOR.getAndIncrement(), BLOCO))
                + bloco(ALEATORIO.nextInt(BLOCO))
                + bloco(ALEATORIO.nextInt(BLOCO));
    }

    private static String bloco(int n) {
        String s = Integer.toString(n, 36);
        return "0".repeat(4 - s.length()) + s;
    }
}
