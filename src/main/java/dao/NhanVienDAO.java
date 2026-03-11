package dao;

import entity.NhanVien;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO {

    // Lay tat ca nhan vien
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
            System.out.println(">>> Loi getAll NhanVien: " + e.getMessage());
        }
        return list;
    }

    // Lay nhan vien theo ma (Ho tro BUS kiem tra trung)
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

    // Them nhan vien
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
            System.out.println(">>> Loi insert NhanVien: " + e.getMessage());
        }
        return false;
    }

    // Cap nhat nhan vien
    public boolean update(NhanVien nv) {
        // Kiem tra ton tai truoc khi update (Giong mau HoaDon)
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

    // Xoa nhan vien
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

    // Ham kiem tra dang nhap
    public NhanVien checkLogin(String user, String pass) {
        String sql = "SELECT * FROM NhanVien WHERE tendangnhap = ? AND matkhau = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user);
            ps.setString(2, pass);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Neu tim thay, dung ham map de tao doi tuong NhanVien
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Neu khong tim thay hoac co loi, tra ve null
        return null;
    }

    // Ham anh xa ResultSet sang Entity (Giup code gon hon)
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