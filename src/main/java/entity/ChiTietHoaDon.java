package entity;

public class ChiTietHoaDon
{
    String maCTHD;
    String maHD;
    String maSP;
    String tenSP;
    double soluong;
    double dongia;
    double thanhtien;
    KhuyenMai khuyenmai;

    public ChiTietHoaDon() {}

    public ChiTietHoaDon(String maHD, String maSP, String tenSP, int soluong, double dongia) {
        this.maHD = maHD;
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.soluong = soluong;
        this.dongia = dongia;
        this.thanhtien = dongia * soluong;
    }

    public ChiTietHoaDon(String maCTHD, String maHD, String maSP, String tenSP, double soluong, double dongia, double thanhtien) {
        this.maCTHD = maCTHD;
        this.maHD = maHD;
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.soluong = soluong;
        this.dongia = dongia;
        this.thanhtien = thanhtien;
    }

    // Phương thức áp dụng mã giảm giá cho món
    public void ApDungKhuyenMai(KhuyenMai km)
    {
        if (km != null && km.isKhaDung())
            this.khuyenmai = km;
    }

    public double SoTienGiamTheoSanPham()
    {
        if (khuyenmai != null)
            return khuyenmai.tinhSoTienGiam(this.dongia * this.soluong);
        return 0;
    }

    // Thành tiền của món sau khi trừ khuyến mãi riêng của nó
    public void TinhThanhTien() {
        this.thanhtien = (this.dongia * this.soluong) - SoTienGiamTheoSanPham();
    }

    public double getThanhtien() {
        return thanhtien;
    }

    public void setThanhtien(double thanhtien) {
        this.thanhtien = thanhtien;
    }

    public String getMaCTHD() {
        return maCTHD;
    }

    public void setMaCTHD(String maCTHD) {
        this.maCTHD = maCTHD;
    }

    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }

    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        this.maSP = maSP;
    }

    public String getTenSP() {
        return tenSP;
    }

    public void setTenSP(String tenSP) {
        this.tenSP = tenSP;
    }

    public double getSoluong() {
        return soluong;
    }

    public void setSoluong(double soluong) {
        this.soluong = soluong;
        TinhThanhTien();
    }

    public double getDongia() {
        return dongia;
    }

    public void setDongia(double dongia) {
        this.dongia = dongia;
        TinhThanhTien();
    }

    public KhuyenMai getKhuyenmai() {
        return khuyenmai;
    }

    public void setKhuyenmai(KhuyenMai khuyenmai) {
        this.khuyenmai = khuyenmai;
    }
}