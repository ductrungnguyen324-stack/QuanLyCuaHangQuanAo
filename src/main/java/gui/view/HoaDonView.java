package gui.view;

import entity.HoaDon;
import gui.controller.HoaDonController;
import gui.controller.LoginController;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

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
    private JButton           btnThem, btnReset;

    // ── Phân quyền ──────────────────────────────────────
    private boolean chiXem = false; // true = chỉ xem, ẩn nút Xoá/In

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
        setSize(1200, 700);
        setMinimumSize(new Dimension(900, 500));
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(buildHeader(),  BorderLayout.NORTH);
        top.add(buildToolbar(), BorderLayout.SOUTH);
        setVisible(true);

        add(top,          BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);

        // Controller được khởi tạo từ MainFrame (kèm chucvu)
    }

    // ── Header ───────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(SURFACE);
        // set vien cho h va createCompoundBorder gop 2 vien (1 out, 1 in)
        h.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                BorderFactory.createEmptyBorder(16, 22, 16, 22)
        ));

        JLabel title = new JLabel("Quản lý Hoá Đơn");
        title.setFont(new Font("Sans serif", Font.BOLD, 20));
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
        left.add(title, BorderLayout.NORTH);// tren
        left.add(chips, BorderLayout.CENTER);// giua

        btnThem = makeButton("+ Tạo hoá đơn", ACCENT, Color.WHITE);
