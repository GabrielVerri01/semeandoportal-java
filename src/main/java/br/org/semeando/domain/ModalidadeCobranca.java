package br.org.semeando.domain;

/**
 * Modelo DUAL de cobrança, unificado (RN-04/RN-05) — enum com estratégia.
 * O que muda entre as modalidades é só o "documento base" e o credor exibido;
 * plano, parcelas, encargos e conciliação são iguais (PlanoCobranca).
 * Nova modalidade (ex.: atividade extra) = nova constante + linha na tabela; nenhuma regra muda.
 */
public enum ModalidadeCobranca {
    TAXA_ASSOCIADO(
            "Taxa de associado",
            TipoCobranca.TAXA_ASSOCIADO,
            "Ficha de sócio do Instituto",
            "FS",
            Escola.RAZAO_SOCIAL,
            false),
    MENSALIDADE(
            "Mensalidade",
            TipoCobranca.MENSALIDADE,
            "Contrato de prestação de serviços educacionais",
            "CT",
            Escola.RAZAO_SOCIAL + " (" + Escola.NOME + ")",
            true);

    private final String rotulo;
    private final TipoCobranca tipoCobranca;
    private final String documentoBase;
    private final String prefixo;
    private final String credor;
    private final boolean emiteBoleto;

    ModalidadeCobranca(String rotulo, TipoCobranca tipoCobranca, String documentoBase, String prefixo,
            String credor, boolean emiteBoleto) {
        this.rotulo = rotulo;
        this.tipoCobranca = tipoCobranca;
        this.documentoBase = documentoBase;
        this.prefixo = prefixo;
        this.credor = credor;
        this.emiteBoleto = emiteBoleto;
    }

    public String rotulo() {
        return rotulo;
    }

    public TipoCobranca tipoCobranca() {
        return tipoCobranca;
    }

    /** Documento que dá origem à cobrança. */
    public String documentoBase() {
        return documentoBase;
    }

    /** Prefixo do número do documento (CT-2027-0001 / FS-2027-0001). */
    public String prefixo() {
        return prefixo;
    }

    /** Quem recebe (sempre a mesma pessoa jurídica — RN-01). */
    public String credor() {
        return credor;
    }

    /** Mensalidade vai por boleto; taxa de associado, na secretaria (HIPÓTESE até o Anexo II). */
    public boolean emiteBoleto() {
        return emiteBoleto;
    }
}
