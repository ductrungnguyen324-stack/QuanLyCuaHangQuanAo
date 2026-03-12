package bus;

import dao.MyConnection;
import dao.PhieuNhapHangDAO;
import dao.ChiTietPhieuNhapDAO;
import entity.PhieuNhapHangDTO;
import entity.ChiTietPhieuNhapDTO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class ChiTietPhieuNhapBUS {

    private final ChiTietPhieuNhapDAO ctpnDAO = new ChiTietPhieuNhapDAO();
    private final PhieuNhapHangDAO pnDAO = new PhieuNhapHangDAO();

    public ArrayList<ChiTietPhieuNhapDTO> getByMaPN(String maPN) {
        return ctpnDAO.getByMaPN(maPN);
    }

    public boolean insert(ChiTietPhieuNhapDTO ctpn) {
        Connection conn = null;
        try {
            conn = MyConnection.getConnection();
            return ctpnDAO.insert(ctpn, conn);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean deletePhieuFull(String maPN) {
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

    public boolean themPhieuFull(PhieuNhapHangDTO pn, ArrayList<ChiTietPhieuNhapDTO> dsCT) {
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

    public String taoMaMoi() {
        String maxMa = ctpnDAO.getMaMax();
        if (maxMa == null || maxMa.isEmpty()) {
            return "CTPN001";
        }

        String prefix = maxMa.replaceAll("\\d", "");
        String numberPart = maxMa.replaceAll("\\D", "");

        int nextNumber = Integer.parseInt(numberPart) + 1;

        return String.format("%s%03d", prefix, nextNumber);
    }

    public boolean updatePhieuFull(PhieuNhapHangDTO pn, ArrayList<ChiTietPhieuNhapDTO> dsCT) {
        Connection conn = null;
        try {
            conn = MyConnection.getConnection();
            conn.setAutoCommit(false);

            if (!pnDAO.update(pn, conn)) {
                throw new SQLException("Lỗi cập nhật phiếu chính");
            }

            if (!ctpnDAO.deleteByMaPN(pn.getMaPN(), conn)) {
                throw new SQLException("Lỗi xóa chi tiết cũ");
            }

            for (ChiTietPhieuNhapDTO ct : dsCT) {
                if (!ctpnDAO.insert(ct, conn)) {
                    throw new SQLException("Lỗi chèn chi tiết mới");
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
            } catch (Exception e) {
            }
        }
    }

}