//        btnThem.addActionListener(e -> moDialogThem());

        h.add(left,    BorderLayout.WEST);// trai
        h.add(btnThem, BorderLayout.EAST);// phai
        return h;
    }

    // ── Toolbar ──────────────────────────────────────────
    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        bar.setBackground(BG);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));

        searchField = new JTextField(18);
        styleTextField(searchField);

        cbPhuongThuc = new JComboBox<>(new String[]{
                "Tất cả phương thức", "TIENMAT", "CHUYENKHOAN", "MOMO", "VNPAY", "ZaloPay"
        });

        cbTrangThai = new JComboBox<>(new String[]{
                "Tất cả trạng thái", "CHUATHANHTOAN", "DATHANHTOAN"
        });

        styleCombo(cbPhuongThuc);
        styleCombo(cbTrangThai);

        btnReset = makeButton("Làm mới", CARD, TEXT2);

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
        table.setGridColor(BORDER);// mau duong ke giua cac o
        table.setRowHeight(40);// chieu cao moi dong
        table.setFont(new Font("Sans serif", Font.PLAIN, 12));
        table.setSelectionBackground(ROW_SEL);
        table.setShowVerticalLines(false); // tat duong ke doc
        table.setFillsViewportHeight(true);// Nếu bảng ít dữ liệu, JTable sẽ vẫn lấp đầy chiều cao của ScrollPane.

        JTableHeader header = table.getTableHeader();

        header.setBackground(CARD);
        header.setForeground(TEXT2);
        header.setFont(new Font("Sans serif", Font.BOLD, 11));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));// Tạo border với độ dày từng cạnh

        // An cot ID (col 0)
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        // Do rong cot
        int[] widths = {0, 80, 110, 110, 90, 120, 110, 90, 110, 110, 120, 130};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);// setPreferredWidth dat do rong cho cot

        // Renderer cot Trạng thái (col 10)
        table.getColumnModel().getColumn(10).setCellRenderer(
                (t, val, sel, foc, row, col) -> {
                    String s = String.valueOf(val);
                    JLabel l = new JLabel("  " + s);
                    l.setOpaque(true);
                    l.setFont(new Font("Sans serif", Font.BOLD, 11));
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

        // Renderer cot Thao tác (col 11) — ẩn nút In/Xoá nếu chiXem=true
        table.getColumnModel().getColumn(11).setCellRenderer(
                (t, val, sel, foc, row, col) -> {
                    JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 8));
                    p.setBackground(sel ? ROW_SEL : row % 2 == 0 ? SURFACE : ROW_ODD);
                    p.add(makeTag("Chi tiết", CYAN));
                    if (!chiXem) {
                        p.add(makeTag("In",  GREEN));
                        p.add(makeTag("Xoá", RED));
                    }
                    return p;
                }
        );

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(SURFACE);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        return scroll;
    }


    public void updateStats() {
        int total = tableModel.getRowCount();
        int chuaTT = 0;
        double tongThu = 0;
        for (int i = 0; i < total; i++) {
            if ("CHUATHANHTOAN".equals(tableModel.getValueAt(i, 10))) chuaTT++;
            // col 8 = "Thanh toán" dạng "1,000 đ" → cần parse
            try {
                String val = tableModel.getValueAt(i, 8).toString()
                        .replaceAll("[^\\d]", "");
                tongThu += Double.parseDouble(val);
            } catch (Exception ignored) {}
        }
        lblTongHD.setText("HD: " + total);
        lblTongThu.setText(String.format("Doanh thu: %,.0f đ", tongThu));
        lblChuaTT.setText("Chưa TT: " + chuaTT);
    }

    // ── Helpers ──────────────────────────────────────────
    private JLabel makeTag(String text, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Sans serif", Font.BOLD, 10));
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
        l.setFont(new Font("Sans serif", Font.BOLD, 11));
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
        b.setFont(new Font("Sans serif", Font.BOLD, 12));
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
        l.setFont(new Font("Sans serif", Font.BOLD, 12));
        return l;
    }

    private void styleTextField(JTextField f) {
        f.setBackground(CARD);
        f.setForeground(TEXT1);
        f.setCaretColor(ACCENT);
        f.setFont(new Font("Sans serif", Font.PLAIN, 12));
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
    }

    private void styleCombo(JComboBox<String> cb) {
        cb.setBackground(CARD);
        cb.setForeground(TEXT2);
        cb.setFont(new Font("Sans serif", Font.BOLD, 12));
    }

    public void renderDanhSach(List<HoaDon> list) {
        tableModel.setRowCount(0);

        if(list == null) {
            return;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        for(HoaDon hd : list) {
            tableModel.addRow(new Object[]{
                    hd.getMaHD(),                                            // col 0 ẩn
                    hd.getMaHD(),                                            // col 1 Mã HD
                    hd.getMaKH(),                                            // col 2 Khách hàng
                    hd.getMaNV(),                                            // col 3 Nhân viên
                    hd.getKhuyenmai() != null ? hd.getKhuyenmai() : "-",              // col 4 Khuyến mãi
                    hd.getNgaytao() != null ? hd.getNgaytao().format(formatter) : "-", // col 5
                    String.format("%,.0f đ", hd.getTongtien()),              // col 6 Tổng tiền
                    String.format("%,.0f đ", hd.getSotiengiam()),            // col 7 Giảm giá
                    String.format("%,.0f đ", hd.getThanhtoan()),             // col 8 Thanh toán
                    hd.getPhuongthucTT(),                                    // col 9
                    hd.getTrangthai(),                                       // col 10
                    ""
            });
        }
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Successful", JOptionPane.INFORMATION_MESSAGE);
    }

    // ── Phân quyền: gọi từ Controller sau khi khởi tạo ──
    public void setChiXem(boolean chiXem) {
        this.chiXem = chiXem;
        table.repaint(); // vẽ lại bảng để renderer cập nhật nút
    }

    public boolean isChiXem() {
        return chiXem;
    }

    public JButton getBtnThem() {
        return btnThem;
    }
    public JTextField getSearchField() {
        return searchField;
    }
    public JComboBox<String> getCbPhuongThuc() {
        return cbPhuongThuc;
    }
    public JComboBox<String> getCbTrangThai() {
        return cbTrangThai;
    }
    public JTable getTable() {
        return table;
    }
    public JButton getBtnReset() {
        return btnReset;
    }
    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(HoaDonView::new);
    }
}