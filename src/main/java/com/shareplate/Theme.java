
package com.shareplate;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class Theme {

    public static final Color FOREST = new Color(23, 63, 50);
    public static final Color DARK_GREEN = new Color(35, 82, 61);
    public static final Color LEAF = new Color(91, 139, 82);
    public static final Color SAGE = new Color(168, 189, 145);
    public static final Color LIGHT_SAGE = new Color(224, 233, 211);
    public static final Color CREAM = new Color(244, 240, 223);
    public static final Color WHITE = Color.WHITE;
    public static final Color TEXT = new Color(43, 61, 48);
    public static final Color MUTED = new Color(112, 126, 110);
    public static final Color BORDER = new Color(218, 226, 208);
    public static final Color ORANGE = new Color(218, 145, 75);

    public static final Font TITLE =
        new Font("Serif", Font.BOLD, 30);

    public static final Font HEADING =
        new Font("SansSerif", Font.BOLD, 20);

    public static final Font NORMAL =
        new Font("SansSerif", Font.PLAIN, 14);

    public static final Font BUTTON =
        new Font("SansSerif", Font.BOLD, 14);

    public static void styleButton(JButton button) {

        button.setBackground(FOREST);
        button.setForeground(WHITE);
        button.setFont(BUTTON);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(160, 42));
        button.setBorder(new EmptyBorder(10, 18, 10, 18));
    }

    public static void styleField(JTextField field) {

        field.setFont(NORMAL);
        field.setForeground(TEXT);
        field.setBackground(WHITE);
        field.setCaretColor(FOREST);

        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(10, 12, 10, 12)
        ));

        field.setPreferredSize(new Dimension(250, 42));
    }

    public static JPanel createCard() {

        JPanel panel = new JPanel();
        panel.setBackground(WHITE);

        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(18, 18, 18, 18)
        ));

        return panel;
    }

    public static JLabel createHeading(String text) {

        JLabel label = new JLabel(text);
        label.setFont(HEADING);
        label.setForeground(FOREST);

        return label;
    }
}
