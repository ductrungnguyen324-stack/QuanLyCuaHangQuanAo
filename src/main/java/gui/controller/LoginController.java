package gui.controller;
import entity.NhanVien;
import gui.view.*;
import bus.*;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginController {
    private LoginView view;
    private NhanVienBUS nvbus = new NhanVienBUS();

    public LoginController(LoginView view) {
        this.view = view;
        allListeners();
    }

    public void allListeners() {
        // get btnDangNhap de xu li
        view.getBtnLogin().addActionListener(e -> handleLogin());
        // ng dung an enter matkhau => dang nhap
        view.getPPassword().addActionListener(e -> handleLogin());
        // enter trong o tk chuyen sang o pass
        view.getTUserName().addActionListener(e -> view.getPPassword().requestFocusInWindow());

        // 4. KeyListener toàn form: Esc → thoát
        KeyAdapter escListener = new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    int confirm = JOptionPane.showConfirmDialog(
                            view, "Bạn có muốn thoát ứng dụng?",
                            "Xác nhận", JOptionPane.YES_NO_OPTION
                    );
                    if (confirm == JOptionPane.YES_OPTION) System.exit(0);
                }
            }
        };

        view.getBtnLogin().addKeyListener(escListener);
        view.getTUserName().addKeyListener(escListener);
        view.getPPassword().addKeyListener(escListener);
    }

    public void handleLogin() {
        String username = view.getUsername();
        String password = view.getPassword();

        String isGood = validate(username, password);

        if(isGood != null) {
            view.showError(isGood);
            return;
        }

        NhanVien nv = nvbus.Login(username, password);

        if (nv == null) {
            // Sai tài khoản hoặc mật khẩu
            view.showError("Sai tài khoản hoặc mật khẩu!");
            view.clearPassword();
            view.focusPassword();
            return;
        }
        // dn thanh cong
        view.showError("");
        view.dispose();

    }

    private String validate(String username, String password) {
        if (username == null || username.isEmpty())
            return "Vui lòng nhập tên đăng nhập!";
        if (password == null || password.isEmpty())
            return "Vui lòng nhập mật khẩu!";
        return null; // hợp lệ
    }

}