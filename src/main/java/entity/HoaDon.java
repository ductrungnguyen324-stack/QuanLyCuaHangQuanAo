package entity;

import java.time.LocalDateTime;

public class HoaDon
{
    String maHD;
    String maKH;
    String maNV;
    LocalDateTime ngaytao;
    String maKM;
    double tongtien;
    double sotiengiam;
    double thanhtoan;
    String phuongthucTT; // ENUM : "TIENMAT", "CHUYENKHOAN", "MOMO", "VNPAY", "ZaloPay"
    String trangthai; // ENUM : "CHUATHANHTOAN", "DATHANHTOAN"

    public HoaDon()
    {
        this.ngaytao = LocalDateTime.now();
        this.phuongthucTT = "TIENMAT";
        this.trangthai = "CHUATHANHTOAN";
        this.sotiengiam = 0;
    }

    public HoaDon(String maKH, String maNV)
    {
        this();
        this.maKH = maKH;
        this.maNV = maNV;
    }

    public HoaDon(String maHD, String maKH, String maNV, LocalDateTime ngaytao, String khuyenmai, double tongtien, double sotiengiam, double thanhtoan, String phuongthucTT, String trangthai) {
        this.maHD = maHD;
        this.maKH = maKH;
        this.maNV = maNV;
        this.ngaytao = ngaytao;
        this.maKM = khuyenmai;
        this.tongtien = tongtien;
        this.sotiengiam = sotiengiam;
        this.thanhtoan = thanhtoan;
        this.phuongthucTT = phuongthucTT;
        this.trangthai = trangthai;
    }

    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public LocalDateTime getNgaytao() {
        return ngaytao;
    }

    public void setNgaytao(LocalDateTime ngaytao) {
        this.ngaytao = ngaytao;
    }

    public String getKhuyenmai() {
        return maKM;
    }

    public void setKhuyenmai(String khuyenmai) {
        this.maKM = khuyenmai;
    }

    public double getTongtien() {
        return tongtien;
    }

    public void setTongtien(double tongtien) {
        this.tongtien = tongtien;
    }

    public double getSotiengiam() {
        return sotiengiam;
    }

    public void setSotiengiam(double sotiengiam) {
        this.sotiengiam = sotiengiam;
    }

    public double getThanhtoan() {
        return thanhtoan;
    }

    public void setThanhtoan(double thanhtoan) {
        this.thanhtoan = thanhtoan;
    }

    public String getPhuongthucTT() {
        return phuongthucTT;
    }

    public void setPhuongthucTT(String phuongthucTT) {
        this.phuongthucTT = phuongthucTT;
    }

    public String getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(String trangthai) {
        this.trangthai = trangthai;
    }
}