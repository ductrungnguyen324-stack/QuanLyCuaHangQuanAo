package gui.view;


import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class KhuyenMaiView extends JFrame {

    // ── Màu ─────────────────────────────────────────────
    private static final Color BG      = new Color(7,  10, 20);
    private static final Color SURFACE = new Color(11, 15, 30);
    private static final Color CARD    = new Color(14, 20, 40);
    private static final Color BORDER  = new Color(30, 42, 72);
    private static final Color ACCENT  = new Color(99, 102, 241);
    private static final Color GREEN   = new Color(16, 185, 129);
    private static final Color YELLOW  = new Color(245, 158, 11);
    private static final Color RED     = new Color(239, 68, 68);
    private static final Color CYAN    = new Color(6, 182, 212);
    private static final Color TEXT1   = new Color(226, 232, 240);
    private static final Color TEXT2   = new Color(100, 116, 139);
    private static final Color ROW_ODD = new Color(10, 14, 28);
    private static final Color ROW_SEL = new Color(25, 35, 80);

    // ── Components ───────────────────────────────────────
    // private final KhuyenMaiBUS bus = new KhuyenMaiBUS(); // ← BẬT KHI CÓ BUS
    private JTable              table;
    private DefaultTableModel   tableModel;
    private JTextField          searchField;
    private JComboBox<String>   cbLoaiKM, cbTrangThai;
    private JLabel              lblTongKM, lblDangHoat, lblHetHan;

    private static final String[] COLUMNS = {
            "Mã KM",            // col 0
            "Tên khuyến mãi",   // col 1
            "Loại",             // col 2  (loaiKM)
            "Giá trị giảm",     // col 3  (giatrigiam)
            "Giảm tối đa",      // col 4  (giamtoida)
            "Đơn tối thiểu",    // col 5  (giatridonhangtoithieu)
            "Ngày bắt đầu",     // col 6  (ngaybatdau)
            "Ngày kết thúc",    // col 7  (ngayketthuc)
            "Số lượng",         // col 8  (soluong)
            "Đã dùng",          // col 9  (dasudung)
            "Trạng thái",       // col 10 (isKhaDung)
            "Thao tác"          // col 11
    };

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public KhuyenMaiView() {
        setTitle("Quản lý Khuyến mãi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setBackground(BG);
        setLayout(new BorderLayout(0, 0));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(buildHeader(),  BorderLayout.NORTH);
        top.add(buildToolbar(), BorderLayout.SOUTH);

        add(top,          BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);

        loadDanhSach();
        setVisible(true);
    }

    // ── Header ───────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(SURFACE);
        h.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                BorderFactory.createEmptyBorder(16, 22, 16, 22)
        ));

        JLabel title = new JLabel("Quản lý Khuyến mãi");
        title.setFont(new Font("Dialog", Font.BOLD, 20));
        title.setForeground(TEXT1);

        // Chip thống kê
        lblTongKM   = makeChip("KM: --",          ACCENT);
        lblDangHoat = makeChip("Đang hoạt động: --", GREEN);
        lblHetHan   = makeChip("Hết hạn: --",     RED);

        JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        chips.setOpaque(false);
        chips.add(lblTongKM);
        chips.add(lblDangHoat);
        chips.add(lblHetHan);

        JPanel left = new JPanel(new BorderLayout(0, 6));
        left.setOpaque(false);
        left.add(title, BorderLayout.NORTH);
        left.add(chips, BorderLayout.CENTER);

        JButton btnThem = makeButton("+ Thêm khuyến mãi", ACCENT, Color.WHITE);
