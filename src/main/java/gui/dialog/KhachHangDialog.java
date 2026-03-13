package gui.dialog;

import entity.KhachHang;
import gui.controller.KhachHangController;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class KhachHangDialog extends JDialog {

    // ── Màu ─────────────────────────────────────────────
    private static final Color SURFACE = new Color(11, 15, 30);
    private static final Color CARD    = new Color(14, 20, 40);
    private static final Color BORDER  = new Color(30, 42, 72);
    private static final Color ACCENT  = new Color(99, 102, 241);
    private static final Color CYAN    = new Color(6, 182, 212);
    private static final Color TEXT1   = new Color(226, 232, 240);
    private static final Color TEXT2   = new Color(100, 116, 139);

    private final KhachHangController controller;
    private final KhachHang khCu;   // null = thêm mới
    private final boolean isNew;

    // Fields
    private JTextField tfMa, tfTen, tfSDT, tfNgay;

    public KhachHangDialog(Frame owner, KhachHang khCu, KhachHangController controller) {
        super(owner, khCu == null ? "Thêm khách hàng mới" : "Chỉnh sửa khách hàng", true);
        this.khCu       = khCu;
        this.isNew      = (khCu == null);
        this.controller = controller;

        setSize(420, 360);
        setLocationRelativeTo(owner);
        getContentPane().setBackground(CARD);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(),   BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    // ── Header ───────────────────────────────────────────
    private JPanel buildHeader() {
        JLabel title = new JLabel(isNew ? "Thêm khách hàng mới" : "Chỉnh sửa khách hàng");
        title.setFont(new Font("Dialog", Font.BOLD, 14));
        title.setForeground(TEXT1);

        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setBackground(SURFACE);
        hdr.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));
        hdr.add(title);
        return hdr;
    }

    // ── Body ─────────────────────────────────────────────
    private JPanel buildBody() {
        tfMa   = new JTextField(isNew ? controller.autoMaKH() : khCu.getMaKH());
        tfTen  = new JTextField(isNew ? "" : khCu.getHoten());
        tfSDT  = new JTextField(isNew ? "" : khCu.getSdt());
        tfNgay = new JTextField(isNew
                ? LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                : (khCu.getNgaythamgia() != null
                ? khCu.getNgaythamgia().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                : ""));

        tfMa.setEditable(false);
        tfMa.setForeground(CYAN);

        JTextField[] fields = {tfMa, tfTen, tfSDT, tfNgay};
        String[]     labels = {
                "Mã khách hàng",
                "Họ và tên",
                "Số điện thoại (10 số)",
                "Ngày tham gia (yyyy-MM-dd)"
        };

        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(CARD);
        body.setBorder(BorderFactory.createEmptyBorder(18, 18, 10, 18));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0.38;
            JLabel lbl = makeLabel(labels[i]);
            lbl.setBorder(BorderFactory.createEmptyBorder());
            body.add(lbl, gbc);

            gbc.gridx = 1; gbc.weightx = 0.62;
            styleTextField(fields[i]);
            body.add(fields[i], gbc);
        }
        return body;
    }

    // ── Footer ───────────────────────────────────────────
    private JPanel buildFooter() {
        JPanel ftr = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
        ftr.setBackground(SURFACE);
        ftr.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));

        JButton btnHuy = makeButton("Hủy",            CARD,   TEXT2);
        JButton btnLuu = makeButton("Lưu khách hàng", ACCENT, Color.WHITE);

        btnHuy.addActionListener(e -> dispose());
        btnLuu.addActionListener(e -> luuKhachHang());

        ftr.add(btnHuy);
        ftr.add(btnLuu);
        return ftr;
    }

    // ── Logic lưu ────────────────────────────────────────
    private void luuKhachHang() {
        String ten  = tfTen.getText().trim();
        String sdt  = tfSDT.getText().trim();
        String ngay = tfNgay.getText().trim();

        if (ten.isEmpty() || sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập đầy đủ họ tên và số điện thoại.",
                    "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate ngayLD;
        try {
            ngayLD = LocalDate.parse(ngay, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Ngày tham gia không hợp lệ. Định dạng: yyyy-MM-dd",
                    "Lỗi ngày", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (isNew) {
            KhachHang khMoi = new KhachHang(tfMa.getText().trim(), ten, sdt, ngayLD);
            boolean ok = controller.themKhachHang(khMoi);
            if (!ok) {
                JOptionPane.showMessageDialog(this,
                        "Thêm thất bại! Có thể mã KH hoặc SĐT đã tồn tại,\nhoặc SĐT không đúng định dạng 10 số.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            KhachHang khSua = new KhachHang(khCu.getMaKH(), ten, sdt, ngayLD);
            boolean ok = controller.suaKhachHang(khSua);
            if (!ok) {
                JOptionPane.showMessageDialog(this,
                        "Cập nhật thất bại!",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        dispose();
    }

    // ── Helpers ──────────────────────────────────────────
    private JButton makeButton(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Dialog", Font.BOLD, 12));
        b.setBackground(bg);
        b.setForeground(fg);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return b;
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(TEXT2);
        l.setFont(new Font("Dialog", Font.BOLD, 12));
        return l;
    }

    private void styleTextField(JTextField f) {
        f.setBackground(CARD);
        f.setForeground(TEXT1);
        f.setCaretColor(ACCENT);
        f.setFont(new Font("Dialog", Font.PLAIN, 12));
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
    }
}