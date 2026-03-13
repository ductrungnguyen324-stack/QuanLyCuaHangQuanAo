package gui.view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * PhieuNhapPanel.java — Quản lý Phiếu nhập
 * Cột theo SQL: maPN, maNV, nhacungcap, ngaytao, thanhtien, trangthai
 *
 * Kết nối BUS: private final PhieuNhapBUS bus = new PhieuNhapBUS();
 */
public class PhieuNhapView extends JFrame {

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

    // private final PhieuNhapBUS bus = new PhieuNhapBUS(); // ← BẬT KHI CÓ BUS

    private JTable            table;
    private DefaultTableModel tableModel;
    private JTextField        searchField;
    private JComboBox<String> cbTrangThai;
    private JLabel            lblTongPN, lblTongTien, lblChoXuLy;

    private static final String[] COLUMNS = {
            "ID",           // col 0 ẩn (maPN)
            "Mã PN",        // col 1
            "Nhân viên",    // col 2
            "Nhà cung cấp", // col 3
            "Ngày tạo",     // col 4
            "Thành tiền",   // col 5
            "Trạng thái",   // col 6
            "Thao tác"      // col 7
    };

    public PhieuNhapView() {
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

        JLabel title = new JLabel("Quản lý Phiếu nhập");
        title.setFont(new Font("Dialog", Font.BOLD, 20));
        title.setForeground(TEXT1);

        lblTongPN   = makeChip("Phiếu: --",    ACCENT);
        lblTongTien = makeChip("Tổng chi: --", RED);
        lblChoXuLy  = makeChip("Chờ xử lý: --", YELLOW);

        JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        chips.setOpaque(false);
        chips.add(lblTongPN);
        chips.add(lblTongTien);
        chips.add(lblChoXuLy);

        JPanel left = new JPanel(new BorderLayout(0, 6));
        left.setOpaque(false);
        left.add(title, BorderLayout.NORTH);
        left.add(chips, BorderLayout.CENTER);

        JButton btnThem = makeButton("+ Tạo phiếu nhập", ACCENT, Color.WHITE);
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

        searchField = new JTextField(20);
        styleTextField(searchField);
        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { locDuLieu(); }
            public void removeUpdate(DocumentEvent e) { locDuLieu(); }
            public void changedUpdate(DocumentEvent e) { locDuLieu(); }
        };
        searchField.getDocument().addDocumentListener(dl);

        cbTrangThai = new JComboBox<>(new String[]{
                "Tất cả trạng thái", "Chờ xử lý", "Đã nhập kho", "Đã huỷ"
        });
        styleCombo(cbTrangThai);
        cbTrangThai.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) locDuLieu();
        });

        JButton btnReset = makeButton("Làm mới", CARD, TEXT2);
        btnReset.addActionListener(e -> {
            searchField.setText("");
            cbTrangThai.setSelectedIndex(0);
            loadDanhSach();
        });

        bar.add(makeLabel("Tìm kiếm:"));
        bar.add(searchField);
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
        table.setRowHeight(42);
        table.setFont(new Font("Dialog", Font.PLAIN, 12));
        table.setSelectionBackground(ROW_SEL);
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setBackground(CARD);
        header.setForeground(TEXT2);
        header.setFont(new Font("Dialog", Font.BOLD, 11));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));

        // Ẩn cột ID
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        // Độ rộng cột
        int[] widths = {0, 90, 130, 180, 130, 130, 120, 150};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Renderer cột Trạng thái (col 6)
        table.getColumnModel().getColumn(6).setCellRenderer(
                (t, val, sel, foc, row, col) -> {
                    String s = String.valueOf(val);
                    JLabel l = new JLabel("  ● " + s);
                    l.setOpaque(true);
                    l.setFont(new Font("Dialog", Font.BOLD, 11));
                    l.setBackground(sel ? ROW_SEL : row % 2 == 0 ? SURFACE : ROW_ODD);
                    l.setForeground(
                            s.equals("Đã nhập kho") ? GREEN  :
                                    s.equals("Chờ xử lý")   ? YELLOW : RED
                    );
                    return l;
                }
        );

        // Renderer cột Thành tiền (col 5)
        table.getColumnModel().getColumn(5).setCellRenderer(
                (t, val, sel, foc, row, col) -> {
                    JLabel l = new JLabel("  " + val);
                    l.setOpaque(true);
                    l.setFont(new Font("Dialog", Font.BOLD, 12));
                    l.setBackground(sel ? ROW_SEL : row % 2 == 0 ? SURFACE : ROW_ODD);
                    l.setForeground(CYAN);
                    return l;
                }
        );

        // Renderer cột Thao tác (col 7)
        table.getColumnModel().getColumn(7).setCellRenderer(
                (t, val, sel, foc, row, col) -> {
                    JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 9));
                    p.setBackground(sel ? ROW_SEL : row % 2 == 0 ? SURFACE : ROW_ODD);
                    p.add(makeTag("Chi tiết", CYAN));
                    p.add(makeTag("Duyệt",    GREEN));
                    p.add(makeTag("Xoá",      RED));
                    return p;
                }
        );

        // MouseListener: click thao tác
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row < 0) return;
                int modelRow = table.convertRowIndexToModel(row);
                String maPN  = (String) tableModel.getValueAt(modelRow, 0);
                int col = table.columnAtPoint(e.getPoint());

