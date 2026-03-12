import gui.controller.HoaDonController;
import gui.view.HoaDonView;
import gui.view.LoginView;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * MainFrame.java — Khung chính sau khi đăng nhập
 *
 * Nhận từ LoginController: hoTen, maNV, role
 * Khởi tạo tất cả Panel + Controller tương ứng
 */
public class MainFrame extends JFrame {

    private static final Color BG      = new Color(7,  10, 20);
    private static final Color SIDEBAR = new Color(11, 15, 30);
    private static final Color CARD    = new Color(14, 20, 40);
    private static final Color BORDER  = new Color(30, 42, 72);
    private static final Color ACCENT  = new Color(99, 102, 241);
    private static final Color TEXT1   = new Color(226, 232, 240);
    private static final Color TEXT2   = new Color(100, 116, 139);
    private static final Color HOVER   = new Color(20, 28, 58);
    private static final Color ACTIVE  = new Color(25, 32, 70);

    // ── Thông tin nhân viên đăng nhập ───────────────────
    private final String hoTen;
    private final String maNV;
    private final String role;

    // ── Layout ──────────────────────────────────────────
    private JPanel     contentPanel;
    private CardLayout cardLayout;

    // ── Các Panel — khởi tạo 1 lần, dùng lại ──────────
    private HoaDonView hdview;
//    private SanPhamPanel  sanPhamPanel;
//    private PhieuNhapPanel phieuNhapPanel;
//    private BaoCaoPanel   baoCaoPanel;

    // ── Constructor từ LoginController ──────────────────
    public MainFrame(String hoTen, String maNV, String role) {
        this.hoTen = hoTen;
        this.maNV  = maNV;
        this.role  = role;
        init();
    }

    // Constructor chạy thử
    public MainFrame() {
        this("Quản Trị Viên", "NV001", "admin");
    }

    private void init() {
        setTitle("FashionPro — Quản lý");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());
        setBackground(BG);

        add(buildSidebar(), BorderLayout.WEST);
        add(buildContent(), BorderLayout.CENTER);

        setVisible(true);
        showPanel("sanpham");

        // Khởi tạo Controller SAU setVisible()
        // để SwingUtilities.getWindowAncestor() không trả về null
        SwingUtilities.invokeLater(this::initControllers);
    }

    /**
     * Khởi tạo tất cả Controller — truyền Panel + maNV vào
     * Phải gọi SAU setVisible() để Panel đã có parent window
     */
    private void initControllers() {
        new HoaDonController(hdview, "Nv001");
        // new SanPhamController(sanPhamPanel, maNV);    // thêm khi có
        // new PhieuNhapController(phieuNhapPanel, maNV);// thêm khi có
    }

    // ── Sidebar ──────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(SIDEBAR);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER));

        sidebar.add(buildLogo(),   BorderLayout.NORTH);
        sidebar.add(buildMenu(),   BorderLayout.CENTER);
        sidebar.add(buildUserBox(),BorderLayout.SOUTH);
        return sidebar;
    }

    private JPanel buildLogo() {
        JPanel logo = new JPanel(new BorderLayout());
        logo.setBackground(SIDEBAR);
        logo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));
        JLabel logoText = new JLabel("FashionPro");
        logoText.setFont(new Font("Dialog", Font.BOLD, 16));
        logoText.setForeground(TEXT1);
        JLabel logoSub = new JLabel("Quản lý bán hàng");
        logoSub.setFont(new Font("Dialog", Font.PLAIN, 11));
        logoSub.setForeground(TEXT2);
        logo.add(logoText, BorderLayout.NORTH);
        logo.add(logoSub,  BorderLayout.SOUTH);
        return logo;
    }

    private JPanel buildMenu() {
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBackground(SIDEBAR);
        menu.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        addMenuLabel(menu, "QUẢN LÝ");
        addMenuItem(menu, "Sản phẩm",   "sanpham",   true);
        addMenuItem(menu, "Hoá đơn",    "hoadon",    false);
        addMenuItem(menu, "Khách hàng", "khachhang", false);
        addMenuItem(menu, "Phiếu nhập", "phieunhap", false);
        addMenuItem(menu, "Khuyến mãi", "khuyenmai", false);

        addMenuLabel(menu, "HỆ THỐNG");
        addMenuItem(menu, "Nhân viên",  "nhanvien",  false);
        addMenuItem(menu, "Báo cáo",    "baocao",    false);

        // Nút đăng xuất dưới cùng menu
        menu.add(Box.createVerticalGlue());
        addMenuDangXuat(menu);

        return menu;
    }

    private JPanel buildUserBox() {
        JPanel userBox = new JPanel(new BorderLayout(10, 0));
        userBox.setBackground(new Color(9, 13, 26));
        userBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        // Avatar chữ cái đầu
        JLabel avatar = new JLabel(hoTen.substring(0, 1).toUpperCase());
        avatar.setFont(new Font("Dialog", Font.BOLD, 14));
        avatar.setForeground(Color.WHITE);
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(34, 34));
        avatar.setBackground(ACCENT);
        avatar.setOpaque(true);

        JLabel nameLabel = new JLabel(hoTen);
        nameLabel.setFont(new Font("Dialog", Font.BOLD, 12));
        nameLabel.setForeground(TEXT1);

        JLabel roleLabel = new JLabel(role);
        roleLabel.setFont(new Font("Dialog", Font.PLAIN, 11));
        roleLabel.setForeground(TEXT2);

        JPanel namePanel = new JPanel(new GridLayout(2, 1));
        namePanel.setOpaque(false);
        namePanel.add(nameLabel);
        namePanel.add(roleLabel);

        userBox.add(avatar,    BorderLayout.WEST);
        userBox.add(namePanel, BorderLayout.CENTER);
        return userBox;
    }

    // ── Content ──────────────────────────────────────────
    private JPanel buildContent() {
        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG);

        // Khởi tạo Panel và lưu reference để Controller dùng
