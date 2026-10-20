package br.org.semeando.aluno;

import br.org.semeando.comum.Ids;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

/**
 * Saúde e inclusão (ficha de matrícula). DADO SENSÍVEL (LGPD art. 11 + art. 14):
 * tabela separada, só a secretaria lê, e todo acesso é auditado.
 */
@Entity
public class FichaSaude {

    @Id
    private String id = Ids.novo();

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "alunoId")
    private Aluno aluno;

    /** null = não. */
    private String deficiencia;

    private boolean dificuldadeFala;
    private boolean dificuldadeVisao;
    private boolean dificuldadeLocomocao;

    /** FONO, PSICOLOGIA, TO, PSICOPEDAGOGIA, OUTROS. */
    @JdbcTypeCode(SqlTypes.ARRAY)
    private List<String> acompanhamentos = new ArrayList<>();

    private String acompanhamentoOutro;
    private String restricaoAlimentar;
    private String alergia;
    private String observacoes;

    @UpdateTimestamp
    private Instant atualizadoEm;

    protected FichaSaude() {
    }

    public FichaSaude(Aluno aluno) {
        this.aluno = aluno;
    }

    public String getId() {
        return id;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public String getDeficiencia() {
        return deficiencia;
    }

    public void setDeficiencia(String deficiencia) {
        this.deficiencia = deficiencia;
    }

    public boolean isDificuldadeFala() {
        return dificuldadeFala;
    }

    public void setDificuldadeFala(boolean dificuldadeFala) {
        this.dificuldadeFala = dificuldadeFala;
    }

    public boolean isDificuldadeVisao() {
        return dificuldadeVisao;
    }

    public void setDificuldadeVisao(boolean dificuldadeVisao) {
        this.dificuldadeVisao = dificuldadeVisao;
    }

    public boolean isDificuldadeLocomocao() {
        return dificuldadeLocomocao;
    }

    public void setDificuldadeLocomocao(boolean dificuldadeLocomocao) {
        this.dificuldadeLocomocao = dificuldadeLocomocao;
    }

    public List<String> getAcompanhamentos() {
        return List.copyOf(acompanhamentos);
    }

    public void setAcompanhamentos(List<String> acompanhamentos) {
        this.acompanhamentos = new ArrayList<>(acompanhamentos);
    }

    public String getAcompanhamentoOutro() {
        return acompanhamentoOutro;
    }

    public void setAcompanhamentoOutro(String acompanhamentoOutro) {
        this.acompanhamentoOutro = acompanhamentoOutro;
    }

    public String getRestricaoAlimentar() {
        return restricaoAlimentar;
    }

    public void setRestricaoAlimentar(String restricaoAlimentar) {
        this.restricaoAlimentar = restricaoAlimentar;
    }

    public String getAlergia() {
        return alergia;
    }

    public void setAlergia(String alergia) {
        this.alergia = alergia;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }
}
