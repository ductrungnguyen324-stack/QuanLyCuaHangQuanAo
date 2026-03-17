package gui;

import entity.NhanVien;
import gui.controller.*;
import gui.view.*;

import javax.swing.*;
import java.awt.*;


public class MainFrame extends JFrame {

    // ── Màu ─────────────────────────────────────────────
    private static final Color BG       = new Color(7,  10, 20);
    private static final Color SURFACE  = new Color(11, 15, 30);
    private static final Color CARD     = new Color(14, 20, 40);
    private static final Color BORDER   = new Color(30, 42, 72);
    private static final Color ACCENT   = new Color(99, 102, 241);
    private static final Color TEXT1    = new Color(226, 232, 240);
    private static final Color TEXT2    = new Color(100, 116, 139);
    private static final Color SIDEBAR  = new Color(9, 13, 26);
    private static final Color SEL      = new Color(25, 35, 80);

    private JPanel      contentPanel;
    private CardLayout  cardLayout;

    private String maNV;
    private String chucvu; // ← THÊM MỚI

    // Sidebar buttons
    private JButton btnHoaDon;
    private JButton btnKhuyenMai;
    private JButton btnKhachHang;
    private JButton btnNhanVien;
    private JButton btnSanPham;
    private JButton btnPhieuNhap;
    private JButton btnBaoCao;

