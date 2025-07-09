package com.literalura.literatura.component;

import com.literalura.literatura.model.Autor;
import com.literalura.literatura.model.Livro;
import com.literalura.literatura.repository.AutorRepository;
import com.literalura.literatura.repository.LivroRepository;
import com.literalura.literatura.service.GutendexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class MenuApp implements CommandLineRunner {

    @Autowired
    private GutendexService gutendexService;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private AutorRepository autorRepository;

    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void run(String... args) {
        int opcao = -1;
        do {
            System.out.println("""
                \nMenu de opções:
                1 - Buscar livro pelo título
                2 - Listar livros registrados
                3 - Listar autores registrados
                4 - Listar autores por ano de nascimento
                5 - Listar livros por idioma
                0 - Sair
                """);
            System.out.print("Digite a opção desejada: ");
            String input = scanner.nextLine();
            try {
                opcao = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Opção inválida. Digite um número.");
                continue;
            }

            switch (opcao) {
                case 1 -> buscarLivro();
                case 2 -> listarLivros();
                case 3 -> listarAutores();
                case 4 -> listarAutoresPorAno();
                case 5 -> listarLivrosPorIdioma();
                case 0 -> System.out.println("Encerrando...");
                default -> System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    private void buscarLivro() {
        System.out.print("Digite o título do livro: ");
        String titulo = scanner.nextLine().trim();
        if (titulo.isEmpty()) {
            System.out.println("Título não pode ser vazio.");
            return;
        }
        String resposta = gutendexService.buscarLivros(titulo);
        System.out.println(resposta);
    }

    private void listarLivros() {
        List<Livro> livros = livroRepository.findAll();
        if (livros.isEmpty()) {
            System.out.println("Nenhum livro registrado.");
            return;
        }
        System.out.println("Livros registrados:");
        livros.forEach(l -> System.out.println("- " + l.getTitulo()));
    }

    private void listarAutores() {
        List<Autor> autores = autorRepository.findAll();
        if (autores.isEmpty()) {
            System.out.println("Nenhum autor registrado.");
            return;
        }
        System.out.println("Autores registrados:");
        autores.forEach(a -> System.out.printf("- %s (Nascimento: %s, Falecimento: %s)%n",
                a.getNome(),
                a.getAnoNascimento() != null ? a.getAnoNascimento() : "desconhecido",
                a.getAnoFalecimento() != null ? a.getAnoFalecimento() : "desconhecido"));
    }

    private void listarAutoresPorAno() {
        System.out.print("Digite o ano de nascimento: ");
        String anoStr = scanner.nextLine();
        Integer ano;
        try {
            ano = Integer.parseInt(anoStr);
        } catch (NumberFormatException e) {
            System.out.println("Ano inválido.");
            return;
        }
        List<Autor> autores = autorRepository.findByAnoNascimento(ano);
        if (autores.isEmpty()) {
            System.out.println("Nenhum autor encontrado para o ano: " + ano);
            return;
        }
        System.out.println("Autores nascidos em " + ano + ":");
        autores.forEach(a -> System.out.printf("- %s (Nascimento: %d, Falecimento: %s)%n",
                a.getNome(),
                a.getAnoNascimento(),
                a.getAnoFalecimento() != null ? a.getAnoFalecimento() : "desconhecido"));
    }

    private void listarLivrosPorIdioma() {
        System.out.println("""
                Escolha um idioma:
                1 - Espanhol
                2 - Inglês
                3 - Francês
                4 - Português
                """);
        System.out.print("Opção: ");
        String opcaoIdioma = scanner.nextLine();

        String idioma = switch (opcaoIdioma) {
            case "1" -> "espanhol";
            case "2" -> "ingles";
            case "3" -> "frances";
            case "4" -> "portugues";
            default -> null;
        };

        if (idioma == null) {
            System.out.println("Idioma inválido.");
            return;
        }

        List<Livro> livros = livroRepository.findByIdiomaIgnoreCase(idioma);
        if (livros.isEmpty()) {
            System.out.println("Nenhum livro encontrado no idioma: " + idioma);
            return;
        }
        System.out.println("Livros em " + idioma + ":");
        livros.forEach(l -> System.out.println("- " + l.getTitulo()));
    }
}