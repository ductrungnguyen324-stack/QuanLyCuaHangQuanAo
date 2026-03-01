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
        String sql = "SELECT * FROM khuyenmai WHERE MaKhuyenMai = ?";

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
        String sql = "INSERT INTO khumay (MaKhuyenMai, TenKhuyenMai, LoaiKhuyeMai, GiaTriGiam, GiamToiDa, GiaTriDonHangToiThieu, NgayBatDau, NgayKetThuc, SoLuong, DaSuDung) " +
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
            throw new RuntimeException("Lỗi khuyến mãi không tồn tại !");
        }

        String sql = "UPDATE khuyenmai SET TenKhuyenMai = ?, LoaiKhuyenMai = ?, GiaTriGiam = ?, GiamToiDa = ?, GiaTriDonHangToiThieu = ?, NgayBatDau = ?, NgayKetThuc = ?, SoLuong = ?, DaSuDung = ? " +
                "WHERE MaKhuyenMai = ?";
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

        String sql = "DELETE FROM khuyenmai WHERE MaKhuyenMai = ?";
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

    public List<KhuyenMai> getActivePromotions()
    {
        List<KhuyenMai> list = new ArrayList<>();
        String sql = "SELECT * FROM KhuyenMai WHERE DaSuDung < SoLuong " +
                "AND NOW() >= NgayBatDau AND NOW() <= NgayKetThuc";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql))
        {
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                KhuyenMai km = new KhuyenMai();
                km.setMaKM(rs.getString("MaKhuyenMai"));
                km.setTenKM(rs.getString("TenKhuyenMai"));
                km.setLoaiKM(rs.getString("LoaiKhuyenMai"));
                km.setGiatrigiam(rs.getDouble("GiaTriGiam"));
                km.setGiamtoida(rs.getDouble("GiamToiDa"));
                km.setGiatridonhangtoithieu(rs.getDouble("GiaTriDonHangToiThieu"));

                Timestamp nbd = rs.getTimestamp("NgayBatDau");
                if (nbd != null) km.setNgaybatdau(nbd.toLocalDateTime());

                Timestamp nkt = rs.getTimestamp("NgayKetThuc");
                if (nkt != null) km.setNgayketthuc(nkt.toLocalDateTime());

                km.setSoluong(rs.getInt("SoLuong"));
                km.setDasudung(rs.getInt("DaSuDung"));

                list.add(km);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi getActivePromotions KhuyenMai: " + e.getMessage());
        }
        return list;
    }

    public boolean updateUsageCount(String makhuyenmai, int amount)
    {
        // SQL: Tăng giá trị dasudung thêm amount đơn vị
        // Điều kiện: dasudung + amount không được vượt quá soluong và không nhỏ hơn 0
        String sql = "UPDATE Khuyenmai SET DaSuDung = DaSuDung + ? " +
                "WHERE MaKhuyenMai = ? AND (DaSuDung + ?) <= SoLuong AND (DaSuDung + ?) >= 0";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql))
        {
            ps.setInt(1, amount);
            ps.setString(2, makhuyenmai);
            ps.setInt(3, amount);
            ps.setInt(4, amount);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi updateUsageCount KhuyenMai: " + e.getMessage());
        }
    }

    //Tạo mã tự động
    public String generateMaKhuyenMai()
    {
        String sql = "SELECT MaKhuyenMai FROM khuyenmai "+
                "ORDER BY MaKhuyenMai DESC LIMIT 1";

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
        km.setMaKM(rs.getString("MaKhuyenMai"));
        km.setTenKM(rs.getString("TenKhuyenMai"));
        km.setLoaiKM(rs.getString("LoaiKhuyenMai"));
        km.setGiatrigiam(rs.getDouble("GiaTriGiam"));
        km.setGiamtoida(rs.getDouble("GiaToiDa"));
        km.setGiatridonhangtoithieu(rs.getDouble("GiaTriDonHangToiThieu"));

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