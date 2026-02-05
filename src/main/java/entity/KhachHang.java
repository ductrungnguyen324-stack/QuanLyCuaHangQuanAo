package entity;

import java.time.LocalDate;

public class KhachHang
{
    String maKH;
    String hoten;
    String sdt;
    LocalDate ngaythamgia;
    static int soluong;

    public KhachHang() {}

    public KhachHang(String maKH, String hoten, String sdt, LocalDate ngaythamgia)
    {
        this.maKH = maKH;
        this.hoten = hoten;
        this.sdt = sdt;
        this.ngaythamgia = ngaythamgia;
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String makh) {
        this.maKH = maKH;
    }

    public String getHoten() {
        return hoten;
    }

    public void setHoten(String hoten) {
        this.hoten = hoten;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public LocalDate getNgaythamgia() {
        return ngaythamgia;
    }

    public void setNgaythamgia(LocalDate ngaythamgia) {
        this.ngaythamgia = ngaythamgia;
    }

    public static int getSoluong() {
        return soluong;
    }

    public static void setSoluong(int soluong) {
        KhachHang.soluong = soluong;
    }
}



