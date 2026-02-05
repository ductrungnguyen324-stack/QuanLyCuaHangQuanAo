package entity;

public class NhanVien
{
    String maNV;
    String hoten;
    String sdt;
    String chucvu;
    static int soluong;

    NhanVien() {}

    public NhanVien(String maNV, String hoten, String sdt, String chucvu)
    {
        this.maNV = maNV;
        this.hoten = hoten;
        this.sdt = sdt;
        this.chucvu = chucvu;
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

    public static int getSoluong() {
        return soluong;
    }

    public static void setSoluong(int soluong) {
        NhanVien.soluong = soluong;
    }
}