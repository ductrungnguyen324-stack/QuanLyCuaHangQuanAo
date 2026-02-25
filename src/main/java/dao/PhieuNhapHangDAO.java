package DAO;

import DTO.PhieuNhapDTO;
import java.sql.*;
import java.util.ArrayList;

public class PhieuNhapDAO {

    public ArrayList<PhieuNhapDTO> getAll() {
        ArrayList<PhieuNhapDTO> ds = new ArrayList<>();
        String sql = "SELECT p.*, n.TenNCC, k.TenKho FROM PhieuNhap p " +
                     "JOIN NhaCungCap n ON p.MaNCC = n.MaNCC " +
                     "JOIN KhoHang k ON p.MaKho = k.MaKho";
        
        try (Connection con = MyConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            while (rs.next()) {
                PhieuNhapDTO pn = new PhieuNhapDTO();
                pn.setMaPhieu(rs.getString("MaPhieu"));
                pn.setTenNCC(rs.getString("TenNCC"));
                pn.setTenKho(rs.getString("TenKho"));
                pn.setNgayDat(rs.getDate("NgayDat"));
                pn.setTongTien(rs.getDouble("TongTien"));
                pn.setDaThanhToan(rs.getDouble("DaThanhToan"));
                pn.setTrangThai(rs.getString("TrangThai"));
                pn.setTtThanhToan(rs.getString("TTThanhToan"));
                ds.add(pn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public boolean insert(PhieuNhapDTO pn) {
        String sql = "INSERT INTO PhieuNhap (MaPhieu, MaNCC, MaKho, NgayDat, TongTien, DaThanhToan, TrangThai, TTThanhToan) VALUES (?,?,?,?,?,?,?,?)";
        
        try (Connection con = MyConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, pn.getMaPhieu());
            ps.setString(2, pn.getMaNCC());
            ps.setString(3, pn.getMaKho());
            
            if (pn.getNgayDat() != null) {
                ps.setDate(4, new java.sql.Date(pn.getNgayDat().getTime()));
            } else {
                ps.setNull(4, Types.DATE);
            }
            
            ps.setDouble(5, pn.getTongTien());
            ps.setDouble(6, pn.getDaThanhToan());
            ps.setString(7, pn.getTrangThai());
            ps.setString(8, pn.getTtThanhToan());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
