package dao;

import entity.NhanVien;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO {

    // Lấy tất cả nhân viên
    public List<NhanVien> getAll() {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien ORDER BY maNV ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            System.out.println(">>> Lỗi getAll NhanVien: " + e.getMessage());
        }
        return list;
    }

    // Lấy nhân viên theo mã (Hỗ trợ BUS kiểm tra trùng)
    public NhanVien getById(String maNV) {
        String sql = "SELECT * FROM NhanVien WHERE maNV = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maNV);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Thêm nhân viên
    public boolean insert(NhanVien nv) {
        String sql = "INSERT INTO NhanVien (maNV, hoten, sdt, chucvu, tendangnhap, matkhau, trangthai) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nv.getManv());
            ps.setString(2, nv.getHoten());
            ps.setString(3, nv.getSdt());
            ps.setString(4, nv.getChucvu());
            ps.setString(5, nv.getTendannhap());
            ps.setString(6, nv.getMatkhau());
            ps.setString(7, nv.getTrangthai());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println(">>> Lỗi insert NhanVien: " + e.getMessage());
        }
        return false;
    }

    // Cập nhật nhân viên
    public boolean update(NhanVien nv) {
        // Kiểm tra tồn tại trước khi update (Giống mẫu HoaDon)
        if (getById(nv.getManv()) == null) {
            return false;
        }

        String sql = "UPDATE NhanVien SET hoten=?, sdt=?, chucvu=?, tendangnhap=?, matkhau=?, trangthai=? WHERE maNV=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nv.getHoten());
            ps.setString(2, nv.getSdt());
            ps.setString(3, nv.getChucvu());
            ps.setString(4, nv.getTendannhap());
            ps.setString(5, nv.getMatkhau());
            ps.setString(6, nv.getTrangthai());
            ps.setString(7, nv.getManv());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa nhân viên
    public boolean delete(String maNV) {
        String sql = "DELETE FROM NhanVien WHERE maNV=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maNV);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Hàm ánh xạ ResultSet sang Entity (Giúp code gọn hơn)
    private NhanVien mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new NhanVien(
                rs.getString("maNV"),
                rs.getString("hoten"),
                rs.getString("sdt"),
                rs.getString("chucvu"),
                rs.getString("tendangnhap"),
                rs.getString("matkhau"),
                rs.getString("trangthai")
        );
    }
}