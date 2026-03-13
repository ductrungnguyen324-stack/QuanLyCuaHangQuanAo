package bus;

import dao.*;
import entity.*;
import java.util.List;

public class NhaCungCapBUS {

    private NhaCungCapDAO nhaCungCapDAO = new NhaCungCapDAO();

    // Lấy tất cả nhà cung cấp
    public List<NhaCungCap> getAll() {
        return nhaCungCapDAO.getAll();
    }

    // Thêm nhà cung cấp
    public boolean add(NhaCungCap ncc) {

        if (ncc.getTenNCC() == null || ncc.getTenNCC().isEmpty()) {
            System.out.println("Tên nhà cung cấp không được rỗng");
            return false;
        }

        if (ncc.getSodienthoai() == null || ncc.getSodienthoai().isEmpty()) {
            System.out.println("Số điện thoại không được rỗng");
            return false;
        }
        return nhaCungCapDAO.insert(ncc);
    }

    // Cập nhật nhà cung cấp
    public boolean update(NhaCungCap ncc) {
        return nhaCungCapDAO.update(ncc);
    }

    // Lấy theo mã
    public NhaCungCap getById(String maNCC) {
        return nhaCungCapDAO.getById(maNCC);
    }
}