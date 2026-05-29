package com.tandem.views;

import com.tandem.controllers.AuthController;
import com.tandem.models.User;
import com.tandem.services.Session;
import com.tandem.views.components.UITheme;
import java.awt.event.*;
import javax.swing.*;

public class LoginForm extends javax.swing.JFrame {

    private final AuthController ac = new AuthController();

    public LoginForm() {
        initComponents();

        getContentPane().setBackground(UITheme.BG);
        emailTextField.setBackground(UITheme.CARD);
        emailTextField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER),
            BorderFactory.createEmptyBorder(0, 12, 0, 12)));
        passwordField.setBackground(UITheme.CARD);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER),
            BorderFactory.createEmptyBorder(0, 12, 0, 12)));

        loginButton.addActionListener(e -> doLogin());
        emailTextField.addActionListener(e -> doLogin());
        passwordField.addActionListener(e -> doLogin());
        registerLabel.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                new RegisterForm();
                dispose();
            }
        });

        setSize(UITheme.W, UITheme.H);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void doLogin() {
        String email = emailTextField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email dan password harus diisi.", "Login", JOptionPane.WARNING_MESSAGE);
            return;
        }
        User user = ac.login(email, password);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Email atau password salah.", "Login Gagal", JOptionPane.ERROR_MESSAGE);
        } else {
            Session.setCurrentUser(user);
            new MainFrame();
            dispose();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        titleLabel = new javax.swing.JLabel();
        subtitleLabel = new javax.swing.JLabel();
        emailLabel = new javax.swing.JLabel();
        emailTextField = new javax.swing.JTextField();
        passwordLabel = new javax.swing.JLabel();
        passwordField = new javax.swing.JPasswordField();
        loginButton = new javax.swing.JButton();
        registerLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Tandem");
        setResizable(false);

        titleLabel.setFont(new java.awt.Font("SansSerif", 1, 26));
        titleLabel.setForeground(new java.awt.Color(26, 26, 26));
        titleLabel.setText("Welcome back");

        subtitleLabel.setFont(new java.awt.Font("SansSerif", 0, 14));
        subtitleLabel.setForeground(new java.awt.Color(107, 107, 107));
        subtitleLabel.setText("Sign in to find your team");

        emailLabel.setFont(new java.awt.Font("SansSerif", 1, 12));
        emailLabel.setForeground(new java.awt.Color(26, 26, 26));
        emailLabel.setText("Email");

        emailTextField.setFont(new java.awt.Font("SansSerif", 0, 14));
        emailTextField.setPreferredSize(new java.awt.Dimension(394, 48));

        passwordLabel.setFont(new java.awt.Font("SansSerif", 1, 12));
        passwordLabel.setForeground(new java.awt.Color(26, 26, 26));
        passwordLabel.setText("Password");

        passwordField.setFont(new java.awt.Font("SansSerif", 0, 14));
        passwordField.setPreferredSize(new java.awt.Dimension(394, 48));

        loginButton.setBackground(new java.awt.Color(26, 26, 26));
        loginButton.setFont(new java.awt.Font("SansSerif", 1, 15));
        loginButton.setForeground(java.awt.Color.white);
        loginButton.setText("Sign In");
        loginButton.setPreferredSize(new java.awt.Dimension(394, 54));

        registerLabel.setFont(new java.awt.Font("SansSerif", 0, 13));
        registerLabel.setForeground(new java.awt.Color(107, 107, 107));
        registerLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        registerLabel.setText("Don't have an account? Register");
        registerLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(titleLabel)
                    .addComponent(subtitleLabel)
                    .addComponent(emailLabel)
                    .addComponent(emailTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
                    .addComponent(passwordLabel)
                    .addComponent(passwordField, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
                    .addComponent(loginButton, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
                    .addComponent(registerLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE))
                .addGap(28, 28, 28))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(160, 160, 160)
                .addComponent(titleLabel)
                .addGap(8, 8, 8)
                .addComponent(subtitleLabel)
                .addGap(40, 40, 40)
                .addComponent(emailLabel)
                .addGap(8, 8, 8)
                .addComponent(emailTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(passwordLabel)
                .addGap(8, 8, 8)
                .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(loginButton, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(registerLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>

    // Variables declaration - do not modify
    private javax.swing.JTextField emailTextField;
    private javax.swing.JLabel emailLabel;
    private javax.swing.JButton loginButton;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JLabel registerLabel;
    private javax.swing.JLabel subtitleLabel;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration
}
