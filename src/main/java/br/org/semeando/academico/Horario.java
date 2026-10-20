package br.org.semeando.academico;

import br.org.semeando.comum.Ids;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Horario {

    @Id
    private String id = Ids.novo();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aulaId")
    private Aula aula;

    /** 1 = segunda … 5 = sexta. */
    private int diaSemana;

    /** "07:30". */
    private String horaInicio;

    /** "08:20". */
    private String horaFim;

    private String sala;

    protected Horario() {
    }

    public Horario(Aula aula, int diaSemana, String horaInicio, String horaFim, String sala) {
        this.aula = aula;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.sala = sala;
    }

    public String getId() {
        return id;
    }

    public Aula getAula() {
        return aula;
    }

    public int getDiaSemana() {
        return diaSemana;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public String getHoraFim() {
        return horaFim;
    }

    public String getSala() {
        return sala;
    }
}