    // ── THAY ĐỔI: nhận NhanVien thay vì String maNV ──
    public MainFrame(NhanVien nv) {
        this.maNV   = nv.getManv();
        this.chucvu = nv.getChucvu();

        setTitle("Quản lý bán quần áo");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1024, 600));
        setBackground(BG);

        setLayout(new BorderLayout(0, 0));
        add(buildSidebar(),  BorderLayout.WEST);
        add(buildContent(),  BorderLayout.CENTER);

        showCard("NHANVIEN");
        setVisible(true);
    }

    // ── Sidebar ──────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel side = new JPanel(new BorderLayout());
        side.setBackground(SIDEBAR);
        side.setPreferredSize(new Dimension(210, 0));
        side.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER));

        // Logo
        JPanel logo = new JPanel(new BorderLayout());
        logo.setBackground(SIDEBAR);
        logo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));
        JLabel appName = new JLabel("☕ Hệ Thống Quản Lý");
        appName.setFont(new Font("Sans serif", Font.BOLD, 16));
        appName.setForeground(ACCENT);
        logo.add(appName, BorderLayout.CENTER);

        // Menu items
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBackground(SIDEBAR);
        menu.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        btnHoaDon    = makeSideBtn("Hoá đơn",     "HOADON");
        btnKhuyenMai = makeSideBtn("Khuyến Mãi",  "KHUYENMAI");
        btnKhachHang = makeSideBtn("Khách Hàng",  "KHACHHANG");
        btnNhanVien  = makeSideBtn("Nhân viên",   "NHANVIEN");
        btnSanPham   = makeSideBtn("Sản phẩm",    "SANPHAM");
        btnPhieuNhap = makeSideBtn("Phiếu nhập",  "PHIEUNHAP");
        btnBaoCao = makeSideBtn("Báo cáo", "BAOCAO");

        menu.add(btnNhanVien);
        menu.add(btnSanPham);
        menu.add(btnKhachHang);
        menu.add(btnHoaDon);
        menu.add(btnKhuyenMai);
        menu.add(btnPhieuNhap);
        menu.add(btnBaoCao);

        setSelected(btnNhanVien);

        // Footer: tên NV + chức vụ + logout
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(SIDEBAR);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        JLabel lblNV = new JLabel("NV: " + maNV + "  [" + chucvu + "]");
        lblNV.setFont(new Font("Sans serif", Font.BOLD, 12));
        lblNV.setForeground(TEXT1);

        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setFont(new Font("Sans serif", Font.BOLD, 11));
        btnLogout.setForeground(new Color(239, 68, 68));
        btnLogout.setBackground(SIDEBAR);
        btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.setHorizontalAlignment(SwingConstants.LEFT);
        btnLogout.addActionListener(e -> handleLogout());

        footer.add(lblNV,     BorderLayout.NORTH);
        footer.add(btnLogout, BorderLayout.SOUTH);

        side.add(logo,   BorderLayout.NORTH);
        side.add(menu,   BorderLayout.CENTER);
        side.add(footer, BorderLayout.SOUTH);
        return side;
    }

    private JButton makeSideBtn(String text, String card) {
        JButton b = new JButton(text);
        b.setFont(new Font("Sans serif", Font.PLAIN, 13));
        b.setForeground(TEXT2);
        b.setBackground(SIDEBAR);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!b.getBackground().equals(SEL)) {
                    b.setBackground(CARD);
                    b.setForeground(TEXT1);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!b.getBackground().equals(SEL)) {
                    b.setBackground(SIDEBAR);
                    b.setForeground(TEXT2);
                }
            }
        });

        b.addActionListener(e -> {
            clearSelected();
            setSelected(b);
            showCard(card);
        });

        return b;
    }

    private void setSelected(JButton b) {
        b.setBackground(SEL);
        b.setForeground(TEXT1);
        b.setFont(new Font("Sans serif", Font.BOLD, 13));
    }

    private void clearSelected() {
        for (Component c : ((JPanel) btnHoaDon.getParent()).getComponents()) {
            if (c instanceof JButton btn) {
                btn.setBackground(SIDEBAR);
                btn.setForeground(TEXT2);
                btn.setFont(new Font("Sans serif", Font.PLAIN, 13));
            }
        }
    }

    // ── Content area ─────────────────────────────────────
    private JPanel buildContent() {
        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG);

        // ── Hoá đơn ──
        HoaDonView hoaDonView = new HoaDonView();
        new HoaDonController(hoaDonView, chucvu); // ← truyền chucvu
        contentPanel.add(wrapView(hoaDonView), "HOADON");

        // ── Khuyến mãi ──
        KhuyenMaiView khuyenmaiview = new KhuyenMaiView(chucvu);
        new KhuyenMaiController(khuyenmaiview, chucvu); // ← truyền chucvu
        contentPanel.add(wrapView(khuyenmaiview), "KHUYENMAI");

        // ── Khách hàng ──
        KhachHangView khachHangView = new KhachHangView(chucvu);
        new KhachHangController(khachHangView, chucvu); // ← truyền chucvu
        contentPanel.add(wrapView(khachHangView), "KHACHHANG");

        // ── Nhân viên ──
        NhanVienView nvview = new NhanVienView(chucvu);
        new NhanVienController(nvview, chucvu); // ← truyền chucvu
        contentPanel.add(wrapView(nvview), "NHANVIEN");

        // ── Sản phẩm ── //thêm
        SanPhamView sanPhamView = new SanPhamView();
        SanPhamController sanPhamController=new SanPhamController(sanPhamView, chucvu); // ← truyền chucvu
        contentPanel.add(wrapView(sanPhamView), "SANPHAM");

        // ── Phiếu nhập ──//thêm
         PhieuNhapView phieuNhapView = new PhieuNhapView(sanPhamController);
         new PhieuNhapController(chucvu);
         contentPanel.add(wrapView(phieuNhapView), "PHIEUNHAP");

         BaoCaoView baocaoview = new BaoCaoView();
         contentPanel.add(wrapView(baocaoview), "BAOCAO");

        return contentPanel;
    }

    private JPanel wrapView(JFrame frame) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);
        Container content = frame.getContentPane();
        frame.dispose();
        wrapper.add(content, BorderLayout.CENTER);
        return wrapper;
    }

    private void showCard(String name) {
        cardLayout.show(contentPanel, name);
    }

    // ── Logout ───────────────────────────────────────────
    private void handleLogout() {
        int ok = JOptionPane.showConfirmDialog(this,
                "Bạn có muốn đăng xuất không?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(LoginView::new);
        }
    }


}