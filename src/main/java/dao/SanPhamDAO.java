package dao;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import entity.*;


public class SanPhamDAO {
    
    public List<SanPham> getALL() {
        List<SanPham> list = new ArrayList<>();
        String sql = "SELECT * FROM SanPham ORDER BY maSP DESC";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                SanPham sp = mapResultSetToEntity(rs);
                list.add(sp);
            }
            rs.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return list;

    }

    public boolean insert(SanPham sp) {
        String sql = "INSERT INTO SanPham VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String maSP = generateSP();
        sp.setMasp(maSP);

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, sp.getMasp());
            pstmt.setString(2, sp.getTensp());
            pstmt.setString(3, sp.getLoaisp());
            pstmt.setDouble(4, sp.getGiaban());
            pstmt.setString(5, sp.getThuonghieu());
            pstmt.setString(6, sp.getKichco());
            pstmt.setString(7, sp.getMausac());
            pstmt.setString(8, sp.getTrangthai());
            pstmt.setInt(9, sp.getTonkho());

            return pstmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(SanPham sp) {
        SanPham check = new SanPham();
        if(check == null) throw new RuntimeException("San Pham chua ton tai");

        String sql = "UPDATE SanPham SET tenSP=?, loaiSP=?, giaban=?, thuonghieu=?, kichco=?," +
                "mausac=?, trangthai=?, tonkho=?" +
                "WHERE maSP = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, sp.getTensp());
            pstmt.setString(2, sp.getLoaisp());
            pstmt.setDouble(3, sp.getGiaban());
            pstmt.setString(4, sp.getThuonghieu());
            pstmt.setString(5, sp.getKichco());
            pstmt.setString(6, sp.getMausac());
            pstmt.setString(7, sp.getTrangthai());
            pstmt.setInt(8, sp.getTonkho());
            pstmt.setString(9, sp.getMasp());

            return pstmt.executeUpdate() > 0;
        }catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public SanPham getById(String maSP) {
        String sql = "SELECT * from SanPham WHERE maSP = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maSP);
            ResultSet rs = pstmt.executeQuery();


            if(rs.next()) {
                return mapResultSetToEntity(rs);
            }

        }catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String generateSP() {
        String sql = "SELECT maSP FROM SanPham ORDER BY maSP DESC LIMIT 1";

        try(Connection conn =DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()) {
                String maSP = rs.getString("maSP");
                int number = Integer.parseInt(maSP.substring(2));
                return String.format("SP%03d", number + 1);
            }
            rs.close();

        }catch(SQLException e) {
            e.printStackTrace();
        }
        return "SP001";
    }

    public SanPham mapResultSetToEntity(ResultSet rs) throws SQLException {
        SanPham sp = new SanPham();

        sp.setMasp(rs.getString("maSP"));
        sp.setTensp(rs.getString("tenSP"));
        sp.setLoaisp(rs.getString("loaiSP"));
        sp.setGiaban(rs.getDouble("giaban"));
        sp.setThuonghieu(rs.getString("thuonghieu"));
        sp.setKichco(rs.getString("kichco"));
        sp.setMausac(rs.getString("mausac"));
        sp.setTrangthai(rs.getString("trangthai"));
        sp.setTonkho(rs.getInt("tonkho"));
        sp.setSoluong(rs.getInt("soluong"));

        return sp;
    }
}