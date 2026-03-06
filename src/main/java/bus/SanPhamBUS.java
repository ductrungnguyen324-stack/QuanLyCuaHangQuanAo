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

    public boolean update(SanPham sp) {
        return spDAO.update(sp);
    }

    public SanPham getById(String maSP) {
        return spDAO.getById(maSP);
    }
}