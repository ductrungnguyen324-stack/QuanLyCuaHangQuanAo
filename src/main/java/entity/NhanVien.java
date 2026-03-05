package entity;

public class NhanVien
{
    String maNV;
    String hoten;
    String sdt;
    String chucvu;
    String tendannhap;
    String matkhau;
    String trangthai;

    NhanVien() {}

    public NhanVien(String maNV, String hoten, String sdt, String chucvu, String tendannhap, String matkhau, String trangthai)
    {
        this.maNV = maNV;
        this.hoten = hoten;
        this.sdt = sdt;
        this.chucvu = chucvu;
        this.tendannhap = tendannhap;
        this.matkhau = matkhau;
        this.trangthai = trangthai;
    }

    public String getManv() {
        return maNV;
    }

    public void setManv(String maNV) {
        this.maNV = maNV;
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

    public String getChucvu() {
        return chucvu;
    }

    public void setChucvu(String chucvu) {
        this.chucvu = chucvu;
    }

    public String getTendannhap() {
        return tendannhap;
    }

    public void setTendannhap(String tendannhap) {
        this.tendannhap = tendannhap;
    }

    public String getMatkhau() {
        return matkhau;
    }

    public void setMatkhau(String matkhau) {
        this.matkhau = matkhau;
    }

    public String getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(String trangthai) {
        this.trangthai = trangthai;
    }
}