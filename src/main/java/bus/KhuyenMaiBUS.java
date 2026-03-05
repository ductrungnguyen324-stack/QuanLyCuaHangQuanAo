package bus;

import dao.KhuyenMaiDAO;
import entity.KhuyenMai;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class KhuyenMaiBUS
{
    final KhuyenMaiDAO kmDAO = new KhuyenMaiDAO();

    public List<KhuyenMai> getAll()
    {
        return kmDAO.getAll();
    }

    public KhuyenMai getByID(String makhuyemai)
    {
        return kmDAO.getByID(makhuyemai);
    }

    public boolean addKhuyenMai(KhuyenMai km) throws Exception
    {
        if (km.getTenKM().trim().isEmpty())
            throw new Exception("Tên khuyến mãi không được để trống!");
        if (km.getGiatrigiam() <= 0)
            throw new Exception("Giá trị giảm phải lớn hơn 0!");
        if (km.getNgayketthuc().isBefore(km.getNgaybatdau()))
            throw new Exception("Ngày kết thúc phải sau ngày bắt đầu!");
        if (km.getSoluong() <= 0)
            throw new Exception("Số lượng phải lớn hơn 0!");
        return kmDAO.insert(km);
    }

    public boolean updateKhuyenMai(KhuyenMai km) throws Exception
    {
        if (km.getTenKM().trim().isEmpty())
            throw new Exception("Tên khuyến mãi không được để trống!");
        if (km.getGiatrigiam() <= 0)
            throw new Exception("Giá trị giảm phải lớn hơn 0!");
        if (km.getNgayketthuc().isBefore(km.getNgaybatdau()))
            throw new Exception("Ngày kết thúc phải sau ngày bắt đầu!");
        if (km.getSoluong() <= 0)
            throw new Exception("Số lượng phải lớn hơn 0!");
        return kmDAO.update(km);
    }

    public boolean deleteKhuyeMai(String makhuyenmai) throws Exception
    {
        KhuyenMai km = kmDAO.getByID(makhuyenmai);
        if (km.getTenKM().trim().isEmpty())
            throw new Exception("Tên khuyến mãi không được để trống!");
        if (km.getDasudung() > 0)
            throw  new Exception("Mã này đã có lượt sử dụng, không thể xóa để đảm bảo thống kê!");
        return kmDAO.delete(makhuyenmai);
    }

    public List<KhuyenMai> getActivePromotions() {
        return kmDAO.getActivePromotions();
    }

    public boolean updateUsageCount(String makhuyemai, int amount) {
        return kmDAO.updateUsageCount(makhuyemai, amount);
    }
}