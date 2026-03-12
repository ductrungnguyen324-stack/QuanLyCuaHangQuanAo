package bus;

import dao.*;
import entity.*;

import java.time.LocalDateTime;
import java.util.List;

public class HoaDonBUS {
    private HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private ChiTietHoaDonDAO chiTietDAO = new ChiTietHoaDonDAO();

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

    public boolean delete(String maHD) {

        HoaDon hd = hoaDonDAO.getById(maHD);

        if (hd == null) {
            throw new RuntimeException("Hóa đơn không tồn tại");
        }

        if ("Đã thanh toán".equalsIgnoreCase(hd.getTrangthai())) {
            throw new RuntimeException("Không thể xóa hóa đơn đã thanh toán");
        }

        // xóa chi tiết hóa đơn trước
        chiTietDAO.delete(maHD);

        // xóa hóa đơn
        return hoaDonDAO.delete(maHD);
    }

    public boolean update(HoaDon hd) {
        return hoaDonDAO.update(hd);
    }

    public HoaDon getById(String maHD) {
        return hoaDonDAO.getById(maHD);
    }


    public static void main(String[] args) {
        HoaDonBUS hdbus = new HoaDonBUS();
        List<HoaDon> list = hdbus.getAll();

        for(HoaDon hd : list) {
            System.out.println(hd.getMaHD());
        }
    }
}