//        btnThem.addActionListener(e -> moDialogThem());

        h.add(left,    BorderLayout.WEST);
        h.add(btnThem, BorderLayout.EAST);
        return h;
    }

    // ── Toolbar ──────────────────────────────────────────
    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        bar.setBackground(BG);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));

        searchField = new JTextField(18);
        styleTextField(searchField);
        searchField.setToolTipText("Tìm theo mã hoặc tên khuyến mãi...");

        cbLoaiKM = new JComboBox<>(new String[]{
                "Tất cả loại", "PHANTRAM", "TIENCODINH"
        });
        cbTrangThai = new JComboBox<>(new String[]{
                "Tất cả trạng thái", "Còn hiệu lực", "Hết hạn"
        });
        styleCombo(cbLoaiKM);
        styleCombo(cbTrangThai);

        cbLoaiKM.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) locDuLieu();
        });
        cbTrangThai.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) locDuLieu();
        });

        JButton btnReset = makeButton("Làm mới", CARD, TEXT2);
        btnReset.addActionListener(e -> {
            searchField.setText("");
            cbLoaiKM.setSelectedIndex(0);
            cbTrangThai.setSelectedIndex(0);
            loadDanhSach();
        });

        bar.add(makeLabel("Tìm kiếm:"));
        bar.add(searchField);
        bar.add(cbLoaiKM);
        bar.add(cbTrangThai);
        bar.add(btnReset);
        return bar;
    }

    // ── Table ────────────────────────────────────────────
    private JScrollPane buildTable() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                c.setBackground(isRowSelected(row) ? ROW_SEL
                        : row % 2 == 0 ? SURFACE : ROW_ODD);
                c.setForeground(TEXT1);
                return c;
            }
        };

        table.setBackground(SURFACE);
        table.setForeground(TEXT1);
        table.setGridColor(BORDER);
        table.setRowHeight(40);
        table.setFont(new Font("Dialog", Font.PLAIN, 12));
        table.setSelectionBackground(ROW_SEL);
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setBackground(CARD);
        header.setForeground(TEXT2);
        header.setFont(new Font("Dialog", Font.BOLD, 11));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));

        // Độ rộng cột
        int[] widths = {80, 200, 110, 110, 110, 120, 130, 130, 80, 80, 120, 130};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Renderer cột Loại (col 2)
        table.getColumnModel().getColumn(2).setCellRenderer(
                (t, val, sel, foc, row, col) -> {
                    String s = String.valueOf(val);
                    JLabel l = new JLabel("  " + s);
                    l.setOpaque(true);
                    l.setFont(new Font("Dialog", Font.BOLD, 11));
                    l.setBackground(sel ? ROW_SEL : row % 2 == 0 ? SURFACE : ROW_ODD);
                    l.setForeground(s.equals("PHANTRAM") ? CYAN : YELLOW);
                    return l;
                }
        );

        // Renderer cột Trạng thái (col 10)
        table.getColumnModel().getColumn(10).setCellRenderer(
                (t, val, sel, foc, row, col) -> {
                    String s = String.valueOf(val);
                    boolean active = s.equals("Còn hiệu lực");
                    JLabel l = new JLabel("  " + s);
                    l.setOpaque(true);
                    l.setFont(new Font("Dialog", Font.BOLD, 11));
                    l.setBackground(sel ? ROW_SEL : row % 2 == 0 ? SURFACE : ROW_ODD);
                    l.setForeground(active ? GREEN : RED);
                    return l;
                }
        );

        // Renderer cột Thao tác (col 11)
        table.getColumnModel().getColumn(11).setCellRenderer(
                (t, val, sel, foc, row, col) -> {
                    JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 8));
                    p.setBackground(sel ? ROW_SEL : row % 2 == 0 ? SURFACE : ROW_ODD);
                    p.add(makeTag("Chi tiết", CYAN));
                    p.add(makeTag("Sửa",      YELLOW));
                    p.add(makeTag("Xóa",      RED));
                    return p;
                }
        );

        // MouseListener: xử lý click cột Thao tác
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row < 0) return;
                int modelRow = table.convertRowIndexToModel(row);
                String maKM  = (String) tableModel.getValueAt(modelRow, 0);
                int col = table.columnAtPoint(e.getPoint());

