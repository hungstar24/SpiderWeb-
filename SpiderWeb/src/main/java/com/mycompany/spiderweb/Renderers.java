package com.mycompany.spiderweb;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.mycompany.spiderweb.Models.*;

public class Renderers {

    public static class DomainRenderer extends JPanel implements ListCellRenderer<DomainItem> {
        private final JLabel lb = new JLabel();
        private final JLabel badge = new JLabel();

        public DomainRenderer() {
            setLayout(new BorderLayout(8, 0));
            setBorder(new EmptyBorder(10, 10, 10, 10));
            setOpaque(true);

            badge.setOpaque(true);
            badge.setBorder(new EmptyBorder(2, 8, 2, 8));
            badge.setFont(badge.getFont().deriveFont(Font.BOLD, 12f));

            add(lb, BorderLayout.CENTER);
            add(badge, BorderLayout.EAST);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends DomainItem> list, DomainItem value, int index,
                                                     boolean isSelected, boolean cellHasFocus) {
            lb.setText(value.domain());
            badge.setText(String.valueOf(value.count()));

            if (isSelected) {
                setBackground(new Color(230, 242, 255));
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(120, 180, 255), 1, true),
                        new EmptyBorder(10, 10, 10, 10)
                ));
            } else {
                setBackground(Color.WHITE);
                setBorder(new EmptyBorder(10, 10, 10, 10));
            }

            badge.setBackground(isSelected ? new Color(120, 180, 255) : new Color(235, 235, 235));
            badge.setForeground(isSelected ? Color.WHITE : new Color(70, 70, 70));

            return this;
        }
    }

    public static class ResultRenderer extends JPanel implements ListCellRenderer<ResultItem> {
        private final JLabel lbTitle = new JLabel();
        private final JLabel lbDomain = new JLabel();
        private final JLabel lbLink = new JLabel();
        private final JLabel lbSnippet = new JLabel();
        private final Supplier<List<String>> keywords;

        public ResultRenderer(Supplier<List<String>> keywords) {
            this.keywords = keywords;
            setLayout(new BorderLayout(8, 6));
            setBorder(new EmptyBorder(12, 12, 12, 12));
            setOpaque(true);

            lbTitle.setFont(lbTitle.getFont().deriveFont(Font.BOLD, 14f));
            lbDomain.setForeground(new Color(110, 110, 110));
            lbLink.setForeground(new Color(70, 120, 200));
            lbSnippet.setForeground(new Color(150, 30, 30));

            JPanel top = new JPanel(new BorderLayout(10, 0));
            top.setOpaque(false);
            top.add(lbTitle, BorderLayout.CENTER);
            top.add(lbDomain, BorderLayout.EAST);

            JPanel wrap = new JPanel();
            wrap.setOpaque(false);
            wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
            wrap.add(top);
            wrap.add(Box.createVerticalStrut(4));
            wrap.add(lbLink);
            wrap.add(Box.createVerticalStrut(8));
            wrap.add(lbSnippet);

            add(wrap, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends ResultItem> list, ResultItem value, int index,
                                                     boolean isSelected, boolean cellHasFocus) {
            String title = value.title() == null || value.title().isBlank() ? value.link() : value.title();
            lbTitle.setText(title);
            lbDomain.setText(value.displayLink());
            lbLink.setText(value.link());

            String snipHtml = highlightToHtml(value.snippet(), keywords.get());
            lbSnippet.setText("<html><div style='width:520px; line-height:1.35;'>" + snipHtml + "</div></html>");

            if (isSelected) {
                setBackground(new Color(230, 242, 255));
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(120, 180, 255), 1, true),
                        new EmptyBorder(12, 12, 12, 12)
                ));
            } else {
                setBackground(Color.WHITE);
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(238, 238, 238), 1, true),
                        new EmptyBorder(12, 12, 12, 12)
                ));
            }
            return this;
        }

        private static String escapeHtml(String s) {
            if (s == null) return "";
            return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;");
        }

        private static String highlightToHtml(String text, List<String> keywords) {
            String out = escapeHtml(text);

            for (String k : keywords) {
                if (k == null || k.isBlank()) continue;
                Pattern p = Pattern.compile(Pattern.quote(k), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
                Matcher m = p.matcher(out);

                StringBuffer sb = new StringBuffer();
                while (m.find()) {
                    String hit = m.group();
                    m.appendReplacement(sb,
                            "<span style='background:#fff2a8; padding:0 2px; border-radius:3px;'><b>" +
                                    escapeHtml(hit) + "</b></span>");
                }
                m.appendTail(sb);
                out = sb.toString();
            }
            return out;
        }
    }
}
