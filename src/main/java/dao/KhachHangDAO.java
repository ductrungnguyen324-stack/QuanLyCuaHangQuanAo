package dao;
import entity.KhachHang;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.util.ArrayList;

public class KhachHangDAO {

    // INSERT
    public int insert(KhachHang kh) {
        int result = 0;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO KhachHang(maKH, hoten, sdt, ngaythamgia) VALUES(?,?,?,?)")) {

            ps.setString(1, kh.getMaKH());
            ps.setString(2, kh.getHoten());
            ps.setString(3, kh.getSdt());
            ps.setDate(4, Date.valueOf(kh.getNgaythamgia()));

            result = ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // UPDATE
    public int update(KhachHang kh) {
        int result = 0;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE KhachHang SET hoten=?, sdt=?, ngaythamgia=? WHERE maKH=?")) {

            ps.setString(1, kh.getHoten());
            ps.setString(2, kh.getSdt());
            ps.setDate(3, Date.valueOf(kh.getNgaythamgia()));
            ps.setString(4, kh.getMaKH());

            result = ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // DELETE
    public int delete(String maKH) {
        int result = 0;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "DELETE FROM KhachHang WHERE maKH=?")) {

            ps.setString(1, maKH);
            result = ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // SELECT ALL
    public ArrayList<KhachHang> getAll() {
        ArrayList<KhachHang> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT * FROM KhachHang");
             ResultSet rs = ps.executeQuery()) {

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

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
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

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
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
