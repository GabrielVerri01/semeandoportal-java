package br.org.semeando.academico;

import br.org.semeando.aluno.Aluno;
import br.org.semeando.comum.Ids;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;

/** Nota bimestral (só Ensino Fundamental — Serie.temNotaNumerica). */
@Entity
public class Nota {

    @Id
    private String id = Ids.novo();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "alunoId")
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aulaId")
    private Aula aula;

    private int bimestre;

    private BigDecimal valor;

    protected Nota() {
    }

    public Nota(Aluno aluno, Aula aula, int bimestre, BigDecimal valor) {
        this.aluno = aluno;
        this.aula = aula;
        this.bimestre = bimestre;
        this.valor = valor;
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

    public int getBimestre() {
        return bimestre;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
