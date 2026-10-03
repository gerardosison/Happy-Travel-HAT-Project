package ui.airline_management;

import dao.*;
import model.*;
import ui.Session;
import ui.UI;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

public class ManageSeatsPanel extends JPanel {

    private static final Color C_BG          = new Color(0xF0F4FA);
    private static final Color C_NAVY        = new Color(0x0D1B3E);
    private static final Color C_NAVY_MID    = new Color(0x1A3A6B);
    private static final Color C_GOLD        = new Color(0xF5C518);
    private static final Color C_GOLD_DARK   = new Color(0xD4A800);
    private static final Color C_WHITE       = Color.WHITE;
    private static final Color C_TEXT        = new Color(0x0F172A);
    private static final Color C_MUTED       = new Color(0x64748B);
    private static final Color C_BORDER      = new Color(0xDDE3EE);
    private static final Color C_GREEN       = new Color(0x059669);
    private static final Color C_GREEN_BG    = new Color(0xD1FAE5);
    private static final Color C_ORANGE      = new Color(0xD97706);
    private static final Color C_ORANGE_BG   = new Color(0xFEF3C7);
    private static final Color C_RED         = new Color(0xDC2626);
    private static final Color C_RED_BG      = new Color(0xFEE2E2);
    private static final Color C_CARD_SEL    = new Color(0xE8EFFD);
    private static final Color C_CARD_HOVER  = new Color(0xF4F7FE);

    private static final Font F_HEADING  = UI.loadFont("util/fonts/Montserrat-Bold.ttf",    20);
    private static final Font F_SUBHEAD  = UI.loadFont("util/fonts/Montserrat-Bold.ttf",    14);
    private static final Font F_CARD_ID  = UI.loadFont("util/fonts/Montserrat-Bold.ttf",    16);
    private static final Font F_BODY     = UI.loadFont("util/fonts/Inter_18pt-Regular.ttf", 13);
    private static final Font F_BOLD     = UI.loadFont("util/fonts/Inter_18pt-Bold.ttf",    13);
    private static final Font F_LABEL    = UI.loadFont("util/fonts/Inter_18pt-Regular.ttf", 11);
    private static final Font F_MONO     = new Font("Consolas", Font.BOLD, 15);

    private final boolean isAdmin;

    private final SeatDAO   seatDAO   = new SeatDAO();
    private final FlightDAO flightDAO = new FlightDAO();

    private List<Flight> allFlights = new ArrayList<>();
    private Flight       selectedFlight = null;

    private JPanel       flightListPanel;  
    private JPanel       detailPanel;      
    private JTextField   txtSearch;

    private JLabel       lblFlightTitle;
    private JLabel       lblRouteInfo;


