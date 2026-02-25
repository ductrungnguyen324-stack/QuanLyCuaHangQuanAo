package DAO;

import entity.ChiTietPhieuNhap;
import java.sql.*;

public class ChiTietPhieuNhapDAO {
    public boolean insert(ChiTietPhieuNhap ct) {
        Connection con = null;
        try {
            con = MyConnection.getConnection();
            con.setAutoCommit(false); // Bắt đầu giao dịch (Transaction)

            // 1. Thêm chi tiết phiếu nhập
            String sqlInsert = "INSERT INTO ChiTietPhieuNhap VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sqlInsert)) {
                ps.setString(1, ct.getMaCTPN());
                ps.setString(2, ct.getMaPN());
                ps.setString(3, ct.getMaSP());
                ps.setDouble(4, ct.getSoluong());
                ps.setDouble(5, ct.getGianhap());
                ps.setDouble(6, ct.getThanhtien());
                ps.executeUpdate();
            }

            // 2. Cập nhật tồn kho trong bảng SanPham
            String sqlUpdateStock = "UPDATE SanPham SET tonkho = tonkho + ? WHERE maSP = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlUpdateStock)) {
                ps.setDouble(1, ct.getSoluong());
                ps.setString(2, ct.getMaSP());
                ps.executeUpdate();
            }

            con.commit();
            return true;
        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}
