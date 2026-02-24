package dao;
import java.sql.Connection;
import java.util.List;
import java.util.ArrayList;
import  java.sql.*;
import entity.*;


public class ChiTietHoaDonDAO {

    public List<ChiTietHoaDon> getAll() {
        List<ChiTietHoaDon> list = new ArrayList<>();
        String sql = "SELECT * from ChiTietHoaDon ORDER BY maCTHD DESC";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {

            while(rs.next()) {
                ChiTietHoaDon cthd = mapResultSetToEntity(rs);
                list.add(cthd);
            }
            rs.close();

        } catch(SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(ChiTietHoaDon cthd) {
        String sql = "INSERT INTO ChiTietHoaDon VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String maCTHD = generate();

        cthd.setMaHD(maCTHD);

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cthd.getMaCTHD());
            pstmt.setString(2, cthd.getMaHD());
            pstmt.setString(3, cthd.getMaSP());
            pstmt.setString(4, cthd.getTenSP());
            pstmt.setDouble(5, cthd.getSoluong());
            pstmt.setDouble(6, cthd.getDongia());
            pstmt.setDouble(7, cthd.getThanhtien());
            pstmt.setString(8, cthd.getKhuyenmai().getMaKM());

            return pstmt.executeUpdate() > 0;
        } catch(Exception e ) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(ChiTietHoaDon cthd) {
        ChiTietHoaDon check = getById(cthd.getMaCTHD());

        if(check == null) throw new RuntimeException("Chi tiet hoa don chua ton tai !");

        String sql =  "UPDATE ChiTietHoaDon SET "
                + "maHD = ?, "
                + "maSP = ?, "
                + "tenSP = ?, "
                + "soluong = ?, "
                + "dongia = ?, "
                + "thanhtien = ?, "
                + "maKM = ? "
                + "WHERE maCTHD = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cthd.getMaHD());
            pstmt.setString(2, cthd.getMaSP());
            pstmt.setString(3, cthd.getTenSP());
            pstmt.setDouble(4, cthd.getSoluong());
            pstmt.setDouble(5, cthd.getDongia());
            pstmt.setDouble(6, cthd.getThanhtien());
            pstmt.setString(7, cthd.getKhuyenmai().getMaKM());

            return pstmt.executeUpdate() > 0;

        }catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public ChiTietHoaDon getById(String maCTHD) {
        String sql = "SELECT * from ChiTietHoaDon where maCTHD = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maCTHD);

            try(ResultSet rs = pstmt.executeQuery()) {
                if(rs.next()) {
                    return mapResultSetToEntity(rs);
                }
                rs.close();
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String generate() {
        String sql = "SELECT maCTHD from ChiTietHoaDon ORDER BY maCTHD DESC LIMIT 1";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {

            if(rs.next()) {
                String maHD = rs.getString("maCTHD");
                int number = Integer.parseInt(maHD.substring(4));
                return String.format("CTHD%03d", number + 1);
            }
            rs.close();

        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return "CTHD001";
    }

    public ChiTietHoaDon mapResultSetToEntity(ResultSet rs) throws SQLException {
        ChiTietHoaDon cthd = new ChiTietHoaDon();

        cthd.setMaCTHD(rs.getString("maCTHD"));
        cthd.setDongia(rs.getDouble("dongia"));
        cthd.setMaHD(rs.getString("maHD"));
        cthd.setMaSP(rs.getString("maSP"));
        cthd.setTenSP(rs.getString("tenSP"));
        cthd.setSoluong(rs.getDouble("soluong"));
        cthd.setThanhtien(rs.getDouble("thanhtien"));
        KhuyenMai km = new KhuyenMai();
        km.setMaKM(rs.getString("maKM"));
        cthd.setKhuyenmai(km);

        return cthd;
    }
}