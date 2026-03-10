package gui.view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import bus.SanPhamBUS;
import entity.SanPham;

public class SanPhamView extends JFrame {
    // ── Màu ─────────────────────────────────────────────
    private static final Color BG      = new Color(7,  10, 20);
    private static final Color SURFACE = new Color(11, 15, 30);
    private static final Color CARD    = new Color(14, 20, 40);
    private static final Color BORDER  = new Color(30, 42, 72);
    private static final Color ACCENT  = new Color(99, 102, 241);
    private static final Color GREEN   = new Color(16, 185, 129);
    private static final Color YELLOW  = new Color(245, 158, 11);
    private static final Color RED     = new Color(239, 68, 68);
    private static final Color TEXT1   = new Color(226, 232, 240);
    private static final Color TEXT2   = new Color(100, 116, 139);
    private static final Color ROW_ODD = new Color(10, 14, 28);
    private static final Color ROW_SEL = new Color(25, 35, 80);

    // ── Components ───────────────────────────────────────
    // private final SanPhamBUS bus = new SanPhamBUS(); // ← BẬT KHI CÓ BUS
    private JTable            table;
    private DefaultTableModel tableModel;
    private JTextField        searchField;
    private JComboBox<String> cbLoai, cbTrangThai;
    private JLabel            lblTongSP, lblTongTon;

    // model
    SanPhamBUS spBus = new SanPhamBUS();

    // ── Cột bảng (theo SQL) ──────────────────────────────
    private static final String[] COLUMNS = {
            "ID",        // col 0 — ẩn (maSP)
            "Mã Sản Phẩm",     // col 1
            "Tên Sản Phẩm",    // col 2
            "Loai Sản Phẩm",   // col 3
            "Thương Hiệu", // col 4
            "Kích Cỡ",   // col 5
            "Màu Sắc",   // col 6
            "Giá Bán",   // col 7
            "Tồn Kho",   // col 8
            "Trạng Thái",// col 9
            "Thao tác"   // col 10
    };

    public SanPhamView() {
        setVisible(true);
        setBackground(BG);
        setLayout(new BorderLayout(0, 0));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(buildHeader(),  BorderLayout.NORTH);
        top.add(buildToolbar(), BorderLayout.SOUTH);

        add(top,           BorderLayout.NORTH);
        add(buildTable(),  BorderLayout.CENTER);

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

        JLabel title = new JLabel("Quản lý sản phẩm");
        title.setFont(new Font("Sans serif", Font.BOLD, 20));
        title.setForeground(TEXT1);

        // Chip thống kê
        lblTongSP  = makeChip("SP: --",   ACCENT);
        lblTongTon = makeChip("Tồn: --",  GREEN);
        JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        chips.setOpaque(false);
        chips.add(lblTongSP);
        chips.add(lblTongTon);

        JPanel left = new JPanel(new BorderLayout(0, 6));
        left.setOpaque(false);
        left.add(title, BorderLayout.NORTH);
        left.add(chips, BorderLayout.CENTER);

        JButton btnThem = makeButton("+ Thêm sản phẩm", ACCENT, Color.WHITE);
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
        searchField.setBackground(CARD);
        searchField.setForeground(TEXT1);
        searchField.setCaretColor(ACCENT);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        searchField.setFont(new Font("Sans serif", Font.PLAIN, 12));
//         DocumentListener: lọc ngay khi gõ
        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { locDuLieu(); }
            public void removeUpdate(DocumentEvent e) { locDuLieu(); }
            public void changedUpdate(DocumentEvent e) { locDuLieu(); }
        };
        searchField.getDocument().addDocumentListener(dl);

        cbLoai = new JComboBox<>(new String[]{
                "Tất cả các loại", "Ao thun", "Ao so mi", "Quan", "Vay", "Ao khoac"
        });
        cbTrangThai = new JComboBox<>(new String[]{
                "Tất cả trạng thái", "Còn hàng", "Sắp hết", "Hết hàn", "Ngừng bán"
        });
        styleCombo(cbLoai);
        styleCombo(cbTrangThai);
        cbLoai.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) locDuLieu();
        });
        cbTrangThai.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) locDuLieu();
        });

        JButton btnReset = makeButton("Làm mới", CARD, TEXT2);
        btnReset.addActionListener(e -> {
            searchField.setText("");
            cbLoai.setSelectedIndex(0);
            cbTrangThai.setSelectedIndex(0);
            loadDanhSach();
        });

        bar.add(new JLabel("  Tìm kiếm:") {{
            setForeground(TEXT2);
            setFont(new Font("Sans serif", Font.BOLD, 12));
        }});
        bar.add(searchField);
        bar.add(cbLoai);
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
        table.setFont(new Font("Sans serif", Font.PLAIN, 12));
        table.setSelectionBackground(ROW_SEL);
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);

        // Header style
        JTableHeader header = table.getTableHeader();
        header.setBackground(CARD);
        header.setForeground(TEXT2);
        header.setFont(new Font("Sans serif", Font.BOLD, 11));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));

        // Ẩn cột ID (col 0)
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        // Độ rộng các cột
        int[] widths = {0, 80, 180, 100, 110, 70, 80, 100, 70, 100, 110};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Renderer cột Trang thai (col 9)
        table.getColumnModel().getColumn(9).setCellRenderer(
                (t, val, sel, foc, row, col) -> {
                    String s = String.valueOf(val);
                    JLabel l = new JLabel("  " + s);
                    l.setOpaque(true);
                    l.setFont(new Font("Sans serif", Font.BOLD, 11));
                    l.setBackground(sel ? ROW_SEL : row % 2 == 0 ? SURFACE : ROW_ODD);
                    l.setForeground(
                            s.equals("Còn hàng")  ? GREEN  :
                                    s.equals("Sắp hết") ? YELLOW : RED
                    );
                    return l;
                }
        );

        // Renderer cột Thao tac (col 10)
        table.getColumnModel().getColumn(10).setCellRenderer(
                (t, val, sel, foc, row, col) -> {
                    JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 8));
                    p.setBackground(sel ? ROW_SEL : row % 2 == 0 ? SURFACE : ROW_ODD);
                    JLabel edit = new JLabel("Sửa");
                    edit.setForeground(new Color(129, 140, 248));
                    edit.setFont(new Font("Sans serif", Font.BOLD, 11));
                    edit.setBackground(new Color(30, 35, 80));
                    edit.setOpaque(true);
                    edit.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
                    JLabel del = new JLabel("Xoá");
                    del.setForeground(RED);
                    del.setFont(new Font("Sans serif", Font.BOLD, 11));
                    del.setBackground(new Color(50, 20, 20));
                    del.setOpaque(true);
                    del.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
                    p.add(edit); p.add(del);
                    return p;
                }
        );

        // MouseListener: click nút Sua / Xoa
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row < 0) return;
                int modelRow = table.convertRowIndexToModel(row);
                String maSP  = (String) tableModel.getValueAt(modelRow, 0);
                int col = table.columnAtPoint(e.getPoint());

