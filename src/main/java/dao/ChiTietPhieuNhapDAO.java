package dao;

import entity.ChiTietPhieuNhapDTO;
import entity.PhieuNhapHangDTO;
import java.sql.*;
import java.util.ArrayList;

public class ChiTietPhieuNhapDAO {

    public ArrayList<ChiTietPhieuNhapDTO> getByMaPN(String maPN) {
        ArrayList<ChiTietPhieuNhapDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM chitietphieunhap WHERE maPN = ?";

        try (Connection con = MyConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maPN);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new ChiTietPhieuNhapDTO(
                        rs.getString("maCTPN"),
                        rs.getString("maPN"),
                        rs.getString("maSP"),
                        rs.getInt("soluong"),
                        rs.getDouble("gianhap"),
                        rs.getDouble("thanhtien")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(ChiTietPhieuNhapDTO ct, Connection conn) {
        String sql = "INSERT INTO chitietphieunhap (maPN, maSP, soluong, gianhap, thanhtien) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ct.getMaPN());
            ps.setString(2, ct.getMaSP());
            ps.setInt(3, ct.getSoLuong());
            ps.setDouble(4, ct.getDonGia());
            ps.setDouble(5, ct.getThanhTien());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteByMaPN(String maPN, Connection conn) {
        String sql = "DELETE FROM chitietphieunhap WHERE maPN = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPN);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getMaMax() {
        String sql = "SELECT MAX(maCTPN) FROM chitietphieunhap";
        try (Connection con = MyConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean update(PhieuNhapHangDTO pn, Connection conn) {
        String sql = "UPDATE phieunhaphang SET maNV = ?, nhacungcap = ?, tongtien = ?, trangthai = ? WHERE maPN = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pn.getMaNV());
            ps.setString(2, pn.getNhaCungCap());
            ps.setDouble(3, pn.getTongTien());
            ps.setString(4, pn.getTrangThai());
            ps.setString(5, pn.getMaPN());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
