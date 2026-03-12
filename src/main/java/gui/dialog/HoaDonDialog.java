package gui;
import dao.ChiTietHoaDonDAO;
import dao.HoaDonDAO;
import gui.dialog.*;
import bus.KhuyenMaiBUS;
import bus.SanPhamBUS;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.KhuyenMai;
import entity.SanPham;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;


public class HoaDonDialog extends JDialog {

    private static final Color BG     = new Color(10, 14, 30);
    private static final Color CARD   = new Color(14, 20, 40);
    private static final Color CARD2  = new Color(18, 26, 52);
    private static final Color BORDER = new Color(30, 42, 72);
    private static final Color ACCENT = new Color(99, 102, 241);
    private static final Color GREEN  = new Color(16, 185, 129);
    private static final Color RED    = new Color(239, 68, 68);
    private static final Color YELLOW = new Color(245, 158, 11);
    private static final Color TEXT1  = new Color(226, 232, 240);
    private static final Color TEXT2  = new Color(100, 116, 139);
    private static final Color DANGER = new Color(239, 68, 68);

    private String ketQua = null;
    private String maNV   = null; // nhận từ session

    // Thong tin HD
    private JTextField     fMaHD, fMaKH;
    private JButton        btnKMChon;   // nút chọn KM, text đổi thành mã sau khi chọn
    private KhuyenMai      selectedKM;  // object KM đang chọn
    private JLabel         lblKMInfo;   // chip info KM
    private JComboBox<String> fPhuongThuc;
    private JLabel         lblStatus;
    private JTable ctTable;

    // Bang chi tiet san pham
    private DefaultTableModel ctModel;
    private JLabel            lblTongTien, lblGiamGia, lblThanhToan;
    // model
    KhuyenMaiBUS kmbus = new KhuyenMaiBUS();
    SanPhamBUS   spbus = new SanPhamBUS();
    HoaDonDAO hddao = new HoaDonDAO();

