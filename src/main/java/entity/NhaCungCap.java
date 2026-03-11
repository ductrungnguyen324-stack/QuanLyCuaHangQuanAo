package entity;

public class NhaCungCap {
    String maNCC;
    String tenNCC;
    String diachi;
    String sodienthoai;


    public NhaCungCap() {
    }

    public NhaCungCap(String tenNCC, String diachi, String sodienthoai, String email) {
        this.tenNCC = tenNCC;
        this.diachi = diachi;
        this.sodienthoai = sodienthoai;
    }

    public String getMaNCC() {
        return maNCC;
    }

    public void setMaNCC(String maNCC) {
        this.maNCC = maNCC;
    }

    public String getTenNCC() {
        return tenNCC;
    }

    public void setTenNCC(String tenNCC) {
        this.tenNCC = tenNCC;
    }

    public String getDiachi() {
        return diachi;
    }

    public void setDiachi(String diachi) {
        this.diachi = diachi;
    }

    public String getSodienthoai() {
        return sodienthoai;
    }

    public void setSodienthoai(String sodienthoai) {
        this.sodienthoai = sodienthoai;
    }
}

