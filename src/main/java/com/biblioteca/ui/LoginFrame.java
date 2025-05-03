package com.biblioteca.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginFrame() {
        setTitle("Login - Sistema de Biblioteca");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Usuário:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Senha:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        loginButton = new JButton("Login");
        panel.add(loginButton);

        add(panel);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (authenticate(usernameField.getText(), new String(passwordField.getPassword()))) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Login bem-sucedido!");
                    new PrincipalFrame().setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Usuário ou senha incorretos.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private boolean authenticate(String username, String password) {
        String sql = "SELECT senha_hash FROM usuarios WHERE nome_usuario = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/biblioteca?ssl=true");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String senhaHash = rs.getString("senha_hash");
                return verifyPassword(password, senhaHash);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean verifyPassword(String password, String senhaHash) {
        // Implementar verificação de hash de senha
        // Exemplo: return BCrypt.checkpw(password, senhaHash);
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
} 