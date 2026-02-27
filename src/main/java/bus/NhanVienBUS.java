package bus;

import dao.NhanVienDAO;
import entity.NhanVien;
import java.util.ArrayList;

public class NhanVienBUS {
    private ArrayList<NhanVien> dsNV;
    private NhanVienDAO nvDAO = new NhanVienDAO();

    public NhanVienBUS() {
        taiDuLieu();
    }

    // Làm mới danh sách từ Database
    public void taiDuLieu() {
        dsNV = nvDAO.getListNhanVien();
        // Cập nhật số lượng vào biến static của lớp NhanVien
        NhanVien.setSoluong(dsNV.size());
    }

    // --- HÀM HIỂN THỊ DÀNH CHO CONSOLE ---
    public void hienThi() {
        if (dsNV == null || dsNV.isEmpty()) {
            System.out.println("Danh sách nhân viên trống!");
            return;
        }

        System.out.println("\n========= DANH SÁCH NHÂN VIÊN =========");
        // Định dạng cột: %-10s (căn trái 10 khoảng trắng), %-25s (căn trái 25 khoảng trắng)...
        System.out.printf("%-10s %-25s %-15s %-15s%n", "Mã NV", "Họ Tên", "SĐT", "Chức Vụ");
        System.out.println("------------------------------------------------------------------");

        for (NhanVien nv : dsNV) {
            System.out.printf("%-10s %-25s %-15s %-15s%n",
                    nv.getManv(),
                    nv.getHoten(),
                    nv.getSdt(),
                    nv.getChucvu());
        }

        System.out.println("------------------------------------------------------------------");
        System.out.println("Tổng cộng: " + NhanVien.getSoluong() + " nhân viên.");
    }

    public ArrayList<NhanVien> getDsNV() {
        return dsNV;
    }

    // --- CÁC TÍNH NĂNG NGHIỆP VỤ KHÁC ---

    public String them(NhanVien nv) {
        if (nv.getManv().trim().isEmpty()) return "Mã nhân viên không được để trống!";
        for (NhanVien x : dsNV) {
            if (x.getManv().equalsIgnoreCase(nv.getManv())) return "Mã nhân viên đã tồn tại!";
        }
        if (nvDAO.insert(nv)) {
            taiDuLieu();
            return "Thêm thành công!";
        }
        return "Thêm thất bại!";
    }

    public String xoa(String maNV) {
        if (nvDAO.delete(maNV)) {
            taiDuLieu();
            return "Xóa thành công!";
        }
        return "Không tìm thấy mã nhân viên để xóa!";
    }

    public String sua(NhanVien nv) {
        if (nvDAO.update(nv)) {
            taiDuLieu();
            return "Cập nhật thành công!";
        }
        return "Cập nhật thất bại!";
    }

    public ArrayList<NhanVien> timKiem(String tuKhoa) {
        ArrayList<NhanVien> kq = new ArrayList<>();
        String key = tuKhoa.toLowerCase();
        for (NhanVien nv : dsNV) {
            if (nv.getManv().toLowerCase().contains(key) ||
                    nv.getHoten().toLowerCase().contains(key)) {
                kq.add(nv);
            }
        }
        return kq;
    }
}