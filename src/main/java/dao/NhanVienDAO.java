package dao;

import entity.NhanVien;
import java.sql.*;
import java.util.ArrayList;

public class NhanVienDAO {
    private Connection conn;

    public NhanVienDAO() {
        this.conn = DBConnection.getConnection();

        if (this.conn == null) {
            System.out.println(">>> LỖI: Không thể lấy kết nối từ DBConnection. Kiểm tra lại PASS và DATABASE!");
        }
    }

    public ArrayList<NhanVien> getAll() {
        ArrayList<NhanVien> ds = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                NhanVien nv = new NhanVien(
                        rs.getString("maNV"),
                        rs.getString("hoten"),
                        rs.getString("sdt"),
                        rs.getString("chucvu"),
                        rs.getString("tendangnhap"),
                        rs.getString("matkhau"),
                        rs.getString("trangthai")
                );
                ds.add(nv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public boolean insert(NhanVien nv) {
        String sql = "INSERT INTO NhanVien (maNV, hoten, sdt, chucvu, tendangnhap, matkhau, trangthai) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nv.getManv());
            ps.setString(2, nv.getHoten());
            ps.setString(3, nv.getSdt());
            ps.setString(4, nv.getChucvu());
            ps.setString(5, nv.getTendannhap());
            ps.setString(6, nv.getMatkhau());
            ps.setString(7, nv.getTrangthai());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(NhanVien nv) {
        String sql = "UPDATE NhanVien SET hoten=?, sdt=?, chucvu=?, tendangnhap=?, matkhau=?, trangthai=? WHERE maNV=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
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
            return false;
        }
    }

    public boolean delete(String maNV) {
        String sql = "DELETE FROM NhanVien WHERE maNV=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNV);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}