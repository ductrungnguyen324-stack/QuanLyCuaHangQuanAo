package gui.dialog;

import entity.NhanVien;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * NhanVienFormDialog.java
 * Package : gui.dialog
 * Chuc nang: Dialog dung chung cho ca Them moi va Cap nhat nhan vien.
 *
 * Su dung:
 *   // Them moi
 *   NhanVienFormDialog dlg = new NhanVienFormDialog(parentFrame, null);
 *   dlg.setVisible(true);
 *   NhanVien kq = dlg.getKetQua(); // null neu nguoi dung huy
 *
 *   // Cap nhat
 *   NhanVienFormDialog dlg = new NhanVienFormDialog(parentFrame, nvHienTai);
 *   dlg.setVisible(true);
 *   NhanVien kq = dlg.getKetQua();
 *
 * Luu y:
 *   - getKetQua().getMatkhau() == null nghia la nguoi dung bo trong mat khau
 *     (Controller can giu lai mat khau cu khi cap nhat)
 */
public class NhanVienFormDialog extends JDialog {

    // ── Mau (dong bo NhanVienPanel) ──────────────────────────
    private static final Color BG      = new Color(7,  10, 20);
    private static final Color SURFACE = new Color(11, 15, 30);
    private static final Color CARD    = new Color(14, 20, 40);
    private static final Color BORDER  = new Color(30, 42, 72);
    private static final Color ACCENT  = new Color(99, 102, 241);
    private static final Color GREEN   = new Color(16, 185, 129);
    private static final Color RED     = new Color(239, 68,  68);
    private static final Color TEXT1   = new Color(226, 232, 240);
    private static final Color TEXT2   = new Color(100, 116, 139);
    private static final Color ERR_CLR = new Color(239, 68,  68);

    // ── Trang thai ───────────────────────────────────────────
    private final boolean isEdit;
    private NhanVien      ketQua = null;

    // ── Input fields ─────────────────────────────────────────
    private JTextField     fMaNV, fHoten, fSdt, fTendangnhap;
    private JPasswordField fMatkhau, fXacNhanMK;
    private JComboBox<String> cbChucVu, cbTrangThai;

    // ── Error labels ─────────────────────────────────────────
    private JLabel errMaNV, errHoten, errSdt, errTendangnhap, errMatkhau;

