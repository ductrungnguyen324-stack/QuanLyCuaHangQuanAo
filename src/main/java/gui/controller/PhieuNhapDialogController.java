package gui.controller;

import bus.PhieuNhapHangBUS;
import dao.ChiTietPhieuNhapDAO;
import dao.DBConnection;
import dao.SanPhamDAO;
import entity.ChiTietPhieuNhapDTO;
import entity.PhieuNhapHangDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class PhieuNhapDialogController {

    private final PhieuNhapHangBUS bus = new PhieuNhapHangBUS();
    private final ChiTietPhieuNhapDAO ctpnDAO = new ChiTietPhieuNhapDAO();
    private final SanPhamDAO spDAO = new SanPhamDAO();

    // ── Validate form header ──────────────────────────────
    public String validate(String maPN, String maNV, String ncc, int soSP) {
        if (maPN.isEmpty()) {
            return "⚠ Mã phiếu không được để trống!";
        }
        if (maNV.isEmpty()) {
            return "⚠ Mã nhân viên không được để trống!";
        }
        if (ncc.isEmpty()) {
            return "⚠ Nhà cung cấp không được để trống!";
        }
        if (soSP == 0) {
            return "⚠ Phải có ít nhất 1 sản phẩm!";
        }
        return null; // hợp lệ
    }

    // ── Validate từng dòng sản phẩm ──────────────────────
    public String validateDong(int dongSo, int soLuong, double gia) {
        if (soLuong <= 0) {
            return "⚠ Dòng " + dongSo + ": Số lượng phải lớn hơn 0!";
        }
        if (gia <= 0) {
            return "⚠ Dòng " + dongSo + ": Giá nhập phải lớn hơn 0!";
        }
        return null; // hợp lệ
    }

    // ── Thêm phiếu mới ───────────────────────────────────
    public boolean themPhieuMoi(PhieuNhapHangDTO pn, ArrayList<ChiTietPhieuNhapDTO> dsCT) {
        return bus.themPhieuNhap(pn, dsCT);
    }

    // ── Cập nhật phiếu ───────────────────────────────────
    public boolean capNhatPhieu(PhieuNhapHangDTO pn, ArrayList<ChiTietPhieuNhapDTO> dsCT) {
        return bus.capNhatPhieu(pn, dsCT);
    }

    // ── Lấy chi tiết cũ (khi sửa phiếu) ─────────────────
    public ArrayList<ChiTietPhieuNhapDTO> getChiTietCu(String maPN) {
        return bus.getChiTietByMaPN(maPN);
    }

    // ── Tự động điền Tên SP + Giá khi nhập Mã SP ─────────
    // Trả về [tenSP, giaNhap] hoặc ["Không tìm thấy", "0"] nếu không có
    public ArrayList<String> tuDongDienThongTin(String maSP) {
        ArrayList<String> result = new ArrayList<>();
        String sql = "SELECT tenSP, giaban FROM sanpham WHERE maSP = ?"; // ← đổi giaNhap → giaban
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSP);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result.add(rs.getString("tenSP"));
                    result.add(String.valueOf(rs.getDouble("giaban"))); // ← đổi giaNhap → giaban
                    return result;
                }
            }
        } catch (Exception e) {
            System.out.println("[Controller] tuDongDienThongTin lỗi: " + e.getMessage());
        }
        result.add("Không tìm thấy");
        result.add("0");
        return result;
    }

    // ── Sinh mã CTPN mới ─────────────────────────────────
    public String taoMaCTPN() {
        String last = ctpnDAO.getLastMaCTPN();
        if (last == null || last.isEmpty()) {
            return "CTPN001";
        }
        try {
            // Giả sử format: CTPN001, CTPN002, ...
            int next = Integer.parseInt(last.replaceAll("[^0-9]", "")) + 1;
            return String.format("CTPN%03d", next);
        } catch (NumberFormatException e) {
            return "CTPN001";
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
