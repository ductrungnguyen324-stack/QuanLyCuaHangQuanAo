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

            boolean matchCV = "T\u1ea5t c\u1ea3 ch\u1ee9c v\u1ee5".equals(chucVu)    || chucVu.equals(nv.getChucvu());
            boolean matchTT = "T\u1ea5t c\u1ea3 tr\u1ea1ng th\u00e1i".equals(tt)     || tt.equals(nv.getTrangthai());

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
            if ("Ho\u1ea1t \u0111\u1ed9ng".equals(nv.getTrangthai())) hoatDong++;
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
        NhanVienFormDialog dlg = new NhanVienFormDialog(view.getParentFrame(), null);
        dlg.setVisible(true);

        NhanVien nv = dlg.getKetQua();
        if (nv == null) return;

        boolean ok = bus.add(nv);
        if (ok) {
            loadDanhSach();
            showSuccess("Th\u00eam nh\u00e2n vi\u00ean " + nv.getManv() + " th\u00e0nh c\u00f4ng!");
        } else {
            showError("Th\u00eam th\u1ea5t b\u1ea1i!\nM\u00e3 nh\u00e2n vi\u00ean c\u00f3 th\u1ec3 \u0111\u00e3 t\u1ed3n t\u1ea1i.");
        }
    }

    public void sua(String maNV) {
        NhanVien nvHienTai = bus.getById(maNV);
        if (nvHienTai == null) {
            showError("Kh\u00f4ng t\u00ecm th\u1ea5y nh\u00e2n vi\u00ean: " + maNV);
            return;
        }

        NhanVienFormDialog dlg = new NhanVienFormDialog(view.getParentFrame(), nvHienTai);
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
            showSuccess("C\u1eadp nh\u1eadt nh\u00e2n vi\u00ean " + maNV + " th\u00e0nh c\u00f4ng!");
        } else {
            showError("C\u1eadp nh\u1eadt th\u1ea5t b\u1ea1i!");
        }
    }

    public void xoa(String maNV) {
        NhanVien nv       = bus.getById(maNV);
        String tenHienThi = (nv != null) ? nv.getHoten() : maNV;

        int confirm = JOptionPane.showConfirmDialog(
                view.getComponent(),
                "B\u1ea1n c\u00f3 ch\u1eafc mu\u1ed1n x\u00f3a nh\u00e2n vi\u00ean:\n" + maNV + " - " + tenHienThi + "?",
                "X\u00e1c nh\u1eadn x\u00f3a",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        boolean ok = bus.delete(maNV);
        if (ok) {
            loadDanhSach();
            showSuccess("\u0110\u00e3 x\u00f3a nh\u00e2n vi\u00ean " + maNV + ".");
        } else {
            showError("X\u00f3a th\u1ea5t b\u1ea1i!\nNh\u00e2n vi\u00ean c\u00f3 th\u1ec3 \u0111ang \u0111\u01b0\u1ee3c s\u1eed d\u1ee5ng.");
        }
    }

    private void showSuccess(String msg) {
        JOptionPane.showMessageDialog(view.getComponent(), msg,
                "Th\u00e0nh c\u00f4ng", JOptionPane.INFORMATION_MESSAGE);
    }
    private void showError(String msg) {
        JOptionPane.showMessageDialog(view.getComponent(), msg,
                "L\u1ed7i", JOptionPane.ERROR_MESSAGE);
    }
}