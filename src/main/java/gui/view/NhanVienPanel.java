package gui.view;

import gui.controller.NhanVienController;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

/**
 * NhanVienPanel.java — View thuan tuy
 * Package: gui.view
 *
 * Chi chiu trach nhiem hien thi UI va nhan su kien.
 * Toan bo logic nghiep vu duoc xu ly boi NhanVienController.
 *
 * Implements NhanVienController.IView de Controller co the
 * giao tiep voi View ma khong phu thuoc truc tiep.
 */
public class NhanVienPanel extends JPanel implements NhanVienController.IView {

    // ── Mau ──────────────────────────────────────────────────
    private static final Color BG      = new Color(7,  10, 20);
    private static final Color SURFACE = new Color(11, 15, 30);
    private static final Color CARD    = new Color(14, 20, 40);
    private static final Color BORDER  = new Color(30, 42, 72);
    private static final Color ACCENT  = new Color(99, 102, 241);
    private static final Color GREEN   = new Color(16, 185, 129);
    private static final Color YELLOW  = new Color(245, 158, 11);
    private static final Color RED     = new Color(239, 68,  68);
    private static final Color PURPLE  = new Color(168, 85, 247);
    private static final Color TEXT1   = new Color(226, 232, 240);
    private static final Color TEXT2   = new Color(100, 116, 139);
    private static final Color ROW_ODD = new Color(10, 14, 28);
    private static final Color ROW_SEL = new Color(25, 35, 80);

    // ── Controller ───────────────────────────────────────────
    private final NhanVienController controller;

    // ── Components ───────────────────────────────────────────
    private JTable            table;
    private DefaultTableModel tableModel;
    private JTextField        searchField;
    private JComboBox<String> cbChucVu, cbTrangThai;
    private JLabel            lblTongNV, lblHoatDong, lblNghiViec;

    private static final String[] COLUMNS = {
            "ID",            // col 0 an (maNV)
            "Mã NV",         // col 1
            "Họ tên",        // col 2
            "Số điện thoại", // col 3
            "Chức vụ",       // col 4
            "Tên đăng nhập", // col 5
            "Trạng thái",    // col 6
            "Thao tác"       // col 7
    };

    // =========================================================
    // Constructor
    // =========================================================
    public NhanVienPanel() {
        setBackground(BG);
        setLayout(new BorderLayout(0, 0));

        // Khoi tao Controller, truyen chinh this (IView) vao
        this.controller = new NhanVienController(this);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(buildHeader(),  BorderLayout.NORTH);
        top.add(buildToolbar(), BorderLayout.SOUTH);

        add(top,          BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);

        // Controller tai du lieu khi khoi dong
        controller.loadDanhSach();
    }

    // =========================================================
    // Implements NhanVienController.IView
    // =========================================================

