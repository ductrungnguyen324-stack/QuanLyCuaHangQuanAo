package dao;

import entity.PhieuNhapHangDTO;
import java.sql.*;
import java.util.ArrayList;

public class PhieuNhapHangDAO {

    private static PhieuNhapHangDAO instance;

    public static PhieuNhapHangDAO getInstance() {
        if (instance == null) {
            instance = new PhieuNhapHangDAO();
        }
        return instance;
    }

    public ArrayList<PhieuNhapHangDTO> getAll() {
        ArrayList<PhieuNhapHangDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM phieunhap ORDER BY ngaytao DESC";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                PhieuNhapHangDTO pn = new PhieuNhapHangDTO();
                pn.setMaPN(rs.getString("maPN"));
                pn.setMaNV(rs.getString("maNV"));
                pn.setNhaCungCap(rs.getString("nhacungcap"));
                pn.setNgayTao(rs.getTimestamp("ngaytao"));
                pn.setTongTien(rs.getDouble("thanhtien"));
                pn.setTrangThai(rs.getString("trangthai"));
                list.add(pn);
            }
        } catch (SQLException e) {
            System.out.println("[DAO] getAll() lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /*
     */
    public boolean insert(PhieuNhapHangDTO pn, Connection conn) {
        String sql = "INSERT INTO phieunhap (maPN, maNV, nhacungcap, ngaytao, thanhtien, trangthai) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pn.getMaPN());
            ps.setString(2, pn.getMaNV());
            ps.setString(3, pn.getNhaCungCap());
            ps.setTimestamp(4, new java.sql.Timestamp(pn.getNgayTao().getTime()));
            ps.setDouble(5, pn.getTongTien());
            ps.setString(6, pn.getTrangThai());

            // ← Thêm log này
            System.out.println("[DAO] SQL: " + ps.toString());
            System.out.println("[DAO] maPN=" + pn.getMaPN());
            System.out.println("[DAO] maNV=" + pn.getMaNV());
            System.out.println("[DAO] ncc=" + pn.getNhaCungCap());
            System.out.println("[DAO] ngayTao=" + pn.getNgayTao());
            System.out.println("[DAO] tongTien=" + pn.getTongTien());
            System.out.println("[DAO] trangThai=" + pn.getTrangThai());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTrangThai(String maPN, String trangThai) {
        String sql = "UPDATE phieunhap SET trangthai = ? WHERE maPN = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, trangThai);
            ps.setString(2, maPN);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DAO] updateTrangThai() lỗi: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String maPN, Connection conn) {
        String sql = "DELETE FROM phieunhap WHERE maPN = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPN);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DAO] delete() lỗi: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public PhieuNhapHangDTO getPhieuNhapById(String maPN) {
        String sql = "SELECT * FROM phieunhap WHERE maPN = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPN);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new PhieuNhapHangDTO(
                            rs.getString("maPN"),
                            rs.getString("maNV"),
                            rs.getString("nhacungcap"),
                            rs.getTimestamp("ngaytao"),
                            rs.getDouble("thanhtien"),
                            rs.getString("trangthai")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("[DAO] getPhieuNhapById() lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean update(PhieuNhapHangDTO pn, Connection conn) throws SQLException {
        String sql = "UPDATE phieunhap SET maNV = ?, nhacungcap = ?, thanhtien = ?, trangthai = ? "
                + "WHERE maPN = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pn.getMaNV());
            ps.setString(2, pn.getNhaCungCap());
            ps.setDouble(3, pn.getTongTien());
            ps.setString(4, pn.getTrangThai());
            ps.setString(5, pn.getMaPN());
            return ps.executeUpdate() > 0;
        }
    }

    public String getLastMaPhieuNhap() {
       //sql server: String sql = "SELECT TOP 1 maPN FROM phieunhap ORDER BY maPN DESC";
       String sql = "SELECT maPN FROM phieunhap ORDER BY maPN DESC LIMIT 1";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getString("maPN");
            }
        } catch (SQLException e) {
            System.out.println("[DAO] getLastMaPhieuNhap() lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }
}
