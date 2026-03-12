package gui;
import dao.ChiTietHoaDonDAO;
import dao.HoaDonDAO;
import gui.dialog.*;
import bus.KhuyenMaiBUS;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.KhuyenMai;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;


public class HoaDonDialog extends JDialog {

    private static final Color BG     = new Color(10, 14, 30);
    private static final Color CARD   = new Color(14, 20, 40);
    private static final Color CARD2  = new Color(18, 26, 52);
    private static final Color BORDER = new Color(30, 42, 72);
    private static final Color ACCENT = new Color(99, 102, 241);
    private static final Color GREEN  = new Color(16, 185, 129);
    private static final Color RED    = new Color(239, 68, 68);
    private static final Color TEXT1  = new Color(226, 232, 240);
    private static final Color TEXT2  = new Color(100, 116, 139);
    private static final Color DANGER = new Color(239, 68, 68);

    private String ketQua = null;
    private String maNV   = null; // nhận từ session

    // Thong tin HD
    private JTextField     fMaHD, fMaKH, fMaKM;
    private JComboBox<String> fPhuongThuc;
    private JLabel         lblStatus;
    private JTable ctTable;

    // Bang chi tiet san pham
    private DefaultTableModel ctModel;
    private JLabel            lblTongTien, lblGiamGia, lblThanhToan;
    // model
    KhuyenMaiBUS kmbus = new KhuyenMaiBUS();
    HoaDonDAO hddao = new HoaDonDAO();

