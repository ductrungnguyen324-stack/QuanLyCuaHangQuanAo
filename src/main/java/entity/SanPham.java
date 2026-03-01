package entity;

public class SanPham
{
    String maSP;
    String tenSP;
    String loaiSP;
    double giaban;
    String thuonghieu;
    String kichco;
    String mausac;
    String trangthai;
    int tonkho;
    static int soluong;

    public SanPham() {}

    public SanPham(String maSP, String tenSP, String loaiSP, double giaban, String thuonghieu, String kichco, String mausac, String trangthai, int tonkho) {
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.loaiSP = loaiSP;
        this.giaban = giaban;
        this.thuonghieu = thuonghieu;
        this.kichco = kichco;
        this.mausac = mausac;
        this.trangthai = trangthai;
        this.tonkho = tonkho;
    }

    public String getMasp() {
        return maSP;
    }

    public void setMasp(String maSP) {
        this.maSP = maSP;
    }

    public String getTensp() {
        return tenSP;
    }

    public void setTensp(String tenSP) {
        this.tenSP = tenSP;
    }

    public String getLoaisp() {
        return loaiSP;
    }

    public void setLoaisp(String loaiSP) {
        this.loaiSP = loaiSP;
    }

    public double getGiaban() {
        return giaban;
    }

    public void setGiaban(double giaban) {
        this.giaban = giaban;
    }

    public String getThuonghieu() {
        return thuonghieu;
    }

    public void setThuonghieu(String thuonghieu) {
        this.thuonghieu = thuonghieu;
    }

    public String getKichco() {
        return kichco;
    }

    public void setKichco(String kichco) {
        this.kichco = kichco;
    }

    public String getMausac() {
        return mausac;
    }

    public void setMausac(String mausac) {
        this.mausac = mausac;
    }

    public String getTrangthai() {
        return this.trangthai;
    }

    public void setTrangthai(String trangthai) {
        this.trangthai = trangthai;
    }

    public int getTonkho() {
        return this.tonkho;
    }

    public void setTonkho(int tonkho) {
        this.tonkho = tonkho;
    }

    public static int getSoluong() {
        return soluong;
    }

    public static void setSoluong(int soluong) {
        SanPham.soluong = soluong;
    }


}