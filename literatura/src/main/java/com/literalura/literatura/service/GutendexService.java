package com.literalura.literatura.service;

import com.literalura.literatura.dto.GutendexAuthor;
import com.literalura.literatura.dto.GutendexBook;
import com.literalura.literatura.dto.GutendexResponse;
import com.literalura.literatura.model.Autor;
import com.literalura.literatura.model.Livro;
import com.literalura.literatura.repository.AutorRepository;
import com.literalura.literatura.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class GutendexService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private AutorRepository autorRepository;

    public String buscarLivros(String termoBusca) {
        String url = "https://gutendex.com/books/?search=" + termoBusca.replace(" ", "%20");

        GutendexResponse response = restTemplate.getForObject(url, GutendexResponse.class);

        if (response == null || response.getResults() == null || response.getResults().isEmpty()) {
            return "Nenhum livro encontrado para o termo: " + termoBusca;
        }

        StringBuilder sb = new StringBuilder();

        for (GutendexBook book : response.getResults()) {
            Livro livro = converterParaLivro(book);

            // Verifica se o livro já existe pelo título (evita duplicação)
            List<Livro> livrosExistentes = livroRepository.findAll()
                    .stream()
                    .filter(l -> l.getTitulo().equalsIgnoreCase(livro.getTitulo()))
                    .toList();

            if (livrosExistentes.isEmpty()) {
                // Verifica e salva autores (sem duplicar)
                List<Autor> autoresAssociados = new ArrayList<>();
                for (Autor autor : livro.getAutores()) {
                    Autor autorExistente = autorRepository.findByNome(autor.getNome());
                    if (autorExistente != null) {
                        autoresAssociados.add(autorExistente);
                    } else {
                        autoresAssociados.add(autorRepository.save(autor));
                    }
                }
                livro.setAutores(autoresAssociados);

                livroRepository.save(livro);
                sb.append("Livro salvo: ").append(livro.getTitulo()).append("\n");
            } else {
                sb.append("Livro já existe no banco: ").append(livro.getTitulo()).append("\n");
            }
        }

        return sb.toString();
    }

    private Livro converterParaLivro(GutendexBook book) {
        Livro livro = new Livro();
        livro.setTitulo(book.getTitle());

        if (book.getLanguages() != null && !book.getLanguages().isEmpty()) {
            livro.setIdioma(mapearIdioma(book.getLanguages().get(0)));
        } else {
            livro.setIdioma("desconhecido");
        }

        livro.setNumeroDownloads(book.getDownloadCount());

        Set<Autor> autores = new HashSet<>();
        if (book.getAuthors() != null) {
            for (GutendexAuthor ga : book.getAuthors()) {
                Autor autor = new Autor();
                autor.setNome(ga.getName());
                autor.setAnoNascimento(ga.getBirthYear());
                autor.setAnoFalecimento(ga.getDeathYear());
                autores.add(autor);
            }
        }
        livro.setAutores(new ArrayList<>(autores));
        return livro;
    }

    private String mapearIdioma(String codigo) {
        return switch (codigo.toLowerCase()) {
            case "en" -> "ingles";
            case "es" -> "espanhol";
            case "fr" -> "frances";
            case "pt" -> "portugues";
            default -> codigo;
        };
    }
}
