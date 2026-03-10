package gui.view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.text.NumberFormat;
import java.util.*;

/**
 * BaoCaoPanel.java — Báo cáo & Thống kê
 * 3 mục:
 *   1. Doanh thu theo ngày/tháng/năm (Bar chart)
 *   2. Top sản phẩm bán chạy         (Bar chart ngang)
 *   3. Tồn kho sản phẩm              (Bar chart + màu cảnh báo)
 *
 * Tất cả biểu đồ vẽ thủ công bằng Graphics2D — không cần thư viện ngoài.
 * Kết nối BUS: thay các chỗ comment "TODO: bus..."
 */
public class BaoCaoView extends JFrame {

    // ── Màu ─────────────────────────────────────────────
    private static final Color BG      = new Color(7,  10, 20);
    private static final Color SURFACE = new Color(11, 15, 30);
    private static final Color CARD    = new Color(14, 20, 40);
    private static final Color CARD2   = new Color(18, 26, 52);
    private static final Color BORDER  = new Color(30, 42, 72);
    private static final Color ACCENT  = new Color(99, 102, 241);
    private static final Color GREEN   = new Color(16, 185, 129);
    private static final Color YELLOW  = new Color(245, 158, 11);
    private static final Color RED     = new Color(239, 68, 68);
    private static final Color CYAN    = new Color(6, 182, 212);
    private static final Color TEXT1   = new Color(226, 232, 240);
    private static final Color TEXT2   = new Color(100, 116, 139);
    private static final Color TEXT3   = new Color(30, 42, 72);
    private static final Color GRID    = new Color(20, 28, 55);

    // ── Dữ liệu mẫu ─────────────────────────────────────
    // TODO: thay bằng bus.getDoanhThuTheoThang(), bus.getTopSanPham(), bus.getTonKho()

    // Doanh thu 12 tháng (triệu đồng)
    private static final double[] DOANH_THU_THANG = {
            12.5, 18.2, 15.8, 22.1, 19.4, 28.6,
            24.3, 31.7, 27.9, 35.2, 29.8, 42.0
    };
    private static final String[] THANG_LABEL = {
            "T1","T2","T3","T4","T5","T6","T7","T8","T9","T10","T11","T12"
    };

    // Top 6 sản phẩm bán chạy
    private static final String[] TOP_SP_TEN = {
            "Áo thun basic","Quần jean slim","Váy hoa mùa hè",
            "Áo sơ mi kẻ","Áo khoác bomber","Áo hoodie"
    };
    private static final int[] TOP_SP_SL = { 145, 98, 87, 76, 65, 54 };

    // Tồn kho (nhóm theo loại)
    private static final String[] TON_KHO_LOAI = {
            "Áo thun","Áo sơ mi","Quần","Váy","Áo khoác","Phụ kiện"
    };
    private static final int[] TON_KHO_SO = { 83, 45, 33, 25, 20, 60 };

    // ── Components ───────────────────────────────────────
    private JComboBox<String> cbNam, cbLoaiThoiGian;
    private JLabel lblTongDT, lblTongHD, lblTBNgay;

    // Tab đang chọn
    private int activeTab = 0;
    private JPanel[] tabBtns = new JPanel[3];
    private JPanel chartArea;

