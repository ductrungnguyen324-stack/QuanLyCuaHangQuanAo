package gui.controller;

import bus.PhieuNhapHangBUS;
import dao.ChiTietPhieuNhapDAO;
import dao.DBConnection;
import dao.PhieuNhapHangDAO;
import dao.SanPhamDAO;
import entity.ChiTietPhieuNhapDTO;
import entity.PhieuNhapHangDTO;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * PhieuNhapController — có phân quyền theo chức vụ.
 * Quản lý + Quản kho: toàn quyền thao tác phiếu nhập.
 * Thu ngân + Nhân viên: chỉ được xem.
 */
public class PhieuNhapController {

    private final PhieuNhapHangBUS bus      = new PhieuNhapHangBUS();
    private final PhieuNhapHangDAO dao      = new PhieuNhapHangDAO();
    private final ChiTietPhieuNhapDAO ctpnDAO = new ChiTietPhieuNhapDAO();
    private final SanPhamDAO spDAO          = new SanPhamDAO();

    // ── THÊM MỚI: lưu chức vụ ──
    private String chucvu;

    // Constructor mặc định (không phân quyền — giữ tương thích cũ)
    public PhieuNhapController() {
        this.chucvu = "Quan ly"; // mặc định full quyền
    }

    // Constructor với phân quyền
    public PhieuNhapController(String chucvu) {
        this.chucvu = chucvu;
    }

    // ── THÊM MỚI: kiểm tra quyền ──
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

    public ArrayList<PhieuNhapHangDTO> locDuLieu(String keyword, String trangThai) {
        String kw = keyword.trim().toLowerCase();
        ArrayList<PhieuNhapHangDTO> ketQua = new ArrayList<>();

        for (PhieuNhapHangDTO pn : bus.getAll()) {
            boolean matchTT = trangThai.equals("Tất cả trạng thái")
                    || pn.getTrangThai().equalsIgnoreCase(trangThai);
            boolean matchKW = kw.isEmpty()
                    || pn.getMaPN().toLowerCase().contains(kw)
                    || pn.getMaNV().toLowerCase().contains(kw)
                    || pn.getNhaCungCap().toLowerCase().contains(kw);
            if (matchTT && matchKW) ketQua.add(pn);
        }
        return ketQua;
    }

    public PhieuNhapHangDTO getById(String maPN) {
        return bus.getPhieuNhapById(maPN);
    }

    // ═══════════════════════════════════════════════════════
    // THÊM / CẬP NHẬT / XOÁ / DUYỆT  (kiểm tra quyền trước khi gọi)
    // ═══════════════════════════════════════════════════════

    public boolean themPhieuMoi(PhieuNhapHangDTO pn, ArrayList<ChiTietPhieuNhapDTO> dsCT) {
        if (!coQuyen()) return false;
        return bus.themPhieuNhap(pn, dsCT);
    }

    public boolean capNhatPhieu(PhieuNhapHangDTO pn, ArrayList<ChiTietPhieuNhapDTO> dsCT) {
        if (!coQuyen()) return false;
        return bus.capNhatPhieu(pn, dsCT);
    }

    public boolean duyetPhieu(String maPN) {
        if (!coQuyen()) return false;
        return bus.duyetPhieu(maPN);
    }

    public boolean xoaPhieu(String maPN) {
        if (!coQuyen()) return false;
        return bus.xoaPhieuNhap(maPN);
    }

    // ═══════════════════════════════════════════════════════
    // VALIDATE
    // ═══════════════════════════════════════════════════════

    public String validate(String maPN, String maNV, String ncc, int soSP) {
        if (maPN.isEmpty())  return "⚠ Mã phiếu không được để trống!";
        if (maNV.isEmpty())  return "⚠ Mã nhân viên không được để trống!";
        if (ncc.isEmpty())   return "⚠ Nhà cung cấp không được để trống!";
        if (soSP == 0)       return "⚠ Phải có ít nhất 1 sản phẩm!";
        return null;
    }

    public String validateDong(int dongSo, int soLuong, double gia) {
        if (soLuong <= 0) return "⚠ Dòng " + dongSo + ": Số lượng phải lớn hơn 0!";
        if (gia <= 0)     return "⚠ Dòng " + dongSo + ": Giá nhập phải lớn hơn 0!";
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
            if ("Chờ xử lý".equals(tt))   stats[1]++;
            if ("Đã nhập kho".equals(tt))  stats[2]++;
            if ("Đã huỷ".equals(tt))       stats[3]++;
        }
        return stats;
    }

    public int[] tinhThongKeAll() {
        return tinhThongKe(bus.getAll());
    }

    public double tinhTongChi(ArrayList<PhieuNhapHangDTO> list) {
        double tong = 0;
        for (PhieuNhapHangDTO pn : list) tong += pn.getTongTien();
        return tong;
    }

    public double tinhTongChiAll() {
        return tinhTongChi(bus.getAll());
    }

    public boolean isDaDuyet(String trangThai) {
        return "Đã nhập kho".equals(trangThai);
    }

    // ═══════════════════════════════════════════════════════
    // CHI TIẾT & HỖ TRỢ
    // ═══════════════════════════════════════════════════════

    public ArrayList<ChiTietPhieuNhapDTO> getChiTietCu(String maPN) {
        return bus.getChiTietByMaPN(maPN);
    }

    public ArrayList<String> tuDongDienThongTin(String maSP) {
        ArrayList<String> result = new ArrayList<>();
        String sql = "SELECT tenSP, giaban FROM sanpham WHERE maSP = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSP);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result.add(rs.getString("tenSP"));
                    result.add(String.valueOf(rs.getDouble("giaban")));
                    return result;
                }
            }
        } catch (Exception e) {
            System.out.println("[PhieuNhapController] tuDongDienThongTin lỗi: " + e.getMessage());
        }
        result.add("Không tìm thấy");
        result.add("0");
        return result;
    }

    public String generateMaSP() {
        return spDAO.generateSP();
    }

    public String taoMaCTPN() {
        String last = ctpnDAO.getLastMaCTPN();
        if (last == null || last.isEmpty()) return "CTPN001";
        try {
            int next = Integer.parseInt(last.replaceAll("[^0-9]", "")) + 1;
            return String.format("CTPN%03d", next);
        } catch (NumberFormatException e) {
            return "CTPN001";
        }
    }

    public double parseDouble(Object val) {
        if (val == null) return 0;
        try {
            return Double.parseDouble(val.toString().replace(",", "").replace("đ", "").trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}