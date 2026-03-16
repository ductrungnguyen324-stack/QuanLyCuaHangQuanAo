package bus;

import dao.*;
import entity.*;
import java.util.ArrayList;
import java.util.List;

public class SanPhamBUS {
    SanPhamDAO spDAO = new SanPhamDAO();

    public List<SanPham> getAll() {
        return spDAO.getALL();
    }

    public boolean add(SanPham sp) {
        // Không check maSP vì DAO tự generate qua generateSP()
        if (sp.getTensp() == null || sp.getTensp().trim().isEmpty()) {
            System.out.println("Tên sản phẩm không được rỗng");
            return false;
        }
        if (sp.getGiaban() <= 0) {
            System.out.println("Giá bán phải lớn hơn 0");
            return false;
        }
        return spDAO.insert(sp);
    }

    public boolean update(SanPham sp) {
        if (sp.getMasp() == null || sp.getMasp().trim().isEmpty()) return false;
        return spDAO.update(sp);
    }

    public SanPham getById(String maSP) {
        return spDAO.getById(maSP);
    }

    public Object[] getTonKhoTheoLoai() {
        return spDAO.getTonKhoTheoLoai();
    }

    public void giamTonKho(String maSP, int soLuong) {
        SanPham sp = spDAO.getById(maSP);
        if (sp == null) throw new RuntimeException("Không tìm thấy SP: " + maSP);
        int tonMoi = sp.getTonkho() - soLuong;
        if (tonMoi < 0) throw new RuntimeException("Sản phẩm " + maSP + " không đủ tồn kho!");
        sp.setTonkho(tonMoi);
        spDAO.update(sp);
    }
     public ArrayList<String> getThongTinSP(String maSP) {
        return spDAO.getThongTinSP(maSP);
    }

}