package entity;

public class ChiTietPhieuNhap
{
    String maCTPN;
    String maPN;
    String maSP;
    double soluong;
    double gianhap;
    double thanhtien;

    public ChiTietPhieuNhap() {}

    public ChiTietPhieuNhap(String maCTPN, String maPN, String maSP, double soluong, double gianhap, double thanhtien) {
        this.maCTPN = maCTPN;
        this.maPN = maPN;
        this.maSP = maSP;
        this.soluong = soluong;
        this.gianhap = gianhap;
        this.thanhtien = soluong * gianhap;
    }

    public String getMaCTPN() {
        return maCTPN;
    }

    public void setMaCTPN(String maCTPN) {
        this.maCTPN = maCTPN;
    }

    public String getMaPN() {
        return maPN;
    }

    public void setMaPN(String maPN) {
        this.maPN = maPN;
    }

    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        this.maSP = maSP;
    }

    public double getSoluong() {
        return soluong;
    }

    public void setSoluong(double soluong) {
        this.soluong = soluong;
        TinhThanhTien();
    }

    public double getGianhap() {
        return gianhap;
    }

    public void setGianhap(double gianhap) {
        this.gianhap = gianhap;
        TinhThanhTien();
    }

    public double getThanhtien() {
        return thanhtien;
    }

    public void setThanhtien(double thanhtien) {
        this.thanhtien = thanhtien;
    }

    public void TinhThanhTien()
    {
        this.thanhtien = this.soluong * this.gianhap;
    }
}