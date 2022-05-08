package com.github.afanas10101111.cvv2calculator;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

public class Window extends JFrame implements ActionListener {
    private static final String DEFAULT_CARD_DATA = "4000000000000001=2205";
    private static final String DEFAULT_KEY_PART_ONE = "0011223344556677";
    private static final String DEFAULT_KEY_PART_TWO = "8899AABBCCDDEEFF";

    private static final String CARD_DATA_REGEX = "^\\d{1,19}=\\d{4}$";
    private static final String KEY_PART_REGEX = "^[a-fA-F\\d]{16}$";

    private static final String OK_TITLE = "CVV2";
    private static final String ERROR_TITLE = "Error...";
    private static final String INPUT_FORMAT_ERROR_TITLE = "Input format error";

    private final Pattern cardDataPattern = Pattern.compile(CARD_DATA_REGEX);
    private final Pattern keyPartPattern = Pattern.compile(KEY_PART_REGEX);
    private final JTextField cardData = new JTextField(DEFAULT_CARD_DATA);
    private final JTextField keyPartOne = new JTextField(DEFAULT_KEY_PART_ONE);
    private final JTextField keyPartTwo = new JTextField(DEFAULT_KEY_PART_TWO);

    public static void main(String[] args) {
        new Window();
    }

    public Window() {
        super("CVV2Calculator");
        setSize(360, 120);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JButton doItButton = new JButton("Calculate CVV2");
        doItButton.addActionListener(this);

        JPanel panel = new JPanel(new GridLayout(2, 2));
        add(panel);
        setVisible(true);
        panel.add(cardData);
        panel.add(keyPartOne);
        panel.add(doItButton);
        panel.add(keyPartTwo);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cardDataText = removeSpaces(cardData.getText());
        boolean cardDataMatch = cardDataPattern.matcher(cardDataText).matches();
        String keyPartOneText = removeSpaces(keyPartOne.getText());
        String keyPartTwoText = removeSpaces(keyPartTwo.getText());
        boolean keysMatch =
                keyPartPattern.matcher(keyPartOneText).matches() && keyPartPattern.matcher(keyPartTwoText).matches();
        if (cardDataMatch && keysMatch) {
            try {
                String[] split = cardDataText.split("=");
                String result = CVV2Calculator.calculateCVV2(split[0], split[1], keyPartOneText, keyPartTwoText);
                showMessageDialog(OK_TITLE, result, JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                showMessageDialog(ERROR_TITLE, "Internal error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (keysMatch) {
            showMessageDialog(
                    INPUT_FORMAT_ERROR_TITLE,
                    "Input PAN=YYMM:\n" + CARD_DATA_REGEX,
                    JOptionPane.WARNING_MESSAGE
            );
        } else {
            showMessageDialog(
                    INPUT_FORMAT_ERROR_TITLE,
                    "The keys must consist of 8 bytes:\n" + KEY_PART_REGEX,
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private String removeSpaces(String s) {
        return s.replace(" ", "");
    }

    private void showMessageDialog(String title, String message, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}
