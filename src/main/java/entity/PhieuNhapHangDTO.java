package entity;

import java.util.Date;

public class PhieuNhapHangDTO {

    private String maPN;
    private String maNV;
    private String maNCC;      // maps to DB column: nhacungcap (FK)
    private String tenNCC;     // từ JOIN nhacungcap — chỉ hiển thị, không lưu DB
    private Date ngayTao;
    private double tongTien;
    private String trangThai;

    public PhieuNhapHangDTO() {
    }

    public PhieuNhapHangDTO(String maPN, String maNV, String maNCC, Date ngayTao, double tongTien, String trangThai) {
        this.maPN = maPN;
        this.maNV = maNV;
        this.maNCC = maNCC;
        this.ngayTao = ngayTao;
        this.tongTien = tongTien;
        this.trangThai = trangThai;
    }

    public String getMaPN() {
        return maPN;
    }

    public void setMaPN(String maPN) {
        this.maPN = maPN;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    /**
     * Mã NCC — lưu xuống DB cột nhacungcap.
     */
    public String getMaNCC() {
        return maNCC;
    }

    public void setMaNCC(String maNCC) {
        this.maNCC = maNCC;
    }

    /**
     * Tên NCC — lấy từ JOIN, chỉ dùng để hiển thị, không lưu DB.
     */
    public String getTenNCC() {
        return tenNCC;
    }

    public void setTenNCC(String tenNCC) {
        this.tenNCC = tenNCC;
    }

    public Date getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Date ngayTao) {
        this.ngayTao = ngayTao;
    }

    public double getTongTien() {
        return tongTien;
    }

    public void setTongTien(double tongTien) {
        this.tongTien = tongTien;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return "PhieuNhap{maPN='" + maPN + "', maNCC='" + maNCC + "', tongTien=" + tongTien + '}';
    }
}
