package gui.view.dialogs;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.*;
import java.util.Locale;

/**
 * Component chọn ngày + giờ kiểu dark theme.
 * Dùng như JComponent thông thường, gọi getValue() / setValue().
 */
public class DatePickerField extends JPanel {

    // ── Palette ───────────────────────────────────────────
    private static final Color BG      = new Color(7,  10, 20);
    private static final Color SURFACE = new Color(11, 15, 30);
    private static final Color CARD    = new Color(14, 20, 40);
    private static final Color CARD2   = new Color(18, 26, 52);
    private static final Color BORDER  = new Color(30, 42, 72);
    private static final Color ACCENT  = new Color(99, 102, 241);
    private static final Color TEXT1   = new Color(226, 232, 240);
    private static final Color TEXT2   = new Color(100, 116, 139);
    private static final Color HOVER   = new Color(30, 38, 72);
    private static final Color TODAY   = new Color(30, 42, 90);

    private static final DateTimeFormatter DISPLAY_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private LocalDateTime value;
    private final JTextField txtDisplay;
    private final String placeholder;

    public DatePickerField(String placeholder) {
        this.placeholder = placeholder;
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);

        // ── Text hiển thị (read-only) ─────────────────────
        txtDisplay = new JTextField();
        txtDisplay.setEditable(false);
        txtDisplay.setBackground(new Color(20, 28, 52));
        txtDisplay.setForeground(TEXT2);
        txtDisplay.setCaretColor(ACCENT);
        txtDisplay.setFont(new Font("Dialog", Font.PLAIN, 13));
        txtDisplay.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 8)
        ));
        txtDisplay.setText(placeholder);
        txtDisplay.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // ── Nút lịch ──────────────────────────────────────
        JButton btnPick = new JButton("📅");
        btnPick.setFont(new Font("Dialog", Font.PLAIN, 14));
        btnPick.setBackground(CARD);
        btnPick.setForeground(TEXT1);
        btnPick.setOpaque(true);
        btnPick.setBorderPainted(false);
        btnPick.setFocusPainted(false);
        btnPick.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnPick.setPreferredSize(new Dimension(36, 0));
        btnPick.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(0, 4, 0, 4)
        ));

        ActionListener openCalendar = e -> openCalendarDialog();
        btnPick.addActionListener(openCalendar);
        txtDisplay.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { openCalendarDialog(); }
        });

        add(txtDisplay, BorderLayout.CENTER);
        add(btnPick,    BorderLayout.EAST);
    }

    // ── Getter / Setter ───────────────────────────────────
    public LocalDateTime getValue() { return value; }

    public void setValue(LocalDateTime dt) {
        this.value = dt;
        if (dt != null) {
            txtDisplay.setText(dt.format(DISPLAY_FMT));
            txtDisplay.setForeground(TEXT1);
        } else {
            txtDisplay.setText(placeholder);
            txtDisplay.setForeground(TEXT2);
        }
    }

    // ── Mở calendar popup ─────────────────────────────────
    private void openCalendarDialog() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        CalendarDialog dialog = new CalendarDialog(
                parent instanceof Frame ? (Frame) parent : null,
                value != null ? value : LocalDateTime.now()
        );
        dialog.setVisible(true);
        if (dialog.getResult() != null)
            setValue(dialog.getResult());
    }

    // ════════════════════════════════════════════════════════
    //  Inner class: CalendarDialog
    // ════════════════════════════════════════════════════════
    private static class CalendarDialog extends JDialog {

        private LocalDate   selectedDate;
        private LocalDateTime result;

        private JLabel      lblMonthYear;
        private JPanel      dayGrid;
        private JSpinner    spinHour, spinMin;

        private static final String[] DAY_NAMES = {"T2","T3","T4","T5","T6","T7","CN"};

        CalendarDialog(Frame parent, LocalDateTime initial) {
            super(parent, "Chọn ngày & giờ", true);
            this.selectedDate = initial.toLocalDate();

            setBackground(BG);
            setLayout(new BorderLayout(0, 0));
            getRootPane().setBorder(new LineBorder(BORDER, 1));

            add(buildCalendarPanel(), BorderLayout.CENTER);
            add(buildFooter(initial), BorderLayout.SOUTH);

            pack();
            setResizable(false);
            setLocationRelativeTo(parent);
        }

        // ── Calendar panel ────────────────────────────────
        private JPanel buildCalendarPanel() {
            JPanel wrap = new JPanel(new BorderLayout(0, 0));
            wrap.setBackground(BG);
            wrap.setBorder(BorderFactory.createEmptyBorder(16, 16, 12, 16));

            // Nav: < Month Year >
            JPanel nav = new JPanel(new BorderLayout(6, 0));
            nav.setBackground(BG);

            JButton btnPrev = navButton("‹");
            JButton btnNext = navButton("›");
            lblMonthYear = new JLabel("", SwingConstants.CENTER);
            lblMonthYear.setFont(new Font("Dialog", Font.BOLD, 13));
            lblMonthYear.setForeground(TEXT1);

            btnPrev.addActionListener(e -> { selectedDate = selectedDate.minusMonths(1); rebuildDayGrid(); });
            btnNext.addActionListener(e -> { selectedDate = selectedDate.plusMonths(1);  rebuildDayGrid(); });

            nav.add(btnPrev, BorderLayout.WEST);
            nav.add(lblMonthYear, BorderLayout.CENTER);
            nav.add(btnNext, BorderLayout.EAST);

            // Day header
            JPanel header = new JPanel(new GridLayout(1, 7, 2, 0));
            header.setBackground(BG);
            header.setBorder(BorderFactory.createEmptyBorder(10, 0, 4, 0));
            for (String d : DAY_NAMES) {
                JLabel lbl = new JLabel(d, SwingConstants.CENTER);
                lbl.setFont(new Font("Dialog", Font.BOLD, 11));
                lbl.setForeground(TEXT2);
                header.add(lbl);
            }

            dayGrid = new JPanel(new GridLayout(6, 7, 2, 2));
            dayGrid.setBackground(BG);

            rebuildDayGrid();

            wrap.add(nav,    BorderLayout.NORTH);
            wrap.add(header, BorderLayout.CENTER);
            wrap.add(dayGrid,BorderLayout.SOUTH);
            return wrap;
        }

        private void rebuildDayGrid() {
            // Cập nhật label tháng/năm
            lblMonthYear.setText(
                    selectedDate.getMonth().getDisplayName(TextStyle.FULL, new Locale("vi"))
                            + "  " + selectedDate.getYear()
            );

            dayGrid.removeAll();
            LocalDate first = selectedDate.withDayOfMonth(1);
            // DayOfWeek: MON=1 ... SUN=7, grid bắt đầu từ T2
            int startOffset = first.getDayOfWeek().getValue() - 1; // 0-based, MON=0

            LocalDate today = LocalDate.now();

            // Ô trống đầu tháng
            for (int i = 0; i < startOffset; i++)
                dayGrid.add(emptyCell());

            int daysInMonth = selectedDate.lengthOfMonth();
            for (int day = 1; day <= daysInMonth; day++) {
                LocalDate date = selectedDate.withDayOfMonth(day);
                JButton btn = dayButton(day, date, today);
                dayGrid.add(btn);
            }

            // Ô trống cuối (fill đến 42 = 6x7)
            int total = startOffset + daysInMonth;
            for (int i = total; i < 42; i++)
                dayGrid.add(emptyCell());

            dayGrid.revalidate();
            dayGrid.repaint();
        }

        private JButton dayButton(int day, LocalDate date, LocalDate today) {
            boolean isSelected = date.equals(selectedDate);
            boolean isToday    = date.equals(today);

            JButton btn = new JButton(String.valueOf(day));
            btn.setFont(new Font("Dialog", isSelected ? Font.BOLD : Font.PLAIN, 12));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setOpaque(true);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setPreferredSize(new Dimension(34, 30));

            if (isSelected) {
                btn.setBackground(ACCENT);
                btn.setForeground(Color.WHITE);
            } else if (isToday) {
                btn.setBackground(TODAY);
                btn.setForeground(ACCENT);
            } else {
                btn.setBackground(BG);
                btn.setForeground(TEXT1);
            }

            btn.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    if (!isSelected) btn.setBackground(HOVER);
                }
                @Override public void mouseExited(MouseEvent e) {
                    if (!isSelected) btn.setBackground(isToday ? TODAY : BG);
                }
            });

            btn.addActionListener(e -> {
                selectedDate = date;
                rebuildDayGrid();
            });
            return btn;
        }

        private JPanel emptyCell() {
            JPanel p = new JPanel(); p.setBackground(BG); return p;
        }

        private JButton navButton(String text) {
            JButton b = new JButton(text);
            b.setFont(new Font("Dialog", Font.BOLD, 16));
            b.setBackground(BG);
            b.setForeground(TEXT1);
            b.setOpaque(true);
            b.setBorderPainted(false);
            b.setFocusPainted(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.setPreferredSize(new Dimension(32, 28));
            return b;
        }

        // ── Footer: giờ + nút OK/Cancel ───────────────────
        private JPanel buildFooter(LocalDateTime initial) {
            JPanel footer = new JPanel(new BorderLayout(0, 0));
            footer.setBackground(SURFACE);
            footer.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
                    BorderFactory.createEmptyBorder(10, 16, 10, 16)
            ));

            // Time row
            JPanel timeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            timeRow.setBackground(SURFACE);

            JLabel lblTime = new JLabel("Giờ:");
            lblTime.setForeground(TEXT2);
            lblTime.setFont(new Font("Dialog", Font.BOLD, 12));

            spinHour = makeSpinner(0, 23, initial.getHour());
            spinMin  = makeSpinner(0, 59, initial.getMinute());

            JLabel sep = new JLabel(":");
            sep.setForeground(TEXT1);
            sep.setFont(new Font("Dialog", Font.BOLD, 14));

            timeRow.add(lblTime);
            timeRow.add(spinHour);
            timeRow.add(sep);
            timeRow.add(spinMin);

            // Buttons
            JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            btnRow.setBackground(SURFACE);

            JButton btnCancel = footerButton("Hủy",  CARD,   TEXT2);
            JButton btnOK     = footerButton("Chọn", ACCENT, Color.WHITE);

            btnCancel.addActionListener(e -> dispose());
            btnOK.addActionListener(e -> {
                result = LocalDateTime.of(
                        selectedDate,
                        LocalTime.of((int) spinHour.getValue(), (int) spinMin.getValue())
                );
                dispose();
            });

            getRootPane().setDefaultButton(btnOK);

            btnRow.add(btnCancel);
            btnRow.add(btnOK);

            footer.add(timeRow, BorderLayout.WEST);
            footer.add(btnRow,  BorderLayout.EAST);
            return footer;
        }

        private JSpinner makeSpinner(int min, int max, int initial) {
            JSpinner sp = new JSpinner(new SpinnerNumberModel(initial, min, max, 1));
            sp.setFont(new Font("Dialog", Font.PLAIN, 13));
            sp.setPreferredSize(new Dimension(52, 28));
            sp.setBackground(CARD);
            sp.setForeground(TEXT1);
            JComponent editor = sp.getEditor();
            if (editor instanceof JSpinner.DefaultEditor de) {
                de.getTextField().setBackground(CARD2);
                de.getTextField().setForeground(TEXT1);
                de.getTextField().setHorizontalAlignment(SwingConstants.CENTER);
                de.getTextField().setFont(new Font("Dialog", Font.PLAIN, 13));
            }
            return sp;
        }

        private JButton footerButton(String text, Color bg, Color fg) {
            JButton b = new JButton(text);
            b.setFont(new Font("Dialog", Font.BOLD, 12));
            b.setBackground(bg); b.setForeground(fg);
            b.setOpaque(true); b.setBorderPainted(false); b.setFocusPainted(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.setBorder(BorderFactory.createEmptyBorder(7, 18, 7, 18));
            Color hover = bg.brighter();
            b.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { b.setBackground(hover); }
                @Override public void mouseExited (MouseEvent e) { b.setBackground(bg);    }
            });
            return b;
        }

        LocalDateTime getResult() { return result; }
    }
}