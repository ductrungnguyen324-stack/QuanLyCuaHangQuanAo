package BUS;

import DAO.PhieuNhapHangDAO;
import DAO.ChiTietPhieuNhapDAO;
import entity.PhieuNhapHang;
import entity.ChiTietPhieuNhap;
import java.util.ArrayList;

public class PhieuNhapBUS {
    private PhieuNhapHangDAO pnDAO = new PhieuNhapHangDAO();
    private ChiTietPhieuNhapDAO ctDAO = new ChiTietPhieuNhapDAO();

    public ArrayList<PhieuNhapHang> getAll() {
        return pnDAO.getAll();
    }

   public String addFullPhieuNhap(PhieuNhapHang pn, ArrayList<ChiTietPhieuNhap> listCT) {
    if (pn.getMaPN() == null || pn.getMaPN().trim().isEmpty()) return "Mã phiếu không được để trống!";
    if (listCT == null || listCT.isEmpty()) return "Phiếu nhập phải có ít nhất một sản phẩm!";
    for (ChiTietPhieuNhap ct : listCT) {
        if (ct.getSoluong() <= 0) return "Số lượng sản phẩm " + ct.getMaSP() + " phải > 0!";
    }
 if (pnDAO.insert(pn)) {
        for (ChiTietPhieuNhap ct : listCT) {
            if (!ctDAO.insert(ct)) {
                return "Lỗi khi lưu chi tiết sản phẩm: " + ct.getMaSP();
            }
        }
        return "Nhập hàng thành công!";
    }
    return "Lỗi hệ thống khi tạo phiếu nhập!";
}
   public ArrayList<PhieuNhapHang> search(String keyword) {
    ArrayList<PhieuNhapHang> result = new ArrayList<>();
    if (keyword == null || keyword.trim().isEmpty()) {
        return getAll(); 
    }
    
    String key = keyword.trim().toLowerCase();
    for (PhieuNhapHang pn : pnDAO.getAll()) {
        if (pn.getMaPN().toLowerCase().contains(key) || 
            pn.getNhacungcap().toLowerCase().contains(key)) {
            result.add(pn);
        }
    }
    return result;
}
}
