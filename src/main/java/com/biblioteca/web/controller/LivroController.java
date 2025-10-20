/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.biblioteca.web.controller;

/**
 *
 * @author Lucas
 */
import com.biblioteca.web.model.Livro;
import com.biblioteca.web.model.Usuario;
import com.biblioteca.web.repository.LivroRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/livros")
public class LivroController {
    
    @Autowired
    private LivroRepository livroRepository;
    
    @GetMapping
    public String listarLivros(HttpSession session, Model model, 
                              @RequestParam(required = false) String busca) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) return "redirect:/";
        
        List<Livro> livros;
        if (busca != null && !busca.trim().isEmpty()) {
            livros = livroRepository.findByTituloContainingIgnoreCase(busca);
        } else {
            livros = livroRepository.findAll();
        }
        
        model.addAttribute("livros", livros);
        model.addAttribute("usuario", usuario);
        model.addAttribute("busca", busca);
        return "livros";
    }
    
    @GetMapping("/novo")
    public String novoLivroForm(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            return "redirect:/";
        }
        
        if (!usuario.getTipo().equals("bibliotecario")) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado: Apenas bibliotecários podem cadastrar livros");
            return "redirect:/livros";
        }
        
        model.addAttribute("livro", new Livro());
        return "form-livro";
    }
    
    @PostMapping("/salvar")
    public String salvarLivro(@ModelAttribute Livro livro, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null || !usuario.getTipo().equals("bibliotecario")) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado: Apenas bibliotecários podem cadastrar livros");
            return "redirect:/livros";
        }
        
        // Validação de campos obrigatórios no backend
        if (livro.getTitulo() == null || livro.getTitulo().trim().isEmpty() ||
            livro.getAutor() == null || livro.getAutor().trim().isEmpty() ||
            livro.getIsbn() == null || livro.getIsbn().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Todos os campos são obrigatórios");
            return "redirect:/livros/novo";
        }
        
        // Validar formato do ISBN
        if (livro.getIsbn().length() < 10) {
            redirectAttributes.addFlashAttribute("mensagemErro", "ISBN deve ter pelo menos 10 caracteres");
            return "redirect:/livros/novo";
        }
        
        // Verificar se ISBN já existe
        Optional<Livro> livroExistente = livroRepository.findByIsbn(livro.getIsbn());
        if (livroExistente.isPresent() && 
            (livro.getId() == null || !livroExistente.get().getId().equals(livro.getId()))) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Já existe um livro com este ISBN");
            return "redirect:/livros/novo";
        }
        
        // Garantir valores padrão para campos booleanos
        if (livro.getDisponivel() == null) {
            livro.setDisponivel(true);
        }
        if (livro.getReservado() == null) {
            livro.setReservado(false);
        }
        
        livroRepository.save(livro);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Livro salvo com sucesso!");
        return "redirect:/livros";
    }
    
    @GetMapping("/editar/{id}")
    public String editarLivroForm(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null || !usuario.getTipo().equals("bibliotecario")) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado: Apenas bibliotecários podem editar livros");
            return "redirect:/livros";
        }
        
        Optional<Livro> livro = livroRepository.findById(id);
        if (livro.isPresent()) {
            model.addAttribute("livro", livro.get());
            return "form-livro";
        }
        
        redirectAttributes.addFlashAttribute("mensagemErro", "Livro não encontrado");
        return "redirect:/livros";
    }
    
    @GetMapping("/reservar/{id}")
    public String reservarLivro(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("mensagemErro", "É necessário fazer login para reservar livros");
            return "redirect:/";
        }
        
        Optional<Livro> livroOpt = livroRepository.findById(id);
        if (livroOpt.isPresent()) {
            Livro livro = livroOpt.get();
            if (livro.getDisponivel() && !livro.getReservado()) {
                livro.setDisponivel(false);
                livro.setReservado(true);
                livroRepository.save(livro);
                redirectAttributes.addFlashAttribute("mensagemSucesso", "Livro reservado com sucesso!");
            } else {
                redirectAttributes.addFlashAttribute("mensagemErro", "Livro não disponível para reserva");
            }
        } else {
            redirectAttributes.addFlashAttribute("mensagemErro", "Livro não encontrado");
        }
        return "redirect:/livros";
    }
    
    @GetMapping("/remover/{id}")
    public String removerLivro(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null || !usuario.getTipo().equals("bibliotecario")) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado: Apenas bibliotecários podem remover livros");
            return "redirect:/livros";
        }
        
        Optional<Livro> livroOpt = livroRepository.findById(id);
        if (livroOpt.isPresent()) {
            Livro livro = livroOpt.get();
            
            // Verificar se o livro está emprestado ou reservado
            if (!livro.getDisponivel() || livro.getReservado()) {
                redirectAttributes.addFlashAttribute("mensagemErro", "Não é possível remover um livro que está emprestado ou reservado");
                return "redirect:/livros";
            }
            
            livroRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Livro removido com sucesso!");
        } else {
            redirectAttributes.addFlashAttribute("mensagemErro", "Livro não encontrado");
        }
        return "redirect:/livros";
    }
}