    public BaoCaoView() {
        setBackground(BG);
        setLayout(new BorderLayout(0, 0));
        add(buildHeader(),  BorderLayout.NORTH);
        add(buildBody(),    BorderLayout.CENTER);
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

        JLabel title = new JLabel("Báo cáo & Thống kê");
        title.setFont(new Font("Dialog", Font.BOLD, 20));
        title.setForeground(TEXT1);

        // Chip tổng quan
        lblTongDT  = makeChip("Doanh thu: --",  ACCENT);
        lblTongHD  = makeChip("Hoá đơn: --",    GREEN);
        lblTBNgay  = makeChip("TB/ngày: --",    CYAN);
        updateSummaryChips();

        JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        chips.setOpaque(false);
        chips.add(lblTongDT);
        chips.add(lblTongHD);
        chips.add(lblTBNgay);

        JPanel left = new JPanel(new BorderLayout(0, 6));
        left.setOpaque(false);
        left.add(title, BorderLayout.NORTH);
        left.add(chips, BorderLayout.CENTER);

        // Bộ lọc thời gian
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filters.setOpaque(false);
        cbLoaiThoiGian = new JComboBox<>(new String[]{"Theo tháng","Theo quý","Theo năm"});
        cbNam = new JComboBox<>(new String[]{"2025","2024","2023"});
        styleCombo(cbLoaiThoiGian);
        styleCombo(cbNam);
        cbLoaiThoiGian.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) chartArea.repaint();
        });
        cbNam.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) chartArea.repaint();
        });
        JButton btnLoc = makeButton("Cập nhật", ACCENT, Color.WHITE);
        btnLoc.addActionListener(e -> { updateSummaryChips(); chartArea.repaint(); });
        filters.add(new JLabel("Năm:") {{ setForeground(TEXT2); setFont(new Font("Dialog",Font.BOLD,12)); }});
        filters.add(cbNam);
        filters.add(cbLoaiThoiGian);
        filters.add(btnLoc);

        h.add(left,    BorderLayout.WEST);
        h.add(filters, BorderLayout.EAST);
        return h;
    }

    // ── Body: tabs + chart ───────────────────────────────
    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(0, 0));
        body.setBackground(BG);

        // Tab bar
        body.add(buildTabBar(), BorderLayout.NORTH);

        // Vùng chart — vẽ lại mỗi khi đổi tab
        chartArea = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                if (activeTab == 0) drawDoanhThu(g2, getWidth(), getHeight());
                else if (activeTab == 1) drawTopSanPham(g2, getWidth(), getHeight());
                else                     drawTonKho(g2, getWidth(), getHeight());
                g2.dispose();
            }
        };
        chartArea.setBackground(BG);
        body.add(chartArea, BorderLayout.CENTER);
        return body;
    }

    private JPanel buildTabBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bar.setBackground(SURFACE);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));

        String[] tabs = {"📈 Doanh thu", "🏆 Bán chạy", "📦 Tồn kho"};
        for (int i = 0; i < tabs.length; i++) {
            final int idx = i;
            JPanel tab = new JPanel(new BorderLayout());
            tab.setBackground(SURFACE);
            tab.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            tab.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

            JLabel lbl = new JLabel(tabs[i]);
            lbl.setFont(new Font("Dialog", Font.BOLD, 13));
            lbl.setForeground(i == 0 ? TEXT1 : TEXT2);
            lbl.setBorder(BorderFactory.createEmptyBorder(12, 22, 12, 22));

            // Đường gạch dưới khi active
            JPanel underline = new JPanel();
            underline.setPreferredSize(new Dimension(0, 2));
            underline.setBackground(i == 0 ? ACCENT : SURFACE);

            tab.add(lbl,      BorderLayout.CENTER);
            tab.add(underline,BorderLayout.SOUTH);
            tabBtns[i] = tab;

            tab.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    // Reset tất cả tabs
                    for (int j = 0; j < tabBtns.length; j++) {
                        ((JLabel) tabBtns[j].getComponent(0)).setForeground(TEXT2);
                        tabBtns[j].getComponent(1).setBackground(SURFACE);
                    }
                    // Active tab được chọn
                    lbl.setForeground(TEXT1);
                    underline.setBackground(ACCENT);
                    activeTab = idx;
                    chartArea.repaint();
                }
                public void mouseEntered(MouseEvent e) {
                    if (activeTab != idx) lbl.setForeground(TEXT1);
                }
                public void mouseExited(MouseEvent e) {
                    if (activeTab != idx) lbl.setForeground(TEXT2);
                }
            });
            bar.add(tab);
        }
        return bar;
    }

    // ════════════════════════════════════════════════════
    // CHART 1: Doanh thu theo tháng — Bar chart dọc
    // ════════════════════════════════════════════════════
    private void drawDoanhThu(Graphics2D g2, int W, int H) {
        int padL = 80, padR = 40, padT = 60, padB = 60;
        int chartW = W - padL - padR;
        int chartH = H - padT - padB;

        // Tiêu đề
        drawChartTitle(g2, "Doanh thu theo tháng — " + cbNam.getSelectedItem(), W, padT);

        // Vẽ grid ngang
        double maxVal = Arrays.stream(DOANH_THU_THANG).max().getAsDouble() * 1.2;
        int gridLines = 5;
        for (int i = 0; i <= gridLines; i++) {
            int y = padT + chartH - (int)(chartH * i / gridLines);
            g2.setColor(GRID);
            g2.setStroke(new BasicStroke(1f));
            g2.drawLine(padL, y, padL + chartW, y);
            // Label trục Y
            double val = maxVal * i / gridLines;
            g2.setColor(TEXT2);
            g2.setFont(new Font("Dialog", Font.PLAIN, 10));
            String label = String.format("%.0fM", val);
            g2.drawString(label, padL - 38, y + 4);
        }

        // Vẽ cột
        int n = DOANH_THU_THANG.length;
        int barW = (int)(chartW * 0.55 / n);
        int gap  = chartW / n;

        for (int i = 0; i < n; i++) {
            int barH    = (int)(chartH * DOANH_THU_THANG[i] / maxVal);
            int x       = padL + i * gap + (gap - barW) / 2;
            int y       = padT + chartH - barH;

            // Gradient cột
            GradientPaint gp = new GradientPaint(
                    x, y, new Color(120, 130, 255),
                    x, y + barH, ACCENT
            );
            g2.setPaint(gp);
            g2.fill(new RoundRectangle2D.Float(x, y, barW, barH, 6, 6));

            // Giá trị trên đầu cột
            g2.setColor(TEXT1);
            g2.setFont(new Font("Dialog", Font.BOLD, 10));
            String val = String.format("%.1f", DOANH_THU_THANG[i]);
            int valW = g2.getFontMetrics().stringWidth(val);
            g2.drawString(val, x + (barW - valW) / 2, y - 6);

            // Label tháng dưới cột
            g2.setColor(TEXT2);
            g2.setFont(new Font("Dialog", Font.BOLD, 11));
            int lblW = g2.getFontMetrics().stringWidth(THANG_LABEL[i]);
            g2.drawString(THANG_LABEL[i], x + (barW - lblW) / 2, padT + chartH + 20);
        }

        // Trục X
        g2.setColor(BORDER);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(padL, padT + chartH, padL + chartW, padT + chartH);
        // Trục Y
        g2.drawLine(padL, padT, padL, padT + chartH);

        // Ghi chú đơn vị
        g2.setColor(TEXT2);
        g2.setFont(new Font("Dialog", Font.ITALIC, 10));
        g2.drawString("Đơn vị: triệu đồng", padL, padT - 10);
    }

    // ════════════════════════════════════════════════════
    // CHART 2: Top sản phẩm bán chạy — Bar chart ngang
    // ════════════════════════════════════════════════════
    private void drawTopSanPham(Graphics2D g2, int W, int H) {
        int padL = 160, padR = 80, padT = 60, padB = 40;
        int chartW = W - padL - padR;
        int chartH = H - padT - padB;

        drawChartTitle(g2, "Top sản phẩm bán chạy", W, padT);

        int n    = TOP_SP_TEN.length;
        int maxV = Arrays.stream(TOP_SP_SL).max().getAsInt();
        int barH = (int)(chartH * 0.55 / n);
        int gap  = chartH / n;

        // Grid dọc
        for (int i = 0; i <= 5; i++) {
            int x = padL + (int)(chartW * i / 5.0);
            g2.setColor(GRID);
            g2.drawLine(x, padT, x, padT + chartH);
            g2.setColor(TEXT2);
            g2.setFont(new Font("Dialog", Font.PLAIN, 10));
            String lbl = String.valueOf((int)(maxV * i / 5.0));
            g2.drawString(lbl, x - g2.getFontMetrics().stringWidth(lbl)/2, padT + chartH + 16);
        }

        // Màu gradient cho từng cột
        Color[] barColors = { ACCENT, GREEN, CYAN, YELLOW, new Color(236,72,153), new Color(168,85,247) };

        for (int i = 0; i < n; i++) {
            int barW = (int)(chartW * TOP_SP_SL[i] / (double) maxV);
            int y    = padT + i * gap + (gap - barH) / 2;

            // Tên sản phẩm
            g2.setColor(TEXT1);
            g2.setFont(new Font("Dialog", Font.BOLD, 12));
            String ten = TOP_SP_TEN[i];
            g2.drawString(ten, padL - g2.getFontMetrics().stringWidth(ten) - 12, y + barH/2 + 4);

            // Cột ngang
            GradientPaint gp = new GradientPaint(
                    padL, y, barColors[i % barColors.length].brighter(),
                    padL + barW, y, barColors[i % barColors.length]
            );
            g2.setPaint(gp);
            g2.fill(new RoundRectangle2D.Float(padL, y, barW, barH, 6, 6));

            // Số lượng cuối cột
            g2.setColor(TEXT1);
            g2.setFont(new Font("Dialog", Font.BOLD, 11));
            g2.drawString(TOP_SP_SL[i] + " cái", padL + barW + 8, y + barH/2 + 4);
        }

        // Trục Y
        g2.setColor(BORDER);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(padL, padT, padL, padT + chartH);
        g2.drawLine(padL, padT + chartH, padL + chartW, padT + chartH);

        g2.setColor(TEXT2);
        g2.setFont(new Font("Dialog", Font.ITALIC, 10));
        g2.drawString("Đơn vị: số lượng đã bán", padL, padT - 10);
    }

    // ════════════════════════════════════════════════════
    // CHART 3: Tồn kho — Bar chart dọc với màu cảnh báo
    // ════════════════════════════════════════════════════
    private void drawTonKho(Graphics2D g2, int W, int H) {
        int padL = 80, padR = 40, padT = 60, padB = 80;
        int chartW = W - padL - padR;
        int chartH = H - padT - padB;

        drawChartTitle(g2, "Tồn kho theo loại sản phẩm", W, padT);

        int maxVal = Arrays.stream(TON_KHO_SO).max().getAsInt();
        int n      = TON_KHO_LOAI.length;

        // Grid ngang
        for (int i = 0; i <= 5; i++) {
            int y = padT + chartH - (int)(chartH * i / 5.0);
            g2.setColor(GRID);
            g2.drawLine(padL, y, padL + chartW, y);
            g2.setColor(TEXT2);
            g2.setFont(new Font("Dialog", Font.PLAIN, 10));
            String lbl = String.valueOf((int)(maxVal * i / 5.0));
            g2.drawString(lbl, padL - g2.getFontMetrics().stringWidth(lbl) - 8, y + 4);
        }

        // Ngưỡng cảnh báo (đường kẻ đỏ nét đứt)
        int nguongCanBao = 30;
        int yNguong = padT + chartH - (int)(chartH * nguongCanBao / (double) maxVal);
        g2.setColor(new Color(RED.getRed(), RED.getGreen(), RED.getBlue(), 150));
        g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                10f, new float[]{6f}, 0f));
        g2.drawLine(padL, yNguong, padL + chartW, yNguong);
        g2.setFont(new Font("Dialog", Font.BOLD, 10));
        g2.drawString("Ngưỡng cảnh báo (" + nguongCanBao + ")", padL + chartW - 160, yNguong - 5);
        g2.setStroke(new BasicStroke(1f));

        int barW = (int)(chartW * 0.55 / n);
        int gap  = chartW / n;

        for (int i = 0; i < n; i++) {
            int barH = (int)(chartH * TON_KHO_SO[i] / (double) maxVal);
            int x    = padL + i * gap + (gap - barW) / 2;
            int y    = padT + chartH - barH;

            // Màu theo mức tồn kho
            Color col = TON_KHO_SO[i] <= 20 ? RED
                    : TON_KHO_SO[i] <= 30 ? YELLOW
                    : GREEN;

            GradientPaint gp = new GradientPaint(
                    x, y, col.brighter(),
                    x, y + barH, col.darker()
            );
            g2.setPaint(gp);
            g2.fill(new RoundRectangle2D.Float(x, y, barW, barH, 6, 6));

            // Số lượng trên đầu cột
            g2.setColor(TEXT1);
            g2.setFont(new Font("Dialog", Font.BOLD, 11));
            String val = String.valueOf(TON_KHO_SO[i]);
            int vW = g2.getFontMetrics().stringWidth(val);
            g2.drawString(val, x + (barW - vW) / 2, y - 6);

            // Tên loại dưới cột — xuống 2 dòng nếu dài
            g2.setColor(TEXT2);
            g2.setFont(new Font("Dialog", Font.BOLD, 11));
            String ten = TON_KHO_LOAI[i];
            int tW = g2.getFontMetrics().stringWidth(ten);
            g2.drawString(ten, x + (barW - tW) / 2, padT + chartH + 20);
        }

        // Trục
        g2.setColor(BORDER);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(padL, padT, padL, padT + chartH);
        g2.drawLine(padL, padT + chartH, padL + chartW, padT + chartH);

        // Chú thích màu
        drawLegend(g2, W, padT + chartH + 50,
                new String[]{"Đủ hàng (>30)", "Sắp hết (21–30)", "Cần nhập (≤20)"},
                new Color[]{GREEN, YELLOW, RED}
        );
    }

    // ── Helpers vẽ ───────────────────────────────────────
    private void drawChartTitle(Graphics2D g2, String title, int W, int padT) {
        g2.setColor(TEXT1);
        g2.setFont(new Font("Dialog", Font.BOLD, 15));
        int tW = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, (W - tW) / 2, padT - 20);
    }

    private void drawLegend(Graphics2D g2, int W, int y, String[] labels, Color[] colors) {
        int totalW = 0;
        g2.setFont(new Font("Dialog", Font.BOLD, 11));
        for (String l : labels) totalW += g2.getFontMetrics().stringWidth(l) + 36;
        int x = (W - totalW) / 2;
        for (int i = 0; i < labels.length; i++) {
            g2.setColor(colors[i]);
            g2.fillRoundRect(x, y - 10, 14, 14, 4, 4);
            g2.setColor(TEXT2);
            g2.drawString(labels[i], x + 20, y + 2);
            x += g2.getFontMetrics().stringWidth(labels[i]) + 36;
        }
    }

    private void updateSummaryChips() {
        double tongDT = Arrays.stream(DOANH_THU_THANG).sum();
        lblTongDT.setText(String.format("Doanh thu: %.0fM đ", tongDT));
        lblTongHD.setText("Hoá đơn: 248");
        lblTBNgay.setText(String.format("TB/ngày: %.1fM đ", tongDT / 365));
    }

    // ── UI Helpers ───────────────────────────────────────
    private JLabel makeChip(String text, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Dialog", Font.BOLD, 11));
        l.setForeground(color);
        l.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
        l.setOpaque(true);
        l.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(color.getRed(), color.getGreen(), color.getBlue(), 60), 1, true),
                BorderFactory.createEmptyBorder(3, 10, 3, 10)
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
        b.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        return b;
    }

    private void styleCombo(JComboBox<String> cb) {
        cb.setBackground(CARD);
        cb.setForeground(TEXT2);
        cb.setFont(new Font("Dialog", Font.BOLD, 12));
    }

    // ── Main chạy thử ────────────────────────────────────
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test — Báo cáo & Thống kê");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setContentPane(new BaoCaoView());
            frame.setVisible(true);
        });
    }
}
