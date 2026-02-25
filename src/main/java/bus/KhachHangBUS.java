package bus;

import dao.KhachHangDAO;
import entity.KhachHang;

import java.time.LocalDate;
import java.util.ArrayList;

public class KhachHangBUS {

    private final KhachHangDAO khDAO = new KhachHangDAO();

    public ArrayList<KhachHang> getAll() {
        return khDAO.getAll();
    }


    public KhachHang findById(String maKH) {
        if (maKH == null || maKH.trim().isEmpty())
            return null;

        return khDAO.findById(maKH);
    }


    public KhachHang findByPhone(String sdt) {
        if (!isValidPhone(sdt))
            return null;

        return khDAO.findByPhone(sdt);
    }


    public boolean add(KhachHang kh) {

        // 1. Validate dữ liệu
        if (!isValid(kh))
            return false;

        // 2. Kiểm tra trùng mã
        if (khDAO.findById(kh.getMaKH()) != null)
            return false;

        // 3. Kiểm tra trùng SĐT
        if (khDAO.findByPhone(kh.getSdt()) != null)
            return false;

        // 4. Nếu ngày tham gia null thì set hôm nay
        if (kh.getNgaythamgia() == null) {
            kh.setNgaythamgia(LocalDate.now());
        }

        return khDAO.insert(kh) > 0;
    }


    public boolean update(KhachHang kh) {

        if (!isValid(kh))
            return false;

        // Kiểm tra tồn tại trước khi update
        if (khDAO.findById(kh.getMaKH()) == null)
            return false;

        return khDAO.update(kh) > 0;
    }


    public boolean delete(String maKH) {

        if (maKH == null || maKH.trim().isEmpty())
            return false;

        if (khDAO.findById(maKH) == null)
            return false;

        return khDAO.delete(maKH) > 0;
    }


    private boolean isValid(KhachHang kh) {

        if (kh == null)
            return false;

        if (kh.getMaKH() == null || kh.getMaKH().trim().isEmpty())
            return false;

        if (kh.getHoten() == null || kh.getHoten().trim().isEmpty())
            return false;

        if (!isValidPhone(kh.getSdt()))
            return false;

        return true;
    }

    private boolean isValidPhone(String sdt) {
        return sdt != null && sdt.matches("\\d{10}");
    }
}
