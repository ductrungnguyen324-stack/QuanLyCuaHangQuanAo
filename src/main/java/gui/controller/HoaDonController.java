package gui.controller;

import bus.HoaDonBUS;
import gui.view.HoaDonView;

public class HoaDonController {
    private HoaDonView view;
    private HoaDonBUS hoadonbus = new HoaDonBUS();

    public HoaDonController(HoaDonView view) {
        this.view = view;
        loadDanhSach();
        allListener();
    }

    public void loadDanhSach() {

    }
    public void allListener() {

    }
}