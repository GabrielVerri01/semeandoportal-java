package br.org.semeando.aviso;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AvisoRepository extends JpaRepository<Aviso, String> {
}
