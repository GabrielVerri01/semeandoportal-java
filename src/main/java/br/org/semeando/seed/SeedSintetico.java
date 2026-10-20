package br.org.semeando.seed;

import br.org.semeando.academico.Aula;
import br.org.semeando.academico.Disciplina;
import br.org.semeando.academico.Frequencia;
import br.org.semeando.academico.Horario;
import br.org.semeando.academico.Nota;
import br.org.semeando.academico.Professor;
import br.org.semeando.academico.Turma;
import br.org.semeando.aluno.Aluno;
import br.org.semeando.aluno.FichaSaude;
import br.org.semeando.auditoria.Auditoria;
import br.org.semeando.aviso.Aviso;
import br.org.semeando.conta.Usuario;
import br.org.semeando.domain.Escola;
import br.org.semeando.domain.MetodoPagamento;
import br.org.semeando.domain.ModalidadeCobranca;
import br.org.semeando.domain.OrigemPlano;
import br.org.semeando.domain.Role;
import br.org.semeando.domain.Serie;
import br.org.semeando.domain.Sexo;
import br.org.semeando.domain.StatusMatricula;
import br.org.semeando.domain.StatusPlano;
import br.org.semeando.domain.TipoSocio;
import br.org.semeando.domain.Turno;
import br.org.semeando.domain.cobranca.Boleto;
import br.org.semeando.domain.cobranca.Encargos;
import br.org.semeando.domain.cobranca.Parcelas;
import br.org.semeando.domain.cobranca.TabelaOficial;
import br.org.semeando.domain.matricula.Checklist;
import br.org.semeando.domain.matricula.ItemChecklist;
import br.org.semeando.financeiro.Socio;
import br.org.semeando.financeiro.TabelaPreco;
import br.org.semeando.matricula.Matricula;
import br.org.semeando.matricula.Matricula.RegistroHistorico;
import br.org.semeando.matricula.PlanoCobranca;
import br.org.semeando.matricula.PlanoCobranca.PagamentoImportado;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Seed do Semeando Digital — DADOS 100% SINTÉTICOS (regra inviolável do projeto:
 * nenhum dado real de criança, responsável ou da instituição entra no repositório).
 *
 * <p>Porte fiel de {@code portal-aluno/prisma/seed.ts}: mesma semente e mesma ordem de sorteios.
 * Cria: secretaria, 11 professores, 9 turmas do ano atual + 9 do próximo, tabela oficial
 * (Anexo II) dos dois anos, 150 alunos com ficha completa, matrículas do ano atual ativas
 * com planos e parcelas (incluindo contratos LEGADOS divergentes — o caso dos R$ 310),
 * renovação do próximo ano em todos os estados, notas, frequência, avisos e auditoria.
 *
 * <p>Só existe nos perfis {@code seed} (linha de comando) e {@code test}: apaga todos os dados.
 */
@Component
@Profile({"seed", "test"})
public class SeedSintetico {

    public static final String SENHA_SECRETARIA = "secretaria123";
    public static final String SENHA_PROFESSOR = "professor123";
    public static final String SENHA_ALUNO = "aluno123";

    // --- Nomes sintéticos ---------------------------------------------------------------------------
    private static final List<String> NOMES_F = List.of("Ana", "Alice", "Beatriz", "Cecília", "Clara", "Elisa", "Giovana", "Helena", "Isabela", "Júlia", "Lara", "Laura", "Lívia", "Luiza", "Manuela", "Maria", "Marina", "Mariana", "Olívia", "Rafaela", "Sara", "Sofia", "Valentina", "Yasmin");
    private static final List<String> NOMES_M = List.of("Arthur", "Benjamin", "Bernardo", "Caio", "Davi", "Enzo", "Gabriel", "Gael", "Heitor", "Henrique", "Isaac", "João", "Lucas", "Miguel", "Murilo", "Nicolas", "Noah", "Pedro", "Rafael", "Samuel", "Theo", "Vicente");
    private static final List<String> SOBRENOMES = List.of("Almeida", "Araújo", "Barbosa", "Cardoso", "Carvalho", "Castro", "Costa", "Dias", "Ferreira", "Gomes", "Lima", "Martins", "Melo", "Mendes", "Moraes", "Nascimento", "Oliveira", "Pereira", "Ribeiro", "Rocha", "Santos", "Silva", "Sousa", "Teixeira", "Vieira");
    private static final List<String> ADULTOS_F = List.of("Adriana", "Camila", "Daniela", "Fernanda", "Jaqueline", "Juliana", "Luciana", "Patrícia", "Renata", "Simone", "Tatiane", "Vanessa");
    private static final List<String> ADULTOS_M = List.of("Anderson", "Carlos", "Diego", "Eduardo", "Fábio", "Leandro", "Marcelo", "Paulo", "Ricardo", "Rodrigo", "Sérgio", "Thiago");
    private static final List<String> BAIRROS = List.of("Pontal da Ilha", "Vila Nova", "Recanto Verde", "Jardim das Sementes", "Morada do Sol");
    private static final List<String> RUAS = List.of("Rua das Acácias", "Rua dos Ipês", "Travessa do Sol", "Rua da Paz", "Avenida das Palmeiras");
    private static final List<String> HOSPITAIS = List.of("Hospital Infantil Fictício", "UPA Fictícia Pontal", "Hospital Sintético São Lucas");