//                if (col == 11) {
//                    Rectangle rect = table.getCellRect(row, col, true);
//                    int third = rect.width / 3;
//                    int dx = e.getX() - rect.x;
//                    if (dx < third)           xemChiTiet(maKM);
//                    else if (dx < third * 2)  suaKhuyenMai(maKM);
//                    else                      xoaKhuyenMai(maKM);
//                } else if (e.getClickCount() == 2) {
//                    xemChiTiet(maKM);
//                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(SURFACE);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        return scroll;
    }

    // ── Load & Render ────────────────────────────────────
    private void loadDanhSach() {
        // TODO: List<KhuyenMai> list = bus.getAll();
        tableModel.setRowCount(0);
        LocalDateTime now = LocalDateTime.now();

        // Dữ liệu mẫu — mỗi phần tử tương ứng field trong KhuyenMai.java
        // { maKM, tenKM, loaiKM, giatrigiam, giamtoida, giatridonhangtoithieu,
        //   ngaybatdau, ngayketthuc, soluong, dasudung }
        Object[][] sample = {
                {"KM001", "Giảm 10% đơn từ 200k",     "PHANTRAM",   10.0,  50000.0, 200000.0,
                        now.minusDays(10), now.plusDays(20),  100, 42},
                {"KM002", "Giảm 50k đơn từ 300k",      "TIENCODINH", 50000.0, 50000.0, 300000.0,
                        now.minusDays(5),  now.plusDays(25),  200, 87},
                {"KM003", "Flash Sale 20%",             "PHANTRAM",   20.0, 100000.0, 500000.0,
                        now.minusDays(1),  now.plusDays(1),   50,  50},
                {"KM004", "Ưu đãi tháng 3 - 15%",      "PHANTRAM",   15.0,  80000.0, 400000.0,
                        now.minusDays(30), now.minusDays(1),  150, 145},
                {"KM005", "Giảm 100k đơn từ 1 triệu",  "TIENCODINH",100000.0,100000.0,1000000.0,
                        now.minusDays(3),  now.plusDays(27),  80,  12},
        };

        for (Object[] r : sample) {
            LocalDateTime batDau   = (LocalDateTime) r[6];
            LocalDateTime ketThuc  = (LocalDateTime) r[7];
            int    soluong  = (int)    r[8];
            int    dasudung = (int)    r[9];

            boolean conHieu = (dasudung < soluong)
                    && now.isAfter(batDau) && now.isBefore(ketThuc);
            String loai = (String) r[2];

            // Hiển thị giá trị giảm: % hoặc tiền cố định
            String giaTriHienThi = loai.equals("PHANTRAM")
                    ? String.format("%.0f%%", (double) r[3])
                    : String.format("%,.0f đ", (double) r[3]);

            tableModel.addRow(new Object[]{
                    r[0],                                                    // col 0  maKM
                    r[1],                                                    // col 1  tenKM
                    loai,                                                    // col 2  loaiKM
                    giaTriHienThi,                                           // col 3  giatrigiam
                    String.format("%,.0f đ", (double) r[4]),                 // col 4  giamtoida
                    String.format("%,.0f đ", (double) r[5]),                 // col 5  giatridonhangtoithieu
                    batDau.format(FMT),                                      // col 6  ngaybatdau
                    ketThuc.format(FMT),                                     // col 7  ngayketthuc
                    soluong,                                                 // col 8  soluong
                    dasudung,                                                // col 9  dasudung
                    conHieu ? "Còn hiệu lực" : "Hết hạn",                   // col 10 trangthai
                    ""                                                       // col 11 thaotac
            });
        }
        updateStats();
    }

    private void locDuLieu() {
        // TODO: filter trên bus.getAll()
        loadDanhSach();
    }

    private void updateStats() {
        int total = tableModel.getRowCount();
        int hoatDong = 0, hetHan = 0;
        for (int i = 0; i < total; i++) {
            String tt = (String) tableModel.getValueAt(i, 10);
            if ("Còn hiệu lực".equals(tt)) hoatDong++;
            else hetHan++;
        }
        lblTongKM.setText("KM: " + total);
        lblDangHoat.setText("Đang hoạt động: " + hoatDong);
        lblHetHan.setText("Hết hạn: " + hetHan);
    }

    // ── Chức năng (mở comment khi có BUS) ────────────────
//    private void moDialogThem() {
//        KhuyenMaiDialog dialog = new KhuyenMaiDialog(this, null);
//        dialog.setVisible(true);
//        if (dialog.getKetQua() != null) {
//            bus.them(dialog.getKetQua());
//            loadDanhSach();
//            JOptionPane.showMessageDialog(this, "Thêm khuyến mãi thành công!");
//        }
//    }

//    private void xemChiTiet(String maKM) {
//        KhuyenMai km = bus.getByMa(maKM);
//        JOptionPane.showMessageDialog(this, km.toString(), "Chi tiết: " + maKM, JOptionPane.INFORMATION_MESSAGE);
//    }

//    private void suaKhuyenMai(String maKM) {
//        KhuyenMai km = bus.getByMa(maKM);
//        KhuyenMaiDialog dialog = new KhuyenMaiDialog(this, km);
//        dialog.setVisible(true);
//        if (dialog.getKetQua() != null) {
//            bus.capNhat(dialog.getKetQua());
//            loadDanhSach();
//        }
//    }

    private void xoaKhuyenMai(String maKM) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa khuyến mãi: " + maKM + "?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            // TODO: bus.xoa(maKM);
            loadDanhSach();
            JOptionPane.showMessageDialog(this, "Đã xóa " + maKM);
        }
    }

    // ── Helpers ──────────────────────────────────────────
    private JLabel makeTag(String text, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Dialog", Font.BOLD, 10));
        l.setForeground(color);
        l.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 25));
        l.setOpaque(true);
        l.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(color.getRed(), color.getGreen(), color.getBlue(), 80), 1, true),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
        l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return l;
    }

    private JLabel makeChip(String text, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Dialog", Font.BOLD, 11));
        l.setForeground(color);
        l.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
        l.setOpaque(true);
        l.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(color.getRed(), color.getGreen(), color.getBlue(), 60), 1, true),
                BorderFactory.createEmptyBorder(3, 9, 3, 9)
        ));
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

    private void styleCombo(JComboBox<String> cb) {
        cb.setBackground(CARD);
        cb.setForeground(TEXT2);
        cb.setFont(new Font("Dialog", Font.BOLD, 12));
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(KhuyenMaiView::new);
    }
}