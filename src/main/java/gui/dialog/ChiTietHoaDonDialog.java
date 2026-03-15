package gui.dialog;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

import entity.*;


public class ChiTietHoaDonDialog extends JDialog {

    private static final Color BG     = new Color(10, 14, 30);
    private static final Color CARD   = new Color(14, 20, 40);
    private static final Color CARD2  = new Color(18, 26, 52);
    private static final Color BORDER = new Color(30, 42, 72);
    private static final Color ACCENT = new Color(99, 102, 241);
    private static final Color GREEN  = new Color(16, 185, 129);
    private static final Color YELLOW = new Color(245, 158, 11);
    private static final Color RED    = new Color(239, 68, 68);
    private static final Color CYAN   = new Color(6, 182, 212);
    private static final Color TEXT1  = new Color(226, 232, 240);
    private static final Color TEXT2  = new Color(100, 116, 139);


    private boolean duyet = false;


    private final HoaDon            hd;
    private final List<ChiTietHoaDon> chiTiet;

    public ChiTietHoaDonDialog(Frame parent, HoaDon hd, List<ChiTietHoaDon> chiTiet) {
        super(parent, true);
        this.hd      = hd;
        this.chiTiet = chiTiet;
        setTitle("Chi tiết hoá đơn: " + hd.getMaHD());
        setSize(660, 580);
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
        main.add(buildHeader(),  BorderLayout.NORTH);
        main.add(buildBody(),    BorderLayout.CENTER);
        main.add(buildFooter(),  BorderLayout.SOUTH);
        setContentPane(main);
    }

