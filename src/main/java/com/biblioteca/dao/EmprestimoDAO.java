package com.biblioteca.dao;

import com.biblioteca.db.ConexaoDB;
import com.biblioteca.model.Emprestimo;
import com.biblioteca.model.Aluno;
import com.biblioteca.model.Livro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmprestimoDAO {
    private AlunoDAO alunoDAO;
    private LivroDAO livroDAO;

    public EmprestimoDAO() {
        this.alunoDAO = new AlunoDAO();
        this.livroDAO = new LivroDAO();
    }

    public void inserir(Emprestimo emprestimo) throws SQLException {
        String sql = "INSERT INTO emprestimo (aluno_id, livro_id, data_emprestimo, data_devolucao) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, emprestimo.getAluno().getId());
            stmt.setInt(2, emprestimo.getLivro().getId());
            stmt.setDate(3, new java.sql.Date(emprestimo.getDataEmprestimo().getTime()));
            stmt.setDate(4, new java.sql.Date(emprestimo.getDataDevolucao().getTime()));
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    emprestimo.setId(rs.getInt(1));
                }
            }
        }
    }

    public void atualizar(Emprestimo emprestimo) throws SQLException {
        String sql = "UPDATE emprestimo SET aluno_id = ?, livro_id = ?, data_emprestimo = ?, data_devolucao = ? WHERE id = ?";
        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, emprestimo.getAluno().getId());
            stmt.setInt(2, emprestimo.getLivro().getId());
            stmt.setDate(3, new java.sql.Date(emprestimo.getDataEmprestimo().getTime()));
            stmt.setDate(4, new java.sql.Date(emprestimo.getDataDevolucao().getTime()));
            stmt.setInt(5, emprestimo.getId());
            stmt.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM emprestimo WHERE id = ?";
        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public Emprestimo buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM emprestimo WHERE id = ?";
        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Aluno aluno = alunoDAO.buscarPorId(rs.getInt("aluno_id"));
                    Livro livro = livroDAO.buscarPorId(rs.getInt("livro_id"));
                    return new Emprestimo(
                        rs.getInt("id"),
                        aluno,
                        livro,
                        rs.getDate("data_emprestimo"),
                        rs.getDate("data_devolucao")
                    );
                }
            }
        }
        return null;
    }

    public List<Emprestimo> listarTodos() throws SQLException {
        List<Emprestimo> emprestimos = new ArrayList<>();
        String sql = "SELECT * FROM emprestimo ORDER BY data_emprestimo DESC";
        try (Connection conn = ConexaoDB.getConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Aluno aluno = alunoDAO.buscarPorId(rs.getInt("aluno_id"));
                Livro livro = livroDAO.buscarPorId(rs.getInt("livro_id"));
                emprestimos.add(new Emprestimo(
                    rs.getInt("id"),
                    aluno,
                    livro,
                    rs.getDate("data_emprestimo"),
                    rs.getDate("data_devolucao")
                ));
            }
        }
        return emprestimos;
    }
} 