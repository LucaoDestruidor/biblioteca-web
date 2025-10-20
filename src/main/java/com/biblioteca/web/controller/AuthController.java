/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.biblioteca.web.controller;

/**
 *
 * @author Lucas
 */
// AuthController.java
import com.biblioteca.web.model.Usuario;
import com.biblioteca.web.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Optional;

@Controller
public class AuthController {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @GetMapping("/")
    public String loginForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "index";
    }
    
    @PostMapping("/login")
    public String login(@ModelAttribute Usuario usuario, HttpSession session, RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByLogin(usuario.getLogin());
        
        if (usuarioOpt.isPresent() && usuarioOpt.get().getSenha().equals(usuario.getSenha())) {
            session.setAttribute("usuarioLogado", usuarioOpt.get());
            return "redirect:/menu";
        } else {
            redirectAttributes.addFlashAttribute("mensagemErro", "Login ou senha incorretos");
            return "redirect:/";
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
    
    @GetMapping("/menu")
    public String menu(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            return "redirect:/";
        }
        model.addAttribute("usuario", usuario);
        return "menu";
    }
}