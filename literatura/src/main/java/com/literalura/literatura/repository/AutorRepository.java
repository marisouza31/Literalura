package com.literalura.literatura.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.literalura.literatura.model.Autor;
import java.util.List;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    Autor findByNome(String nome);

    List<Autor> findByAnoNascimento(Integer anoNascimento);
}
