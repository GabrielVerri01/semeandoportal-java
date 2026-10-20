package br.org.semeando.matricula;

import br.org.semeando.aluno.Aluno;
import br.org.semeando.comum.EnumPostgres;
import br.org.semeando.comum.Ids;
import br.org.semeando.domain.MetodoPagamento;
import br.org.semeando.domain.StatusPagamento;
import br.org.semeando.domain.TipoCobranca;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.Instant;
import java.time.LocalDate;
import org.hibernate.annotations.JdbcType;

/**
 * Parcela de um plano (ou cobrança avulsa). Valores em centavos, copiados do plano.
 * Pagamento e realinhamento só no CobrancaService — mutadores package-private.
 */
@Entity
public class Cobranca {

    @Id
    private String id = Ids.novo();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "alunoId")
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matriculaId")
    private Matricula matricula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planoId")
    private PlanoCobranca plano;

    /** 1..quantidadeParcelas. */
    private Integer parcela;

    @Enumerated(EnumType.STRING)
    @JdbcType(EnumPostgres.class)
    private TipoCobranca tipo;

    /** "Mensalidade 03/11 — Abril/2026". */
    private String referencia;

    private int valorCentavos;

    /** Pago até o vencimento → este valor (desconto de pontualidade). */
    private Integer valorComDescontoCentavos;

    private LocalDate vencimento;

    @Enumerated(EnumType.STRING)
    @JdbcType(EnumPostgres.class)
    private StatusPagamento status = StatusPagamento.ABERTO;

    @Enumerated(EnumType.STRING)
    @JdbcType(EnumPostgres.class)
    private MetodoPagamento metodo;

    private String gatewayId;
    private String pixCopiaCola;
    private String boletoLinha;
    private String boletoPdfUrl;
    private Instant pagoEm;

    /** Com encargos, quando pago em atraso. */
    private Integer valorPagoCentavos;

    private Instant createdAt = Instant.now();

    protected Cobranca() {
    }

    /** Parcela de plano: valores copiados do plano no momento da geração. */
    Cobranca(PlanoCobranca plano, int parcela, String referencia, LocalDate vencimento, String boletoLinha) {
        this.plano = plano;
        this.matricula = plano.getMatricula();
        this.aluno = plano.getMatricula().getAluno();
        this.parcela = parcela;
        this.tipo = plano.getModalidade().tipoCobranca();
        this.referencia = referencia;
        this.valorCentavos = plano.getValorParcelaCentavos();
        this.valorComDescontoCentavos = plano.getValorComDescontoCentavos();
        this.vencimento = vencimento;
        this.boletoLinha = boletoLinha;
    }

    void registrarPagamento(MetodoPagamento metodo, Instant pagoEm, int valorPagoCentavos) {
        this.status = StatusPagamento.PAGO;
        this.metodo = metodo;
        this.pagoEm = pagoEm;
        this.valorPagoCentavos = valorPagoCentavos;
    }

    void cancelar() {
        this.status = StatusPagamento.CANCELADO;
    }

    /** Realinhamento de parcela em aberto ao valor do plano (RN-09). */
    void realinharAoPlano() {
        this.valorCentavos = plano.getValorParcelaCentavos();
        this.valorComDescontoCentavos = plano.getValorComDescontoCentavos();
    }

    void definirBoletoLinha(String boletoLinha) {
        this.boletoLinha = boletoLinha;
    }

    public String getId() {
        return id;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public Matricula getMatricula() {
        return matricula;
    }

    public PlanoCobranca getPlano() {
        return plano;
    }

    public Integer getParcela() {
        return parcela;
    }

    public TipoCobranca getTipo() {
        return tipo;
    }

    public String getReferencia() {
        return referencia;
    }

    public int getValorCentavos() {
        return valorCentavos;
    }

    public Integer getValorComDescontoCentavos() {
        return valorComDescontoCentavos;
    }

    public LocalDate getVencimento() {
        return vencimento;
    }

    public StatusPagamento getStatus() {
        return status;
    }

    public MetodoPagamento getMetodo() {
        return metodo;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public String getPixCopiaCola() {
        return pixCopiaCola;
    }

    public String getBoletoLinha() {
        return boletoLinha;
    }

    public String getBoletoPdfUrl() {
        return boletoPdfUrl;
    }

    public Instant getPagoEm() {
        return pagoEm;
    }

    public Integer getValorPagoCentavos() {
        return valorPagoCentavos;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
