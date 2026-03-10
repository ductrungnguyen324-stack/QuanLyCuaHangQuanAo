package gui.view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * HoaDonPanel.java — Danh sach hoa don
 * Cot theo SQL: maHD, maKH, maNV, maKM, ngaytao,
 *               tongtien, sotiengiam, thanhtoan, phuongthucTT, trangthai
 *
 * Ket noi BUS that:
 *   private final HoaDonBUS bus = new HoaDonBUS();
 *   Thay cac cho comment GỌI BUS
 */
public class HoaDonView extends JFrame {

    // ── Mau ─────────────────────────────────────────────
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
    // private final HoaDonBUS bus = new HoaDonBUS(); // ← BAT KHI CO BUS
    private JTable            table;
    private DefaultTableModel tableModel;
    private JTextField        searchField;
    private JComboBox<String> cbPhuongThuc, cbTrangThai;
    private JLabel            lblTongHD, lblTongThu, lblChuaTT;

    private static final String[] COLUMNS = {
            "ID",           // col 0 an (maHD)
            "Mã HD",        // col 1
            "Khách hàng",   // col 2 (maKH)
            "Nhân viên",    // col 3 (maNV)
            "Khuyến mãi",   // col 4 (maKM)
            "Ngày tạo",     // col 5
            "Tổng tiền",    // col 6
            "Giảm giá",     // col 7
            "Thanh toán",   // col 8
            "Phương thức",  // col 9
            "Trạng thái",   // col 10
            "Thao tác"      // col 11
    };

    public HoaDonView() {
        setBackground(BG);
        setLayout(new BorderLayout(0, 0));
        setVisible(true);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(buildHeader(),  BorderLayout.NORTH);
        top.add(buildToolbar(), BorderLayout.SOUTH);

        add(top,          BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);

        loadDanhSach();
    }

    // ── Header ───────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(SURFACE);
        h.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                BorderFactory.createEmptyBorder(16, 22, 16, 22)
        ));

        JLabel title = new JLabel("Quản lý Hoá đơn");
        title.setFont(new Font("Dialog", Font.BOLD, 20));
        title.setForeground(TEXT1);

        // Chip thong ke
        lblTongHD  = makeChip("HD: --",      ACCENT);
        lblTongThu = makeChip("Doanh thu: --", GREEN);
        lblChuaTT  = makeChip("Chưa TT: --", YELLOW);

        JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        chips.setOpaque(false);
        chips.add(lblTongHD);
        chips.add(lblTongThu);
        chips.add(lblChuaTT);

        JPanel left = new JPanel(new BorderLayout(0, 6));
        left.setOpaque(false);
        left.add(title, BorderLayout.NORTH);
        left.add(chips, BorderLayout.CENTER);

        JButton btnThem = makeButton("+ Tạo hoá đơn", ACCENT, Color.WHITE);
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
//        searchField.getDocument().addDocumentListener(
//                new javax.swing.event.DocumentAdapter() {
//                    public void update(javax.swing.event.DocumentEvent e) { locDuLieu(); }
//                }
//        );

        cbPhuongThuc = new JComboBox<>(new String[]{
                "Tất cả phương thức", "TIENMAT", "CHUYENKHOAN", "MOMO", "VNPAY", "ZaloPay"
        });
        cbTrangThai = new JComboBox<>(new String[]{
                "Tất cả trạng thái", "CHUATHANHTOAN", "DATHANHTOAN"
        });
        styleCombo(cbPhuongThuc);
        styleCombo(cbTrangThai);

        cbPhuongThuc.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) locDuLieu();
        });
        cbTrangThai.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) locDuLieu();
        });

        JButton btnReset = makeButton("Làm mới", CARD, TEXT2);
        btnReset.addActionListener(e -> {
            searchField.setText("");
            cbPhuongThuc.setSelectedIndex(0);
            cbTrangThai.setSelectedIndex(0);
            loadDanhSach();
        });

        bar.add(makeLabel("Tìm kiếm:"));
        bar.add(searchField);
        bar.add(cbPhuongThuc);
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

        // An cot ID (col 0)
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        // Do rong cot
        int[] widths = {0, 80, 110, 110, 90, 120, 110, 90, 110, 110, 120, 130};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Renderer cot Trạng thái (col 10)
        table.getColumnModel().getColumn(10).setCellRenderer(
                (t, val, sel, foc, row, col) -> {
                    String s = String.valueOf(val);
                    JLabel l = new JLabel("  " + s);
                    l.setOpaque(true);
                    l.setFont(new Font("Dialog", Font.BOLD, 11));
                    l.setBackground(sel ? ROW_SEL : row % 2 == 0 ? SURFACE : ROW_ODD);
                    l.setForeground(s.equals("DATHANHTOAN") ? GREEN : YELLOW);
                    return l;
                }
        );

        // Renderer cot Phương thức (col 9)
        table.getColumnModel().getColumn(9).setCellRenderer(
                (t, val, sel, foc, row, col) -> {
                    String s = String.valueOf(val);
                    JLabel l = new JLabel("  " + s);
                    l.setOpaque(true);
                    l.setFont(new Font("Dialog", Font.BOLD, 11));
                    l.setBackground(sel ? ROW_SEL : row % 2 == 0 ? SURFACE : ROW_ODD);
                    l.setForeground(
                            s.equals("TIENMAT")      ? GREEN  :
                                    s.equals("CHUYENKHOAN")  ? CYAN   :
                                            s.equals("MOMO")         ? new Color(219, 39, 119) :
                                                    s.equals("VNPAY")        ? RED    : ACCENT
                    );
                    return l;
                }
        );

        // Renderer cot Thao tác (col 11)
        table.getColumnModel().getColumn(11).setCellRenderer(
                (t, val, sel, foc, row, col) -> {
                    JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 8));
                    p.setBackground(sel ? ROW_SEL : row % 2 == 0 ? SURFACE : ROW_ODD);
                    p.add(makeTag("Chi tiết", CYAN));
                    p.add(makeTag("In",       GREEN));
                    p.add(makeTag("Xoa",      RED));
                    return p;
                }
        );

        // MouseListener: xu ly click cot Thao tác
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row < 0) return;
                int modelRow = table.convertRowIndexToModel(row);
                String maHD  = (String) tableModel.getValueAt(modelRow, 0);
                int col = table.columnAtPoint(e.getPoint());

