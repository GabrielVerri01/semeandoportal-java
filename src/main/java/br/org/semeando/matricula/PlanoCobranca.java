package br.org.semeando.matricula;

import br.org.semeando.comum.EnumPostgres;
import br.org.semeando.comum.Ids;
import br.org.semeando.domain.MetodoPagamento;
import br.org.semeando.domain.ModalidadeCobranca;
import br.org.semeando.domain.OrigemPlano;
import br.org.semeando.domain.StatusPlano;
import br.org.semeando.financeiro.Socio;
import br.org.semeando.financeiro.TabelaPreco;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
 * Abstração que unifica o modelo dual (MODELO.md §4.2): o "documento base" muda (contrato ou
 * ficha de sócio), mas o plano, as parcelas, os encargos e a conciliação são os mesmos.
 *
 * <p>RN-05: o valor é uma CÓPIA da TabelaPreco no momento da geração — nunca digitado. Não há
 * setter de valor; planos de origem LEGADO (Word) só entram por {@link #importar}.
 */
@Entity
public class PlanoCobranca {

    @Id
    private String id = Ids.novo();

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "matriculaId")
    private Matricula matricula;

    @Enumerated(EnumType.STRING)
    @JdbcType(EnumPostgres.class)
    private ModalidadeCobranca modalidade;

    /** CT-2027-0001 (contrato) ou FS-2027-0001 (ficha de sócio). */
    private String numeroDocumento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tabelaPrecoId")
    private TabelaPreco tabelaPreco;

    /** Taxa de associado: o responsável sócio do Instituto que assina a ficha. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "socioId")
    private Socio socio;

    private int valorParcelaCentavos;

    private Integer valorComDescontoCentavos;

    private int quantidadeParcelas;

    @Enumerated(EnumType.STRING)
    @JdbcType(EnumPostgres.class)
    private OrigemPlano origem = OrigemPlano.SISTEMA;

    @Enumerated(EnumType.STRING)
    @JdbcType(EnumPostgres.class)
    private StatusPlano status = StatusPlano.ATIVO;

    private Instant assinadoEm;

    @OneToMany(mappedBy = "plano", cascade = CascadeType.ALL)
    @OrderBy("parcela")
    private List<Cobranca> cobrancas = new ArrayList<>();

    private Instant createdAt = Instant.now();

    protected PlanoCobranca() {
    }

    private PlanoCobranca(Matricula matricula, ModalidadeCobranca modalidade, String numeroDocumento) {
        this.matricula = matricula;
        this.modalidade = modalidade;
        this.numeroDocumento = numeroDocumento;
    }

    /** Plano gerado pelo sistema: copia valor, desconto e parcelas da linha da tabela oficial (RN-05). */
    static PlanoCobranca daTabela(Matricula matricula, String numeroDocumento, TabelaPreco tabela, Socio socio) {
        PlanoCobranca p = new PlanoCobranca(matricula, tabela.getModalidade(), numeroDocumento);
        p.copiarDaTabela(tabela);
        p.socio = socio;
        return p;
    }

    /**
     * Carga inicial (seed ou migração dos contratos do Word). É a única forma de um plano ter valor
     * diferente da tabela — e é exatamente o que a conciliação (RN-09) detecta depois.
     */
    public static PlanoCobranca importar(Matricula matricula, ModalidadeCobranca modalidade, String numeroDocumento,
            TabelaPreco tabelaPreco, Socio socio, int valorParcelaCentavos, Integer valorComDescontoCentavos,
            int quantidadeParcelas, OrigemPlano origem, StatusPlano status, Instant assinadoEm) {
        PlanoCobranca p = new PlanoCobranca(matricula, modalidade, numeroDocumento);
        p.tabelaPreco = tabelaPreco;
        p.socio = socio;
        p.valorParcelaCentavos = valorParcelaCentavos;
        p.valorComDescontoCentavos = valorComDescontoCentavos;
        p.quantidadeParcelas = quantidadeParcelas;
        p.origem = origem;
        p.status = status;
        p.assinadoEm = assinadoEm;
        return p;
    }

    /** Pagamento já ocorrido, para a carga inicial. */
    public record PagamentoImportado(MetodoPagamento metodo, Instant pagoEm,
            int valorPagoCentavos) {
    }

    /** Carga inicial de uma parcela (valores copiados do plano). {@code pagamento} null = em aberto. */
    public Cobranca importarParcela(int parcela, String referencia, LocalDate vencimento, String boletoLinha,
            PagamentoImportado pagamento) {
        Cobranca c = new Cobranca(this, parcela, referencia, vencimento, boletoLinha);
        if (pagamento != null) {
            c.registrarPagamento(pagamento.metodo(), pagamento.pagoEm(), pagamento.valorPagoCentavos());
        }
        cobrancas.add(c);
        return c;
    }

    /** Realinhamento (RN-09): o plano volta a valer o que diz a tabela oficial. */
    void copiarDaTabela(TabelaPreco tabela) {
        this.tabelaPreco = tabela;
        this.valorParcelaCentavos = tabela.getValorParcelaCentavos();
        this.valorComDescontoCentavos = tabela.getValorComDescontoCentavos();
        this.quantidadeParcelas = tabela.getQuantidadeParcelas();
    }

    void definirStatus(StatusPlano status) {
        this.status = status;
    }

    void marcarAssinado(Instant em) {
        this.assinadoEm = em;
    }

    Cobranca adicionarParcela(int parcela, String referencia, LocalDate vencimento, String boletoLinha) {
        Cobranca c = new Cobranca(this, parcela, referencia, vencimento, boletoLinha);
        cobrancas.add(c);
        return c;
    }

    public String getId() {
        return id;
    }

    public Matricula getMatricula() {
        return matricula;
    }

    public ModalidadeCobranca getModalidade() {
        return modalidade;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public TabelaPreco getTabelaPreco() {
        return tabelaPreco;
    }

    public Socio getSocio() {
        return socio;
    }

    public int getValorParcelaCentavos() {
        return valorParcelaCentavos;
    }

    public Integer getValorComDescontoCentavos() {
        return valorComDescontoCentavos;
    }

    public int getQuantidadeParcelas() {
        return quantidadeParcelas;
    }

    public OrigemPlano getOrigem() {
        return origem;
    }

    public StatusPlano getStatus() {
        return status;
    }

    public Instant getAssinadoEm() {
        return assinadoEm;
    }

    public List<Cobranca> getCobrancas() {
        return Collections.unmodifiableList(cobrancas);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
