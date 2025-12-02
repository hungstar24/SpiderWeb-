package com.mycompany.spiderweb;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Pretty{

    public static void installLookAndFeel() {
        FlatLightLaf.setup();
        UIManager.put("Component.arc", 16);
        UIManager.put("Button.arc", 16);
        UIManager.put("TextComponent.arc", 14);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("Button.margin", new Insets(10, 14, 10, 14));
    }

    public static JPanel card() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int arc = 18, w = getWidth(), h = getHeight();
                g2.setColor(new Color(0, 0, 0, 16));
                g2.fillRoundRect(2, 4, w - 4, h - 6, arc, arc);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, w - 4, h - 6, arc, arc);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(14, 14, 14, 14));
        return p;
    }

    public static JLabel sectionTitle(String text) {
        JLabel lb = new JLabel(text);
        lb.setFont(lb.getFont().deriveFont(Font.BOLD, 14f));
        return lb;
    }

    public static void styleTextField(JTextField tf, String placeholder) {
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        tf.putClientProperty("JTextField.placeholderText", placeholder);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(11, 12, 11, 12)
        ));
    }

    public static JPanel gradientHeader(String title, String subtitle, JComponent rightButton) {
        JPanel header = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.setPaint(new GradientPaint(0,0,new Color(12,74,110), w,h,new Color(2,132,199)));
                g2.fillRoundRect(0,0,w,h,22,22);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setLayout(new BorderLayout(12, 12));
        header.setBorder(new EmptyBorder(14, 14, 14, 14));

        JLabel t = new JLabel(title);
        t.setForeground(Color.WHITE);
        t.setFont(t.getFont().deriveFont(Font.BOLD, 22f));

        JLabel sub = new JLabel(subtitle);
        sub.setForeground(new Color(255,255,255,230));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(t);
        left.add(Box.createVerticalStrut(4));
        left.add(sub);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        if (rightButton != null) {
            rightButton.setForeground(Color.WHITE);
            rightButton.setOpaque(false);
            rightButton.setBorder(BorderFactory.createLineBorder(new Color(255,255,255,160), 1, true));
            rightButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            right.add(rightButton);
        }

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }
}
