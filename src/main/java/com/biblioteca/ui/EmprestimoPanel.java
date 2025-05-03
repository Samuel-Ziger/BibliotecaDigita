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
import javax.swing.text.MaskFormatter;
import java.util.stream.Collectors;

public class EmprestimoPanel extends JPanel {
    private JTextField txtId;
    private JComboBox<Aluno> cmbAluno;
    private JComboBox<Livro> cmbLivro;
    private JTextField txtDataEmprestimo;
    private JFormattedTextField txtDataDevolucao;
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
    private JComboBox<String> cmbFiltro;
    private JComboBox<String> cmbOrdenacao;
    private JTextField txtNomeFiltro;
    private JTextField txtTituloFiltro;

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
        txtId.setPreferredSize(new Dimension(150, 25));
        txtId.setMinimumSize(new Dimension(100, 25));
        add(txtId, gbc);

        // Campo Aluno
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Aluno:"), gbc);
        gbc.gridx = 1;
        cmbAluno = new JComboBox<>();
        cmbAluno.setPreferredSize(new Dimension(150, 25));
        cmbAluno.setMinimumSize(new Dimension(100, 25));
        add(cmbAluno, gbc);

        // Campo Livro
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Livro:"), gbc);
        gbc.gridx = 1;
        cmbLivro = new JComboBox<>();
        cmbLivro.setPreferredSize(new Dimension(150, 25));
        cmbLivro.setMinimumSize(new Dimension(100, 25));
        add(cmbLivro, gbc);

        // Campo Data Empréstimo
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Data Empréstimo:"), gbc);
        gbc.gridx = 1;
        txtDataEmprestimo = new JTextField(20);
        txtDataEmprestimo.setText(dateFormat.format(new Date()));
        txtDataEmprestimo.setPreferredSize(new Dimension(150, 25));
        txtDataEmprestimo.setMinimumSize(new Dimension(100, 25));
        add(txtDataEmprestimo, gbc);

