package dao;

import entity.ChiTietPhieuNhapDTO;
import java.sql.*;
import java.util.ArrayList;

public class ChiTietPhieuNhapDAO {

    // ── Lấy chi tiết theo mã phiếu nhập ─────────────────
    public ArrayList<ChiTietPhieuNhapDTO> getByMaPN(String maPN) {
        ArrayList<ChiTietPhieuNhapDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM chitietphieunhap WHERE maPN = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPN);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ChiTietPhieuNhapDTO ct = new ChiTietPhieuNhapDTO();
                    ct.setMaCTPN(rs.getString("maCTPN"));
                    ct.setMaPN(rs.getString("maPN"));
                    ct.setMaSP(rs.getString("maSP"));
                    ct.setSoLuong(rs.getInt("soluong"));
                    ct.setDonGia(rs.getDouble("gianhap"));
                    ct.setThanhTien(rs.getDouble("thanhtien"));
                    list.add(ct);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DAO] getByMaPN() lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // ── Thêm 1 chi tiết (dùng connection chung cho transaction) ─
    public boolean insert(ChiTietPhieuNhapDTO ct, Connection conn) {
        String sql = "INSERT INTO chitietphieunhap (maCTPN, maPN, maSP, soluong, gianhap, thanhtien) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ct.getMaCTPN());
            ps.setString(2, ct.getMaPN());
            ps.setString(3, ct.getMaSP());
            ps.setInt(4, ct.getSoLuong());
            ps.setDouble(5, ct.getDonGia());    // map vào cột gianhap
            ps.setDouble(6, ct.getThanhTien());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DAO-CT] lỗi: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ── Xoá toàn bộ chi tiết theo mã phiếu ──────────────
    public boolean deleteByMaPN(String maPN, Connection conn) {
        String sql = "DELETE FROM chitietphieunhap WHERE maPN = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPN);
            ps.executeUpdate(); // Không check > 0 vì có thể phiếu chưa có chi tiết
            return true;
        } catch (SQLException e) {
            System.out.println("[DAO] deleteByMaPN() chitiet lỗi: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ── Lấy mã CTPN lớn nhất để sinh mã mới ─────────────
    public String getLastMaCTPN() {
        String sql = "SELECT maCTPN FROM chitietphieunhap ORDER BY maCTPN DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getString("maCTPN");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }
}