    public HoaDonDialog(Frame parent, String maHD, String maNV) {
        super(parent, true);
        this.maNV = maNV;
        setTitle(maHD != null ? "Sửa hoá đơn" : "Tạo hoá đơn mới");
        setSize(700, 600);
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

        // Header
        main.add(buildHeader(), BorderLayout.NORTH);

        // Body = info tren + bang CTHD duoi
        JPanel body = new JPanel(new BorderLayout(0, 14));
        body.setBackground(BG);
        body.setBorder(BorderFactory.createEmptyBorder(16, 20, 0, 20));
        body.add(buildInfoForm(), BorderLayout.NORTH);
        body.add(buildCTHDTable(), BorderLayout.CENTER);
        main.add(body, BorderLayout.CENTER);

        // Footer: tong tien + nut
        main.add(buildFooter(), BorderLayout.SOUTH);
        setContentPane(main);
    }

    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(new Color(11, 16, 35));
        h.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                BorderFactory.createEmptyBorder(16, 22, 16, 22)
        ));
        JLabel t = new JLabel("Tạo hoá đơn mới");
        t.setFont(new Font("Dialog", Font.BOLD, 16));
        t.setForeground(TEXT1);
        JLabel s = new JLabel("Chọn sản phẩm và điền thông tin");
        s.setFont(new Font("Dialog", Font.PLAIN, 12));
        s.setForeground(TEXT2);
        h.add(t, BorderLayout.NORTH);
        h.add(s, BorderLayout.SOUTH);
        return h;
    }

    // Phan thong tin hoa don (tren)
    private JPanel buildInfoForm() {
        JPanel p = new JPanel(new GridLayout(2, 4, 12, 10));
        p.setBackground(BG);

        fMaHD = addField(p, "Mã HD *",     hddao.generateHD());
        fMaKH = addField(p, "Mã KH *",     "");
        fMaKM = addField(p, "Mã KM",       "");
        fPhuongThuc = new JComboBox<>(new String[]{
                "TIENMAT","CHUYENKHOAN","MOMO","VNPAY","ZaloPay"
        });
        JLabel lblPT = new JLabel("Phương thức TT");
        lblPT.setFont(new Font("Dialog", Font.BOLD, 11));
        lblPT.setForeground(TEXT2);
        fPhuongThuc.setBackground(CARD2);
        fPhuongThuc.setForeground(new Color(0,0,0));
        fPhuongThuc.setFont(new Font("Dialog", Font.PLAIN, 12));
        p.add(lblPT);
        p.add(fPhuongThuc);

        return p;
    }

    // Bang chi tiet hoa don (chitiethoadon)
    private JPanel buildCTHDTable() {
        JPanel wrap = new JPanel(new BorderLayout(0, 8));
        wrap.setBackground(BG);

        // Thành công cu
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setBackground(BG);
        JLabel lbl = new JLabel("Chi tiết sản phẩm:");
        lbl.setFont(new Font("sans serif", Font.BOLD, 13));
        lbl.setForeground(TEXT1);
        JButton btnThem = makeButton("+ Thêm SP", ACCENT, Color.WHITE);
        JButton btnXoa  = makeButton("Xoá dòng", RED,    Color.WHITE);

        // Them san pham mau vao bang
        btnThem.addActionListener(e -> {
            ctModel.addRow(new Object[]{
                    "SP00" + (ctModel.getRowCount()+1),
                    "Sản phẩm " + (ctModel.getRowCount()+1),
                    1,
                    150000.0,
                    150000.0
            });
            tinhTong();
        });
        // Xoá dòng dang chon
        btnXoa.addActionListener(e -> {
            int row = ctTable.getSelectedRow();
            if (row >= 0) { ctModel.removeRow(row); tinhTong(); }
        });

        toolbar.add(lbl);
        toolbar.add(btnThem);
        toolbar.add(btnXoa);
        wrap.add(toolbar, BorderLayout.NORTH);

        // Bang CTHD
        String[] cols = {"Mã SP", "Tên SP", "Số lượng", "Đơn giá", "Thành tiền"};
        ctModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) {
                return c == 2; // chi cho sua so luong
            }
        };

        ctTable = new JTable(ctModel) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                c.setBackground(isRowSelected(row)
                        ? new Color(25, 35, 80) : new Color(14, 20, 40));
                c.setForeground(TEXT1);
                return c;
            }
        };
        ctTable.setBackground(CARD);
        ctTable.setForeground(TEXT1);
        ctTable.setGridColor(BORDER);
        ctTable.setRowHeight(36);
        ctTable.setFont(new Font("Dialog", Font.PLAIN, 12));
        ctTable.setShowVerticalLines(false);
        ctTable.getTableHeader().setBackground(CARD2);
        ctTable.getTableHeader().setForeground(TEXT2);
        ctTable.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 11));

        // Cap nhat thanh tien khi sua so luong
        ctModel.addTableModelListener(e -> {
            if (e.getColumn() == 2) tinhTong();
        });

        JScrollPane scroll = new JScrollPane(ctTable);
        scroll.getViewport().setBackground(CARD);
        scroll.setBorder(new LineBorder(BORDER, 1));
        wrap.add(scroll, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(BG);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
                BorderFactory.createEmptyBorder(12, 20, 16, 20)
        ));

        // Tổng tiền
        JPanel tong = new JPanel(new GridLayout(1, 3, 20, 0));
        tong.setOpaque(false);
        lblTongTien  = makeTongLabel("Tổng tiền:", "0 VNĐ");
        lblGiamGia   = makeTongLabel("Giảm giá:", "0 VNĐ");
        lblThanhToan = makeTongLabel("Thanh toán:", "0 VNĐ");
        tong.add(lblTongTien);
        tong.add(lblGiamGia);
        tong.add(lblThanhToan);

        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Dialog", Font.BOLD, 12));
        lblStatus.setForeground(DANGER);

        JPanel topFt = new JPanel(new BorderLayout());
        topFt.setOpaque(false);
        topFt.add(tong,      BorderLayout.WEST);
        topFt.add(lblStatus, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btns.setOpaque(false);
        JButton btnHuy = makeButton("Huỷ",        CARD,   TEXT2);
        JButton btnLuu = makeButton("Tạo hoá đơn", ACCENT, Color.WHITE);
        btnHuy.addActionListener(e -> dispose());
        btnLuu.addActionListener(e -> save());
        btns.add(btnHuy);
        btns.add(btnLuu);

        footer.add(topFt, BorderLayout.NORTH);
        footer.add(btns,  BorderLayout.SOUTH);
        return footer;
    }

    private JLabel makeTongLabel(String title, String value) {
        JPanel p = new JPanel(new BorderLayout(4, 0));
        p.setOpaque(false);
        JLabel t = new JLabel(title);
        t.setFont(new Font("Dialog", Font.PLAIN, 11));
        t.setForeground(TEXT2);
        JLabel v = new JLabel(value);
        v.setFont(new Font("Dialog", Font.BOLD, 14));
        v.setForeground(TEXT1);
        // Tra ve label value de update sau
        v.putClientProperty("title", t);
        return v;
    }

    private void tinhTong() {
        double tong = 0;
        for (int i = 0; i < ctModel.getRowCount(); i++) {
            Object sl = ctModel.getValueAt(i, 2);
            Object dg = ctModel.getValueAt(i, 3);
            try {
                double soLuong = Double.parseDouble(sl.toString());
                double donGia  = Double.parseDouble(dg.toString());
                double tt = soLuong * donGia;
                ctModel.setValueAt(tt, i, 4);
                tong += tt;
            } catch (Exception ignored) {}
        }
        lblTongTien.setText(String.format("%,.0f VNĐ", tong));
        lblThanhToan.setText(String.format("%,.0f VNĐ", tong));
    }

    private void save() {
        if (fMaHD.getText().trim().isEmpty()) {
            lblStatus.setText("Mã HD khong duoc trong!"); return;
        }
        if (fMaKH.getText().trim().isEmpty()) {
            lblStatus.setText("Mã KH không được trống!"); return;
        }
        if (ctModel.getRowCount() == 0) {
            lblStatus.setText("Chưa có sản phẩm nào!"); return;
        }
        // Controller sẽ gọi buildHoaDon() + buildChiTiet() + bus.them()
        ketQua = fMaHD.getText().trim();
        dispose();
    }

    private JTextField addField(JPanel form, String label, String value) {
        JLabel l = new JLabel(label);
        l.setFont(new Font("Dialog", Font.BOLD, 11));
        l.setForeground(TEXT2);
        JTextField f = new JTextField(value);
        f.setBackground(CARD2);
        f.setForeground(TEXT1);
        f.setCaretColor(ACCENT);
        f.setFont(new Font("Dialog", Font.PLAIN, 12));
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)
        ));
        form.add(l);
        form.add(f);
        return f;
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
        b.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        return b;
    }
    public String getKetQua() { return ketQua; }