//        sanPhamPanel   = new SanPhamPanel();
        hdview    = new HoaDonView();
//        phieuNhapPanel = new PhieuNhapPanel();
//        baoCaoPanel    = new BaoCaoPanel();

//        contentPanel.add(sanPhamPanel,              "sanpham");
        contentPanel.add(hdview,               "hoadon");
//        contentPanel.add(buildPlaceholder("Khách hàng"), "khachhang");
//        contentPanel.add(phieuNhapPanel,            "phieunhap");
//        contentPanel.add(buildPlaceholder("Khuyến mãi"), "khuyenmai");
//        contentPanel.add(buildPlaceholder("Nhân viên"),  "nhanvien");
//        contentPanel.add(baoCaoPanel,               "baocao");

        return contentPanel;
    }

    private void showPanel(String name) {
        cardLayout.show(contentPanel, name);
    }

    // ── Menu Helpers ─────────────────────────────────────
    private void addMenuLabel(JPanel menu, String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Dialog", Font.BOLD, 10));
        l.setForeground(new Color(51, 65, 85));
        l.setBorder(BorderFactory.createEmptyBorder(14, 20, 6, 20));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        menu.add(l);
    }

    private void addMenuItem(JPanel menu, String label, String panelName, boolean isActive) {
        JPanel item = new JPanel(new BorderLayout());
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        item.setBackground(isActive ? ACTIVE : SIDEBAR);
        item.setAlignmentX(Component.LEFT_ALIGNMENT);
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Thanh accent trái khi active
        JPanel accentBar = new JPanel();
        accentBar.setPreferredSize(new Dimension(3, 0));
        accentBar.setBackground(isActive ? ACCENT : SIDEBAR);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Dialog", Font.BOLD, 13));
        lbl.setForeground(isActive ? TEXT1 : TEXT2);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));

        item.add(accentBar, BorderLayout.WEST);
        item.add(lbl,       BorderLayout.CENTER);

        // Gắn listener vào cả item lẫn lbl
        // vì JLabel chặn MouseEvent, không bubble lên JPanel cha
        MouseAdapter menuListener = new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                item.setBackground(HOVER);
                lbl.setForeground(TEXT1);
            }
            public void mouseExited(MouseEvent e) {
                item.setBackground(SIDEBAR);
                lbl.setForeground(TEXT2);
                accentBar.setBackground(SIDEBAR);
            }
            public void mouseClicked(MouseEvent e) {
                resetMenuItems(menu);
                accentBar.setBackground(ACCENT);
                item.setBackground(ACTIVE);
                lbl.setForeground(TEXT1);
                showPanel(panelName);
            }
        };

        item.addMouseListener(menuListener);
        lbl.addMouseListener(menuListener);  // ← quan trọng

        menu.add(item);
    }

    private void addMenuDangXuat(JPanel menu) {
        JPanel item = new JPanel(new BorderLayout());
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        item.setBackground(SIDEBAR);
        item.setAlignmentX(Component.LEFT_ALIGNMENT);
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        item.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));

        JLabel lbl = new JLabel("Đăng xuất");
        lbl.setFont(new Font("Dialog", Font.BOLD, 13));
        lbl.setForeground(new Color(239, 68, 68));
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        item.add(lbl, BorderLayout.CENTER);

        item.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int ok = JOptionPane.showConfirmDialog(
                        MainFrame.this,
                        "Bạn có muốn đăng xuất?",
                        "Xác nhận", JOptionPane.YES_NO_OPTION
                );
                if (ok == JOptionPane.YES_OPTION) {
                    dispose();
                    new LoginView(); // quay về màn hình đăng nhập
                }
            }
            public void mouseEntered(MouseEvent e) { item.setBackground(HOVER); }
            public void mouseExited(MouseEvent e)  { item.setBackground(SIDEBAR); }
        });
        menu.add(item);
    }

    // Reset tất cả menu item về trạng thái mặc định
    private void resetMenuItems(JPanel menu) {
        for (Component c : menu.getComponents()) {
            if (c instanceof JPanel) {
                c.setBackground(SIDEBAR);
                for (Component child : ((JPanel) c).getComponents()) {
                    if (child instanceof JLabel) {
                        ((JLabel) child).setForeground(TEXT2);
                    } else if (child instanceof JPanel) {
                        child.setBackground(SIDEBAR); // accent bar
                    }
                }
            }
        }
    }

    private JPanel buildPlaceholder(String name) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(BG);
        JLabel l = new JLabel(name + " — Đang phát triển...");
        l.setFont(new Font("Dialog", Font.BOLD, 18));
        l.setForeground(TEXT2);
        p.add(l);
        return p;
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(MainFrame::new);
    }
}