    // =========================================================
    // Constructor
    // =========================================================
    /**
     * @param parent    Frame cha
     * @param nv        null = them moi | NhanVien = cap nhat
     */
    public NhanVienFormDialog(Frame parent, NhanVien nv) {
        super(parent, nv == null ? "Thêm nhân viên mới" : "Cập nhật nhân viên", true);
        this.isEdit = (nv != null);

        setSize(540, isEdit ? 555 : 595);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        add(buildHeader(),   BorderLayout.NORTH);
        add(buildForm(nv),   BorderLayout.CENTER);
        add(buildFooter(),   BorderLayout.SOUTH);

        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    // =========================================================
    // Header
    // =========================================================
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(SURFACE);
        h.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                BorderFactory.createEmptyBorder(14, 22, 14, 22)
        ));

        JLabel icon = new JLabel(isEdit ? "✏" : "＋");
        icon.setFont(new Font("Dialog", Font.PLAIN, 22));
        icon.setForeground(ACCENT);
        icon.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 12));

        JLabel lblTitle = new JLabel(isEdit ? "Cập nhật nhân viên" : "Thêm nhân viên mới");
        lblTitle.setFont(new Font("Dialog", Font.BOLD, 16));
        lblTitle.setForeground(TEXT1);

        JLabel lblSub = new JLabel(isEdit
                ? "Chỉnh sửa thông tin nhân viên trong hệ thống"
                : "Điền đầy đủ thông tin để tạo tài khoản nhân viên mới");
        lblSub.setFont(new Font("Dialog", Font.PLAIN, 11));
        lblSub.setForeground(TEXT2);

        JPanel pText = new JPanel(new BorderLayout(0, 3));
        pText.setOpaque(false);
        pText.add(lblTitle, BorderLayout.NORTH);
        pText.add(lblSub,   BorderLayout.SOUTH);

        JPanel pLeft = new JPanel(new BorderLayout());
        pLeft.setOpaque(false);
        pLeft.add(icon,  BorderLayout.WEST);
        pLeft.add(pText, BorderLayout.CENTER);

        JButton btnX = new JButton("✕");
        btnX.setFont(new Font("Dialog", Font.BOLD, 13));
        btnX.setForeground(TEXT2);
        btnX.setBackground(SURFACE);
        btnX.setBorderPainted(false);
        btnX.setFocusPainted(false);
        btnX.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnX.addActionListener(e -> dispose());
        btnX.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnX.setForeground(RED); }
            public void mouseExited(MouseEvent e)  { btnX.setForeground(TEXT2); }
        });

        h.add(pLeft, BorderLayout.WEST);
        h.add(btnX,  BorderLayout.EAST);
        return h;
    }

    // =========================================================
    // Form
    // =========================================================
    private JScrollPane buildForm(NhanVien nv) {
        JPanel form = new JPanel();
        form.setBackground(BG);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(16, 22, 8, 22));

        // ── Section 1: Thong tin co ban ──────────────────────
        form.add(makeSectionPanel("Thông tin cơ bản"));
        form.add(Box.createVerticalStrut(10));

        // Hang 1: Ma NV + Ho ten
        fMaNV    = makeField();
        fHoten   = makeField();
        errMaNV  = makeErrLabel();
        errHoten = makeErrLabel();

        if (isEdit && nv != null) {
            fMaNV.setText(nv.getManv());
            fMaNV.setEnabled(false);
            fMaNV.setBackground(new Color(20, 26, 50));
            fMaNV.setForeground(TEXT2);
        }
        if (nv != null) fHoten.setText(nv.getHoten());

        form.add(makeRowDouble(
                makeFieldPanel("Mã nhân viên *", fMaNV,  errMaNV),
                makeFieldPanel("Họ và tên *",    fHoten, errHoten)
        ));
        form.add(Box.createVerticalStrut(4));

        // Hang 2: SDT + Chuc vu
        fSdt     = makeField();
        errSdt   = makeErrLabel();
        cbChucVu = makeCombo(new String[]{
                "Quản lý", "Nhân viên bán hàng", "Kho", "Thu ngân"
        });
        if (nv != null) {
            fSdt.setText(nv.getSdt());
            cbChucVu.setSelectedItem(nv.getChucvu());
        }
        form.add(makeRowDouble(
                makeFieldPanel("Số điện thoại", fSdt,     makeErrLabel()),
                makeFieldPanel("Chức vụ *",     cbChucVu, makeErrLabel())
        ));
        form.add(Box.createVerticalStrut(16));

        // ── Section 2: Tai khoan dang nhap ───────────────────
        form.add(makeSectionPanel("Tài khoản đăng nhập"));
        form.add(Box.createVerticalStrut(10));

        // Ten dang nhap (full width)
        fTendangnhap   = makeField();
        errTendangnhap = makeErrLabel();
        if (nv != null) fTendangnhap.setText(nv.getTendannhap());
        form.add(makeRowFull(makeFieldPanel("Tên đăng nhập *", fTendangnhap, errTendangnhap)));
        form.add(Box.createVerticalStrut(4));

        // Mat khau + Xac nhan
        fMatkhau   = makePassField();
        fXacNhanMK = makePassField();
        errMatkhau = makeErrLabel();
        form.add(makeRowDouble(
                makeFieldPanel(isEdit ? "Mật khẩu mới" : "Mật khẩu *", fMatkhau,   errMatkhau),
                makeFieldPanel("Xác nhận mật khẩu",                      fXacNhanMK, makeErrLabel())
        ));
        form.add(Box.createVerticalStrut(16));

        // ── Section 3: Trang thai ─────────────────────────────
        form.add(makeSectionPanel("Trạng thái"));
        form.add(Box.createVerticalStrut(10));

        cbTrangThai = makeCombo(new String[]{"Hoạt động", "Nghỉ việc"});
        if (nv != null) cbTrangThai.setSelectedItem(nv.getTrangthai());
        form.add(makeRowFull(makeFieldPanel("Trạng thái *", cbTrangThai, makeErrLabel())));
        form.add(Box.createVerticalStrut(8));

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBackground(BG);
        scroll.getViewport().setBackground(BG);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return scroll;
    }

    // =========================================================
    // Footer
    // =========================================================
    private JPanel buildFooter() {
        JPanel f = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        f.setBackground(SURFACE);
        f.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));

        JButton btnHuy = makeBtn("Hủy", CARD, TEXT2);
        btnHuy.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        btnHuy.addActionListener(e -> dispose());

        JButton btnLuu = makeBtn(isEdit ? "Cập nhật" : "Thêm mới", ACCENT, Color.WHITE);
        btnLuu.addActionListener(e -> onSave());
        getRootPane().setDefaultButton(btnLuu);

        f.add(btnHuy);
        f.add(btnLuu);
        return f;
    }

    // =========================================================
    // Xu ly luu
    // =========================================================
    private void onSave() {
        if (!doValidate()) return;

        String maNV        = fMaNV.getText().trim();
        String hoten       = fHoten.getText().trim();
        String sdt         = fSdt.getText().trim();
        String chucvu      = (String) cbChucVu.getSelectedItem();
        String tendangnhap = fTendangnhap.getText().trim();
        String matkhau     = new String(fMatkhau.getPassword()).trim();
        String trangthai   = (String) cbTrangThai.getSelectedItem();

        // matkhau == null -> Controller se giu lai mat khau cu
        ketQua = new NhanVien(
                maNV, hoten, sdt, chucvu, tendangnhap,
                matkhau.isEmpty() ? null : matkhau,
                trangthai
        );
        dispose();
    }

    // =========================================================
    // Validation
    // =========================================================
    private boolean doValidate() {
        clearErrors();
        boolean ok = true;

        // Ma NV (chi khi them moi)
        if (!isEdit) {
            String maNV = fMaNV.getText().trim();
            if (maNV.isEmpty()) {
                setErr(errMaNV, "Mã nhân viên không được để trống!");
                ok = false;
            } else if (!maNV.matches("NV\\d{3,}")) {
                setErr(errMaNV, "Định dạng: NV + ít nhất 3 chữ số (VD: NV001)");
                ok = false;
            }
        }

        // Ho ten
        String hoten = fHoten.getText().trim();
        if (hoten.isEmpty()) {
            setErr(errHoten, "Họ tên không được để trống!");
            ok = false;
        } else if (hoten.length() < 3) {
            setErr(errHoten, "Họ tên phải có ít nhất 3 ký tự!");
            ok = false;
        }

        // SDT (tuy chon)
        String sdt = fSdt.getText().trim();
        if (!sdt.isEmpty() && !sdt.matches("0[0-9]{9}")) {
            setErr(errSdt, "SĐT không hợp lệ (VD: 0901234567)");
            ok = false;
        }

        // Ten dang nhap
        String tdnhap = fTendangnhap.getText().trim();
        if (tdnhap.isEmpty()) {
            setErr(errTendangnhap, "Tên đăng nhập không được để trống!");
            ok = false;
        } else if (tdnhap.length() < 4) {
            setErr(errTendangnhap, "Tên đăng nhập phải có ít nhất 4 ký tự!");
            ok = false;
        } else if (!tdnhap.matches("[a-zA-Z0-9_.]+")) {
            setErr(errTendangnhap, "Chỉ chấp nhận chữ, số, dấu chấm và _");
            ok = false;
        }

        // Mat khau
        String mk  = new String(fMatkhau.getPassword());
        String mk2 = new String(fXacNhanMK.getPassword());
        if (!isEdit && mk.isEmpty()) {
            setErr(errMatkhau, "Mật khẩu không được để trống!");
            ok = false;
        } else if (!mk.isEmpty() && mk.length() < 6) {
            setErr(errMatkhau, "Mật khẩu phải có ít nhất 6 ký tự!");
            ok = false;
        } else if (!mk.isEmpty() && !mk.equals(mk2)) {
            setErr(errMatkhau, "Mật khẩu xác nhận không khớp!");
            ok = false;
        }

        return ok;
    }

    private void clearErrors() {
        errMaNV.setText(" ");        errHoten.setText(" ");
        errSdt.setText(" ");         errTendangnhap.setText(" ");
        errMatkhau.setText(" ");
    }

    private void setErr(JLabel lbl, String msg) { lbl.setText("⚠  " + msg); }

    // =========================================================
    // Getter
    // =========================================================
    /** @return NhanVien neu Luu, null neu Huy/Dong */
    public NhanVien getKetQua() { return ketQua; }

    // =========================================================
    // UI Helpers
    // =========================================================
    private JPanel makeSectionPanel(String text) {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Dialog", Font.BOLD, 11));
        lbl.setForeground(ACCENT);
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER);
        p.add(lbl, BorderLayout.WEST);
        p.add(sep, BorderLayout.CENTER);
        return p;
    }

    private JPanel makeFieldPanel(String label, JComponent input, JLabel err) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Dialog", Font.BOLD, 11));
        lbl.setForeground(TEXT2);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        input.setAlignmentX(Component.LEFT_ALIGNMENT);
        input.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        err.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(lbl);
        p.add(Box.createVerticalStrut(4));
        p.add(input);
        p.add(err);
        return p;
    }

    private JPanel makeRowDouble(JPanel left, JPanel right) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        row.add(left);
        row.add(Box.createHorizontalStrut(14));
        row.add(right);
        return row;
    }

    private JPanel makeRowFull(JPanel field) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        row.add(field);
        return row;
    }

    private JTextField makeField() {
        JTextField f = new JTextField();
        f.setBackground(CARD);   f.setForeground(TEXT1);
        f.setCaretColor(ACCENT); f.setFont(new Font("Dialog", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true), BorderFactory.createEmptyBorder(7,10,7,10)));
        addFocusBorder(f);
        return f;
    }

    private JPasswordField makePassField() {
        JPasswordField f = new JPasswordField();
        f.setBackground(CARD);   f.setForeground(TEXT1);
        f.setCaretColor(ACCENT); f.setFont(new Font("Dialog", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true), BorderFactory.createEmptyBorder(7,10,7,10)));
        addFocusBorder(f);
        return f;
    }

    private JComboBox<String> makeCombo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setBackground(CARD);  cb.setForeground(TEXT1);
        cb.setFont(new Font("Dialog", Font.PLAIN, 13));
        cb.setBorder(new LineBorder(BORDER, 1, true));
        cb.setFocusable(false);
        cb.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(
                    JList<?> l, Object v, int i, boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, i, s, f);
                setBackground(s ? new Color(99,102,241,60) : CARD);
                setForeground(TEXT1);
                setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
                return this;
            }
        });
        return cb;
    }

    private JLabel makeErrLabel() {
        JLabel l = new JLabel(" ");
        l.setFont(new Font("Dialog", Font.PLAIN, 11));
        l.setForeground(ERR_CLR);
        return l;
    }

    private JButton makeBtn(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Dialog", Font.BOLD, 13));
        b.setBackground(bg); b.setForeground(fg);
        b.setOpaque(true); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(9, 24, 9, 24));
        b.addMouseListener(new MouseAdapter() {
            final Color orig = bg;
            public void mouseEntered(MouseEvent e) { b.setBackground(orig.brighter()); }
            public void mouseExited(MouseEvent e)  { b.setBackground(orig); }
        });
        return b;
    }

    private void addFocusBorder(JComponent f) {
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(ACCENT, 1, true), BorderFactory.createEmptyBorder(7,10,7,10)));
            }
            public void focusLost(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(BORDER, 1, true), BorderFactory.createEmptyBorder(7,10,7,10)));
            }
        });
    }
}