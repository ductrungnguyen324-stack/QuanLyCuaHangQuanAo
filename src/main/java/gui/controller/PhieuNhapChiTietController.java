package gui.controller;

import bus.ChiTietPhieuNhapBUS;
import bus.PhieuNhapHangBUS;
import entity.ChiTietPhieuNhapDTO;
import entity.PhieuNhapHangDTO;
import java.util.ArrayList;

public class PhieuNhapChiTietController {

    private final PhieuNhapHangBUS    pnBUS = new PhieuNhapHangBUS();
    private final ChiTietPhieuNhapBUS ctBUS = new ChiTietPhieuNhapBUS();

    // ── Lấy danh sách chi tiết ───────────────────────────
    public ArrayList<ChiTietPhieuNhapDTO> getChiTiet(String maPN) {
        ArrayList<ChiTietPhieuNhapDTO> list = ctBUS.getByMaPN(maPN);
        System.out.println("[Controller] getChiTiet(" + maPN + ") → " + list.size() + " dòng");
        return list;
    }

    public boolean duyetPhieu(String maPN) {
        return pnBUS.duyetPhieu(maPN);
    }

    public boolean isDaDuyet(PhieuNhapHangDTO pn) {
        return "Đã nhập kho".equals(pn.getTrangThai());
    }
}
