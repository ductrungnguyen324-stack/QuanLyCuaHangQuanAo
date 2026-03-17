package gui.controller;

import bus.ChiTietPhieuNhapBUS;
import bus.PhieuNhapHangBUS;
import bus.SanPhamBUS;
import entity.ChiTietPhieuNhapDTO;
import entity.PhieuNhapHangDTO;
import java.util.ArrayList;

public class PhieuNhapChiTietController {

    private final PhieuNhapHangBUS pnBUS = new PhieuNhapHangBUS();
    private final ChiTietPhieuNhapBUS ctBUS = new ChiTietPhieuNhapBUS();

     private final SanPhamBUS spBUS = new SanPhamBUS();
    
    public ArrayList<ChiTietPhieuNhapDTO> getChiTiet(String maPN) {
        ArrayList<ChiTietPhieuNhapDTO> list = ctBUS.getByMaPN(maPN);
       // System.out.println("[Controller] getChiTiet(" + maPN + ") → " + list.size() + " dòng");
        return list;
    }

    /**
     * Reload phiếu từ DB — dùng để kiểm tra trạng thái mới nhất trước khi
     * duyệt.
     */
    public PhieuNhapHangDTO getPhieuNhapById(String maPN) {
        return pnBUS.getPhieuNhapById(maPN);
    }

    public boolean duyetPhieu(String maPN) {
        return pnBUS.duyetPhieu(maPN);
    }

    public boolean isDaDuyet(PhieuNhapHangDTO pn) {
        return "Đã nhập kho".equals(pn.getTrangThai());
    }
    
    //thêm
    public boolean capNhatTonKho(ArrayList<ChiTietPhieuNhapDTO> listCT) {
        if (listCT == null || listCT.isEmpty()) return true;
        try {
            for (ChiTietPhieuNhapDTO ct : listCT) {
                spBUS.tangTonKho(ct.getMaSP(), ct.getSoLuong()); // ném RuntimeException nếu lỗi
            }
            return true;
        } catch (RuntimeException e) {
            System.out.println("[Controller] capNhatTonKho lỗi: " + e.getMessage());
            return false;
        }
    }
}
