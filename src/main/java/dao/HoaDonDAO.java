package dao;
import java.util.ArrayList;
import java.util.List;
import entity.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.Date;

public class HoaDonDAO {

    public List<HoaDon> getAll() {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT * from HoaDon ORDER BY maHD DESC";

        try (Connection connect = DBConnection.getConnection();
             PreparedStatement pstmt = connect.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()) {
                HoaDon hd = mapResultSetToEntity(rs);
                list.add(hd);
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public boolean insert (HoaDon hd) {
        String sql = "INSERT INTO HoaDon VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String maHD = generateHD();
        hd.setMaHD(maHD);

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, hd.getMaHD());
            pstmt.setString(2, hd.getMaKH());
            pstmt.setString(3, hd.getMaNV());
            pstmt.setString(4, hd.getKhuyenmai());
            pstmt.setTimestamp(5, Timestamp.valueOf(hd.getNgaytao()));
            pstmt.setDouble(6, hd.getTongtien());
            pstmt.setDouble(7, hd.getSotiengiam());
            pstmt.setDouble(8, hd.getThanhtoan());
            pstmt.setString(9, hd.getPhuongthucTT());
            pstmt.setString(10, hd.getTrangthai());

            return pstmt.executeUpdate() > 0;
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public boolean update(HoaDon hd) {
        HoaDon check = getById(hd.getMaHD());

        if(check == null) throw new RuntimeException("Hoa Don chua ton tai !");

        String sql = "UPDATE hoadon SET maKH=?, maNV=?, maKM=?, ngaytao=?, tongtien=?, "
                + "sotiengiam=?, thanhtoan=?, phuongthucTT=?, trangthai=? "
                + "WHERE maHD=?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, hd.getMaKH());
            pstmt.setString(2, hd.getMaNV());
            pstmt.setString(3, hd.getKhuyenmai());
            pstmt.setTimestamp(4, Timestamp.valueOf(hd.getNgaytao()));
            pstmt.setDouble(5, hd.getTongtien());
            pstmt.setDouble(6, hd.getSotiengiam());
            pstmt.setDouble(7, hd.getThanhtoan());
            pstmt.setString(8, hd.getPhuongthucTT());
            pstmt.setString(9, hd.getTrangthai());
            pstmt.setString(10, hd.getMaHD());

            return pstmt.executeUpdate() > 0;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String generateHD() {
        String sql = "SELECT maHD from HoaDon ORDER BY maHD DESC LIMIT 1";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {

            if(rs.next()) {
                String maHD = rs.getString("maHD");
                int number = Integer.parseInt(maHD.substring(2));
                return String.format("HD%03d", number + 1);
            }
            rs.close();

        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return "HD001";
    }

    public HoaDon getById(String maHD) {
        String sql = "SELECT * from HoaDon where maHD = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maHD);

            try(ResultSet rs = pstmt.executeQuery()) {
                if(rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public HoaDon mapResultSetToEntity(ResultSet rs) throws SQLException {
        HoaDon hd = new HoaDon();

        hd.setMaHD(rs.getString("maHD"));
        hd.setMaKH(rs.getString("maKH"));
        hd.setMaNV(rs.getString("maNV"));
        hd.setPhuongthucTT(rs.getString("phuongthucTT"));
        hd.setSotiengiam(rs.getDouble("sotiengiam"));
        hd.setThanhtoan(rs.getDouble("thanhtoan"));
        hd.setTongtien(rs.getDouble("tongtien"));
        hd.setTrangthai(rs.getString("trangthai"));
        hd.setKhuyenmai(rs.getString("maKM"));

        Timestamp ts = rs.getTimestamp("ngaytao");
        if (ts != null) {
            hd.setNgaytao(ts.toLocalDateTime());
        }
        return hd;
    }

    public static void main(String[] args) {
        HoaDonDAO hd = new HoaDonDAO();
        String ma = hd.generateHD();
    }
}
