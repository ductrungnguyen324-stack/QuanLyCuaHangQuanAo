package dao;

import entity.NhanVien;
import java.sql.*;
import java.util.ArrayList;

public class NhanVienDAO extends DBConnection {

    // 1. Lấy toàn bộ danh sách nhân viên
    public ArrayList<NhanVien> getListNhanVien() {
        ArrayList<NhanVien> dsNV = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien";
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                dsNV.add(new NhanVien(
                        rs.getString("MaNV"),
                        rs.getString("TenNV"),
                        rs.getString("SDT"),
                        rs.getString("ChucVu")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getListNhanVien: " + e.getMessage());
        }
        return dsNV;
    }

    // 2. Thêm nhân viên mới
    public boolean insert(NhanVien nv) {
        String sql = "INSERT INTO NhanVien (MaNV, TenNV, SDT, ChucVu) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nv.getManv());
            ps.setString(2, nv.getHoten());
            ps.setString(3, nv.getSdt());
            ps.setString(4, nv.getChucvu());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 3. Xóa nhân viên theo mã
    public boolean delete(String maNV) {
        String sql = "DELETE FROM NhanVien WHERE MaNV = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNV);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4. Cập nhật thông tin nhân viên
    public boolean update(NhanVien nv) {
        String sql = "UPDATE NhanVien SET TenNV = ?, SDT = ?, ChucVu = ? WHERE MaNV = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nv.getHoten());
            ps.setString(2, nv.getSdt());
            ps.setString(3, nv.getChucvu());
            ps.setString(4, nv.getManv());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}