package gui.dialog;

import entity.NhanVien;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class NhanVienDialog extends JDialog {

    private static final Color BG      = new Color(7,   10,  20);
    private static final Color SURFACE = new Color(11,  15,  30);
    private static final Color CARD    = new Color(14,  20,  40);
    private static final Color BORDER_C= new Color(30,  42,  72);
    private static final Color ACCENT  = new Color(99,  102, 241);
    private static final Color RED     = new Color(239, 68,  68);
    private static final Color TEXT1   = new Color(226, 232, 240);
    private static final Color TEXT2   = new Color(140, 155, 175);
    private static final Color COMBO_BG= new Color(14,  20,  40);
    private static final Color ERR_CLR = new Color(239, 68,  68);

    private final boolean isEdit;
    private NhanVien ketQua = null;

    private JTextField     fMaNV, fHoten, fSdt, fTendangnhap;
    private JPasswordField fMatkhau, fXacNhanMK;
    private FakeCombo      cbChucVu, cbTrangThai;
    private JLabel         errMaNV, errHoten, errSdt, errTendangnhap, errMatkhau, errXacNhan;

    public NhanVienDialog(Frame parent, NhanVien nv) {
        super(parent,
                nv == null ? "Thêm nhân viên mới" : "Cập nhật nhân viên",
                true);
        this.isEdit = (nv != null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());
        add(buildHeader(),  BorderLayout.NORTH);
        add(buildForm(nv),  BorderLayout.CENTER);
        add(buildFooter(),  BorderLayout.SOUTH);
        pack();
        setSize(600, isEdit ? 570 : 630);
        setLocationRelativeTo(parent);
        setResizable(false);
        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    // =========================================================
    // HEADER
    // =========================================================
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(SURFACE);
        h.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0,0,1,0,BORDER_C),
                BorderFactory.createEmptyBorder(14,22,14,22)));

        // Ti\u00eau \u0111\u1ec1 - tho\u00e1t Unicode
        String title = isEdit
                ? "Cập nhật nhân viên"
                : "Thêm nhân viên mới";
        String sub = isEdit
                ? "Chỉnh sửa thông tin nhân viên"
                : "Điền đầy đủ thông tin để thêm nhân viên mới";

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Dialog", Font.BOLD, 17));
        lblTitle.setForeground(TEXT1);

        JLabel lblSub = new JLabel(sub);
        lblSub.setFont(new Font("Dialog", Font.PLAIN, 12));
        lblSub.setForeground(TEXT2);

        JPanel pText = new JPanel(new BorderLayout(0,4));
        pText.setOpaque(false);
        pText.add(lblTitle, BorderLayout.NORTH);
        pText.add(lblSub,   BorderLayout.SOUTH);

        // Nut dong
        FlatBtn btnX = new FlatBtn("X", new Color(40,50,80), TEXT2, 32, 32);
        btnX.addClickListener(this::dispose);

        h.add(pText, BorderLayout.WEST);
        h.add(btnX,  BorderLayout.EAST);
        return h;
    }

    // =========================================================
    // FORM
    // =========================================================
    private JScrollPane buildForm(NhanVien nv) {
        JPanel form = new JPanel();
        form.setBackground(BG);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(16,24,10,24));

        // --- Section 1 ---
        form.add(secLbl("THÔNG TIN CƠ BẢN"));
        form.add(vg(10));

        fMaNV    = mkField(); errMaNV  = errLbl();
        fHoten   = mkField(); errHoten = errLbl();
        if (isEdit && nv != null) {
            fMaNV.setText(s(nv.getManv()));
            fMaNV.setEnabled(false);
            fMaNV.setBackground(new Color(20,26,52));
            fMaNV.setForeground(TEXT2);
        }
        if (nv != null) fHoten.setText(s(nv.getHoten()));

        String lbMa = isEdit
                ? "Mã nhân viên"
                : "Mã nhân viên *  (VD: NV001)";
        form.add(row2(
                blk(lbMa,              fMaNV,  errMaNV),
                blk("Họ và tên *", fHoten, errHoten)));
        form.add(vg(6));

        fSdt     = mkField(); errSdt = errLbl();
        cbChucVu = new FakeCombo(
                new String[]{"Quản lý","Nhân viên","Thu ngân"},
                COMBO_BG, TEXT1);
        if (nv != null) { fSdt.setText(s(nv.getSdt())); cbChucVu.setSelectedItem(nv.getChucvu()); }
        form.add(row2(
                blk("Số điện thoại  (VD: 0901234567)", fSdt, errSdt),
                blk("Chức vụ *", cbChucVu, errLbl())));
        form.add(vg(16));

        // --- Section 2 ---
        form.add(secLbl("TÀI KHOẢN ĐĂNG NHẬP"));
        form.add(vg(10));

        fTendangnhap = mkField(); errTendangnhap = errLbl();
        if (nv != null) fTendangnhap.setText(s(nv.getTendannhap()));
        form.add(row1(blk(
                 "Tên đăng nhập * (≥4 ký tự, a-z 0-9 _ .)",
                fTendangnhap, errTendangnhap)));
        form.add(vg(6));

        fMatkhau   = mkPassField(); errMatkhau = errLbl();
        fXacNhanMK = mkPassField(); errXacNhan = errLbl();
        String lbMk = isEdit
                 ? "Mật khẩu mới (trống = giữ cũ)"
        : "Mật khẩu * (≥6 ký tự)";
        form.add(row2(
                blk(lbMk, fMatkhau, errMatkhau),
                blk("Xác nhận mật khẩu", fXacNhanMK, errXacNhan)));
        form.add(vg(16));

        // --- Section 3 ---
        form.add(secLbl("TRẠNG THÁI"));
        form.add(vg(10));

        cbTrangThai = new FakeCombo(
                new String[]{"Hoạt động", "Ngừng hoạt động"},
                COMBO_BG, TEXT1);
        if (nv != null) cbTrangThai.setSelectedItem(nv.getTrangthai());
        form.add(row1(blk("Trạng thái *", cbTrangThai, errLbl())));
        form.add(vg(10));

        JScrollPane sc = new JScrollPane(form);
        sc.setBackground(BG);
        sc.getViewport().setBackground(BG);
        sc.setBorder(BorderFactory.createEmptyBorder());
        sc.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return sc;
    }

    // =========================================================
    // FOOTER
    // =========================================================
    private JPanel buildFooter() {
        JPanel f = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        f.setBackground(SURFACE);
        f.setBorder(new MatteBorder(1,0,0,0,BORDER_C));

        FlatBtn btnHuy = new FlatBtn("Hủy", new Color(22,30,58), TEXT2, 90, 38);
        btnHuy.setBorderColor(new Color(50,70,110));
        btnHuy.addClickListener(this::dispose);

        String lblLuu = isEdit ? "Cập nhật" : "Thêm mới";
        FlatBtn btnLuu = new FlatBtn(lblLuu, ACCENT, Color.WHITE, 120, 38);
        btnLuu.addClickListener(this::onSave);

        f.add(btnHuy);
        f.add(btnLuu);
        return f;
    }

    // =========================================================
    // SAVE / VALIDATE
    // =========================================================
    private void onSave() {
        System.out.println("[DIALOG] onSave()");
        if (!doValidate()) return;
        String mk = new String(fMatkhau.getPassword()).trim();
        ketQua = new NhanVien(
                fMaNV.getText().trim(), fHoten.getText().trim(),
                fSdt.getText().trim(),  cbChucVu.getSelected(),
                fTendangnhap.getText().trim(),
                mk.isEmpty() ? null : mk,
                cbTrangThai.getSelected());
        System.out.println("[DIALOG] OK => " + ketQua.getManv());
        dispose();
    }

    private boolean doValidate() {
    clearErr();
    boolean ok = true;

    if (!isEdit) {
        String v = fMaNV.getText().trim();
        if (v.isEmpty()) {
            setErr(errMaNV, "Vui lòng nhập thông tin!");
            ok = false;
        } 
        else if (!v.matches("NV\\d{3,}")) {
            setErr(errMaNV, "Dạng NV + ≥3 số. VD: NV001");
            ok = false;
        }
    }

    String ht = fHoten.getText().trim();
    if (ht.isEmpty()) {
        setErr(errHoten, "Vui lòng nhập thông tin!");
        ok = false;
    } 
    else if (ht.length() < 3) {
        setErr(errHoten, "Ít nhất 3 ký tự!");
        ok = false;
    }

    String sdt = fSdt.getText().trim();
    if (!sdt.isEmpty() && !sdt.matches("0[0-9]{9}")) {
        setErr(errSdt, "SĐT không hợp lệ!");
        ok = false;
    }

    String tdn = fTendangnhap.getText().trim();
    if (tdn.isEmpty()) {
        setErr(errTendangnhap, "Vui lòng nhập thông tin!");
        ok = false;
    } 
    else if (tdn.length() < 4) {
        setErr(errTendangnhap, "Ít nhất 4 ký tự!");
        ok = false;
    } 
    else if (!tdn.matches("[a-zA-Z0-9_.]+")) {
        setErr(errTendangnhap, "Chỉ a-z, 0-9, _ và .");
        ok = false;
    }

    String mk = new String(fMatkhau.getPassword()).trim();
    String mk2 = new String(fXacNhanMK.getPassword()).trim();

    if (!isEdit && mk.isEmpty()) {
        setErr(errMatkhau, "Vui lòng nhập thông tin!");
        ok = false;
    } 
    else if (!mk.isEmpty() && mk.length() < 6) {
        setErr(errMatkhau, "Ít nhất 6 ký tự!");
        ok = false;
    } 
    else if (!mk.isEmpty() && !mk.equals(mk2)) {
        setErr(errXacNhan, "Mật khẩu không khớp!");
        ok = false;
    }

    return ok;
}

