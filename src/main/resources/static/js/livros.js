// Funções JavaScript para interatividade na página de livros
document.addEventListener('DOMContentLoaded', function() {
    // Adicionar confirmação para exclusão
    const deleteLinks = document.querySelectorAll('a[href*="/remover/"]');
    deleteLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            if (!confirm('Tem certeza que deseja remover este livro?')) {
                e.preventDefault();
            }
        });
    });
    
    // Validação de formulário
    const form = document.querySelector('form');
    if (form) {
        form.addEventListener('submit', function(e) {
            const titulo = document.getElementById('titulo').value.trim();
            const autor = document.getElementById('autor').value.trim();
            const isbn = document.getElementById('isbn').value.trim();
            
            if (!titulo || !autor || !isbn) {
                e.preventDefault();
                alert('Todos os campos são obrigatórios');
                return false;
            }
            
            // Validar formato do ISBN (exemplo simples)
            if (isbn.length < 10) {
                e.preventDefault();
                alert('ISBN deve ter pelo menos 10 caracteres');
                return false;
            }
        });
    }
});