package br.org.semeando.matricula;

import br.org.semeando.academico.Turma;
import br.org.semeando.aluno.Aluno;
import br.org.semeando.comum.EnumPostgres;
import br.org.semeando.comum.Ids;
import br.org.semeando.domain.StatusMatricula;
import br.org.semeando.domain.matricula.Checklist;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

/**
 * Raiz do agregado Matrícula: status, checklist, histórico, plano e parcelas.
 *
 * <p>Regra inviolável 3 (CLAUDE.md): status, checklist, plano e parcelas só mudam no
 * MatriculaService. Por isso os métodos que alteram o estado são <b>package-private</b> —
 * fora do pacote {@code matricula} o compilador não deixa chamá-los. A única porta pública
 * de criação com estado arbitrário é {@link #importar}, reservada à carga inicial (seed/legado).
 */
@Entity
public class Matricula {

    @Id
    private String id = Ids.novo();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "alunoId")
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "turmaId")
    private Turma turma;

    /** Ano letivo: "2027". */
    private String periodo;

    @Enumerated(EnumType.STRING)
    @JdbcType(EnumPostgres.class)
    private StatusMatricula status = StatusMatricula.PENDENTE;

    /** Chaves de ItemChecklist (7 documentos + termo + consentimento). */
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Boolean> checklist = Checklist.vazio().comoJson();

    @OneToMany(mappedBy = "matricula", cascade = CascadeType.ALL)
    @OrderBy("createdAt")
    private List<HistoricoMatricula> historico = new ArrayList<>();

    private Instant createdAt = Instant.now();

    @UpdateTimestamp
    private Instant updatedAt;

    protected Matricula() {
    }

    Matricula(Aluno aluno, Turma turma, String periodo) {
        this.aluno = aluno;
        this.turma = turma;
        this.periodo = periodo;
    }

    /** Um evento do histórico, para a carga inicial. {@code de} null = abertura; {@code autorId} null = sistema. */
    public record RegistroHistorico(StatusMatricula de, StatusMatricula para, String descricao, String autorId,
            Instant em) {
    }

    /**
     * Carga inicial (seed ou migração dos arquivos Word): cria a matrícula já no estado em que ela
     * está hoje, com o histórico que a levou até lá. Não use em fluxo de tela — use o MatriculaService.
     */
    public static Matricula importar(Aluno aluno, Turma turma, String periodo, StatusMatricula status,
            Checklist checklist, Instant criadaEm, List<RegistroHistorico> historico) {
        Matricula m = new Matricula(aluno, turma, periodo);
        m.status = status;
        m.checklist = checklist.comoJson();
        m.createdAt = criadaEm;
        for (RegistroHistorico r : historico) {
            m.historico.add(new HistoricoMatricula(m, r.de(), r.para(), r.descricao(), r.autorId(), r.em()));
        }
        return m;
    }

    /** Muda o status e registra no histórico. A validação da transição fica no MatriculaService. */
    void registrarTransicao(StatusMatricula para, String descricao, String autorId) {
        historico.add(new HistoricoMatricula(this, status, para, descricao, autorId, Instant.now()));
        status = para;
    }

    void registrarAbertura(String descricao, String autorId) {
        historico.add(new HistoricoMatricula(this, null, status, descricao, autorId, Instant.now()));
    }

    void definirChecklist(Checklist novo) {
        checklist = novo.comoJson();
    }

    public String getId() {
        return id;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public Turma getTurma() {
        return turma;
    }

    public String getPeriodo() {
        return periodo;
    }

    public StatusMatricula getStatus() {
        return status;
    }

    public Checklist getChecklist() {
        return Checklist.ler(checklist);
    }

    public List<HistoricoMatricula> getHistorico() {
        return Collections.unmodifiableList(historico);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
