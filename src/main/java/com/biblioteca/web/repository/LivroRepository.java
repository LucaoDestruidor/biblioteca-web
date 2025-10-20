/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.biblioteca.web.repository;

/**
 *
 * @author Lucas
 */
// LivroRepository.java
import com.biblioteca.web.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {
    List<Livro> findByTituloContainingIgnoreCase(String titulo);
    Optional<Livro> findByIsbn(String isbn);
    List<Livro> findByDisponivelTrue();
    List<Livro> findByReservadoTrue();
    
    @Query("SELECT l FROM Livro l WHERE LOWER(l.titulo) LIKE LOWER(CONCAT('%', :titulo, '%')) OR LOWER(l.autor) LIKE LOWER(CONCAT('%', :autor, '%'))")
    List<Livro> findByTituloOrAutor(@Param("titulo") String titulo, @Param("autor") String autor);
}