    public HoaDonDialog(Frame parent, String maHD, String maNV) {
        super(parent, true);
        this.maNV = maNV;
        setTitle(maHD != null ? "Sửa hoá đơn" : "Tạo hoá đơn mới");
        setSize(700, 600);
        setLocationRelativeTo(parent);
        setResizable(false);
        buildUI();
        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    private void buildUI() {
        JPanel main = new JPanel(new BorderLayout(0, 0));
        main.setBackground(BG);

        // Header
        main.add(buildHeader(), BorderLayout.NORTH);

        // Body = info tren + bang CTHD duoi
        JPanel body = new JPanel(new BorderLayout(0, 14));
        body.setBackground(BG);
        body.setBorder(BorderFactory.createEmptyBorder(16, 20, 0, 20));
        body.add(buildInfoForm(), BorderLayout.NORTH);
        body.add(buildCTHDTable(), BorderLayout.CENTER);
        main.add(body, BorderLayout.CENTER);

        // Footer: tong tien + nut
        main.add(buildFooter(), BorderLayout.SOUTH);
        setContentPane(main);
    }

    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(new Color(11, 16, 35));
        h.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                BorderFactory.createEmptyBorder(16, 22, 16, 22)
        ));
        JLabel t = new JLabel("Tạo hoá đơn mới");
        t.setFont(new Font("Dialog", Font.BOLD, 16));
        t.setForeground(TEXT1);
        JLabel s = new JLabel("Chọn sản phẩm và điền thông tin");
        s.setFont(new Font("Dialog", Font.PLAIN, 12));
        s.setForeground(TEXT2);
        h.add(t, BorderLayout.NORTH);
        h.add(s, BorderLayout.SOUTH);
        return h;
    }

    // Phan thong tin hoa don (tren)
    private JPanel buildInfoForm() {
        JPanel p = new JPanel(new GridLayout(3, 4, 12, 10));
        p.setBackground(BG);

        // Hàng 1: Mã HD | Mã KH
        fMaHD = addField(p, "Mã HD *", hddao.generateHD());
        fMaKH = addField(p, "Mã KH *", "");

        // Hàng 2: Mã KM (readonly + nút chọn) | Phương thức TT
        p.add(makeLabel("Mã KM"));
        p.add(buildKMPicker());

        JLabel lblPT = makeLabel("Phương thức TT");
        fPhuongThuc = new JComboBox<>(new String[]{
                "TIENMAT","CHUYENKHOAN","MOMO","VNPAY","ZaloPay"
        });
        fPhuongThuc.setBackground(CARD2);
        fPhuongThuc.setForeground(new Color(0,0,0));
        fPhuongThuc.setFont(new Font("Dialog", Font.PLAIN, 12));
        p.add(lblPT);
        p.add(fPhuongThuc);

        // Hàng 3: chip thông tin KM đang chọn
        lblKMInfo = new JLabel(" ");
        lblKMInfo.setFont(new Font("Dialog", Font.ITALIC, 11));
        lblKMInfo.setForeground(GREEN);
        p.add(lblKMInfo);
        p.add(new JLabel()); p.add(new JLabel()); p.add(new JLabel());

        return p;
    }

    /** nút Chọn KM chiếm 7 phần, nút ✕ chiếm 3 phần, fill full width */
    private JPanel buildKMPicker() {
        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setBackground(BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 6);

        btnKMChon = makeButton("Chọn KM", ACCENT, Color.WHITE);
        btnKMChon.setFont(new Font("Dialog", Font.BOLD, 11));
        btnKMChon.addActionListener(e -> openKMPicker());
        gbc.gridx = 0; gbc.weightx = 7;
        wrap.add(btnKMChon, gbc);

        JButton btnXoa = makeButton("✕", new Color(50, 30, 30), RED);
        btnXoa.setFont(new Font("Dialog", Font.BOLD, 11));
        btnXoa.setToolTipText("Bỏ khuyến mãi");
        btnXoa.addActionListener(e -> clearKM());
        gbc.gridx = 1; gbc.weightx = 3; gbc.insets = new Insets(0, 0, 0, 0);
        wrap.add(btnXoa, gbc);

        return wrap;
    }

    /** Mở dialog popup danh sách KM còn khả dụng */
    private void openKMPicker() {
        List<KhuyenMai> danhSach;
        try {
            danhSach = kmbus.getActivePromotions();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Không thể tải danh sách khuyến mãi!\n" + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (danhSach == null || danhSach.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Hiện không có khuyến mãi nào khả dụng.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog picker = new JDialog(this, "Chọn mã khuyến mãi", true);
        picker.setSize(700, 420);
        picker.setLocationRelativeTo(this);
        picker.setResizable(false);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BG);

        // Header picker
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(11, 16, 35));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));
        JLabel title = new JLabel("Danh sách khuyến mãi");
        title.setFont(new Font("Dialog", Font.BOLD, 15));
        title.setForeground(TEXT1);
        JLabel sub = new JLabel("Nhấp đúp hoặc chọn rồi bấm 'Áp dụng'");
        sub.setFont(new Font("Dialog", Font.PLAIN, 11));
        sub.setForeground(TEXT2);
        header.add(title, BorderLayout.NORTH);
        header.add(sub,   BorderLayout.SOUTH);
        main.add(header, BorderLayout.NORTH);

        // Bảng KM: 7 cột
        String[] cols = {"Mã KM", "Tên KM", "Loại", "Giá trị giảm", "Giảm tối đa", "Đơn tối thiểu", "Còn lại"};
        DefaultTableModel kmModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        for (KhuyenMai km : danhSach) {
            String loai = km.getLoaiKM() != null ? km.getLoaiKM() : "-";
            String giatri = "PHANTRAM".equalsIgnoreCase(loai)
                    ? String.format("%.0f%%", km.getGiatrigiam())
                    : String.format("%,.0f đ", km.getGiatrigiam());
            String giamToiDa = "PHANTRAM".equalsIgnoreCase(loai)
                    ? String.format("%,.0f đ", km.getGiamtoida()) : "—";
            String donToiThieu = km.getGiatridonhangtoithieu() > 0
                    ? String.format("%,.0f đ", km.getGiatridonhangtoithieu()) : "Không có";
            String conLai = (km.getSoluong() - km.getDasudung()) + " lượt";
            kmModel.addRow(new Object[]{
                    km.getMaKM(), km.getTenKM(), loai, giatri, giamToiDa, donToiThieu, conLai
            });
        }

        JTable kmTable = new JTable(kmModel) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                c.setBackground(isRowSelected(row)
                        ? new Color(25, 35, 80) : row % 2 == 0 ? CARD : new Color(11, 16, 30));
                c.setForeground(col == 3 ? YELLOW : col == 6 ? GREEN : TEXT1);
                return c;
            }
        };
        kmTable.setBackground(CARD); kmTable.setForeground(TEXT1);
        kmTable.setGridColor(BORDER); kmTable.setRowHeight(36);
        kmTable.setFont(new Font("Dialog", Font.PLAIN, 12));
        kmTable.setShowVerticalLines(false);
        kmTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        kmTable.getTableHeader().setBackground(CARD2);
        kmTable.getTableHeader().setForeground(TEXT2);
        kmTable.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 11));
        int[] widths = {65, 140, 90, 95, 90, 110, 70};
        for (int i = 0; i < widths.length; i++)
            kmTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane scroll = new JScrollPane(kmTable);
        scroll.getViewport().setBackground(CARD);
        scroll.setBorder(new LineBorder(BORDER, 1));
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(BG);
        body.setBorder(BorderFactory.createEmptyBorder(12, 16, 8, 16));
        body.add(scroll, BorderLayout.CENTER);
        main.add(body, BorderLayout.CENTER);

        // Footer picker
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        footer.setBackground(BG);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));
        JButton btnHuy    = makeButton("Huỷ",      CARD,   TEXT2);
        JButton btnApDung = makeButton("Áp dụng",  ACCENT, Color.WHITE);
        btnHuy.addActionListener(e2 -> picker.dispose());
        btnApDung.addActionListener(e2 -> {
            int row = kmTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(picker, "Vui lòng chọn một khuyến mãi!",
                        "Thông báo", JOptionPane.WARNING_MESSAGE); return;
            }
            applyKM(danhSach.get(kmTable.convertRowIndexToModel(row)));
            picker.dispose();
        });
        kmTable.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e2) {
                if (e2.getClickCount() == 2 && kmTable.getSelectedRow() >= 0) {
                    applyKM(danhSach.get(kmTable.convertRowIndexToModel(kmTable.getSelectedRow())));
                    picker.dispose();
                }
            }
        });
        footer.add(btnHuy); footer.add(btnApDung);
        main.add(footer, BorderLayout.SOUTH);

        picker.setContentPane(main);
        picker.setVisible(true);
    }

    /** Gán KM đã chọn → cập nhật UI + tính lại tổng */
    private void applyKM(KhuyenMai km) {
        selectedKM = km;
        btnKMChon.setText(km.getMaKM());          // hiện mã KM trên chính nút
        btnKMChon.setBackground(GREEN);
        btnKMChon.setForeground(Color.WHITE);
        String loai = km.getLoaiKM() != null ? km.getLoaiKM() : "-";
        String info = "PHANTRAM".equalsIgnoreCase(loai)
                ? "✓ " + km.getTenKM() + "  —  Giảm " + String.format("%.0f%%", km.getGiatrigiam())
                + "  (tối đa " + String.format("%,.0f đ", km.getGiamtoida()) + ")"
                : "✓ " + km.getTenKM() + "  —  Giảm " + String.format("%,.0f đ", km.getGiatrigiam());
        if (km.getGiatridonhangtoithieu() > 0)
            info += "  |  Đơn từ " + String.format("%,.0f đ", km.getGiatridonhangtoithieu());
        lblKMInfo.setText(info);
        tinhTong();
    }

    /** Bỏ KM đang chọn */
    private void clearKM() {
        selectedKM = null;
        btnKMChon.setText("Chọn KM");
        btnKMChon.setBackground(ACCENT);
        btnKMChon.setForeground(Color.WHITE);
        lblKMInfo.setText(" ");
        tinhTong();
    }

    /** Mở popup danh sách sản phẩm để chọn thêm vào hoá đơn */
    private void openSPPicker() {
        List<SanPham> danhSach;
        try {
            danhSach = spbus.getAll();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Không thể tải danh sách sản phẩm!\n" + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (danhSach == null || danhSach.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có sản phẩm nào.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog picker = new JDialog(this, "Chọn sản phẩm", true);
        picker.setSize(700, 460);
        picker.setLocationRelativeTo(this);
        picker.setResizable(false);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BG);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(11, 16, 35));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));
        JLabel title = new JLabel("Danh sách sản phẩm");
        title.setFont(new Font("Dialog", Font.BOLD, 15));
        title.setForeground(TEXT1);
        JLabel sub = new JLabel("Nhấp đúp hoặc chọn rồi bấm 'Thêm vào hoá đơn'");
        sub.setFont(new Font("Dialog", Font.PLAIN, 11));
        sub.setForeground(TEXT2);
        header.add(title, BorderLayout.NORTH);
        header.add(sub,   BorderLayout.SOUTH);
        main.add(header, BorderLayout.NORTH);

        // Search bar
        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setBackground(BG);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 16, 4, 16));
        JTextField txtSearch = new JTextField();
        txtSearch.setBackground(CARD2);
        txtSearch.setForeground(TEXT1);
        txtSearch.setCaretColor(ACCENT);
        txtSearch.setFont(new Font("Dialog", Font.PLAIN, 12));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm theo mã hoặc tên...");
        searchPanel.add(new JLabel("🔍 ") {{ setForeground(TEXT2); }}, BorderLayout.WEST);
        searchPanel.add(txtSearch, BorderLayout.CENTER);
        main.add(searchPanel, BorderLayout.NORTH); // sẽ override header → dùng wrapper

        // Wrapper NORTH = header + search
        JPanel northWrap = new JPanel(new BorderLayout());
        northWrap.setBackground(BG);
        northWrap.add(header,      BorderLayout.NORTH);
        northWrap.add(searchPanel, BorderLayout.SOUTH);
        main.add(northWrap, BorderLayout.NORTH);

        // Bảng SP
        String[] cols = {"Mã SP", "Tên SP", "Loại", "Giá bán", "Thương hiệu", "Tồn kho", "Trạng thái"};
        DefaultTableModel spModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        for (SanPham sp : danhSach) {
            spModel.addRow(new Object[]{
                    sp.getMasp(),
                    sp.getTensp(),
                    sp.getLoaisp(),
                    String.format("%,.0f đ", sp.getGiaban()),
                    sp.getThuonghieu(),
                    sp.getTonkho(),
                    sp.getTrangthai()
            });
        }

        JTable spTable = new JTable(spModel) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                c.setBackground(isRowSelected(row)
                        ? new Color(25, 35, 80) : row % 2 == 0 ? CARD : new Color(11, 16, 30));
                // Cột giá bán màu vàng, tồn kho màu xanh/đỏ tuỳ số
                if (col == 3) {
                    c.setForeground(YELLOW);
                } else if (col == 5) {
                    int tonkho = Integer.parseInt(spModel.getValueAt(row, 5).toString());
                    c.setForeground(tonkho > 0 ? GREEN : RED);
                } else if (col == 6) {
                    String tt = spModel.getValueAt(row, 6).toString();
                    c.setForeground("CONHANG".equalsIgnoreCase(tt) ? GREEN : RED);
                } else {
                    c.setForeground(TEXT1);
                }
                return c;
            }
        };
        spTable.setBackground(CARD); spTable.setForeground(TEXT1);
        spTable.setGridColor(BORDER); spTable.setRowHeight(36);
        spTable.setFont(new Font("Dialog", Font.PLAIN, 12));
        spTable.setShowVerticalLines(false);
        spTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        spTable.getTableHeader().setBackground(CARD2);
        spTable.getTableHeader().setForeground(TEXT2);
        spTable.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 11));
        int[] widths = {65, 160, 80, 90, 100, 65, 90};
        for (int i = 0; i < widths.length; i++)
            spTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Live search filter
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(spModel);
        spTable.setRowSorter(sorter);
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            void filter() {
                String kw = txtSearch.getText().trim();
                sorter.setRowFilter(kw.isEmpty() ? null : RowFilter.regexFilter("(?i)" + kw));
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
        });

        JScrollPane scroll = new JScrollPane(spTable);
        scroll.getViewport().setBackground(CARD);
        scroll.setBorder(new LineBorder(BORDER, 1));
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(BG);
        body.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        body.add(scroll, BorderLayout.CENTER);
        main.add(body, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        footer.setBackground(BG);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));
        JButton btnHuy   = makeButton("Huỷ",                CARD,   TEXT2);
        JButton btnThem  = makeButton("Thêm vào hoá đơn",  ACCENT, Color.WHITE);
        btnHuy.addActionListener(e2 -> picker.dispose());

        // Hàm thêm SP được chọn vào bảng CTHD
        Runnable addSelected = () -> {
            int row = spTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(picker, "Vui lòng chọn một sản phẩm!",
                        "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = spTable.convertRowIndexToModel(row);
            SanPham sp = danhSach.get(modelRow);

            // Nếu SP đã có trong bảng → tăng số lượng thay vì thêm dòng mới
            for (int i = 0; i < ctModel.getRowCount(); i++) {
                if (ctModel.getValueAt(i, 0).toString().equals(sp.getMasp())) {
                    int sl = Integer.parseInt(ctModel.getValueAt(i, 2).toString());
                    ctModel.setValueAt(sl + 1, i, 2);
                    tinhTong();
                    picker.dispose();
                    return;
                }
            }
            // SP chưa có → thêm dòng mới
            ctModel.addRow(new Object[]{
                    sp.getMasp(),
                    sp.getTensp(),
                    1,
                    sp.getGiaban(),
                    sp.getGiaban()
            });
            tinhTong();
            picker.dispose();
        };

        btnThem.addActionListener(e2 -> addSelected.run());
        spTable.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e2) {
                if (e2.getClickCount() == 2) addSelected.run();
            }
        });

        footer.add(btnHuy); footer.add(btnThem);
        main.add(footer, BorderLayout.SOUTH);

        picker.setContentPane(main);
        picker.setVisible(true);
    }

    // Bang chi tiet hoa don (chitiethoadon)
    private JPanel buildCTHDTable() {
        JPanel wrap = new JPanel(new BorderLayout(0, 8));
        wrap.setBackground(BG);

        // Thành công cu
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setBackground(BG);
        JLabel lbl = new JLabel("Chi tiết sản phẩm:");
        lbl.setFont(new Font("sans serif", Font.BOLD, 13));
        lbl.setForeground(TEXT1);
        JButton btnThem = makeButton("+ Thêm SP", ACCENT, Color.WHITE);
        JButton btnXoa  = makeButton("Xoá dòng", RED,    Color.WHITE);

        btnThem.addActionListener(e -> openSPPicker());
        // Xoá dòng dang chon
        btnXoa.addActionListener(e -> {
            int row = ctTable.getSelectedRow();
            if (row >= 0) { ctModel.removeRow(row); tinhTong(); }
        });

        toolbar.add(lbl);
        toolbar.add(btnThem);
        toolbar.add(btnXoa);
        wrap.add(toolbar, BorderLayout.NORTH);

        // Bang CTHD
        String[] cols = {"Mã SP", "Tên SP", "Số lượng", "Đơn giá", "Thành tiền"};
        ctModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) {
                return c == 2; // chi cho sua so luong
            }
        };

        ctTable = new JTable(ctModel) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                c.setBackground(isRowSelected(row)
                        ? new Color(25, 35, 80) : new Color(14, 20, 40));
                c.setForeground(TEXT1);
                return c;
            }
        };
        ctTable.setBackground(CARD);
        ctTable.setForeground(TEXT1);
        ctTable.setGridColor(BORDER);
        ctTable.setRowHeight(36);
        ctTable.setFont(new Font("Dialog", Font.PLAIN, 12));
        ctTable.setShowVerticalLines(false);
        ctTable.getTableHeader().setBackground(CARD2);
        ctTable.getTableHeader().setForeground(TEXT2);
        ctTable.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 11));

        // Cap nhat thanh tien khi sua so luong
        ctModel.addTableModelListener(e -> {
            if (e.getColumn() == 2) tinhTong();
        });

        JScrollPane scroll = new JScrollPane(ctTable);
        scroll.getViewport().setBackground(CARD);
        scroll.setBorder(new LineBorder(BORDER, 1));
        wrap.add(scroll, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(BG);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
                BorderFactory.createEmptyBorder(12, 20, 16, 20)
        ));

        // Hàng 1: 3 label tổng tiền
        JPanel tong = new JPanel(new GridLayout(1, 3, 20, 0));
        tong.setOpaque(false);
        lblTongTien  = makeTongLabel("Tổng tiền:",  "0 VNĐ");
        lblGiamGia   = makeTongLabel("Giảm giá:",   "0 VNĐ");
        lblThanhToan = makeTongLabel("Thanh toán:", "0 VNĐ");
        tong.add(lblTongTien);
        tong.add(lblGiamGia);
        tong.add(lblThanhToan);

        // Hàng 2: lblStatus riêng, căn trái, có margin trên
        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Dialog", Font.BOLD, 12));
        lblStatus.setForeground(DANGER);
        lblStatus.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0)); // ← margin top

        // Hàng 3: nút
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btns.setOpaque(false);
        JButton btnHuy = makeButton("Huỷ",         CARD,   TEXT2);
        JButton btnLuu = makeButton("Tạo hoá đơn", ACCENT, Color.WHITE);
        btnHuy.addActionListener(e -> dispose());
        btnLuu.addActionListener(e -> save());
        btns.add(btnHuy);
        btns.add(btnLuu);

        // Gộp tong + status vào 1 panel dọc
        JPanel left = new JPanel(new BorderLayout());
        left.setOpaque(false);
        left.add(tong,      BorderLayout.NORTH);  // ← tổng tiền
        left.add(lblStatus, BorderLayout.CENTER); // ← lỗi xuống dòng riêng ✅

        footer.add(left,  BorderLayout.CENTER);
        footer.add(btns,  BorderLayout.SOUTH);
        return footer;
    }

    private JLabel makeTongLabel(String title, String value) {
        JPanel p = new JPanel(new BorderLayout(4, 0));
        p.setOpaque(false);
        JLabel t = new JLabel(title);
        t.setFont(new Font("Dialog", Font.PLAIN, 11));
        t.setForeground(TEXT2);
        JLabel v = new JLabel(value);
        v.setFont(new Font("Dialog", Font.BOLD, 14));
        v.setForeground(TEXT1);
        // Tra ve label value de update sau
        v.putClientProperty("title", t);
        return v;
    }

    private void tinhTong() {
        double tong = 0;
        for (int i = 0; i < ctModel.getRowCount(); i++) {
            Object sl = ctModel.getValueAt(i, 2);
            Object dg = ctModel.getValueAt(i, 3);
            try {
                double soLuong = Double.parseDouble(sl.toString());
                double donGia  = Double.parseDouble(dg.toString());
                double tt = soLuong * donGia;
                ctModel.setValueAt(tt, i, 4);
                tong += tt;
            } catch (Exception ignored) {}
        }
        double giam = (selectedKM != null) ? selectedKM.tinhSoTienGiam(tong) : 0;
        double thanhToan = tong - giam;
        lblTongTien.setText(String.format("%,.0f VNĐ", tong));
        lblGiamGia.setText(String.format("%,.0f VNĐ", giam));
        lblThanhToan.setText(String.format("%,.0f VNĐ", thanhToan));
        lblGiamGia.setForeground(giam > 0 ? YELLOW : TEXT1);
        lblThanhToan.setForeground(giam > 0 ? GREEN : TEXT1);
    }

    private void save() {
        if (fMaHD.getText().trim().isEmpty()) {
            lblStatus.setText("Mã HD khong duoc trong!"); return;
        }
        if (fMaKH.getText().trim().isEmpty()) {
            lblStatus.setText("Mã KH không được trống!"); return;
        }
        if (ctModel.getRowCount() == 0) {
            lblStatus.setText("Chưa có sản phẩm nào!"); return;
        }
        // Controller sẽ gọi buildHoaDon() + buildChiTiet() + bus.them()
        ketQua = fMaHD.getText().trim();
        dispose();
    }

    private JTextField addField(JPanel form, String label, String value) {
        JLabel l = new JLabel(label);
        l.setFont(new Font("Dialog", Font.BOLD, 11));
        l.setForeground(TEXT2);
        JTextField f = new JTextField(value);
        f.setBackground(CARD2);
        f.setForeground(TEXT1);
        f.setCaretColor(ACCENT);
        f.setFont(new Font("Dialog", Font.PLAIN, 12));
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)
        ));
        form.add(l);
        form.add(f);
        return f;
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Dialog", Font.BOLD, 11));
        l.setForeground(TEXT2);
        return l;
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
        b.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        return b;
    }
    public String getKetQua() { return ketQua; }
    public String getMaHD()      { return fMaHD.getText().trim(); }
    public String getMaKH()      { return fMaKH.getText().trim(); }
    public int    getSoLuongSP() { return ctModel.getRowCount(); }
    public double getTongTien() {
        double tong = 0;
        for (int i = 0; i < ctModel.getRowCount(); i++) {
            try { tong += Double.parseDouble(ctModel.getValueAt(i, 4).toString()); }
            catch (Exception ignored) {}
        }
        return tong;
    }

    public HoaDon buildHoaDon() {
        HoaDon hd = new HoaDon();
        hd.setMaHD(fMaHD.getText().trim());
        hd.setMaKH(fMaKH.getText().trim());
        // Lấy maNV từ session — truyền vào Dialog qua constructor
        hd.setMaNV(maNV != null ? maNV : "NV001");
        hd.setKhuyenmai(selectedKM != null ? selectedKM.getMaKM() : null);
        hd.setNgaytao(LocalDateTime.now());
        hd.setPhuongthucTT((String) fPhuongThuc.getSelectedItem());
        hd.setTrangthai("CHUATHANHTOAN");

        // Tính tiền từ bảng chi tiết
        double tongTien = getTongTien();
        hd.setTongtien(tongTien);
        // Tính giảm giá từ mã KM nếu có
        double soTienGiam = (selectedKM != null) ? selectedKM.tinhSoTienGiam(tongTien) : 0;

        hd.setSotiengiam(soTienGiam);
        hd.setThanhtoan(tongTien - soTienGiam);
        return hd;
    }

    public List<ChiTietHoaDon> buildChiTiet() {
        List<ChiTietHoaDon> list = new ArrayList<>();
        String maHD = fMaHD.getText().trim();


        ChiTietHoaDonDAO cthdDAO = new ChiTietHoaDonDAO();
        String maDau = cthdDAO.generate();
        int soThu    = Integer.parseInt(maDau.substring(4));

        for (int i = 0; i < ctModel.getRowCount(); i++) {
            ChiTietHoaDon ct = new ChiTietHoaDon();
            ct.setMaCTHD(String.format("CTHD%03d", soThu + i));
            ct.setMaHD(maHD);
            ct.setMaSP(ctModel.getValueAt(i, 0).toString());
            ct.setTenSP(ctModel.getValueAt(i, 1).toString());
            try {
                ct.setSoluong(Double.parseDouble(ctModel.getValueAt(i, 2).toString()));
                ct.setDongia(Double.parseDouble(ctModel.getValueAt(i, 3).toString()));
                ct.setThanhtien(Double.parseDouble(ctModel.getValueAt(i, 4).toString()));
            } catch (Exception ignored) {}
            ct.setKhuyenmai(null);
            list.add(ct);
        }
        return list;
    }
}