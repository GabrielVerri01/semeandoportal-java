package br.org.semeando.domain.matricula;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Itens do checklist da matrícula (Matricula.checklist): os 7 documentos obrigatórios do Anexo I
 * (RN-06) + termo assinado (contrato ou ficha de sócio) + consentimento LGPD do responsável.
 * A {@link #chave()} é a gravada no JSON do banco — a mesma da versão Next.js.
 */
public enum ItemChecklist {
    DOC_RESPONSAVEL("doc_responsavel", "Identidade e CPF dos pais ou responsáveis", true),
    CERTIDAO("certidao", "Certidão de nascimento da criança (ou RG e CPF)", true),
    VACINA("vacina", "Carteira de vacinação", true),
    COMPROVANTE("comprovante", "Comprovante de residência", true),
    /** Só para quem vem de outra escola particular (Anexo I). */
    ADIMPLENCIA("adimplencia", "Declaração de adimplência", true),
    FOTOS("fotos", "2 fotos 3x4", true),
    DOC_AUTORIZADOS("doc_autorizados", "Documento das pessoas autorizadas a buscar a criança", true),
    TERMO("termo", "Contrato ou ficha de sócio assinado", false),
    LGPD("lgpd", "Consentimento LGPD do responsável", false);

    private final String chave;
    private final String rotulo;
    private final boolean documentoObrigatorio;

    ItemChecklist(String chave, String rotulo, boolean documentoObrigatorio) {
        this.chave = chave;
        this.rotulo = rotulo;
        this.documentoObrigatorio = documentoObrigatorio;
    }

    public String chave() {
        return chave;
    }

    public String rotulo() {
        return rotulo;
    }

    /** Um dos 7 documentos do Anexo I (os demais itens são termo e LGPD). */
    public boolean documentoObrigatorio() {
        return documentoObrigatorio;
    }

    public static List<ItemChecklist> documentosObrigatorios() {
        return Arrays.stream(values()).filter(ItemChecklist::documentoObrigatorio).toList();
    }

    public static Optional<ItemChecklist> daChave(String chave) {
        return Arrays.stream(values()).filter(i -> i.chave.equals(chave)).findFirst();
    }

    /** Rótulo de um tipo de documento enviado (Documento.tipo): obrigatório, termo ou avulso. */
    public static String rotuloDocumento(String chave) {
        if ("outro".equals(chave)) {
            return "Outro documento";
        }
        return daChave(chave).filter(i -> i != LGPD).map(ItemChecklist::rotulo).orElse("Documento");
    }
}
