package gui.view;

import gui.controller.NhanVienController;
import gui.dialog.NhanVienFormDialog;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class NhanVienPanel extends JPanel implements NhanVienController.IView {

    private static final Color BG      = new Color(7,   10,  20);
    private static final Color SURFACE = new Color(11,  15,  30);
    private static final Color CARD    = new Color(14,  20,  40);
    private static final Color BORDER  = new Color(30,  42,  72);
    private static final Color ACCENT  = new Color(99,  102, 241);
    private static final Color GREEN   = new Color(16,  185, 129);
    private static final Color RED     = new Color(239, 68,  68);
    private static final Color CYAN    = new Color(6,   182, 212);
    private static final Color TEXT1   = new Color(226, 232, 240);
    private static final Color TEXT2   = new Color(160, 174, 192);
    private static final Color ROW_ODD = new Color(10,  14,  28);
    private static final Color ROW_SEL = new Color(25,  35,  80);
    private static final Color COMBO_BG= new Color(14,  20,  40);

    // Su dung Unicode escape de dam bao hien thi dung tren moi OS/encoding
    static final String[] CHUC_VU_LIST    = {
            "T\u1ea5t c\u1ea3 ch\u1ee9c v\u1ee5",
            "Qu\u1ea3n l\u00fd",
            "Nh\u00e2n vi\u00ean",
            "Thu ng\u00e2n"
    };
    static final String[] TRANG_THAI_LIST = {
            "T\u1ea5t c\u1ea3 tr\u1ea1ng th\u00e1i",
            "Ho\u1ea1t \u0111\u1ed9ng",
            "Ng\u1eebng ho\u1ea1t \u0111\u1ed9ng"
    };

    private final NhanVienController controller;
    private JTable            table;
    private DefaultTableModel tableModel;
    private JTextField        searchField;
    private FakeCombo         cbChucVu, cbTrangThai;
    private JLabel            lblTongNV, lblHoatDong, lblNghiViec;

    private static final String[] COLUMNS = {
            "ID",
            "M\u00e3 NV",
            "H\u1ecd t\u00ean",
            "S\u1ed1 \u0111i\u1ec7n tho\u1ea1i",
            "Ch\u1ee9c v\u1ee5",
            "T\u00ean \u0111\u0103ng nh\u1eadp",
            "Tr\u1ea1ng th\u00e1i",
            "Thao t\u00e1c"
    };

    public NhanVienPanel() {
        setBackground(BG);
        setLayout(new BorderLayout(0, 0));
        this.controller = new NhanVienController(this);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(buildHeader(),  BorderLayout.NORTH);
        top.add(buildToolbar(), BorderLayout.SOUTH);

        add(top,          BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        controller.loadDanhSach();
    }

    @Override public DefaultTableModel getTableModel()  { return tableModel; }
    @Override public Frame  getParentFrame()            { return (Frame) SwingUtilities.getWindowAncestor(this); }
    @Override public Component getComponent()           { return this; }
    @Override public String getKeyword()                { return searchField.getText(); }
    @Override public String getChucVuFilter()           { return cbChucVu.getSelected(); }
    @Override public String getTrangThaiFilter()        { return cbTrangThai.getSelected(); }

    @Override
    public void updateStats(int total, int hoatDong, int nghiViec) {
        lblTongNV.setText("T\u1ed5ng: "           + total);
        lblHoatDong.setText("Ho\u1ea1t \u0111\u1ed9ng: " + hoatDong);
        lblNghiViec.setText("Ng\u1eebng H\u0110: " + nghiViec);
    }

    // ── HEADER ───────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(SURFACE);
        h.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0,0,1,0,BORDER),
                BorderFactory.createEmptyBorder(14,22,14,22)));

        JLabel title = new JLabel("Qu\u1ea3n l\u00fd Nh\u00e2n vi\u00ean");
        title.setFont(new Font("Dialog", Font.BOLD, 20));
        title.setForeground(TEXT1);

        lblTongNV   = chip("T\u1ed5ng: 0",             ACCENT);
        lblHoatDong = chip("Ho\u1ea1t \u0111\u1ed9ng: 0", GREEN);
        lblNghiViec = chip("Ng\u1eebng H\u0110: 0",    RED);

        JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        chips.setOpaque(false);
        chips.add(lblTongNV); chips.add(lblHoatDong); chips.add(lblNghiViec);

        JPanel left = new JPanel(new BorderLayout(0, 6));
        left.setOpaque(false);
        left.add(title, BorderLayout.NORTH);
        left.add(chips, BorderLayout.CENTER);

        FlatBtn btnThem = new FlatBtn(
                "+ Th\u00eam nh\u00e2n vi\u00ean", ACCENT, Color.WHITE, 170, 36);
        btnThem.addClickListener(() -> controller.them());

        h.add(left,    BorderLayout.WEST);
        h.add(btnThem, BorderLayout.EAST);
        return h;
    }

    // ── TOOLBAR ──────────────────────────────────────────────
    private JPanel buildToolbar() {
        JPanel bar = new JPanel();
        bar.setLayout(new BoxLayout(bar, BoxLayout.X_AXIS));
        bar.setBackground(BG);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0,0,1,0,BORDER),
                BorderFactory.createEmptyBorder(8,16,8,16)));

        JLabel lblSearch = new JLabel("T\u00ecm ki\u1ebfm:");
        lblSearch.setForeground(TEXT2);
        lblSearch.setFont(new Font("Dialog", Font.BOLD, 13));
        lblSearch.setAlignmentY(Component.CENTER_ALIGNMENT);

        searchField = new JTextField(16);
        searchField.setBackground(CARD);
        searchField.setForeground(TEXT1);
        searchField.setCaretColor(ACCENT);
        searchField.setFont(new Font("Dialog", Font.PLAIN, 13));
        searchField.setMaximumSize(new Dimension(220, 34));
        searchField.setPreferredSize(new Dimension(220, 34));
        searchField.setAlignmentY(Component.CENTER_ALIGNMENT);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { controller.loc(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { controller.loc(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { controller.loc(); }
        });

        Dimension comboDim = new Dimension(170, 34);
        cbChucVu    = new FakeCombo(CHUC_VU_LIST,    COMBO_BG, TEXT1);
        cbTrangThai = new FakeCombo(TRANG_THAI_LIST, COMBO_BG, TEXT1);
        cbChucVu.setPreferredSize(comboDim);    cbChucVu.setMaximumSize(comboDim);
        cbTrangThai.setPreferredSize(comboDim); cbTrangThai.setMaximumSize(comboDim);
        cbChucVu.setAlignmentY(Component.CENTER_ALIGNMENT);
        cbTrangThai.setAlignmentY(Component.CENTER_ALIGNMENT);
        cbChucVu.setOnSelect(controller::loc);
        cbTrangThai.setOnSelect(controller::loc);

        FlatBtn btnReset = new FlatBtn(
                "L\u00e0m m\u1edbi", new Color(38,50,90), TEXT2, 100, 34);
        btnReset.setBorderColor(new Color(70,90,150));
        btnReset.setAlignmentY(Component.CENTER_ALIGNMENT);
        btnReset.addClickListener(() -> {
            searchField.setText("");
            cbChucVu.setSelectedIndex(0);
            cbTrangThai.setSelectedIndex(0);
            controller.loadDanhSach();
        });

        bar.add(lblSearch);
        bar.add(Box.createHorizontalStrut(8));
        bar.add(searchField);
        bar.add(Box.createHorizontalStrut(10));
        bar.add(cbChucVu);
        bar.add(Box.createHorizontalStrut(8));
        bar.add(cbTrangThai);
        bar.add(Box.createHorizontalStrut(10));
        bar.add(btnReset);
        bar.add(Box.createHorizontalGlue());
        return bar;
    }

    // ── TABLE ────────────────────────────────────────────────
    private JScrollPane buildTable() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                c.setBackground(isRowSelected(row) ? ROW_SEL : row % 2 == 0 ? SURFACE : ROW_ODD);
                c.setForeground(TEXT1);
                return c;
            }
        };
        table.setBackground(SURFACE); table.setForeground(TEXT1);
        table.setGridColor(BORDER);   table.setRowHeight(40);
        table.setFont(new Font("Dialog", Font.PLAIN, 13));
        table.setSelectionBackground(ROW_SEL);
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setBackground(CARD); header.setForeground(TEXT2);
        header.setFont(new Font("Dialog", Font.BOLD, 12));
        header.setBorder(BorderFactory.createMatteBorder(0,0,1,0,BORDER));

        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        int[] widths = {0, 80, 190, 130, 130, 160, 130, 120};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // col1 Ma NV
        table.getColumnModel().getColumn(1).setCellRenderer((t,val,sel,foc,row,col) -> {
            JLabel l = new JLabel("  " + val); l.setOpaque(true);
            l.setFont(new Font("Dialog",Font.BOLD,13));
            l.setBackground(sel?ROW_SEL:row%2==0?SURFACE:ROW_ODD); l.setForeground(ACCENT);
            return l;
        });
        // col4 Chuc vu
        table.getColumnModel().getColumn(4).setCellRenderer((t,val,sel,foc,row,col) -> {
            String s = String.valueOf(val);
            JLabel l = new JLabel("  " + s); l.setOpaque(true);
            l.setFont(new Font("Dialog",Font.BOLD,12));
            l.setBackground(sel?ROW_SEL:row%2==0?SURFACE:ROW_ODD);
            l.setForeground(chucVuColor(s)); return l;
        });
        // col6 Trang thai
        table.getColumnModel().getColumn(6).setCellRenderer((t,val,sel,foc,row,col) -> {
            String s = String.valueOf(val);
            JLabel l = new JLabel("  " + s); l.setOpaque(true);
            l.setFont(new Font("Dialog",Font.BOLD,12));
            l.setBackground(sel?ROW_SEL:row%2==0?SURFACE:ROW_ODD);
            l.setForeground("Ho\u1ea1t \u0111\u1ed9ng".equals(s) ? GREEN : RED);
            return l;
        });
        // col7 Thao tac
        table.getColumnModel().getColumn(7).setCellRenderer((t,val,sel,foc,row,col) -> {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER,4,8));
            p.setBackground(sel?ROW_SEL:row%2==0?SURFACE:ROW_ODD);
            p.add(tag("S\u1eeda", ACCENT));
            p.add(tag("X\u00f3a", RED));
            return p;
        });

        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row < 0) return;
                String maNV = (String) tableModel.getValueAt(table.convertRowIndexToModel(row), 0);
                if (col == 7) {
                    Rectangle rect = table.getCellRect(row, col, true);
                    if (e.getX() - rect.x < rect.width/2) controller.sua(maNV);
                    else                                   controller.xoa(maNV);
                } else if (e.getClickCount() == 2) controller.sua(maNV);
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(SURFACE);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        return scroll;
    }

    // ── HELPERS ──────────────────────────────────────────────
    private Color chucVuColor(String cv) {
        if ("Qu\u1ea3n l\u00fd".equals(cv))   return ACCENT;
        if ("Nh\u00e2n vi\u00ean".equals(cv)) return CYAN;
        if ("Thu ng\u00e2n".equals(cv))       return GREEN;
        return TEXT2;
    }

    private JLabel chip(String text, Color color) {
        JLabel l = new JLabel(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(color.getRed(),color.getGreen(),color.getBlue(),35));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                g2.setColor(new Color(color.getRed(),color.getGreen(),color.getBlue(),100));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,8,8);
                g2.dispose(); super.paintComponent(g);
            }
        };
        l.setFont(new Font("Dialog",Font.BOLD,12)); l.setForeground(color);
        l.setOpaque(false); l.setBorder(BorderFactory.createEmptyBorder(4,10,4,10));
        return l;
    }

    private JLabel tag(String text, Color color) {
        JLabel l = new JLabel(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(color.getRed(),color.getGreen(),color.getBlue(),30));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),6,6);
                g2.setColor(new Color(color.getRed(),color.getGreen(),color.getBlue(),90));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,6,6);
                g2.dispose(); super.paintComponent(g);
            }
        };
        l.setFont(new Font("Dialog",Font.BOLD,11)); l.setForeground(color);
        l.setOpaque(false); l.setBorder(BorderFactory.createEmptyBorder(3,8,3,8));
        l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return l;
    }

    // =========================================================
    // INNER: FlatBtn — JPanel tu ve thay JButton
    // =========================================================
    static class FlatBtn extends JPanel {
        private final String text;
        private final Color bg, fg;
        private Color borderColor = null;
        private boolean hover = false;
        private Runnable onClick;

        FlatBtn(String text, Color bg, Color fg, int w, int h) {
            this.text = text; this.bg = bg; this.fg = fg;
            setOpaque(false);
            setPreferredSize(new Dimension(w, h));
            setMaximumSize(new Dimension(w+40, h));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setAlignmentY(Component.CENTER_ALIGNMENT);
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hover=true;  repaint(); }
                public void mouseExited(MouseEvent e)  { hover=false; repaint(); }
                public void mouseClicked(MouseEvent e) { if (onClick!=null) onClick.run(); }
            });
        }
        void setBorderColor(Color c)      { this.borderColor = c; }
        void addClickListener(Runnable r) { this.onClick = r; }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(hover ? bg.brighter() : bg);
            g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
            if (borderColor != null) {
                g2.setColor(borderColor);
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,8,8);
            }
            g2.setFont(new Font("Dialog",Font.BOLD,13));
            g2.setColor(fg);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(text,
                    (getWidth()-fm.stringWidth(text))/2,
                    (getHeight()+fm.getAscent()-fm.getDescent())/2);
            g2.dispose();
        }
    }

    // =========================================================
    // INNER: FakeCombo — JPanel tu ve thay JComboBox
    // =========================================================
    static class FakeCombo extends JPanel {
        private final String[] items;
        private int selectedIndex = 0;
        private final Color bg, fg;
        private Runnable onSelect;

        FakeCombo(String[] items, Color bg, Color fg) {
            this.items = items; this.bg = bg; this.fg = fg;
            setOpaque(false);
            setPreferredSize(new Dimension(100, 34));
            setMinimumSize(new Dimension(60,  34));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setAlignmentY(Component.CENTER_ALIGNMENT);
            JPopupMenu popup = buildPopup();
            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {

                    popup.setPreferredSize(
                            new Dimension(getWidth(), popup.getPreferredSize().height)
                    );

                    Point p = getLocationOnScreen();
                    popup.setLocation(p.x, p.y + getHeight());
                    popup.setInvoker(FakeCombo.this);
                    popup.setVisible(true);
                }
            });
        }
        private JPopupMenu buildPopup() {
            JPopupMenu pm = new JPopupMenu();
            pm.setBackground(bg);
            pm.setBorder(new LineBorder(new Color(50,68,110),1));
            for (int i=0;i<items.length;i++) {
                final int idx=i;
                JMenuItem mi = new JMenuItem(items[i]);
                mi.setBackground(bg); mi.setForeground(fg);
                mi.setFont(new Font("Dialog",Font.PLAIN,13));
                mi.setBorder(BorderFactory.createEmptyBorder(6,10,6,6));
                mi.setOpaque(true);
                mi.addActionListener(e -> {
                    selectedIndex=idx; repaint();
                    if (onSelect!=null) onSelect.run();
                });
                pm.add(mi);
            }
            return pm;
        }
        void setOnSelect(Runnable r)    { this.onSelect = r; }
        String getSelected()            { return items[selectedIndex]; }
        void setSelectedIndex(int i)    { if(i>=0&&i<items.length){selectedIndex=i;repaint();} }
        void setSelectedItem(String v)  {
            if(v==null) return;
            for(int i=0;i<items.length;i++) if(items[i].equalsIgnoreCase(v)){selectedIndex=i;break;}
            repaint();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0,0,getWidth(),getHeight(),6,6);
            g2.setColor(new Color(50,68,110));
            g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,6,6);
            g2.setFont(new Font("Dialog",Font.PLAIN,13));
            g2.setColor(fg);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(items[selectedIndex], 12, (getHeight()+fm.getAscent()-fm.getDescent())/2);
            g2.setColor(new Color(140,155,175));
            int ax=getWidth()-16, ay=getHeight()/2;
            g2.fillPolygon(new int[]{ax-4,ax+4,ax}, new int[]{ay-2,ay-2,ay+3}, 3);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Qu\u1ea3n l\u00fd Nh\u00e2n vi\u00ean");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setSize(1150, 680); f.setLocationRelativeTo(null);
            f.setContentPane(new NhanVienPanel());
            f.setVisible(true);
        });
    }
}