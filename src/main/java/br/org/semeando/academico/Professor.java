package br.org.semeando.academico;

import br.org.semeando.comum.Ids;
import br.org.semeando.conta.Usuario;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class Professor {

    @Id
    private String id = Ids.novo();

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userId")
    private Usuario user;

    private String formacao;

    protected Professor() {
    }

    public Professor(Usuario user, String formacao) {
        this.user = user;
        this.formacao = formacao;
    }

    public String getId() {
        return id;
    }

    public Usuario getUser() {
        return user;
    }

    public String getNome() {
        return user.getNome();
    }

    public String getFormacao() {
        return formacao;
    }

    public void setFormacao(String formacao) {
        this.formacao = formacao;
    }
}
