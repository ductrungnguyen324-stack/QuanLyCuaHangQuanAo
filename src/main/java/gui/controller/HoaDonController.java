package gui.controller;

import bus.HoaDonBUS;
import bus.ChiTietHoaDonBUS;
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
    private LoginController login;

    public HoaDonController(HoaDonView view, String login) {
        sorter = new TableRowSorter<>(view.getTable().getModel());
        view.getTable().setRowSorter(sorter);
        this.view = view;
        loadDanhSach();
        allListener();
    }

    public void allListener() {
        view.getBtnThem().addActionListener(e -> handleThem());

        view.getSearchField().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                handleTimKiem();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                handleTimKiem();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                handleTimKiem();
            }
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
                if(row < 0) return;

                int modeleRow = table.convertRowIndexToModel(row);
                String maHD = (String) view.getTableModel().getValueAt(modeleRow, 0);
                int col = table.columnAtPoint(e.getPoint());

                if(col == 11) {
                    Rectangle rect = table.getCellRect(row, col, true);
                    int third = rect.width / 3;
                    int dx    = e.getX() - rect.x;
                    if (dx < third)          handleXemChiTiet(maHD);
                    else if (dx < third * 2) handleIn(maHD);
                    else                     handleXoa(maHD);
                } else if (e.getClickCount() == 2) {
                    // Double click bất kỳ cột nào → xem chi tiết
                    handleXemChiTiet(maHD);
                }
            }
            @Override public void mouseMoved(MouseEvent e) {
                JTable table = view.getTable();
                int col      = table.columnAtPoint(e.getPoint());
                table.setCursor(col == 11
                        ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                        : Cursor.getDefaultCursor());
            }
        });
    }
    public void loadDanhSach() {
        try {
            List<HoaDon> list = hoadonbus.getAll();
            view.renderDanhSach(list);
            view.updateStats();
        } catch(Exception ex) {
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
            // Lấy HoaDon + danh sách ChiTietHoaDon từ BUS
            HoaDon hd              = hoadonbus.getById(maHD);
            if (hd == null) { view.showError("Không tìm thấy hoá đơn: " + maHD); return; }

            List<ChiTietHoaDon> ct = cthdbus.getAllByMaHD(maHD);

            // Mở dialog — truyền đủ dữ liệu thật
            ChiTietHoaDonDialog dialog = new ChiTietHoaDonDialog(
                    (java.awt.Frame) SwingUtilities.getWindowAncestor(view), hd, ct);
            dialog.setVisible(true);

            // Nếu người dùng bấm "Duyệt thanh toán" bên trong dialog
            if (dialog.isDuyet()) {
                hd.setTrangthai("DATHANHTOAN");
                hoadonbus.update(hd);
                loadDanhSach();
                view.showSuccess("Hoá đơn " + maHD + " đã thanh toán thành công!");
            }

            // Reload lại bảng dù có duyệt hay không
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
            filters.add(RowFilter.regexFilter("^" + phuongThuc + "$", 9));  // col 9

        if (!"Tất cả trạng thái".equals(trangThai))
            filters.add(RowFilter.regexFilter("^" + trangThai + "$", 10)); // col 10

        sorter.setRowFilter(filters.isEmpty() ? null : RowFilter.andFilter(filters));
    }

    private void handleThem() {
        HoaDonDialog dialog = new HoaDonDialog(
                (Frame) SwingUtilities.getWindowAncestor(view), null, "NV001");
        dialog.setVisible(true);

        if (dialog.getKetQua() == null) return; // người dùng huỷ

        // Validate trước khi gọi BUS
        String loi = validate(dialog);
        if (loi != null) {
            view.showError(loi);
            return;
        }

        try {
            HoaDon hd              = dialog.buildHoaDon();
            List<ChiTietHoaDon> cthd = dialog.buildChiTiet();
            hoadonbus.add(hd);        // lưu hoá đơn trước
            for(ChiTietHoaDon ct : cthd) {
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
            // TODO: PrintUtil.inHoaDon(hd);
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

            // Không cho xoá hoá đơn đã thanh toán
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
        if (dialog.getMaKH() == null || dialog.getMaKH().isEmpty())
            return "Mã khách hàng không được trống!";
        if (dialog.getSoLuongSP() == 0)
            return "Hoá đơn phải có ít nhất 1 sản phẩm!";
        if (dialog.getTongTien() <= 0)
            return "Tổng tiền phải lớn hơn 0!";
        return null;
    }

}