    // --- Estrutura da escola ---------------------------------------------------------------------------
    private record DisciplinaFundamental(String nome, int semana) {
    }

    private static final List<DisciplinaFundamental> DISC_FUND = List.of(
            new DisciplinaFundamental("Língua Portuguesa", 6),
            new DisciplinaFundamental("Matemática", 6),
            new DisciplinaFundamental("Ciências", 3),
            new DisciplinaFundamental("História", 2),
            new DisciplinaFundamental("Geografia", 2),
            new DisciplinaFundamental("Ensino Religioso", 2),
            new DisciplinaFundamental("Arte", 2),
            new DisciplinaFundamental("Educação Física", 2));
    private static final String ARTE = "Arte";
    private static final String ED_FISICA = "Educação Física";
    private static final String ROTINA_INFANTIL = "Rotina pedagógica (Educação Infantil)";

    private static final Map<Serie, Integer> IDADE_DA_SERIE = porSerie(2, 3, 4, 5, 6, 7, 8, 9, 10);
    private static final Map<Serie, Integer> ALUNOS_POR_TURMA = porSerie(14, 16, 17, 18, 18, 17, 17, 17, 16);
    private static final String[][] SLOTS_MANHA = {{"07:30", "08:20"}, {"08:20", "09:10"}, {"09:10", "10:00"}, {"10:20", "11:10"}, {"11:10", "12:00"}};
    private static final String[] SLOT_TARDE = {"13:30", "17:30"};
    private static final List<String> NOMES_REGENTES = List.of("Carla Nogueira", "Débora Freitas", "Elaine Rocha", "Fabiana Moura", "Gisele Campos", "Ana Paula Moreira", "Rosana Pires", "Kátia Lopes", "Márcia Teles");

    /** Alunos fixos, com cenários conhecidos para testar (os demais são gerados). */
    private record Fixo(String nome, Serie serie, Sexo sexo, boolean legado, StatusMatricula renovacao, String obs) {
    }

    private static final List<Fixo> FIXOS = List.of(
            new Fixo("Sofia Rabelo Dias", Serie.ANO_3, Sexo.FEMININO, true, StatusMatricula.PENDENTE, "3º ano, contrato LEGADO de R$ 310 (divergente); renovação pendente"),
            new Fixo("Pedro Souza Lima", Serie.PRE_II, Sexo.MASCULINO, false, StatusMatricula.EM_ANALISE, "Pré II (taxa de associado) → 1º ano (mensalidade R$ 310)"),
            new Fixo("Laura Mendes Carvalho", Serie.ANO_5, Sexo.FEMININO, false, null, "5º ano: conclui, sem renovação"),
            new Fixo("Theo Ribeiro Araújo", Serie.MATERNAL_II, Sexo.MASCULINO, false, StatusMatricula.ATIVA, "Infantil, renovação ativa (ficha de sócio)"));

    /** Um login de teste impresso ao final. */
    public record Login(String perfil, String login, String senha, String obs) {
    }

    public record Resumo(int totalAlunos, int legadosCriados, String periodoAtual, String periodoProximo,
            List<Login> logins) {
    }

    private record ProfessorSeed(Professor professor, String email) {
    }

    private record AulaSeed(Aula aula, Set<Integer> dias) {
    }

    private final EntityManager em;

    public SeedSintetico(EntityManager em) {
        this.em = em;
    }

