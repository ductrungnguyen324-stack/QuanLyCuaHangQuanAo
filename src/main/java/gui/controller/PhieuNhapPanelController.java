package gui.controller;

import bus.PhieuNhapHangBUS;
import entity.PhieuNhapHangDTO;
import java.util.ArrayList;

public class PhieuNhapPanelController {

    private final PhieuNhapHangBUS bus = new PhieuNhapHangBUS();

    // ── Lấy toàn bộ danh sách ────────────────────────────
    public ArrayList<PhieuNhapHangDTO> getAll() {
        return bus.getAll();
    }

    // ── Tìm kiếm + lọc trạng thái (xử lý hoàn toàn ở Controller) ──
    public ArrayList<PhieuNhapHangDTO> locDuLieu(String keyword, String trangThai) {
        String kw = keyword.trim().toLowerCase();
        ArrayList<PhieuNhapHangDTO> listGoc = bus.getAll();
        ArrayList<PhieuNhapHangDTO> ketQua  = new ArrayList<>();

        for (PhieuNhapHangDTO pn : listGoc) {
            boolean matchTT = trangThai.equals("Tất cả trạng thái")
                           || pn.getTrangThai().equalsIgnoreCase(trangThai);
            boolean matchKW = kw.isEmpty()
                           || pn.getMaPN().toLowerCase().contains(kw)
                           || pn.getMaNV().toLowerCase().contains(kw)
                           || pn.getNhaCungCap().toLowerCase().contains(kw);
            if (matchTT && matchKW) ketQua.add(pn);
        }
        return ketQua;
    }

    // ── Lấy 1 phiếu theo mã ──────────────────────────────
    public PhieuNhapHangDTO getById(String maPN) {
        return bus.getPhieuNhapById(maPN);
    }

    // ── Duyệt phiếu ──────────────────────────────────────
    public boolean duyetPhieu(String maPN) {
        return bus.duyetPhieu(maPN);
    }

    // ── Xoá phiếu ────────────────────────────────────────
    public boolean xoaPhieu(String maPN) {
        return bus.xoaPhieuNhap(maPN);
    }

    // ── Kiểm tra đã duyệt chưa ───────────────────────────
    public boolean isDaDuyet(String trangThai) {
        return "Đã nhập kho".equals(trangThai);
    }

    // ── Tính thống kê cho chips header ───────────────────
    public int[] tinhThongKe(ArrayList<PhieuNhapHangDTO> list) {
        // [0]=tổng, [1]=chờ xử lý, [2]=đã nhập kho, [3]=đã huỷ
        int[] stats = new int[4];
        stats[0] = list.size();
        for (PhieuNhapHangDTO pn : list) {
            String tt = pn.getTrangThai();
            if ("Chờ xử lý".equals(tt))   stats[1]++;
            if ("Đã nhập kho".equals(tt)) stats[2]++;
            if ("Đã huỷ".equals(tt))      stats[3]++;
        }
        return stats;
    }

    // ── Tính tổng chi từ danh sách ───────────────────────
    public double tinhTongChi(ArrayList<PhieuNhapHangDTO> list) {
        double tong = 0;
        for (PhieuNhapHangDTO pn : list) tong += pn.getTongTien();
        return tong;
    }
}
