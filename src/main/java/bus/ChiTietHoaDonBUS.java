package bus;

import dao.ChiTietHoaDonDAO;
import entity.ChiTietHoaDon;
import java.util.List;

public class ChiTietHoaDonBUS {

    private ChiTietHoaDonDAO dao;

    public ChiTietHoaDonBUS() {
        dao = new ChiTietHoaDonDAO();
    }

    public List<ChiTietHoaDon> getAll() {
        return dao.getAll();
    }

    public List<ChiTietHoaDon> getAllByMaHD(String maHD) {
        if(maHD == null || maHD.isEmpty()) {
            throw new RuntimeException("Mã hóa đơn không hợp lệ");
        }
        return dao.getAllByMaHD(maHD);
    }

    public boolean add(ChiTietHoaDon cthd) {

        if(cthd == null) {
            throw new RuntimeException("Chi tiết hóa đơn không hợp lệ");
        }

        if(cthd.getSoluong() <= 0) {
            throw new RuntimeException("Số lượng phải lớn hơn 0");
        }

        if(cthd.getDongia() < 0) {
            throw new RuntimeException("Đơn giá không hợp lệ");
        }

        // tính thành tiền
        double thanhTien = cthd.getSoluong() * cthd.getDongia();
        cthd.setThanhtien(thanhTien);

        return dao.insert(cthd);
    }

    public boolean update(ChiTietHoaDon cthd) {

        if(cthd == null) {
            throw new RuntimeException("Chi tiết hóa đơn không hợp lệ");
        }

        double thanhTien = cthd.getSoluong() * cthd.getDongia();
        cthd.setThanhtien(thanhTien);

        return dao.update(cthd);
    }

    public Object[] getTopSanPham(int limit) {
        return dao.getTopSanPham(limit);
    }

    public ChiTietHoaDon getById(String maCTHD) {
        return dao.getById(maCTHD);
    }
}