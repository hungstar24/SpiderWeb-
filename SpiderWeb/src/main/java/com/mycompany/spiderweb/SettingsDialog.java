package com.mycompany.spiderweb;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import static com.mycompany.spiderweb.Models.*;

public class SettingsDialog {

    public static void open(JFrame owner, SettingsStore store, boolean allowCancel) {
        JDialog dlg = new JDialog(owner, "Cấu hình Google CSE", true);
        dlg.setLayout(new BorderLayout(12, 12));
        dlg.setSize(560, 320);
        dlg.setLocationRelativeTo(owner);

        JPanel content = new JPanel();
        content.setBorder(new EmptyBorder(14, 14, 14, 14));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JPasswordField tfKey = new JPasswordField(store.apiKey());
        JTextField tfCx = new JTextField(store.cx());

        Pretty.styleTextField(tfKey, "API Key (AIza...)");
        Pretty.styleTextField(tfCx, "CX / Search engine ID");

        JLabel note = new JLabel("<html><span style='color:#666'>Key/CX lưu trong máy (Preferences). Không hard-code.</span></html>");

        content.add(Pretty.sectionTitle("API Key"));
        content.add(Box.createVerticalStrut(6));
        content.add(tfKey);

        content.add(Box.createVerticalStrut(12));
        content.add(Pretty.sectionTitle("CX (Search engine ID)"));
        content.add(Box.createVerticalStrut(6));
        content.add(tfCx);
        content.add(Box.createVerticalStrut(12));
        content.add(note);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnSave = new JButton("Lưu");
        JButton btnCancel = new JButton("Hủy");

        btnSave.addActionListener(e -> {
            String k = new String(tfKey.getPassword()).trim();
            String c = tfCx.getText().trim();
            if (k.isBlank() || c.isBlank()) {
                JOptionPane.showMessageDialog(dlg, "Bạn phải nhập đủ API Key và CX.", "Thiếu dữ liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }
            store.save(k, c);
            dlg.dispose();
        });

        btnCancel.addActionListener(e -> {
            if (!allowCancel && !store.isReady()) {
                JOptionPane.showMessageDialog(dlg, "Bạn cần cấu hình API Key/CX để dùng chương trình.", "Bắt buộc", JOptionPane.WARNING_MESSAGE);
                return;
            }
            dlg.dispose();
        });

        actions.add(btnCancel);
        actions.add(btnSave);

        dlg.add(content, BorderLayout.CENTER);
        dlg.add(actions, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }
}
