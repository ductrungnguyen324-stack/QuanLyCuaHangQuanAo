package gui.dialog;

import entity.SanPham;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class SanPhamDialog extends JDialog {

    // ── Màu — giống ChiTietHoaDonDialog ──────────────────────────────────────
    private static final Color BG     = new Color(10, 14, 30);
    private static final Color CARD   = new Color(14, 20, 40);
    private static final Color CARD2  = new Color(18, 26, 52);
    private static final Color BORDER = new Color(30, 42, 72);
    private static final Color ACCENT = new Color(99, 102, 241);
    private static final Color GREEN  = new Color(16, 185, 129);
    private static final Color RED    = new Color(239, 68, 68);
    private static final Color TEXT1  = new Color(226, 232, 240);
    private static final Color TEXT2  = new Color(100, 116, 139);

    // ── Components ────────────────────────────────────────────────────────────
    private JTextField        txtMaSP, txtTenSP, txtThuongHieu, txtKichCo, txtMauSac, txtGiaBan, txtTonKho;
    private JComboBox<String> cboLoaiSP, cboTrangThai;

    private SanPham ketQua    = null;
    private boolean confirmed = false;

    private final SanPham spHienTai;

    // ── Constructor ───────────────────────────────────────────────────────────
    public SanPhamDialog(Frame parent, SanPham sp) {
        super(parent, sp == null ? "Thêm Sản Phẩm" : "Sửa Sản Phẩm", true);
        this.spHienTai = sp;
        setSize(500, 560);
        setResizable(false);
        setLocationRelativeTo(parent);

        JPanel main = new JPanel(new BorderLayout(0, 0));
        main.setBackground(BG);
        main.add(buildHeader(), BorderLayout.NORTH);
        main.add(buildForm(),   BorderLayout.CENTER);
        main.add(buildFooter(), BorderLayout.SOUTH);
        setContentPane(main);

        if (sp != null) fillData(sp);

        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    // ── Header ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(new Color(11, 16, 35));
        h.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                BorderFactory.createEmptyBorder(16, 22, 16, 22)
        ));
        JLabel title = new JLabel(spHienTai == null ? "Thêm sản phẩm mới" : "Sửa sản phẩm: " + spHienTai.getMasp());
        title.setFont(new Font("Dialog", Font.BOLD, 16));
        title.setForeground(TEXT1);

        JLabel sub = new JLabel(spHienTai == null ? "Điền đầy đủ thông tin bên dưới" : "Chỉnh sửa thông tin sản phẩm");
        sub.setFont(new Font("Dialog", Font.PLAIN, 12));
        sub.setForeground(TEXT2);

        h.add(title, BorderLayout.NORTH);
        h.add(sub,   BorderLayout.SOUTH);
        return h;
    }

    // ── Form ──────────────────────────────────────────────────────────────────
    private JPanel buildForm() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(BG);
        wrap.setBorder(BorderFactory.createEmptyBorder(16, 20, 8, 20));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(CARD);
        form.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        GridBagConstraints lc = lbc();
        GridBagConstraints fc = fbc();

        // Mã SP
        lc.gridy = 0; form.add(makeLabel("Mã sản phẩm"), lc);
        fc.gridy = 0; txtMaSP = makeField("");
        txtMaSP.setEnabled(false);
        txtMaSP.setForeground(TEXT2);
        txtMaSP.setToolTipText("Tự động sinh");
        form.add(txtMaSP, fc);

        // Tên SP
        lc.gridy = 1; form.add(makeLabel("Tên sản phẩm *"), lc);
        fc.gridy = 1; txtTenSP = makeField("");
        form.add(txtTenSP, fc);

        // Loại SP
        lc.gridy = 2; form.add(makeLabel("Loại sản phẩm"), lc);
        fc.gridy = 2; cboLoaiSP = makeCombo(new String[]{"Áo", "Quần", "Váy", "Giày", "Dép", "Phụ kiện", "Khác"});
        form.add(cboLoaiSP, fc);

        // Thương hiệu
        lc.gridy = 3; form.add(makeLabel("Thương hiệu"), lc);
        fc.gridy = 3; txtThuongHieu = makeField("");
        form.add(txtThuongHieu, fc);

        // Kích cỡ
        lc.gridy = 4; form.add(makeLabel("Kích cỡ"), lc);
        fc.gridy = 4; txtKichCo = makeField("");
        form.add(txtKichCo, fc);

        // Màu sắc
        lc.gridy = 5; form.add(makeLabel("Màu sắc"), lc);
        fc.gridy = 5; txtMauSac = makeField("");
        form.add(txtMauSac, fc);

        // Giá bán
        lc.gridy = 6; form.add(makeLabel("Giá bán (đ) *"), lc);
        fc.gridy = 6; txtGiaBan = makeField("0");
        form.add(txtGiaBan, fc);

        // Tồn kho
        lc.gridy = 7; form.add(makeLabel("Tồn kho"), lc);
        fc.gridy = 7; txtTonKho = makeField("0");
        form.add(txtTonKho, fc);

        // Trạng thái
        lc.gridy = 8; form.add(makeLabel("Trạng thái"), lc);
        fc.gridy = 8; cboTrangThai = makeCombo(new String[]{"CONHANG", "HETHANG"});
        form.add(cboTrangThai, fc);

        wrap.add(form, BorderLayout.CENTER);
        return wrap;
    }

    // ── Footer ────────────────────────────────────────────────────────────────
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        footer.setBackground(BG);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));

        JButton btnHuy = makeButton("Hủy", CARD, TEXT2);
        btnHuy.addActionListener(e -> dispose());

        JButton btnLuu = makeButton("Lưu", ACCENT, Color.WHITE);
        btnLuu.addActionListener(e -> onLuu());
        getRootPane().setDefaultButton(btnLuu);

        footer.add(btnHuy);
        footer.add(btnLuu);
        return footer;
    }

    // ── Fill data khi sửa ─────────────────────────────────────────────────────
    private void fillData(SanPham sp) {
        txtMaSP.setText(sp.getMasp());
        txtTenSP.setText(sp.getTensp());
        txtThuongHieu.setText(sp.getThuonghieu() != null ? sp.getThuonghieu() : "");
        txtKichCo.setText(sp.getKichco()     != null ? sp.getKichco()     : "");
        txtMauSac.setText(sp.getMausac()     != null ? sp.getMausac()     : "");
        txtGiaBan.setText(String.valueOf(sp.getGiaban()));
        txtTonKho.setText(String.valueOf(sp.getTonkho()));
        if (sp.getLoaisp()    != null) cboLoaiSP.setSelectedItem(sp.getLoaisp());
        if (sp.getTrangthai() != null) cboTrangThai.setSelectedItem(sp.getTrangthai());
    }

    // ── Validate & save ───────────────────────────────────────────────────────
    private void onLuu() {
        String tenSP = txtTenSP.getText().trim();
        if (tenSP.isEmpty()) {
            showError("Tên sản phẩm không được để trống!");
            txtTenSP.requestFocus();
            return;
        }

        double giaBan;
        try {
            giaBan = Double.parseDouble(txtGiaBan.getText().trim().replace(",", ""));
            if (giaBan <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showError("Giá bán phải là số dương!");
            txtGiaBan.requestFocus();
            return;
        }

        int tonKho;
        try {
            tonKho = Integer.parseInt(txtTonKho.getText().trim());
            if (tonKho < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showError("Tồn kho phải là số nguyên không âm!");
            txtTonKho.requestFocus();
            return;
        }

        SanPham sp = new SanPham();
        if (spHienTai != null) sp.setMasp(spHienTai.getMasp());
        sp.setTensp(tenSP);
        sp.setLoaisp((String) cboLoaiSP.getSelectedItem());
        sp.setThuonghieu(txtThuongHieu.getText().trim());
        sp.setKichco(txtKichCo.getText().trim());
        sp.setMausac(txtMauSac.getText().trim());
        sp.setGiaban(giaBan);
        sp.setTonkho(tonKho);
        sp.setTrangthai((String) cboTrangThai.getSelectedItem());

        this.ketQua    = sp;
        this.confirmed = true;
        dispose();
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public SanPham getKetQua()   { return ketQua;    }
    public boolean isConfirmed() { return confirmed; }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Dialog", Font.BOLD, 12));
        l.setForeground(TEXT2);
        return l;
    }

    private JTextField makeField(String def) {
        JTextField f = new JTextField(def);
        f.setBackground(CARD2);
        f.setForeground(TEXT1);
        f.setCaretColor(ACCENT);
        f.setFont(new Font("Dialog", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return f;
    }

    private JComboBox<String> makeCombo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setBackground(CARD2);
        cb.setForeground(TEXT1);
        cb.setFont(new Font("Dialog", Font.PLAIN, 13));
        return cb;
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
        b.setBorder(BorderFactory.createEmptyBorder(9, 20, 9, 20));
        return b;
    }

    private GridBagConstraints lbc() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx  = 0; c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(6, 6, 6, 14);
        return c;
    }

    private GridBagConstraints fbc() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx   = 1; c.fill = GridBagConstraints.HORIZONTAL;
        c.insets  = new Insets(6, 0, 6, 6);
        c.weightx = 1.0;
        return c;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
    }
}