package gui.view;

import entity.KhachHang;
import gui.controller.KhachHangController;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class KhachHangView extends JFrame {

    // ── Màu ─────────────────────────────────────────────
    private static final Color BG      = new Color(7,  10, 20);
    private static final Color SURFACE = new Color(11, 15, 30);
    private static final Color CARD    = new Color(14, 20, 40);
    private static final Color BORDER  = new Color(30, 42, 72);
    private static final Color ACCENT  = new Color(99, 102, 241);
    private static final Color GREEN   = new Color(16, 185, 129);
    private static final Color RED     = new Color(239, 68, 68);
    private static final Color CYAN    = new Color(6, 182, 212);
    private static final Color TEXT1   = new Color(226, 232, 240);
    private static final Color TEXT2   = new Color(100, 116, 139);
    private static final Color ROW_ODD = new Color(10, 14, 28);
    private static final Color ROW_SEL = new Color(25, 35, 80);

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final String[] COLUMNS = {
            "ID",            // col 0 ẩn (maKH)
            "Mã KH",         // col 1
            "Họ và tên",     // col 2
            "Số điện thoại", // col 3
            "Ngày tham gia", // col 4
            "Thao tác"       // col 5
    };

    // ── Components ───────────────────────────────────────
    private JTable            table;
    private DefaultTableModel tableModel;
    private JTextField        searchField;
    private JLabel            lblTongKH, lblMoi;

    // ── Controller ───────────────────────────────────────
    private final KhachHangController controller;
    private boolean chiXem = false; // phân quyền

    public KhachHangView() {
        this("Nhan vien"); // mặc định
    }

    public KhachHangView(String chucvu) {
        this.chiXem = !("Quan ly".equals(chucvu));
        controller = new KhachHangController(this, chucvu);

        setTitle("Quản lý Khách Hàng");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setBackground(BG);
        setLayout(new BorderLayout(0, 0));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(buildHeader(),  BorderLayout.NORTH);
        top.add(buildToolbar(), BorderLayout.SOUTH);

        add(top,          BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);

        controller.loadDanhSach();
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

        JLabel title = new JLabel("Quản lý Khách Hàng");
        title.setFont(new Font("Dialog", Font.BOLD, 20));
        title.setForeground(TEXT1);

        lblTongKH = makeChip("KH: 0",        CYAN);
        lblMoi    = makeChip("Đang xem: 0", GREEN);

        JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        chips.setOpaque(false);
        chips.add(lblTongKH);
        chips.add(lblMoi);

        JPanel left = new JPanel(new BorderLayout(0, 6));
        left.setOpaque(false);
        left.add(title, BorderLayout.NORTH);
        left.add(chips, BorderLayout.CENTER);

        JButton btnThem = makeButton("+ Thêm khách hàng", ACCENT, Color.WHITE);
        btnThem.addActionListener(e -> controller.moDialogThem());
        if (chiXem) btnThem.setVisible(false);

        h.add(left,    BorderLayout.WEST);
        h.add(btnThem, BorderLayout.EAST);
        return h;
    }

    // ── Toolbar ──────────────────────────────────────────
    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        bar.setBackground(BG);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));

        searchField = new JTextField(22);
        searchField.setToolTipText("Tìm theo mã KH hoặc số điện thoại");
        styleTextField(searchField);

        searchField.getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {
                    public void insertUpdate(javax.swing.event.DocumentEvent e)  { controller.locDuLieu(searchField.getText()); }
                    public void removeUpdate(javax.swing.event.DocumentEvent e)  { controller.locDuLieu(searchField.getText()); }
                    public void changedUpdate(javax.swing.event.DocumentEvent e) { controller.locDuLieu(searchField.getText()); }
                }
        );

        JButton btnReset = makeButton("Làm mới", CARD, TEXT2);
        btnReset.addActionListener(e -> searchField.setText(""));

        bar.add(makeLabel("Tìm kiếm (Mã KH / SĐT):"));
        bar.add(searchField);
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

        // Ẩn cột ID (col 0)
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        // Độ rộng cột
        int[] widths = {0, 90, 220, 150, 150, 130};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Renderer cột Mã KH (col 1)
        table.getColumnModel().getColumn(1).setCellRenderer(
                (t, val, sel, foc, row, col) -> {
                    JLabel l = new JLabel("  " + val);
                    l.setOpaque(true);
                    l.setFont(new Font("Monospaced", Font.BOLD, 12));
                    l.setBackground(sel ? ROW_SEL : row % 2 == 0 ? SURFACE : ROW_ODD);
                    l.setForeground(CYAN);
                    return l;
                }
        );

        // Renderer cột Thao tác (col 5) — ẩn nút nếu chiXem=true
        table.getColumnModel().getColumn(5).setCellRenderer(
                (t, val, sel, foc, row, col) -> {
                    JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 8));
                    p.setBackground(sel ? ROW_SEL : row % 2 == 0 ? SURFACE : ROW_ODD);
                    if (!chiXem) {
                        p.add(makeTag("Sửa", CYAN));
                        p.add(makeTag("Xoá", RED));
                    } else {
                        p.add(makeTag("Chỉ xem", TEXT2));
                    }
                    return p;
                }
        );

        // MouseListener: xử lý click cột Thao tác
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row < 0) return;
                int modelRow = table.convertRowIndexToModel(row);
                String maKH  = (String) tableModel.getValueAt(modelRow, 0);
                String ten   = (String) tableModel.getValueAt(modelRow, 2);
                int col = table.columnAtPoint(e.getPoint());

                if (col == 5) {
                    if (chiXem) return; // chặn thao tác
                    Rectangle rect = table.getCellRect(row, col, true);
                    int dx = e.getX() - rect.x;
                    if (dx < rect.width / 2) controller.moDialogSua(maKH);
                    else                     controller.xoaKhachHang(maKH, ten);
                } else if (e.getClickCount() == 2 && !chiXem) {
                    controller.moDialogSua(maKH);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(SURFACE);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        return scroll;
    }

    // ── Public API cho Controller ─────────────────────────

    public void hienThiDanhSach(ArrayList<KhachHang> list) {
        tableModel.setRowCount(0);
        for (KhachHang kh : list) {
            tableModel.addRow(new Object[]{
                    kh.getMaKH(),
                    kh.getMaKH(),
                    kh.getHoten(),
                    kh.getSdt(),
                    kh.getNgaythamgia() != null ? kh.getNgaythamgia().format(FORMATTER) : "",
                    ""
            });
        }
        updateStats();
    }

    private void updateStats() {
        int total   = controller.getDanhSachGoc().size();
        int dangXem = tableModel.getRowCount();
        lblTongKH.setText("KH: " + total);
        lblMoi.setText("Đang xem: " + dangXem);
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
                BorderFactory.createEmptyBorder(2, 8, 2, 8)
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

    public void setChiXem(boolean chiXem) { this.chiXem = chiXem; table.repaint(); }
    public boolean isChiXem() { return chiXem; }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(KhachHangView::new);
    }
}