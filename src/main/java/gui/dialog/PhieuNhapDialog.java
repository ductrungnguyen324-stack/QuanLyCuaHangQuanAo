package gui.dialog;

import gui.controller.PhieuNhapDialogController;
import entity.ChiTietPhieuNhapDTO;
import entity.PhieuNhapHangDTO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class PhieuNhapDialog extends JDialog {

 
    private final PhieuNhapDialogController controller = new PhieuNhapDialogController();


    private static final Color BG     = new Color(10, 14, 30);
    private static final Color CARD   = new Color(14, 20, 40);
    private static final Color CARD2  = new Color(18, 26, 52);
    private static final Color BORDER = new Color(30, 42, 72);
    private static final Color ACCENT = new Color(99, 102, 241);
    private static final Color RED    = new Color(239, 68, 68);
    private static final Color CYAN   = new Color(6, 182, 212);
    private static final Color TEXT1  = new Color(226, 232, 240);
    private static final Color TEXT2  = new Color(100, 116, 139);
    private static final Color DANGER = new Color(239, 68, 68);

    private String            ketQua = null;
    private JTextField        fMaPN, fMaNV, fNhaCungCap;
    private JLabel            lblTongTien, lblStatus;
    private DefaultTableModel ctModel;
    private JTable            ctTable;


    public PhieuNhapDialog(Frame parent, PhieuNhapHangDTO pn) {
        super(parent, true);
        setTitle(pn != null ? "Sửa phiếu nhập" : "Tạo phiếu nhập mới");
        setSize(680, 580);
        setLocationRelativeTo(parent);
        setResizable(false);
        buildUI(pn);
        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    private void buildUI(PhieuNhapHangDTO pn) {
        JPanel main = new JPanel(new BorderLayout(0, 0));
        main.setBackground(BG);
        main.add(buildHeader(pn),  BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 14));
        body.setBackground(BG);
        body.setBorder(BorderFactory.createEmptyBorder(16, 20, 0, 20));
        body.add(buildInfoForm(pn), BorderLayout.NORTH);
        body.add(buildCTTable(),    BorderLayout.CENTER);

        main.add(body,            BorderLayout.CENTER);
        main.add(buildFooter(pn), BorderLayout.SOUTH);
        setContentPane(main);
    }

    private JPanel buildHeader(PhieuNhapHangDTO pn) {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(new Color(11, 16, 35));
        h.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                BorderFactory.createEmptyBorder(16, 22, 16, 22)
        ));
        JLabel t = new JLabel(pn != null ? "Sửa phiếu nhập: " + pn.getMaPN() : "Tạo phiếu nhập mới");
        t.setFont(new Font("Dialog", Font.BOLD, 16));
        t.setForeground(TEXT1);
        JLabel s = new JLabel(pn != null ? "Chỉnh sửa thông tin phiếu " + pn.getMaPN() : "Chọn sản phẩm và điền số lượng, giá nhập");
        s.setFont(new Font("Dialog", Font.PLAIN, 12));
        s.setForeground(TEXT2);
        h.add(t, BorderLayout.NORTH);
        h.add(s, BorderLayout.SOUTH);
        return h;
    }

    private JPanel buildInfoForm(PhieuNhapHangDTO pn) {
        JPanel p = new JPanel(new GridLayout(2, 4, 12, 10));
        p.setBackground(BG);

        fMaPN       = addField(p, "Mã phiếu *",     pn != null ? pn.getMaPN()       : "PN001");
        fMaNV       = addField(p, "Mã nhân viên *",  pn != null ? pn.getMaNV()       : "NV001");
        fNhaCungCap = addField(p, "Nhà cung cấp *",  pn != null ? pn.getNhaCungCap() : "");

        JLabel lblNgay = new JLabel("Ngày tạo");
        lblNgay.setFont(new Font("Dialog", Font.BOLD, 11));
        lblNgay.setForeground(TEXT2);

        String ngay = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm")
                .format(pn != null ? pn.getNgayTao() : new java.util.Date());
        JLabel valNgay = new JLabel(ngay);
        valNgay.setFont(new Font("Dialog", Font.BOLD, 12));
        valNgay.setForeground(CYAN);

        p.add(lblNgay);
        p.add(valNgay);

        if (pn != null) { fMaPN.setEditable(false); fMaPN.setEnabled(false); }
        return p;
    }

    private JPanel buildCTTable() {
        JPanel wrap = new JPanel(new BorderLayout(0, 8));
        wrap.setBackground(BG);

        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bar.setBackground(BG);
        JLabel lbl = new JLabel("Chi tiết sản phẩm nhập:");
        lbl.setFont(new Font("Dialog", Font.BOLD, 13));
        lbl.setForeground(TEXT1);

        JButton btnThem = makeButton("+ Thêm SP", ACCENT, Color.WHITE);
        JButton btnXoa  = makeButton("Xoá dòng",  RED,    Color.WHITE);

        btnThem.addActionListener(e -> {
            int n = ctModel.getRowCount() + 1;
            ctModel.addRow(new Object[]{"SP00" + n, "Sản phẩm " + n, 1, 80000.0, 80000.0});
            tinhTong();
        });
        btnXoa.addActionListener(e -> {
            int row = ctTable.getSelectedRow();
            if (row >= 0) { ctModel.removeRow(row); tinhTong(); }
            else JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần xoá!");
        });

        bar.add(lbl); bar.add(btnThem); bar.add(btnXoa);
        wrap.add(bar, BorderLayout.NORTH);

        String[] cols = {"Mã SP", "Tên SP", "Số lượng", "Giá nhập", "Thành tiền"};
        ctModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 0 || c == 2 || c == 3; }
            @Override public Class<?> getColumnClass(int c) {
                return (c == 2 || c == 3 || c == 4) ? Double.class : String.class;
            }
        };

        ctTable = new JTable(ctModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                boolean edit = col == 2 || col == 3;
                c.setBackground(isRowSelected(row) ? new Color(25, 35, 80) : edit ? CARD2 : CARD);
                c.setForeground(edit ? CYAN : TEXT1);
                return c;
            }
        };
        ctTable.setBackground(CARD); ctTable.setForeground(TEXT1);
        ctTable.setGridColor(BORDER); ctTable.setRowHeight(36);
        ctTable.setFont(new Font("Dialog", Font.PLAIN, 12));
        ctTable.setShowVerticalLines(false);

        JTableHeader th = ctTable.getTableHeader();
        th.setBackground(CARD2); th.setForeground(TEXT2);
        th.setFont(new Font("Dialog", Font.BOLD, 11));

        // Tự động tính thành tiền khi sửa
        ctModel.addTableModelListener(e -> {
            if (e.getColumn() == 2 || e.getColumn() == 3) {
                int row = e.getFirstRow();
                try {
                    double sl = controller.parseDouble(ctModel.getValueAt(row, 2));
                    double gn = controller.parseDouble(ctModel.getValueAt(row, 3));
                    ctModel.setValueAt(sl * gn, row, 4);
                } catch (Exception ignored) {}
                tinhTong();
            }
        });

        JTextField tfMaSP = new JTextField();
        tfMaSP.addActionListener(e -> {
            int row = ctTable.getSelectedRow();
            if (row != -1) {
                String[] info = controller.tuDongDienThongTin(tfMaSP.getText().trim());
                ctModel.setValueAt(info[0], row, 1);
                ctModel.setValueAt(Double.parseDouble(info[1]), row, 3);
                ctTable.changeSelection(row, 2, false, false);
            }
        });
        ctTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(tfMaSP));

        JLabel hint = new JLabel("  * Cột màu xanh có thể chỉnh sửa trực tiếp");
        hint.setFont(new Font("Dialog", Font.ITALIC, 10));
        hint.setForeground(new Color(60, 80, 120));

        JScrollPane scroll = new JScrollPane(ctTable);
        scroll.getViewport().setBackground(CARD);
        scroll.setBorder(new LineBorder(BORDER, 1));

        JPanel tableWrap = new JPanel(new BorderLayout(0, 4));
        tableWrap.setBackground(BG);
        tableWrap.add(scroll, BorderLayout.CENTER);
        tableWrap.add(hint,   BorderLayout.SOUTH);

        wrap.add(tableWrap, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel buildFooter(PhieuNhapHangDTO pn) {
        JPanel footer = new JPanel(new BorderLayout(0, 15));
        footer.setBackground(BG);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
                BorderFactory.createEmptyBorder(12, 20, 14, 20)
        ));

        JPanel tongPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        tongPanel.setOpaque(false);
        JLabel tongLabel = new JLabel("Tổng thành tiền:");
        tongLabel.setFont(new Font("Dialog", Font.BOLD, 13));
        tongLabel.setForeground(TEXT2);
        lblTongTien = new JLabel("0 đ");
        lblTongTien.setFont(new Font("Dialog", Font.BOLD, 18));
        lblTongTien.setForeground(CYAN);
        tongPanel.add(tongLabel); tongPanel.add(lblTongTien);

        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Dialog", Font.BOLD, 12));
        lblStatus.setForeground(DANGER);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(tongPanel,  BorderLayout.WEST);
        top.add(lblStatus,  BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btns.setOpaque(false);
        JButton btnHuy = makeButton("Huỷ", CARD, TEXT2);
        JButton btnLuu = makeButton(pn != null ? "Cập nhật phiếu" : "Tạo phiếu nhập", ACCENT, Color.WHITE);
        btnHuy.addActionListener(e -> dispose());
        btnLuu.addActionListener(e -> onSave(pn));
        btns.add(btnHuy); btns.add(btnLuu);

        footer.add(top,  BorderLayout.NORTH);
        footer.add(btns, BorderLayout.SOUTH);
        return footer;
    }

    private void onSave(PhieuNhapHangDTO pn) {
        String maPN = fMaPN.getText().trim();
        String maNV = fMaNV.getText().trim();
        String ncc  = fNhaCungCap.getText().trim();

        String errForm = controller.validate(maPN, maNV, ncc, ctModel.getRowCount());
        if (errForm != null) { lblStatus.setText(errForm); return; }

        try {
            ArrayList<ChiTietPhieuNhapDTO> dsCT   = new ArrayList<>();
            ArrayList<ChiTietPhieuNhapDTO> dsCTCu = (pn != null)
                    ? controller.getChiTietCu(pn.getMaPN()) : new ArrayList<>();
            double tongTien = 0;

            for (int i = 0; i < ctModel.getRowCount(); i++) {
                String maSP      = ctModel.getValueAt(i, 0).toString();
                int    sl        = (int) controller.parseDouble(ctModel.getValueAt(i, 2));
                double gia       = controller.parseDouble(ctModel.getValueAt(i, 3));
                double thanhTien = sl * gia;

                String errDong = controller.validateDong(i + 1, sl, gia);
                if (errDong != null) { lblStatus.setText(errDong); return; }

                String maCTPN = null;
                for (ChiTietPhieuNhapDTO old : dsCTCu)
                    if (old.getMaSP().equals(maSP)) { maCTPN = old.getMaCTPN(); break; }
                if (maCTPN == null) maCTPN = controller.taoMaCTPN();

                dsCT.add(new ChiTietPhieuNhapDTO(maCTPN, maPN, maSP, sl, gia, thanhTien));
                tongTien += thanhTien;
            }

            boolean success;
            if (pn == null) {
                PhieuNhapHangDTO newPn = new PhieuNhapHangDTO(
                        maPN, maNV, ncc, new java.util.Date(), tongTien, "Chờ xử lý");
                success = controller.themPhieuMoi(newPn, dsCT);
            } else {
                pn.setMaNV(maNV); pn.setNhaCungCap(ncc); pn.setTongTien(tongTien);
                success = controller.capNhatPhieu(pn, dsCT);
            }

            if (success) {
                JOptionPane.showMessageDialog(this, (pn == null ? "Tạo" : "Cập nhật") + " thành công!");
                this.ketQua = maPN;
                dispose();
            } else {
                lblStatus.setText("Lỗi: Không thể lưu vào cơ sở dữ liệu!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            lblStatus.setText("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void tinhTong() {
        double tong = 0;
        for (int i = 0; i < ctModel.getRowCount(); i++)
            tong += controller.parseDouble(ctModel.getValueAt(i, 4));
        lblTongTien.setText(String.format("%,.0f đ", tong));
    }

    private JTextField addField(JPanel form, String label, String value) {
        JLabel l = new JLabel(label);
        l.setFont(new Font("Dialog", Font.BOLD, 11)); l.setForeground(TEXT2);
        JTextField f = new JTextField(value);
        f.setBackground(CARD2); f.setForeground(TEXT1); f.setCaretColor(ACCENT);
        f.setFont(new Font("Dialog", Font.PLAIN, 12));
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        form.add(l); form.add(f);
        return f;
    }

    private JButton makeButton(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Dialog", Font.BOLD, 12));
        b.setBackground(bg); b.setForeground(fg);
        b.setOpaque(true); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return b;
    }

    public String getKetQua() { return ketQua; }
}