    public ManageSeatsPanel() {
        this.isAdmin = Session.getInstance().isAdmin();

        setLayout(new BorderLayout());
        setBackground(C_BG);

        add(buildTopBar(),    BorderLayout.NORTH);
        add(buildMainSplit(), BorderLayout.CENTER);
        loadFlights();
    }


    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout(0, 0));
        bar.setBackground(UI.BLUE); 
        bar.setBorder(new javax.swing.border.EmptyBorder(18, 28, 18, 28));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        left.setOpaque(false);

        JLabel iconBadge = new JLabel("\uD83D\uDCBA") {  
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UI.YELLOW); 
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconBadge.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        iconBadge.setHorizontalAlignment(SwingConstants.CENTER);
        iconBadge.setPreferredSize(new Dimension(48, 48));
        iconBadge.setOpaque(false);

        JPanel textBlock = new JPanel(new BorderLayout(0, 3));
        textBlock.setOpaque(false);

        JLabel title = new JLabel("Seat Availability Manager");
        title.setFont(UI.FONT_SUBTITLE); 
        title.setForeground(UI.WHITE);

        String sub = isAdmin
            ? "Select a flight to manage Economy and First Class seats"
            : "Browse seat availability by flight";
        JLabel subtitle = new JLabel(sub);
        subtitle.setFont(UI.FONT_BODY); 
        subtitle.setForeground(new Color(0xD6E0EE));

        textBlock.add(title,    BorderLayout.NORTH);
        textBlock.add(subtitle, BorderLayout.SOUTH);

        left.add(iconBadge);
        left.add(textBlock);

        JButton btnRefresh = UI.smallOutlineButton("\u21BB  Refresh");   
        btnRefresh.setForeground(UI.WHITE);
        btnRefresh.addActionListener(e -> {
            loadFlights();
            if (selectedFlight != null) loadSeatDetail(selectedFlight);
        });

        bar.add(left,        BorderLayout.WEST);
        bar.add(btnRefresh,  BorderLayout.EAST);
        return bar;
    }

    private JSplitPane buildMainSplit() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildLeftPanel(), buildRightPanel());
        split.setDividerLocation(320);
        split.setDividerSize(1);
        split.setBackground(C_BORDER);
        split.setBorder(BorderFactory.createEmptyBorder());
        split.setContinuousLayout(true);
        return split;
    }

    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(C_WHITE);
        panel.setPreferredSize(new Dimension(320, 0));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, C_BORDER));

        JPanel searchBar = new JPanel(new BorderLayout(8, 0));
        searchBar.setBackground(C_WHITE);
        searchBar.setBorder(new EmptyBorder(14, 14, 10, 14));

        JLabel searchIcon = new JLabel("\uD83D\uDD0D");  
        searchIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        searchIcon.setForeground(C_MUTED);

        txtSearch = new JTextField();
        txtSearch.setFont(F_BODY);
        txtSearch.setForeground(C_TEXT);
        txtSearch.setBackground(C_BG);
        txtSearch.setCaretColor(C_NAVY);
        txtSearch.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(C_BORDER, 1),
            new EmptyBorder(8, 10, 8, 10)));
        txtSearch.setToolTipText("Search by Flight ID or route…");

        txtSearch.setForeground(C_MUTED);
        txtSearch.setText("Search flights…");
        txtSearch.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (txtSearch.getText().equals("Search flights…")) {
                    txtSearch.setText("");
                    txtSearch.setForeground(C_TEXT);
                }
            }
            public void focusLost(FocusEvent e) {
                if (txtSearch.getText().isEmpty()) {
                    txtSearch.setText("Search flights…");
                    txtSearch.setForeground(C_MUTED);
                }
            }
        });
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { filterFlights(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { filterFlights(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterFlights(); }
        });

        searchBar.add(searchIcon, BorderLayout.WEST);
        searchBar.add(txtSearch,  BorderLayout.CENTER);

        JPanel listHeader = new JPanel(new BorderLayout());
        listHeader.setBackground(C_BG);
        listHeader.setBorder(new EmptyBorder(4, 14, 4, 14));

        JLabel lhLabel = new JLabel("FLIGHTS");
        lhLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lhLabel.setForeground(C_MUTED);
        listHeader.add(lhLabel, BorderLayout.WEST);

        JPanel topPart = new JPanel(new BorderLayout());
        topPart.setBackground(C_WHITE);
        topPart.add(searchBar,  BorderLayout.NORTH);
        topPart.add(listHeader, BorderLayout.SOUTH);
        flightListPanel = new JPanel();
        flightListPanel.setLayout(new BoxLayout(flightListPanel, BoxLayout.Y_AXIS));
        flightListPanel.setBackground(C_WHITE);

        JScrollPane scroll = new JScrollPane(flightListPanel);
        scroll.setBackground(C_WHITE);
        scroll.getViewport().setBackground(C_WHITE);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        styleScrollBar(scroll.getVerticalScrollBar());

        panel.add(topPart, BorderLayout.NORTH);
        panel.add(scroll,  BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildRightPanel() {
        detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBackground(C_BG);

        JPanel placeholder = new JPanel(new GridBagLayout());
        placeholder.setBackground(C_BG);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0; c.anchor = GridBagConstraints.CENTER;

        JLabel planeIcon = new JLabel("\u2708\uFE0F");   
        planeIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 52));
        planeIcon.setHorizontalAlignment(SwingConstants.CENTER);

        c.insets = new Insets(0, 0, 10, 0);
        placeholder.add(planeIcon, c);

        c.gridy = 1; c.insets = new Insets(0, 0, 4, 0);
        JLabel ph1 = new JLabel("Select a Flight");
        ph1.setFont(F_SUBHEAD);
        ph1.setForeground(C_TEXT);
        placeholder.add(ph1, c);

        c.gridy = 2; c.insets = new Insets(0, 0, 0, 0);
        JLabel ph2 = new JLabel("Choose a flight from the list to manage its seat availability");
        ph2.setFont(F_BODY);
        ph2.setForeground(C_MUTED);
        placeholder.add(ph2, c);

        detailPanel.add(placeholder, BorderLayout.CENTER);
        return detailPanel;
    }

    private void loadFlights() {
        try {
            allFlights = flightDAO.getAll();
        } catch (Exception ex) {
            ex.printStackTrace();
            allFlights = new ArrayList<>();
            showError("Failed to load flights: " + ex.getMessage());
        }
        renderFlightCards(allFlights);
    }

    private void filterFlights() {
        String q = txtSearch.getText().trim().toLowerCase();
        if (q.equals("search flights…")) q = "";
        final String query = q;
        List<Flight> filtered = new ArrayList<>();
        for (Flight f : allFlights) {
            if (f.getFlightID().toLowerCase().contains(query)
                    || (f.getRouteID() != null && f.getRouteID().toLowerCase().contains(query)))
                filtered.add(f);
        }
        renderFlightCards(filtered);
    }

    private void renderFlightCards(List<Flight> flights) {
        flightListPanel.removeAll();
        if (flights.isEmpty()) {
            JPanel empty = new JPanel(new FlowLayout(FlowLayout.CENTER));
            empty.setBackground(C_WHITE);
            JLabel lbl = new JLabel("No flights found");
            lbl.setFont(F_BODY);
            lbl.setForeground(C_MUTED);
            empty.add(lbl);
            flightListPanel.add(empty);
        }
        for (Flight f : flights) {
            flightListPanel.add(buildFlightCard(f));
        }
        flightListPanel.add(Box.createVerticalGlue());
        flightListPanel.revalidate();
        flightListPanel.repaint();
    }

    private JPanel buildFlightCard(Flight flight) {
        boolean isSelected = selectedFlight != null
                && selectedFlight.getFlightID().equals(flight.getFlightID());

        JPanel card = new JPanel(new BorderLayout(12, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isSelected ? C_CARD_SEL : C_WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                if (isSelected) {
                    g2.setColor(C_NAVY);
                    g2.fillRect(0, 0, 4, getHeight());
                }
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new CompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER),
            new EmptyBorder(13, 18, 13, 14)));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel planeLabel = new JLabel("\u2708");  
        planeLabel.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 18));
        planeLabel.setForeground(isSelected ? C_NAVY : C_MUTED);
        planeLabel.setVerticalAlignment(SwingConstants.CENTER);

        JPanel textBlock = new JPanel(new BorderLayout(0, 3));
        textBlock.setOpaque(false);

        JLabel idLabel = new JLabel(flight.getFlightID());
        idLabel.setFont(F_CARD_ID);
        idLabel.setForeground(isSelected ? C_NAVY : C_TEXT);

        String subInfo = buildFlightSubInfo(flight);
        JLabel subLabel = new JLabel(subInfo);
        subLabel.setFont(F_LABEL);
        subLabel.setForeground(C_MUTED);

        textBlock.add(idLabel,  BorderLayout.NORTH);
        textBlock.add(subLabel, BorderLayout.SOUTH);

        card.add(planeLabel, BorderLayout.WEST);
        card.add(textBlock,  BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!isSelected) card.setBackground(C_CARD_HOVER);
                card.repaint();
            }
            public void mouseExited(MouseEvent e) {
                if (!isSelected) card.setBackground(C_WHITE);
                card.repaint();
            }
            public void mouseClicked(MouseEvent e) {
                selectedFlight = flight;
                renderFlightCards(filterList());
                loadSeatDetail(flight);
            }
        });

        card.setPreferredSize(new Dimension(360, 70));
        card.setMaximumSize(new Dimension(360, 70));

        return card;
    }

    private List<Flight> filterList() {
        String q = txtSearch.getText().trim().toLowerCase();
        if (q.equals("search flights…") || q.isEmpty()) return allFlights;
        List<Flight> filtered = new ArrayList<>();
        for (Flight f : allFlights) {
            if (f.getFlightID().toLowerCase().contains(q)
                    || (f.getRouteID() != null && f.getRouteID().toLowerCase().contains(q)))
                filtered.add(f);
        }
        return filtered;
    }

    private String buildFlightSubInfo(Flight f) {
        StringBuilder sb = new StringBuilder();
        if (f.getAirlineID() != null) sb.append(f.getAirlineID());
        if (f.getRouteID()   != null) sb.append("  ·  ").append(f.getRouteID());
        return sb.length() > 0 ? sb.toString() : "No additional info";
    }

    private void loadSeatDetail(Flight flight) {
        detailPanel.removeAll();

        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setBackground(C_NAVY);
        header.setBorder(new EmptyBorder(20, 28, 20, 28));

        JPanel headerLeft = new JPanel(new BorderLayout(0, 5));
        headerLeft.setOpaque(false);

        lblFlightTitle = new JLabel("Flight  " + flight.getFlightID());
        lblFlightTitle.setFont(F_HEADING);
        lblFlightTitle.setForeground(C_GOLD);

        lblRouteInfo = new JLabel(buildFlightSubInfo(flight));
        lblRouteInfo.setFont(F_BODY);
        lblRouteInfo.setForeground(new Color(0xAEC3E8));

        headerLeft.add(lblFlightTitle, BorderLayout.NORTH);
        headerLeft.add(lblRouteInfo,   BorderLayout.SOUTH);
        header.add(headerLeft, BorderLayout.WEST);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(C_BG);
        content.setBorder(new EmptyBorder(24, 28, 24, 28));

        List<Seat> seats = new ArrayList<>();
        try {
            seats = seatDAO.getAll();
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Failed to load seat data: " + ex.getMessage());
        }

        Seat economy   = null;
        Seat firstClass = null;
        for (Seat s : seats) {
            if (s.getFlightID() != null && s.getFlightID().equalsIgnoreCase(flight.getFlightID())) {
                if ("Economy".equalsIgnoreCase(s.getSeatClass()))   economy    = s;
                if ("First Class".equalsIgnoreCase(s.getSeatClass())) firstClass = s;
            }
        }

        content.add(buildSeatClassCard("Economy",     "\uD83D\uDCBA", economy,   flight.getFlightID()));
        content.add(Box.createVerticalStrut(18));
        content.add(buildSeatClassCard("First Class", "\u2B50",        firstClass, flight.getFlightID()));
        content.add(Box.createVerticalStrut(18));

        if (!isAdmin) {
            JPanel infoBanner = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
            infoBanner.setBackground(new Color(0xEFF6FF));
            infoBanner.setBorder(BorderFactory.createLineBorder(new Color(0xBFDBFE), 1));
            infoBanner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            JLabel infoLbl = new JLabel("\u2139\uFE0F  Contact an administrator to modify seat records.");
            infoLbl.setFont(F_BODY);
            infoLbl.setForeground(new Color(0x1D4ED8));
            infoBanner.add(infoLbl);
            content.add(infoBanner);
        }

        JScrollPane scrollContent = new JScrollPane(content);
        scrollContent.setBackground(C_BG);
        scrollContent.getViewport().setBackground(C_BG);
        scrollContent.setBorder(BorderFactory.createEmptyBorder());
        scrollContent.getVerticalScrollBar().setUnitIncrement(16);
        styleScrollBar(scrollContent.getVerticalScrollBar());

        detailPanel.add(header,        BorderLayout.NORTH);
        detailPanel.add(scrollContent, BorderLayout.CENTER);
        detailPanel.revalidate();
        detailPanel.repaint();
    }

    private JPanel buildSeatClassCard(String className, String emoji, Seat seat, String flightID) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(C_WHITE);
        card.setBorder(BorderFactory.createLineBorder(C_BORDER, 1));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setBackground(C_NAVY_MID);
        cardHeader.setBorder(new EmptyBorder(12, 18, 12, 18));

        JPanel cardHeaderLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        cardHeaderLeft.setOpaque(false);

        JLabel emojiLabel = new JLabel(emoji);
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        emojiLabel.setForeground(C_WHITE);

        JLabel classLabel = new JLabel(className);
        classLabel.setFont(F_SUBHEAD);
        classLabel.setForeground(C_WHITE);

        cardHeaderLeft.add(emojiLabel);
        cardHeaderLeft.add(classLabel);

        JLabel statusBadge = buildStatusBadge(seat);
        cardHeader.add(cardHeaderLeft, BorderLayout.WEST);
        cardHeader.add(statusBadge,    BorderLayout.EAST);
        card.add(cardHeader, BorderLayout.NORTH);

        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(C_WHITE);
        body.setBorder(new EmptyBorder(20, 22, 20, 22));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets  = new Insets(8, 8, 8, 8);
        gc.fill    = GridBagConstraints.HORIZONTAL;
        gc.anchor  = GridBagConstraints.WEST;

        if (seat != null) {
            int avail = seat.getAvailableSeats();
            BigDecimal price = seat.getPrice();

            gc.gridx = 0; gc.gridy = 0; gc.weightx = 0.5;
            body.add(statBlock("Available Seats", String.valueOf(avail)), gc);
            gc.gridx = 1;
            body.add(statBlock("Price per Seat",
                    "\u20B1" + (price != null ? price.toPlainString() : "—")), gc);
        } else {
            gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2; gc.weightx = 1.0;
            JLabel noRecord = new JLabel("No seat record found for this class.");
            noRecord.setFont(F_BODY);
            noRecord.setForeground(C_MUTED);
            body.add(noRecord, gc);
            gc.gridwidth = 1;
        }

        if (isAdmin) {
            JTextField fldAvail = styledField("e.g. 120");
            JTextField fldPrice = styledField("e.g. 3500.00");

            if (seat != null) {
                fldAvail.setText(String.valueOf(seat.getAvailableSeats()));
                fldPrice.setText(seat.getPrice() != null ? seat.getPrice().toPlainString() : "");
            }

            gc.gridx = 0; gc.gridy = 1; gc.weightx = 0.5;
            body.add(labeledField("Available Seats", fldAvail), gc);
            gc.gridx = 1;
            body.add(labeledField("Price (\u20B1)", fldPrice), gc);

            gc.gridx = 0; gc.gridy = 2; gc.gridwidth = 2; gc.weightx = 1.0;
            JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            btnRow.setBackground(C_WHITE);

            if (seat != null) {
                JButton btnDelete = outlineBtn("Delete", C_RED);
                btnDelete.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(this,
                        "Delete " + className + " record for flight " + flightID + "?",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            seatDAO.delete(flightID, className);
                            showSuccess("Deleted " + className + " record.");
                            loadFlights();
                            loadSeatDetail(selectedFlight);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            showError("Delete failed: " + ex.getMessage());
                        }
                    }
                });
                btnRow.add(btnDelete);

                JButton btnUpdate = navBtn("Update");
                btnUpdate.addActionListener(e -> {
                    try {
                        int av = Integer.parseInt(fldAvail.getText().trim());
                        BigDecimal pr = new BigDecimal(fldPrice.getText().trim());
                        seatDAO.update(new Seat(flightID, className, pr, av));
                        showSuccess(className + " record updated.");
                        loadFlights();
                        loadSeatDetail(selectedFlight);
                    } catch (NumberFormatException ex) {
                        showError("Please enter valid numbers for seats and price.");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showError("Update failed: " + ex.getMessage());
                    }
                });
                btnRow.add(btnUpdate);
            } else {
                JButton btnAdd = goldBtn("+ Add Record");
                btnAdd.addActionListener(e -> {
                    try {
                        int av = Integer.parseInt(fldAvail.getText().trim());
                        BigDecimal pr = new BigDecimal(fldPrice.getText().trim());
                        seatDAO.add(new Seat(flightID, className, pr, av));
                        showSuccess(className + " record added.");
                        loadFlights();
                        loadSeatDetail(selectedFlight);
                    } catch (NumberFormatException ex) {
                        showError("Please enter valid numbers for seats and price.");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showError("Add failed: " + ex.getMessage());
                    }
                });
                btnRow.add(btnAdd);
            }

            body.add(btnRow, gc);
        }

        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JLabel buildStatusBadge(Seat seat) {
        String text;
        Color  bg, fg;
        if (seat == null) {
            text = "NO RECORD";
            bg   = new Color(0xE5E7EB);
            fg   = C_MUTED;
        } else {
            int avail = seat.getAvailableSeats();
            if (avail == 0)       { text = "FULL";      bg = C_RED_BG;    fg = C_RED;    }
            else if (avail <= 10) { text = "LOW";       bg = C_ORANGE_BG; fg = C_ORANGE; }
            else                  { text = "AVAILABLE"; bg = C_GREEN_BG;  fg = C_GREEN;  }
        }
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(fg);
        lbl.setBackground(bg);
        lbl.setOpaque(true);
        lbl.setBorder(new EmptyBorder(4, 10, 4, 10));
        return lbl;
    }

    private JPanel statBlock(String label, String value) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(C_BG);
        p.setBorder(new EmptyBorder(10, 14, 10, 14));

        JLabel val = new JLabel(value);
        val.setFont(F_MONO);
        val.setForeground(C_NAVY);

        JLabel lbl = new JLabel(label.toUpperCase());
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lbl.setForeground(C_MUTED);

        p.add(val, BorderLayout.NORTH);
        p.add(lbl, BorderLayout.SOUTH);
        return p;
    }

    private void styleScrollBar(JScrollBar sb) {
        sb.setPreferredSize(new Dimension(6, 6));
        sb.setBackground(C_BG);
        sb.setUI(new BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                thumbColor = new Color(0xB0BEC5);
                trackColor = C_BG;
            }
            @Override protected JButton createDecreaseButton(int o) { return zeroBtn(); }
            @Override protected JButton createIncreaseButton(int o) { return zeroBtn(); }
            @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                g2.fillRoundRect(r.x + 1, r.y + 3, r.width - 2, r.height - 6, 6, 6);
                g2.dispose();
            }
            @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
                g.setColor(trackColor);
                g.fillRect(r.x, r.y, r.width, r.height);
            }
            private JButton zeroBtn() {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(0, 0));
                b.setMinimumSize(new Dimension(0, 0));
                b.setMaximumSize(new Dimension(0, 0));
                return b;
            }
        });
    }

    private JPanel labeledField(String label, JTextField field) {
        JPanel wrap = new JPanel(new BorderLayout(0, 5));
        wrap.setBackground(C_WHITE);
        JLabel lbl = new JLabel(label);
        lbl.setFont(F_LABEL);
        lbl.setForeground(C_MUTED);
        wrap.add(lbl,   BorderLayout.NORTH);
        wrap.add(field, BorderLayout.CENTER);
        return wrap;
    }

    private JTextField styledField(String placeholder) {
        JTextField tf = new JTextField(14);
        tf.setFont(F_BODY);
        tf.setForeground(C_TEXT);
        tf.setBackground(C_BG);
        tf.setCaretColor(C_NAVY);
        tf.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(C_BORDER, 1),
            new EmptyBorder(8, 12, 8, 12)));
        tf.setToolTipText(placeholder);
        return tf;
    }

    private JButton goldBtn(String text) {
        JButton b = new JButton(text);
        b.setBackground(C_GOLD);
        b.setForeground(C_NAVY);
        b.setFont(F_BOLD);
        b.setBorder(new EmptyBorder(9, 20, 9, 20));
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(C_GOLD_DARK); }
            public void mouseExited(MouseEvent e)  { b.setBackground(C_GOLD); }
        });
        return b;
    }

    private JButton navBtn(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(0x1A3A6B));
        b.setForeground(C_WHITE);
        b.setFont(F_BOLD);
        b.setBorder(new EmptyBorder(8, 18, 8, 18));
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(new Color(0x243F78)); }
            public void mouseExited(MouseEvent e)  { b.setBackground(new Color(0x1A3A6B)); }
        });
        return b;
    }

    private JButton outlineBtn(String text, Color color) {
        JButton b = new JButton(text);
        b.setBackground(C_WHITE);
        b.setForeground(color);
        b.setFont(F_BOLD);
        b.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(color, 1),
            new EmptyBorder(8, 16, 8, 16)));
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}