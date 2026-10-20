package br.org.semeando.aluno;

import br.org.semeando.comum.Ids;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.Instant;

/** Documento enviado. O arquivo fica no storage privado; {@code url} guarda só a chave ("alunoId/arquivo"). */
@Entity
public class Documento {

    @Id
    private String id = Ids.novo();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "alunoId")
    private Aluno aluno;

    /** Chave de ItemChecklist (documento obrigatório), "termo" ou "outro". */
    private String tipo;

    /** Chave no storage (privado). */
    private String url;

    private String nomeArquivo;

    private boolean conferido;

    private Instant createdAt = Instant.now();

    protected Documento() {
    }

    Documento(Aluno aluno, String tipo, String url, String nomeArquivo) {
        this.aluno = aluno;
        this.tipo = tipo;
        this.url = url;
        this.nomeArquivo = nomeArquivo;
    }

    public String getId() {
        return id;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public String getTipo() {
        return tipo;
    }

    public String getUrl() {
        return url;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public boolean isConferido() {
        return conferido;
    }

    public void setConferido(boolean conferido) {
        this.conferido = conferido;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