private void clearErr() {
    for (JLabel l : new JLabel[]{errMaNV, errHoten, errSdt, errTendangnhap, errMatkhau, errXacNhan})
        l.setText(" ");
}

private void setErr(JLabel l, String m) {
    l.setText("! " + m);
}

public NhanVien getKetQua() {
    return ketQua;
}
    // =========================================================
    // UI HELPERS
    // =========================================================
    private JLabel secLbl(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Dialog", Font.BOLD, 10));
        l.setForeground(new Color(130, 133, 255));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        l.setBorder(new MatteBorder(0,0,1,0,new Color(30,42,72,140)));
        return l;
    }
    private JPanel blk(String lbTxt, JComponent input, JLabel err) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel lb = new JLabel(lbTxt);
        lb.setFont(new Font("Dialog", Font.BOLD, 11));
        lb.setForeground(TEXT2);
        lb.setAlignmentX(LEFT_ALIGNMENT);
        lb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 16));

        // Khoa chieu cao input = 36px, tranh FakeCombo bi BoxLayout keo giãn
        input.setAlignmentX(LEFT_ALIGNMENT);
        input.setPreferredSize(new Dimension(10, 36));   // width=10 se bi override boi BoxLayout
        input.setMinimumSize(new Dimension(10, 36));
        input.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        err.setAlignmentX(LEFT_ALIGNMENT);
        err.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));

        p.add(lb);
        p.add(Box.createVerticalStrut(4));
        p.add(input);
        p.add(err);
        return p;
    }
    private JPanel row2(JPanel l, JPanel r) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.setOpaque(false);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));
        // hai blk phai bang nhau chieu ngang
        l.setAlignmentY(TOP_ALIGNMENT);
        r.setAlignmentY(TOP_ALIGNMENT);
        p.add(l); p.add(Box.createHorizontalStrut(14)); p.add(r);
        return p;
    }
    private JPanel row1(JPanel f) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.setOpaque(false);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));
        f.setAlignmentY(TOP_ALIGNMENT);
        p.add(f);
        return p;
    }
    private JTextField mkField() {
        JTextField f = new JTextField(); f.setBackground(CARD); f.setForeground(TEXT1); f.setCaretColor(ACCENT);
        f.setFont(new Font("Dialog",Font.PLAIN,13));
        f.setBorder(BorderFactory.createCompoundBorder(new LineBorder(BORDER_C,1,true),BorderFactory.createEmptyBorder(7,10,7,10)));
        addFocusBorder(f); return f;
    }
    private JPasswordField mkPassField() {
        JPasswordField f = new JPasswordField(); f.setBackground(CARD); f.setForeground(TEXT1); f.setCaretColor(ACCENT);
        f.setFont(new Font("Dialog",Font.PLAIN,13));
        f.setBorder(BorderFactory.createCompoundBorder(new LineBorder(BORDER_C,1,true),BorderFactory.createEmptyBorder(7,10,7,10)));
        addFocusBorder(f); return f;
    }
    private JLabel errLbl() { JLabel l=new JLabel(" "); l.setFont(new Font("Dialog",Font.PLAIN,11)); l.setForeground(ERR_CLR); return l; }
    private Component vg(int h) { return Box.createVerticalStrut(h); }
    private void addFocusBorder(JComponent c) {
        c.addFocusListener(new FocusAdapter(){
            public void focusGained(FocusEvent e){ c.setBorder(BorderFactory.createCompoundBorder(new LineBorder(ACCENT,1,true),BorderFactory.createEmptyBorder(7,10,7,10))); }
            public void focusLost(FocusEvent e)  { c.setBorder(BorderFactory.createCompoundBorder(new LineBorder(BORDER_C,1,true),BorderFactory.createEmptyBorder(7,10,7,10))); }
        });
    }
    private static String s(String v) { return v!=null?v:""; }

    // =========================================================
    // INNER: FlatBtn — JPanel tu ve, khong dung JButton
    // =========================================================
    static class FlatBtn extends JPanel {
        private final String text;
        private final Color bg, fg;
        private Color borderColor = null;
        private boolean hover = false;
        private Runnable onClick;

        FlatBtn(String text, Color bg, Color fg, int w, int h) {
            this.text = text; this.bg = bg; this.fg = fg;
            setOpaque(false);
            setPreferredSize(new Dimension(w, h));
            setMaximumSize(new Dimension(w+40, h));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hover=true;  repaint(); }
                public void mouseExited(MouseEvent e)  { hover=false; repaint(); }
                public void mouseClicked(MouseEvent e) { if (onClick!=null) onClick.run(); }
            });
        }
        void setBorderColor(Color c)      { this.borderColor = c; }
        void addClickListener(Runnable r) { this.onClick = r; }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(hover ? bg.brighter() : bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            if (borderColor != null) {
                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
            }
            g2.setFont(new Font("Dialog", Font.BOLD, 13));
            g2.setColor(fg);
            FontMetrics fm = g2.getFontMetrics();
            int tx = (getWidth()  - fm.stringWidth(text)) / 2;
            int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(text, tx, ty);
            g2.dispose();
        }
    }

    // =========================================================
    // INNER: FakeCombo — JPanel tu ve, khong dung JComboBox
    // =========================================================
    static class FakeCombo extends JPanel {
        private final String[] items;
        private int selectedIndex = 0;
        private final Color bg, fg;

        FakeCombo(String[] items, Color bg, Color fg) {
            this.items = items; this.bg = bg; this.fg = fg;
            setOpaque(false);
            setPreferredSize(new Dimension(100, 36));
            setMinimumSize(new Dimension(60,  36));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            JPopupMenu popup = buildPopup();
            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) { popup.setPreferredSize(
                        new Dimension(getWidth(), popup.getPreferredSize().height)
                );

                    Point p = getLocationOnScreen();
                    popup.setLocation(p.x, p.y + getHeight());
                    popup.setInvoker(FakeCombo.this);
                    popup.setVisible(true);}
            });
        }

        private JPopupMenu buildPopup() {
            JPopupMenu pm = new JPopupMenu();
            pm.setBackground(bg);
            pm.setBorder(new LineBorder(new Color(50,68,110),1));
            for (int i = 0; i < items.length; i++) {
                final int idx = i;
                JMenuItem mi = new JMenuItem(items[i]);
                mi.setBackground(bg); mi.setForeground(fg);
                mi.setFont(new Font("Dialog",Font.PLAIN,13));
                mi.setBorder(BorderFactory.createEmptyBorder(6,10,6,6));
                mi.setOpaque(true);
                mi.addActionListener(e -> { selectedIndex=idx; repaint(); });
                pm.add(mi);
            }
            return pm;
        }

        String getSelected() { return items[selectedIndex]; }
        void setSelectedItem(String val) {
            if (val==null) return;
            for (int i=0;i<items.length;i++)
                if (items[i].equalsIgnoreCase(val)){selectedIndex=i;break;}
            repaint();
        }
        void setSelectedIndex(int i) { if(i>=0&&i<items.length){selectedIndex=i;repaint();} }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Nen
            g2.setColor(bg);
            g2.fillRoundRect(0,0,getWidth(),getHeight(),6,6);
            // Vien
            g2.setColor(new Color(50,68,110));
            g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,6,6);
            // Text
            g2.setFont(new Font("Dialog",Font.PLAIN,13));
            g2.setColor(fg);
            FontMetrics fm = g2.getFontMetrics();
            int ty = (getHeight()+fm.getAscent()-fm.getDescent())/2;
            g2.drawString(items[selectedIndex], 12, ty);
            // Mui ten
            g2.setColor(new Color(140,155,175));
            int ax = getWidth()-16, ay = getHeight()/2;
            int[] px = {ax-4, ax+4, ax};
            int[] py = {ay-2, ay-2, ay+3};
            g2.fillPolygon(px, py, 3);
            g2.dispose();
        }
    }
}