package gui.controller;

import bus.HoaDonBUS;
import bus.ChiTietHoaDonBUS;
import bus.SanPhamBUS;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import gui.HoaDonDialog;
import gui.view.HoaDonView;
import gui.dialog.ChiTietHoaDonDialog;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.awt.Rectangle;

public class HoaDonController {
    private HoaDonView view;
    private HoaDonBUS hoadonbus = new HoaDonBUS();
    private ChiTietHoaDonBUS cthdbus = new ChiTietHoaDonBUS();
    private TableRowSorter<TableModel> sorter;

    // ── THÊM MỚI: lưu chức vụ để kiểm tra quyền ──
    private String chucvu;

    public HoaDonController(HoaDonView view, String maNV, String chucvu) {
        sorter = new TableRowSorter<>(view.getTable().getModel());
        view.getTable().setRowSorter(sorter);
        this.view   = view;
        this.chucvu = chucvu;
        loadDanhSach();
        allListener();
        applyQuyen(); // ← áp dụng phân quyền sau khi khởi tạo
    }

    // ── THÊM MỚI: áp dụng quyền dựa theo chức vụ ──
    private void applyQuyen() {
        // Chỉ Quản lý và Thu ngân được thao tác hoá đơn
        boolean coQuyen = "Quan ly".equals(chucvu) || "Thu ngan".equals(chucvu);
        view.getBtnThem().setVisible(coQuyen);
        view.getBtnThem().setEnabled(coQuyen);
        view.setChiXem(!coQuyen); // truyền flag vào view để ẩn nút Xoá/In trong bảng
    }

