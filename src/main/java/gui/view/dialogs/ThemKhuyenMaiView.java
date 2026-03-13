package gui.view.dialogs;

import entity.KhuyenMai;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.*;
import java.util.Date;

public class ThemKhuyenMaiView extends JPanel {

    private static final Color BG      = new Color(7,  10, 20);
    private static final Color CARD    = new Color(14, 20, 40);
    private static final Color CARD2   = new Color(20, 28, 52);
    private static final Color BORDER  = new Color(30, 42, 72);
    private static final Color ACCENT  = new Color(99, 102, 241);
    private static final Color TEXT1   = new Color(226, 232, 240);
    private static final Color TEXT2   = new Color(100, 116, 139);
    private static final Color GREEN   = new Color(16, 185, 129);
    private static final Color YELLOW  = new Color(245, 158, 11);
    private static final Color RED     = new Color(239, 68, 68);

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final JTextField        txtTenKM;
    private final JComboBox<String> cbLoaiKM;
    private final JTextField        txtGiaTri;
    private final JTextField        txtGiamToiDa;
    private final JTextField        txtDonToiThieu;
    private final JTextField        txtNgayBatDau;
    private final JTextField        txtNgayKetThuc;
    private final JTextField        txtSoLuong;
    private final JLabel            lblGiaTri;

    // Giá trị thực sau khi chọn từ lịch
    private LocalDateTime dateBatDau;
    private LocalDateTime dateKetThuc;

