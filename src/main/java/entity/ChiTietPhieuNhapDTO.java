package entity;

public class ChiTietPhieuNhapDTO {

    private String maCTPN;
    private String maPN;
    private String maSP;
    private int soLuong;
    private double donGia;
    private double thanhTien;

    public ChiTietPhieuNhapDTO() {
    }

    public ChiTietPhieuNhapDTO(String maCTPN, String maPN, String maSP, int soLuong, double donGia, double thanhTien) {
        this.maCTPN = maCTPN;
        this.maPN = maPN;
        this.maSP = maSP;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.thanhTien = thanhTien;
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

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        this.donGia = donGia;
    }

    public double getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(double thanhTien) {
        this.thanhTien = thanhTien;
    }

    public double tinhThanhTien() {
        return this.soLuong * this.donGia;
    }
}
