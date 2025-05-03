package com.biblioteca.ui;

import javax.swing.*;
import java.awt.*;

public class PrincipalFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private AlunoPanel alunoPanel;
    private LivroPanel livroPanel;
    private EmprestimoPanel emprestimoPanel;

    public PrincipalFrame() {
        setTitle("Sistema de Biblioteca");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Criando o painel de abas
        tabbedPane = new JTabbedPane();
        
        // Criando os painéis para cada aba
        alunoPanel = new AlunoPanel();
        livroPanel = new LivroPanel();
        emprestimoPanel = new EmprestimoPanel();

        // Adicionando as abas
        tabbedPane.addTab("Alunos", alunoPanel);
        tabbedPane.addTab("Livros", livroPanel);
        tabbedPane.addTab("Empréstimos", emprestimoPanel);

        // Adicionando o painel de abas ao frame
        add(tabbedPane);
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Se Nimbus não estiver disponível, usar o padrão
        }
        SwingUtilities.invokeLater(() -> {
            new PrincipalFrame().setVisible(true);
        });
    }
} 