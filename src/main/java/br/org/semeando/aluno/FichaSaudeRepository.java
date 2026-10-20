package br.org.semeando.aluno;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FichaSaudeRepository extends JpaRepository<FichaSaude, String> {

    Optional<FichaSaude> findByAlunoId(String alunoId);
}
