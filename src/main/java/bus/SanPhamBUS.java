package bus;

import dao.*;
import entity.*;
import java.util.List;

public class SanPhamBUS {
    SanPhamDAO spDAO = new SanPhamDAO();

    public List<SanPham> getAll() {
        return spDAO.getALL();
    }

    public boolean add(SanPham sp) {
        if(sp.getMasp() == null || sp.getMasp().isEmpty()) {
            System.out.println("Ma san pham khong duoc rong");
            return false;
        }
        if(sp.getTensp() == null || sp.getTensp().isEmpty()) {
            System.out.println("Ten san pham khong duoc rong");
            return false;
        }

        return spDAO.insert(sp);
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
    
    public String[] getThongTinSP(String maSP) {
        return spDAO.getThongTinSP(maSP);
    }

    public boolean update(SanPham sp) {
        return spDAO.update(sp);
    }

    public SanPham getById(String maSP) {
        return spDAO.getById(maSP);
    }
    //thêm
     public void tangTonKho(String maSP, int soLuong) {
        if (soLuong <= 0) throw new RuntimeException("Số lượng tăng phải > 0, maSP: " + maSP);
        if (spDAO.getById(maSP) == null) throw new RuntimeException("Không tìm thấy SP: " + maSP);
        if (!spDAO.tangTonKho(maSP, soLuong)) {
            throw new RuntimeException("Cập nhật tồn kho thất bại, maSP: " + maSP);
        }
    }
}