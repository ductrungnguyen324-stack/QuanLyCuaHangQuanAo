package entity;

import java.time.LocalDateTime;

public class KhuyenMai
{
    String maKM;
    String tenKM;
    String loaiKM; // ENUM : "PHAMTRAM", "TIENCODINH"
    double giatrigiam;
    double giamtoida; // Mức giảm trần
    double giatridonhangtoithieu; // Đơn hàng đạt mức này mới được áp dụng
    LocalDateTime ngaybatdau;
    LocalDateTime ngayketthuc;
    int soluong;
    int dasudung;

    // Kiểm tra xem
    public boolean isKhaDung()
    {
        LocalDateTime baygio = LocalDateTime.now();
        return (dasudung < soluong) && (baygio.isAfter(ngaybatdau) && baygio.isBefore(ngayketthuc));
    }

    /**
     * Tính toán số được được giảm dựa trên tổng tiền hóa đơn
     * @param tongtienhoadon
     * @return Số tiền thực tế được giảm
     */
    public double tinhSoTienGiam(double tongtienhoadon)
    {
        if (!isKhaDung() || tongtienhoadon < giatridonhangtoithieu)
            return 0;

        double sotienduocgiam = 0;
        if (loaiKM.equals("TIENCODINH"))
            sotienduocgiam = giatrigiam;
        else if (loaiKM.equals("PHANTRAM"))
        {
            sotienduocgiam = tongtienhoadon * (giatrigiam / 100);
            if (sotienduocgiam > giamtoida)
                sotienduocgiam = giamtoida;
        }
        return sotienduocgiam;
    }

    public KhuyenMai() {}

    public KhuyenMai(String maKM, String tenKM, String loaiKM, double giatrigiam, double giamtoida, double giatridonhangtoithieu, LocalDateTime ngaybatdau, LocalDateTime ngayketthuc, int soluong, int dasudung) {
        this.maKM = maKM;
        this.tenKM = tenKM;
        this.loaiKM = loaiKM;
        this.giatrigiam = giatrigiam;
        this.giamtoida = giamtoida;
        this.giatridonhangtoithieu = giatridonhangtoithieu;
        this.ngaybatdau = ngaybatdau;
        this.ngayketthuc = ngayketthuc;
        this.soluong = soluong;
        this.dasudung = dasudung;
    }

    public String getMaKM() {
        return maKM;
    }

    public void setMaKM(String maKM) {
        this.maKM = maKM;
    }

    public String getTenKM() {
        return tenKM;
    }

    public void setTenKM(String tenKM) {
        this.tenKM = tenKM;
    }

    public String getLoaiKM() {
        return loaiKM;
    }

    public void setLoaiKM(String loaiKM) {
        this.loaiKM = loaiKM;
    }

    public double getGiatrigiam() {
        return giatrigiam;
    }

    public void setGiatrigiam(double giatrigiam) {
        this.giatrigiam = giatrigiam;
    }

    public double getGiamtoida() {
        return giamtoida;
    }

    public void setGiamtoida(double giamtoida) {
        this.giamtoida = giamtoida;
    }

    public double getGiatridonhangtoithieu() {
        return giatridonhangtoithieu;
    }

    public void setGiatridonhangtoithieu(double giatridonhangtoithieu) {
        this.giatridonhangtoithieu = giatridonhangtoithieu;
    }

    public LocalDateTime getNgaybatdau() {
        return ngaybatdau;
    }

    public void setNgaybatdau(LocalDateTime ngaybatdau) {
        this.ngaybatdau = ngaybatdau;
    }

    public LocalDateTime getNgayketthuc() {
        return ngayketthuc;
    }

    public void setNgayketthuc(LocalDateTime ngayketthuc) {
        this.ngayketthuc = ngayketthuc;
    }

    public int getSoluong() {
        return soluong;
    }

    public void setSoluong(int soluong) {
        this.soluong = soluong;
    }

    public int getDasudung() {
        return dasudung;
    }

    public void setDasudung(int dasudung) {
        this.dasudung = dasudung;
    }
}