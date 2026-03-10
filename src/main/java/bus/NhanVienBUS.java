package bus;

import dao.NhanVienDAO;
import entity.NhanVien;
import java.util.List;

public class NhanVienBUS {
    private NhanVienDAO nvDAO = new NhanVienDAO();

    // Lấy tất cả nhân viên trực tiếp từ DAO
    public List<NhanVien> getAll() {
        return nvDAO.getAll();
    }

    // Thêm nhân viên với kiểm tra logic
    public boolean add(NhanVien nv) {
        // Kiểm tra mã nhân viên không được rỗng
        if (nv.getManv() == null || nv.getManv().trim().isEmpty()) {
            System.out.println("Mã nhân viên không được rỗng!");
            return false;
        }

        // Kiểm tra họ tên không được rỗng
        if (nv.getHoten() == null || nv.getHoten().trim().isEmpty()) {
            System.out.println("Họ tên nhân viên không được rỗng!");
            return false;
        }

        // Kiểm tra trùng mã (tương tự logic mẫu HoaDon)
        if (getById(nv.getManv()) != null) {
            System.out.println("Mã nhân viên đã tồn tại!");
            return false;
        }

        return nvDAO.insert(nv);
    }

    // Cập nhật thông tin nhân viên
    public boolean update(NhanVien nv) {
        if (nv.getManv() == null || nv.getManv().trim().isEmpty()) {
            return false;
        }
        return nvDAO.update(nv);
    }

    // Xóa nhân viên
    public boolean delete(String maNV) {
        if (maNV == null || maNV.trim().isEmpty()) {
            return false;
        }
        return nvDAO.delete(maNV);
    }

    // Tìm kiếm nhân viên theo mã (getById) theo mẫu HoaDon
    public NhanVien getById(String maNV) {
        return nvDAO.getById(maNV);
    }
}