    @Override
    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    @Override
    public Frame getParentFrame() {
        return (Frame) SwingUtilities.getWindowAncestor(this);
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public void updateStats(int total, int hoatDong, int nghiViec) {
        lblTongNV.setText("NV: "          + total);
        lblHoatDong.setText("Hoạt động: " + hoatDong);
        lblNghiViec.setText("Nghỉ việc: " + nghiViec);
    }

    @Override
    public String getKeyword() {
        return searchField.getText();
    }

    @Override
    public String getChucVuFilter() {
        return (String) cbChucVu.getSelectedItem();
    }

    @Override
    public String getTrangThaiFilter() {
        return (String) cbTrangThai.getSelectedItem();
    }

    // =========================================================
    // Header
    // =========================================================
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(SURFACE);
        h.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                BorderFactory.createEmptyBorder(16, 22, 16, 22)
        ));

        JLabel title = new JLabel("Quản lý Nhân viên");
        title.setFont(new Font("Dialog", Font.BOLD, 20));
        title.setForeground(TEXT1);

        lblTongNV   = makeChip("NV: 0",         ACCENT);
        lblHoatDong = makeChip("Hoạt động: 0",  GREEN);
        lblNghiViec = makeChip("Nghỉ việc: 0",  RED);

        JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        chips.setOpaque(false);
        chips.add(lblTongNV);
        chips.add(lblHoatDong);
        chips.add(lblNghiViec);

        JPanel left = new JPanel(new BorderLayout(0, 6));
        left.setOpaque(false);
        left.add(title, BorderLayout.NORTH);
        left.add(chips, BorderLayout.CENTER);

        JButton btnThem = makeButton("+ Thêm nhân viên", ACCENT, Color.WHITE);
        btnThem.addActionListener(e -> controller.them());

        h.add(left,    BorderLayout.WEST);
        h.add(btnThem, BorderLayout.EAST);
        return h;
    }

    // =========================================================
    // Toolbar
    // =========================================================
    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        bar.setBackground(BG);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));

        searchField = new JTextField(18);
        styleTextField(searchField);
        searchField.getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {
                    public void insertUpdate(javax.swing.event.DocumentEvent e)  { controller.loc(); }
                    public void removeUpdate(javax.swing.event.DocumentEvent e)  { controller.loc(); }
                    public void changedUpdate(javax.swing.event.DocumentEvent e) { controller.loc(); }
                }
        );

        cbChucVu = new JComboBox<>(new String[]{
                "Tất cả chức vụ", "Quản lý", "Nhân viên bán hàng", "Kho", "Thu ngân"
        });
        cbTrangThai = new JComboBox<>(new String[]{
                "Tất cả trạng thái", "Hoạt động", "Nghỉ việc"
        });
        styleCombo(cbChucVu);
        styleCombo(cbTrangThai);

        cbChucVu.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) controller.loc();
        });
        cbTrangThai.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) controller.loc();
        });

        JButton btnReset = makeButton("Làm mới", CARD, TEXT2);
        btnReset.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        btnReset.addActionListener(e -> {
            searchField.setText("");
            cbChucVu.setSelectedIndex(0);
            cbTrangThai.setSelectedIndex(0);
            controller.loadDanhSach();
        });

        bar.add(makeLabel("Tìm kiếm:"));
        bar.add(searchField);
        bar.add(cbChucVu);
        bar.add(cbTrangThai);
        bar.add(btnReset);
        return bar;
    }

    // =========================================================
    // Table
    // =========================================================
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

        // An col 0
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        // Do rong cot
        int[] widths = {0, 80, 180, 130, 150, 160, 120, 140};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Renderer col 1 — Ma NV
        table.getColumnModel().getColumn(1).setCellRenderer(
                (t, val, sel, foc, row, col) -> {
                    JLabel l = new JLabel("  " + val);
                    l.setOpaque(true);
                    l.setFont(new Font("Dialog", Font.BOLD, 12));
                    l.setBackground(sel ? ROW_SEL : row % 2 == 0 ? SURFACE : ROW_ODD);
                    l.setForeground(ACCENT);
                    return l;
                }
        );

        // Renderer col 4 — Chuc vu
        table.getColumnModel().getColumn(4).setCellRenderer(
                (t, val, sel, foc, row, col) -> {
                    String s = String.valueOf(val);
                    JLabel l = new JLabel("  " + s);
                    l.setOpaque(true);
                    l.setFont(new Font("Dialog", Font.BOLD, 11));
                    l.setBackground(sel ? ROW_SEL : row % 2 == 0 ? SURFACE : ROW_ODD);
                    l.setForeground(chucVuColor(s));
                    return l;
                }
        );

        // Renderer col 6 — Trang thai
        table.getColumnModel().getColumn(6).setCellRenderer(
                (t, val, sel, foc, row, col) -> {
                    String s = String.valueOf(val);
                    JLabel l = new JLabel("  " + s);
                    l.setOpaque(true);
                    l.setFont(new Font("Dialog", Font.BOLD, 11));
                    l.setBackground(sel ? ROW_SEL : row % 2 == 0 ? SURFACE : ROW_ODD);
                    l.setForeground(s.equals("Hoạt động") ? GREEN : RED);
                    return l;
                }
        );

        // Renderer col 7 — Thao tac
        table.getColumnModel().getColumn(7).setCellRenderer(
                (t, val, sel, foc, row, col) -> {
                    JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 8));
                    p.setBackground(sel ? ROW_SEL : row % 2 == 0 ? SURFACE : ROW_ODD);
                    p.add(makeTag("Sửa", ACCENT));
                    p.add(makeTag("Xóa", RED));
                    return p;
                }
        );

        // MouseListener — chuyen su kien sang Controller
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row < 0) return;

                int modelRow = table.convertRowIndexToModel(row);
                String maNV  = (String) tableModel.getValueAt(modelRow, 0);

                if (col == 7) {
                    Rectangle rect = table.getCellRect(row, col, true);
                    int dx   = e.getX() - rect.x;
                    int half = rect.width / 2;
                    if (dx < half) controller.sua(maNV);
                    else           controller.xoa(maNV);
                } else if (e.getClickCount() == 2) {
                    controller.sua(maNV);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(SURFACE);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        return scroll;
    }

    // =========================================================
    // Helpers UI
    // =========================================================
    private Color chucVuColor(String cv) {
        switch (cv) {
            case "Quản lý":            return ACCENT;
            case "Nhân viên bán hàng": return PURPLE;
            case "Kho":                return YELLOW;
            case "Thu ngân":           return GREEN;
            default:                   return TEXT2;
        }
    }

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

    // =========================================================
    // main — test doc lap
    // =========================================================
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Quản lý Nhân viên");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setSize(1100, 650);
            f.setLocationRelativeTo(null);
            f.setContentPane(new NhanVienPanel());
            f.setVisible(true);
        });
    }
}