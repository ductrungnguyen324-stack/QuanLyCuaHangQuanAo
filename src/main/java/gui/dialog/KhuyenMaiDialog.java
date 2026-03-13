package gui.dialog;

import entity.KhuyenMai;
import gui.view.dialogs.ThemKhuyenMaiView;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class KhuyenMaiDialog extends JDialog {

    private static final Color BG      = new Color(7,  10, 20);
    private static final Color SURFACE = new Color(11, 15, 30);
    private static final Color CARD    = new Color(14, 20, 40);
    private static final Color BORDER  = new Color(30, 42, 72);
    private static final Color ACCENT  = new Color(99, 102, 241);
    private static final Color RED     = new Color(239, 68, 68);
    private static final Color TEXT1   = new Color(226, 232, 240);
    private static final Color TEXT2   = new Color(100, 116, 139);

    private final ThemKhuyenMaiView formPanel;
    private final String            maKMGoc;   
    private       KhuyenMai         ketQua;    

    
    public KhuyenMaiDialog(Frame parent, KhuyenMai kmEdit) {
        super(parent, kmEdit == null ? "Thêm khuyến mãi" : "Cập nhật khuyến mãi", true);

        this.maKMGoc   = (kmEdit != null) ? kmEdit.getMaKM() : null;
        this.formPanel = new ThemKhuyenMaiView(kmEdit);

        setBackground(BG);
        setLayout(new BorderLayout(0, 0));
        getRootPane().setBorder(BorderFactory.createLineBorder(BORDER));

       
        add(formPanel, BorderLayout.CENTER);

     
        add(buildFooter(kmEdit == null), BorderLayout.SOUTH);

        pack();
        setMinimumSize(new Dimension(480, getHeight()));
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    private JPanel buildFooter(boolean isAdd) {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        footer.setBackground(SURFACE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));

        JButton btnHuy  = makeButton("Hủy",                    CARD,   TEXT2);
        JButton btnLuu  = makeButton(isAdd ? "Thêm" : "Lưu",  ACCENT, Color.WHITE);

        btnHuy.addActionListener(e -> dispose());   // ketQua vẫn null → hủy

        btnLuu.addActionListener(e -> onLuu());

        // Enter → lưu, Escape → hủy
        getRootPane().setDefaultButton(btnLuu);
        registerEscapeKey(btnHuy);

        footer.add(btnHuy);
        footer.add(btnLuu);
        return footer;
    }

    private void onLuu() {
        try {
            // maKMGoc == null khi thêm → DAO sẽ tự sinh mã (truyền "" cho an toàn)
            ketQua = formPanel.buildKhuyenMai(maKMGoc != null ? maKMGoc : "");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Dữ liệu không hợp lệ",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

   
    public KhuyenMai getKetQua() {
        return ketQua;
    }

  
    private JButton makeButton(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Dialog", Font.BOLD, 12));
        b.setBackground(bg);
        b.setForeground(fg);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

      
        Color hover = bg.brighter();
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(hover);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(bg);
            }
        });
        return b;
    }

    private void registerEscapeKey(JButton btnHuy) {
        KeyStroke escape = KeyStroke.getKeyStroke("ESCAPE");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", new javax.swing.AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                btnHuy.doClick();
            }
        });
    }
}
