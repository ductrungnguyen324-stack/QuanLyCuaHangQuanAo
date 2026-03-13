package gui.dialog;

import gui.controller.PhieuNhapChiTietController;
import entity.ChiTietPhieuNhapDTO;
import entity.PhieuNhapHangDTO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class PhieuNhapChiTietDialog extends JDialog {

    // ── Controller ───────────────────────────────────────
    private final PhieuNhapChiTietController controller = new PhieuNhapChiTietController();

    // ── Màu sắc ──────────────────────────────────────────
    private static final Color BG = new Color(10, 14, 30);
    private static final Color CARD = new Color(14, 20, 40);
    private static final Color CARD2 = new Color(18, 26, 52);
    private static final Color BORDER = new Color(30, 42, 72);
    private static final Color GREEN = new Color(16, 185, 129);
    private static final Color CYAN = new Color(6, 182, 212);
    private static final Color TEXT1 = new Color(226, 232, 240);
    private static final Color TEXT2 = new Color(100, 116, 139);

    // ── Format ───────────────────────────────────────────
    private final DecimalFormat df = new DecimalFormat("#,### đ");
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    // ── Constructor ──────────────────────────────────────
    public PhieuNhapChiTietDialog(Frame parent, PhieuNhapHangDTO pn) {
        super(parent, true);
        setTitle("Chi tiết phiếu nhập: " + pn.getMaPN());
        setSize(700, 600);
        setLocationRelativeTo(parent);
        setResizable(false);
        buildUI(pn);
    }

    // ── Build UI ─────────────────────────────────────────
    private void buildUI(PhieuNhapHangDTO pn) {
        // Lấy dữ liệu qua Controller
        ArrayList<ChiTietPhieuNhapDTO> listCT = controller.getChiTiet(pn.getMaPN());

        JPanel main = new JPanel(new BorderLayout(0, 0));
        main.setBackground(BG);
        main.add(buildHeader(pn), BorderLayout.NORTH);
        main.add(buildBody(pn, listCT), BorderLayout.CENTER);
        main.add(buildFooter(pn), BorderLayout.SOUTH);
        setContentPane(main);
    }

    // ── Header ───────────────────────────────────────────
    private JPanel buildHeader(PhieuNhapHangDTO pn) {
        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setBackground(new Color(11, 16, 35));
        hdr.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                BorderFactory.createEmptyBorder(16, 22, 16, 22)
        ));
        JLabel t = new JLabel("Phiếu nhập: " + pn.getMaPN());
        t.setFont(new Font("Dialog", Font.BOLD, 16));
        t.setForeground(TEXT1);
        JLabel s = new JLabel("Ngày tạo: " + sdf.format(pn.getNgayTao()) + "  |  Nhân viên: " + pn.getMaNV());
        s.setFont(new Font("Dialog", Font.PLAIN, 12));
        s.setForeground(TEXT2);
        hdr.add(t, BorderLayout.NORTH);
        hdr.add(s, BorderLayout.SOUTH);
        return hdr;
    }

    // ── Body ─────────────────────────────────────────────
    private JPanel buildBody(PhieuNhapHangDTO pn, ArrayList<ChiTietPhieuNhapDTO> listCT) {
        JPanel body = new JPanel(new BorderLayout(0, 14));
        body.setBackground(BG);
        body.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        body.add(buildInfoCards(pn), BorderLayout.NORTH);
        body.add(buildTable(listCT), BorderLayout.CENTER);
        return body;
    }

    // ── 3 thẻ thông tin ──────────────────────────────────
    private JPanel buildInfoCards(PhieuNhapHangDTO pn) {
        JPanel info = new JPanel(new GridLayout(1, 3, 12, 0));
        info.setBackground(BG);
        Color colorTT = controller.isDaDuyet(pn) ? GREEN : CYAN;
        info.add(makeInfoCard("Nhà cung cấp", pn.getNhaCungCap(), TEXT1));
        info.add(makeInfoCard("Trạng thái", pn.getTrangThai(), colorTT));
        info.add(makeInfoCard("Tổng thành tiền", df.format(pn.getTongTien()), CYAN));
        return info;
    }

    // ── Bảng chi tiết ────────────────────────────────────
    private JScrollPane buildTable(ArrayList<ChiTietPhieuNhapDTO> listCT) {
        String[] cols = {"Mã CTPN", "Mã PN", "Mã SP", "Số lượng", "Giá nhập", "Thành tiền"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        for (ChiTietPhieuNhapDTO ct : listCT) {
            model.addRow(new Object[]{
                ct.getMaCTPN(), ct.getMaPN(), ct.getMaSP(),
                ct.getSoLuong(), df.format(ct.getDonGia()), df.format(ct.getThanhTien())
            });
        }

        JTable table = new JTable(model);
        table.setBackground(CARD);
        table.setForeground(TEXT1);
        table.setRowHeight(36);
        table.setFont(new Font("Dialog", Font.PLAIN, 12));
        table.setGridColor(BORDER);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(25, 35, 80));

        JTableHeader header = table.getTableHeader();
        header.setBackground(CARD2);
        header.setForeground(TEXT2);
        header.setFont(new Font("Dialog", Font.BOLD, 11));

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(CARD);
        scroll.setBorder(new LineBorder(BORDER, 1));
        return scroll;
    }

    // ── Footer ───────────────────────────────────────────
    private JPanel buildFooter(PhieuNhapHangDTO pn) {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        footer.setBackground(BG);

        JButton btnDuyet = makeButton("✓ Duyệt nhập kho", GREEN, Color.WHITE);
        JButton btnDong = makeButton("Đóng", CARD, TEXT2);

        btnDuyet.setEnabled(!controller.isDaDuyet(pn));
        btnDuyet.addActionListener(e -> onDuyetClick(pn));
        btnDong.addActionListener(e -> dispose());

        footer.add(btnDuyet);
        footer.add(btnDong);
        return footer;
    }

    // ── Sự kiện Duyệt ────────────────────────────────────
    private void onDuyetClick(PhieuNhapHangDTO pn) {
        int ok = JOptionPane.showConfirmDialog(this,
                "Duyệt phiếu " + pn.getMaPN() + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            if (controller.duyetPhieu(pn.getMaPN())) {
                JOptionPane.showMessageDialog(this, "Duyệt thành công!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Duyệt thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ── Helpers UI ───────────────────────────────────────
    private JPanel makeInfoCard(String label, String value, Color color) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
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
