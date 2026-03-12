package gui.controller;

import bus.NhanVienBUS;
import entity.NhanVien;
import gui.dialog.NhanVienFormDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * NhanVienController.java
 * Package: gui.controller
 *
 * Chiu trach nhiem:
 *   - Xu ly toan bo logic nghiep vu cua man hinh Quan ly Nhan vien
 *   - Lam cau noi giua NhanVienPanel (View) va NhanVienBUS (Model)
 *   - Quan ly cache danh sach, loc du lieu, chuyen doi Entity <-> DTO
 *
 * Su dung trong NhanVienPanel:
 *   private NhanVienController controller;
 *
 *   // Trong constructor cua NhanVienPanel:
 *   controller = new NhanVienController(this);
 *   controller.loadDanhSach();
 *
 *   // Gan su kien:
 *   btnThem.addActionListener(e -> controller.them());
 *   // click Sua: controller.sua(maNV)
 *   // click Xoa: controller.xoa(maNV)
 *   // tim kiem: controller.loc(keyword, chucVu, trangThai)
 */
public class NhanVienController {

    // ── Lop tham chieu toi View ──────────────────────────────
    /**
     * Interface nay cho phep Controller giao tiep voi View
     * ma khong phu thuoc vao implemention cu the cua NhanVienPanel.
     * NhanVienPanel implements interface nay.
     */
    public interface IView {
        /** Lay tableModel de controller render du lieu len */
        DefaultTableModel getTableModel();

        /** Lay parent Frame de lam parent cho cac JDialog */
        Frame getParentFrame();

        /** Lay chinh Component nay de lam parent cho JOptionPane */
        Component getComponent();

        /** Cap nhat 3 chip thong ke (tong / hoat dong / nghi viec) */
        void updateStats(int total, int hoatDong, int nghiViec);

        /** Lay tu khoa tim kiem hien tai */
        String getKeyword();

        /** Lay chuc vu dang chon trong combobox */
        String getChucVuFilter();

        /** Lay trang thai dang chon trong combobox */
        String getTrangThaiFilter();
    }

    // ── Dependencies ─────────────────────────────────────────
    private final IView        view;
    private final NhanVienBUS  bus;

    // ── Cache du lieu ─────────────────────────────────────────
    private List<NhanVien> danhSachGoc = new ArrayList<>();

    // =========================================================
    // Constructor
    // =========================================================
    public NhanVienController(IView view) {
        this.view = view;
        this.bus  = new NhanVienBUS();
    }

    // Overload: cho phep inject BUS tu ben ngoai (tien cho unit test)
    public NhanVienController(IView view, NhanVienBUS bus) {
        this.view = view;
        this.bus  = bus;
    }

    // =========================================================
    // Load & Render
    // =========================================================

    /**
     * Tai toan bo danh sach tu DB qua BUS,
     * luu vao cache va render len bang.
     */
    public void loadDanhSach() {
        danhSachGoc = bus.getAll();
        renderTable(danhSachGoc);
    }

    /**
     * Loc danh sach tren cache (khong goi DB lai).
     * Lay filter tu View thong qua IView.
     */
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
            boolean matchTT = "Tất cả trạng thái".equals(tt)      || tt.equals(nv.getTrangthai());

            if (matchKey && matchCV && matchTT) ketQua.add(nv);
        }
        renderTable(ketQua);
    }

    /**
     * Render List<NhanVien> len DefaultTableModel cua View.
     * Sau do cap nhat chip thong ke.
     */
    private void renderTable(List<NhanVien> list) {
        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);

        for (NhanVien nv : list) {
            model.addRow(new Object[]{
                    nv.getManv(),         // col 0 — an (khoa chinh)
                    nv.getManv(),         // col 1 — Ma NV
                    nv.getHoten(),        // col 2 — Ho ten
                    nv.getSdt(),          // col 3 — So dien thoai
                    nv.getChucvu(),       // col 4 — Chuc vu
                    nv.getTendannhap(),   // col 5 — Ten dang nhap
                    nv.getTrangthai(),    // col 6 — Trang thai
                    ""                    // col 7 — Thao tac
            });
        }
        hienThiThongKe(list);
    }

    /** Tinh va gui thong ke len View */
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

    /**
     * Mo NhanVienFormDialog de them nhan vien moi.
     * Neu nguoi dung xac nhan → goi bus.add() → reload.
     */
    public void them() {
        // 1. Đổi sang gọi đúng class ThemNhanVien mà bạn mới viết
        gui.view.dialogs.ThemNhanVien dlg = new gui.view.dialogs.ThemNhanVien(view.getParentFrame());
        dlg.setVisible(true);

        // 2. Vì class ThemNhanVien của bạn tự gọi BUS.add() bên trong nó rồi,
        // nên ở đây chỉ cần kiểm tra xem nó thêm thành công chưa để load lại bảng thôi.
        if (dlg.isThemThanhCong()) {
            loadDanhSach(); // Load lại bảng ở màn hình chính
        }
    }

    /**
     * Lay NV hien tai tu BUS, mo dialog sua.
     * Neu nguoi dung xac nhan → giu mat khau cu neu bo trong → goi bus.update().
     */
    public void sua(String maNV) {
        NhanVien nvHienTai = bus.getById(maNV);
        if (nvHienTai == null) {
            showError("Không tìm thấy nhân viên: " + maNV);
            return;
        }

        NhanVienFormDialog dlg = new NhanVienFormDialog(
                view.getParentFrame(), nvHienTai);
        dlg.setVisible(true);

        NhanVien nvCapNhat = dlg.getKetQua();
        if (nvCapNhat == null) return; // nguoi dung huy

        // Giu mat khau cu neu nguoi dung de trong truong mat khau
        if (nvCapNhat.getMatkhau() == null || nvCapNhat.getMatkhau().trim().isEmpty()) {
            nvCapNhat = new NhanVien(
                    nvCapNhat.getManv(),
                    nvCapNhat.getHoten(),
                    nvCapNhat.getSdt(),
                    nvCapNhat.getChucvu(),
                    nvCapNhat.getTendannhap(),
                    nvHienTai.getMatkhau(), // giu mat khau cu
                    nvCapNhat.getTrangthai()
            );
        }

        boolean ok = bus.update(nvCapNhat);
        if (ok) {
            loadDanhSach();
            showSuccess("Cập nhật nhân viên " + maNV + " thành công!");
        } else {
            showError("Cập nhật thất bại!");
        }
    }

    /**
     * Hien hop thoai xac nhan, neu dong y → goi bus.delete().
     */
    public void xoa(String maNV) {
        NhanVien nv       = bus.getById(maNV);
        String tenHienThi = (nv != null) ? nv.getHoten() : maNV;

        int confirm = JOptionPane.showConfirmDialog(
                view.getComponent(),
                "Bạn có chắc muốn xoá nhân viên:\n" + maNV + " — " + tenHienThi + "?",
                "Xác nhận xoá",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        boolean ok = bus.delete(maNV);
        if (ok) {
            loadDanhSach();
            showSuccess("Đã xoá nhân viên " + maNV + ".");
        } else {
            showError("Xoá thất bại!\nNhân viên có thể đang được sử dụng trong hoá đơn.");
        }
    }

    // =========================================================
    // Helpers thong bao
    // =========================================================
    private void showSuccess(String msg) {
        JOptionPane.showMessageDialog(
                view.getComponent(), msg, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(
                view.getComponent(), msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}