    @Transactional
    public Resumo executar(LocalDate hoje) {
        Rng rng = new Rng(20261013);
        int ano = hoje.getYear();
        String pAtual = String.valueOf(ano);
        String pProx = String.valueOf(ano + 1);
        limpar();

        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder(10);
        String hSec = bcrypt.encode(SENHA_SECRETARIA);
        String hProf = bcrypt.encode(SENHA_PROFESSOR);
        String hAluno = bcrypt.encode(SENHA_ALUNO);

        Usuario secretaria = new Usuario("Helena Duarte", "secretaria@" + Escola.DOMINIO_EMAIL, hSec, Role.SECRETARIA);
        em.persist(secretaria);

        // Tabela oficial (Anexo II) — ano atual e próximo ------------------------------------------------
        Map<String, TabelaPreco> tabelas = new HashMap<>();
        for (int a : new int[] {ano, ano + 1}) {
            for (TabelaOficial.Linha l : TabelaOficial.anexoII()) {
                TabelaPreco t = new TabelaPreco(a, l);
                em.persist(t);
                tabelas.put(a + ":" + l.serie(), t);
            }
        }

        // Disciplinas e professores ------------------------------------------------------------------------
        Map<String, Disciplina> disciplinas = new HashMap<>();
        List<String> nomesDisciplinas = new ArrayList<>(DISC_FUND.stream().map(DisciplinaFundamental::nome).toList());
        nomesDisciplinas.add(ROTINA_INFANTIL);
        for (String nome : nomesDisciplinas) {
            Disciplina d = new Disciplina(nome);
            em.persist(d);
            disciplinas.put(nome, d);
        }

        Map<Serie, ProfessorSeed> regentes = new EnumMap<>(Serie.class);
        for (Serie serie : Serie.values()) {
            String formacao = serie.infantil() ? "Pedagogia — Educação Infantil" : "Pedagogia — Anos Iniciais";
            regentes.put(serie, criarProfessor(NOMES_REGENTES.get(serie.ordinal()), formacao, hProf));
        }
        ProfessorSeed profArte = criarProfessor("Bruno Saraiva", "Licenciatura em Artes Visuais", hProf);
        ProfessorSeed profEdFisica = criarProfessor("Diego Andrade", "Licenciatura em Educação Física", hProf);

        // Turmas (ano atual com grade; próximo ano sem grade) ------------------------------------------------
        Map<String, Turma> turmas = new HashMap<>();
        Map<Serie, List<AulaSeed>> aulasAtuais = new EnumMap<>(Serie.class);
        Set<String> ocupado = new HashSet<>(); // professor|dia|slot (turno da manhã)
        for (String periodo : List.of(pAtual, pProx)) {
            for (Serie serie : Serie.values()) {
                Turno turno = serie.infantil() ? Turno.TARDE : Turno.MANHA;
                Turma turma = new Turma(serie.nome() + " A", serie, turno, periodo, 20);
                em.persist(turma);
                turmas.put(periodo + ":" + serie, turma);
                if (!periodo.equals(pAtual)) {
                    continue;
                }
                aulasAtuais.put(serie, criarGrade(turma, serie, disciplinas, regentes.get(serie).professor(),
                        profArte.professor(), profEdFisica.professor(), ocupado));
            }
        }

        // Alunos ------------------------------------------------------------------------------------------
        Map<Integer, Integer> seqMatricula = new HashMap<>();
        Map<String, Integer> seqDocumento = new HashMap<>();
        Map<String, Socio> socios = new HashMap<>(); // cpf do responsável → sócio
        List<Login> logins = new ArrayList<>();
        List<Login> fixosResumo = new ArrayList<>();
        int legadosCriados = 0;
        int totalAlunos = 0;

        for (Serie serie : Serie.values()) {
            Turma turmaAtual = turmas.get(pAtual + ":" + serie);
            List<Fixo> fixos = FIXOS.stream().filter(f -> f.serie() == serie).toList();
            boolean infantil = serie.infantil();

            for (int i = 0; i < ALUNOS_POR_TURMA.get(serie); i++) {
                Fixo fixo = i < fixos.size() ? fixos.get(i) : null;
                Sexo sexo = fixo != null ? fixo.sexo() : (rng.chance(0.5) ? Sexo.FEMININO : Sexo.MASCULINO);
                String sob1 = rng.sortear(SOBRENOMES);
                String sob2 = rng.sortear(SOBRENOMES);
                String nome = fixo != null
                        ? fixo.nome()
                        : rng.sortear(sexo == Sexo.FEMININO ? NOMES_F : NOMES_M) + " " + sob1 + " " + sob2;
                String mae = rng.sortear(ADULTOS_F) + " " + sob1;
                String pai = rng.sortear(ADULTOS_M) + " " + sob2;
                int idade = IDADE_DA_SERIE.get(serie);
                int mesNascimento = 3 + (int) (rng.proximo() * 9);
                int diaNascimento = 1 + (int) (rng.proximo() * 27);
                LocalDate nascimento = LocalDate.of(ano - idade - 1, mesNascimento, diaNascimento);
                int anoIngresso = Math.max(2017, ano - (int) (rng.proximo() * Math.min(idade - 1, 6)));
                int seq = seqMatricula.merge(anoIngresso, 1, Integer::sum);
                String numeroMatricula = anoIngresso + "%06d".formatted(seq);
                String responsavelCpf = gerarCpf(rng);
                String responsavelTelefone = telefone(rng);
                String bairro = rng.sortear(BAIRROS);
                String endereco = rng.sortear(RUAS) + ", " + (int) rng.entre(1, 400);

                Usuario user = new Usuario(nome, null, hAluno, Role.ALUNO);
                em.persist(user);
                Aluno aluno = new Aluno(user, numeroMatricula, nascimento);
                aluno.setCpf(infantil && rng.chance(0.5) ? null : gerarCpf(rng));
                aluno.setSexo(sexo);
                aluno.setTelefone(telefone(rng));
                aluno.setCep("65%03d-000".formatted((int) rng.entre(0, 999)));
                aluno.setEndereco(endereco);
                aluno.setBairro(bairro);
                aluno.setFiliacao1(mae);
                aluno.setFiliacao2(rng.chance(0.85) ? pai : null);
                aluno.setResponsavelNome(mae);
                aluno.setResponsavelParentesco("Mãe");
                aluno.setResponsavelCpf(responsavelCpf);
                aluno.setResponsavelTelefone(responsavelTelefone);
                aluno.setResponsavelEmail(semAcento(mae.split(" ")[0]) + "." + numeroMatricula.substring(numeroMatricula.length() - 4) + "@exemplo.com");
                aluno.setHospitalEmergencia(rng.sortear(HOSPITAIS));
                aluno.setOrigemEscolaParticular(rng.chance(0.15));
                aluno.setAutorizaUsoImagem(rng.chance(0.7));
                aluno.registrarConsentimento(instante(LocalDate.of(ano, 1, 15)), "1.0");
                aluno.setCreatedAt(instante(LocalDate.of(anoIngresso, 1, 10)));
                aluno.adicionarAutorizado(mae, "Mãe", responsavelCpf, responsavelTelefone);
                if (rng.chance(0.6)) {
                    String nomeAutorizado = rng.sortear(ADULTOS_F) + " " + rng.sortear(SOBRENOMES);
                    String parentesco = rng.sortear(List.of("Avó", "Tia", "Madrinha"));
                    String documento = gerarCpf(rng);
                    aluno.adicionarAutorizado(nomeAutorizado, parentesco, documento, telefone(rng));
                }
                FichaSaude saude = null;
                if (rng.chance(0.25)) {
                    saude = new FichaSaude(aluno);
                    saude.setAlergia(rng.chance(0.4) ? rng.sortear(List.of("Dipirona", "Amendoim", "Picada de inseto")) : null);
                    saude.setRestricaoAlimentar(rng.chance(0.5) ? rng.sortear(List.of("Intolerância à lactose", "Doença celíaca")) : null);
                    saude.setAcompanhamentos(rng.chance(0.3) ? List.of(rng.sortear(List.of("FONO", "PSICOLOGIA", "TO", "PSICOPEDAGOGIA"))) : List.of());
                    saude.setDificuldadeFala(rng.chance(0.1));
                }
                for (String tipo : List.of("certidao", "vacina", "comprovante")) {
                    aluno.adicionarDocumento(tipo, "seed/" + numeroMatricula + "/" + tipo + ".pdf", tipo + ".pdf").setConferido(true);
                }
                em.persist(aluno);
                if (saude != null) {
                    em.persist(saude);
                }
                totalAlunos++;

                // Sócio do Instituto = o responsável de criança do Infantil (no ano atual ou no próximo).
                Serie proximaSerie = serie.proxima().orElse(null);
                Socio socio = null;
                if (infantil || (proximaSerie != null && proximaSerie.infantil())) {
                    socio = socios.get(responsavelCpf);
                    if (socio == null) {
                        socio = new Socio(mae, responsavelCpf, LocalDate.of(anoIngresso, 1, 20));
                        socio.setEstadoCivil(rng.sortear(List.of("Casada", "Solteira", "União estável")));
                        socio.setProfissao(rng.sortear(List.of("Autônoma", "Professora", "Comerciante", "Técnica de enfermagem")));
                        socio.setEndereco(endereco);
                        socio.setBairro(bairro);
                        socio.setContato(responsavelTelefone);
                        socio.setTipo(rng.sortear(List.of(TipoSocio.EFETIVO, TipoSocio.EFETIVO, TipoSocio.MANTENEDOR_CONTRIBUINTE, TipoSocio.MANTENEDOR_APOIADOR)));
                        socio.setFilhosCriancas(1);
                        em.persist(socio);
                        socios.put(responsavelCpf, socio);
                    }
                }

                // Matrícula do ano atual (ativa) + plano + parcelas ---------------------------------------------
                TabelaPreco tab = tabelas.get(ano + ":" + serie);
                ModalidadeCobranca modalidade = serie.modalidade();
                // O caso do Semeando: contratos do 2º ao 5º ano feitos no Word com o valor do 1º ano (R$ 310).
                boolean legado = fixo != null
                        ? fixo.legado()
                        : List.of(Serie.ANO_2, Serie.ANO_3, Serie.ANO_4, Serie.ANO_5).contains(serie)
                                && legadosCriados < 8 && rng.chance(0.12);
                if (legado) {
                    legadosCriados++;
                }
                int valorPlano = legado ? 31000 : tab.getValorParcelaCentavos();
                Integer descontoPlano = legado ? Integer.valueOf(27900) : tab.getValorComDescontoCentavos();
                Instant criadoEm = instante(LocalDate.of(ano - 1, 12, 1 + (int) (rng.proximo() * 20)));
                Matricula matricula = Matricula.importar(aluno, turmaAtual, pAtual, StatusMatricula.ATIVA,
                        Checklist.completo(), criadoEm, List.of(
                                new RegistroHistorico(null, StatusMatricula.PENDENTE, "Ficha de matrícula preenchida.", secretaria.getId(), criadoEm),
                                new RegistroHistorico(StatusMatricula.PENDENTE, StatusMatricula.EM_ANALISE, "Documentos entregues para conferência.", secretaria.getId(), dias(criadoEm, 3)),
                                new RegistroHistorico(StatusMatricula.EM_ANALISE, StatusMatricula.ATIVA, "Documentos conferidos e termo assinado.", secretaria.getId(), dias(criadoEm, 5))));
                em.persist(matricula);
                PlanoCobranca plano = PlanoCobranca.importar(matricula, modalidade,
                        proximoNumeroDocumento(seqDocumento, modalidade, ano), legado ? null : tab,
                        modalidade == ModalidadeCobranca.TAXA_ASSOCIADO ? socio : null, valorPlano, descontoPlano,
                        tab.getQuantidadeParcelas(), legado ? OrigemPlano.LEGADO : OrigemPlano.SISTEMA,
                        StatusPlano.ATIVO, dias(criadoEm, 5));
                List<LocalDate> vencimentos = Parcelas.vencimentos(new Parcelas.Parametros(ano,
                        tab.getQuantidadeParcelas(), tab.getPrimeiroMes(), tab.getDiaVencimento()));
                long qtdVencidas = vencimentos.stream().filter(v -> v.isBefore(hoje)).count();
                boolean inadimplente = rng.chance(0.12);
                for (int idx = 0; idx < vencimentos.size(); idx++) {
                    LocalDate venc = vencimentos.get(idx);
                    boolean vencida = venc.isBefore(hoje);
                    boolean pago = vencida && !(inadimplente && idx == qtdVencidas - 1);
                    int atraso = pago && rng.chance(0.15) ? (int) rng.entre(2, 20) : 0;
                    LocalDate pagoEm = pago ? venc.plusDays(atraso != 0 ? atraso : -(int) rng.entre(0, 5)) : null;
                    PagamentoImportado pagamento = null;
                    if (pago) {
                        MetodoPagamento metodo = rng.sortear(modalidade == ModalidadeCobranca.MENSALIDADE
                                ? List.of(MetodoPagamento.BOLETO, MetodoPagamento.BOLETO, MetodoPagamento.PIX)
                                : List.of(MetodoPagamento.PIX, MetodoPagamento.DINHEIRO));
                        int valorPago = Encargos.valorDevido(valorPlano, descontoPlano, venc, pagoEm).totalCentavos();
                        pagamento = new PagamentoImportado(metodo, instante(pagoEm), valorPago);
                    }
                    String boletoLinha = !pago && modalidade.emiteBoleto() ? boleto(rng, valorPlano, venc) : null;
                    plano.importarParcela(idx + 1, Parcelas.referencia(modalidade, idx + 1, tab.getQuantidadeParcelas(), venc),
                            venc, boletoLinha, pagamento);
                }
                em.persist(plano);

                // Notas (Fundamental) e frequência (últimas 8 semanas) ------------------------------------------
                double desempenho = rng.entre(5.5, 9.5);
                double assiduidade = rng.chance(0.08) ? 0.7 : rng.entre(0.86, 0.99);
                for (AulaSeed a : aulasAtuais.get(serie)) {
                    if (!infantil) {
                        double vies = rng.entre(-1, 1);
                        for (int b = 1; b <= 3; b++) {
                            double bruta = Math.min(10, Math.max(3, desempenho + vies + rng.entre(-1, 1)));
                            em.persist(new Nota(aluno, a.aula(), b, BigDecimal.valueOf(Math.round(bruta * 2) / 2.0)));
                        }
                    }
                    for (int d = 56; d >= 1; d--) {
                        LocalDate data = hoje.minusDays(d);
                        if (a.dias().contains(data.getDayOfWeek().getValue())) {
                            em.persist(new Frequencia(aluno, a.aula(), data, rng.proximo() < assiduidade));
                        }
                    }
                }

                // Renovação para o próximo ano --------------------------------------------------------------------
                StatusMatricula statusRenov = null;
                if (proximaSerie != null) {
                    if (fixo != null) {
                        statusRenov = fixo.renovacao();
                    } else {
                        double r = rng.proximo();
                        statusRenov = r < 0.38 ? StatusMatricula.PENDENTE
                                : r < 0.58 ? StatusMatricula.EM_ANALISE
                                : r < 0.83 ? StatusMatricula.ATIVA
                                : r < 0.88 ? StatusMatricula.CANCELADA
                                : null;
                    }
                }
                if (proximaSerie != null && statusRenov != null) {
                    renovar(rng, hoje, aluno, proximaSerie, statusRenov, socio, secretaria,
                            turmas.get(pProx + ":" + proximaSerie), tabelas.get((ano + 1) + ":" + proximaSerie),
                            seqDocumento, pProx);
                }

                if (fixo != null) {
                    fixosResumo.add(new Login("ALUNO", numeroMatricula + " (" + nome.split(" ")[0] + ")", SENHA_ALUNO, fixo.obs()));
                }
                em.flush();
                em.clear();
            }
        }

        // Avisos e auditoria inicial ------------------------------------------------------------------------
        Instant agora = instante(hoje);
        em.persist(new Aviso("Renovação de matrículas " + pProx,
                "A renovação para " + pProx + " acontece de 1º de novembro a 31 de janeiro. Separe os 7 documentos da lista.",
                Role.ALUNO, dias(agora, -2)));
        em.persist(new Aviso("Reunião de pais e responsáveis", "Entrega dos boletins do 3º bimestre no sábado, às 9h.",
                null, dias(agora, -4)));
        em.persist(new Aviso("Festa da Primavera", "Apresentações das turmas do Infantil e do Fundamental no pátio.",
                null, dias(agora, -7)));
        em.persist(new Aviso("Prazo de lançamento de notas", "As notas do 3º bimestre devem ser lançadas até sexta-feira.",
                Role.PROFESSOR, dias(agora, -1)));
        em.persist(new Auditoria(secretaria.getId(), secretaria.getNome(), "sistema.implantacao", "Sistema", null,
                Map.of("origem", "seed sintético")));

        logins.add(new Login("SECRETARIA", secretaria.getEmail(), SENHA_SECRETARIA, ""));
        logins.add(new Login("PROFESSOR", regentes.get(Serie.ANO_3).email(), SENHA_PROFESSOR, "regente do 3º Ano A"));
        logins.add(new Login("PROFESSOR", regentes.get(Serie.PRE_II).email(), SENHA_PROFESSOR, "regente do Pré II A (Infantil)"));
        logins.add(new Login("PROFESSOR", profEdFisica.email(), SENHA_PROFESSOR, "Educação Física (1º ao 5º ano)"));
        logins.addAll(fixosResumo);
        return new Resumo(totalAlunos, legadosCriados, pAtual, pProx, logins);
    }

