package br.org.semeando.financeiro;

import br.org.semeando.domain.Serie;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TabelaPrecoRepository extends JpaRepository<TabelaPreco, String> {

    Optional<TabelaPreco> findByAnoLetivoAndSerie(int anoLetivo, Serie serie);
}