    public void allListener() {
        view.getBtnThem().addActionListener(e -> handleThem());

        view.getSearchField().getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { handleTimKiem(); }
            @Override public void removeUpdate(DocumentEvent e)  { handleTimKiem(); }
            @Override public void changedUpdate(DocumentEvent e) { handleTimKiem(); }
        });

        view.getCbPhuongThuc().addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) handleLoc();
        });

        view.getCbTrangThai().addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) handleLoc();
        });

        view.getBtnReset().addActionListener(e -> handleReset());

        view.getTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTable table = view.getTable();
                int row = table.rowAtPoint(e.getPoint());
                if (row < 0) return;

                int modeleRow = table.convertRowIndexToModel(row);
                String maHD = (String) view.getTableModel().getValueAt(modeleRow, 0);
                int col = table.columnAtPoint(e.getPoint());

                if (col == 11) {
                    Rectangle rect  = table.getCellRect(row, col, true);
                    int third = rect.width / 3;
                    int dx    = e.getX() - rect.x;

                    if (dx < third) {
                        // Xem chi tiết — tất cả đều được xem
                        handleXemChiTiet(maHD);
                    } else if (dx < third * 2) {
                        // In — chỉ có quyền mới in
                        if (coQuyen()) handleIn(maHD);
                        else showNoQuyen();
                    } else {
                        // Xoá — chỉ có quyền mới xoá
                        if (coQuyen()) handleXoa(maHD);
                        else showNoQuyen();
                    }
                } else if (e.getClickCount() == 2) {
                    handleXemChiTiet(maHD);
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                JTable table = view.getTable();
                int col = table.columnAtPoint(e.getPoint());
                table.setCursor(col == 11
                        ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                        : Cursor.getDefaultCursor());
            }
        });
    }

    // ── Helper kiểm tra quyền ──
    private boolean coQuyen() {
        return "Quan ly".equals(chucvu) || "Thu ngan".equals(chucvu);
    }

    private void showNoQuyen() {
        JOptionPane.showMessageDialog(view,
                "Bạn không có quyền thực hiện thao tác này!",
                "Không có quyền", JOptionPane.WARNING_MESSAGE);
    }

    public void loadDanhSach() {
        try {
            List<HoaDon> list = hoadonbus.getAll();
            view.renderDanhSach(list);
            view.updateStats();
        } catch (Exception ex) {
            view.showError("Không thể tải danh sách hoá đơn!\n" + ex.getMessage());
        }
    }

    public void handleTimKiem() {
        String keyword = view.getSearchField().getText().trim();
        if (keyword.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + keyword));
        }
    }

    private void handleXemChiTiet(String maHD) {
        try {
            HoaDon hd = hoadonbus.getById(maHD);
            if (hd == null) { view.showError("Không tìm thấy hoá đơn: " + maHD); return; }

            List<ChiTietHoaDon> ct = cthdbus.getAllByMaHD(maHD);

            ChiTietHoaDonDialog dialog = new ChiTietHoaDonDialog(
                    (java.awt.Frame) SwingUtilities.getWindowAncestor(view), hd, ct);
            dialog.setVisible(true);

            if (dialog.isDuyet()) {
                // Chỉ cho duyệt thanh toán nếu có quyền
                if (!coQuyen()) {
                    showNoQuyen();
                    return;
                }
                hd.setTrangthai("DATHANHTOAN");
                hoadonbus.update(hd);
                List<ChiTietHoaDon> ctList = cthdbus.getAllByMaHD(maHD);
                SanPhamBUS spbus = new SanPhamBUS();
                for (ChiTietHoaDon cthd : ctList) {
                    spbus.giamTonKho(cthd.getMaSP(), (int) cthd.getSoluong());
                }
                loadDanhSach();
                view.showSuccess("Hoá đơn " + maHD + " đã thanh toán thành công!");
            }
            loadDanhSach();
        } catch (Exception ex) {
            view.showError("Lỗi tải chi tiết: " + ex.getMessage());
        }
    }

    private void handleLoc() {
        String keyword    = view.getSearchField().getText().trim();
        String phuongThuc = (String) view.getCbPhuongThuc().getSelectedItem();
        String trangThai  = (String) view.getCbTrangThai().getSelectedItem();

        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        if (!keyword.isEmpty())
            filters.add(RowFilter.regexFilter("(?i)" + keyword));

        if (!"Tất cả phương thức".equals(phuongThuc))
            filters.add(RowFilter.regexFilter("^" + phuongThuc + "$", 9));

        if (!"Tất cả trạng thái".equals(trangThai))
            filters.add(RowFilter.regexFilter("^" + trangThai + "$", 10));

        sorter.setRowFilter(filters.isEmpty() ? null : RowFilter.andFilter(filters));
    }

    private void handleThem() {
        if (!coQuyen()) { showNoQuyen(); return; }

        HoaDonDialog dialog = new HoaDonDialog(
                (Frame) SwingUtilities.getWindowAncestor(view), null, "NV001");
        dialog.setVisible(true);

        if (dialog.getKetQua() == null) return;

        String loi = validate(dialog);
        if (loi != null) { view.showError(loi); return; }

        try {
            HoaDon hd              = dialog.buildHoaDon();
            hoadonbus.add(hd);
            List<ChiTietHoaDon> cthd = dialog.buildChiTiet();
            for (ChiTietHoaDon ct : cthd) {
                ct.setMaHD(hd.getMaHD());
                cthdbus.add(ct);
            }
            loadDanhSach();
            view.showSuccess("Tạo hoá đơn " + hd.getMaHD() + " thành công!");
        } catch (Exception ex) {
            view.showError("Tạo hoá đơn thất bại!\n" + ex.getMessage());
        }
    }

    private void handleIn(String maHD) {
        try {
            HoaDon hd = hoadonbus.getById(maHD);
            if (hd == null) { view.showError("Không tìm thấy hoá đơn: " + maHD); return; }
            JOptionPane.showMessageDialog(view,
                    "Đang in hoá đơn: " + maHD,
                    "In hoá đơn", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            view.showError("Lỗi in hoá đơn: " + ex.getMessage());
        }
    }

    private void handleReset() {
        view.getSearchField().setText("");
        view.getCbPhuongThuc().setSelectedIndex(0);
        view.getCbTrangThai().setSelectedIndex(0);
        loadDanhSach();
    }

    private void handleXoa(String maHD) {
        try {
            HoaDon hd = hoadonbus.getById(maHD);
            if (hd == null) { view.showError("Không tìm thấy hoá đơn: " + maHD); return; }

            if ("DATHANHTOAN".equals(hd.getTrangthai())) {
                view.showError("Không thể xoá hoá đơn đã thanh toán!");
                return;
            }

            int ok = JOptionPane.showConfirmDialog(view,
                    "Xoá hoá đơn: " + maHD + "?\nHành động này không thể hoàn tác.",
                    "Xác nhận xoá", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (ok == JOptionPane.YES_OPTION) {
                hoadonbus.delete(maHD);
                loadDanhSach();
                view.showSuccess("Đã xoá hoá đơn " + maHD);
            }
        } catch (Exception ex) {
            view.showError("Xoá thất bại!\n" + ex.getMessage());
        }
    }

    private String validate(HoaDonDialog dialog) {
        if (dialog.getMaHD() == null || dialog.getMaHD().isEmpty())
            return "Mã hoá đơn không được trống!";
        if (dialog.getSoLuongSP() == 0)
            return "Hoá đơn phải có ít nhất 1 sản phẩm!";
        if (dialog.getTongTien() <= 0)
            return "Tổng tiền phải lớn hơn 0!";
        return null;
    }
}