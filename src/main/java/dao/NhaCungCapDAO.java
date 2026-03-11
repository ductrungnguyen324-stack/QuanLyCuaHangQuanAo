package dao;

import entity.NhaCungCap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NhaCungCapDAO {
    public List<NhaCungCap> getAll() {
        List<NhaCungCap> list = new ArrayList<>();
        String sql = "SELECT * FROM NhaCungCap ORDER BY maNCC DESC";

        try (Connection connect = DBConnection.getConnection();
             PreparedStatement pstmt = connect.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                NhaCungCap ncc = mapResultSetToEntity(rs);
                list.add(ncc);
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public boolean insert(NhaCungCap ncc) {

        String sql = "INSERT INTO NhaCungCap VALUES(?, ?, ?, ?)";

        String maNCC = generateNCC();
        ncc.setMaNCC(maNCC);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ncc.getMaNCC());
            pstmt.setString(2, ncc.getTenNCC());
            pstmt.setString(3, ncc.getDiachi());
            pstmt.setString(4, ncc.getSodienthoai());

            return pstmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    public boolean update(NhaCungCap ncc) {

        NhaCungCap check = getById(ncc.getMaNCC());

        if (check == null) throw new RuntimeException("Nha cung cap chua ton tai!");

        String sql = "UPDATE NhaCungCap SET tenNCC=?, diachi=?, sodienthoai=? WHERE maNCC=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ncc.getTenNCC());
            pstmt.setString(2, ncc.getDiachi());
            pstmt.setString(3, ncc.getSodienthoai());
            pstmt.setString(6, ncc.getMaNCC());

            return pstmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public String generateNCC() {

        String sql = "SELECT maNCC FROM NhaCungCap ORDER BY maNCC DESC LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {

                String maNCC = rs.getString("maNCC");
                int number = Integer.parseInt(maNCC.substring(3));

                return String.format("NCC%03d", number + 1);
            }
            rs.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return "NCC001";
    }

    public NhaCungCap getById(String maNCC) {

        String sql = "SELECT * FROM NhaCungCap WHERE maNCC = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maNCC);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public NhaCungCap mapResultSetToEntity(ResultSet rs) throws SQLException {

        NhaCungCap ncc = new NhaCungCap();

        ncc.setMaNCC(rs.getString("maNCC"));
        ncc.setTenNCC(rs.getString("tenNCC"));
        ncc.setDiachi(rs.getString("diachi"));
        ncc.setSodienthoai(rs.getString("sodienthoai"));

        return ncc;
    }
}
