package bus;

import dao.NhanVienDAO;
import entity.NhanVien;
import java.util.ArrayList;

public class NhanVienBUS {
    private ArrayList<NhanVien> dsNV;
    private NhanVienDAO nvDAO;

    public NhanVienBUS() {
        nvDAO = new NhanVienDAO();
        dsNV = nvDAO.getAll();
    }

    public ArrayList<NhanVien> getDsNV() {
        return dsNV;
    }

    public String add(NhanVien nv) {
        // Kiểm tra logic: không được trùng mã
        for (NhanVien item : dsNV) {
            if (item.getManv().equalsIgnoreCase(nv.getManv())) {
                return "Mã nhân viên đã tồn tại!";
            }
        }
        if (nvDAO.insert(nv)) {
            dsNV.add(nv);
            return "Thêm thành công!";
        }
        return "Thêm thất bại!";
    }

    public String update(NhanVien nv) {
        if (nvDAO.update(nv)) {
            // Cập nhật lại trong danh sách bộ nhớ
            for (int i = 0; i < dsNV.size(); i++) {
                if (dsNV.get(i).getManv().equals(nv.getManv())) {
                    dsNV.set(i, nv);
                    break;
                }
            }
            return "Cập nhật thành công!";
        }
        return "Cập nhật thất bại!";
    }

    public String delete(String maNV) {
        if (nvDAO.delete(maNV)) {
            dsNV.removeIf(nv -> nv.getManv().equals(maNV));
            return "Xóa thành công!";
        }
        return "Xóa thất bại!";
    }

    public ArrayList<NhanVien> search(String keyword) {
        ArrayList<NhanVien> result = new ArrayList<>();
        for (NhanVien nv : dsNV) {
            if (nv.getManv().contains(keyword) || nv.getHoten().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(nv);
            }
        }
        return result;
    }

}