package br.org.semeando.seed;

import java.time.LocalDate;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * {@code ./mvnw spring-boot:run -Dspring-boot.run.profiles=seed}: popula o banco com dados
 * sintéticos, imprime os logins de teste e encerra (equivale ao {@code npm run db:seed}).
 */
@Component
@Profile("seed")
class SeedRunner implements ApplicationRunner {

    private final SeedSintetico seed;
    private final ConfigurableApplicationContext contexto;

    SeedRunner(SeedSintetico seed, ConfigurableApplicationContext contexto) {
        this.seed = seed;
        this.contexto = contexto;
    }

    @Override
    public void run(ApplicationArguments args) {
        SeedSintetico.Resumo r = seed.executar(LocalDate.now());
        StringBuilder out = new StringBuilder();
        out.append("%n✔ Seed concluído: %d alunos, %d contratos legados divergentes (R$ 310).%n"
                .formatted(r.totalAlunos(), r.legadosCriados()));
        out.append("  Ano letivo %s, renovação %s.%n%nLogins de teste (dados sintéticos):%n%n"
                .formatted(r.periodoAtual(), r.periodoProximo()));
        out.append("  %-11s %-42s %-14s OBS%n".formatted("PERFIL", "LOGIN", "SENHA"));
        for (SeedSintetico.Login l : r.logins()) {
            out.append("  %-11s %-42s %-14s %s%n".formatted(l.perfil(), l.login(), l.senha(), l.obs()));
        }
        out.append("%n  Demais alunos: qualquer nº de matrícula com a senha %s.%n".formatted(SeedSintetico.SENHA_ALUNO));
        System.out.println(out);
        System.exit(SpringApplication.exit(contexto, () -> 0));
    }
}
