package ui.airline_management;

import dao.SeatDAO;
import model.Seat;
import ui.Session;
import ui.UI;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class ModifyPricesPanel extends JPanel {

    private final SeatDAO seatDAO = new SeatDAO();

    private DefaultTableModel tableModel;
    private JTable priceTable;

    private JTextField txtFilterFlight;
    private JComboBox<String> cbFilterClass;

    private JTextField txtEditFlight;
    private JTextField txtEditClass;
    private JTextField txtCurrentPrice;
    private JTextField txtNewPrice;

    private JTextField txtBulkPct;
    private JComboBox<String> cbBulkClass;
    private JComboBox<String> cbBulkDir;

    private String selectedFlightID = null;
    private String selectedSeatClass = null;

    private JLabel lblAvgEco, lblAvgFirst;

    private JLabel lblStatus;

    public ModifyPricesPanel() {
        if (!Session.getInstance().isAdmin()) {
            setLayout(new BorderLayout());
            setBackground(UI.BG_DEEP);
            JLabel deny = new JLabel("Access restricted to administrators.", SwingConstants.CENTER);
            deny.setFont(UI.FONT_BODY);
            deny.setForeground(UI.TEXT_MUTED);
            add(deny, BorderLayout.CENTER);
            return;
        }

        setLayout(new BorderLayout(0, 0));
        setBackground(UI.BG_DEEP);

        add(buildPageHeader(), BorderLayout.NORTH);
        add(buildBody(),       BorderLayout.CENTER);

        refresh(null, "All Classes");
    }

    private JPanel buildPageHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 0));
        header.setBackground(UI.BLUE); 
        header.setBorder(new javax.swing.border.EmptyBorder(18, 28, 18, 28)); 

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        left.setOpaque(false);

        JPanel iconBadge = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(UI.YELLOW); 
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                g2.setColor(UI.TEXT_PRIMARY); 
                paintPriceIcon(g2, 10, 10, getWidth() - 20, getHeight() - 20);
                g2.dispose();
            }
        };
        iconBadge.setPreferredSize(new Dimension(48, 48));
        iconBadge.setOpaque(false);

        JPanel textBlock = new JPanel(new BorderLayout(0, 3));
        textBlock.setOpaque(false);

        JLabel titleLbl = new JLabel("Modify Prices");
        titleLbl.setFont(UI.FONT_SUBTITLE); 
        titleLbl.setForeground(UI.WHITE);

        JLabel subLbl = new JLabel("Adjust seat prices individually or in bulk");
        subLbl.setFont(UI.FONT_BODY); 
        subLbl.setForeground(new Color(0xD6E0EE)); 

        textBlock.add(titleLbl, BorderLayout.NORTH);
        textBlock.add(subLbl,   BorderLayout.SOUTH);

        left.add(iconBadge);
        left.add(textBlock);

        header.add(left, BorderLayout.WEST);
        return header;
    }

    private JScrollPane buildBody() {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(UI.BG_DEEP);
        body.setBorder(new EmptyBorder(16, 20, 16, 20));

        body.add(buildSummaryBanner());
        body.add(Box.createVerticalStrut(14));
        body.add(buildFilterCard());
        body.add(Box.createVerticalStrut(14));
        body.add(buildPriceTableCard());
        body.add(Box.createVerticalStrut(14));
        body.add(buildEditFormCard());
        body.add(Box.createVerticalStrut(14));
        body.add(buildBulkCard());
        body.add(Box.createVerticalStrut(10));
        body.add(buildStatusBar());

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBackground(UI.BG_DEEP);
        scroll.getViewport().setBackground(UI.BG_DEEP);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        UI.styleScrollPane(scroll);
        return scroll;
    }

    private JPanel buildSummaryBanner() {
        JPanel row = new JPanel(new GridLayout(1, 2, 14, 0));
        row.setBackground(UI.BG_DEEP);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        lblAvgEco = new JLabel("P —", SwingConstants.LEFT);
        lblAvgFirst = new JLabel("P —", SwingConstants.LEFT);

        row.add(summaryCard("Average Economy Price", lblAvgEco, new Color(0x3B82F6)));
        row.add(summaryCard("Average First Class Price", lblAvgFirst, new Color(0x8B5CF6)));
        return row;
    }

    private JPanel summaryCard(String title, JLabel valueLabel, Color accent) {
        JPanel card = roundCard();
        card.setLayout(new BorderLayout(0, 4));
        card.setBorder(new EmptyBorder(14, 16, 14, 16));

        JPanel stripe = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(accent);
                g2.fillRect(0, 0, getWidth(), 3);
            }
        };
        stripe.setPreferredSize(new Dimension(0, 3));
        stripe.setOpaque(false);
        card.add(stripe, BorderLayout.NORTH);

        valueLabel.setFont(UI.FONT_TITLE.deriveFont(24f));
        valueLabel.setForeground(accent);
        card.add(valueLabel, BorderLayout.CENTER);

        JLabel lbl = new JLabel(title);
        lbl.setFont(UI.FONT_LABEL);
        lbl.setForeground(UI.TEXT_SECONDARY);
        card.add(lbl, BorderLayout.SOUTH);
        return card;
    }

    private void updateSummary() {
        List<Seat> all = seatDAO.getAll();
        lblAvgEco.setText("P " + avg(all, "Economy"));
        lblAvgFirst.setText("P " + avg(all, "First Class"));
    }

    private String avg(List<Seat> seats, String cls) {
        return seats.stream()
                .filter(s -> s.getSeatClass().equals(cls))
                .map(Seat::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(
                        BigDecimal.valueOf(
                                Math.max(1, seats.stream().filter(s -> s.getSeatClass().equals(cls)).count())),
                        2, RoundingMode.HALF_UP)
                .toPlainString();
    }

    private JPanel buildFilterCard() {
        JPanel card = roundCard();
        card.setLayout(new BorderLayout(12, 0));
        card.setBorder(new EmptyBorder(12, 16, 12, 16));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        txtFilterFlight = UI.styledField(0);
        txtFilterFlight.setToolTipText("Filter by Flight ID");
        txtFilterFlight.setPreferredSize(new Dimension(200, 32));

        String[] classes = {"All Classes", "Economy", "First Class"};
        cbFilterClass = new JComboBox<>(classes);
        UI.styleCombo(cbFilterClass);
        cbFilterClass.setPreferredSize(new Dimension(160, 32));

        left.add(labeledField("Flight ID", txtFilterFlight));
        left.add(labeledField("Seat Class", cbFilterClass));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        JButton btnClear = UI.smallOutlineButton("Clear");
        JButton btnFilter = UI.smallYellowButton("Filter");

        btnFilter.addActionListener(e -> applyFilter());
        btnClear.addActionListener(e -> {
            txtFilterFlight.setText("");
            cbFilterClass.setSelectedIndex(0);
            refresh(null, "All Classes");
        });

        right.add(btnClear);
        right.add(btnFilter);

        card.add(left, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);
        return card;
    }

    private JPanel buildPriceTableCard() {
        JPanel card = roundCard();
        card.setLayout(new BorderLayout(0, 0));
        card.setPreferredSize(new Dimension(0, 280));

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setBackground(UI.TABLE_ALT_ROW);
        tableHeader.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 1, 0, UI.BORDER_CARD),
            new EmptyBorder(8, 20, 8, 20)
        ));
        JLabel tTitle = new JLabel("CURRENT PRICES");
        tTitle.setFont(UI.FONT_LABEL);
        tTitle.setForeground(UI.TEXT_MUTED);
        tableHeader.add(tTitle, BorderLayout.WEST);
        JLabel tHint = new JLabel("Click a row to edit");
        tHint.setFont(UI.FONT_SMALL.deriveFont(Font.ITALIC));
        tHint.setForeground(UI.TEXT_MUTED);
        tableHeader.add(tHint, BorderLayout.EAST);
        card.add(tableHeader, BorderLayout.NORTH);

        String[] cols = {"Flight ID", "Seat Class", "Current Price (P)", "Available Seats"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        priceTable = buildTable(tableModel);

        priceTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setFont(UI.FONT_BODY);
                setBorder(new EmptyBorder(0, 14, 0, 14));
                setBackground(sel ? new Color(0xEDE9FE) : (row % 2 == 0 ? UI.BG_PANEL : UI.TABLE_ALT_ROW));
                setForeground(sel ? new Color(0x6D28D9) : UI.TEXT_PRIMARY);
                if (col == 2 && v != null) {
                    setFont(UI.FONT_BOLD);
                    setForeground(new Color(0x047857));
                }
                return this;
            }
        });

        priceTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = priceTable.getSelectedRow();
            if (row < 0) return;

            selectedFlightID = (String) tableModel.getValueAt(row, 0);
            selectedSeatClass = (String) tableModel.getValueAt(row, 1);
            String currentPriceRaw = tableModel.getValueAt(row, 2)
                    .toString().replace("P", "").trim();

            txtEditFlight.setText(selectedFlightID);
            txtEditClass.setText(selectedSeatClass);
            txtCurrentPrice.setText("P " + currentPriceRaw);
            txtNewPrice.setText(currentPriceRaw);
            txtNewPrice.requestFocusInWindow();
        });

        JScrollPane sp = tableScrollPane(priceTable);
        card.add(sp, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildEditFormCard() {
        JPanel card = roundCard();
        card.setLayout(new BorderLayout(0, 0));
        card.setBorder(new EmptyBorder(20, 24, 20, 24));

        card.add(sectionHeader("Edit Selected Price",
            "Update price for the selected flight and seat class", "edit"), BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setBackground(UI.BG_PANEL);
        fields.setBorder(new EmptyBorder(16, 0, 0, 0));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(0, 0, 12, 14);
        gc.fill = GridBagConstraints.HORIZONTAL;

        txtEditFlight = styledField("—");
        txtEditFlight.setEditable(false);
        txtEditFlight.setBackground(UI.BG_CARD);
        txtEditClass = styledField("—");
        txtEditClass.setEditable(false);
        txtEditClass.setBackground(UI.BG_CARD);
        txtCurrentPrice = styledField("—");
        txtCurrentPrice.setEditable(false);
        txtCurrentPrice.setBackground(UI.BG_CARD);
        txtNewPrice = styledField("Enter new price");

        gc.weightx = 0.25; gc.gridx = 0; gc.gridy = 0;
        fields.add(labeledField("Flight ID", txtEditFlight), gc);
        gc.weightx = 0.25; gc.gridx = 1;
        fields.add(labeledField("Seat Class", txtEditClass), gc);
        gc.weightx = 0.25; gc.gridx = 2;
        fields.add(labeledField("Current Price", txtCurrentPrice), gc);
        gc.weightx = 0.25; gc.gridx = 3; gc.insets = new Insets(0, 0, 12, 0);
        fields.add(labeledField("New Price (P) *", txtNewPrice), gc);

        card.add(fields, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setBackground(UI.BG_PANEL);
        btnRow.setBorder(new EmptyBorder(12, 0, 0, 0));

        JButton btnClear = UI.smallOutlineButton("Clear");
        JButton btnUpdate = UI.goldButton("Update Price");

        btnClear.addActionListener(e -> clearEditForm());
        btnUpdate.addActionListener(e -> updatePrice());

        btnRow.add(btnClear);
        btnRow.add(btnUpdate);
        card.add(btnRow, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildBulkCard() {
        JPanel card = roundCard();
        card.setLayout(new BorderLayout(0, 0));
        card.setBorder(new EmptyBorder(20, 24, 20, 24));

        card.add(sectionHeader("Bulk Price Adjustment",
            "Apply percentage increase or decrease to multiple records", "bulk"), BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setBackground(UI.BG_PANEL);
        fields.setBorder(new EmptyBorder(16, 0, 0, 0));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(0, 0, 12, 14);
        gc.fill = GridBagConstraints.HORIZONTAL;

        String[] classes = {"All Classes", "Economy", "First Class"};
        cbBulkClass = new JComboBox<>(classes);
        UI.styleCombo(cbBulkClass);
        String[] dirs = {"Increase", "Decrease"};
        cbBulkDir = new JComboBox<>(dirs);
        UI.styleCombo(cbBulkDir);
        txtBulkPct = styledField("e.g., 10");

        gc.weightx = 0.33; gc.gridx = 0; gc.gridy = 0;
        fields.add(labeledField("Seat Class", cbBulkClass), gc);
        gc.weightx = 0.33; gc.gridx = 1;
        fields.add(labeledField("Direction", cbBulkDir), gc);
        gc.weightx = 0.34; gc.gridx = 2; gc.insets = new Insets(0, 0, 12, 0);
        fields.add(labeledField("Percentage (%)", txtBulkPct), gc);

        card.add(fields, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new BorderLayout());
        btnRow.setBackground(UI.BG_PANEL);
        btnRow.setBorder(new EmptyBorder(12, 0, 0, 0));

        JLabel warn = new JLabel("Warning: This will update ALL matching records.");
        warn.setFont(UI.FONT_SMALL.deriveFont(Font.ITALIC));
        warn.setForeground(UI.DANGER);
        btnRow.add(warn, BorderLayout.WEST);

        JButton btnApply = UI.dangerButton("Apply Bulk Adjustment");
        btnApply.addActionListener(e -> applyBulk());
        btnRow.add(btnApply, BorderLayout.EAST);

        card.add(btnRow, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildStatusBar() {
        JPanel bar = roundCard();
        bar.setLayout(new BorderLayout());
        bar.setBorder(new EmptyBorder(10, 16, 10, 16));
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        lblStatus = new JLabel("Ready - search, select a row, or use bulk adjustment.");
        lblStatus.setFont(UI.FONT_SMALL);
        lblStatus.setForeground(UI.TEXT_MUTED);
        bar.add(lblStatus, BorderLayout.WEST);
        return bar;
    }

    private void updatePrice() {
        if (selectedFlightID == null) {
            showStatus("Click a row in the table to select the record to update.", UI.DANGER);
            return;
        }
        String raw = txtNewPrice.getText().trim();
        if (raw.isEmpty()) {
            showStatus("New price is required.", UI.DANGER);
            return;
        }
        BigDecimal price;
        try {
            price = new BigDecimal(raw);
            if (price.signum() < 0) {
                showStatus("Price cannot be negative.", UI.DANGER);
                return;
            }
        } catch (NumberFormatException ex) {
            showStatus("Price must be a valid number (e.g. 3500.00).", UI.DANGER);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Update price for Flight " + selectedFlightID + " / " + selectedSeatClass + " to P" + price.toPlainString() + "?",
                "Confirm Price Update", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            seatDAO.updatePriceOnly(selectedFlightID, selectedSeatClass, price);
            refresh(null, "All Classes");
            clearEditForm();
            showStatus("Price updated successfully.", UI.SUCCESS);
        } catch (Exception ex) {
            ex.printStackTrace();
            showStatus("Failed to update price: " + ex.getMessage(), UI.DANGER);
        }
    }

    private void applyBulk() {
        String pctStr = txtBulkPct.getText().trim();
        if (pctStr.isEmpty()) {
            showStatus("Enter a percentage value.", UI.DANGER);
            return;
        }
        double pct;
        try {
            pct = Double.parseDouble(pctStr);
            if (pct <= 0 || pct > 100) {
                showStatus("Percentage must be between 1 and 100.", UI.DANGER);
                return;
            }
        } catch (NumberFormatException ex) {
            showStatus("Percentage must be a number (e.g. 10).", UI.DANGER);
            return;
        }

        String cls = (String) cbBulkClass.getSelectedItem();
        String dir = (String) cbBulkDir.getSelectedItem();
        boolean increase = "Increase".equals(dir);

        int confirm = JOptionPane.showConfirmDialog(this,
                dir + " prices by " + pct + "% for: " + cls + "\n\nThis cannot be undone.",
                "Confirm Bulk Adjustment", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        List<Seat> seats = seatDAO.getAll();
        int updated = 0;
        for (Seat s : seats) {
            boolean match = "All Classes".equals(cls) || s.getSeatClass().equals(cls);
            if (!match) continue;
            BigDecimal current = s.getPrice();
            BigDecimal factor = BigDecimal.valueOf(pct / 100.0);
            BigDecimal delta = current.multiply(factor).setScale(2, RoundingMode.HALF_UP);
            BigDecimal newPrice = increase ? current.add(delta) : current.subtract(delta);
            if (newPrice.signum() < 0) newPrice = BigDecimal.ZERO;
            try {
                seatDAO.updatePriceOnly(s.getFlightID(), s.getSeatClass(), newPrice);
                updated++;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        refresh(null, "All Classes");
        clearEditForm();
        showStatus(updated + " price record(s) updated.", UI.SUCCESS);
    }

    private void clearEditForm() {
        selectedFlightID = null;
        selectedSeatClass = null;
        txtEditFlight.setText("");
        txtEditClass.setText("");
        txtCurrentPrice.setText("");
        txtNewPrice.setText("");
        priceTable.clearSelection();
    }

    private void applyFilter() {
        String flt = txtFilterFlight.getText().trim();
        String cls = (String) cbFilterClass.getSelectedItem();
        refresh(flt.isEmpty() ? null : flt, cls);
    }

    private void refresh(String flightFilter, String classFilter) {
        tableModel.setRowCount(0);
        for (Seat s : seatDAO.getAll()) {
            boolean matchFlight = (flightFilter == null)
                    || s.getFlightID().toLowerCase().contains(flightFilter.toLowerCase());
            boolean matchClass = "All Classes".equals(classFilter)
                    || s.getSeatClass().equals(classFilter);
            if (!matchFlight || !matchClass) continue;
            tableModel.addRow(new Object[] {
                    s.getFlightID(),
                    s.getSeatClass(),
                    "P" + s.getPrice().toPlainString(),
                    s.getAvailableSeats()
            });
        }
        updateSummary();
        showStatus("Loaded " + tableModel.getRowCount() + " price records from database.", UI.TEXT_MUTED);
    }

    private JPanel roundCard() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UI.BG_PANEL);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.setColor(UI.BORDER_CARD);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            }
        };
        card.setOpaque(false);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        return card;
    }

    private JPanel sectionHeader(String title, String subtitle, String iconType) {
        JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setBackground(UI.BG_PANEL);

        JPanel iconBox = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(UI.YELLOW.getRed(), UI.YELLOW.getGreen(), UI.YELLOW.getBlue(), 35));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(UI.YELLOW);
                paintIcon(g2, iconType, 6, 6, getWidth() - 12, getHeight() - 12);
            }
        };
        iconBox.setPreferredSize(new Dimension(32, 32));
        iconBox.setOpaque(false);

        JPanel textBox = new JPanel();
        textBox.setOpaque(false);
        textBox.setLayout(new BoxLayout(textBox, BoxLayout.Y_AXIS));
        JLabel t = new JLabel(title);
        t.setFont(UI.FONT_BOLD);
        t.setForeground(UI.TEXT_PRIMARY);
        JLabel s = new JLabel(subtitle);
        s.setFont(UI.FONT_SMALL);
        s.setForeground(UI.TEXT_SECONDARY);
        textBox.add(t);
        textBox.add(Box.createVerticalStrut(2));
        textBox.add(s);

        p.add(iconBox, BorderLayout.WEST);
        p.add(textBox, BorderLayout.CENTER);
        return p;
    }

    private JPanel labeledField(String labelText, JComponent field) {
        JPanel wrap = new JPanel(new BorderLayout(0, 5));
        wrap.setBackground(UI.BG_PANEL);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(UI.FONT_LABEL);
        lbl.setForeground(UI.TEXT_SECONDARY);
        wrap.add(lbl,   BorderLayout.NORTH);
        wrap.add(field, BorderLayout.CENTER);
        return wrap;
    }

    private JTextField styledField(String tooltip) {
        JTextField tf = UI.styledField(0);
        tf.setToolTipText(tooltip);
        return tf;
    }

    private JTable buildTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        UI.styleTable(table);
        table.setRowHeight(34);
        table.setFont(UI.FONT_BODY);
        table.setSelectionBackground(new Color(0xEDE9FE));
        table.setSelectionForeground(new Color(0x6D28D9));
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(0xEEF1F7));
        table.setAutoCreateRowSorter(true);
        table.setFocusable(false);
        table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JTableHeader header = table.getTableHeader();
        header.setFont(UI.FONT_LABEL);
        header.setForeground(UI.TEXT_SECONDARY);
        header.setBackground(UI.TABLE_ALT_ROW);
        header.setBorder(new MatteBorder(0, 0, 1, 0, UI.BORDER_CARD));
        header.setPreferredSize(new Dimension(0, 32));
        ((DefaultTableCellRenderer) header.getDefaultRenderer())
                .setHorizontalAlignment(SwingConstants.LEFT);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setFont(UI.FONT_BODY);
                setBorder(new EmptyBorder(0, 14, 0, 14));
                setBackground(sel ? new Color(0xEDE9FE) : (row % 2 == 0 ? UI.BG_PANEL : UI.TABLE_ALT_ROW));
                setForeground(sel ? new Color(0x6D28D9) : UI.TEXT_PRIMARY);
                return this;
            }
        });
        return table;
    }

    private JScrollPane tableScrollPane(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBackground(UI.BG_PANEL);
        sp.getViewport().setBackground(UI.BG_PANEL);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getVerticalScrollBar().setUnitIncrement(12);
        UI.styleScrollPane(sp);
        return sp;
    }

    private void showStatus(String msg, Color color) {
        lblStatus.setText(msg);
        lblStatus.setForeground(color);
    }

    private static void paintIcon(Graphics2D g2, String type, int x, int y, int w, int h) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int cx = x + w / 2, cy = y + h / 2;

        switch (type) {
            case "edit":
                g2.drawRect(cx - 5, cy - 6, 10, 12);
                g2.drawLine(cx - 3, cy - 3, cx + 3, cy - 3);
                g2.drawLine(cx - 3, cy, cx + 3, cy);
                g2.drawLine(cx - 3, cy + 3, cx + 1, cy + 3);
                break;
            case "bulk":
                g2.drawRect(cx - 5, cy - 4, 10, 8);
                g2.drawLine(cx - 2, cy - 1, cx + 2, cy - 1);
                g2.drawLine(cx, cy - 3, cx, cy + 1);
                break;
            default:
                g2.drawOval(cx - 4, cy - 4, 8, 8);
                break;
        }
    }

    private static void paintPriceIcon(Graphics2D g2, int x, int y, int w, int h) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int cx = x + w / 2, cy = y + h / 2;
        g2.drawOval(cx - 4, cy - 5, 8, 10);
        g2.drawLine(cx, cy - 4, cx, cy + 4);
        g2.drawLine(cx - 2, cy - 2, cx + 2, cy - 2);
        g2.drawLine(cx - 2, cy + 2, cx + 2, cy + 2);
    }
}