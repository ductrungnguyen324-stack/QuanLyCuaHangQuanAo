package DTO;

import java.util.Date;

public class PhieuNhapHangDTO {

    private String maPN;
    private String maNV;
    private String nhaCungCap;
    private Date ngayTao;
    private double tongTien;
    private String trangThai;

    public PhieuNhapHangDTO() {
    }

    public PhieuNhapHangDTO(String maPN, String maNV, String nhaCungCap, Date ngayTao, double tongTien, String trangThai) {
        this.maPN = maPN;
        this.maNV = maNV;
        this.nhaCungCap = nhaCungCap;
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

    public String getNhaCungCap() {
        return nhaCungCap;
    }

    public void setNhaCungCap(String nhaCungCap) {
        this.nhaCungCap = nhaCungCap;
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
        return "PhieuNhap{" + "maPN='" + maPN + '\'' + ", tongTien=" + tongTien + '}';
    }
}
