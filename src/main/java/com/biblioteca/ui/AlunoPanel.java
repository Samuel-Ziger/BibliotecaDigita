package com.biblioteca.ui;

import com.biblioteca.dao.AlunoDAO;
import com.biblioteca.model.Aluno;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AlunoPanel extends JPanel {
    private JTextField txtId;
    private JTextField txtNome;
    private JTextField txtMatricula;
    private JButton btnGravar;
    private JButton btnAtualizar;
    private JButton btnDeletar;
    private JButton btnLimpar;
    private JTable tabelaAlunos;
    private AlunoDAO alunoDAO;

    public AlunoPanel() {
        alunoDAO = new AlunoDAO();
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

        // Campo Nome
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1;
        txtNome = new JTextField(20);
        add(txtNome, gbc);

        // Campo Matrícula
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Matrícula:"), gbc);
        gbc.gridx = 1;
        txtMatricula = new JTextField(20);
        add(txtMatricula, gbc);

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
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        // Tabela de alunos
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        tabelaAlunos = new JTable();
        JScrollPane scrollPane = new JScrollPane(tabelaAlunos);
        add(scrollPane, gbc);

        // Adicionando ação aos botões
        btnGravar.addActionListener(e -> gravarAluno());
        btnAtualizar.addActionListener(e -> atualizarAluno());
        btnDeletar.addActionListener(e -> deletarAluno());
        btnLimpar.addActionListener(e -> limparCampos());

        // Adicionando listener para seleção na tabela
        tabelaAlunos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tabelaAlunos.getSelectedRow();
                if (selectedRow >= 0) {
                    carregarAlunoSelecionado(selectedRow);
                }
            }
        });

        // Carregar dados iniciais
        carregarDadosTabela();
    }

    private void gravarAluno() {
        try {
            Aluno aluno = new Aluno();
            aluno.setNome(txtNome.getText());
            aluno.setMatricula(txtMatricula.getText());
            alunoDAO.inserir(aluno);
            JOptionPane.showMessageDialog(this, "Aluno gravado com sucesso!");
            limparCampos();
            carregarDadosTabela();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao gravar aluno: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarAluno() {
        try {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selecione um aluno para atualizar!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Aluno aluno = new Aluno();
            aluno.setId(Integer.parseInt(txtId.getText()));
            aluno.setNome(txtNome.getText());
            aluno.setMatricula(txtMatricula.getText());
            alunoDAO.atualizar(aluno);
            JOptionPane.showMessageDialog(this, "Aluno atualizado com sucesso!");
            limparCampos();
            carregarDadosTabela();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar aluno: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletarAluno() {
        try {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selecione um aluno para deletar!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int id = Integer.parseInt(txtId.getText());
            alunoDAO.deletar(id);
            JOptionPane.showMessageDialog(this, "Aluno deletado com sucesso!");
            limparCampos();
            carregarDadosTabela();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao deletar aluno: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos() {
        txtId.setText("");
        txtNome.setText("");
        txtMatricula.setText("");
    }

    private void carregarDadosTabela() {
        try {
            List<Aluno> alunos = alunoDAO.listarTodos();
            String[] colunas = {"ID", "Nome", "Matrícula"};
            Object[][] dados = new Object[alunos.size()][3];
            
            for (int i = 0; i < alunos.size(); i++) {
                Aluno aluno = alunos.get(i);
                dados[i][0] = aluno.getId();
                dados[i][1] = aluno.getNome();
                dados[i][2] = aluno.getMatricula();
            }
            
            tabelaAlunos.setModel(new javax.swing.table.DefaultTableModel(dados, colunas) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarAlunoSelecionado(int row) {
        txtId.setText(tabelaAlunos.getValueAt(row, 0).toString());
        txtNome.setText(tabelaAlunos.getValueAt(row, 1).toString());
        txtMatricula.setText(tabelaAlunos.getValueAt(row, 2).toString());
    }
} 