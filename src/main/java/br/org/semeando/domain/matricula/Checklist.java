package br.org.semeando.domain.matricula;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Checklist de conferência da matrícula (imutável). Só com tudo cumprido a matrícula fica ATIVA (RN-07).
 * A declaração de adimplência é dispensada de quem não vem de escola particular.
 */
public final class Checklist {

    private final EnumMap<ItemChecklist, Boolean> itens;

    private Checklist(EnumMap<ItemChecklist, Boolean> itens) {
        this.itens = itens;
    }

    public static Checklist vazio() {
        return comTodos(false);
    }

    public static Checklist completo() {
        return comTodos(true);
    }

    private static Checklist comTodos(boolean valor) {
        EnumMap<ItemChecklist, Boolean> m = new EnumMap<>(ItemChecklist.class);
        for (ItemChecklist i : ItemChecklist.values()) {
            m.put(i, valor);
        }
        return new Checklist(m);
    }

    /** Lê o JSON do banco com segurança (chaves ausentes ou não booleanas viram false). */
    public static Checklist ler(Map<String, ?> json) {
        EnumMap<ItemChecklist, Boolean> m = new EnumMap<>(ItemChecklist.class);
        for (ItemChecklist i : ItemChecklist.values()) {
            m.put(i, json != null && Boolean.TRUE.equals(json.get(i.chave())));
        }
        return new Checklist(m);
    }

    /** Formato gravado no banco: {"doc_responsavel": true, ...}. */
    public Map<String, Boolean> comoJson() {
        Map<String, Boolean> json = new LinkedHashMap<>();
        itens.forEach((item, valor) -> json.put(item.chave(), valor));
        return json;
    }

    public boolean cumprido(ItemChecklist item) {
        return itens.get(item);
    }

    public Checklist com(ItemChecklist item, boolean valor) {
        EnumMap<ItemChecklist, Boolean> m = new EnumMap<>(itens);
        m.put(item, valor);
        return new Checklist(m);
    }

    public static boolean dispensado(ItemChecklist item, boolean origemEscolaParticular) {
        return item == ItemChecklist.ADIMPLENCIA && !origemEscolaParticular;
    }

    public static List<ItemChecklist> exigidos(boolean origemEscolaParticular) {
        return Arrays.stream(ItemChecklist.values()).filter(i -> !dispensado(i, origemEscolaParticular)).toList();
    }

    public boolean estaCompleto(boolean origemEscolaParticular) {
        return exigidos(origemEscolaParticular).stream().allMatch(this::cumprido);
    }

    /** Progresso: cumpridos de exigidos. */
    public record Progresso(int cumpridos, int exigidos) {
    }

    public Progresso progresso(boolean origemEscolaParticular) {
        List<ItemChecklist> exigidos = exigidos(origemEscolaParticular);
        return new Progresso((int) exigidos.stream().filter(this::cumprido).count(), exigidos.size());
    }

    /** Rótulos dos itens que faltam. */
    public List<String> pendencias(boolean origemEscolaParticular) {
        return exigidos(origemEscolaParticular).stream()
                .filter(i -> !cumprido(i))
                .map(ItemChecklist::rotulo)
                .toList();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Checklist c && itens.equals(c.itens);
    }

    @Override
    public int hashCode() {
        return itens.hashCode();
    }

    @Override
    public String toString() {
        return comoJson().toString();
    }
}