//                if (col == 7) {
//                    Rectangle rect  = table.getCellRect(row, col, true);
//                    int third = rect.width / 3;
//                    int dx    = e.getX() - rect.x;
//                    if (dx < third)           xemChiTiet(maPN);
//                    else if (dx < third * 2)  duyetPhieu(maPN, modelRow);
//                    else                      xoaPhieu(maPN);
//                } else if (e.getClickCount() == 2) {
//                    xemChiTiet(maPN);
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
        // TODO: List<PhieuNhap> list = bus.getAll();
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Object[][] sample = {
                {"PN001","PN001","NV001","Công ty Vải Thắng Lợi", new Date(), 12500000.0, "Đã nhập kho"},
                {"PN002","PN002","NV002","Xưởng May Minh Châu",   new Date(),  8750000.0, "Chờ xử lý"},
                {"PN003","PN003","NV001","Công ty Vải Thắng Lợi", new Date(), 21000000.0, "Đã nhập kho"},
                {"PN004","PN004","NV003","Nhà Phân Phối ABC",      new Date(),  5200000.0, "Chờ xử lý"},
                {"PN005","PN005","NV002","Xưởng May Minh Châu",   new Date(),  3600000.0, "Đã huỷ"},
        };
        for (Object[] r : sample) {
            tableModel.addRow(new Object[]{
                    r[0],
                    r[1],
                    r[2],
                    r[3],
                    sdf.format((Date) r[4]),
                    String.format("%,.0f đ", (double) r[5]),
                    r[6],
                    ""
            });
        }
        updateStats();
    }

    private void locDuLieu() {
        // TODO: filter trên bus.getAll()
        loadDanhSach();
    }

    private void updateStats() {
        int total    = tableModel.getRowCount();
        int choXuLy  = 0;
        for (int i = 0; i < total; i++)
            if ("Chờ xử lý".equals(tableModel.getValueAt(i, 6))) choXuLy++;
        lblTongPN.setText("Phiếu: "      + total);
        lblChoXuLy.setText("Chờ xử lý: " + choXuLy);
        lblTongTien.setText("Tổng chi: 51,050,000 đ");
    }

    // ── Chức năng ────────────────────────────────────────
//    private void moDialogThem() {
//        PhieuNhapDialog dialog = new PhieuNhapDialog(
//                (Frame) SwingUtilities.getWindowAncestor(this), null);
//        dialog.setVisible(true);
//        if (dialog.getKetQua() != null) {
//            // TODO: bus.them(dialog.getKetQua());
//            loadDanhSach();
//            JOptionPane.showMessageDialog(this, "Tạo phiếu nhập thành công!");
//        }
//    }

//    private void xemChiTiet(String maPN) {
//        PhieuNhapChiTietDialog dialog = new PhieuNhapChiTietDialog(
//                (Frame) SwingUtilities.getWindowAncestor(this), maPN);
//        dialog.setVisible(true);
//    }

    private void duyetPhieu(String maPN, int modelRow) {
        String tt = (String) tableModel.getValueAt(modelRow, 6);
        if ("Đã nhập kho".equals(tt)) {
            JOptionPane.showMessageDialog(this,
                    "Phiếu " + maPN + " đã được duyệt rồi!", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this,
                "Duyệt phiếu " + maPN + "?\nSản phẩm sẽ được cập nhật vào tồn kho.",
                "Xác nhận duyệt", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            // TODO: bus.duyetPhieu(maPN);
            tableModel.setValueAt("Đã nhập kho", modelRow, 6);
            updateStats();
            JOptionPane.showMessageDialog(this, "Duyệt phiếu " + maPN + " thành công!\nTồn kho đã được cập nhật.");
        }
    }

    private void xoaPhieu(String maPN) {
        int ok = JOptionPane.showConfirmDialog(this,
                "Xoá phiếu nhập: " + maPN + "?",
                "Xác nhận xoá", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ok == JOptionPane.YES_OPTION) {
            // TODO: bus.xoa(maPN);
            loadDanhSach();
            JOptionPane.showMessageDialog(this, "Đã xoá " + maPN);
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
                BorderFactory.createEmptyBorder(2, 7, 2, 7)
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
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test — Phiếu nhập");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setContentPane(new PhieuNhapView());
            frame.setVisible(true);
        });
    }
}
