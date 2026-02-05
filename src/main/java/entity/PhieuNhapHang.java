package entity;

import java.time.LocalDateTime;

public class PhieuNhapHang
{
    String maPN;
    String maNV;
    String nhacungcap;
    LocalDateTime ngaytao;
    double thanhtien;
    String trangthai;

    public PhieuNhapHang() {}

    public PhieuNhapHang(String maPN, String maNV, String nhacungcap, LocalDateTime ngaytao, double thanhtien, String trangthai) {
        this.maPN = maPN;
        this.maNV = maNV;
        this.nhacungcap = nhacungcap;
        this.ngaytao = ngaytao;
        this.thanhtien = thanhtien;
        this.trangthai = trangthai;
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

    public String getNhacungcap() {
        return nhacungcap;
    }

    public void setNhacungcap(String nhacungcap) {
        this.nhacungcap = nhacungcap;
    }

    public LocalDateTime getNgaytao() {
        return ngaytao;
    }

    public void setNgaytao(LocalDateTime ngaytao) {
        this.ngaytao = ngaytao;
    }

    public double getThanhtien() {
        return thanhtien;
    }

    public void setThanhtien(double thanhtien) {
        this.thanhtien = thanhtien;
    }

    public String getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(String trangthai) {
        this.trangthai = trangthai;
    }
}