package bus;

import dao.NhanVienDAO;
import entity.NhanVien;
import java.util.List;

public class NhanVienBUS {
    private NhanVienDAO nvDAO = new NhanVienDAO();

    // Lay tat ca nhan vien truc tiep tu DAO
    public List<NhanVien> getAll() {
        return nvDAO.getAll();
    }

    // Them nhan vien voi kiem tra logic
    public boolean add(NhanVien nv) {
        // Kiem tra ma nhan vien khong duoc rong
        if (nv.getManv() == null || nv.getManv().trim().isEmpty()) {
            System.out.println("Ma nhan vien khong duoc rong!");
            return false;
        }

        // Kiem tra ho ten khong duoc rong
        if (nv.getHoten() == null || nv.getHoten().trim().isEmpty()) {
            System.out.println("Ho ten nhan vien khong duoc rong!");
            return false;
        }

        // Kiem tra trung ma (tuong tu logic mau HoaDon)
        if (getById(nv.getManv()) != null) {
            System.out.println("Ma nhan vien da ton tai!");
            return false;
        }

        return nvDAO.insert(nv);
    }

    // Cap nhat thong tin nhan vien
    public boolean update(NhanVien nv) {
        if (nv.getManv() == null || nv.getManv().trim().isEmpty()) {
            return false;
        }
        return nvDAO.update(nv);
    }

    // Xoa nhan vien
    public boolean delete(String maNV) {
        if (maNV == null || maNV.trim().isEmpty()) {
            return false;
        }
        return nvDAO.delete(maNV);
    }

    // Tim kiem nhan vien theo ma (getById) theo mau HoaDon
    public NhanVien getById(String maNV) {
        return nvDAO.getById(maNV);
    }

    // Ham dang nhap goi tu DAO
    public NhanVien Login(String username, String password) {
        return nvDAO.checkLogin(username, password);
    }
}