package DAO;

import DTO.ChiTietPhieuNhapDTO;
import java.sql.*;

public class ChiTietPhieuNhapDAO {

    public boolean insert(ChiTietPhieuNhapDTO ct) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = MyConnection.getConnection();
            con.setAutoCommit(false); 
            String sql = "INSERT INTO ChiTietPhieuNhap (MaPhieu, MaSP, SoLuong, DonGia) VALUES (?,?,?,?)";
            ps = con.prepareStatement(sql);
            ps.setString(1, ct.getMaPhieu());
            ps.setString(2, ct.getMaSP());
            ps.setInt(3, ct.getSoLuong());
            ps.setDouble(4, ct.getDonGia());
            int result = ps.executeUpdate();
            updateStock(con, ct.getMaSP(), ct.getSoLuong());
            con.commit(); 
            return result > 0;

        } catch (Exception e) {
            try {
                if (con != null) con.rollback(); 
            } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    private void updateStock(Connection con, String maSP, int soLuong) throws SQLException {
        String sql = "UPDATE SanPham SET SoLuongTon = SoLuongTon + ? WHERE MaSP = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, soLuong);
            ps.setString(2, maSP);
            ps.executeUpdate();
        }
    }
}
