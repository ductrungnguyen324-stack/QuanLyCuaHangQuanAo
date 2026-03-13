package gui.controller;

import bus.SanPhamBUS;
import entity.SanPham;
import gui.dialog.SanPhamDialog;
import gui.view.SanPhamView;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class SanPhamController {

    private final SanPhamView view;
    private final SanPhamBUS  bus;
    private TableRowSorter<TableModel> sorter;

    // Cache — không gọi DB mỗi lần gõ phím
    private List<SanPham> danhSachGoc = new ArrayList<>();

    public SanPhamController(SanPhamView view) {
        this.view = view;
        sorter =
                new TableRowSorter<>(view.getTable().getModel());
        view.getTable().setRowSorter(sorter);
        this.bus  = new SanPhamBUS();
        initEvents();
        loadDanhSach();
    }

    // ── Events ────────────────────────────────────────────────────────────────
    private void initEvents() {
        // Tìm kiếm + filter — lọc trên cache
        view.getSearchField().getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e){ loc(); }
            public void removeUpdate(DocumentEvent e){ loc(); }
            public void changedUpdate(DocumentEvent e){ loc(); }
        });
        view.getCbLoai().addActionListener(e -> loc());
        view.getCbTrangThai().addActionListener(e -> loc());

        // Nút thêm
        view.getBtnThem().addActionListener(e -> them());

        // Nút làm mới
        view.getBtnReset().addActionListener(e -> {
            view.getSearchField().setText("");
            view.getCbLoai().setSelectedIndex(0);
            view.getCbTrangThai().setSelectedIndex(0);
            loadDanhSach();
        });

        // Click vào tag Sửa / Xóa trong cột Thao tác
        view.getTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = view.getTable().rowAtPoint(e.getPoint());
                int col = view.getTable().columnAtPoint(e.getPoint());
                if (row < 0 || col != 9) return; // col 9 = Thao tác

                String maSP = view.getTableModel().getValueAt(row, 0).toString();

                // Xác định click vào tag nào dựa theo vị trí X trong ô
                // tag "Sửa" nằm bên trái, "Xóa" bên phải
                java.awt.Rectangle cellRect = view.getTable().getCellRect(row, col, true);
                int relX = e.getX() - cellRect.x;
                if (relX < cellRect.width / 2) {
                    sua(maSP);
                } else {
                    xoa(maSP);
                }
            }
        });
    }

    // ── Load & Lọc ───────────────────────────────────────────────────────────
    public void loadDanhSach() {
        danhSachGoc = bus.getAll();
        view.renderDanhSach(danhSachGoc);
    }

    private void loc() {
        String keyword = view.getSearchField().getText().trim();

        if (keyword.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + keyword));
        }
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────
    private void them() {
        SanPhamDialog dlg = new SanPhamDialog(null, null);
        dlg.setVisible(true);
        if (!dlg.isConfirmed()) return;

        try {
            if (bus.add(dlg.getKetQua())) {
                loadDanhSach();
                view.showSuccess("Thêm sản phẩm thành công!");
            } else {
                view.showError("Thêm thất bại! Kiểm tra lại thông tin.");
            }
        } catch (Exception ex) {
            view.showError("Lỗi: " + ex.getMessage());
        }
    }

    private void sua(String maSP) {
        SanPham spHienTai = bus.getById(maSP);
        if (spHienTai == null) { view.showError("Không tìm thấy sản phẩm: " + maSP); return; }

        SanPhamDialog dlg = new SanPhamDialog(null, spHienTai);
        dlg.setVisible(true);
        if (!dlg.isConfirmed()) return;

        try {
            if (bus.update(dlg.getKetQua())) {
                loadDanhSach();
                view.showSuccess("Cập nhật sản phẩm thành công!");
            } else {
                view.showError("Cập nhật thất bại!");
            }
        } catch (Exception ex) {
            view.showError("Lỗi: " + ex.getMessage());
        }
    }

    private void xoa(String maSP) {
        SanPham sp = bus.getById(maSP);
        if (sp == null) { view.showError("Không tìm thấy sản phẩm: " + maSP); return; }

        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Ngừng bán sản phẩm: " + maSP + " - " + sp.getTensp() + "?\n(Trạng thái sẽ đổi thành HETHANG)",
                "Xác nhận ngừng bán",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            sp.setTrangthai("HETHANG");
            if (bus.update(sp)) {
                loadDanhSach();
                view.showSuccess("Đã ngừng bán sản phẩm " + maSP + "!");
            } else {
                view.showError("Thao tác thất bại!");
            }
        } catch (Exception ex) {
            view.showError("Lỗi: " + ex.getMessage());
        }
    }
}