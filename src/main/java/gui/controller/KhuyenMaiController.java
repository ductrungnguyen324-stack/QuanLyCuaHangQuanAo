package gui.controller;

import bus.KhuyenMaiBUS;
import entity.KhuyenMai;
import gui.view.KhuyenMaiView;
import gui.dialog.KhuyenMaiDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class KhuyenMaiController {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final KhuyenMaiView view;
    private final KhuyenMaiBUS  bus;

    // ── THÊM MỚI: lưu chức vụ ──
    private String chucvu;

    public KhuyenMaiController(KhuyenMaiView view, String chucvu) {
        this.view   = view;
        this.bus    = new KhuyenMaiBUS();
        this.chucvu = chucvu;
    }

    // Constructor cũ (tương thích)
    public KhuyenMaiController(KhuyenMaiView view) {
        this(view, "Quản lý");
    }

    // ── THÊM MỚI: kiểm tra quyền ──
    // Chỉ Quản lý mới được thêm/sửa/xoá khuyến mãi
    private boolean coQuyen() {
        return "Quản lý".equals(chucvu);
    }

    private void showNoQuyen() {
        JOptionPane.showMessageDialog(view,
                "Bạn không có quyền thực hiện thao tác này!",
                "Không có quyền", JOptionPane.WARNING_MESSAGE);
    }

    // ── Tải toàn bộ danh sách ─────────────────────────────
    public void loadDanhSach() {
        try {
            List<KhuyenMai> list = bus.getAll();
            renderRows(list);
        } catch (Exception e) {
            showError("Không thể tải dữ liệu: " + e.getMessage());
        }
    }

    // ── Lọc theo loại và trạng thái ─────────────────────
    public void locDuLieu(String keyword, String loaiKM, String trangThai) {
        try {
            List<KhuyenMai> list = (keyword == null || keyword.trim().isEmpty())
                    ? bus.getAll()
                    : bus.searchKhuyenMai(keyword.trim());

            if (!"Tất cả loại".equals(loaiKM)) {
                list = list.stream()
                        .filter(km -> loaiKM.equals(km.getLoaiKM()))
                        .collect(java.util.stream.Collectors.toList());
            }

            if ("Còn hiệu lực".equals(trangThai)) {
                list = list.stream()
                        .filter(KhuyenMai::isKhaDung)
                        .collect(java.util.stream.Collectors.toList());
            } else if ("Hết hạn".equals(trangThai)) {
                list = list.stream()
                        .filter(km -> !km.isKhaDung())
                        .collect(java.util.stream.Collectors.toList());
            }

            renderRows(list);
        } catch (Exception e) {
            showError("Lỗi lọc dữ liệu: " + e.getMessage());
        }
    }

    // ── Thêm mới ─────────────────────────────────────────
    public void moDialogThem() {
        if (!coQuyen()) { showNoQuyen(); return; }

        KhuyenMaiDialog dialog = new KhuyenMaiDialog(view, null);
        dialog.setVisible(true);

        KhuyenMai km = dialog.getKetQua();
        if (km != null) {
            try {
                bus.addKhuyenMai(km);
                loadDanhSach();
                showInfo("Thêm khuyến mãi thành công!");
            } catch (Exception e) {
                showError("Thêm thất bại: " + e.getMessage());
            }
        }
    }

    // ── Sửa ──────────────────────────────────────────────
    public void suaKhuyenMai(String maKM) {
        if (!coQuyen()) { showNoQuyen(); return; }

        KhuyenMai km = bus.getByID(maKM);
        if (km == null) { showError("Không tìm thấy khuyến mãi: " + maKM); return; }

        KhuyenMaiDialog dialog = new KhuyenMaiDialog(view, km);
        dialog.setVisible(true);

        KhuyenMai result = dialog.getKetQua();
        if (result != null) {
            try {
                bus.updateKhuyenMai(result);
                loadDanhSach();
                showInfo("Cập nhật khuyến mãi thành công!");
            } catch (Exception e) {
                showError("Cập nhật thất bại: " + e.getMessage());
            }
        }
    }

    // ── Xem chi tiết (tất cả đều xem được) ──────────────
    public void xemChiTiet(String maKM) {
        KhuyenMai km = bus.getByID(maKM);
        if (km == null) { showError("Không tìm thấy khuyến mãi: " + maKM); return; }

        String loaiHienThi = km.getLoaiKM().equals("Phần trăm") ? "Phần trăm (%)" : "Tiền cố định (đ)";
        String giaTriHienThi = km.getLoaiKM().equals("Phần trăm")
                ? String.format("%.0f%%", km.getGiatrigiam())
                : String.format("%,.0f đ", km.getGiatrigiam());

        String msg = String.format(
                """
                ╔══════════════════════════════════════╗
                  Chi tiết Khuyến mãi
                ╠══════════════════════════════════════╣
                  Mã KM          : %s
                  Tên            : %s
                  Loại           : %s
                  Giá trị giảm  : %s
                  Giảm tối đa   : %,.0f đ
                  Đơn tối thiểu : %,.0f đ
                  Bắt đầu        : %s
                  Kết thúc       : %s
                  Số lượng       : %d
                  Đã dùng        : %d
                  Trạng thái     : %s
                ╚══════════════════════════════════════╝
                """,
                km.getMaKM(), km.getTenKM(), loaiHienThi,
                giaTriHienThi, km.getGiamtoida(), km.getGiatridonhangtoithieu(),
                km.getNgaybatdau().format(FMT), km.getNgayketthuc().format(FMT),
                km.getSoluong(), km.getDasudung(),
                km.isKhaDung() ? "Còn hiệu lực" : "Hết hạn"
        );
        JOptionPane.showMessageDialog(view, msg, "Chi tiết: " + maKM,
                JOptionPane.INFORMATION_MESSAGE);
    }

    // ── Xóa ──────────────────────────────────────────────
    public void xoaKhuyenMai(String maKM) {
        if (!coQuyen()) { showNoQuyen(); return; }

        int confirm = JOptionPane.showConfirmDialog(view,
                "Bạn có chắc muốn xóa khuyến mãi: " + maKM + "?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                bus.deleteKhuyeMai(maKM);
                loadDanhSach();
                showInfo("Đã xóa khuyến mãi " + maKM);
            } catch (Exception e) {
                showError("Xóa thất bại: " + e.getMessage());
            }
        }
    }

    // ── Render rows vào tableModel ────────────────────────
    private void renderRows(List<KhuyenMai> list) {
        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);

        for (KhuyenMai km : list) {
            String loai = km.getLoaiKM();
            String giaTriHienThi = loai.equals("Phần trăm")
                    ? String.format("%.0f%%", km.getGiatrigiam())
                    : String.format("%,.0f đ", km.getGiatrigiam());

            model.addRow(new Object[]{
                    km.getMaKM(),
                    km.getTenKM(),
                    loai,
                    giaTriHienThi,
                    String.format("%,.0f đ", km.getGiamtoida()),
                    String.format("%,.0f đ", km.getGiatridonhangtoithieu()),
                    km.getNgaybatdau() != null ? km.getNgaybatdau().format(FMT) : "",
                    km.getNgayketthuc() != null ? km.getNgayketthuc().format(FMT) : "",
                    km.getSoluong(),
                    km.getDasudung(),
                    km.isKhaDung() ? "Còn hiệu lực" : "Hết hạn",
                    ""
            });
        }
        refreshStats();
    }

    private void refreshStats() {
        try {
            List<KhuyenMai> all = bus.getAll();
            int total    = all.size();
            int hoatDong = (int) all.stream().filter(KhuyenMai::isKhaDung).count();
            int hetHan   = total - hoatDong;
            view.updateStats(total, hoatDong, hetHan);
        } catch (Exception e) {
            view.updateStats(0, 0, 0);
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(view, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(view, msg, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
}