

package DAO;

import entity.PhieuNhapHang;
import java.sql.*;
import java.util.ArrayList;

public class PhieuNhapHangDAO {
    public ArrayList<PhieuNhapHang> getAll() {
        ArrayList<PhieuNhapHang> ds = new ArrayList<>();
        String sql = "SELECT * FROM PhieuNhapHang";
        try (Connection con = MyConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PhieuNhapHang pn = new PhieuNhapHang();
                pn.setMaPN(rs.getString("maPN"));
                pn.setMaNV(rs.getString("maNV"));
                pn.setNhacungcap(rs.getString("nhacungcap"));
                pn.setNgaytao(rs.getTimestamp("ngaytao").toLocalDateTime());
                pn.setThanhtien(rs.getDouble("thanhtien"));
                pn.setTrangthai(rs.getString("trangthai"));
                ds.add(pn);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return ds;
    }

    public boolean insert(PhieuNhapHang pn) {
        String sql = "INSERT INTO PhieuNhapHang VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = MyConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, pn.getMaPN());
            ps.setString(2, pn.getMaNV());
            ps.setString(3, pn.getNhacungcap());
            ps.setTimestamp(4, Timestamp.valueOf(pn.getNgaytao()));
            ps.setDouble(5, pn.getThanhtien());
            ps.setString(6, pn.getTrangthai());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
