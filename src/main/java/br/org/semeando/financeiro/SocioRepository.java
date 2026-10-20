package br.org.semeando.financeiro;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocioRepository extends JpaRepository<Socio, String> {

    Optional<Socio> findByCpf(String cpf);
}
