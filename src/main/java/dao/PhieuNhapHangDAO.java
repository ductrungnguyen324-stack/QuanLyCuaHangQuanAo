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

    // ── Lấy tất cả phiếu nhập, JOIN nhacungcap để lấy tên hiển thị ──────────
    public ArrayList<PhieuNhapHangDTO> getAll() {
        ArrayList<PhieuNhapHangDTO> list = new ArrayList<>();
        String sql = "SELECT pn.*, ncc.tenNCC "
                + "FROM phieunhap pn "
                + "LEFT JOIN nhacungcap ncc ON pn.nhacungcap = ncc.maNCC "
                + "ORDER BY pn.ngaytao DESC";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                PhieuNhapHangDTO pn = new PhieuNhapHangDTO();
                pn.setMaPN(rs.getString("maPN"));
                pn.setMaNV(rs.getString("maNV"));
                pn.setMaNCC(rs.getString("nhacungcap"));  // mã NCC (FK) — tên cột trong DB là nhacungcap
                pn.setTenNCC(rs.getString("tenNCC"));     // tên NCC để hiển thị (từ JOIN)
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

    // ── Lấy danh sách phiếu nhập theo mã nhà cung cấp ───────────────────────
    public ArrayList<PhieuNhapHangDTO> getByMaNCC(String maNCC) {
        ArrayList<PhieuNhapHangDTO> list = new ArrayList<>();
        String sql = "SELECT pn.*, ncc.tenNCC "
                + "FROM phieunhap pn "
                + "LEFT JOIN nhacungcap ncc ON pn.nhacungcap = ncc.maNCC "
                + "WHERE pn.nhacungcap = ? "
                + "ORDER BY pn.ngaytao DESC";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maNCC);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PhieuNhapHangDTO pn = new PhieuNhapHangDTO();
                    pn.setMaPN(rs.getString("maPN"));
                    pn.setMaNV(rs.getString("maNV"));
                    pn.setMaNCC(rs.getString("nhacungcap"));
                    pn.setTenNCC(rs.getString("tenNCC"));
                    pn.setNgayTao(rs.getTimestamp("ngaytao"));
                    pn.setTongTien(rs.getDouble("thanhtien"));
                    pn.setTrangThai(rs.getString("trangthai"));
                    list.add(pn);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DAO] getByMaNCC() lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // ── Thêm phiếu nhập (dùng connection chung cho transaction) ─────────────
    public boolean insert(PhieuNhapHangDTO pn, Connection conn) {
        String sql = "INSERT INTO phieunhap (maPN, maNV, nhacungcap, ngaytao, thanhtien, trangthai) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pn.getMaPN());
            ps.setString(2, pn.getMaNV());
            ps.setString(3, pn.getMaNCC());               // lưu mã NCC, không lưu tên
            ps.setTimestamp(4, new java.sql.Timestamp(pn.getNgayTao().getTime()));
            ps.setDouble(5, pn.getTongTien());
            ps.setString(6, pn.getTrangThai());

            System.out.println("[DAO] SQL: " + ps.toString());
            System.out.println("[DAO] maPN=" + pn.getMaPN());
            System.out.println("[DAO] maNV=" + pn.getMaNV());
            System.out.println("[DAO] maNCC=" + pn.getMaNCC());
            System.out.println("[DAO] ngayTao=" + pn.getNgayTao());
            System.out.println("[DAO] tongTien=" + pn.getTongTien());
            System.out.println("[DAO] trangThai=" + pn.getTrangThai());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── Cập nhật trạng thái ──────────────────────────────────────────────────
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

    // ── Xoá phiếu nhập (dùng connection chung cho transaction) ──────────────
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

    // ── Lấy phiếu nhập theo mã, JOIN để có tên NCC ──────────────────────────
    public PhieuNhapHangDTO getPhieuNhapById(String maPN) {
        String sql = "SELECT pn.*, ncc.tenNCC "
                + "FROM phieunhap pn "
                + "LEFT JOIN nhacungcap ncc ON pn.nhacungcap = ncc.maNCC "
                + "WHERE pn.maPN = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPN);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PhieuNhapHangDTO pn = new PhieuNhapHangDTO(
                            rs.getString("maPN"),
                            rs.getString("maNV"),
                            rs.getString("nhacungcap"), // tên cột trong DB là nhacungcap
                            rs.getTimestamp("ngaytao"),
                            rs.getDouble("thanhtien"),
                            rs.getString("trangthai")
                    );
                    pn.setTenNCC(rs.getString("tenNCC")); // gán tên NCC để hiển thị
                    return pn;
                }
            }
        } catch (SQLException e) {
            System.out.println("[DAO] getPhieuNhapById() lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // ── Cập nhật phiếu nhập (dùng connection chung cho transaction) ─────────
    public boolean update(PhieuNhapHangDTO pn, Connection conn) throws SQLException {
        String sql = "UPDATE phieunhap SET maNV = ?, nhacungcap = ?, thanhtien = ?, trangthai = ? "
                + "WHERE maPN = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pn.getMaNV());
            ps.setString(2, pn.getMaNCC());               // cập nhật mã NCC
            ps.setDouble(3, pn.getTongTien());
            ps.setString(4, pn.getTrangThai());
            ps.setString(5, pn.getMaPN());
            return ps.executeUpdate() > 0;
        }
    }

    // ── Lấy mã phiếu nhập lớn nhất để sinh mã mới ───────────────────────────
    public String getLastMaPhieuNhap() {
        // SQL Server
       // String sql = "SELECT TOP 1 maPN FROM phieunhap ORDER BY maPN DESC";

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
