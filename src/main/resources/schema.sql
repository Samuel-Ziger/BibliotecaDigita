-- Criação da tabela aluno
CREATE TABLE IF NOT EXISTS aluno (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    matricula VARCHAR(20) NOT NULL UNIQUE
);

-- Criação da tabela livro
CREATE TABLE IF NOT EXISTS livro (
    id SERIAL PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    autor VARCHAR(100) NOT NULL,
    editora VARCHAR(100) NOT NULL
);

-- Criação da tabela emprestimo
CREATE TABLE IF NOT EXISTS emprestimo (
    id SERIAL PRIMARY KEY,
    aluno_id INTEGER NOT NULL,
    livro_id INTEGER NOT NULL,
    data_emprestimo DATE NOT NULL,
    data_devolucao DATE,
    FOREIGN KEY (aluno_id) REFERENCES aluno(id),
    FOREIGN KEY (livro_id) REFERENCES livro(id)
);

-- Índices para melhorar o desempenho das consultas
CREATE INDEX idx_aluno_nome ON aluno(nome);
CREATE INDEX idx_livro_titulo ON livro(titulo);
CREATE INDEX idx_emprestimo_aluno_id ON emprestimo(aluno_id);
CREATE INDEX idx_emprestimo_livro_id ON emprestimo(livro_id);
CREATE INDEX idx_emprestimo_data_emprestimo ON emprestimo(data_emprestimo); 