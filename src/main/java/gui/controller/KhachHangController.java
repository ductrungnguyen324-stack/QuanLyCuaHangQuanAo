package gui.controller;

import bus.KhachHangBUS;
import entity.KhachHang;
import gui.dialog.KhachHangDialog;
import gui.view.KhachHangView;

import javax.swing.*;
import java.util.ArrayList;

public class KhachHangController {

    private final KhachHangBUS bus = new KhachHangBUS();
    private ArrayList<KhachHang> danhSachGoc = new ArrayList<>();

    private final KhachHangView view;

    // ── THÊM MỚI: lưu chức vụ ──
    private String chucvu;

    public KhachHangController(KhachHangView view, String chucvu) {
        this.view   = view;
        this.chucvu = chucvu;
    }

    // Constructor cũ (tương thích)
    public KhachHangController(KhachHangView view) {
        this(view, "Quan ly");
    }

    // ── THÊM MỚI: kiểm tra quyền ──
    // Chỉ Quản lý mới được thêm/sửa/xoá khách hàng
    public boolean coQuyen() {
        return "Quan ly".equals(chucvu) || "Thu ngan".equals(chucvu);
    }

    private void showNoQuyen() {
        JOptionPane.showMessageDialog(view,
                "Bạn không có quyền thực hiện thao tác này!",
                "Không có quyền", JOptionPane.WARNING_MESSAGE);
    }

    // ── Load & Filter ────────────────────────────────────

    public void loadDanhSach() {
        danhSachGoc = bus.getAll();
        view.hienThiDanhSach(danhSachGoc);
    }

    public ArrayList<KhachHang> getDanhSachGoc() {
        return danhSachGoc;
    }

    public void locDuLieu(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            view.hienThiDanhSach(danhSachGoc);
            return;
        }
        String kw = keyword.trim().toLowerCase();
        ArrayList<KhachHang> ketQua = new ArrayList<>();
        for (KhachHang kh : danhSachGoc) {
            boolean trungMa  = kh.getMaKH().toLowerCase().contains(kw);
            boolean trungSDT = kh.getSdt().contains(kw);
            if (trungMa || trungSDT) ketQua.add(kh);
        }
        view.hienThiDanhSach(ketQua);
    }

    // ── CRUD ─────────────────────────────────────────────

    public void moDialogThem() {
        if (!coQuyen()) { showNoQuyen(); return; }
        KhachHangDialog dialog = new KhachHangDialog(view, null, this);
        dialog.setVisible(true);
    }

    public void moDialogSua(String maKH) {
        if (!coQuyen()) { showNoQuyen(); return; }
        KhachHang kh = bus.findById(maKH);
        if (kh == null) return;
        KhachHangDialog dialog = new KhachHangDialog(view, kh, this);
        dialog.setVisible(true);
    }

    public boolean themKhachHang(KhachHang kh) {
        if (!coQuyen()) return false;
        boolean ok = bus.add(kh);
        if (ok) loadDanhSach();
        return ok;
    }

    public boolean suaKhachHang(KhachHang kh) {
        if (!coQuyen()) return false;
        boolean ok = bus.update(kh);
        if (ok) loadDanhSach();
        return ok;
    }

    public void xoaKhachHang(String maKH, String ten) {
        if (!coQuyen()) { showNoQuyen(); return; }

        int confirm = JOptionPane.showConfirmDialog(view,
                "Bạn có chắc muốn xoá khách hàng:\n" + maKH + " - " + ten + "?",
                "Xác nhận xoá", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = bus.delete(maKH);
            if (ok) {
                loadDanhSach();
                JOptionPane.showMessageDialog(view, "Đã xoá khách hàng: " + maKH);
            } else {
                JOptionPane.showMessageDialog(view,
                        "Xoá thất bại! Khách hàng có thể đang được sử dụng.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ── Helper ───────────────────────────────────────────

    public String autoMaKH() {
        return String.format("KH%03d", danhSachGoc.size() + 1);
    }
}