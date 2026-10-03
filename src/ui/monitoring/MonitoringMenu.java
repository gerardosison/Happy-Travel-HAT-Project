package ui.monitoring;
import dao.*;
import model.*;
import ui.UI;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MonitoringMenu extends JPanel {

    private static final Color NAVY        = new Color(0x0D1B2E);
    private static final Color GOLD        = new Color(0xF5C518);
    private static final Color GOLD_DIM    = new Color(0xC49B10);
    private static final Color WHITE       = Color.WHITE;
    private static final Color BG_PAGE     = new Color(0xF2F5FB);
    private static final Color BG_CARD     = new Color(0xFFFFFF);
    private static final Color TEXT_DARK   = new Color(0x0D1B2E);
    private static final Color TEXT_MID    = new Color(0x4A6080);
    private static final Color TEXT_DIM    = new Color(0x8AA0BC);
    private static final Color BORDER_COL  = new Color(0xD8E2F0);
    private static final Color ROW_EVEN    = new Color(0xF7F9FD);
    private static final Color ROW_ODD     = new Color(0xFFFFFF);
    private static final Color SEL_BG      = new Color(0xDDEAFF);
    private static final Color SEL_FG      = new Color(0x0D1B2E);
    private static final Color SUCCESS     = new Color(0x1A9E6E);
    private static final Color DANGER      = new Color(0xE74C3C);
    private static final Color INFO_BLUE   = new Color(0x2979CC);

    private final List<TransactionLog> allLogs;
    private final List<Flight>         allFlights;
    private final List<Airline>        allAirlines;
    private final List<Route>          allRoutes;
    private final List<Plane>          allPlanes;
    private final List<Seat>           allSeats;
    private final List<Reservation>    allReservations;
    private final List<Passenger>      allPassengers;

    private final JPanel contentArea = new JPanel(new CardLayout());
    private final String[] TAB_KEYS  = {"transactions", "flights", "passengers", "seats"};
    private final String[] TAB_LABELS = {
        "Transaction Report",
        "Flight Details",
        "Flight Passengers",
        "Seat Price & Availability"
    };
    private JButton[] tabButtons;
    private int activeTab = 0;

    public MonitoringMenu() {
        allLogs         = new TransactionLogDAO().getAll();
        allFlights      = new FlightDAO().getAll();
        allAirlines     = new AirlineDAO().getAll();
        allRoutes       = new RouteDAO().getAll();
        allPlanes       = new PlaneDAO().getAll();
        allSeats        = new SeatDAO().getAll();
        allReservations = new ReservationDAO().getAll();
        allPassengers   = new PassengerDAO().getAll();

        setLayout(new BorderLayout());
        setBackground(BG_PAGE);

        add(buildHeader(),   BorderLayout.NORTH);
        add(buildBody(),     BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
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
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth("📊")) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString("📊", x, y);
                
                g2.dispose();
            }
        };
        iconBadge.setPreferredSize(new Dimension(48, 48));
        iconBadge.setOpaque(false);

        JPanel textBlock = new JPanel(new BorderLayout(0, 3));
        textBlock.setOpaque(false);

        JLabel titleLbl = new JLabel("Monitoring & Reports");
        titleLbl.setFont(UI.FONT_SUBTITLE); 
        titleLbl.setForeground(UI.WHITE);

        int totalRecords = allLogs.size() + allFlights.size() + allReservations.size() + allSeats.size();
        JLabel subLbl = new JLabel("System analytical overview and operational logs");
        subLbl.setFont(UI.FONT_BODY); 
        subLbl.setForeground(new Color(0xD6E0EE)); 

        textBlock.add(titleLbl, BorderLayout.NORTH);
        textBlock.add(subLbl,   BorderLayout.SOUTH);

        left.add(iconBadge);
        left.add(textBlock);

        JPanel right = new JPanel(new GridBagLayout());
        right.setOpaque(false);

        JLabel badgeLbl = new JLabel(totalRecords + " total records");
        badgeLbl.setFont(UI.FONT_LABEL);
        badgeLbl.setForeground(UI.YELLOW);

        JPanel pillBadge = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Subtle transparent background variant matching the brand identity
                g2.setColor(new Color(UI.YELLOW.getRed(), UI.YELLOW.getGreen(), UI.YELLOW.getBlue(), 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Clean accent outline
                g2.setColor(UI.YELLOW);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };
        pillBadge.setOpaque(false);
        pillBadge.setBorder(new javax.swing.border.EmptyBorder(6, 14, 6, 14));
        pillBadge.add(badgeLbl, BorderLayout.CENTER);
        
        right.add(pillBadge);

        header.add(left,  BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(BG_PAGE);
        body.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Custom horizontal tab bar
        body.add(buildTabBar(),  BorderLayout.NORTH);

        // Content area
        contentArea.setBackground(BG_PAGE);
        contentArea.add(buildTransactionReport(),   TAB_KEYS[0]);
        contentArea.add(buildFlightDetailsReport(), TAB_KEYS[1]);
        contentArea.add(buildFlightPassengersReport(), TAB_KEYS[2]);
        contentArea.add(buildSeatPriceReport(),     TAB_KEYS[3]);

        body.add(contentArea, BorderLayout.CENTER);
        return body;
    }

    private JPanel buildTabBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
            }
        };
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(0, 0, 12, 0));
        tabButtons = new JButton[TAB_LABELS.length];

        for (int i = 0; i < TAB_LABELS.length; i++) {
            final int idx = i;
            JButton btn = new JButton(TAB_LABELS[i]) {
                boolean isActive() { return activeTab == idx; }
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    if (isActive()) {
                        g2.setColor(NAVY);
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    } else if (getModel().isRollover()) {
                        g2.setColor(new Color(0xE2EAF7));
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    } else {
                        g2.setColor(WHITE);
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                        g2.setColor(BORDER_COL);
                        g2.setStroke(new BasicStroke(1f));
                        g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                    }
                    // Gold underline for active
                    if (isActive()) {
                        g2.setColor(GOLD);
                        g2.fillRoundRect(8, getHeight()-4, getWidth()-16, 3, 3, 3);
                    }
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btn.setForeground(i == 0 ? WHITE : TEXT_MID);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setBorder(new EmptyBorder(9, 16, 12, 16));
            btn.setOpaque(false);
            btn.addActionListener(e -> switchTab(idx));
            tabButtons[i] = btn;
            bar.add(btn);
        }
        return bar;
    }

    private void switchTab(int idx) {
        activeTab = idx;
        CardLayout cl = (CardLayout) contentArea.getLayout();
        cl.show(contentArea, TAB_KEYS[idx]);
        for (int i = 0; i < tabButtons.length; i++) {
            tabButtons[i].setForeground(i == idx ? WHITE : TEXT_MID);
            tabButtons[i].repaint();
        }
    }

    private JPanel buildTransactionReport() {
        // Compute stats
        long bookings   = allLogs.stream().filter(t -> "Booking".equalsIgnoreCase(t.getTransactionType())).count();
        long onlineCnt  = allLogs.stream().filter(t -> "Online".equalsIgnoreCase(t.getChannel())).count();
        long counterCnt = allLogs.stream().filter(t -> "Counter".equalsIgnoreCase(t.getChannel())).count();

        JPanel[] statCards = {
            buildStatCard("Total Transactions", String.valueOf(allLogs.size()),      "📄", INFO_BLUE),
            buildStatCard("Bookings",           String.valueOf(bookings),            "🎫", SUCCESS),
            buildStatCard("Online",             String.valueOf(onlineCnt),           "🌐", new Color(0x8B5CF6)),
            buildStatCard("Counter",            String.valueOf(counterCnt),          "🏢", new Color(0xF59E0B))
        };

        String[] cols = {
            "#", "Transaction ID", "Reservation ID", "Passenger",
            "Type", "Date", "Time", "Channel"
        };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm:ss");
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        int rowNum = 1;
        for (TransactionLog t : allLogs) {
            String passengerName = "—";
            Reservation res = allReservations.stream()
                .filter(r -> r.getReservationID() == t.getReservationID())
                .findFirst().orElse(null);
            if (res != null) {
                Passenger p = allPassengers.stream()
                    .filter(x -> x.getPassengerID() == res.getPassengerID())
                    .findFirst().orElse(null);
                if (p != null) passengerName = p.getFullName();
            }
            model.addRow(new Object[]{
                rowNum++,
                t.getTransactionID(), t.getReservationID(), passengerName,
                t.getTransactionType(),
                t.getTransactionDate().format(dateFmt),
                t.getTransactionTime().format(timeFmt),
                t.getChannel()
            });
        }
        JTable table = buildStyledTable(model);

        // Color "Channel" column
        table.getColumnModel().getColumn(7).setCellRenderer(new BadgeCellRenderer("Online", "Counter"));

        return assembleReportPanel(statCards, null, table, "transaction_report.csv");
    }

    private JPanel buildFlightDetailsReport() {
        long totalSeats   = allPlanes.stream().mapToLong(Plane::getTotalSeats).sum();
        long totalFcSeats = allPlanes.stream().mapToLong(Plane::getFirstClassSeats).sum();
        long totalEcoSeats= allPlanes.stream().mapToLong(Plane::getEconomySeats).sum();

        JPanel[] statCards = {
            buildStatCard("Total Flights",   String.valueOf(allFlights.size()), "🛫", INFO_BLUE),
            buildStatCard("Total Seats",     String.valueOf(totalSeats),        "💺", SUCCESS),
            buildStatCard("First Class",     String.valueOf(totalFcSeats),      "⭐", GOLD_DIM),
            buildStatCard("Economy",         String.valueOf(totalEcoSeats),     "🪑", TEXT_MID)
        };

        String[] cols = {
            "#", "Flight ID", "Airline", "Origin", "Destination",
            "Plane Model", "Departure", "Arrival", "Duration",
            "Total Seats", "FC Seats", "Eco Seats"
        };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        int rowNum = 1;
        for (Flight f : allFlights) {
            Airline a = findAirline(f.getAirlineID());
            Route   r = findRoute(f.getRouteID());
            Plane   p = findPlane(f.getPlaneID());
            model.addRow(new Object[]{
                rowNum++,
                f.getFlightID(),
                a != null ? a.getAirlineName()      : f.getAirlineID(),
                r != null ? r.getOrigin()           : "N/A",
                r != null ? r.getDestination()      : "N/A",
                p != null ? p.getPlaneModel()       : f.getPlaneID(),
                f.getDepartureDateTime().format(dtf),
                f.getArrivalDateTime().format(dtf),
                r != null ? r.getEstimatedDuration(): "N/A",
                p != null ? p.getTotalSeats()       : "N/A",
                p != null ? p.getFirstClassSeats()  : "N/A",
                p != null ? p.getEconomySeats()     : "N/A"
            });
        }
        JTable table = buildStyledTable(model);
        return assembleReportPanel(statCards, null, table, "flight_details_report.csv");
    }

    private JPanel buildFlightPassengersReport() {
        long confirmed = allReservations.stream().filter(r -> "Confirmed".equalsIgnoreCase(r.getStatus())).count();
        long cancelled = allReservations.stream().filter(r -> "Cancelled".equalsIgnoreCase(r.getStatus())).count();
        long fc        = allReservations.stream().filter(r -> "First Class".equalsIgnoreCase(r.getSeatClass())).count();
        long eco       = allReservations.stream().filter(r -> "Economy".equalsIgnoreCase(r.getSeatClass())).count();

        JPanel[] statCards = {
            buildStatCard("Total Reservations", String.valueOf(allReservations.size()), "📋", INFO_BLUE),
            buildStatCard("Confirmed",          String.valueOf(confirmed),              "✅", SUCCESS),
            buildStatCard("Cancelled",          String.valueOf(cancelled),              "❌", DANGER),
            buildStatCard("First Class",        String.valueOf(fc),                    "⭐", GOLD_DIM)
        };

        String[] cols = {
            "#", "Ticket #", "Passenger Name", "Flight ID",
            "Origin", "Destination", "Departure", "Seat Class", "Status", "Booking Date"
        };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = buildStyledTable(model);

        // Color "Status" column (col 8) and "Seat Class" (col 7)
        table.getColumnModel().getColumn(8).setCellRenderer(new StatusCellRenderer());
        table.getColumnModel().getColumn(7).setCellRenderer(new SeatClassCellRenderer());

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        Runnable populate = () -> {
            model.setRowCount(0);
            int[] rowNum = {1};
            for (Reservation res : allReservations) {
                Passenger p = allPassengers.stream()
                    .filter(x -> x.getPassengerID() == res.getPassengerID())
                    .findFirst().orElse(null);
                String fullName = p != null ? p.getFullName() : "Unknown";
                Flight  f  = findFlight(res.getFlightID());
                Route   rt = f != null ? findRoute(f.getRouteID()) : null;
                model.addRow(new Object[]{
                    rowNum[0]++, res.getTicketNumber(), fullName, res.getFlightID(),
                    rt != null ? rt.getOrigin()      : "N/A",
                    rt != null ? rt.getDestination() : "N/A",
                    f  != null ? f.getDepartureDateTime().format(dtf) : "N/A",
                    res.getSeatClass(), res.getStatus(), res.getReservationDate()
                });
            }
        };
        populate.run();

        // Filter controls
        JPanel controls = buildFilterBar(table, model, populate, fc, eco);

        return assembleReportPanel(statCards, controls, table, "flight_passengers_report.csv");
    }

    private JPanel buildSeatPriceReport() {
        long fullFlights  = allSeats.stream().filter(s -> s.getAvailableSeats() == 0).count();
        long availFlights = allSeats.stream().filter(s -> s.getAvailableSeats() > 0).count();
        long fcSeats = allSeats.stream().filter(s -> "First Class".equalsIgnoreCase(s.getSeatClass())).count();

        JPanel[] statCards = {
            buildStatCard("Seat Records",     String.valueOf(allSeats.size()), "💺", INFO_BLUE),
            buildStatCard("Has Availability", String.valueOf(availFlights),    "✅", SUCCESS),
            buildStatCard("Fully Booked",     String.valueOf(fullFlights),     "🚫", DANGER),
            buildStatCard("First Class",      String.valueOf(fcSeats),         "⭐", GOLD_DIM)
        };

        String[] cols = {
            "#", "Flight ID", "Airline", "Route",
            "Seat Class", "Price (USD)", "Available", "Capacity", "Booked", "Availability %"
        };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        int rowNum = 1;
        for (Seat s : allSeats) {
            Flight  f = findFlight(s.getFlightID());
            if (f == null) continue;
            Airline a = findAirline(f.getAirlineID());
            Route   r = findRoute(f.getRouteID());
            Plane   p = findPlane(f.getPlaneID());
            int capacity = 0;
            if (p != null) {
                capacity = "First Class".equalsIgnoreCase(s.getSeatClass())
                    ? p.getFirstClassSeats() : p.getEconomySeats();
            }
            int booked = Math.max(0, capacity - s.getAvailableSeats());
            double pct = capacity > 0 ? (s.getAvailableSeats() * 100.0 / capacity) : 0.0;
            model.addRow(new Object[]{
                rowNum++,
                s.getFlightID(),
                a != null ? a.getAirlineName() : f.getAirlineID(),
                r != null ? r.getOrigin() + " → " + r.getDestination() : "N/A",
                s.getSeatClass(),
                "$" + s.getPrice().toPlainString(),
                s.getAvailableSeats(),
                capacity, booked,
                String.format("%.1f%%", pct)
            });
        }
        JTable table = buildStyledTable(model);
        table.getColumnModel().getColumn(4).setCellRenderer(new SeatClassCellRenderer());

        return assembleReportPanel(statCards, null, table, "seat_price_availability_report.csv");
    }

    private JPanel assembleReportPanel(JPanel[] statCards, JPanel controls, JTable table, String csvName) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BG_PAGE);

        JPanel statsRow = new JPanel(new GridLayout(1, statCards.length, 10, 0));
        statsRow.setOpaque(false);
        for (JPanel card : statCards) statsRow.add(card);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(statsRow);
        if (controls != null) {
            top.add(Box.createVerticalStrut(10));
            top.add(controls);
        }

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(BG_CARD);
        sp.setBackground(BG_CARD);

        JPanel tableCard = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(BORDER_COL);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
            }
        };
        tableCard.setOpaque(false);
        tableCard.setBorder(new EmptyBorder(0, 0, 0, 0));
        tableCard.add(buildTableHeader(table, csvName), BorderLayout.NORTH);
        tableCard.add(sp, BorderLayout.CENTER);
        tableCard.add(buildTableFooter(table, csvName), BorderLayout.SOUTH);

        panel.add(top,       BorderLayout.NORTH);
        panel.add(tableCard, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildTableHeader(JTable table, String csvName) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER_COL),
            new EmptyBorder(10, 16, 10, 16)
        ));

        JLabel title = new JLabel(csvName.replace("_", " ").replace(".csv", "").toUpperCase());
        title.setFont(new Font("Segoe UI", Font.BOLD, 11));
        title.setForeground(TEXT_DIM);

        JLabel count = new JLabel(table.getRowCount() + " records");
        count.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        count.setForeground(TEXT_DIM);

        table.getModel().addTableModelListener(e -> {
            SwingUtilities.invokeLater(() -> count.setText(table.getRowCount() + " records"));
        });

        bar.add(title, BorderLayout.WEST);
        bar.add(count, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildTableFooter(JTable table, String csvName) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, BORDER_COL),
            new EmptyBorder(8, 16, 8, 16)
        ));

        JLabel hint = new JLabel("Click column header to sort");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(TEXT_DIM);

        JButton exportBtn = buildExportButton(table, csvName);
        bar.add(hint,      BorderLayout.WEST);
        bar.add(exportBtn, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildStatCard(String label, String value, String emoji, Color accent) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                // left accent bar
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, 4, getHeight(), 4, 4);
                g2.fillRect(2, 0, 2, getHeight());
                // border
                g2.setColor(BORDER_COL);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(8, 0));
        card.setBorder(new EmptyBorder(14, 18, 14, 14));
        card.setPreferredSize(new Dimension(0, 74));

        JPanel iconBox = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 18));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            }
        };
        iconBox.setOpaque(false);
        iconBox.setPreferredSize(new Dimension(40, 40));

        JLabel emojiLbl = new JLabel(emoji);
        emojiLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        iconBox.add(emojiLbl);

        JPanel textBox = new JPanel();
        textBox.setOpaque(false);
        textBox.setLayout(new BoxLayout(textBox, BoxLayout.Y_AXIS));

        JLabel valLbl = new JLabel(value);
        valLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valLbl.setForeground(TEXT_DARK);

        JLabel lblLbl = new JLabel(label);
        lblLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblLbl.setForeground(TEXT_DIM);

        textBox.add(valLbl);
        textBox.add(lblLbl);

        card.add(iconBox, BorderLayout.WEST);
        card.add(textBox, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildFilterBar(JTable table, DefaultTableModel model, Runnable basePopulate, long fc, long eco) {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(BORDER_COL);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
            }
        };
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(10, 16, 10, 16));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        JLabel lblClass = makeLabel("Class:", Font.BOLD, 12, TEXT_DARK);
        String[] classOptions = {"All", "First Class", "Economy"};
        JComboBox<String> cbClass = buildStyledCombo(classOptions);

        JLabel lblSearch = makeLabel("Search:", Font.BOLD, 12, TEXT_DARK);
        JTextField tfSearch = buildStyledSearchField();

        JButton btnFilter = buildGoldButton("Filter");

        left.add(lblClass); left.add(cbClass);
        left.add(Box.createHorizontalStrut(8));
        left.add(lblSearch); left.add(tfSearch);
        left.add(btnFilter);

        Runnable doFilter = () -> {
            model.setRowCount(0);
            String selectedClass = (String) cbClass.getSelectedItem();
            String searchText    = tfSearch.getText().trim().toLowerCase();
            int[] rowNum = {1};
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            for (Reservation res : allReservations) {
                if (!"All".equals(selectedClass) &&
                    !selectedClass.equalsIgnoreCase(res.getSeatClass())) continue;
                Passenger p = allPassengers.stream()
                    .filter(x -> x.getPassengerID() == res.getPassengerID())
                    .findFirst().orElse(null);
                String fullName = p != null ? p.getFullName() : "Unknown";
                if (!searchText.isEmpty() && !fullName.toLowerCase().contains(searchText)) continue;
                Flight f  = findFlight(res.getFlightID());
                Route  rt = f != null ? findRoute(f.getRouteID()) : null;
                model.addRow(new Object[]{
                    rowNum[0]++, res.getTicketNumber(), fullName, res.getFlightID(),
                    rt != null ? rt.getOrigin()      : "N/A",
                    rt != null ? rt.getDestination() : "N/A",
                    f  != null ? f.getDepartureDateTime().format(dtf) : "N/A",
                    res.getSeatClass(), res.getStatus(), res.getReservationDate()
                });
            }
        };

        btnFilter.addActionListener(e -> doFilter.run());
        tfSearch.addActionListener(e -> doFilter.run());
        // Live search on key release
        tfSearch.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { doFilter.run(); }
        });

        // Reset button
        JButton btnReset = buildOutlineButton("Reset");
        btnReset.addActionListener(e -> {
            cbClass.setSelectedIndex(0);
            tfSearch.setText("");
            basePopulate.run();
        });
        left.add(btnReset);

        bar.add(left, BorderLayout.WEST);
        return bar;
    }

    private JTable buildStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? ROW_EVEN : ROW_ODD);
                    c.setForeground(TEXT_DARK);
                } else {
                    c.setBackground(SEL_BG);
                    c.setForeground(SEL_FG);
                }
                return c;
            }
        };

        table.setRowHeight(36);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(TEXT_DARK);
        table.setBackground(ROW_ODD);
        table.setSelectionBackground(SEL_BG);
        table.setSelectionForeground(SEL_FG);
        table.setFocusable(false);
        table.setAutoCreateRowSorter(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setForeground(TEXT_MID);
        header.setBackground(new Color(0xF7F9FD));
        header.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_COL));
        header.setPreferredSize(new Dimension(0, 36));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);


        if (table.getColumnCount() > 0) {
            table.getColumnModel().getColumn(0).setPreferredWidth(36);
            table.getColumnModel().getColumn(0).setMaxWidth(50);
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
            centerRenderer.setForeground(TEXT_DIM);
            table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        }

        // Alternating row separator lines via row painting
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(0xEEF1F7));

        return table;
    }

    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(
                JTable t, Object val, boolean sel, boolean foc, int row, int col) {
            JPanel pill = new JPanel(new GridBagLayout()) {
                String text = val != null ? val.toString() : "";
                boolean confirmed = "Confirmed".equalsIgnoreCase(text);
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    Color bg = confirmed ? new Color(0xE6F7F1) : new Color(0xFDECEB);
                    g2.setColor(bg);
                    g2.fillRoundRect(2, 4, getWidth()-4, getHeight()-8, 20, 20);
                    g2.setColor(getComponents()[0].getForeground());
                    super.paintComponent(g);
                }
            };
            pill.setOpaque(false);
            String text = val != null ? val.toString() : "";
            boolean confirmed = "Confirmed".equalsIgnoreCase(text);
            JLabel lbl = new JLabel((confirmed ? "● " : "○ ") + text);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setForeground(confirmed ? new Color(0x0F7A54) : new Color(0xB83832));
            pill.add(lbl);
            pill.setBackground(sel ? SEL_BG : (row % 2 == 0 ? ROW_EVEN : ROW_ODD));
            pill.setOpaque(true);
            return pill;
        }
    }

    private static class SeatClassCellRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(
                JTable t, Object val, boolean sel, boolean foc, int row, int col) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
            p.setOpaque(true);
            p.setBackground(sel ? SEL_BG : (row % 2 == 0 ? ROW_EVEN : ROW_ODD));
            String text = val != null ? val.toString() : "";
            boolean isFC = "First Class".equalsIgnoreCase(text);
            JLabel lbl = new JLabel(isFC ? "First Class" : "Economy");
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setForeground(isFC ? new Color(0x9A6000) : new Color(0x2060A0));
            p.add(lbl);
            return p;
        }
    }

    private static class BadgeCellRenderer extends DefaultTableCellRenderer {
        private final String a;
        BadgeCellRenderer(String a, String b) { this.a = a; }
        @Override public Component getTableCellRendererComponent(
                JTable t, Object val, boolean sel, boolean foc, int row, int col) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
            p.setOpaque(true);
            p.setBackground(sel ? SEL_BG : (row % 2 == 0 ? ROW_EVEN : ROW_ODD));
            String text = val != null ? val.toString() : "";
            boolean isA = a.equalsIgnoreCase(text);
            JLabel lbl = new JLabel(text);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setForeground(isA ? new Color(0x2060A0) : new Color(0x7A4A00));
            p.add(lbl);
            return p;
        }
    }

    private JLabel makeLabel(String text, int style, int size, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", style, size));
        lbl.setForeground(color);
        return lbl;
    }

    private JComboBox<String> buildStyledCombo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cb.setBackground(WHITE);
        cb.setForeground(TEXT_DARK);
        cb.setPreferredSize(new Dimension(130, 32));
        return cb;
    }

    private JTextField buildStyledSearchField() {
        JTextField tf = new JTextField(14) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(TEXT_DIM);
                    g2.setFont(new Font("Segoe UI", Font.ITALIC, 12));
                    g2.drawString("Search passenger name…", 8, 20);
                }
            }
        };
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COL, 1, true),
            new EmptyBorder(4, 8, 4, 8)
        ));
        tf.setPreferredSize(new Dimension(200, 32));
        return tf;
    }

    private JButton buildGoldButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? GOLD_DIM
                          : getModel().isRollover() ? new Color(0xF9D34A) : GOLD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(NAVY);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(7, 16, 7, 16));
        return btn;
    }

    private JButton buildOutlineButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(0xEDF2FB) : WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(BORDER_COL);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(TEXT_MID);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(7, 14, 7, 14));
        return btn;
    }

    private JButton buildExportButton(JTable table, String filename) {
        JButton btn = buildGoldButton("Export CSV");
        btn.addActionListener(e -> exportTable(table, filename));
        return btn;
    }

    private Airline findAirline(String id) {
        return allAirlines.stream().filter(x -> x.getAirlineID().equals(id)).findFirst().orElse(null);
    }
    private Route findRoute(String id) {
        return allRoutes.stream().filter(x -> x.getRouteID().equals(id)).findFirst().orElse(null);
    }
    private Plane findPlane(String id) {
        return allPlanes.stream().filter(x -> x.getPlaneID().equals(id)).findFirst().orElse(null);
    }
    private Flight findFlight(String id) {
        return allFlights.stream().filter(x -> x.getFlightID().equals(id)).findFirst().orElse(null);
    }

    private void exportTable(JTable table, String filename) {
        try (FileWriter fw = new FileWriter(filename)) {
            for (int i = 0; i < table.getColumnCount(); i++) {
                fw.write(table.getColumnName(i));
                if (i < table.getColumnCount() - 1) fw.write(",");
            }
            fw.write("\n");
            for (int row = 0; row < table.getRowCount(); row++) {
                for (int col = 0; col < table.getColumnCount(); col++) {
                    Object val = table.getValueAt(row, col);
                    String cell = val != null ? val.toString().replace(",", ";") : "";
                    fw.write(cell);
                    if (col < table.getColumnCount() - 1) fw.write(",");
                }
                fw.write("\n");
            }
            JOptionPane.showMessageDialog(this,
                "Exported to: " + filename, "Export Complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}