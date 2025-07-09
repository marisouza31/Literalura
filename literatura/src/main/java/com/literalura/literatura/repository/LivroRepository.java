package com.literalura.literatura.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.literalura.literatura.model.Livro;
import java.util.List;

public interface LivroRepository extends JpaRepository<Livro, Long> {
    List<Livro> findByIdiomaIgnoreCase(String idioma);
}
