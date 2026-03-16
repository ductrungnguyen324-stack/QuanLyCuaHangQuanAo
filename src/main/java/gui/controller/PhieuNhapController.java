package gui.controller;

import bus.PhieuNhapHangBUS;
import dao.ChiTietPhieuNhapDAO;
import dao.PhieuNhapHangDAO;
import dao.SanPhamDAO;
import entity.ChiTietPhieuNhapDTO;
import entity.PhieuNhapHangDTO;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * PhieuNhapController — controller duy nhất cho toàn bộ module phiếu nhập. Gộp
 * từ PhieuNhapDialogController + PhieuNhapPanelController.
 *
 * Phân quyền: Quan ly + Quan kho → toàn quyền thao tác. Thu ngân + Nhân viên →
 * chỉ được xem.
 */
public class PhieuNhapController {

    private final PhieuNhapHangBUS bus = new PhieuNhapHangBUS();
    private final PhieuNhapHangDAO dao = PhieuNhapHangDAO.getInstance();
    private final ChiTietPhieuNhapDAO ctpnDAO = new ChiTietPhieuNhapDAO();
    private final SanPhamDAO spDAO = new SanPhamDAO();

    private final String chucvu;

    // Constructor mặc định — không phân quyền (tương thích cũ)
    public PhieuNhapController() {
        this.chucvu = "Quan ly";
    }

    // Constructor với phân quyền
    public PhieuNhapController(String chucvu) {
        this.chucvu = chucvu;
    }

    // ═══════════════════════════════════════════════════════
    // PHÂN QUYỀN
    // ═══════════════════════════════════════════════════════
    public boolean coQuyen() {
        return "Quan ly".equals(chucvu) || "Quan kho".equals(chucvu);
    }

    public void showNoQuyen(Component parent) {
        JOptionPane.showMessageDialog(parent,
                "Bạn không có quyền thực hiện thao tác này!",
                "Không có quyền",
                JOptionPane.WARNING_MESSAGE);
    }

    // ═══════════════════════════════════════════════════════
    // DANH SÁCH & LỌC
    // ═══════════════════════════════════════════════════════
    public ArrayList<PhieuNhapHangDTO> getAll() {
        return bus.getAll();
    }

    /**
     * Lọc theo keyword (maPN, maNV, maNCC, tenNCC) và trạng thái.
     */
    public ArrayList<PhieuNhapHangDTO> locDuLieu(String keyword, String trangThai) {
        String kw = keyword.trim().toLowerCase();
        ArrayList<PhieuNhapHangDTO> ketQua = new ArrayList<>();

        for (PhieuNhapHangDTO pn : bus.getAll()) {
            boolean matchTT = trangThai.equals("Tất cả trạng thái")
                    || pn.getTrangThai().equalsIgnoreCase(trangThai);

            boolean matchKW = kw.isEmpty()
                    || pn.getMaPN().toLowerCase().contains(kw)
                    || pn.getMaNV().toLowerCase().contains(kw)
                    || pn.getMaNCC().toLowerCase().contains(kw)
                    || (pn.getTenNCC() != null && pn.getTenNCC().toLowerCase().contains(kw));

            if (matchTT && matchKW) {
                ketQua.add(pn);
            }
        }
        return ketQua;
    }

    public ArrayList<PhieuNhapHangDTO> getByMaNCC(String maNCC) {
        return dao.getByMaNCC(maNCC);
    }

    public PhieuNhapHangDTO getById(String maPN) {
        return bus.getPhieuNhapById(maPN);
    }

    // ═══════════════════════════════════════════════════════
    // THÊM / CẬP NHẬT / XOÁ / DUYỆT
    // ═══════════════════════════════════════════════════════
    public boolean themPhieuMoi(PhieuNhapHangDTO pn, ArrayList<ChiTietPhieuNhapDTO> dsCT) {
        if (!coQuyen()) {
            return false;
        }
        return bus.themPhieuNhap(pn, dsCT);
    }

    public boolean capNhatPhieu(PhieuNhapHangDTO pn, ArrayList<ChiTietPhieuNhapDTO> dsCT) {
        if (!coQuyen()) {
            return false;
        }
        return bus.capNhatPhieu(pn, dsCT);
    }

