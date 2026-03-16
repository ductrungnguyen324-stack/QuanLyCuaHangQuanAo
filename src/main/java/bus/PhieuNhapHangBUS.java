package bus;

import dao.PhieuNhapHangDAO;
import dao.ChiTietPhieuNhapDAO;
import dao.DBConnection;
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

    public PhieuNhapHangDTO getPhieuNhapById(String maPN) {
        return pnDAO.getPhieuNhapById(maPN);
    }

    public boolean themPhieuNhap(PhieuNhapHangDTO pn, ArrayList<ChiTietPhieuNhapDTO> dsCT) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) {
                System.out.println("[BUS] Không thể kết nối database!");
                return false;
            }
            conn.setAutoCommit(false);

            System.out.println("[BUS] Insert phieu: " + pn.getMaPN());
            boolean insertPN = pnDAO.insert(pn, conn);
            System.out.println("[BUS] Insert phieu result: " + insertPN);
            if (!insertPN) {
                conn.rollback();
                return false;
            }

            for (ChiTietPhieuNhapDTO ct : dsCT) {
                System.out.println("[BUS] Insert chiTiet: maSP=" + ct.getMaSP()
                        + ", sl=" + ct.getSoLuong() + ", gia=" + ct.getDonGia());
                boolean insertCT = ctpnDAO.insert(ct, conn);
                System.out.println("[BUS] Insert chiTiet result: " + insertCT);
                if (!insertCT) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            System.out.println("[BUS] Commit thành công: " + pn.getMaPN());
            return true;

        } catch (Exception e) {
            System.out.println("[BUS] Exception: " + e.getMessage());
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
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

    public boolean capNhatPhieu(PhieuNhapHangDTO pn, ArrayList<ChiTietPhieuNhapDTO> dsCT) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) {
                return false;
            }
            conn.setAutoCommit(false);

            boolean updatePN = pnDAO.update(pn, conn);
            System.out.println("[BUS] Update phieu result: " + updatePN);
            if (!updatePN) {
                conn.rollback();
                return false;
            }

            ctpnDAO.deleteByMaPN(pn.getMaPN(), conn);

            for (ChiTietPhieuNhapDTO ct : dsCT) {
                if (!ctpnDAO.insert(ct, conn)) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            System.out.println("[BUS] capNhatPhieu Exception: " + e.getMessage());
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
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
            conn = DBConnection.getConnection();
            if (conn == null) {
                return false;
            }
            conn.setAutoCommit(false);

            ctpnDAO.deleteByMaPN(maPN, conn);
            if (!pnDAO.delete(maPN, conn)) {
                conn.rollback();
                return false;
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            System.out.println("[BUS] xoaPhieuNhap Exception: " + e.getMessage());
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
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

    public boolean duyetPhieu(String maPN) {
        return pnDAO.updateTrangThai(maPN, "Đã nhập kho");
    }

    public ArrayList<PhieuNhapHangDTO> timKiemVaLoc(String keyword, String trangThaiLoc) {
        ArrayList<PhieuNhapHangDTO> all = pnDAO.getAll();
        ArrayList<PhieuNhapHangDTO> result = new ArrayList<>();
        String keyLower = (keyword == null) ? "" : keyword.toLowerCase();
        for (PhieuNhapHangDTO pn : all) {
            boolean matchKey = keyLower.isEmpty()
                    || pn.getMaPN().toLowerCase().contains(keyLower)
                    || pn.getMaNV().toLowerCase().contains(keyLower)
                    || pn.getMaNCC().toLowerCase().contains(keyLower)
                    || (pn.getTenNCC() != null && pn.getTenNCC().toLowerCase().contains(keyLower));
            boolean matchStatus = trangThaiLoc == null
                    || trangThaiLoc.isEmpty()
                    || trangThaiLoc.equals("Tất cả trạng thái")
                    || pn.getTrangThai().equals(trangThaiLoc);
            if (matchKey && matchStatus) {
                result.add(pn);
            }
        }
        return result;
    }

    public String generateNextMaPN() {
        String lastMa = PhieuNhapHangDAO.getInstance().getLastMaPhieuNhap();
        if (lastMa == null || lastMa.isEmpty()) {
            return "PN001";
        }
        try {
            int nextNumber = Integer.parseInt(lastMa.substring(2)) + 1;
            return String.format("PN%03d", nextNumber);
        } catch (NumberFormatException e) {
            return "PN001";
        }
    }
}
