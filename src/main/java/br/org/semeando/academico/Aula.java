package br.org.semeando.academico;

import br.org.semeando.comum.Ids;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/** O professor X leciona a disciplina Y na turma Z. Os horários semanais ficam em Horario. */
@Entity
public class Aula {

    @Id
    private String id = Ids.novo();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "turmaId")
    private Turma turma;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "disciplinaId")
    private Disciplina disciplina;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "professorId")
    private Professor professor;

    protected Aula() {
    }

    public Aula(Turma turma, Disciplina disciplina, Professor professor) {
        this.turma = turma;
        this.disciplina = disciplina;
        this.professor = professor;
    }

    public String getId() {
        return id;
    }

    public Turma getTurma() {
        return turma;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }
}
