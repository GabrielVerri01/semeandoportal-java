package br.org.semeando.financeiro;

import br.org.semeando.comum.EnumPostgres;
import br.org.semeando.comum.Ids;
import br.org.semeando.domain.TipoSocio;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import java.time.Instant;
import java.time.LocalDate;
import org.hibernate.annotations.JdbcType;

/** Sócio do Instituto (Ficha de Cadastro de Sócio). É o responsável que paga a taxa de associado. */
@Entity
public class Socio {

    @Id
    private String id = Ids.novo();

    private String nome;
    private String cpf;
    private LocalDate dataNascimento;
    private String estadoCivil;
    private String profissao;
    private String endereco;
    private String bairro;
    private String contato;

    @Enumerated(EnumType.STRING)
    @JdbcType(EnumPostgres.class)
    private TipoSocio tipo = TipoSocio.EFETIVO;

    private int filhosCriancas;
    private int filhosAdolescentes;
    private String observacoes;
    private LocalDate dataAssociacao;
    private LocalDate dataAfastamento;

    private Instant createdAt = Instant.now();

    protected Socio() {
    }

    public Socio(String nome, String cpf, LocalDate dataAssociacao) {
        this.nome = nome;
        this.cpf = cpf;
        this.dataAssociacao = dataAssociacao;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(String estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public String getProfissao() {
        return profissao;
    }

    public void setProfissao(String profissao) {
        this.profissao = profissao;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public TipoSocio getTipo() {
        return tipo;
    }

    public void setTipo(TipoSocio tipo) {
        this.tipo = tipo;
    }

    public int getFilhosCriancas() {
        return filhosCriancas;
    }

    public void setFilhosCriancas(int filhosCriancas) {
        this.filhosCriancas = filhosCriancas;
    }

    public int getFilhosAdolescentes() {
        return filhosAdolescentes;
    }

    public void setFilhosAdolescentes(int filhosAdolescentes) {
        this.filhosAdolescentes = filhosAdolescentes;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public LocalDate getDataAssociacao() {
        return dataAssociacao;
    }

    public LocalDate getDataAfastamento() {
        return dataAfastamento;
    }

    public void setDataAfastamento(LocalDate dataAfastamento) {
        this.dataAfastamento = dataAfastamento;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
