package com.imildo.represencas_api_fingerprint_.repository;

import com.imildo.represencas_api_fingerprint_.model.Estudantes;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.LinkedList;

/**
 * @author Imildo Sitoe
 * @see org.springframework.data.repository.CrudRepository
 */
public interface EstudanteRepository extends CrudRepository<Estudantes, Integer> {

    @Query(value = "SELECT * FROM estudantes e JOIN inscricaos i on e.id_estudante = i.id_estudante JOIN disciplina_cursos dc on i.id_disciplina_curso = dc.id_disciplina_curso JOIN alocacaos a on dc.id_disciplina_curso = a.id_disciplina_curso WHERE a.id_alocacao=?", nativeQuery = true)
    LinkedList<Estudantes> findAll(@Param("id_alocacao") Integer id_alocacao);
}
