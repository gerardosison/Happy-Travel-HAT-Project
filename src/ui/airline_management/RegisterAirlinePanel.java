package ui.airline_management;

import dao.*;
import model.*;
import ui.UI;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.event.*;


public class RegisterAirlinePanel extends JPanel {

    // DAOs
    private final AirlineDAO airlineDAO = new AirlineDAO();
    private final PlaneDAO   planeDAO   = new PlaneDAO();

    // Table models
    private DefaultTableModel airlineModel;
    private DefaultTableModel fleetModel;

    // Form fields
    private JTextField txtAirlineCode, txtAirlineName, txtHQ, txtContact;
    private JTextField txtPlaneID, txtPlaneModel, txtCapacity, txtFirstClass, txtEconomy, txtAmenities;

    // Tables
    private JTable airlineTable, fleetTable;

    // Status labels
    private JLabel lblAirlineStatus, lblFleetStatus;

    public RegisterAirlinePanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(UI.BG_DEEP);

        add(buildPageHeader(), BorderLayout.NORTH);
        add(buildBody(),       BorderLayout.CENTER);

        refreshAirlines();
        refreshFleet();
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
                paintAirlineIcon(g2, 10, 10, getWidth() - 20, getHeight() - 20);
                g2.dispose();
            }
        };
        iconBadge.setPreferredSize(new Dimension(48, 48));
        iconBadge.setOpaque(false);

        // Text block layout matching the modern vertical title structure
        JPanel textBlock = new JPanel(new BorderLayout(0, 3));
        textBlock.setOpaque(false);

        JLabel titleLbl = new JLabel("Register Airline & Fleet");
        titleLbl.setFont(UI.FONT_SUBTITLE); 
        titleLbl.setForeground(UI.WHITE);

        JLabel subLbl = new JLabel("Manage airlines and aircraft fleet");
        subLbl.setFont(UI.FONT_BODY); 
        subLbl.setForeground(new Color(0xD6E0EE)); 

        textBlock.add(titleLbl, BorderLayout.NORTH);
        textBlock.add(subLbl,   BorderLayout.SOUTH);

        left.add(iconBadge);
        left.add(textBlock);

        header.add(left, BorderLayout.WEST);
        return header;
    }

    private JTabbedPane buildBody() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(UI.BG_DEEP);
        tabs.setForeground(UI.TEXT_SECONDARY);
        tabs.setFont(UI.FONT_BOLD.deriveFont(12f));
        tabs.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        tabs.addTab("  Airlines  ", buildAirlineTab());
        tabs.addTab("  Fleet  ",    buildFleetTab());

        // Custom tab styling
        tabs.setUI(new BasicTabbedPaneUI() {
            @Override protected void paintTabBackground(Graphics g, int tabPlacement,
                    int tabIndex, int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isSelected) {
                    g2.setColor(UI.BG_PANEL);
                    g2.fillRoundRect(x + 2, y + 2, w - 4, h - 2, 8, 8);
                    g2.setColor(UI.YELLOW);
                    g2.fillRect(x + 2, y + h - 3, w - 4, 3);
                } else {
                    g2.setColor(UI.BG_DEEP);
                    g2.fillRect(x, y, w, h);
                }
            }
            @Override protected void paintTabBorder(Graphics g, int tabPlacement,
                    int tabIndex, int x, int y, int w, int h, boolean isSelected) {
                // No border
            }
            @Override protected void paintContentBorderTopEdge(Graphics g, int tabPlacement,
                    int selectedIndex, int x, int y, int w, int h) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(UI.BORDER_CARD);
                g2.drawLine(x, y, x + w, y);
            }
            @Override protected void paintContentBorderLeftEdge(Graphics g, int tabPlacement,
                    int selectedIndex, int x, int y, int w, int h) {
                // No left border
            }
            @Override protected void paintContentBorderRightEdge(Graphics g, int tabPlacement,
                    int selectedIndex, int x, int y, int w, int h) {
                // No right border
            }
            @Override protected void paintContentBorderBottomEdge(Graphics g, int tabPlacement,
                    int selectedIndex, int x, int y, int w, int h) {
                // No bottom border
            }
        });

        return tabs;
    }

    // AIRLINE TAB
    private JPanel buildAirlineTab() {
        JPanel tab = new JPanel(new BorderLayout(0, 12));
        tab.setBackground(UI.BG_DEEP);
        tab.setBorder(new EmptyBorder(12, 4, 4, 4));

        tab.add(buildAirlineFormCard(),  BorderLayout.NORTH);
        tab.add(buildAirlineTableCard(), BorderLayout.CENTER);

        return tab;
    }

    private JPanel buildAirlineFormCard() {
        JPanel card = roundCard();
        card.setLayout(new BorderLayout(0, 0));
        card.setBorder(new EmptyBorder(20, 24, 20, 24));

        card.add(sectionHeader("Airline Information",
            "Register new airlines or update existing records", "airline"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UI.BG_PANEL);
        form.setBorder(new EmptyBorder(16, 0, 0, 0));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(0, 0, 12, 14);

        txtAirlineCode = styledField("e.g., 5J");
        txtAirlineName = styledField("e.g., Cebu Pacific Air");
        txtContact     = styledField("e.g., +63 2 8702 0888");
        txtHQ          = styledField("e.g., Manila, Philippines");

        g.weightx = 0.20; g.gridx = 0; g.gridy = 0;
        form.add(labeledField("AIRLINE CODE *", txtAirlineCode), g);
        g.weightx = 0.35; g.gridx = 1;
        form.add(labeledField("AIRLINE NAME *", txtAirlineName), g);
        g.weightx = 0.25; g.gridx = 2;
        form.add(labeledField("CONTACT INFO", txtContact), g);
        g.weightx = 0.20; g.gridx = 3; g.insets = new Insets(0, 0, 12, 0);
        JPanel clearWrap = new JPanel(new BorderLayout());
        clearWrap.setOpaque(false);
        clearWrap.add(Box.createVerticalStrut(18), BorderLayout.NORTH);
        JButton btnClear = UI.smallOutlineButton("Clear");
        btnClear.addActionListener(e -> clearAirlineForm());
        clearWrap.add(btnClear, BorderLayout.CENTER);
        form.add(clearWrap, g);

        g.gridwidth = 4; g.gridx = 0; g.gridy = 1; g.insets = new Insets(0, 0, 0, 0);
        form.add(labeledField("HEADQUARTERS LOCATION", txtHQ), g);

        card.add(form, BorderLayout.CENTER);
        card.add(buildAirlineActionBar(), BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildAirlineTableCard() {
        JPanel card = roundCard();
        card.setLayout(new BorderLayout(0, 0));
        card.setPreferredSize(new Dimension(0, 240));

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setBackground(UI.TABLE_ALT_ROW);
        tableHeader.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 1, 0, UI.BORDER_CARD),
            new EmptyBorder(8, 20, 8, 20)
        ));
        JLabel tTitle = new JLabel("REGISTERED AIRLINES");
        tTitle.setFont(UI.FONT_LABEL);
        tTitle.setForeground(UI.TEXT_MUTED);
        tableHeader.add(tTitle, BorderLayout.WEST);
        JLabel tHint = new JLabel("Click a row to edit");
        tHint.setFont(UI.FONT_SMALL.deriveFont(Font.ITALIC));
        tHint.setForeground(UI.TEXT_MUTED);
        tableHeader.add(tHint, BorderLayout.EAST);
        card.add(tableHeader, BorderLayout.NORTH);

        String[] cols = {"Airline ID", "Name", "Contact", "Headquarters"};
        airlineModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        airlineTable = buildTable(airlineModel);
        airlineTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = airlineTable.getSelectedRow();
            if (row >= 0) {
                txtAirlineCode.setText((String) airlineModel.getValueAt(row, 0));
                txtAirlineName.setText((String) airlineModel.getValueAt(row, 1));
                txtContact.setText((String) airlineModel.getValueAt(row, 2));
                txtHQ.setText((String) airlineModel.getValueAt(row, 3));
            }
        });

        JScrollPane sp = tableScrollPane(airlineTable);
        card.add(sp, BorderLayout.CENTER);

        lblAirlineStatus = new JLabel("Ready - fill in the form and click Register Airline.");
        lblAirlineStatus.setFont(UI.FONT_SMALL);
        lblAirlineStatus.setForeground(UI.TEXT_MUTED);
        lblAirlineStatus.setBorder(new EmptyBorder(8, 20, 8, 20));
        card.add(lblAirlineStatus, BorderLayout.SOUTH);

        return card;
    }

    private JPanel buildAirlineActionBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UI.BG_PANEL);
        bar.setBorder(new EmptyBorder(12, 0, 0, 0));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        JLabel hint = new JLabel("* Required fields");
        hint.setFont(UI.FONT_SMALL.deriveFont(Font.ITALIC));
        hint.setForeground(UI.TEXT_MUTED);
        left.add(hint);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        JButton btnDelete = UI.dangerButton("Delete Airline");
        JButton btnUpdate = UI.ghostButton("Update Airline");
        JButton btnReg    = UI.goldButton("Register Airline");

        btnReg.addActionListener(e    -> registerAirline());
        btnUpdate.addActionListener(e -> updateAirline());
        btnDelete.addActionListener(e -> deleteAirline());

        right.add(btnDelete);
        right.add(btnUpdate);
        right.add(btnReg);

        bar.add(left,  BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // FLEET TAB
    private JPanel buildFleetTab() {
        JPanel tab = new JPanel(new BorderLayout(0, 12));
        tab.setBackground(UI.BG_DEEP);
        tab.setBorder(new EmptyBorder(12, 4, 4, 4));

        tab.add(buildFleetFormCard(),  BorderLayout.NORTH);
        tab.add(buildFleetTableCard(), BorderLayout.CENTER);

        return tab;
    }

    private JPanel buildFleetFormCard() {
        JPanel card = roundCard();
        card.setLayout(new BorderLayout(0, 0));
        card.setBorder(new EmptyBorder(20, 24, 20, 24));

        card.add(sectionHeader("Fleet Management",
            "Add and manage aircraft assigned to registered airlines", "fleet"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UI.BG_PANEL);
        form.setBorder(new EmptyBorder(16, 0, 0, 0));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(0, 0, 12, 14);

        txtPlaneID    = styledField("e.g., 5J-001");
        txtPlaneModel = styledField("e.g., Airbus A320");
        txtCapacity   = styledField("e.g., 180");
        txtFirstClass = styledField("e.g., 12");
        txtEconomy    = styledField("e.g., 168");
        txtAmenities  = styledField("e.g., Wi-Fi, USB, IFE");

        g.weightx = 0.18; g.gridx = 0; g.gridy = 0;
        form.add(labeledField("PLANE ID *", txtPlaneID), g);
        g.weightx = 0.32; g.gridx = 1;
        form.add(labeledField("AIRCRAFT MODEL *", txtPlaneModel), g);
        g.weightx = 0.50; g.gridx = 2; g.gridwidth = 2; g.insets = new Insets(0, 0, 12, 0);
        form.add(labeledField("AMENITIES", txtAmenities), g);

        g.gridwidth = 1; g.insets = new Insets(0, 0, 0, 14);
        g.weightx = 0.20; g.gridx = 0; g.gridy = 1;
        form.add(labeledField("TOTAL SEATS *", txtCapacity), g);
        g.weightx = 0.20; g.gridx = 1;
        form.add(labeledField("FIRST CLASS *", txtFirstClass), g);
        g.weightx = 0.20; g.gridx = 2;
        form.add(labeledField("ECONOMY *", txtEconomy), g);
        g.weightx = 0.40; g.gridx = 3; g.insets = new Insets(0, 0, 0, 0);

        JPanel hintWrap = new JPanel(new BorderLayout());
        hintWrap.setOpaque(false);
        hintWrap.add(Box.createVerticalStrut(18), BorderLayout.NORTH);

        JLabel seatHint = new JLabel("<html><font color='#94A3B8'>First + Economy must not exceed Total</font></html>");
        seatHint.setFont(UI.FONT_SMALL);
        hintWrap.add(seatHint, BorderLayout.CENTER);
        form.add(hintWrap, g);

        card.add(form, BorderLayout.CENTER);
        card.add(buildFleetActionBar(), BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildFleetTableCard() {
        JPanel card = roundCard();
        card.setLayout(new BorderLayout(0, 0));
        card.setPreferredSize(new Dimension(0, 260));

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setBackground(UI.TABLE_ALT_ROW);
        tableHeader.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 1, 0, UI.BORDER_CARD),
            new EmptyBorder(8, 20, 8, 20)
        ));
        JLabel tTitle = new JLabel("REGISTERED FLEET");
        tTitle.setFont(UI.FONT_LABEL);
        tTitle.setForeground(UI.TEXT_MUTED);
        tableHeader.add(tTitle, BorderLayout.WEST);
        JLabel tHint = new JLabel("Click a row to edit");
        tHint.setFont(UI.FONT_SMALL.deriveFont(Font.ITALIC));
        tHint.setForeground(UI.TEXT_MUTED);
        tableHeader.add(tHint, BorderLayout.EAST);
        card.add(tableHeader, BorderLayout.NORTH);

        String[] cols = {"Plane ID", "Airline", "Model", "Total", "First Class", "Economy", "Amenities"};
        fleetModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        fleetTable = buildTable(fleetModel);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        centerRenderer.setFont(UI.FONT_BODY);
        for (int col : new int[]{3, 4, 5}) {
            fleetTable.getColumnModel().getColumn(col).setCellRenderer(centerRenderer);
        }

        fleetTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = fleetTable.getSelectedRow();
            if (row >= 0) {
                txtPlaneID.setText((String) fleetModel.getValueAt(row, 0));
                txtAirlineCode.setText((String) fleetModel.getValueAt(row, 1));
                txtPlaneModel.setText((String) fleetModel.getValueAt(row, 2));
                txtCapacity.setText(String.valueOf(fleetModel.getValueAt(row, 3)));
                txtFirstClass.setText(String.valueOf(fleetModel.getValueAt(row, 4)));
                txtEconomy.setText(String.valueOf(fleetModel.getValueAt(row, 5)));
                txtAmenities.setText((String) fleetModel.getValueAt(row, 6));
            }
        });

        JScrollPane sp = tableScrollPane(fleetTable);
        card.add(sp, BorderLayout.CENTER);

        lblFleetStatus = new JLabel("Ready - fill in the form and click Add Aircraft.");
        lblFleetStatus.setFont(UI.FONT_SMALL);
        lblFleetStatus.setForeground(UI.TEXT_MUTED);
        lblFleetStatus.setBorder(new EmptyBorder(8, 20, 8, 20));
        card.add(lblFleetStatus, BorderLayout.SOUTH);

        return card;
    }

    private JPanel buildFleetActionBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UI.BG_PANEL);
        bar.setBorder(new EmptyBorder(12, 0, 0, 0));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        JLabel hint = new JLabel("Select a row above to edit or remove");
        hint.setFont(UI.FONT_SMALL.deriveFont(Font.ITALIC));
        hint.setForeground(UI.TEXT_MUTED);
        left.add(hint);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        JButton btnRemove = UI.dangerButton("Remove Aircraft");
        JButton btnUpdate = UI.ghostButton("Update Aircraft");
        JButton btnAdd    = UI.goldButton("Add Aircraft");

        btnAdd.addActionListener(e    -> addAircraft());
        btnUpdate.addActionListener(e -> updateAircraft());
        btnRemove.addActionListener(e -> removeAircraft());

        right.add(btnRemove);
        right.add(btnUpdate);
        right.add(btnAdd);

        bar.add(left,  BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private void registerAirline() {
        String id   = txtAirlineCode.getText().trim();
        String name = txtAirlineName.getText().trim();
        String cont = txtContact.getText().trim();
        String hq   = txtHQ.getText().trim();
        if (id.isEmpty() || name.isEmpty()) {
            showAirlineStatus("Airline Code and Name are required.", UI.DANGER);
            return;
        }
        airlineDAO.add(new Airline(id, name, cont, hq));
        refreshAirlines();
        clearAirlineForm();
        showAirlineStatus("Airline registered successfully: " + name, UI.SUCCESS);
    }

    private void updateAirline() {
        int row = airlineTable.getSelectedRow();
        if (row < 0) { showAirlineStatus("Select an airline row to update.", UI.DANGER); return; }
        String id   = txtAirlineCode.getText().trim();
        String name = txtAirlineName.getText().trim();
        String cont = txtContact.getText().trim();
        String hq   = txtHQ.getText().trim();
        if (id.isEmpty() || name.isEmpty()) {
            showAirlineStatus("Airline Code and Name are required.", UI.DANGER);
            return;
        }
        airlineDAO.update(new Airline(id, name, cont, hq));
        refreshAirlines();
        clearAirlineForm();
        showAirlineStatus("Airline updated successfully.", UI.SUCCESS);
    }

    private void deleteAirline() {
        int row = airlineTable.getSelectedRow();
        if (row < 0) { showAirlineStatus("Select an airline row to delete.", UI.DANGER); return; }
        String id = (String) airlineModel.getValueAt(row, 0);
        int ok = JOptionPane.showConfirmDialog(this,
            "Delete airline " + id + "?\nThis cannot be undone.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ok == JOptionPane.YES_OPTION) {
            airlineDAO.delete(id);
            refreshAirlines();
            clearAirlineForm();
            showAirlineStatus("Airline deleted: " + id, UI.TEXT_MUTED);
        }
    }

    private void addAircraft() {
        String aid     = txtAirlineCode.getText().trim();
        String pid     = txtPlaneID.getText().trim();
        String model   = txtPlaneModel.getText().trim();
        String capTxt  = txtCapacity.getText().trim();
        String fcTxt   = txtFirstClass.getText().trim();
        String ecTxt   = txtEconomy.getText().trim();
        String amen    = txtAmenities.getText().trim();

        if (aid.isEmpty() || pid.isEmpty() || model.isEmpty()
                || capTxt.isEmpty() || fcTxt.isEmpty() || ecTxt.isEmpty()) {
            showFleetStatus("Fill Airline Code, Plane ID, Model and all seat counts.", UI.DANGER);
            return;
        }
        try {
            int cap = Integer.parseInt(capTxt);
            int fc  = Integer.parseInt(fcTxt);
            int ec  = Integer.parseInt(ecTxt);
            if (fc + ec > cap) {
                showFleetStatus("First Class + Economy seats cannot exceed Total Seats.", UI.DANGER);
                return;
            }
            planeDAO.add(new Plane(pid, aid, model, cap, fc, ec, amen));
            refreshFleet();
            clearFleetForm();
            showFleetStatus("Aircraft added to fleet: " + model + " (" + pid + ")", UI.SUCCESS);
        } catch (NumberFormatException ex) {
            showFleetStatus("Seat counts must be valid whole numbers.", UI.DANGER);
        }
    }

    private void updateAircraft() {
        int row = fleetTable.getSelectedRow();
        if (row < 0) { showFleetStatus("Select an aircraft row to update.", UI.DANGER); return; }
        try {
            String pid   = txtPlaneID.getText().trim();
            String aid   = txtAirlineCode.getText().trim();
            String model = txtPlaneModel.getText().trim();
            int cap  = Integer.parseInt(txtCapacity.getText().trim());
            int fc   = Integer.parseInt(txtFirstClass.getText().trim());
            int ec   = Integer.parseInt(txtEconomy.getText().trim());
            String amen = txtAmenities.getText().trim();
            if (fc + ec > cap) {
                showFleetStatus("First Class + Economy seats cannot exceed Total Seats.", UI.DANGER);
                return;
            }
            planeDAO.update(new Plane(pid, aid, model, cap, fc, ec, amen));
            refreshFleet();
            clearFleetForm();
            showFleetStatus("Aircraft updated: " + pid, UI.SUCCESS);
        } catch (NumberFormatException ex) {
            showFleetStatus("Seat counts must be valid whole numbers.", UI.DANGER);
        }
    }

    private void removeAircraft() {
        int row = fleetTable.getSelectedRow();
        if (row < 0) { showFleetStatus("Select an aircraft row to remove.", UI.DANGER); return; }
        String pid = (String) fleetModel.getValueAt(row, 0);
        int ok = JOptionPane.showConfirmDialog(this,
            "Remove aircraft " + pid + " from the fleet?",
            "Confirm Remove", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            planeDAO.delete(pid);
            refreshFleet();
            showFleetStatus("Aircraft removed from fleet: " + pid, UI.TEXT_MUTED);
        }
    }

    private void refreshAirlines() {
        airlineModel.setRowCount(0);
        for (Airline a : airlineDAO.getAll())
            airlineModel.addRow(new Object[]{
                a.getAirlineID(), a.getAirlineName(),
                a.getContactInfo(), a.getHeadquartersLocation()
            });
    }

    private void refreshFleet() {
        fleetModel.setRowCount(0);
        for (Plane p : planeDAO.getAll())
            fleetModel.addRow(new Object[]{
                p.getPlaneID(), p.getAirlineID(), p.getPlaneModel(),
                p.getTotalSeats(), p.getFirstClassSeats(),
                p.getEconomySeats(), p.getAmenities()
            });
    }

    private void clearAirlineForm() {
        txtAirlineCode.setText(""); txtAirlineName.setText("");
        txtContact.setText(""); txtHQ.setText("");
        airlineTable.clearSelection();
    }

    private void clearFleetForm() {
        txtPlaneID.setText(""); txtPlaneModel.setText("");
        txtCapacity.setText(""); txtFirstClass.setText("");
        txtEconomy.setText(""); txtAmenities.setText("");
        fleetTable.clearSelection();
    }

    private void showAirlineStatus(String msg, Color color) {
        lblAirlineStatus.setText(msg);
        lblAirlineStatus.setForeground(color);
    }

    private void showFleetStatus(String msg, Color color) {
        lblFleetStatus.setText(msg);
        lblFleetStatus.setForeground(color);
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
        tf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { tf.repaint(); }
            public void focusLost(FocusEvent e) { tf.repaint(); }
        });
        return tf;
    }

    private JTable buildTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        UI.styleTable(table);
        table.setRowHeight(34);
        table.setFont(UI.FONT_BODY);
        table.setSelectionBackground(new Color(0xDCEBFF));
        table.setSelectionForeground(UI.TEXT_PRIMARY);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(0xEEF1F7));
        table.setAutoCreateRowSorter(true);
        table.setFocusable(false);

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
                setBackground(sel ? new Color(0xDCEBFF) : (row % 2 == 0 ? UI.BG_PANEL : UI.TABLE_ALT_ROW));
                setForeground(sel ? UI.TEXT_PRIMARY : UI.TEXT_PRIMARY);
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


    private static void paintIcon(Graphics2D g2, String type, int x, int y, int w, int h) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int cx = x + w / 2, cy = y + h / 2;

        switch (type) {
            case "airline":
                g2.drawLine(cx - 6, cy + 2, cx + 6, cy - 2);
                g2.drawLine(cx - 2, cy - 4, cx + 2, cy + 4);
                g2.drawLine(cx - 4, cy - 1, cx - 1, cy + 1);
                g2.drawLine(cx + 1, cy - 1, cx + 4, cy + 1);
                break;
            case "fleet":
                g2.drawLine(cx - 5, cy, cx + 5, cy);
                g2.drawLine(cx - 3, cy - 3, cx + 3, cy - 3);
                g2.drawLine(cx, cy - 3, cx, cy + 3);
                g2.drawOval(cx - 1, cy + 2, 3, 3);
                break;
            default:
                g2.drawOval(cx - 4, cy - 4, 8, 8);
                break;
        }
    }

    private static void paintAirlineIcon(Graphics2D g2, int x, int y, int w, int h) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int cx = x + w / 2, cy = y + h / 2;
        g2.drawLine(cx - 7, cy + 2, cx + 7, cy - 2);
        g2.drawLine(cx - 3, cy - 5, cx + 3, cy + 5);
        g2.drawLine(cx - 5, cy - 1, cx - 2, cy + 1);
        g2.drawLine(cx + 2, cy - 1, cx + 5, cy + 1);
    }
}