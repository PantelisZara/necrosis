package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class GameWindow extends JFrame {

    private final JTextArea outputArea;
    private final JTextField inputField;

    public GameWindow() {
        setTitle("NECROSIS");
        setSize(980, 720);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        Color bg = new Color(8, 8, 8);
        Color panel = new Color(12, 12, 12);
        Color fg = new Color(120, 255, 180);
        Color dim = new Color(140, 180, 160);
        Color border = new Color(50, 120, 90);


        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(panel);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(border, 1),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        JLabel title = new JLabel("ZAUN BIOMEDICAL TERMINAL");
        title.setForeground(fg);
        title.setFont(new Font("Consolas", Font.BOLD, 18));

        JLabel subtitle = new JLabel("Containment Facility — NV-1");
        subtitle.setForeground(dim);
        subtitle.setFont(new Font("Consolas", Font.PLAIN, 13));

        JPanel titleWrap = new JPanel(new BorderLayout());
        titleWrap.setBackground(panel);
        titleWrap.add(title, BorderLayout.NORTH);
        titleWrap.add(subtitle, BorderLayout.SOUTH);

        header.add(titleWrap, BorderLayout.WEST);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 16));
        outputArea.setBackground(bg);
        outputArea.setForeground(fg);
        outputArea.setCaretColor(fg);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setMargin(new Insets(18, 18, 18, 18));

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(border, 1),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));
        scrollPane.getViewport().setBackground(bg);

        JLabel statusLabel = new JLabel("READY");
        statusLabel.setForeground(dim);
        statusLabel.setFont(new Font("Consolas", Font.PLAIN, 13));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(panel);

        inputField = new JTextField();
        inputField.setFont(new Font("Consolas", Font.PLAIN, 16));
        inputField.setBackground(bg);
        inputField.setForeground(fg);
        inputField.setCaretColor(fg);
        inputField.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JLabel prompt = new JLabel(">");
        prompt.setFont(new Font("Consolas", Font.BOLD, 18));
        prompt.setForeground(fg);
        prompt.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 8));
        prompt.setOpaque(true);
        prompt.setBackground(bg);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(bg);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(border, 1),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));
        inputPanel.add(prompt, BorderLayout.WEST);
        inputPanel.add(inputField, BorderLayout.CENTER);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(bg);
        center.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        center.add(scrollPane, BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout());
        south.setBackground(panel);
        south.add(statusLabel, BorderLayout.NORTH);
        south.add(inputPanel, BorderLayout.SOUTH);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(bg);
        root.add(header, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        root.add(south, BorderLayout.SOUTH);

        setContentPane(root);
    }


    public void appendOutput(String text) {
        outputArea.append(text);
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }



    public void setInputHandler(ActionListener handler) {
        inputField.addActionListener(handler);
    }

    public String getInput() {
        return inputField.getText();
    }

    public void clearInput() {
        inputField.setText("");
    }






}