public String getMaHD()      { return fMaHD.getText().trim(); }
public String getMaKH()      { return fMaKH.getText().trim(); }
public int    getSoLuongSP() { return ctModel.getRowCount(); }
public double getTongTien() {
    double tong = 0;
    for (int i = 0; i < ctModel.getRowCount(); i++) {
        try { tong += Double.parseDouble(ctModel.getValueAt(i, 4).toString()); }
        catch (Exception ignored) {}
    }
    return tong;
}

public HoaDon buildHoaDon() {
    HoaDon hd = new HoaDon();
    hd.setMaHD(fMaHD.getText().trim());
    hd.setMaKH(fMaKH.getText().trim());
    // Lấy maNV từ session — truyền vào Dialog qua constructor
    hd.setMaNV(maNV != null ? maNV : "NV001");
    hd.setKhuyenmai(fMaKM.getText().trim().isEmpty() ? null : fMaKM.getText().trim());
    hd.setNgaytao(LocalDateTime.now());
    hd.setPhuongthucTT((String) fPhuongThuc.getSelectedItem());
    hd.setTrangthai("CHUATHANHTOAN");

    // Tính tiền từ bảng chi tiết
    double tongTien = getTongTien();
    hd.setTongtien(tongTien);
    // Tính giảm giá từ mã KM nếu có
    double soTienGiam = kmbus.getByID(fMaHD.getText().trim()).tinhSoTienGiam(tongTien);
    hd.setSotiengiam(soTienGiam);
    hd.setThanhtoan(tongTien - soTienGiam);
    return hd;
}

public List<ChiTietHoaDon> buildChiTiet() {
    List<ChiTietHoaDon> list = new ArrayList<>();
    String maHD = fMaHD.getText().trim();


    ChiTietHoaDonDAO cthdDAO = new ChiTietHoaDonDAO();
    String maDau = cthdDAO.generate();
    int soThu    = Integer.parseInt(maDau.substring(4));

    for (int i = 0; i < ctModel.getRowCount(); i++) {
        ChiTietHoaDon ct = new ChiTietHoaDon();
        ct.setMaCTHD(String.format("CTHD%03d", soThu + i));
        ct.setMaHD(maHD);
        ct.setMaSP(ctModel.getValueAt(i, 0).toString());
        ct.setTenSP(ctModel.getValueAt(i, 1).toString());
        try {
            ct.setSoluong(Double.parseDouble(ctModel.getValueAt(i, 2).toString()));
            ct.setDongia(Double.parseDouble(ctModel.getValueAt(i, 3).toString()));
            ct.setThanhtien(Double.parseDouble(ctModel.getValueAt(i, 4).toString()));
        } catch (Exception ignored) {}
        ct.setKhuyenmai(null);
        list.add(ct);
    }
    return list;
}
}
