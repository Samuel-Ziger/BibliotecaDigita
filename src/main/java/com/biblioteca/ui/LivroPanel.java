package com.biblioteca.ui;

import com.biblioteca.dao.LivroDAO;
import com.biblioteca.model.Livro;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class LivroPanel extends JPanel {
    private JTextField txtId;
    private JTextField txtTitulo;
    private JTextField txtAutor;
    private JTextField txtEditora;
    private JButton btnGravar;
    private JButton btnAtualizar;
    private JButton btnDeletar;
    private JButton btnLimpar;
    private JTable tabelaLivros;
    private LivroDAO livroDAO;

    public LivroPanel() {
        livroDAO = new LivroDAO();
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Campo ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        txtId = new JTextField(20);
        txtId.setEditable(false);
        add(txtId, gbc);

        // Campo Título
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Título:"), gbc);
        gbc.gridx = 1;
        txtTitulo = new JTextField(20);
        add(txtTitulo, gbc);

        // Campo Autor
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Autor:"), gbc);
        gbc.gridx = 1;
        txtAutor = new JTextField(20);
        add(txtAutor, gbc);

        // Campo Editora
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Editora:"), gbc);
        gbc.gridx = 1;
        txtEditora = new JTextField(20);
        add(txtEditora, gbc);

        // Painel de botões
        JPanel buttonPanel = new JPanel();
        btnGravar = new JButton("Gravar");
        btnAtualizar = new JButton("Atualizar");
        btnDeletar = new JButton("Deletar");
        btnLimpar = new JButton("Limpar");

        buttonPanel.add(btnGravar);
        buttonPanel.add(btnAtualizar);
        buttonPanel.add(btnDeletar);
        buttonPanel.add(btnLimpar);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        // Tabela de livros
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        tabelaLivros = new JTable();
        JScrollPane scrollPane = new JScrollPane(tabelaLivros);
        add(scrollPane, gbc);

        // Adicionando ação aos botões
        btnGravar.addActionListener(e -> gravarLivro());
        btnAtualizar.addActionListener(e -> atualizarLivro());
        btnDeletar.addActionListener(e -> deletarLivro());
        btnLimpar.addActionListener(e -> limparCampos());

        // Adicionando listener para seleção na tabela
        tabelaLivros.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tabelaLivros.getSelectedRow();
                if (selectedRow >= 0) {
                    carregarLivroSelecionado(selectedRow);
                }
            }
        });

        // Carregar dados iniciais
        carregarDadosTabela();
    }

    private void gravarLivro() {
        try {
            Livro livro = new Livro();
            livro.setTitulo(txtTitulo.getText());
            livro.setAutor(txtAutor.getText());
            livro.setEditora(txtEditora.getText());
            livroDAO.inserir(livro);
            JOptionPane.showMessageDialog(this, "Livro gravado com sucesso!");
            limparCampos();
            carregarDadosTabela();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao gravar livro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarLivro() {
        try {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selecione um livro para atualizar!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Livro livro = new Livro();
            livro.setId(Integer.parseInt(txtId.getText()));
            livro.setTitulo(txtTitulo.getText());
            livro.setAutor(txtAutor.getText());
            livro.setEditora(txtEditora.getText());
            livroDAO.atualizar(livro);
            JOptionPane.showMessageDialog(this, "Livro atualizado com sucesso!");
            limparCampos();
            carregarDadosTabela();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar livro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletarLivro() {
        try {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selecione um livro para deletar!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int id = Integer.parseInt(txtId.getText());
            livroDAO.deletar(id);
            JOptionPane.showMessageDialog(this, "Livro deletado com sucesso!");
            limparCampos();
            carregarDadosTabela();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao deletar livro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos() {
        txtId.setText("");
        txtTitulo.setText("");
        txtAutor.setText("");
        txtEditora.setText("");
    }

    private void carregarDadosTabela() {
        try {
            List<Livro> livros = livroDAO.listarTodos();
            String[] colunas = {"ID", "Título", "Autor", "Editora"};
            Object[][] dados = new Object[livros.size()][4];
            
            for (int i = 0; i < livros.size(); i++) {
                Livro livro = livros.get(i);
                dados[i][0] = livro.getId();
                dados[i][1] = livro.getTitulo();
                dados[i][2] = livro.getAutor();
                dados[i][3] = livro.getEditora();
            }
            
            tabelaLivros.setModel(new javax.swing.table.DefaultTableModel(dados, colunas) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarLivroSelecionado(int row) {
        txtId.setText(tabelaLivros.getValueAt(row, 0).toString());
        txtTitulo.setText(tabelaLivros.getValueAt(row, 1).toString());
        txtAutor.setText(tabelaLivros.getValueAt(row, 2).toString());
        txtEditora.setText(tabelaLivros.getValueAt(row, 3).toString());
    }
} 