    private void renovar(Rng rng, LocalDate hoje, Aluno aluno, Serie proximaSerie, StatusMatricula statusRenov,
            Socio socio, Usuario secretaria, Turma turmaProx, TabelaPreco tabProx, Map<String, Integer> seqDocumento,
            String pProx) {
        ModalidadeCobranca modProx = proximaSerie.modalidade();
        Checklist checklist = switch (statusRenov) {
            case ATIVA -> Checklist.completo();
            case EM_ANALISE -> Checklist.completo()
                    .com(ItemChecklist.TERMO, false)
                    .com(ItemChecklist.LGPD, false)
                    .com(ItemChecklist.VACINA, false);
            default -> Checklist.vazio()
                    .com(ItemChecklist.DOC_RESPONSAVEL, true)
                    .com(ItemChecklist.COMPROVANTE, rng.chance(0.5))
                    .com(ItemChecklist.CERTIDAO, rng.chance(0.6));
        };
        Instant inicio = instante(hoje.minusDays((int) rng.entre(1, 15)));
        String autor = secretaria.getId();
        List<RegistroHistorico> historico = new ArrayList<>();
        historico.add(new RegistroHistorico(null, StatusMatricula.PENDENTE,
                "Renovação " + pProx + " aberta (" + proximaSerie.nome() + ").", autor, inicio));
        if (statusRenov == StatusMatricula.EM_ANALISE || statusRenov == StatusMatricula.ATIVA) {
            historico.add(new RegistroHistorico(StatusMatricula.PENDENTE, StatusMatricula.EM_ANALISE,
                    "Documentos entregues para conferência.", autor, dias(inicio, 1)));
        }
        if (statusRenov == StatusMatricula.ATIVA) {
            historico.add(new RegistroHistorico(StatusMatricula.EM_ANALISE, StatusMatricula.ATIVA,
                    "Documentos conferidos e termo assinado.", autor, dias(inicio, 2)));
        }
        if (statusRenov == StatusMatricula.CANCELADA) {
            historico.add(new RegistroHistorico(StatusMatricula.PENDENTE, StatusMatricula.CANCELADA,
                    "Cancelada pela secretaria: família mudou de bairro.", autor, dias(inicio, 2)));
        }
        Matricula m2 = Matricula.importar(aluno, turmaProx, pProx, statusRenov, checklist, inicio, historico);
        em.persist(m2);
        int anoProx = Integer.parseInt(pProx);
        PlanoCobranca plano2 = PlanoCobranca.importar(m2, modProx, proximoNumeroDocumento(seqDocumento, modProx, anoProx),
                tabProx, modProx == ModalidadeCobranca.TAXA_ASSOCIADO ? socio : null,
                tabProx.getValorParcelaCentavos(), tabProx.getValorComDescontoCentavos(), tabProx.getQuantidadeParcelas(),
                OrigemPlano.SISTEMA, statusRenov == StatusMatricula.CANCELADA ? StatusPlano.CANCELADO : StatusPlano.ATIVO,
                statusRenov == StatusMatricula.ATIVA ? dias(inicio, 2) : null);
        if (statusRenov == StatusMatricula.ATIVA) {
            List<LocalDate> vencimentos = Parcelas.vencimentos(new Parcelas.Parametros(anoProx,
                    tabProx.getQuantidadeParcelas(), tabProx.getPrimeiroMes(), tabProx.getDiaVencimento()));
            for (int idx = 0; idx < vencimentos.size(); idx++) {
                LocalDate venc = vencimentos.get(idx);
                String boletoLinha = modProx.emiteBoleto() ? boleto(rng, tabProx.getValorParcelaCentavos(), venc) : null;
                plano2.importarParcela(idx + 1, Parcelas.referencia(modProx, idx + 1, tabProx.getQuantidadeParcelas(), venc),
                        venc, boletoLinha, null);
            }
        }
        em.persist(plano2);
    }

