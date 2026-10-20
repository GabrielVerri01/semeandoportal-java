package br.org.semeando.aluno;

import br.org.semeando.comum.EnumPostgres;
import br.org.semeando.comum.Ids;
import br.org.semeando.conta.Usuario;
import br.org.semeando.domain.Sexo;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.hibernate.annotations.JdbcType;

/**
 * Raiz do agregado Aluno: ficha (dados, filiação, responsável), pessoas autorizadas e documentos.
 * A FichaSaude (dado sensível) NÃO é mapeada aqui de propósito: só é lida sob demanda, pelo
 * repositório próprio, pela secretaria e com auditoria.
 */
@Entity
public class Aluno {

    @Id
    private String id = Ids.novo();

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userId")
    private Usuario user;

    /** AAAA (ano de ingresso) + sequencial de 6 dígitos. */
    private String numeroMatricula;

    /** Crianças pequenas podem não ter CPF. */
    private String cpf;

    private LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)
    @JdbcType(EnumPostgres.class)
    private Sexo sexo;

    private String telefone;
    private String cep;
    private String endereco;
    private String bairro;
    private String filiacao1;
    private String filiacao2;

    /** Vem de outra escola particular → exige declaração de adimplência (Anexo I). */
    private boolean origemEscolaParticular;

    private boolean autorizaUsoImagem;

    private String responsavelNome;
    private String responsavelParentesco;
    private String responsavelCpf;
    private String responsavelTelefone;
    private String responsavelEmail;

    /** Exigido pelo contrato (cláusula de emergência). */
    private String hospitalEmergencia;

    /** LGPD art. 14: consentimento específico de um dos pais/responsável. */
    private Instant consentimentoLgpdEm;
    private String consentimentoVersao;

    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt")
    private List<PessoaAutorizada> autorizados = new ArrayList<>();

    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL)
    @OrderBy("createdAt")
    private List<Documento> documentos = new ArrayList<>();

    private Instant createdAt = Instant.now();

    protected Aluno() {
    }

    public Aluno(Usuario user, String numeroMatricula, LocalDate dataNascimento) {
        this.user = user;
        this.numeroMatricula = numeroMatricula;
        this.dataNascimento = dataNascimento;
    }

    public PessoaAutorizada adicionarAutorizado(String nome, String parentesco, String documento, String telefone) {
        PessoaAutorizada p = new PessoaAutorizada(this, nome, parentesco, documento, telefone);
        autorizados.add(p);
        return p;
    }

    public Documento adicionarDocumento(String tipo, String url, String nomeArquivo) {
        Documento d = new Documento(this, tipo, url, nomeArquivo);
        documentos.add(d);
        return d;
    }

    /** Registra o consentimento LGPD do responsável (versão do termo aceito). */
    public void registrarConsentimento(Instant em, String versao) {
        this.consentimentoLgpdEm = em;
        this.consentimentoVersao = versao;
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

    public String getNumeroMatricula() {
        return numeroMatricula;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
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

    public String getFiliacao1() {
        return filiacao1;
    }

    public void setFiliacao1(String filiacao1) {
        this.filiacao1 = filiacao1;
    }

    public String getFiliacao2() {
        return filiacao2;
    }

    public void setFiliacao2(String filiacao2) {
        this.filiacao2 = filiacao2;
    }

    public boolean isOrigemEscolaParticular() {
        return origemEscolaParticular;
    }

    public void setOrigemEscolaParticular(boolean origemEscolaParticular) {
        this.origemEscolaParticular = origemEscolaParticular;
    }

    public boolean isAutorizaUsoImagem() {
        return autorizaUsoImagem;
    }

    public void setAutorizaUsoImagem(boolean autorizaUsoImagem) {
        this.autorizaUsoImagem = autorizaUsoImagem;
    }

    public String getResponsavelNome() {
        return responsavelNome;
    }

    public void setResponsavelNome(String responsavelNome) {
        this.responsavelNome = responsavelNome;
    }

    public String getResponsavelParentesco() {
        return responsavelParentesco;
    }

    public void setResponsavelParentesco(String responsavelParentesco) {
        this.responsavelParentesco = responsavelParentesco;
    }

    public String getResponsavelCpf() {
        return responsavelCpf;
    }

    public void setResponsavelCpf(String responsavelCpf) {
        this.responsavelCpf = responsavelCpf;
    }

    public String getResponsavelTelefone() {
        return responsavelTelefone;
    }

    public void setResponsavelTelefone(String responsavelTelefone) {
        this.responsavelTelefone = responsavelTelefone;
    }

    public String getResponsavelEmail() {
        return responsavelEmail;
    }

    public void setResponsavelEmail(String responsavelEmail) {
        this.responsavelEmail = responsavelEmail;
    }

    public String getHospitalEmergencia() {
        return hospitalEmergencia;
    }

    public void setHospitalEmergencia(String hospitalEmergencia) {
        this.hospitalEmergencia = hospitalEmergencia;
    }

    public Instant getConsentimentoLgpdEm() {
        return consentimentoLgpdEm;
    }

    public String getConsentimentoVersao() {
        return consentimentoVersao;
    }

    public List<PessoaAutorizada> getAutorizados() {
        return Collections.unmodifiableList(autorizados);
    }

    public List<Documento> getDocumentos() {
        return Collections.unmodifiableList(documentos);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    /** Data de ingresso — usada na carga de dados legados/seed. */
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
