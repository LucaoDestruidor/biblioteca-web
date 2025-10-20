package com.biblioteca.web.controller;

import com.biblioteca.web.model.Usuario;
import com.biblioteca.web.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public String listarUsuarios(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) {
            return "redirect:/";
        }

        // Verificar permissões
        if (!usuarioLogado.getTipo().equals("professor") && !usuarioLogado.getTipo().equals("bibliotecario")) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado: Permissão insuficiente");
            return "redirect:/menu";
        }

        List<Usuario> usuarios = usuarioRepository.findAll();
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("usuario", usuarioLogado);
        return "usuarios";
    }

    @GetMapping("/novo")
    public String novoUsuarioForm(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) {
            return "redirect:/";
        }
        
        if (!usuarioLogado.getTipo().equals("professor") && !usuarioLogado.getTipo().equals("bibliotecario")) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado: Permissão insuficiente");
            return "redirect:/menu";
        }

        model.addAttribute("usuario", new Usuario());
        return "form-usuario";
    }

    @PostMapping("/salvar")
    public String salvarUsuario(@ModelAttribute Usuario usuario,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null || (!usuarioLogado.getTipo().equals("professor") && !usuarioLogado.getTipo().equals("bibliotecario"))) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado: Permissão insuficiente");
            return "redirect:/usuarios";
        }

        try {
            // Validar campos obrigatórios
            if (usuario.getNome() == null || usuario.getNome().trim().isEmpty() ||
                usuario.getLogin() == null || usuario.getLogin().trim().isEmpty() ||
                usuario.getSenha() == null || usuario.getSenha().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("mensagemErro", "Todos os campos são obrigatórios");
                return "redirect:/usuarios/novo";
            }

            // Validar matrícula para alunos
            if (usuario.getTipo().equals("aluno") && (usuario.getMatricula() == null || usuario.getMatricula().trim().isEmpty())) {
                redirectAttributes.addFlashAttribute("mensagemErro", "Matrícula é obrigatória para alunos");
                return "redirect:/usuarios/novo";
            }

            // Definir tipo padrão como aluno para professores
            if (usuarioLogado.getTipo().equals("professor")) {
                usuario.setTipo("aluno");
            }

            // Verificar se login já existe
            Optional<Usuario> usuarioExistente = usuarioRepository.findByLogin(usuario.getLogin());
            if (usuarioExistente.isPresent() && 
                (usuario.getId() == null || !usuarioExistente.get().getId().equals(usuario.getId()))) {
                redirectAttributes.addFlashAttribute("mensagemErro", "Já existe um usuário com este login");
                return "redirect:/usuarios/novo";
            }

            // Verificar matrícula para alunos
            if (usuario.getTipo().equals("aluno") && usuario.getMatricula() != null && !usuario.getMatricula().isEmpty()) {
                Optional<Usuario> matriculaExistente = usuarioRepository.findByMatricula(usuario.getMatricula());
                if (matriculaExistente.isPresent() && 
                    (usuario.getId() == null || !matriculaExistente.get().getId().equals(usuario.getId()))) {
                    redirectAttributes.addFlashAttribute("mensagemErro", "Já existe um aluno com esta matrícula");
                    return "redirect:/usuarios/novo";
                }
            }

            usuarioRepository.save(usuario);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Usuário salvo com sucesso!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao salvar usuário: " + e.getMessage());
            return "redirect:/usuarios/novo";
        }

        return "redirect:/usuarios";
    }

    @GetMapping("/editar/{id}")
    public String editarUsuarioForm(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null || (!usuarioLogado.getTipo().equals("professor") && !usuarioLogado.getTipo().equals("bibliotecario"))) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado: Permissão insuficiente");
            return "redirect:/usuarios";
        }

        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isPresent()) {
            model.addAttribute("usuario", usuario.get());
            return "form-usuario";
        }
        
        redirectAttributes.addFlashAttribute("mensagemErro", "Usuário não encontrado");
        return "redirect:/usuarios";
    }
}