package gui.controller;

import bus.ChiTietPhieuNhapBUS;
import entity.ChiTietPhieuNhapDTO;
import entity.PhieuNhapHangDTO;
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

    public double parseDouble(Object value) {
        if (value == null) return 0.0;
        String str = value.toString().replace(",", "").trim();
        if (str.isEmpty()) return 0.0;
        try { return Double.parseDouble(str); }
        catch (NumberFormatException e) { return 0.0; }
    }

    public double tinhTong(ArrayList<Double> dsThanhTien) {
        double tong = 0;
        for (Double tt : dsThanhTien) if (tt != null) tong += tt;
        return tong;
    }

    public String taoMaCTPN() {
        return ctBus.taoMaMoi();
    }

    public ArrayList<ChiTietPhieuNhapDTO> getChiTietCu(String maPN) {
        return ctBus.getByMaPN(maPN);
    }

    public boolean themPhieuMoi(PhieuNhapHangDTO pn, ArrayList<ChiTietPhieuNhapDTO> dsCT) {
        return ctBus.themPhieuFull(pn, dsCT);
    }

    public boolean capNhatPhieu(PhieuNhapHangDTO pn, ArrayList<ChiTietPhieuNhapDTO> dsCT) {
        return ctBus.updatePhieuFull(pn, dsCT);
    }

    // ── Tự động điền tên SP + giá theo mã (giả lập) ──────
    // TODO: Thay bằng SanPhamBUS.getByMa(maSP) khi có module sản phẩm
    public String[] tuDongDienThongTin(String maSP) {
        // [0] = tên SP, [1] = giá nhập (String)
        if (maSP.equalsIgnoreCase("SP001")) return new String[]{"Sản phẩm 1", "100000.0"};
        if (maSP.equalsIgnoreCase("SP002")) return new String[]{"Sản phẩm 2", "250000.0"};
        return new String[]{"Không tìm thấy", "0"};
    }
}
