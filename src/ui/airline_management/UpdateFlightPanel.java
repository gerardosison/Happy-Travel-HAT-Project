package ui.airline_management;

import dao.*;
import model.*;
import ui.Session;
import ui.UI;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class UpdateFlightPanel extends JPanel {

    private final FlightDAO flightDAO = new FlightDAO();
    private final RouteDAO routeDAO = new RouteDAO();

    private DefaultTableModel flightModel;
    private JTable flightTable;

    private JTextField txtFid, txtAid, txtPid, txtRid, txtDep, txtArr, txtSearch;
    private JLabel lblStatus;

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public UpdateFlightPanel() {
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

        refreshFlights(null);
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
                paintFlightIcon(g2, 10, 10, getWidth() - 20, getHeight() - 20);
                g2.dispose();
            }
        };
        iconBadge.setPreferredSize(new Dimension(48, 48));
        iconBadge.setOpaque(false);

        JPanel textBlock = new JPanel(new BorderLayout(0, 3));
        textBlock.setOpaque(false);

        JLabel titleLbl = new JLabel("Update Flight Information");
        titleLbl.setFont(UI.FONT_SUBTITLE); 
        titleLbl.setForeground(UI.WHITE);

        JLabel subLbl = new JLabel("Modify flight schedules and assign routes");
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

        body.add(buildSearchCard());
        body.add(Box.createVerticalStrut(14));
        body.add(buildFlightTableCard());
        body.add(Box.createVerticalStrut(14));
        body.add(buildFormCard());
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

    private JPanel buildSearchCard() {
        JPanel card = roundCard();
        card.setLayout(new BorderLayout(12, 0));
        card.setBorder(new EmptyBorder(12, 16, 12, 16));

        txtSearch = UI.styledField(0);
        txtSearch.setToolTipText("Search by Flight, Airline, Plane, or Route ID");
        txtSearch.setPreferredSize(new Dimension(280, 32));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);
        left.add(labeledField("Search Records", txtSearch));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        JButton btnRefresh = UI.smallOutlineButton("Clear");
        JButton btnSearch = UI.smallYellowButton("Search");

        btnSearch.addActionListener(e -> refreshFlights(txtSearch.getText().trim()));
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            refreshFlights(null);
        });

        right.add(btnRefresh);
        right.add(btnSearch);

        card.add(left, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);

        card.setPreferredSize(new Dimension(400, 30));
        return card;
    }

    private JPanel buildFlightTableCard() {
        JPanel card = roundCard();
        card.setLayout(new BorderLayout(0, 0));
        card.setPreferredSize(new Dimension(0, 280));

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setBackground(UI.TABLE_ALT_ROW);
        tableHeader.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 1, 0, UI.BORDER_CARD),
            new EmptyBorder(8, 20, 8, 20)
        ));
        JLabel tTitle = new JLabel("CURRENT FLIGHT RECORDS");
        tTitle.setFont(UI.FONT_LABEL);
        tTitle.setForeground(UI.TEXT_MUTED);
        tableHeader.add(tTitle, BorderLayout.WEST);
        JLabel tHint = new JLabel("Click a row to edit or clear");
        tHint.setFont(UI.FONT_SMALL.deriveFont(Font.ITALIC));
        tHint.setForeground(UI.TEXT_MUTED);
        tableHeader.add(tHint, BorderLayout.EAST);
        card.add(tableHeader, BorderLayout.NORTH);

        String[] cols = {
            "Flight ID", "Airline ID", "Plane ID", "Route ID", "Departure", "Arrival", "Route Detail"
        };
        flightModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        flightTable = buildTable(flightModel);

        flightTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setFont(UI.FONT_BODY);
                setBorder(new EmptyBorder(0, 14, 0, 14));
                setBackground(sel ? new Color(0xEDE9FE) : (row % 2 == 0 ? UI.BG_PANEL : UI.TABLE_ALT_ROW));
                setForeground(sel ? new Color(0x6D28D9) : UI.TEXT_PRIMARY);
                if (col == 6 && v != null && v.toString().contains("->")) {
                    setFont(UI.FONT_BOLD);
                    setForeground(new Color(0x047857));
                }
                return this;
            }
        });

        flightTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = flightTable.getSelectedRow();
            if (row < 0) return;

            txtFid.setText((String) flightModel.getValueAt(row, 0));
            txtAid.setText((String) flightModel.getValueAt(row, 1));
            txtPid.setText((String) flightModel.getValueAt(row, 2));
            txtRid.setText((String) flightModel.getValueAt(row, 3));
            txtDep.setText((String) flightModel.getValueAt(row, 4));
            txtArr.setText((String) flightModel.getValueAt(row, 5));
            txtAid.requestFocusInWindow();
        });

        JScrollPane sp = tableScrollPane(flightTable);
        card.add(sp, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildFormCard() {
        JPanel card = roundCard();
        card.setLayout(new BorderLayout(0, 0));
        card.setBorder(new EmptyBorder(20, 24, 20, 24));

        card.add(sectionHeader("Add / Edit Flight Details",
            "Create new configurations or rewrite current schedules", "flight"), BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setBackground(UI.BG_PANEL);
        fields.setBorder(new EmptyBorder(16, 0, 0, 0));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(0, 0, 12, 14);
        gc.fill = GridBagConstraints.HORIZONTAL;

        txtFid = styledField("e.g., FL01");
        txtAid = styledField("e.g., 5J");
        txtPid = styledField("e.g., P01");
        txtRid = styledField("e.g., RT01");
        txtDep = styledField("YYYY-MM-DD HH:mm");
        txtArr = styledField("YYYY-MM-DD HH:mm");

        gc.weightx = 0.25; gc.gridx = 0; gc.gridy = 0;
        fields.add(labeledField("Flight ID *", txtFid), gc);
        gc.weightx = 0.25; gc.gridx = 1;
        fields.add(labeledField("Airline ID *", txtAid), gc);
        gc.weightx = 0.25; gc.gridx = 2;
        fields.add(labeledField("Plane ID *", txtPid), gc);
        gc.weightx = 0.25; gc.gridx = 3; gc.insets = new Insets(0, 0, 12, 0);
        fields.add(labeledField("Route ID *", txtRid), gc);

        gc.gridwidth = 2; 
        gc.weightx = 0.50; gc.gridx = 0; gc.gridy = 1; gc.insets = new Insets(0, 0, 0, 14);
        fields.add(labeledField("Departure (YYYY-MM-DD HH:mm) *", txtDep), gc);
        gc.weightx = 0.50; gc.gridx = 2; gc.insets = new Insets(0, 0, 0, 0);
        fields.add(labeledField("Arrival (YYYY-MM-DD HH:mm) *", txtArr), gc);

        card.add(fields, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new BorderLayout());
        btnRow.setBackground(UI.BG_PANEL);
        btnRow.setBorder(new EmptyBorder(12, 0, 0, 0));

        JLabel hint = new JLabel("* Required Fields");
        hint.setFont(UI.FONT_SMALL.deriveFont(Font.ITALIC));
        hint.setForeground(UI.TEXT_MUTED);
        btnRow.add(hint, BorderLayout.WEST);

        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightActions.setOpaque(false);

        JButton btnClear = UI.smallOutlineButton("Clear");
        JButton btnDelete = UI.dangerButton("Delete Flight");
        JButton btnUpdate = UI.goldButton("Update Details");
        JButton btnAdd = UI.smallYellowButton("Add Flight");

        btnClear.addActionListener(e -> clearForm());
        btnAdd.addActionListener(e -> addFlight());
        btnUpdate.addActionListener(e -> updateFlight());
        btnDelete.addActionListener(e -> deleteFlight());

        rightActions.add(btnClear);
        rightActions.add(btnDelete);
        rightActions.add(btnUpdate);
        rightActions.add(btnAdd);
        btnRow.add(rightActions, BorderLayout.EAST);

        card.add(btnRow, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildStatusBar() {
        JPanel bar = roundCard();
        bar.setLayout(new BorderLayout());
        bar.setBorder(new EmptyBorder(10, 16, 10, 16));
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        lblStatus = new JLabel("Ready - search, select a record row, or populate the form workspace.");
        lblStatus.setFont(UI.FONT_SMALL);
        lblStatus.setForeground(UI.TEXT_MUTED);
        bar.add(lblStatus, BorderLayout.WEST);
        return bar;
    }

    private void addFlight() {
        Flight f = parseForm();
        if (f == null) return;
        try {
            flightDAO.add(f);
            refreshFlights(null);
            clearForm();
            showStatus("Flight Manifest " + f.getFlightID() + " committed successfully.", UI.SUCCESS);
        } catch (Exception ex) {
            showStatus("Database sync failure: " + ex.getMessage(), UI.DANGER);
        }
    }

    private void updateFlight() {
        int row = flightTable.getSelectedRow();
        if (row < 0) {
            showStatus("Select an active flight record row to update.", UI.DANGER);
            return;
        }
        Flight f = parseForm();
        if (f == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Commit modified metadata adjustments to Flight " + f.getFlightID() + "?",
                "Confirm Structural Modification", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            flightDAO.update(f);
            refreshFlights(null);
            clearForm();
            showStatus("Flight " + f.getFlightID() + " updated systematically.", UI.SUCCESS);
        } catch (Exception ex) {
            showStatus("Update exception caught: " + ex.getMessage(), UI.DANGER);
        }
    }

    private void deleteFlight() {
        int row = flightTable.getSelectedRow();
        if (row < 0) {
            showStatus("Select a structural row index target to delete.", UI.DANGER);
            return;
        }
        String fid = (String) flightModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Purge flight manifest " + fid + " entirely? This cascading structural choice is absolute.",
                "Critical Deletion Alert", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                flightDAO.delete(fid);
                refreshFlights(null);
                clearForm();
                showStatus("Flight manifest identity " + fid + " wiped cleanly.", UI.TEXT_MUTED);
            } catch (Exception ex) {
                showStatus("Deletion rejected by persistence tier: " + ex.getMessage(), UI.DANGER);
            }
        }
    }

    private Flight parseForm() {
        String fid = txtFid.getText().trim();
        String aid = txtAid.getText().trim();
        String pid = txtPid.getText().trim();
        String rid = txtRid.getText().trim();
        String dep = txtDep.getText().trim();
        String arr = txtArr.getText().trim();

        if (fid.isEmpty() || aid.isEmpty() || pid.isEmpty() || rid.isEmpty() || dep.isEmpty() || arr.isEmpty()) {
            showStatus("All programmatic criteria fields are required.", UI.DANGER);
            return null;
        }
        try {
            LocalDateTime departureTime = LocalDateTime.parse(dep, DT_FMT);
            LocalDateTime arrivalTime = LocalDateTime.parse(arr, DT_FMT);
            return new Flight(fid, aid, pid, rid, departureTime, arrivalTime);
        } catch (Exception ex) {
            showStatus("Malformed time parsing logic. Satisfy format paradigm: YYYY-MM-DD HH:mm.", UI.DANGER);
            return null;
        }
    }

    private void clearForm() {
        txtFid.setText("");
        txtAid.setText("");
        txtPid.setText("");
        txtRid.setText("");
        txtDep.setText("");
        txtArr.setText("");
        flightTable.clearSelection();
    }

    private void refreshFlights(String query) {
        flightModel.setRowCount(0);
        List<Route> routes = routeDAO.getAll();
        String targetQuery = (query == null) ? "" : query.toLowerCase();

        for (Flight f : flightDAO.getAll()) {
            if (!targetQuery.isEmpty() &&
                    !f.getFlightID().toLowerCase().contains(targetQuery) &&
                    !f.getAirlineID().toLowerCase().contains(targetQuery) &&
                    !f.getPlaneID().toLowerCase().contains(targetQuery) &&
                    !f.getRouteID().toLowerCase().contains(targetQuery)) {
                continue;
            }
            Route rt = routes.stream()
                    .filter(r -> r.getRouteID().equals(f.getRouteID()))
                    .findFirst().orElse(null);
            String routeStr = (rt != null)
                    ? rt.getOrigin() + " -> " + rt.getDestination()
                    : f.getRouteID();

            flightModel.addRow(new Object[] {
                    f.getFlightID(), 
                    f.getAirlineID(), 
                    f.getPlaneID(), 
                    f.getRouteID(),
                    f.getDepartureDateTime().format(DT_FMT),
                    f.getArrivalDateTime().format(DT_FMT),
                    routeStr 
            });
        }
        showStatus("Synchronized " + flightModel.getRowCount() + " flight manifests from relational database runtime.", UI.TEXT_MUTED);
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

        if ("flight".equals(type)) {
            g2.drawRect(cx - 5, cy - 6, 10, 12);
            g2.drawLine(cx - 3, cy - 3, cx + 3, cy - 3);
            g2.drawLine(cx - 3, cy, cx + 3, cy);
            g2.drawLine(cx - 3, cy + 3, cx + 1, cy + 3);
        } else {
            g2.drawOval(cx - 4, cy - 4, 8, 8);
        }
    }

    private static void paintFlightIcon(Graphics2D g2, int x, int y, int w, int h) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int cx = x + w / 2, cy = y + h / 2;
        g2.drawRect(cx - 6, cy - 5, 12, 10);
        g2.drawLine(cx - 4, cy + 2, cx - 1, cy - 1);
        g2.drawLine(cx - 1, cy - 1, cx + 2, cy + 1);
        g2.drawLine(cx + 2, cy + 1, cx + 4, cy - 2);
    }
}