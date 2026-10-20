package br.org.semeando.auditoria;

import br.org.semeando.comum.Ids;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.Instant;
import java.util.Map;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/** Trilha de auditoria (LGPD): quem acessou ou alterou dados, quando e o quê. Somente inserção. */
@Entity
public class Auditoria {

    @Id
    private String id = Ids.novo();

    private String usuarioId;

    private String usuarioNome;

    /** Ex.: "matricula.status", "documento.download". */
    private String acao;

    /** Ex.: "Matricula", "Aluno". */
    private String entidade;

    private String entidadeId;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> detalhes;

    private Instant createdAt = Instant.now();

    protected Auditoria() {
    }

    public Auditoria(String usuarioId, String usuarioNome, String acao, String entidade, String entidadeId,
            Map<String, Object> detalhes) {
        this.usuarioId = usuarioId;
        this.usuarioNome = usuarioNome;
        this.acao = acao;
        this.entidade = entidade;
        this.entidadeId = entidadeId;
        this.detalhes = detalhes;
    }

    public String getId() {
        return id;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public String getUsuarioNome() {
        return usuarioNome;
    }

    public String getAcao() {
        return acao;
    }

    public String getEntidade() {
        return entidade;
    }

    public String getEntidadeId() {
        return entidadeId;
    }

    public Map<String, Object> getDetalhes() {
        return detalhes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