//                if (col == 10) {
//                    Rectangle rect = table.getCellRect(row, col, true);
//                    if (e.getX() < rect.x + rect.width / 2)
////                        moDialogSua(maSP);
//                    else
////                        xoaSanPham(maSP);
//                } else if (e.getClickCount() == 2) {
////                    moDialogSua(maSP);
//                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBackground(BG);
        scroll.getViewport().setBackground(SURFACE);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        return scroll;
    }

    // ── Load & Render ────────────────────────────────────
    private void loadDanhSach() {
        // TODO: List<SanPham> list = bus.getAll();
        List<SanPham> list = spBus.getAll();
        tableModel.setRowCount(0);
        for (SanPham sp : list) {
            tableModel.addRow(new Object[]{
                    sp.getMasp(),                               // col 0 maSP (ẩn)
                    sp.getMasp(),                               // col 1 ma SP
                    sp.getTensp(),                              // col 2 tên
                    sp.getLoaisp(),                               // col 3 loại
                    sp.getThuonghieu(),                         // col 4 thương hiệu
                    sp.getKichco(),                             // col 5 kích cỡ
                    sp.getMausac(),                             // col 6 màu sắc
                    String.format("%,.0f đ", sp.getGiaban()),   // col 7 giá bán
                    sp.getTonkho(),                             // col 8 tồn kho
                    sp.getTrangthai(),                          // col 9 trạng thái
                    ""                                                            // col 10 thao tac
            });
        }
        updateStats();
    }

    private void locDuLieu() {
        String keyword = searchField.getText().trim().toLowerCase();

        List<SanPham> list = spBus.getAll();

        tableModel.setRowCount(0);

        for (SanPham sp : list) {

            if (sp.getTensp().toLowerCase().contains(keyword)) {

                tableModel.addRow(new Object[]{
                        sp.getMasp(),
                        sp.getMasp(),
                        sp.getTensp(),
                        sp.getLoaisp(),
                        sp.getThuonghieu(),
                        sp.getKichco(),
                        sp.getMausac(),
                        String.format("%,.0f đ", sp.getGiaban()),
                        sp.getTonkho(),
                        sp.getTrangthai(),
                        ""
                });

            }
        }
        updateStats();
    }

    private void updateStats() {
        lblTongSP.setText("SP: " + tableModel.getRowCount());
    }

    // ── CRUD ─────────────────────────────────────────────
//    private void moDialogThem() {
//        SanPhamDialog dialog = new SanPhamDialog(this, null);
//        dialog.setVisible(true);
//        if (dialog.getKetQua() != null) {
//            // TODO: bus.them(dialog.getKetQua());
//            loadDanhSach();
//            JOptionPane.showMessageDialog(this, "Them thanh cong!");
//        }
//    }
//
//    private void moDialogSua(String maSP) {
//        // TODO: SanPham sp = bus.getById(maSP);
//        SanPhamDialog dialog = new SanPhamDialog(this, maSP); // truyền maSP
//        dialog.setVisible(true);
//        if (dialog.getKetQua() != null) {
//            // TODO: bus.capNhat(dialog.getKetQua());
//            loadDanhSach();
//            JOptionPane.showMessageDialog(this, "Cap nhat thanh cong!");
//        }
//    }
//
//    private void xoaSanPham(String maSP) {
//        int confirm = JOptionPane.showConfirmDialog(this,
//                "Ban co chac muon xoa san pham: " + maSP + "?",
//                "Xac nhan xoa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
//        if (confirm == JOptionPane.YES_OPTION) {
//            // TODO: bus.xoa(maSP);
//            loadDanhSach();
//            JOptionPane.showMessageDialog(this, "Da xoa " + maSP);
//        }
//    }

    // ── Helpers ──────────────────────────────────────────
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

    private void styleCombo(JComboBox<String> cb) {
        cb.setBackground(CARD);
        cb.setForeground(TEXT2);
        cb.setFont(new Font("Sans serif", Font.BOLD, 12));
        cb.setBorder(BorderFactory.createLineBorder(BORDER));
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(SanPhamView::new);
    }
}
