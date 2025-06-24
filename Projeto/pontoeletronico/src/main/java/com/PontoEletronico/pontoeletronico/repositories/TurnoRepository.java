package com.PontoEletronico.pontoeletronico.repositories;

import com.PontoEletronico.pontoeletronico.models.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, Integer> {

    /**
     * Verifica se um turno com uma descrição específica já existe.
     *
     * @param descricaoTurno Descrição do turno.
     * @return true se existir, false caso contrário.
     */
    boolean existsByDescricaoTurno(String descricaoTurno);

    Turno findByDescricaoTurno(String descricaoTurno);

}

