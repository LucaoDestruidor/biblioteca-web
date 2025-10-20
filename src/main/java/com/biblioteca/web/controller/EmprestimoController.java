package com.biblioteca.web.controller;

import com.biblioteca.web.model.Emprestimo;
import com.biblioteca.web.model.Livro;
import com.biblioteca.web.model.Usuario;
import com.biblioteca.web.repository.EmprestimoRepository;
import com.biblioteca.web.repository.LivroRepository;
import com.biblioteca.web.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/emprestimos")
public class EmprestimoController {

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public String listarEmprestimos(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            return "redirect:/";
        }

        try {
            List<Emprestimo> emprestimos = emprestimoRepository.findAllWithLivroAndUsuario();
            model.addAttribute("emprestimos", emprestimos);
            model.addAttribute("usuario", usuario);
            return "emprestimos";
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao carregar empréstimos: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/novo")
    public String novoEmprestimoForm(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null || !usuario.getTipo().equals("bibliotecario")) {
            return "redirect:/emprestimos";
        }

        try {
            List<Livro> livrosDisponiveis = livroRepository.findByDisponivelTrue();
            List<Usuario> usuarios = usuarioRepository.findAll();

            model.addAttribute("livros", livrosDisponiveis);
            model.addAttribute("usuarios", usuarios);
            return "form-emprestimo";
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao carregar formulário: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/novo")
    public String criarEmprestimo(@RequestParam Long livroId,
                                 @RequestParam Long usuarioId,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null || !usuarioLogado.getTipo().equals("bibliotecario")) {
            return "redirect:/emprestimos";
        }

        try {
            Optional<Livro> livroOpt = livroRepository.findById(livroId);
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);

            if (livroOpt.isPresent() && usuarioOpt.isPresent()) {
                Livro livro = livroOpt.get();
                Usuario usuario = usuarioOpt.get();

                if (livro.getDisponivel()) {
                    Emprestimo emprestimo = new Emprestimo();
                    emprestimo.setLivro(livro);
                    emprestimo.setUsuario(usuario);
                    emprestimo.setDataEmprestimo(LocalDate.now());
                    emprestimo.setStatus("ativo");

                    // Atualizar status do livro
                    livro.setDisponivel(false);
                    livroRepository.save(livro);

                    emprestimoRepository.save(emprestimo);
                    redirectAttributes.addFlashAttribute("mensagemSucesso", "Empréstimo realizado com sucesso!");
                } else {
                    redirectAttributes.addFlashAttribute("mensagemErro", "Livro não disponível para empréstimo");
                }
            } else {
                redirectAttributes.addFlashAttribute("mensagemErro", "Livro ou usuário não encontrado");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao criar empréstimo: " + e.getMessage());
        }

        return "redirect:/emprestimos";
    }

    @GetMapping("/devolver/{id}")
    public String devolverLivro(@PathVariable Long id,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null || !usuario.getTipo().equals("bibliotecario")) {
            return "redirect:/emprestimos";
        }

        try {
            Optional<Emprestimo> emprestimoOpt = emprestimoRepository.findById(id);
            if (emprestimoOpt.isPresent()) {
                Emprestimo emprestimo = emprestimoOpt.get();
                emprestimo.setStatus("devolvido");
                emprestimo.setDataDevolucao(LocalDate.now());

                // Liberar o livro
                Livro livro = emprestimo.getLivro();
                livro.setDisponivel(true);
                livro.setReservado(false);

                livroRepository.save(livro);
                emprestimoRepository.save(emprestimo);

                redirectAttributes.addFlashAttribute("mensagemSucesso", "Livro devolvido com sucesso!");
            } else {
                redirectAttributes.addFlashAttribute("mensagemErro", "Empréstimo não encontrado");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao devolver livro: " + e.getMessage());
        }

        return "redirect:/emprestimos";
    }
}