    // ── Header ───────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(new Color(11, 16, 35));
        h.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                BorderFactory.createEmptyBorder(16, 22, 16, 22)
        ));

        JLabel title = new JLabel("Hoá đơn: " + hd.getMaHD());
        title.setFont(new Font("Dialog", Font.BOLD, 16));
        title.setForeground(TEXT1);

        // Ngày tạo + nhân viên từ object thật
        String ngay = hd.getNgaytao() != null
                ? hd.getNgaytao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : "--";
        JLabel sub = new JLabel("Ngày tạo: " + ngay + "  |  Nhân viên: " + hd.getMaNV());
        sub.setFont(new Font("Dialog", Font.PLAIN, 12));
        sub.setForeground(TEXT2);

        h.add(title, BorderLayout.NORTH);
        h.add(sub,   BorderLayout.SOUTH);
        return h;
    }

    // ── Body ─────────────────────────────────────────────
    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(0, 14));
        body.setBackground(BG);
        body.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        body.add(buildInfoCards(), BorderLayout.NORTH);
        body.add(buildCTTable(),   BorderLayout.CENTER);
        body.add(buildSummary(),   BorderLayout.SOUTH);
        return body;
    }

    // 4 info card: KH, KM, Phương thức, Trạng thái
    private JPanel buildInfoCards() {
        JPanel p = new JPanel(new GridLayout(1, 4, 10, 0));
        p.setBackground(BG);

        Color ttColor = "DATHANHTOAN".equals(hd.getTrangthai()) ? GREEN : YELLOW;
        String ttText = "DATHANHTOAN".equals(hd.getTrangthai()) ? "Đã thanh toán" : "Chưa thanh toán";

        p.add(makeInfoCard("Khách hàng",    hd.getMaKH(),      TEXT1));
        p.add(makeInfoCard("Khuyến mãi",    hd.getKhuyenmai() != null ? hd.getKhuyenmai() : "Không có", ACCENT));
        p.add(makeInfoCard("Phương thức",   hd.getPhuongthucTT(), CYAN));
        p.add(makeInfoCard("Trạng thái",    ttText,            ttColor));
        return p;
    }

    // Bảng chi tiết sản phẩm — dữ liệu từ List<ChiTietHoaDon>
    private JScrollPane buildCTTable() {
        String[] cols = {"Mã CTHD", "Mã SP", "Tên SP", "Số lượng", "Đơn giá", "Thành tiền"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        // Map từng ChiTietHoaDon vào bảng
        for (ChiTietHoaDon ct : chiTiet) {
            model.addRow(new Object[]{
                    ct.getMaCTHD(),
                    ct.getMaSP(),
                    ct.getTenSP(),
                    ct.getSoluong() % 1 == 0
                            ? String.valueOf((int) ct.getSoluong())  // 2.0 → "2"
                            : String.valueOf(ct.getSoluong()),        // 1.5 → "1.5"
                    String.format("%,.0f đ", ct.getDongia()),
                    String.format("%,.0f đ", ct.getThanhtien())
            });
        }

        JTable table = new JTable(model) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                c.setBackground(isRowSelected(row)
                        ? new Color(25, 35, 80)
                        : row % 2 == 0 ? CARD : new Color(11, 16, 30));
                // Cột thành tiền nổi bật
                c.setForeground(col == 5 ? CYAN : TEXT1);
                if (col == 5) c.setFont(new Font("Dialog", Font.BOLD, 12));
                return c;
            }
        };
        table.setBackground(CARD);
        table.setForeground(TEXT1);
        table.setGridColor(BORDER);
        table.setRowHeight(36);
        table.setFont(new Font("Dialog", Font.PLAIN, 12));
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);

        JTableHeader th = table.getTableHeader();
        th.setBackground(CARD2);
        th.setForeground(TEXT2);
        th.setFont(new Font("Dialog", Font.BOLD, 11));

        int[] widths = {100, 90, 170, 80, 110, 120};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(CARD);
        scroll.setBorder(new LineBorder(BORDER, 1));
        return scroll;
    }

    // 3 summary card: Tổng tiền, Giảm giá, Thanh toán
    private JPanel buildSummary() {
        JPanel p = new JPanel(new GridLayout(1, 3, 10, 0));
        p.setBackground(BG);
        p.add(makeSummaryCard("Tổng tiền",
                String.format("%,.0f đ", hd.getTongtien()),    TEXT1));
        p.add(makeSummaryCard("Giảm giá",
                String.format("%,.0f đ", hd.getSotiengiam()),  YELLOW));
        p.add(makeSummaryCard("Thanh toán",
                String.format("%,.0f đ", hd.getThanhtoan()),   GREEN));
        return p;
    }

    // ── Footer ───────────────────────────────────────────
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        footer.setBackground(BG);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));

        JButton btnDong  = makeButton("Đóng", CARD, TEXT2);
        btnDong.addActionListener(e -> dispose());

        // Nút duyệt chỉ hiện nếu chưa thanh toán
        if ("CHUATHANHTOAN".equals(hd.getTrangthai())) {
            JButton btnDuyet = makeButton("✓ Xác nhận thanh toán", GREEN, Color.WHITE);
            btnDuyet.addActionListener(e -> {
                int ok = JOptionPane.showConfirmDialog(this,
                        "Xác nhận thanh toán hoá đơn " + hd.getMaHD() + "?\n"
                                + "Số tiền: " + String.format("%,.0f đ", hd.getThanhtoan()),
                        "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (ok == JOptionPane.YES_OPTION) {
                    duyet = true;
                    dispose();
                }
            });
            footer.add(btnDuyet);
        }

        footer.add(btnDong);
        return footer;
    }


    public boolean isDuyet() { return duyet; }

    // ── Helpers ──────────────────────────────────────────
    private JPanel makeInfoCard(String label, String value, Color color) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        JLabel l = new JLabel(label);
        l.setFont(new Font("Dialog", Font.BOLD, 10));
        l.setForeground(TEXT2);
        JLabel v = new JLabel(value);
        v.setFont(new Font("Dialog", Font.BOLD, 13));
        v.setForeground(color);
        p.add(l, BorderLayout.NORTH);
        p.add(v, BorderLayout.CENTER);
        return p;
    }

    private JPanel makeSummaryCard(String label, String value, Color color) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 15));
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(color.getRed(), color.getGreen(), color.getBlue(), 60), 1, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        JLabel l = new JLabel(label);
        l.setFont(new Font("Dialog", Font.BOLD, 11));
        l.setForeground(TEXT2);
        JLabel v = new JLabel(value);
        v.setFont(new Font("Dialog", Font.BOLD, 16));
        v.setForeground(color);
        p.add(l, BorderLayout.NORTH);
        p.add(v, BorderLayout.CENTER);
        return p;
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
        b.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        return b;
    }
}