        // Campo Data Devolução
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("Data Devolução:"), gbc);
        gbc.gridx = 1;
        try {
            MaskFormatter dateMask = new MaskFormatter("##/##/####");
            dateMask.setPlaceholderCharacter('_');
            txtDataDevolucao = new JFormattedTextField(dateMask);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        txtDataDevolucao.setPreferredSize(new Dimension(150, 25));
        txtDataDevolucao.setMinimumSize(new Dimension(100, 25));
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

        // Componentes de filtro e ordenação
        gbc.gridx = 0;
        gbc.gridy = 7;
        add(new JLabel("Filtrar por:"), gbc);
        gbc.gridx = 1;
        cmbFiltro = new JComboBox<>(new String[]{"Todos", "Aluno", "Livro", "Data"});
        cmbFiltro.setPreferredSize(new Dimension(150, 25));
        cmbFiltro.setMinimumSize(new Dimension(100, 25));
        add(cmbFiltro, gbc);

        // Adicionar campos de texto para filtragem
        gbc.gridx = 0;
        gbc.gridy = 8;
        add(new JLabel("Nome do Aluno:"), gbc);
        gbc.gridx = 1;
        txtNomeFiltro = new JTextField(20);
        add(txtNomeFiltro, gbc);

        gbc.gridx = 0;
        gbc.gridy = 9;
        add(new JLabel("Título do Livro:"), gbc);
        gbc.gridx = 1;
        txtTituloFiltro = new JTextField(20);
        add(txtTituloFiltro, gbc);

        gbc.gridx = 0;
        gbc.gridy = 10;
        add(new JLabel("Ordenar por:"), gbc);
        gbc.gridx = 1;
        cmbOrdenacao = new JComboBox<>(new String[]{"ID", "Aluno", "Livro", "Data Empréstimo", "Data Devolução"});
        cmbOrdenacao.setPreferredSize(new Dimension(150, 25));
        cmbOrdenacao.setMinimumSize(new Dimension(100, 25));
        add(cmbOrdenacao, gbc);

        // Ajustar o layout para garantir que as caixas de seleção de filtragem e ordenação não se expandam além do necessário
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;
        gbc.weighty = 0;

        // Ajustar o tamanho das caixas de seleção
        cmbFiltro.setPreferredSize(new Dimension(150, 25));
        cmbFiltro.setMinimumSize(new Dimension(100, 25));
        cmbOrdenacao.setPreferredSize(new Dimension(150, 25));
        cmbOrdenacao.setMinimumSize(new Dimension(100, 25));
    }

    private void carregarComboBoxes() {
        try {
            // Carregar alunos
            List<Aluno> alunos = alunoDAO.listarTodos(10, 0);
            cmbAluno.removeAllItems();
            for (Aluno aluno : alunos) {
                cmbAluno.addItem(aluno);
            }

            // Carregar livros
            List<Livro> livros = livroDAO.listarTodos(10, 0);
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
            if (cmbAluno.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Selecione um aluno!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (cmbLivro.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Selecione um livro!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (txtDataEmprestimo.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "O campo Data Empréstimo é obrigatório!", "Erro", JOptionPane.ERROR_MESSAGE);
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

            int response = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir este empréstimo?", "Confirmação de Exclusão",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (response == JOptionPane.YES_OPTION) {
                int id = Integer.parseInt(txtId.getText());
                emprestimoDAO.deletar(id);
                JOptionPane.showMessageDialog(this, "Empréstimo deletado com sucesso!");
                limparCampos();
                carregarDadosTabela();
            }
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
            List<Emprestimo> emprestimos = emprestimoDAO.listarTodos(10, 0);
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
            List<Emprestimo> emprestimos = emprestimoDAO.listarTodos(10, 0);

            // Aplicar filtro
            String filtro = (String) cmbFiltro.getSelectedItem();
            if (filtro != null && !filtro.equals("Todos")) {
                emprestimos = emprestimos.stream().filter(e -> {
                    switch (filtro) {
                        case "Aluno":
                            return e.getAluno().getNome().contains(txtNomeFiltro.getText());
                        case "Livro":
                            return e.getLivro().getTitulo().contains(txtTituloFiltro.getText());
                        case "Data":
                            return dateFormat.format(e.getDataEmprestimo()).contains(txtDataEmprestimo.getText());
                        default:
                            return true;
                    }
                }).collect(Collectors.toList());
            }

            // Aplicar ordenação
            String ordenacao = (String) cmbOrdenacao.getSelectedItem();
            if (ordenacao != null) {
                emprestimos.sort((e1, e2) -> {
                    switch (ordenacao) {
                        case "Aluno":
                            return e1.getAluno().getNome().compareTo(e2.getAluno().getNome());
                        case "Livro":
                            return e1.getLivro().getTitulo().compareTo(e2.getLivro().getTitulo());
                        case "Data Empréstimo":
                            return e1.getDataEmprestimo().compareTo(e2.getDataEmprestimo());
                        case "Data Devolução":
                            return e1.getDataDevolucao().compareTo(e2.getDataDevolucao());
                        default:
                            return Integer.compare(e1.getId(), e2.getId());
                    }
                });
            }

            // Gerar relatório
            StringBuilder relatorio = new StringBuilder();
            relatorio.append("Relatório de Empréstimos\n\n");
            relatorio.append(String.format("%-5s %-20s %-20s %-15s %-15s\n", "ID", "Aluno", "Livro", "Data Empréstimo", "Data Devolução"));
            relatorio.append("--------------------------------------------------------------------------------\n");

            for (Emprestimo emprestimo : emprestimos) {
                relatorio.append(String.format("%-5d %-20s %-20s %-15s %-15s\n",
                    emprestimo.getId(),
                    emprestimo.getAluno().getNome(),
                    emprestimo.getLivro().getTitulo(),
                    dateFormat.format(emprestimo.getDataEmprestimo()),
                    emprestimo.getDataDevolucao() != null ? dateFormat.format(emprestimo.getDataDevolucao()) : "Não devolvido"));
            }

            JTextArea textArea = new JTextArea(relatorio.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 400));

            JOptionPane.showMessageDialog(this, scrollPane, "Relatório de Empréstimos", JOptionPane.PLAIN_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar relatório: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
} 