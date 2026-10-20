package br.org.semeando.academico;

import br.org.semeando.comum.EnumPostgres;
import br.org.semeando.comum.Ids;
import br.org.semeando.domain.Serie;
import br.org.semeando.domain.Turno;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import org.hibernate.annotations.JdbcType;

@Entity
public class Turma {

    @Id
    private String id = Ids.novo();

    /** "3º Ano A". */
    private String nome;

    @Enumerated(EnumType.STRING)
    @JdbcType(EnumPostgres.class)
    private Serie serie;

    @Enumerated(EnumType.STRING)
    @JdbcType(EnumPostgres.class)
    private Turno turno;

    /** Ano letivo: "2026". */
    private String periodo;

    private int vagas;

    protected Turma() {
    }

    public Turma(String nome, Serie serie, Turno turno, String periodo, int vagas) {
        this.nome = nome;
        this.serie = serie;
        this.turno = turno;
        this.periodo = periodo;
        this.vagas = vagas;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Serie getSerie() {
        return serie;
    }

    public void setSerie(Serie serie) {
        this.serie = serie;
    }

    public Turno getTurno() {
        return turno;
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
    }

    public String getPeriodo() {
        return periodo;
    }

    public int getVagas() {
        return vagas;
    }

    public void setVagas(int vagas) {
        this.vagas = vagas;
    }
}
