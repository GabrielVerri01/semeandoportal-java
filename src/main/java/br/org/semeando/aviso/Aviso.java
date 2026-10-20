package br.org.semeando.aviso;

import br.org.semeando.comum.EnumPostgres;
import br.org.semeando.comum.Ids;
import br.org.semeando.domain.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import java.time.Instant;
import org.hibernate.annotations.JdbcType;

/** Aviso do mural, para um perfil ou para todos. */
@Entity
public class Aviso {

    @Id
    private String id = Ids.novo();

    private String titulo;

    private String texto;

    /** null = todos. */
    @Enumerated(EnumType.STRING)
    @JdbcType(EnumPostgres.class)
    private Role publicoRole;

    private Instant createdAt = Instant.now();

    protected Aviso() {
    }

    public Aviso(String titulo, String texto, Role publicoRole) {
        this.titulo = titulo;
        this.texto = texto;
        this.publicoRole = publicoRole;
    }

    public Aviso(String titulo, String texto, Role publicoRole, Instant createdAt) {
        this(titulo, texto, publicoRole);
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getTexto() {
        return texto;
    }

    public Role getPublicoRole() {
        return publicoRole;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
