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
        String sql = "SELECT * FROM sanpham";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                SanPham sp = mapResultSetToEntity(rs);
                list.add(sp);
            }
            rs.close();
        } catch(SQLException e) {
            throw new RuntimeException("Lỗi getAll SanPham: " + e.getMessage());
        }

        return list;

    }

    public boolean insert(SanPham sp) {
        String sql = "INSERT INTO SanPham (maSP, tenSP, loaiSP, giaban, thuonghieu, kichco, mausac, trangthai, tonkho) " +
                     "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
            throw new RuntimeException("Lỗi insert SanPham: " + e.getMessage());
        }
    }

    public boolean update(SanPham sp) {
        if(getById(sp.getMasp()) == null) throw new RuntimeException("Sản phẩm chưa tồn tại");

        String sql = "UPDATE sanpham SET tenSP=?, loaiSP=?, giaban=?, thuonghieu=?, kichco=?," +
                "mausac=?, trangthai=?, tonkho=? " +   // ← thêm dấu cách trước WHERE
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
            throw new RuntimeException("Lỗi update SanPham: " + e.getMessage());
        }
    }

    public List<SanPham> search(String keyword) {
        List<SanPham> list = new ArrayList<>();
        // Tìm kiếm không phân biệt hoa thường với LIKE và dấu %
        String sql = "SELECT * FROM sanpham WHERE maSP LIKE ? OR tenSP LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Cấu hình tham số tìm kiếm: %keyword%
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi search SanPham: " + e.getMessage());
        }
        return list;
    }

    public int count()
    {
        String sql = "SELECT COUNT(*) FROM sanpham";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql))
        {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            {
                return rs.getInt(1);
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi count SanPham: " + e.getMessage());
        }
        return 0;
    }

    public SanPham getById(String maSP) {
        String sql = "SELECT * from sanpham WHERE maSP = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maSP);
            ResultSet rs = pstmt.executeQuery();


            if(rs.next()) {
                return mapResultSetToEntity(rs);
            }

        }catch(SQLException e) {
            throw new RuntimeException("Lỗi getById SanPham: " + e.getMessage());
        }
        return null;
    }

    public String generateSP() {
        String sql = "SELECT TOP 1 maSP FROM sanpham ORDER BY maSP DESC"; // SQL Server

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()) {
                String maSP = rs.getString("maSP");
                int number = Integer.parseInt(maSP.substring(2));
                return String.format("SP%03d", number + 1);
            }
            rs.close();

        }catch(SQLException e) {
            throw new RuntimeException("Lỗi generateSP SanPham: " + e.getMessage());
        }
        return "SP001";
    }

    /**
     * Lấy thông tin sản phẩm để tự động điền vào form phiếu nhập.
     * @return ArrayList gồm 2 phần tử: [0] tenSP, [1] giaban dạng String.
     *         Trả về ["Không tìm thấy", "0"] nếu maSP không tồn tại.
     */
    public ArrayList<String> getThongTinSP(String maSP) {
        ArrayList<String> result = new ArrayList<>();
        String sql = "SELECT tenSP, giaban FROM sanpham WHERE maSP = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maSP);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                result.add(rs.getString("tenSP"));
                result.add(String.valueOf(rs.getDouble("giaban")));
                return result;
            }
            rs.close();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi getThongTinSP: " + e.getMessage());
        }

        result.add("Không tìm thấy");
        result.add("0");
        return result;
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

        return sp;
    }

   
}