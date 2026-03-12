import gui.view.KhuyenMaiView;

import javax.swing.*;

public class Testing {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(KhuyenMaiView::new);
    }
}