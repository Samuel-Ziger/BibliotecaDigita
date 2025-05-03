package com.biblioteca.ui;

import com.biblioteca.dao.AlunoDAO;
import com.biblioteca.dao.EmprestimoDAO;
import com.biblioteca.dao.LivroDAO;
import com.biblioteca.model.Aluno;
import com.biblioteca.model.Emprestimo;
import com.biblioteca.model.Livro;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EmprestimoPanel extends JPanel {
    private JTextField txtId;
    private JComboBox<Aluno> cmbAluno;
    private JComboBox<Livro> cmbLivro;
    private JTextField txtDataEmprestimo;
    private JTextField txtDataDevolucao;
    private JButton btnGravar;
    private JButton btnAtualizar;
    private JButton btnDeletar;
    private JButton btnLimpar;
    private JButton btnRelatorio;
    private JTable tabelaEmprestimos;
    private EmprestimoDAO emprestimoDAO;
    private AlunoDAO alunoDAO;
    private LivroDAO livroDAO;
    private SimpleDateFormat dateFormat;

    public EmprestimoPanel() {
        emprestimoDAO = new EmprestimoDAO();
        alunoDAO = new AlunoDAO();
        livroDAO = new LivroDAO();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        
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

        // Campo Aluno
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Aluno:"), gbc);
        gbc.gridx = 1;
        cmbAluno = new JComboBox<>();
        add(cmbAluno, gbc);

        // Campo Livro
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Livro:"), gbc);
        gbc.gridx = 1;
        cmbLivro = new JComboBox<>();
        add(cmbLivro, gbc);

        // Campo Data Empréstimo
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Data Empréstimo:"), gbc);
        gbc.gridx = 1;
        txtDataEmprestimo = new JTextField(20);
        txtDataEmprestimo.setText(dateFormat.format(new Date()));
        add(txtDataEmprestimo, gbc);

        // Campo Data Devolução
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("Data Devolução:"), gbc);
        gbc.gridx = 1;
        txtDataDevolucao = new JTextField(20);
        add(txtDataDevolucao, gbc);

        // Painel de botões
        JPanel buttonPanel = new JPanel();
        btnGravar = new JButton("Gravar");
        btnAtualizar = new JButton("Atualizar");
        btnDeletar = new JButton("Deletar");
        btnLimpar = new JButton("Limpar");
        btnRelatorio = new JButton("Relatório");

        buttonPanel.add(btnGravar);
        buttonPanel.add(btnAtualizar);
        buttonPanel.add(btnDeletar);
        buttonPanel.add(btnLimpar);
        buttonPanel.add(btnRelatorio);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        // Tabela de empréstimos
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        tabelaEmprestimos = new JTable();
        JScrollPane scrollPane = new JScrollPane(tabelaEmprestimos);
        add(scrollPane, gbc);

        // Adicionando ação aos botões
        btnGravar.addActionListener(e -> gravarEmprestimo());
        btnAtualizar.addActionListener(e -> atualizarEmprestimo());
        btnDeletar.addActionListener(e -> deletarEmprestimo());
        btnLimpar.addActionListener(e -> limparCampos());
        btnRelatorio.addActionListener(e -> gerarRelatorio());

        // Adicionando listener para seleção na tabela
        tabelaEmprestimos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tabelaEmprestimos.getSelectedRow();
                if (selectedRow >= 0) {
                    carregarEmprestimoSelecionado(selectedRow);
                }
            }
        });

        // Carregar dados iniciais
        carregarComboBoxes();
        carregarDadosTabela();
    }

    private void carregarComboBoxes() {
        try {
            // Carregar alunos
            List<Aluno> alunos = alunoDAO.listarTodos();
            cmbAluno.removeAllItems();
            for (Aluno aluno : alunos) {
                cmbAluno.addItem(aluno);
            }

            // Carregar livros
            List<Livro> livros = livroDAO.listarTodos();
            cmbLivro.removeAllItems();
            for (Livro livro : livros) {
                cmbLivro.addItem(livro);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void gravarEmprestimo() {
        try {
            if (cmbAluno.getSelectedItem() == null || cmbLivro.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Selecione um aluno e um livro!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Emprestimo emprestimo = new Emprestimo();
            emprestimo.setAluno((Aluno) cmbAluno.getSelectedItem());
            emprestimo.setLivro((Livro) cmbLivro.getSelectedItem());
            emprestimo.setDataEmprestimo(dateFormat.parse(txtDataEmprestimo.getText()));
            if (!txtDataDevolucao.getText().isEmpty()) {
                emprestimo.setDataDevolucao(dateFormat.parse(txtDataDevolucao.getText()));
            }
            emprestimoDAO.inserir(emprestimo);
            JOptionPane.showMessageDialog(this, "Empréstimo gravado com sucesso!");
            limparCampos();
            carregarDadosTabela();
        } catch (SQLException | ParseException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao gravar empréstimo: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarEmprestimo() {
        try {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selecione um empréstimo para atualizar!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Emprestimo emprestimo = new Emprestimo();
            emprestimo.setId(Integer.parseInt(txtId.getText()));
            emprestimo.setAluno((Aluno) cmbAluno.getSelectedItem());
            emprestimo.setLivro((Livro) cmbLivro.getSelectedItem());
            emprestimo.setDataEmprestimo(dateFormat.parse(txtDataEmprestimo.getText()));
            if (!txtDataDevolucao.getText().isEmpty()) {
                emprestimo.setDataDevolucao(dateFormat.parse(txtDataDevolucao.getText()));
            }
            emprestimoDAO.atualizar(emprestimo);
            JOptionPane.showMessageDialog(this, "Empréstimo atualizado com sucesso!");
            limparCampos();
            carregarDadosTabela();
        } catch (SQLException | ParseException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar empréstimo: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletarEmprestimo() {
        try {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selecione um empréstimo para deletar!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int id = Integer.parseInt(txtId.getText());
            emprestimoDAO.deletar(id);
            JOptionPane.showMessageDialog(this, "Empréstimo deletado com sucesso!");
            limparCampos();
            carregarDadosTabela();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao deletar empréstimo: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos() {
        txtId.setText("");
        cmbAluno.setSelectedIndex(0);
        cmbLivro.setSelectedIndex(0);
        txtDataEmprestimo.setText(dateFormat.format(new Date()));
        txtDataDevolucao.setText("");
    }

    private void carregarDadosTabela() {
        try {
            List<Emprestimo> emprestimos = emprestimoDAO.listarTodos();
            String[] colunas = {"ID", "Aluno", "Livro", "Data Empréstimo", "Data Devolução"};
            Object[][] dados = new Object[emprestimos.size()][5];
            
            for (int i = 0; i < emprestimos.size(); i++) {
                Emprestimo emprestimo = emprestimos.get(i);
                dados[i][0] = emprestimo.getId();
                dados[i][1] = emprestimo.getAluno().getNome();
                dados[i][2] = emprestimo.getLivro().getTitulo();
                dados[i][3] = dateFormat.format(emprestimo.getDataEmprestimo());
                dados[i][4] = emprestimo.getDataDevolucao() != null ? dateFormat.format(emprestimo.getDataDevolucao()) : "";
            }
            
            tabelaEmprestimos.setModel(new javax.swing.table.DefaultTableModel(dados, colunas) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarEmprestimoSelecionado(int row) {
        try {
            int id = Integer.parseInt(tabelaEmprestimos.getValueAt(row, 0).toString());
            Emprestimo emprestimo = emprestimoDAO.buscarPorId(id);
            
            txtId.setText(String.valueOf(emprestimo.getId()));
            cmbAluno.setSelectedItem(emprestimo.getAluno());
            cmbLivro.setSelectedItem(emprestimo.getLivro());
            txtDataEmprestimo.setText(dateFormat.format(emprestimo.getDataEmprestimo()));
            txtDataDevolucao.setText(emprestimo.getDataDevolucao() != null ? dateFormat.format(emprestimo.getDataDevolucao()) : "");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar empréstimo: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void gerarRelatorio() {
        try {
            List<Emprestimo> emprestimos = emprestimoDAO.listarTodos();
            StringBuilder relatorio = new StringBuilder();
            relatorio.append("Relatório de Empréstimos\n\n");
            
            for (Emprestimo emprestimo : emprestimos) {
                relatorio.append("ID: ").append(emprestimo.getId()).append("\n");
                relatorio.append("Aluno: ").append(emprestimo.getAluno().getNome()).append("\n");
                relatorio.append("Livro: ").append(emprestimo.getLivro().getTitulo()).append("\n");
                relatorio.append("Data Empréstimo: ").append(dateFormat.format(emprestimo.getDataEmprestimo())).append("\n");
                relatorio.append("Data Devolução: ").append(emprestimo.getDataDevolucao() != null ? dateFormat.format(emprestimo.getDataDevolucao()) : "Não devolvido").append("\n");
                relatorio.append("----------------------------------------\n");
            }
            
            JTextArea textArea = new JTextArea(relatorio.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 400));
            
            JOptionPane.showMessageDialog(this, scrollPane, "Relatório de Empréstimos", JOptionPane.PLAIN_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar relatório: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
} 