    public ThemKhuyenMaiView(KhuyenMai kmEdit) {
        boolean isSua = (kmEdit != null);

        setBackground(BG);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel lblTitle = new JLabel(isSua ? "✏  Cập nhật khuyến mãi" : "+  Thêm khuyến mãi mới");
        lblTitle.setFont(new Font("Dialog", Font.BOLD, 16));
        lblTitle.setForeground(TEXT1);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
        add(lblTitle, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridBagLayout());
        grid.setBackground(CARD);
        grid.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)));

        GridBagConstraints lc = labelGBC();
        GridBagConstraints fc = fieldGBC();

        txtTenKM       = makeTextField(24);
        txtGiaTri      = makeTextField(12);
        txtGiamToiDa   = makeTextField(12);
        txtDonToiThieu = makeTextField(12);
        txtSoLuong     = makeTextField(8);
        lblGiaTri      = makeLabel("Giá trị giảm (%) *");

        // ── Trường ngày: read-only, click để mở lịch ─────
        txtNgayBatDau  = makeDateField("Nhấp để chọn ngày bắt đầu...");
        txtNgayKetThuc = makeDateField("Nhấp để chọn ngày kết thúc...");

        txtNgayBatDau.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { openCalendar(true); }
        });
        txtNgayKetThuc.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { openCalendar(false); }
        });

        // ── ComboBox ──────────────────────────────────────
        UIManager.put("ComboBox.background",          CARD2);
        UIManager.put("ComboBox.foreground",          TEXT1);
        UIManager.put("ComboBox.selectionBackground", ACCENT);
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);

        cbLoaiKM = new JComboBox<>(new String[]{"PHANTRAM", "TIENCODINH"});
        cbLoaiKM.setFont(new Font("Dialog", Font.PLAIN, 13));
        cbLoaiKM.setBackground(CARD2);
        cbLoaiKM.setForeground(TEXT1);
        cbLoaiKM.setOpaque(true);
        cbLoaiKM.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list,
                                                                    Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel l = new JLabel(value == null ? "" : value.toString());
                l.setOpaque(true);
                l.setFont(new Font("Dialog", Font.PLAIN, 13));
                l.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
                l.setBackground(isSelected && index >= 0 ? ACCENT : CARD2);
                l.setForeground(isSelected && index >= 0 ? Color.WHITE : TEXT1);
                return l;
            }
        });
        cbLoaiKM.addActionListener(e -> onLoaiChanged());

        // ── Điền sẵn nếu sửa ─────────────────────────────
        if (isSua) {
            txtTenKM.setText(kmEdit.getTenKM());
            cbLoaiKM.setSelectedItem(kmEdit.getLoaiKM());
            txtGiaTri.setText(String.valueOf(kmEdit.getGiatrigiam()));
            txtGiamToiDa.setText(String.valueOf(kmEdit.getGiamtoida()));
            txtDonToiThieu.setText(String.valueOf(kmEdit.getGiatridonhangtoithieu()));
            txtSoLuong.setText(String.valueOf(kmEdit.getSoluong()));
            if (kmEdit.getNgaybatdau() != null) {
                dateBatDau = kmEdit.getNgaybatdau();
                setDateField(txtNgayBatDau, dateBatDau);
            }
            if (kmEdit.getNgayketthuc() != null) {
                dateKetThuc = kmEdit.getNgayketthuc();
                setDateField(txtNgayKetThuc, dateKetThuc);
            }
        }

        // ── Rows ──────────────────────────────────────────
        int row = 0;
        addRow(grid, lc, fc, row++, makeLabel("Tên khuyến mãi *"),   txtTenKM);
        addRow(grid, lc, fc, row++, makeLabel("Loại khuyến mãi *"),  cbLoaiKM);
        addRow(grid, lc, fc, row++, lblGiaTri,                       txtGiaTri);
        addRow(grid, lc, fc, row++, makeLabel("Giảm tối đa (VNĐ)"),  txtGiamToiDa);
        addRow(grid, lc, fc, row++, makeLabel("Đơn hàng tối thiểu"), txtDonToiThieu);
        addRow(grid, lc, fc, row++, makeLabel("Ngày bắt đầu *"),     txtNgayBatDau);
        addRow(grid, lc, fc, row++, makeLabel("Ngày kết thúc *"),    txtNgayKetThuc);
        addRow(grid, lc, fc, row++, makeLabel("Số lượng *"),         txtSoLuong);

        add(grid, BorderLayout.CENTER);

        JLabel note = makeNote("* Bắt buộc điền  |  Nhấp vào ô ngày để mở lịch");
        note.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        add(note, BorderLayout.SOUTH);

        onLoaiChanged();
    }

    // ── Mở lịch ──────────────────────────────────────────
    private void openCalendar(boolean isBatDau) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        Frame frame   = (parent instanceof Frame) ? (Frame) parent : null;

        LocalDateTime initial = isBatDau
                ? (dateBatDau  != null ? dateBatDau  : LocalDateTime.now())
                : (dateKetThuc != null ? dateKetThuc : LocalDateTime.now());

        CalendarDialog cal = new CalendarDialog(frame, initial);
        cal.setVisible(true);

        LocalDateTime result = cal.getResult();
        if (result != null) {
            if (isBatDau) { dateBatDau  = result; setDateField(txtNgayBatDau,  result); }
            else          { dateKetThuc = result; setDateField(txtNgayKetThuc, result); }
        }
    }

    private void setDateField(JTextField f, LocalDateTime dt) {
        f.setText(dt.format(FMT));
        f.setForeground(TEXT1);
    }

    // ── Khi đổi loại ─────────────────────────────────────
    private void onLoaiChanged() {
        boolean isPhanTram = !"TIENCODINH".equals(cbLoaiKM.getSelectedItem());
        lblGiaTri.setText(isPhanTram ? "Giá trị giảm (%) *" : "Giá trị giảm (đ) *");
        lblGiaTri.setForeground(isPhanTram ? GREEN : YELLOW);
        txtGiaTri.setToolTipText(isPhanTram ? "Nhập % từ 1–100" : "Nhập số tiền VNĐ");
        txtGiamToiDa.setEnabled(isPhanTram);
        txtGiamToiDa.setBackground(isPhanTram ? CARD2 : new Color(14, 20, 38));
        txtGiamToiDa.setForeground(isPhanTram ? TEXT1 : TEXT2);
        if (!isPhanTram) txtGiamToiDa.setText("");
    }

    // ── Build entity ──────────────────────────────────────
    public KhuyenMai buildKhuyenMai(String maKM) throws Exception {
        String ten = txtTenKM.getText().trim();
        if (ten.isEmpty()) throw new Exception("Tên khuyến mãi không được để trống!");

        String loai   = (String) cbLoaiKM.getSelectedItem();
        double giaTri = parseDouble(txtGiaTri.getText(), "Giá trị giảm");
        if ("TIENCODINH".equals(loai)) {
            if (giaTri <= 0) throw new Exception("Giá trị giảm tiền cố định phải > 0đ!");
        } else {
            if (giaTri <= 0 || giaTri > 100) throw new Exception("Giá trị % phải từ 1–100!");
        }

        double giamToiDa   = parseDoubleOpt(txtGiamToiDa.getText(),  "Giảm tối đa");
        double donToiThieu = parseDoubleOpt(txtDonToiThieu.getText(), "Đơn hàng tối thiểu");
        int    soLuong     = parseInt(txtSoLuong.getText(), "Số lượng");
        if (soLuong <= 0) throw new Exception("Số lượng phải > 0!");

        if (dateBatDau  == null) throw new Exception("Vui lòng chọn ngày bắt đầu!");
        if (dateKetThuc == null) throw new Exception("Vui lòng chọn ngày kết thúc!");
        if (!dateKetThuc.isAfter(dateBatDau))
            throw new Exception("Ngày kết thúc phải sau ngày bắt đầu!");

        KhuyenMai km = new KhuyenMai();
        km.setMaKM(maKM); km.setTenKM(ten); km.setLoaiKM(loai);
        km.setGiatrigiam(giaTri); km.setGiamtoida(giamToiDa);
        km.setGiatridonhangtoithieu(donToiThieu);
        km.setNgaybatdau(dateBatDau); km.setNgayketthuc(dateKetThuc);
        km.setSoluong(soLuong); km.setDasudung(0);
        return km;
    }

    // ════════════════════════════════════════════════════
    //  Inner class: Popup lịch chọn ngày + giờ
    // ════════════════════════════════════════════════════
    private static class CalendarDialog extends JDialog {

        private static final Color BG2     = new Color(7,  10, 20);
        private static final Color SURFACE = new Color(11, 15, 30);
        private static final Color CARD3   = new Color(14, 20, 40);
        private static final Color BORDER2 = new Color(30, 42, 72);
        private static final Color ACCENT2 = new Color(99, 102, 241);
        private static final Color TEXT_1  = new Color(226, 232, 240);
        private static final Color TEXT_2  = new Color(100, 116, 139);
        private static final Color TODAY_C = new Color(30, 42, 90);
        private static final Color HOVER_C = new Color(25, 35, 70);

        private static final String[] HEADERS = {"T2","T3","T4","T5","T6","T7","CN"};

        private LocalDate  curMonth;   // tháng đang hiển thị
        private LocalDate  selected;   // ngày đã chọn
        private LocalDateTime result;

        private JLabel     lblMonthYear;
        private JPanel     dayGrid;
        private JSpinner   spinH, spinM;

        CalendarDialog(Frame parent, LocalDateTime initial) {
            super(parent, "Chọn ngày & giờ", true);
            this.curMonth = initial.toLocalDate().withDayOfMonth(1);
            this.selected = initial.toLocalDate();

            getContentPane().setBackground(BG2);
            setLayout(new BorderLayout());
            getRootPane().setBorder(new LineBorder(BORDER2));

            add(buildTop(),    BorderLayout.NORTH);
            add(buildGrid(),   BorderLayout.CENTER);
            add(buildFooter(initial), BorderLayout.SOUTH);

            buildDays();
            pack();
            setResizable(false);
            setLocationRelativeTo(parent);
        }

        // ── Tháng năm + nút prev/next ─────────────────
        private JPanel buildTop() {
            JPanel p = new JPanel(new BorderLayout(8, 0));
            p.setBackground(BG2);
            p.setBorder(BorderFactory.createEmptyBorder(14, 16, 6, 16));

            lblMonthYear = new JLabel("", SwingConstants.CENTER);
            lblMonthYear.setFont(new Font("Dialog", Font.BOLD, 13));
            lblMonthYear.setForeground(TEXT_1);

            JButton prev = arrowBtn("  <  ");
            JButton next = arrowBtn("  >  ");
            prev.addActionListener(e -> { curMonth = curMonth.minusMonths(1); buildDays(); });
            next.addActionListener(e -> { curMonth = curMonth.plusMonths(1);  buildDays(); });

            p.add(prev, BorderLayout.WEST);
            p.add(lblMonthYear, BorderLayout.CENTER);
            p.add(next, BorderLayout.EAST);
            return p;
        }

        // ── Khung lưới ngày ───────────────────────────
        private JPanel buildGrid() {
            JPanel wrap = new JPanel(new BorderLayout(0, 4));
            wrap.setBackground(BG2);
            wrap.setBorder(BorderFactory.createEmptyBorder(4, 12, 8, 12));

            // Header: T2 T3 ... CN
            JPanel header = new JPanel(new GridLayout(1, 7, 4, 0));
            header.setBackground(BG2);
            for (String h : HEADERS) {
                JLabel l = new JLabel(h, SwingConstants.CENTER);
                l.setFont(new Font("Dialog", Font.BOLD, 11));
                l.setForeground(TEXT_2);
                header.add(l);
            }

            dayGrid = new JPanel(new GridLayout(0, 7, 4, 4));
            dayGrid.setBackground(BG2);

            wrap.add(header,  BorderLayout.NORTH);
            wrap.add(dayGrid, BorderLayout.CENTER);
            return wrap;
        }

        // ── Điền ngày vào lưới ────────────────────────
        private void buildDays() {
            lblMonthYear.setText(
                    curMonth.getMonth().getDisplayName(TextStyle.FULL, new java.util.Locale("vi"))
                            + "  " + curMonth.getYear());

            dayGrid.removeAll();

            // Offset: thứ 2 = 0, CN = 6
            int offset = curMonth.getDayOfWeek().getValue() - 1;
            LocalDate today = LocalDate.now();

            for (int i = 0; i < offset; i++)
                dayGrid.add(emptyCell());

            int days = curMonth.lengthOfMonth();
            for (int d = 1; d <= days; d++) {
                LocalDate date = curMonth.withDayOfMonth(d);
                boolean isSel   = date.equals(selected);
                boolean isToday = date.equals(today);

                JButton btn = new JButton(String.valueOf(d));
                btn.setFont(new Font("Dialog", isSel ? Font.BOLD : Font.PLAIN, 12));
                btn.setPreferredSize(new Dimension(46, 30));
                btn.setFocusPainted(false);
                btn.setBorderPainted(false);
                btn.setOpaque(true);
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                Color normalBg = isToday ? TODAY_C : BG2;
                btn.setBackground(isSel ? ACCENT2 : normalBg);
                btn.setForeground(isSel ? Color.WHITE : (isToday ? ACCENT2 : TEXT_1));

                btn.addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) {
                        if (!date.equals(selected)) btn.setBackground(HOVER_C);
                    }
                    public void mouseExited(MouseEvent e) {
                        if (!date.equals(selected)) btn.setBackground(normalBg);
                    }
                });
                btn.addActionListener(e -> {
                    selected = date;
                    buildDays();  // re-render để highlight đúng
                });
                dayGrid.add(btn);
            }

            // Fill ô trống cuối hàng
            int rem = (offset + days) % 7;
            if (rem != 0) for (int i = rem; i < 7; i++) dayGrid.add(emptyCell());

            dayGrid.revalidate();
            dayGrid.repaint();
        }

        // ── Footer: giờ phút + nút ────────────────────
        private JPanel buildFooter(LocalDateTime init) {
            JPanel p = new JPanel(new BorderLayout());
            p.setBackground(SURFACE);
            p.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1,0,0,0, BORDER2),
                    BorderFactory.createEmptyBorder(10,14,10,14)));

            // Giờ : phút
            JPanel time = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            time.setBackground(SURFACE);
            JLabel lbl = new JLabel("Giờ:");
            lbl.setForeground(TEXT_2);
            lbl.setFont(new Font("Dialog", Font.BOLD, 12));

            spinH = makeSpinner(0, 23, init.getHour());
            spinM = makeSpinner(0, 59, init.getMinute());

            JLabel colon = new JLabel(":");
            colon.setForeground(TEXT_1);
            colon.setFont(new Font("Dialog", Font.BOLD, 14));

            time.add(lbl); time.add(spinH); time.add(colon); time.add(spinM);

            // Nút
            JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            btns.setBackground(SURFACE);
            JButton btnCancel = footBtn("Hủy",  CARD3,   TEXT_2);
            JButton btnOK     = footBtn("Chọn", ACCENT2, Color.WHITE);
            getRootPane().setDefaultButton(btnOK);

            btnCancel.addActionListener(e -> dispose());
            btnOK.addActionListener(e -> {
                result = LocalDateTime.of(selected,
                        LocalTime.of((int)spinH.getValue(), (int)spinM.getValue()));
                dispose();
            });
            btns.add(btnCancel); btns.add(btnOK);

            p.add(time, BorderLayout.WEST);
            p.add(btns, BorderLayout.EAST);
            return p;
        }

        LocalDateTime getResult() { return result; }

        // ── Helpers ───────────────────────────────────
        private JPanel emptyCell() {
            JPanel c = new JPanel(); c.setBackground(BG2); c.setPreferredSize(new Dimension(42,30));
            return c;
        }

        private JButton arrowBtn(String text) {
            JButton b = new JButton(text);
            b.setFont(new Font("Dialog", Font.BOLD, 13));
            b.setBackground(BG2); b.setForeground(TEXT_1);
            b.setOpaque(true); b.setBorderPainted(false); b.setFocusPainted(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return b;
        }

        private JButton footBtn(String text, Color bg, Color fg) {
            JButton b = new JButton(text);
            b.setFont(new Font("Dialog", Font.BOLD, 12));
            b.setBackground(bg); b.setForeground(fg);
            b.setOpaque(true); b.setBorderPainted(false); b.setFocusPainted(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.setBorder(BorderFactory.createEmptyBorder(7, 18, 7, 18));
            Color hover = bg.brighter();
            b.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { b.setBackground(hover); }
                public void mouseExited (MouseEvent e) { b.setBackground(bg);    }
            });
            return b;
        }

        private JSpinner makeSpinner(int min, int max, int val) {
            JSpinner sp = new JSpinner(new SpinnerNumberModel(val, min, max, 1));
            sp.setPreferredSize(new Dimension(52, 28));
            sp.setBackground(CARD3);
            JComponent ed = sp.getEditor();
            if (ed instanceof JSpinner.DefaultEditor de) {
                de.getTextField().setBackground(new Color(20,28,52));
                de.getTextField().setForeground(TEXT_1);
                de.getTextField().setHorizontalAlignment(SwingConstants.CENTER);
                de.getTextField().setFont(new Font("Dialog", Font.PLAIN, 13));
            }
            return sp;
        }
    }

    // ── Parse helpers ─────────────────────────────────────
    private double parseDouble(String raw, String field) throws Exception {
        try { return Double.parseDouble(raw.trim().replace(",","")); }
        catch (NumberFormatException e) { throw new Exception(field + " phải là số hợp lệ!"); }
    }
    private double parseDoubleOpt(String raw, String field) throws Exception {
        String s = raw.trim().replace(",","");
        return s.isEmpty() ? 0 : parseDouble(s, field);
    }
    private int parseInt(String raw, String field) throws Exception {
        try { return Integer.parseInt(raw.trim()); }
        catch (NumberFormatException e) { throw new Exception(field + " phải là số nguyên!"); }
    }

    // ── Grid helpers ──────────────────────────────────────
    private void addRow(JPanel g, GridBagConstraints lc, GridBagConstraints fc,
                        int row, JLabel label, JComponent field) {
        lc.gridy = fc.gridy = row;
        g.add(label, lc); g.add(field, fc);
    }
    private GridBagConstraints labelGBC() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(7,0,7,16); c.ipadx = 4; return c;
    }
    private GridBagConstraints fieldGBC() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0; c.insets = new Insets(7,0,7,0); return c;
    }

    // ── UI factories ─────────────────────────────────────
    private JTextField makeTextField(int cols) {
        JTextField f = new JTextField(cols);
        f.setBackground(CARD2); f.setForeground(TEXT1); f.setCaretColor(ACCENT);
        f.setFont(new Font("Dialog", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(6,10,6,10)));
        return f;
    }
    private JTextField makeDateField(String placeholder) {
        JTextField f = makeTextField(0);
        f.setText(placeholder);
        f.setForeground(TEXT2);
        f.setEditable(false);
        f.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return f;
    }
    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(TEXT2); l.setFont(new Font("Dialog", Font.BOLD, 12)); return l;
    }
    private JLabel makeNote(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(new Color(71,85,105)); l.setFont(new Font("Dialog", Font.ITALIC, 11));
        return l;
    }
}