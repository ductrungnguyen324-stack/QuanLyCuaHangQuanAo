package bus;

import dao.PhieuNhapHangDAO;
import dao.ChiTietPhieuNhapDAO;
import dao.MyConnection;
import entity.PhieuNhapHangDTO;
import entity.ChiTietPhieuNhapDTO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class PhieuNhapHangBUS {

    private final PhieuNhapHangDAO pnDAO = new PhieuNhapHangDAO();
    private final ChiTietPhieuNhapDAO ctpnDAO = new ChiTietPhieuNhapDAO();

    public ArrayList<PhieuNhapHangDTO> getAll() {
        return pnDAO.getAll();
    }

    public ArrayList<ChiTietPhieuNhapDTO> getChiTietByMaPN(String maPN) {
        return ctpnDAO.getByMaPN(maPN);
    }

    public boolean themPhieuNhap(PhieuNhapHangDTO pn, ArrayList<ChiTietPhieuNhapDTO> dsCT) {
        Connection conn = null;
        try {
            conn = MyConnection.getConnection();
            conn.setAutoCommit(false);

            if (!pnDAO.insert(pn, conn)) {
                conn.rollback();
                return false;
            }

            for (ChiTietPhieuNhapDTO ct : dsCT) {
                if (!ctpnDAO.insert(ct, conn)) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean xoaPhieuNhap(String maPN) {
        Connection conn = null;
        try {
            conn = MyConnection.getConnection();
            conn.setAutoCommit(false);

            ctpnDAO.deleteByMaPN(maPN, conn);

            if (!pnDAO.delete(maPN, conn)) {
                conn.rollback();
                return false;
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public boolean duyetPhieu(String maPN) {
        return pnDAO.updateTrangThai(maPN, "Đã nhập kho");
    }

    public ArrayList<PhieuNhapHangDTO> timKiemVaLoc(String keyword, String trangThaiLoc) {
        ArrayList<PhieuNhapHangDTO> all = pnDAO.getAll();
        ArrayList<PhieuNhapHangDTO> result = new ArrayList<>();

        String keyLower = keyword.toLowerCase();

        for (PhieuNhapHangDTO pn : all) {
            boolean matchKey = pn.getMaPN().toLowerCase().contains(keyLower)
                    || pn.getNhaCungCap().toLowerCase().contains(keyLower);

            boolean matchStatus = trangThaiLoc == null || trangThaiLoc.isEmpty()
                    || pn.getTrangThai().equals(trangThaiLoc);

            if (matchKey && matchStatus) {
                result.add(pn);
            }
        }
        return result;
    }

    public PhieuNhapHangDTO getPhieuNhapById(String maPN) {
        return pnDAO.getPhieuNhapById(maPN);
    }
}
