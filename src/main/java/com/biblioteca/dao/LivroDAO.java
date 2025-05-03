package com.biblioteca.dao;

import com.biblioteca.db.ConexaoDB;
import com.biblioteca.model.Livro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LivroDAO {
    public void inserir(Livro livro) throws SQLException {
        String sql = "INSERT INTO livro (titulo, autor, editora) VALUES (?, ?, ?)";
        Connection conn = null;
        try {
            conn = ConexaoDB.getConexao();
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, livro.getTitulo());
                stmt.setString(2, livro.getAutor());
                stmt.setString(3, livro.getEditora());
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        livro.setId(rs.getInt(1));
                    }
                }
            }
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public void atualizar(Livro livro) throws SQLException {
        String sql = "UPDATE livro SET titulo = ?, autor = ?, editora = ? WHERE id = ?";
        Connection conn = null;
        try {
            conn = ConexaoDB.getConexao();
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, livro.getTitulo());
                stmt.setString(2, livro.getAutor());
                stmt.setString(3, livro.getEditora());
                stmt.setInt(4, livro.getId());
                stmt.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM livro WHERE id = ?";
        Connection conn = null;
        try {
            conn = ConexaoDB.getConexao();
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public Livro buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, titulo, autor, editora FROM livro WHERE id = ?";
        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Livro(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("editora")
                    );
                }
            }
        }
        return null;
    }

    public List<Livro> listarTodos(int limit, int offset) throws SQLException {
        List<Livro> livros = new ArrayList<>();
        String sql = "SELECT id, titulo, autor, editora FROM livro ORDER BY titulo LIMIT ? OFFSET ?";
        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    livros.add(new Livro(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("editora")
                    ));
                }
            }
        }
        return livros;
    }
} 