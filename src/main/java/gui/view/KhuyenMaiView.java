package gui.view;

import gui.controller.KhuyenMaiController;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

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
    private JTable              table;
    private DefaultTableModel   tableModel;
    private JTextField          searchField;
    private JComboBox<String>   cbLoaiKM, cbTrangThai;
    private JLabel              lblTongKM, lblDangHoat, lblHetHan;

    // ── Controller ───────────────────────────────────────
    private KhuyenMaiController controller;
    private boolean chiXem = false; // phân quyền

    private static final String[] COLUMNS = {
            "Mã KM",            // col 0
            "Tên khuyến mãi",   // col 1
            "Loại",             // col 2
            "Giá trị giảm",     // col 3
            "Giảm tối đa",      // col 4
            "Đơn tối thiểu",    // col 5
            "Ngày bắt đầu",     // col 6
            "Ngày kết thúc",    // col 7
            "Số lượng",         // col 8
            "Đã dùng",          // col 9
            "Trạng thái",       // col 10
            "Thao tác"          // col 11
    };

    public KhuyenMaiView() {
        this("Nhan vien"); // mặc định
    }

    public KhuyenMaiView(String chucvu) {
        this.chiXem = !("Quan ly".equals(chucvu));
        setTitle("Quản lý Khuyến Mãi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setBackground(BG);
        setLayout(new BorderLayout(0, 0));

        // Khởi tạo bảng trước (controller cần tableModel)
        JScrollPane scrollPane = buildTable();

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(buildHeader(),  BorderLayout.NORTH);
        top.add(buildToolbar(), BorderLayout.SOUTH);

        add(top,        BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Controller khởi tạo sau khi View đã có đủ component
        controller = new KhuyenMaiController(this, chucvu);
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

        JLabel title = new JLabel("Quản lý Khuyến Mãi");
        title.setFont(new Font("Dialog", Font.BOLD, 20));
        title.setForeground(TEXT1);

        lblTongKM   = makeChip("KM: --",             ACCENT);
        lblDangHoat = makeChip("Đang hoạt động: --", GREEN);
        lblHetHan   = makeChip("Hết hạn: --",        RED);

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

        searchField = new JTextField(18);
        styleTextField(searchField);
        searchField.setToolTipText("Tìm theo mã hoặc tên khuyến mãi...");
        // Tìm kiếm khi nhấn Enter
        // Live search: gõ đến đâu lọc đến đó
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { triggerFilter(); }
            public void removeUpdate(DocumentEvent e)  { triggerFilter(); }
            public void changedUpdate(DocumentEvent e) { triggerFilter(); }
        });

        cbLoaiKM = new JComboBox<>(new String[]{
                "Tất cả loại", "PHANTRAM", "TIENCODINH"
        });
        cbTrangThai = new JComboBox<>(new String[]{
                "Tất cả trạng thái", "Còn hiệu lực", "Hết hạn"
        });
        styleCombo(cbLoaiKM);
        styleCombo(cbTrangThai);

        cbLoaiKM.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) triggerFilter();
        });
        cbTrangThai.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) triggerFilter();
        });

        JButton btnReset = makeButton("Làm mới", CARD, TEXT2);
        btnReset.addActionListener(e -> {
            searchField.setText("");
            cbLoaiKM.setSelectedIndex(0);
            cbTrangThai.setSelectedIndex(0);
            controller.loadDanhSach();
        });

        bar.add(makeLabel("Tìm kiếm:"));
        bar.add(searchField);
        bar.add(cbLoaiKM);
        bar.add(cbTrangThai);
        bar.add(btnReset);
        return bar;
    }

    private void triggerFilter() {
        if (controller != null)
            controller.locDuLieu(
                    searchField.getText(),
                    (String) cbLoaiKM.getSelectedItem(),
                    (String) cbTrangThai.getSelectedItem()
            );
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

        // Renderer cột Thao tác (col 11) — ẩn nút nếu chiXem=true
        table.getColumnModel().getColumn(11).setCellRenderer(
                (t, val, sel, foc, row, col) -> {
                    JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 8));
                    p.setBackground(sel ? ROW_SEL : row % 2 == 0 ? SURFACE : ROW_ODD);
                    if (!chiXem) {
                        p.add(makeTag("Sửa", YELLOW));
                        p.add(makeTag("Xóa", RED));
                    } else {
                        p.add(makeTag("Chỉ xem", TEXT2));
                    }
                    return p;
                }
        );

        // MouseListener: click cột Thao tác — 2 vùng: Sửa | Xóa
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row < 0) return;
                int modelRow = table.convertRowIndexToModel(row);
                String maKM  = (String) tableModel.getValueAt(modelRow, 0);
                int col = table.columnAtPoint(e.getPoint());

                if (col == 11) {
                    if (chiXem) return; // chặn thao tác
                    Rectangle rect = table.getCellRect(row, col, true);
                    int half = rect.width / 2;
                    int dx   = e.getX() - rect.x;
                    if (dx < half) controller.suaKhuyenMai(maKM);
                    else           controller.xoaKhuyenMai(maKM);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(SURFACE);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        return scroll;
    }

    // ── Public: Controller gọi để cập nhật stats ─────────
    /**
     * Luôn nhận số liệu từ toàn bộ DB (không phụ thuộc filter).
     * @param total    tổng số khuyến mãi trong DB
     * @param hoatDong số đang còn hiệu lực
     * @param hetHan   số đã hết hạn / hết lượt
     */
    public void updateStats(int total, int hoatDong, int hetHan) {
        lblTongKM.setText("KM: " + total);
        lblDangHoat.setText("Đang hoạt động: " + hoatDong);
        lblHetHan.setText("Hết hạn: " + hetHan);
    }

    public void setChiXem(boolean chiXem) { this.chiXem = chiXem; table.repaint(); }
    public boolean isChiXem() { return chiXem; }

    /** Controller cần truy cập tableModel để đổ dữ liệu. */
    public DefaultTableModel getTableModel() {
        return tableModel;
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

}