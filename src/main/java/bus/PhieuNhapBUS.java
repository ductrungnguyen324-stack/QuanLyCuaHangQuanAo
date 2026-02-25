package BUS;

import DAO.PhieuNhapDAO;
import DAO.ChiTietPhieuNhapDAO;
import DTO.PhieuNhapDTO;
import DTO.ChiTietPhieuNhapDTO;
import java.util.ArrayList;

public class PhieuNhapBUS {
    private PhieuNhapDAO pnDAO = new PhieuNhapDAO();
    private ChiTietPhieuNhapDAO ctDAO = new ChiTietPhieuNhapDAO();

    public ArrayList<PhieuNhapDTO> getAll() {
        return pnDAO.getAll();
    }

    public String addPhieuNhap(PhieuNhapDTO pn, ArrayList<ChiTietPhieuNhapDTO> dsChiTiet) {
        if (pn.getMaPhieu() == null || pn.getMaPhieu().trim().isEmpty()) 
            return "Mã phiếu không được để trống!";
        
        if (dsChiTiet == null || dsChiTiet.isEmpty()) 
            return "Phiếu nhập phải có ít nhất 1 sản phẩm!";
      
        if (pn.getTongTien() < 0) return "Tổng tiền không hợp lệ!";

        if (pnDAO.insert(pn)) {
            boolean allDetailSuccess = true;
            for (ChiTietPhieuNhapDTO ct : dsChiTiet) {
                if (!ctDAO.insert(ct)) {
                    allDetailSuccess = false;
                    break; 
                }
            }
            
            if (allDetailSuccess) {
                return "Nhập hàng thành công!";
            } else {
                return "Phiếu nhập đã tạo nhưng một số chi tiết bị lỗi!";
            }
        }
        return "Lỗi khi lưu phiếu nhập vào cơ sở dữ liệu!";
    }

    public ArrayList<PhieuNhapDTO> search(String keyword) {
        ArrayList<PhieuNhapDTO> result = new ArrayList<>();
       
        if (keyword == null) return getAll();
        
        String lowercaseKey = keyword.toLowerCase().trim();
        for (PhieuNhapDTO pn : pnDAO.getAll()) {
            
            if (pn.getMaPhieu().toLowerCase().contains(lowercaseKey) || 
                pn.getTenNCC().toLowerCase().contains(lowercaseKey)) {
                result.add(pn);
            }
        }
        return result;
    }
}
