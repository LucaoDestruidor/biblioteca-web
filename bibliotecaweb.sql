CREATE DATABASE IF NOT EXISTS biblioteca_web;

USE biblioteca_web;

-- Tabela de usuários
CREATE TABLE IF NOT EXISTS usuario (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    login VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    matricula VARCHAR(50)
);

-- Tabela de livros
CREATE TABLE IF NOT EXISTS livro (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    autor VARCHAR(255) NOT NULL,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    disponivel BOOLEAN DEFAULT TRUE,
    reservado BOOLEAN DEFAULT FALSE
);

-- Tabela de empréstimos
CREATE TABLE IF NOT EXISTS emprestimo (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    livro_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    data_emprestimo DATE NOT NULL,
    data_devolucao DATE,
    status VARCHAR(50) NOT NULL,
    FOREIGN KEY (livro_id) REFERENCES livro(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
);

-- Inserir dados iniciais
INSERT INTO usuario (nome, login, senha, tipo, matricula) VALUES
('Lucas', 'lucas', '123', 'bibliotecario', NULL),
('Marina', 'marina', '123', 'professor', NULL),
('João Silva', 'joao', '123', 'aluno', '2023001'),
('Jéssica Oliveira', 'jessica', '123', 'aluno', '2023002');

INSERT INTO livro (titulo, autor, isbn, disponivel, reservado) VALUES
('Dom Casmurro', 'Machado de Assis', '978-85-7232-144-9', TRUE, FALSE),
('O Cortiço', 'Aluísio Azevedo', '978-85-7232-145-6', FALSE, TRUE),
('Memórias Póstumas de Brás Cubas', 'Machado de Assis', '978-85-7232-146-3', TRUE, FALSE),
('O Alienista', 'Machado de Assis', '978-85-7232-147-0', TRUE, FALSE),
('Iracema', 'José de Alencar', '978-85-7232-148-7', TRUE, FALSE);

INSERT INTO emprestimo (livro_id, usuario_id, data_emprestimo, data_devolucao, status) VALUES
(1, 3, '2024-10-15', NULL, 'ativo'),
(2, 4, '2024-10-10', NULL, 'reservado'),
(3, 3, '2024-10-05', '2024-10-12', 'devolvido');