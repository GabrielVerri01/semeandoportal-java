package br.org.semeando.matricula;

import br.org.semeando.comum.EnumPostgres;
import br.org.semeando.comum.Ids;
import br.org.semeando.domain.StatusMatricula;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.Instant;
import org.hibernate.annotations.JdbcType;

/** Transição de status da matrícula (auditável). Só é criado pela própria Matricula. */
@Entity
public class HistoricoMatricula {

    @Id
    private String id = Ids.novo();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "matriculaId")
    private Matricula matricula;

    @Enumerated(EnumType.STRING)
    @JdbcType(EnumPostgres.class)
    private StatusMatricula de;

    @Enumerated(EnumType.STRING)
    @JdbcType(EnumPostgres.class)
    private StatusMatricula para;

    private String descricao;

    /** null = sistema. */
    private String autorId;

    private Instant createdAt;

    protected HistoricoMatricula() {
    }

    HistoricoMatricula(Matricula matricula, StatusMatricula de, StatusMatricula para, String descricao,
            String autorId, Instant createdAt) {
        this.matricula = matricula;
        this.de = de;
        this.para = para;
        this.descricao = descricao;
        this.autorId = autorId;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public StatusMatricula getDe() {
        return de;
    }

    public StatusMatricula getPara() {
        return para;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getAutorId() {
        return autorId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
