package br.org.semeando.seed;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.semeando.TestcontainersConfiguration;
import br.org.semeando.domain.Escola;
import br.org.semeando.domain.OrigemPlano;
import br.org.semeando.domain.StatusMatricula;
import br.org.semeando.domain.cobranca.Boleto;
import br.org.semeando.domain.matricula.Checklist;
import br.org.semeando.matricula.Matricula;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/** Fase 1: o seed roda sobre o schema do Flyway e produz a mesma escola sintética da versão Next.js. */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class SeedSinteticoTest {

    /** Data fixa: o seed depende de "hoje" (parcelas vencidas, frequência, renovação). */
    private static final LocalDate HOJE = LocalDate.of(2026, 10, 20);

    @Autowired
    SeedSintetico seed;

    @Autowired
    EntityManager em;

    SeedSintetico.Resumo resumo;

    @BeforeAll
    void popular() {
        resumo = seed.executar(HOJE);
    }

    private long contar(String jpql) {
        return em.createQuery(jpql, Long.class).getSingleResult();
    }

    @Test
    void criaAEscolaCompleta() {
        assertThat(resumo.totalAlunos()).isEqualTo(150);
        assertThat(contar("select count(a) from Aluno a")).isEqualTo(150);
        assertThat(contar("select count(p) from Professor p")).isEqualTo(11);
        assertThat(contar("select count(t) from Turma t")).isEqualTo(18);
        assertThat(contar("select count(t) from TabelaPreco t")).isEqualTo(18);
        assertThat(contar("select count(m) from Matricula m where m.periodo = '2026' and m.status = ATIVA")).isEqualTo(150);
        assertThat(contar("select count(a) from Aviso a")).isEqualTo(4);
    }

    @Test
    void criaContratosLegadosDivergentesDaTabela() {
        // RN-09: o caso dos R$ 310 — plano LEGADO, sem vínculo com a tabela, valor do 1º ano.
        List<Integer> valores = em.createQuery(
                "select p.valorParcelaCentavos from PlanoCobranca p where p.origem = :origem", Integer.class)
                .setParameter("origem", OrigemPlano.LEGADO)
                .getResultList();
        assertThat(valores).hasSize(resumo.legadosCriados()).isNotEmpty().containsOnly(31000);
        assertThat(contar("select count(p) from PlanoCobranca p where p.origem = LEGADO and p.tabelaPreco is not null"))
                .isZero();
    }

    @Test
    void cadaMatriculaTemHistoricoCoerenteComOStatus() {
        List<Object[]> linhas = em.createQuery("""
                select m.status, h.para from Matricula m join m.historico h
                where h.createdAt = (select max(h2.createdAt) from HistoricoMatricula h2 where h2.matricula = m)
                """, Object[].class).getResultList();
        assertThat(linhas).isNotEmpty().allSatisfy(l -> assertThat(l[1]).isEqualTo(l[0]));
    }

    @Test
    void checklistDaMatriculaAtivaEstaCompleto() {
        List<Matricula> ativas = em.createQuery(
                "select m from Matricula m where m.status = :s", Matricula.class)
                .setParameter("s", StatusMatricula.ATIVA)
                .getResultList();
        assertThat(ativas).allSatisfy(m -> assertThat(m.getChecklist()).isEqualTo(Checklist.completo()));
    }

    @Test
    void boletosEmAbertoTemLinhaDigitavelValida() {
        List<String> linhas = em.createQuery(
                "select c.boletoLinha from Cobranca c where c.boletoLinha is not null", String.class)
                .getResultList();
        assertThat(linhas).isNotEmpty().allSatisfy(l -> assertThat(Boleto.linhaValida(l)).isTrue());
    }

    @Test
    void loginsDeTesteConferemComBcrypt() {
        String hash = em.createQuery("select u.senhaHash from Usuario u where u.email = :e", String.class)
                .setParameter("e", "secretaria@" + Escola.DOMINIO_EMAIL)
                .getSingleResult();
        assertThat(new BCryptPasswordEncoder().matches(SeedSintetico.SENHA_SECRETARIA, hash)).isTrue();
        assertThat(resumo.logins()).extracting(SeedSintetico.Login::login)
                .contains("rosana.pires@" + Escola.DOMINIO_EMAIL)
                .anyMatch(l -> l.endsWith("(Sofia)"));
    }
}
