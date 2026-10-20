package br.org.semeando.aluno;

import br.org.semeando.comum.Ids;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.Instant;

/** Pessoa autorizada a buscar a criança (Anexo I do contrato). */
@Entity
public class PessoaAutorizada {

    @Id
    private String id = Ids.novo();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "alunoId")
    private Aluno aluno;

    private String nome;
    private String parentesco;
    /** RG ou CPF. */
    private String documento;
    private String telefone;

    private Instant createdAt = Instant.now();

    protected PessoaAutorizada() {
    }

    PessoaAutorizada(Aluno aluno, String nome, String parentesco, String documento, String telefone) {
        this.aluno = aluno;
        this.nome = nome;
        this.parentesco = parentesco;
        this.documento = documento;
        this.telefone = telefone;
    }

    public String getId() {
        return id;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public String getNome() {
        return nome;
    }

    public String getParentesco() {
        return parentesco;
    }

    public String getDocumento() {
        return documento;
    }

    public String getTelefone() {
        return telefone;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