    /** Grade semanal da turma do ano atual. Infantil: a regente conduz a rotina diária (uma "aula" por dia). */
    private List<AulaSeed> criarGrade(Turma turma, Serie serie, Map<String, Disciplina> disciplinas, Professor regente,
            Professor profArte, Professor profEdFisica, Set<String> ocupado) {
        String sala = "Sala " + (serie.ordinal() + 1);
        if (serie.infantil()) {
            Aula aula = new Aula(turma, disciplinas.get(ROTINA_INFANTIL), regente);
            em.persist(aula);
            for (int d = 1; d <= 5; d++) {
                em.persist(new Horario(aula, d, SLOT_TARDE[0], SLOT_TARDE[1], sala));
            }
            return List.of(new AulaSeed(aula, Set.of(1, 2, 3, 4, 5)));
        }

        // Fundamental: regente polivalente + especialistas de Arte e Educação Física.
        Map<String, AulaSeed> aulas = new LinkedHashMap<>();
        for (DisciplinaFundamental d : DISC_FUND) {
            Professor professor = d.nome().equals(ARTE) ? profArte : d.nome().equals(ED_FISICA) ? profEdFisica : regente;
            Aula aula = new Aula(turma, disciplinas.get(d.nome()), professor);
            em.persist(aula);
            aulas.put(d.nome(), new AulaSeed(aula, new HashSet<>()));
        }
        // Especialistas primeiro, em dias diferentes por turma (Arte e Ed. Física atendem as 5 turmas
        // e não podem estar em duas salas ao mesmo tempo); a regente preenche o restante.
        int k = serie.ordinal() - Serie.ANO_1.ordinal(); // 0..4
        Map<String, String> fixosEspecialistas = Map.of(
                ((k % 5) + 1) + "|0", ARTE,
                ((k % 5) + 1) + "|1", ARTE,
                (((k + 2) % 5) + 1) + "|3", ED_FISICA,
                (((k + 2) % 5) + 1) + "|4", ED_FISICA);
        Map<String, Integer> restante = new LinkedHashMap<>();
        DISC_FUND.stream()
                .filter(d -> !d.nome().equals(ARTE) && !d.nome().equals(ED_FISICA))
                .forEach(d -> restante.put(d.nome(), d.semana()));
        for (int diaSemana = 1; diaSemana <= 5; diaSemana++) {
            Map<String, Integer> noDia = new HashMap<>();
            for (int slot = 0; slot < SLOTS_MANHA.length; slot++) {
                String escolhida = fixosEspecialistas.get(diaSemana + "|" + slot);
                if (escolhida == null) {
                    List<String> ordem = restante.entrySet().stream()
                            .filter(e -> e.getValue() > 0)
                            .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder()))
                            .map(Map.Entry::getKey)
                            .toList();
                    escolhida = ordem.stream().filter(n -> noDia.getOrDefault(n, 0) < 2).findFirst()
                            .orElse(ordem.isEmpty() ? null : ordem.getFirst());
                    if (escolhida == null) {
                        continue;
                    }
                    restante.merge(escolhida, -1, Integer::sum);
                }
                noDia.merge(escolhida, 1, Integer::sum);
                AulaSeed a = aulas.get(escolhida);
                String chave = a.aula().getProfessor().getId() + "|" + diaSemana + "|" + slot;
                if (!ocupado.add(chave)) {
                    throw new IllegalStateException("Conflito de horário: %s (%s) %d/%d".formatted(escolhida, serie.nome(), diaSemana, slot));
                }
                a.dias().add(diaSemana);
                em.persist(new Horario(a.aula(), diaSemana, SLOTS_MANHA[slot][0], SLOTS_MANHA[slot][1], sala));
            }
        }
        return List.copyOf(aulas.values());
    }

    private ProfessorSeed criarProfessor(String nome, String formacao, String senhaHash) {
        String[] partes = nome.split(" ");
        String email = semAcento(partes[0]) + "." + semAcento(partes[partes.length - 1]) + "@" + Escola.DOMINIO_EMAIL;
        Usuario u = new Usuario(nome, email, senhaHash, Role.PROFESSOR);
        em.persist(u);
        Professor p = new Professor(u, formacao);
        em.persist(p);
        return new ProfessorSeed(p, email);
    }

    private void limpar() {
        em.createNativeQuery("""
                TRUNCATE TABLE "Auditoria", "HistoricoMatricula", "Cobranca", "PlanoCobranca", "Socio",
                  "TabelaPreco", "Matricula", "Nota", "Frequencia", "Documento", "PessoaAutorizada",
                  "FichaSaude", "Horario", "Aula", "Turma", "Disciplina", "Aluno", "Professor", "Aviso", "User"
                """).executeUpdate();
    }

    private static String proximoNumeroDocumento(Map<String, Integer> seq, ModalidadeCobranca modalidade, int anoLetivo) {
        int n = seq.merge(modalidade + ":" + anoLetivo, 1, Integer::sum);
        return Parcelas.numeroDocumento(modalidade, anoLetivo, n);
    }

    private static String gerarCpf(Rng rng) {
        int[] d = new int[11];
        for (int i = 0; i < 9; i++) {
            d[i] = (int) (rng.proximo() * 10);
        }
        d[9] = digitoCpf(d, 9);
        d[10] = digitoCpf(d, 10);
        StringBuilder sb = new StringBuilder();
        for (int x : d) {
            sb.append(x);
        }
        return sb.toString();
    }

    private static int digitoCpf(int[] d, int n) {
        int s = 0;
        for (int i = 0; i < n; i++) {
            s += d[i] * (n + 1 - i);
        }
        int r = (s * 10) % 11;
        return r == 10 ? 0 : r;
    }

    private static String telefone(Rng rng) {
        return "989" + (long) rng.entre(10000000, 99999999);
    }

    private static String boleto(Rng rng, int valorCentavos, LocalDate vencimento) {
        StringBuilder campoLivre = new StringBuilder();
        for (int i = 0; i < 25; i++) {
            campoLivre.append((int) (rng.proximo() * 10));
        }
        return Boleto.codigoParaLinha(Boleto.montarCodigoBarras(Boleto.BANCO_FICTICIO, valorCentavos, vencimento, campoLivre.toString()));
    }

    private static String semAcento(String s) {
        return Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase();
    }

    private static Instant instante(LocalDate data) {
        return data.atStartOfDay(ZoneOffset.UTC).toInstant();
    }

    private static Instant dias(Instant base, int dias) {
        return base.plus(Duration.ofDays(dias));
    }

    private static Map<Serie, Integer> porSerie(int... valores) {
        Map<Serie, Integer> m = new EnumMap<>(Serie.class);
        for (Serie s : Serie.values()) {
            m.put(s, valores[s.ordinal()]);
        }
        return m;
    }

    /** Aleatoriedade determinística: mulberry32, idêntico ao {@code criarRng} do seed.ts. */
    static final class Rng {

        private int a;

        Rng(int semente) {
            this.a = semente;
        }

        double proximo() {
            a += 0x6d2b79f5;
            int t = (a ^ (a >>> 15)) * (1 | a);
            t = (t + ((t ^ (t >>> 7)) * (61 | t))) ^ t;
            return ((t ^ (t >>> 14)) & 0xFFFFFFFFL) / 4294967296.0;
        }

        boolean chance(double p) {
            return proximo() < p;
        }

        double entre(double min, double max) {
            return min + proximo() * (max - min);
        }

        <T> T sortear(List<T> lista) {
            return lista.get((int) (proximo() * lista.size()));
        }
    }
}
