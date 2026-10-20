package br.org.semeando.academico;

import br.org.semeando.comum.Ids;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/** Componente curricular (Fundamental) ou rotina/campo de experiência (Infantil) — BNCC. */
@Entity
public class Disciplina {

    @Id
    private String id = Ids.novo();

    private String nome;

    protected Disciplina() {
    }

    public Disciplina(String nome) {
        this.nome = nome;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }
}
