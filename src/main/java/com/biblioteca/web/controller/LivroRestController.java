/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.biblioteca.web.controller;

/**
 *
 * @author Lucas
 */
// LivroRestController.java
import com.biblioteca.web.model.Livro;
import com.biblioteca.web.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/livros")
public class LivroRestController {
    
    @Autowired
    private LivroRepository livroRepository;
    
    @GetMapping
    public List<Livro> listarTodos() {
        return livroRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Livro> obterPorId(@PathVariable Long id) {
        Optional<Livro> livro = livroRepository.findById(id);
        return livro.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Livro> criar(@RequestBody Livro livro) {
        if (livroRepository.findByIsbn(livro.getIsbn()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        Livro novoLivro = livroRepository.save(livro);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoLivro);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Livro> atualizar(@PathVariable Long id, @RequestBody Livro livroAtualizado) {
        if (!livroRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        Optional<Livro> livroComIsbn = livroRepository.findByIsbn(livroAtualizado.getIsbn());
        if (livroComIsbn.isPresent() && !livroComIsbn.get().getId().equals(id)) {
            return ResponseEntity.badRequest().build();
        }
        
        livroAtualizado.setId(id);
        Livro livro = livroRepository.save(livroAtualizado);
        return ResponseEntity.ok(livro);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (!livroRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        livroRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/buscar")
    public List<Livro> buscarPorTitulo(@RequestParam String titulo) {
        return livroRepository.findByTituloContainingIgnoreCase(titulo);
    }
}