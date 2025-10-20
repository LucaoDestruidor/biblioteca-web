/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.biblioteca.web.repository;

/**
 *
 * @author Lucas
 */
// EmprestimoRepository.java
import com.biblioteca.web.model.Emprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
    
    List<Emprestimo> findByUsuarioId(Long usuarioId);
    
    List<Emprestimo> findByLivroId(Long livroId);
    
    List<Emprestimo> findByStatus(String status);
    
    // Método para carregar relações eager
    @Query("SELECT e FROM Emprestimo e LEFT JOIN FETCH e.livro LEFT JOIN FETCH e.usuario")
    List<Emprestimo> findAllWithLivroAndUsuario();
}