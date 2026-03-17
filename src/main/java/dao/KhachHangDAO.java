package dao;

import entity.KhachHang;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.util.ArrayList;

public class KhachHangDAO {

    // INSERT
    public boolean insert(KhachHang kh) {
        int result = 0;
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO KhachHang(maKH, hoten, sdt, ngaythamgia) VALUES(?,?,?,?)")) {

            ps.setString(1, kh.getMaKH());
            ps.setString(2, kh.getHoten());
            ps.setString(3, kh.getSdt());
            ps.setDate(4, Date.valueOf(kh.getNgaythamgia()));

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // UPDATE
    public boolean update(KhachHang kh) {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(
                "UPDATE KhachHang SET hoten=?, sdt=?, ngaythamgia=? WHERE maKH=?")) {

            ps.setString(1, kh.getHoten());
            ps.setString(2, kh.getSdt());
            ps.setDate(3, Date.valueOf(kh.getNgaythamgia()));
            ps.setString(4, kh.getMaKH());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // DELETE
    public boolean delete(String maKH) {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM KhachHang WHERE maKH=?")) {

            ps.setString(1, maKH);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // GENERATE MA KH
    public String generateKH() {
        String sql = "SELECT maKH FROM KhachHang ORDER BY maKH DESC LIMIT 1";
      //  String sql = " SELECT TOP 1 maKH FROM KhachHang ORDER BY maKH DESC";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String last = rs.getString("maKH"); // VD: KH005
                int num = Integer.parseInt(last.replaceAll("[^0-9]", "")) + 1;
                return String.format("KH%03d", num);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "KH001"; // mặc định nếu bảng trống
    }

    public ArrayList<KhachHang> getAll() {
        ArrayList<KhachHang> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM KhachHang"); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                KhachHang kh = new KhachHang(
                        rs.getString("maKH"),
                        rs.getString("hoten"),
                        rs.getString("sdt"),
                        rs.getDate("ngaythamgia").toLocalDate()
                );
                list.add(kh);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // FIND BY ID
    public KhachHang findById(String maKH) {
        KhachHang kh = null;

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM KhachHang WHERE maKH=?")) {

            ps.setString(1, maKH);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                kh = new KhachHang(
                        rs.getString("maKH"),
                        rs.getString("hoten"),
                        rs.getString("sdt"),
                        rs.getDate("ngaythamgia").toLocalDate()
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return kh;
    }

    // FIND BY PHONE
    public KhachHang findByPhone(String sdt) {
        KhachHang kh = null;

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM KhachHang WHERE sdt=?")) {

            ps.setString(1, sdt);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Date ngay = rs.getDate("ngaythamgia");

                    kh = new KhachHang(
                            rs.getString("maKH"),
                            rs.getString("hoten"),
                            rs.getString("sdt"),
                            (ngay != null) ? ngay.toLocalDate() : null
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return kh;
    }
}
