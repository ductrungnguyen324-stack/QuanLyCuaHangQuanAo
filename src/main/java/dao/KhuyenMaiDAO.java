package dao;

import entity.KhuyenMai;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhuyenMaiDAO
{
    public List<KhuyenMai> getAll()
    {
        List<KhuyenMai> list = new ArrayList<>();
        String sql = "SELECT * FROM khuyenmai";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                KhuyenMai km = mapResultSetToEntity(rs);
                list.add(km);
            }
            rs.close();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi getAll KhuyenMai: " + e.getMessage());
        }
        return list;
    }

    public KhuyenMai getByID(String makhuyenmai)
    {
        KhuyenMai km = null;
        String sql = "SELECT * FROM khuyenmai WHERE maKM = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, makhuyenmai);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next())
                km = mapResultSetToEntity(rs);
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi getByID KhuyenMai: " + e.getMessage());
        }
        return km;
    }

    public boolean insert(KhuyenMai km)
    {
        String sql = "INSERT INTO khumay (maKM, tenKM, loaiKM, giatrigiam, giamtoida, giatridonhangtoithieu, ngaybatdau, ngayketthuc, soluong, dasudung) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Tạo mã khu tự động
        String makhuyenmai = generateMaKhuyenMai();
        km.setMaKM(makhuyenmai);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, km.getMaKM());
            pstmt.setString(2, km.getTenKM());
            pstmt.setString(3, km.getLoaiKM());
            pstmt.setDouble(4, km.getGiatrigiam());
            pstmt.setDouble(5, km.getGiamtoida());
            pstmt.setDouble(6, km.getGiatridonhangtoithieu());
            pstmt.setTimestamp(7, Timestamp.valueOf(km.getNgaybatdau()));
            pstmt.setTimestamp(8, Timestamp.valueOf(km.getNgayketthuc()));
            pstmt.setInt(9, km.getSoluong());
            pstmt.setInt(10, km.getDasudung());

            int rowUpdate = pstmt.executeUpdate();
            return rowUpdate > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi insert KhuyenMai: " + e.getMessage());
        }
    }

    public boolean update(KhuyenMai km)
    {
        KhuyenMai existing = getByID(km.getMaKM());

        //kiểm tra khu máy tồn tại
        if (existing == null){
            throw new RuntimeException("Lỗi: khuyến mãi không tồn tại !");
        }

        String sql = "UPDATE khuyenmai SET tenKM = ?, loaiKM = ?, giatrigiam = ?, giamtoida = ?, giatridonhangtoithieu = ?, ngaybatdau = ?, ngayketthuc = ?, soluong = ?, dasudung = ? " +
                "WHERE maKM = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, km.getTenKM());
            pstmt.setString(2, km.getLoaiKM());
            pstmt.setDouble(4, km.getGiamtoida());
            pstmt.setDouble(5, km.getGiatridonhangtoithieu());
            pstmt.setTimestamp(6, Timestamp.valueOf(km.getNgaybatdau()));
            pstmt.setTimestamp(7, Timestamp.valueOf(km.getNgayketthuc()));
            pstmt.setInt(8, km.getSoluong());
            pstmt.setInt(9, km.getDasudung());

            // Tham số cho mệnh đề WHERE
            pstmt.setString(10, km.getMaKM());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi update KhuyenMai: " + e.getMessage());
        }
        return true;
    }

    public boolean delete(String makhuyenmai)
    {
        KhuyenMai km = getByID(makhuyenmai);

        //kiểm tra khu máy tồn tại
        if (km == null) {
            throw new RuntimeException("Lỗi khu máy không tồn tại !");
        }

        String sql = "DELETE FROM khuyenmai WHERE maKM = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, makhuyenmai);
            int row = pstmt.executeUpdate();
            return row > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi delete KhuyenMai: " + e.getMessage());
        }
    }

    public List<KhuyenMai> search(String keyword) {
        List<KhuyenMai> list = new ArrayList<>();
        // Tìm kiếm không phân biệt hoa thường với LIKE và dấu %
        String sql = "SELECT * FROM khuyenmai WHERE maKM LIKE ? OR tenKM LIKE ?";

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
            throw new RuntimeException("Lỗi search KhuyenMai: " + e.getMessage());
        }
        return list;
    }

    public int count()
    {
        String sql = "SELECT COUNT(*) FROM khuyenmai";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql))
        {
            ResultSet rs = pstmt.executeQuery(sql);
            if (rs.next())
            {
                return rs.getInt(1);
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi count KhuyenMai: " + e.getMessage());
        }
        return 0;
    }

    public List<KhuyenMai> getActivePromotions()
    {
        List<KhuyenMai> list = new ArrayList<>();
        String sql = "SELECT * FROM KhuyenMai WHERE dasudung < soluong " +
                "AND NOW() >= ngaybatdau AND NOW() <= ngayketthuc";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
            {
                KhuyenMai km = mapResultSetToEntity(rs);
                list.add(km);
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi getActivePromotions KhuyenMai: " + e.getMessage());
        }
        return list;
    }

    public boolean updateUsageCount(String makhuyenmai, int amount)
    {
        // SQL: Tăng giá trị dasudung thêm amount đơn vị
        // Điều kiện: dasudung + amount không được vượt quá soluong và không nhỏ hơn 0
        String sql = "UPDATE Khuyenmai SET dasudung = dasudung + ? " +
                "WHERE maKM = ? AND (dasudung + ?) <= soluong AND (dasudung + ?) >= 0";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setInt(1, amount);
            pstmt.setString(2, makhuyenmai);
            pstmt.setInt(3, amount);
            pstmt.setInt(4, amount);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi updateUsageCount KhuyenMai: " + e.getMessage());
        }
    }

    //Tạo mã tự động
    public String generateMaKhuyenMai()
    {
        String sql = "SELECT maKM FROM khuyenmai "+
                "ORDER BY maKM DESC LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            ResultSet rs = pstmt.executeQuery(sql);

            if (rs.next())
            {
                String makhuyenmai = rs.getString("MaKhuyenMai");
                int num = Integer.parseInt(makhuyenmai.substring(2));
                return String.format("KM%03d", num + 1);
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi generateMaKhuyenMai" + e.getMessage());
        }
        //CHƯA CÓ DATABASE
        return "KM001";
    }

    public KhuyenMai mapResultSetToEntity(ResultSet rs) throws SQLException
    {
        KhuyenMai km = new KhuyenMai();
        km.setMaKM(rs.getString("maKM"));
        km.setTenKM(rs.getString("tenKM"));
        km.setLoaiKM(rs.getString("loaiKM"));
        km.setGiatrigiam(rs.getDouble("giatrigiam"));
        km.setGiamtoida(rs.getDouble("giatoida"));
        km.setGiatridonhangtoithieu(rs.getDouble("giatridonhangtoithieu"));

        // Chuyển đổi SQL Timestamp sang Java LocalDateTime
        Timestamp nbd = rs.getTimestamp("ngaybatdau");
        if (nbd != null) km.setNgaybatdau(nbd.toLocalDateTime());

        Timestamp nkt = rs.getTimestamp("ngayketthuc");
        if (nkt != null) km.setNgayketthuc(nkt.toLocalDateTime());

        km.setSoluong(rs.getInt("soluong"));
        km.setDasudung(rs.getInt("dasudung"));

        return km;
    }
}
