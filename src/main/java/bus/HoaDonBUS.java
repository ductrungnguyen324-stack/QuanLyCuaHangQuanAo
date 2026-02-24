package bus;

import dao.*;
import entity.*;

import java.time.LocalDateTime;
import java.util.List;

public class HoaDonBUS {
    private HoaDonDAO hoaDonDAO = new HoaDonDAO();

    // Lấy tất cả
    public List<HoaDon> getAll() {
        return hoaDonDAO.getAll();
    }

    // Thêm hóa đơn
    public boolean add(HoaDon hd) {

        if (hd.getMaKH() == null || hd.getMaKH().isEmpty()) {
            System.out.println("Mã khách hàng không được rỗng");
            return false;
        }

        if (hd.getMaNV() == null || hd.getMaNV().isEmpty()) {
            System.out.println("Mã nhân viên không được rỗng");
            return false;
        }

        if (hd.getNgaytao() == null) {
            hd.setNgaytao(LocalDateTime.now());
        }

        return hoaDonDAO.insert(hd);
    }

    public boolean update(HoaDon hd) {
        return hoaDonDAO.update(hd);
    }

    public HoaDon getById(String maHD) {
        return hoaDonDAO.getById(maHD);
    }
}