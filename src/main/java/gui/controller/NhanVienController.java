package gui.controller;

import bus.NhanVienBUS;
import entity.NhanVien;
import gui.dialog.NhanVienDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NhanVienController {

    public interface IView {
        DefaultTableModel getTableModel();
        Frame getParentFrame();
        Component getComponent();
        void updateStats(int total, int hoatDong, int nghiViec);
        String getKeyword();
        String getChucVuFilter();
        String getTrangThaiFilter();
    }

    private final IView       view;
    private final NhanVienBUS bus;

    private List<NhanVien> danhSachGoc = new ArrayList<>();

    // ── THÊM MỚI: lưu chức vụ ──
    private String chucvu;

    public NhanVienController(IView view, String chucvu) {
        this.view   = view;
        this.bus    = new NhanVienBUS();
        this.chucvu = chucvu;
    }

    // Constructor cũ (tương thích)
    public NhanVienController(IView view) {
        this(view, "Quản lý");
    }

    public NhanVienController(IView view, NhanVienBUS bus) {
        this.view   = view;
        this.bus    = bus;
        this.chucvu = "Quản lý";
    }

    // ── THÊM MỚI: kiểm tra quyền ──
    // Chỉ Quản lý mới được thêm/sửa/xoá nhân viên
    private boolean coQuyen() {
        return "Quản lý".equals(chucvu);
    }

    private void showNoQuyen() {
        JOptionPane.showMessageDialog(view.getComponent(),
                "Bạn không có quyền thực hiện thao tác này!",
                "Không có quyền", JOptionPane.WARNING_MESSAGE);
    }

    // =========================================================
    // Load & Render
    // =========================================================

    public void loadDanhSach() {
        danhSachGoc = bus.getAll();
        renderTable(danhSachGoc);
    }

    public void loc() {
        String keyword = view.getKeyword().toLowerCase().trim();
        String chucVu  = view.getChucVuFilter();
        String tt      = view.getTrangThaiFilter();

        List<NhanVien> ketQua = new ArrayList<>();
        for (NhanVien nv : danhSachGoc) {
            boolean matchKey = keyword.isEmpty()
                    || nv.getManv().toLowerCase().contains(keyword)
                    || nv.getHoten().toLowerCase().contains(keyword)
                    || (nv.getSdt()        != null && nv.getSdt().contains(keyword))
                    || (nv.getTendannhap() != null && nv.getTendannhap().toLowerCase().contains(keyword));

            boolean matchCV = "Tất cả chức vụ".equals(chucVu)    || chucVu.equals(nv.getChucvu());
            boolean matchTT = "Tất cả trạng thái".equals(tt)     || tt.equals(nv.getTrangthai());

            if (matchKey && matchCV && matchTT) ketQua.add(nv);
        }
        renderTable(ketQua);
    }

    private void renderTable(List<NhanVien> list) {
        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);

        for (NhanVien nv : list) {
            model.addRow(new Object[]{
                    nv.getManv(),
                    nv.getManv(),
                    nv.getHoten(),
                    nv.getSdt(),
                    nv.getChucvu(),
                    nv.getTendannhap(),
                    nv.getTrangthai(),
                    ""
            });
        }
        hienThiThongKe(list);
    }

    private void hienThiThongKe(List<NhanVien> list) {
        int hoatDong = 0, nghiViec = 0;
        for (NhanVien nv : list) {
            if ("Hoạt động".equals(nv.getTrangthai())) hoatDong++;
            else nghiViec++;
        }
        view.updateStats(list.size(), hoatDong, nghiViec);
    }

    // =========================================================
    // CRUD
    // =========================================================

    public void them() {
        if (!coQuyen()) { showNoQuyen(); return; }

        NhanVienDialog dlg = new NhanVienDialog(view.getParentFrame(), null);
        dlg.setVisible(true);

        NhanVien nv = dlg.getKetQua();
        if (nv == null) return;

        boolean ok = bus.add(nv);
        if (ok) {
            loadDanhSach();
            showSuccess("Thêm nhân viên " + nv.getManv() + " thành công!");
        } else {
            showError("Thêm thất bại!\nMã nhân viên có thể đã tồn tại.");
        }
    }

    public void sua(String maNV) {
        if (!coQuyen()) { showNoQuyen(); return; }

        NhanVien nvHienTai = bus.getById(maNV);
        if (nvHienTai == null) {
            showError("Không tìm thấy nhân viên: " + maNV);
            return;
        }

        NhanVienDialog dlg = new NhanVienDialog(view.getParentFrame(), nvHienTai);
        dlg.setVisible(true);

        NhanVien nvCapNhat = dlg.getKetQua();
        if (nvCapNhat == null) return;

        if (nvCapNhat.getMatkhau() == null || nvCapNhat.getMatkhau().trim().isEmpty()) {
            nvCapNhat = new NhanVien(
                    nvCapNhat.getManv(), nvCapNhat.getHoten(), nvCapNhat.getSdt(),
                    nvCapNhat.getChucvu(), nvCapNhat.getTendannhap(),
                    nvHienTai.getMatkhau(),
                    nvCapNhat.getTrangthai());
        }

        boolean ok = bus.update(nvCapNhat);
        if (ok) {
            loadDanhSach();
            showSuccess("Cập nhật nhân viên " + maNV + " thành công!");
        } else {
            showError("Cập nhật thất bại!");
        }
    }

    public void xoa(String maNV) {
        if (!coQuyen()) { showNoQuyen(); return; }

        NhanVien nv       = bus.getById(maNV);
        String tenHienThi = (nv != null) ? nv.getHoten() : maNV;

        int confirm = JOptionPane.showConfirmDialog(
                view.getComponent(),
                "Bạn có chắc muốn xóa nhân viên:\n" + maNV + " - " + tenHienThi + "?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        boolean ok = bus.delete(maNV);
        if (ok) {
            loadDanhSach();
            showSuccess("Đã xóa nhân viên " + maNV + ".");
        } else {
            showError("Xóa thất bại!\nNhân viên có thể đang được sử dụng.");
        }
    }

    private void showSuccess(String msg) {
        JOptionPane.showMessageDialog(view.getComponent(), msg,
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(view.getComponent(), msg,
                "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}