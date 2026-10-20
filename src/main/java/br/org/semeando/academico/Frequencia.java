package br.org.semeando.academico;

import br.org.semeando.aluno.Aluno;
import br.org.semeando.comum.Ids;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;

/** Presença do aluno numa aula, num dia. */
@Entity
public class Frequencia {

    @Id
    private String id = Ids.novo();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "alunoId")
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aulaId")
    private Aula aula;

    private LocalDate data;

    private boolean presente;

    protected Frequencia() {
    }

    public Frequencia(Aluno aluno, Aula aula, LocalDate data, boolean presente) {
        this.aluno = aluno;
        this.aula = aula;
        this.data = data;
        this.presente = presente;
    }

    public String getId() {
        return id;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public Aula getAula() {
        return aula;
    }

    public LocalDate getData() {
        return data;
    }

    public boolean isPresente() {
        return presente;
    }

    public void setPresente(boolean presente) {
        this.presente = presente;
    }
}
