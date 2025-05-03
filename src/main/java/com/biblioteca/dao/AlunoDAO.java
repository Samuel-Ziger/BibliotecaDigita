package com.biblioteca.dao;

import com.biblioteca.db.ConexaoDB;
import com.biblioteca.model.Aluno;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlunoDAO {
    public void inserir(Aluno aluno) throws SQLException {
        // Validação de entrada
        if (aluno.getNome() == null || aluno.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do aluno não pode ser vazio ou nulo.");
        }
        if (aluno.getMatricula() == null || aluno.getMatricula().trim().isEmpty()) {
            throw new IllegalArgumentException("Matrícula do aluno não pode ser vazia ou nula.");
        }
        
        String sql = "INSERT INTO aluno (nome, matricula) VALUES (?, ?)";
        Connection conn = null;
        try {
            conn = ConexaoDB.getConexao();
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, aluno.getNome());
                stmt.setString(2, aluno.getMatricula());
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        aluno.setId(rs.getInt(1));
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

    public void atualizar(Aluno aluno) throws SQLException {
        String sql = "UPDATE aluno SET nome = ?, matricula = ? WHERE id = ?";
        Connection conn = null;
        try {
            conn = ConexaoDB.getConexao();
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, aluno.getNome());
                stmt.setString(2, aluno.getMatricula());
                stmt.setInt(3, aluno.getId());
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
        String sql = "DELETE FROM aluno WHERE id = ?";
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

    public Aluno buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nome, matricula FROM aluno WHERE id = ?";
        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Aluno(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("matricula")
                    );
                }
            }
        }
        return null;
    }

    public List<Aluno> listarTodos(int limit, int offset) throws SQLException {
        List<Aluno> alunos = new ArrayList<>();
        String sql = "SELECT id, nome, matricula FROM aluno ORDER BY nome LIMIT ? OFFSET ?";
        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    alunos.add(new Aluno(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("matricula")
                    ));
                }
            }
        }
        return alunos;
    }
} 