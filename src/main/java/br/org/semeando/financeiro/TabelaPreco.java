package br.org.semeando.financeiro;

import br.org.semeando.comum.EnumPostgres;
import br.org.semeando.comum.Ids;
import br.org.semeando.domain.ModalidadeCobranca;
import br.org.semeando.domain.Serie;
import br.org.semeando.domain.cobranca.TabelaOficial;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import java.time.Instant;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Tabela oficial de preços: ÚNICA fonte de valores de cobrança (RN-05), por ano letivo e série.
 * Alterar uma linha não altera planos existentes — eles guardam a cópia do valor.
 */
@Entity
public class TabelaPreco {

    @Id
    private String id = Ids.novo();

    private int anoLetivo;

    @Enumerated(EnumType.STRING)
    @JdbcType(EnumPostgres.class)
    private Serie serie;

    @Enumerated(EnumType.STRING)
    @JdbcType(EnumPostgres.class)
    private ModalidadeCobranca modalidade;

    private int valorParcelaCentavos;

    /** Valor com desconto de pontualidade (até o vencimento), como impresso na tabela oficial. */
    private Integer valorComDescontoCentavos;

    private int quantidadeParcelas;

    /** Mês do 1º vencimento (2 = fevereiro). */
    private int primeiroMes;

    private int diaVencimento;

    @UpdateTimestamp
    private Instant atualizadoEm;

    protected TabelaPreco() {
    }

    public TabelaPreco(int anoLetivo, TabelaOficial.Linha linha) {
        this.anoLetivo = anoLetivo;
        this.serie = linha.serie();
        this.modalidade = linha.modalidade();
        this.valorParcelaCentavos = linha.valorParcelaCentavos();
        this.valorComDescontoCentavos = linha.valorComDescontoCentavos();
        this.quantidadeParcelas = linha.quantidadeParcelas();
        this.primeiroMes = linha.primeiroMes();
        this.diaVencimento = linha.diaVencimento();
    }

    public String getId() {
        return id;
    }

    public int getAnoLetivo() {
        return anoLetivo;
    }

    public Serie getSerie() {
        return serie;
    }

    public ModalidadeCobranca getModalidade() {
        return modalidade;
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

    public int getPrimeiroMes() {
        return primeiroMes;
    }

    public int getDiaVencimento() {
        return diaVencimento;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }
}
