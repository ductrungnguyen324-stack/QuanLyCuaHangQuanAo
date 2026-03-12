package gui.view.dialogs;

import entity.KhuyenMai;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.LocalDateTime;

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

    private final JTextField        txtTenKM;
    private final JComboBox<String> cbLoaiKM;
    private final JTextField        txtGiaTri;
    private final JTextField        txtGiamToiDa;
    private final JTextField        txtDonToiThieu;
    private final DatePickerField   dpNgayBatDau;
    private final DatePickerField   dpNgayKetThuc;
    private final JTextField        txtSoLuong;

    // Label động cho Giá trị giảm — thay đổi theo loại
    private final JLabel lblGiaTri;

    public ThemKhuyenMaiView(KhuyenMai kmEdit) {
        boolean isSua = (kmEdit != null);

        setBackground(BG);
        setLayout(new BorderLayout(0, 0));
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
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));

        GridBagConstraints lc = labelGBC();
        GridBagConstraints fc = fieldGBC();

        // ── Khởi tạo fields ───────────────────────────────
        txtTenKM       = makeTextField(24);
        txtGiaTri      = makeTextField(12);
        txtGiamToiDa   = makeTextField(12);
        txtDonToiThieu = makeTextField(12);
        txtSoLuong     = makeTextField(8);
        dpNgayBatDau   = new DatePickerField("Chọn ngày bắt đầu...");
        dpNgayKetThuc  = new DatePickerField("Chọn ngày kết thúc...");

        // ── Label động Giá trị giảm ───────────────────────
        lblGiaTri = makeLabel("Giá trị giảm (%) *");

        // ── ComboBox — fix màu toàn bộ kể cả editor ─────
        // Dùng UIManager để ép buộc màu bất kể LAF nào
        UIManager.put("ComboBox.background",       CARD2);
        UIManager.put("ComboBox.foreground",       TEXT1);
        UIManager.put("ComboBox.selectionBackground", ACCENT);
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);

        cbLoaiKM = new JComboBox<>(new String[]{"PHANTRAM", "TIENCODINH"});
        cbLoaiKM.setFont(new Font("Dialog", Font.PLAIN, 13));
        cbLoaiKM.setBackground(CARD2);
        cbLoaiKM.setForeground(TEXT1);
        cbLoaiKM.setOpaque(true);

        // Force-style editor component (phần text hiển thị khi đóng)
        if (cbLoaiKM.getEditor() != null) {
            Component edComp = cbLoaiKM.getEditor().getEditorComponent();
            edComp.setBackground(CARD2);
            edComp.setForeground(TEXT1);
            edComp.setFont(new Font("Dialog", Font.PLAIN, 13));
        }

        // Renderer — trả về JLabel mới thay vì dùng super (tránh LAF ghi đè)
        cbLoaiKM.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = new JLabel(value == null ? "" : value.toString());
                lbl.setOpaque(true);
                lbl.setFont(new Font("Dialog", Font.PLAIN, 13));
                lbl.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
                // index == -1: item hiển thị trong khung đóng
                if (isSelected && index >= 0) {
                    lbl.setBackground(ACCENT);
                    lbl.setForeground(Color.WHITE);
                } else {
                    lbl.setBackground(CARD2);
                    lbl.setForeground(TEXT1);  // luôn sáng bất kể trạng thái
                }
                return lbl;
            }
        });

        // Khi đổi loại → cập nhật label + toggle giảm tối đa
        cbLoaiKM.addActionListener(e -> onLoaiChanged());

        // ── Điền sẵn nếu sửa ─────────────────────────────
        if (isSua) {
            txtTenKM.setText(kmEdit.getTenKM());
            cbLoaiKM.setSelectedItem(kmEdit.getLoaiKM());
            txtGiaTri.setText(String.valueOf(kmEdit.getGiatrigiam()));
            txtGiamToiDa.setText(String.valueOf(kmEdit.getGiamtoida()));
            txtDonToiThieu.setText(String.valueOf(kmEdit.getGiatridonhangtoithieu()));
            dpNgayBatDau.setValue(kmEdit.getNgaybatdau());
            dpNgayKetThuc.setValue(kmEdit.getNgayketthuc());
            txtSoLuong.setText(String.valueOf(kmEdit.getSoluong()));
        }

        // ── Xây dựng grid ─────────────────────────────────
        int row = 0;
        addRow (grid, lc, fc, row++, makeLabel("Tên khuyến mãi *"),   txtTenKM);
        addRow (grid, lc, fc, row++, makeLabel("Loại khuyến mãi *"),  cbLoaiKM);
        addRowCustomLabel(grid, lc, fc, row++, lblGiaTri, txtGiaTri);
        addRow (grid, lc, fc, row++, makeLabel("Giảm tối đa (VNĐ)"),  txtGiamToiDa);
        addRow (grid, lc, fc, row++, makeLabel("Đơn hàng tối thiểu"), txtDonToiThieu);
        addRow (grid, lc, fc, row++, makeLabel("Ngày bắt đầu *"),     dpNgayBatDau);
        addRow (grid, lc, fc, row++, makeLabel("Ngày kết thúc *"),    dpNgayKetThuc);
        addRow (grid, lc, fc, row++, makeLabel("Số lượng *"),         txtSoLuong);

        add(grid, BorderLayout.CENTER);

        JLabel lblNote = makeNote("* Bắt buộc điền");
        lblNote.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        add(lblNote, BorderLayout.SOUTH);

        // Khởi tạo trạng thái ban đầu
        onLoaiChanged();
    }

    // ── Cập nhật khi đổi loại ─────────────────────────────
    private void onLoaiChanged() {
        boolean isPhanTram = "PHANTRAM".equals(cbLoaiKM.getSelectedItem());

        // Cập nhật label động
        if (isPhanTram) {
            lblGiaTri.setText("Giá trị giảm (%) *");
            lblGiaTri.setForeground(GREEN);
            txtGiaTri.setToolTipText("Nhập số % (vd: 10 = giảm 10%)");
        } else {
            lblGiaTri.setText("Giá trị giảm (đ) *");
            lblGiaTri.setForeground(YELLOW);
            txtGiaTri.setToolTipText("Nhập số tiền VNĐ (vd: 50000 = giảm 50.000đ)");
        }

        // Giảm tối đa chỉ áp dụng cho PHANTRAM
        txtGiamToiDa.setEnabled(isPhanTram);
        txtGiamToiDa.setBackground(isPhanTram ? CARD2 : new Color(14, 20, 38));
        txtGiamToiDa.setForeground(isPhanTram ? TEXT1 : TEXT2);
        if (!isPhanTram) {
            txtGiamToiDa.setText("");
            txtGiamToiDa.setToolTipText("Chỉ áp dụng khi loại là PHANTRAM");
        } else {
            txtGiamToiDa.setToolTipText("Mức giảm tối đa (VNĐ)");
        }
    }

    // ── Build KhuyenMai từ form ───────────────────────────
    public KhuyenMai buildKhuyenMai(String maKM) throws Exception {
        String ten = txtTenKM.getText().trim();
        if (ten.isEmpty()) throw new Exception("Tên khuyến mãi không được để trống!");

        String loai = (String) cbLoaiKM.getSelectedItem();

        // Validate giá trị giảm theo loại
        double giaTri = parseDouble(txtGiaTri.getText(), "Giá trị giảm");
        if ("PHANTRAM".equals(loai)) {
            if (giaTri <= 0 || giaTri > 100)
                throw new Exception("Giá trị giảm theo % phải từ 1 đến 100!");
        } else {
            if (giaTri <= 0)
                throw new Exception("Giá trị giảm tiền cố định phải lớn hơn 0đ!");
        }

        double giamToiDa   = parseDoubleOpt(txtGiamToiDa.getText(),   "Giảm tối đa");
        double donToiThieu = parseDoubleOpt(txtDonToiThieu.getText(),  "Đơn hàng tối thiểu");

        LocalDateTime batDau  = dpNgayBatDau.getValue();
        LocalDateTime ketThuc = dpNgayKetThuc.getValue();
        if (batDau  == null) throw new Exception("Vui lòng chọn ngày bắt đầu!");
        if (ketThuc == null) throw new Exception("Vui lòng chọn ngày kết thúc!");
        if (!ketThuc.isAfter(batDau))
            throw new Exception("Ngày kết thúc phải sau ngày bắt đầu!");

        int soLuong = parseInt(txtSoLuong.getText(), "Số lượng");
        if (soLuong <= 0) throw new Exception("Số lượng phải lớn hơn 0!");

        KhuyenMai km = new KhuyenMai();
        km.setMaKM(maKM);
        km.setTenKM(ten);
        km.setLoaiKM(loai);
        km.setGiatrigiam(giaTri);
        km.setGiamtoida(giamToiDa);
        km.setGiatridonhangtoithieu(donToiThieu);
        km.setNgaybatdau(batDau);
        km.setNgayketthuc(ketThuc);
        km.setSoluong(soLuong);
        km.setDasudung(0);
        return km;
    }

    // ── Parse helpers ─────────────────────────────────────
    private double parseDouble(String raw, String field) throws Exception {
        try { return Double.parseDouble(raw.trim().replace(",", "")); }
        catch (NumberFormatException e) { throw new Exception(field + " phải là số hợp lệ!"); }
    }
    private double parseDoubleOpt(String raw, String field) throws Exception {
        String s = raw.trim().replace(",", "");
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
        g.add(label, lc);
        g.add(field, fc);
    }
    // Dùng khi label cần giữ reference bên ngoài (label động)
    private void addRowCustomLabel(JPanel g, GridBagConstraints lc, GridBagConstraints fc,
                                   int row, JLabel label, JComponent field) {
        lc.gridy = fc.gridy = row;
        g.add(label, lc);
        g.add(field, fc);
    }

    private GridBagConstraints labelGBC() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(7, 0, 7, 16); c.ipadx = 4; return c;
    }
    private GridBagConstraints fieldGBC() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0; c.insets = new Insets(7, 0, 7, 0); return c;
    }

    // ── UI factories ─────────────────────────────────────
    private JTextField makeTextField(int cols) {
        JTextField f = new JTextField(cols);
        f.setBackground(CARD2); f.setForeground(TEXT1); f.setCaretColor(ACCENT);
        f.setFont(new Font("Dialog", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        return f;
    }
    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(TEXT2);
        l.setFont(new Font("Dialog", Font.BOLD, 12));
        return l;
    }
    private JLabel makeNote(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(new Color(71, 85, 105));
        l.setFont(new Font("Dialog", Font.ITALIC, 11));
        return l;
    }
}