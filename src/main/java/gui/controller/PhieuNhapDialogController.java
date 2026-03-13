package gui.controller;

import bus.ChiTietPhieuNhapBUS;
import entity.ChiTietPhieuNhapDTO;
import entity.PhieuNhapHangDTO;
import java.util.ArrayList;




import dao.SanPhamDAO;
import entity.SanPham;
import java.util.ArrayList;

public class PhieuNhapDialogController {

    private final ChiTietPhieuNhapBUS ctBus = new ChiTietPhieuNhapBUS();

    public String validate(String maPN, String maNV, String ncc, int soSanPham) {
        if (maPN.isEmpty() || maNV.isEmpty() || ncc.isEmpty())
            return "Vui lòng điền đủ thông tin!";
        if (soSanPham == 0)
            return "Chưa có sản phẩm nào trong phiếu!";
        return null;
    }

    public String validateDong(int dongSo, int soLuong, double gia) {
        if (soLuong <= 0 || gia <= 0)
            return "Dòng " + dongSo + ": Số lượng và giá phải > 0!";
        return null;
    }

    // ── Parse an toàn (xử lý Double, dấu phẩy, null) ─────
    public double parseDouble(Object value) {
        if (value == null) return 0.0;
        String str = value.toString().replace(",", "").trim();
        if (str.isEmpty()) return 0.0;
        try { return Double.parseDouble(str); }
        catch (NumberFormatException e) { return 0.0; }
    }

    // ── Tính tổng tiền từ cột thành tiền ─────────────────
    public double tinhTong(ArrayList<Double> dsThanhTien) {
        double tong = 0;
        for (Double tt : dsThanhTien) if (tt != null) tong += tt;
        return tong;
    }

    // ── Tạo mã chi tiết mới ──────────────────────────────
    public String taoMaCTPN() {
        return ctBus.taoMaMoi();
    }

    // ── Lấy danh sách chi tiết cũ (dùng khi sửa) ─────────
    public ArrayList<ChiTietPhieuNhapDTO> getChiTietCu(String maPN) {
        return ctBus.getByMaPN(maPN);
    }

    // ── Lưu phiếu mới ────────────────────────────────────
    public boolean themPhieuMoi(PhieuNhapHangDTO pn, ArrayList<ChiTietPhieuNhapDTO> dsCT) {
        return ctBus.themPhieuFull(pn, dsCT);
    }

    // ── Cập nhật phiếu đã có ─────────────────────────────
    public boolean capNhatPhieu(PhieuNhapHangDTO pn, ArrayList<ChiTietPhieuNhapDTO> dsCT) {
        return ctBus.updatePhieuFull(pn, dsCT);
    }

    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();
 
public ArrayList<String> tuDongDienThongTin(String maSP) {
        ArrayList<String> result = new ArrayList<>();
 
        if (maSP == null || maSP.trim().isEmpty()) {
            result.add("");
            result.add("0");
            return result;
        }
 
        try {
            SanPham sp = sanPhamDAO.getById(maSP.trim());
            if (sp != null) {
                result.add(sp.getTensp());
                result.add(String.valueOf(sp.getGiaban()));
                return result;
            }
        } catch (Exception e) {
            System.err.println("[Controller] Lỗi tra cứu SP '" + maSP + "': " + e.getMessage());
        }
 
        result.add("Không tìm thấy");
        result.add("0");
        return result;
    }
}