    public boolean duyetPhieu(String maPN) {
        if (!coQuyen()) {
            return false;
        }
        return bus.duyetPhieu(maPN);
    }

    public boolean xoaPhieu(String maPN) {
        if (!coQuyen()) {
            return false;
        }
        return bus.xoaPhieuNhap(maPN);
    }

    public ArrayList<ChiTietPhieuNhapDTO> getChiTietCu(String maPN) {
        return bus.getChiTietByMaPN(maPN);
    }

    // ═══════════════════════════════════════════════════════
    // VALIDATE
    // ═══════════════════════════════════════════════════════
    public String validate(String maPN, String maNV, String maNCC, int soSP) {
        if (maPN.isEmpty()) {
            return "⚠ Mã phiếu không được để trống!";
        }
        if (maNV.isEmpty()) {
            return "⚠ Mã nhân viên không được để trống!";
        }
        if (maNCC.isEmpty()) {
            return "⚠ Mã nhà cung cấp không được để trống!";
        }
        if (soSP == 0) {
            return "⚠ Phải có ít nhất 1 sản phẩm!";
        }
        return null;
    }

    public String validateDong(int dongSo, int soLuong, double gia) {
        if (soLuong <= 0) {
            return "⚠ Dòng " + dongSo + ": Số lượng phải lớn hơn 0!";
        }
        if (gia <= 0) {
            return "⚠ Dòng " + dongSo + ": Giá nhập phải lớn hơn 0!";
        }
        return null;
    }

    // ═══════════════════════════════════════════════════════
    // THỐNG KÊ
    // ═══════════════════════════════════════════════════════
    public int[] tinhThongKe(ArrayList<PhieuNhapHangDTO> list) {
        int[] stats = new int[4];
        stats[0] = list.size();
        for (PhieuNhapHangDTO pn : list) {
            String tt = pn.getTrangThai();
            if ("Chờ xử lý".equals(tt)) {
                stats[1]++;
            }
            if ("Đã nhập kho".equals(tt)) {
                stats[2]++;
            }
            if ("Đã huỷ".equals(tt)) {
                stats[3]++;
            }
        }
        return stats;
    }

    public int[] tinhThongKeAll() {
        return tinhThongKe(bus.getAll());
    }

    public double tinhTongChi(ArrayList<PhieuNhapHangDTO> list) {
        double tong = 0;
        for (PhieuNhapHangDTO pn : list) {
            tong += pn.getTongTien();
        }
        return tong;
    }

    public double tinhTongChiAll() {
        return tinhTongChi(bus.getAll());
    }

    public boolean isDaDuyet(String trangThai) {
        return "Đã nhập kho".equals(trangThai);
    }

    // ═══════════════════════════════════════════════════════
    // HỖ TRỢ SP & MÃ
    // ═══════════════════════════════════════════════════════
    /**
     * Tự động điền Tên SP + Giá khi nhập Mã SP — dùng SanPhamDAO, không raw
     * SQL.
     */
    public String[] tuDongDienThongTin(String maSP) {
        return spDAO.getThongTinSP(maSP);
    }

    public String generateMaSP() {
        return spDAO.generateSP();
    }

    public String taoMaCTPN() {
        return String.format("CTPN%03d", getNextMaCTPNNumber());
    }

    /**
     * Trả về số thứ tự tiếp theo để sinh maCTPN. Gọi 1 lần DUY NHẤT trước vòng
     * lặp insert để tránh duplicate key: int next =
     * controller.getNextMaCTPNNumber(); for (...) { maCTPN =
     * String.format("CTPN%03d", next++); }
     */
    public int getNextMaCTPNNumber() {
        String last = ctpnDAO.getLastMaCTPN();
        if (last == null || last.isEmpty()) {
            return 1;
        }
        try {
            return Integer.parseInt(last.replaceAll("[^0-9]", "")) + 1;
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    public double parseDouble(Object val) {
        if (val == null) {
            return 0;
        }
        try {
            return Double.parseDouble(val.toString().replace(",", "").replace("đ", "").trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
