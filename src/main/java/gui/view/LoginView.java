package gui.view;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class LoginView extends JFrame {

    private static final Color BG          = new Color(15, 20, 40);
    private static final Color CARD        = new Color(22, 28, 55);
    private static final Color FIELD_BG    = new Color(28, 36, 65);
    private static final Color BORDER      = new Color(45, 55, 90);
    private static final Color ACCENT      = new Color(99, 102, 241);
    private static final Color DANGER      = new Color(239, 68, 68);
    private static final Color TEXT1       = new Color(226, 232, 240);
    private static final Color TEXT2       = new Color(100, 116, 139);
    private static final Color PLACEHOLDER = new Color(70, 85, 120);


    private JTextField     Username;
    private JPasswordField Password;
    private JCheckBox      cbShow;
    private JLabel         lblStatus;
    private JButton        btnLogin;

    public LoginView() {
        setTitle("Đăng nhập");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel bg = new JPanel(new GridBagLayout());
        bg.setBackground(BG);
        bg.add(buildCard());

        setContentPane(bg);
        setVisible(true);
        SwingUtilities.invokeLater(() -> Username.requestFocusInWindow());
    }

    private JPanel buildCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(36, 40, 36, 40)
        ));
        card.setPreferredSize(new Dimension(400, 440));

        JPanel inner = new JPanel(new GridLayout(0, 1, 0, 12));
        inner.setBackground(CARD);
        inner.setPreferredSize(new Dimension(300, 360));

        // Title
        JLabel title = new JLabel("Đăng nhập", SwingConstants.CENTER);
        title.setFont(new Font("Sans serif", Font.BOLD, 25));
        title.setForeground(TEXT1);
        inner.add(title);

        // Label username
        JLabel lblUser = new JLabel("Tên đăng nhập");
        lblUser.setFont(new Font("Sans serif", Font.BOLD, 13));
        lblUser.setForeground(TEXT2);
        inner.add(lblUser);

        // O username
        Username = makePlaceholderField("Nhập tên đăng nhập.....");
        inner.add(Username);

        // Label password
        JLabel lblPass = new JLabel("Mật khẩu");
        lblPass.setFont(new Font("Sans serif", Font.BOLD, 13));
        lblPass.setForeground(TEXT2);
        inner.add(lblPass);

        // O password
        Password = new JPasswordField();
        Password.setEchoChar('\u25CF');
        styleField(Password);
        Password.addActionListener(e -> Login());
        inner.add(Password);

        // Checkbox hien mat khau
        cbShow = new JCheckBox("Hiển thị mật khẩu");
        cbShow.setOpaque(false);
        cbShow.setForeground(TEXT2);
        cbShow.setFont(new Font("Sans serif", Font.PLAIN, 12));
        cbShow.setFocusPainted(false);
        cbShow.addActionListener(e ->
                Password.setEchoChar(cbShow.isSelected() ? (char) 0 : '\u25CF')
        );
        inner.add(cbShow);

        // Status loi
        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("sans serif", Font.BOLD, 12));
        lblStatus.setForeground(DANGER);
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        inner.add(lblStatus);

        // Nut dang nhap
        btnLogin = new JButton("Đăng nhập");
        btnLogin.setFont(new Font("Sans serif", Font.BOLD, 14));
        btnLogin.setBackground(ACCENT);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setOpaque(true);
        btnLogin.setBorderPainted(false);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogin.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        btnLogin.addActionListener(e -> Login());
        inner.add(btnLogin);

        card.add(inner);
        return card;
    }

    private JTextField makePlaceholderField(String placeholder) {
        JTextField f = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(PLACEHOLDER);
                    g2.setFont(getFont().deriveFont(Font.PLAIN));
                    Insets ins = getInsets();
                    g2.drawString(placeholder, ins.left,
                            getHeight() / 2 + getFont().getSize() / 2 - 2);
                    g2.dispose();
                }
            }
        };
        styleField(f);
        return f;
    }

    private void styleField(JTextField f) {
        f.setFont(new Font("Sans serif", Font.PLAIN, 13));
        f.setBackground(FIELD_BG);
        f.setForeground(TEXT1);
        f.setCaretColor(ACCENT);
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(ACCENT, 1, true),
                        BorderFactory.createEmptyBorder(10, 14, 10, 14)));
                f.repaint();
            }
            public void focusLost(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(BORDER, 1, true),
                        BorderFactory.createEmptyBorder(10, 14, 10, 14)));
                f.repaint();
            }
        });
    }
    // controller goi de tuong tac
    public String getUsername() {
        return Username.getText().trim();
    }

    // lay password
    public String getPassword() {
        return new String(Password.getPassword());
    }
    // tra ve button dang nhap
    public JButton getBtnLogin() {
        return btnLogin;
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(LoginView::new);
    }
}
