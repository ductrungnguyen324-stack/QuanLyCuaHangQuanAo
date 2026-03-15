import gui.MainFrame;
import gui.view.LoginView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(LoginView::new);
    }
}