//                if (col == 11) {
//                    Rectangle rect = table.getCellRect(row, col, true);
//                    int third = rect.width / 3;
//                    int dx = e.getX() - rect.x;
//                    if (dx < third)           xemChiTiet(maHD);
//                    else if (dx < third * 2)  inHoaDon(maHD);
//                    else                      xoaHoaDon(maHD);
//                } else if (e.getClickCount() == 2) {
//                    xemChiTiet(maHD);
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
        // TODO: List<HoaDon> list = bus.getAll();
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Object[][] sample = {
                {"HD001","HD001","KH001","NV001","KM001", new Date(), 850000.0, 50000.0, 800000.0, "TIENMAT",     "DATHANHTOAN"},
                {"HD002","HD002","KH002","NV001","-",     new Date(), 450000.0, 0.0,     450000.0, "MOMO",        "DATHANHTOAN"},
                {"HD003","HD003","KH003","NV002","KM002", new Date(),1200000.0,120000.0,1080000.0, "CHUYENKHOAN", "CHUATHANHTOAN"},
                {"HD004","HD004","KH001","NV003","-",     new Date(), 280000.0, 0.0,     280000.0, "VNPAY",       "DATHANHTOAN"},
                {"HD005","HD005","KH004","NV002","-",     new Date(), 680000.0, 0.0,     680000.0, "ZaloPay",     "CHUATHANHTOAN"},
        };
        for (Object[] r : sample) {
            tableModel.addRow(new Object[]{
                    r[0],                                           // col 0 maHD (an)
                    r[1],                                           // col 1 ma HD
                    r[2],                                           // col 2 maKH
                    r[3],                                           // col 3 maNV
                    r[4],                                           // col 4 maKM
                    sdf.format((Date) r[5]),                        // col 5 ngay tao
                    String.format("%,.0f d", (double) r[6]),        // col 6 tong tien
                    String.format("%,.0f d", (double) r[7]),        // col 7 giam gia
                    String.format("%,.0f d", (double) r[8]),        // col 8 thanh toan
                    r[9],                                           // col 9 phuong thuc
                    r[10],                                          // col 10 trang thai
                    ""                                              // col 11 thao tac
            });
        }
        updateStats();
    }

    private void locDuLieu() {
        // TODO: filter tren bus.getAll()
        loadDanhSach();
    }

    private void updateStats() {
        int total = tableModel.getRowCount();
        int chuaTT = 0;
        for (int i = 0; i < total; i++)
            if ("CHUATHANHTOAN".equals(tableModel.getValueAt(i, 10))) chuaTT++;
        lblTongHD.setText("HD: " + total);
        lblChuaTT.setText("Chưa TT: " + chuaTT);
    }

    // ── Chuc nang ────────────────────────────────────────
//    private void moDialogThem() {
//        HoaDonDialog dialog = new HoaDonDialog((Frame) SwingUtilities.getWindowAncestor(this), null);
//        dialog.setVisible(true);
//        if (dialog.getKetQua() != null) {
//            // TODO: bus.them(dialog.getKetQua());
//            loadDanhSach();
//            JOptionPane.showMessageDialog(this, "Tạo hoá đơn thành công!");
//        }
//    }

//    private void xemChiTiet(String maHD) {
//        HoaDonChiTietDialog dialog = new HoaDonChiTietDialog(
//                (Frame) SwingUtilities.getWindowAncestor(this), maHD);
//        dialog.setVisible(true);
//    }

    private void inHoaDon(String maHD) {
        // TODO: tich hop thu vien in that (JasperReports, iText...)
        JOptionPane.showMessageDialog(this,
                "In hoá đơn: " + maHD + "\n(Chức năng đang phát triển)",
                "In hoá đơn", JOptionPane.INFORMATION_MESSAGE);
    }

    private void xoaHoaDon(String maHD) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xoá hoá đơn: " + maHD + "?",
                "Xác nhận xoá", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            // TODO: bus.xoa(maHD);
            loadDanhSach();
            JOptionPane.showMessageDialog(this, "Đã xoá " + maHD);
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
        SwingUtilities.invokeLater(HoaDonView::new);
    }
}