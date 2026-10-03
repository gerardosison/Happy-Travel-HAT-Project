package ui.airline_management;

import dao.*;
import model.*;
import ui.Session;
import ui.UI;
import ui.monitoring.MonitoringMenu;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.Comparator;

public class AdminDashboard extends JFrame {

    private static final Color SB_BG     = new Color(0x0D1E35);
    private static final Color SB_HOVER  = new Color(0x162D4A);
    private static final Color SB_ACTIVE = new Color(0x1A3457);
    private static final Color SB_EDGE   = new Color(0x192C44);
    private static final Color SB_TEXT   = new Color(0x6A8FAF);
    private static final Color SB_ICON   = new Color(0x3D6080);
    private static final int   SB_WIDE   = 230;
    private static final int   SB_SLIM   = 58;
    private boolean sbExpanded = true;

    private static final String[][] NAV = {
        { "dashboard", "Dashboard",        "Dashboard"  },
        { "airline",   "Register Airline", "Airlines"   },
        { "flight",    "Update Flight",    "Flights"    },
        { "seat",      "Manage Seats",     "Seats"      },
        { "price",     "Modify Prices",    "Prices"     },
        { "monitor",   "Monitoring",       "Monitoring" },
    };

    private JPanel   sidebar, contentArea;
    private JPanel[] navRows;
    private JLabel[] navIcons, navTexts;
    private int      activeTab = 0;
    private Timer    sbTimer;

    private final Map<String, JPanel> loadedPanels = new HashMap<>();

    private JLabel lblFlights, lblReservations, lblPassengers, lblRevenue;
    private JLabel lblUpcomingList, lblRecentList;
    private JProgressBar pbFirst, pbEconomy, pbTotal;
    private JLabel       pctFirst, pctEconomy, pctTotal;
    private JPanel       revenueBreakdownPanel;

    private SwingWorker<Void, Void> activeWorker;

    public AdminDashboard() {
        setTitle("Happy Travel (HAT) — Admin Dashboard");
        setSize(1280, 860);
        setMinimumSize(new Dimension(1024, 680));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UI.BG_DEEP);

        sidebar = buildSidebar();
        root.add(sidebar, BorderLayout.WEST);

        contentArea = new JPanel(new CardLayout());
        contentArea.setBackground(UI.BG_DEEP);

        JPanel dashPanel = buildDashboardPanel();
        loadedPanels.put("Dashboard", dashPanel);
        contentArea.add(dashPanel, "Dashboard");

        for (String[] nav : NAV) {
            if (!"Dashboard".equals(nav[2])) {
                contentArea.add(buildLoadingPlaceholder(nav[1]), nav[2]);
            }
        }
        root.add(contentArea, BorderLayout.CENTER);
        setContentPane(root);

        switchTab(0);
        setVisible(true);
    }

    private JPanel buildLoadingPlaceholder(String label) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(UI.BG_DEEP);
        JLabel lbl = new JLabel("Loading " + label + "...");
        lbl.setFont(UI.FONT_BODY);
        lbl.setForeground(UI.TEXT_SECONDARY);
        p.add(lbl);
        return p;
    }

    private JPanel buildOrGetPanel(String key) {
        if (loadedPanels.containsKey(key)) return loadedPanels.get(key);
        JPanel panel = switch (key) {
            case "Airlines"   -> new RegisterAirlinePanel();
            case "Flights"    -> new UpdateFlightPanel();
            case "Seats"      -> new ManageSeatsPanel();
            case "Prices"     -> new ModifyPricesPanel();
            case "Monitoring" -> new MonitoringMenu();
            default           -> buildLoadingPlaceholder(key);
        };
        loadedPanels.put(key, panel);
        contentArea.add(panel, key);
        return panel;
    }

    private JPanel buildDashboardPanel() {
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(UI.BG_DEEP);
        page.setBorder(new EmptyBorder(24, 28, 20, 28));

        page.add(pageHeading("Dashboard",
                "Live overview of Happy Travel (HAT) operations", "dashboard"),
                BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(UI.BG_DEEP);

        body.add(buildStatRow());
        body.add(vgap(18));
        body.add(buildMidRow());
        body.add(vgap(18));
        body.add(buildBottomRow());
        body.add(vgap(10));

        JScrollPane sp = new JScrollPane(body);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(UI.BG_DEEP);
        sp.setBackground(UI.BG_DEEP);
        UI.styleScrollPane(sp);
        page.add(sp, BorderLayout.CENTER);

        return page;
    }

    private JPanel buildStatRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 14, 0));
        row.setBackground(UI.BG_DEEP);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        lblFlights      = new JLabel("—", SwingConstants.LEFT);
        lblReservations = new JLabel("—", SwingConstants.LEFT);
        lblPassengers   = new JLabel("—", SwingConstants.LEFT);
        lblRevenue      = new JLabel("—", SwingConstants.LEFT);

        row.add(statCard("Total Flights",       lblFlights,      "flight",      new Color(0x1A56DB)));
        row.add(statCard("Active Reservations", lblReservations, "reservation", new Color(0x047857)));
        row.add(statCard("Passengers",          lblPassengers,   "passenger",   new Color(0xF59E0B)));
        row.add(statCard("Revenue (Confirmed)", lblRevenue,      "money",       new Color(0x6D28D9)));
        return row;
    }

    private JPanel statCard(String label, JLabel valueLabel, String iconType, Color accent) {
        JPanel card = roundedCard(UI.BG_PANEL, accent, 3);
        card.setLayout(new BorderLayout(0, 4));
        card.setBorder(new EmptyBorder(14, 16, 14, 16));

        JPanel iconBox = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 25));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(accent);
                paintIcon(g2, iconType, 4, 4, getWidth()-8, getHeight()-8);
                g2.dispose();
            }
        };
        iconBox.setPreferredSize(new Dimension(32, 32));
        iconBox.setOpaque(false);
        card.add(iconBox, BorderLayout.NORTH);

        valueLabel.setFont(UI.FONT_TITLE.deriveFont(28f));
        valueLabel.setForeground(accent);
        card.add(valueLabel, BorderLayout.CENTER);

        JLabel lbl = new JLabel(label);
        lbl.setFont(UI.FONT_LABEL);
        lbl.setForeground(UI.TEXT_SECONDARY);
        card.add(lbl, BorderLayout.SOUTH);

        return card;
    }

    private JPanel buildMidRow() {
        JPanel row = new JPanel(new GridLayout(1, 2, 14, 0));
        row.setBackground(UI.BG_DEEP);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));

        JPanel upCard = roundedCard(UI.BG_PANEL, null, 0);
        upCard.setLayout(new BorderLayout());
        upCard.setBorder(new EmptyBorder(16, 18, 14, 18));
        upCard.add(sectionHeader("Upcoming Flights", "flight"), BorderLayout.NORTH);
        lblUpcomingList = new JLabel("<html>Loading...</html>");
        lblUpcomingList.setFont(UI.FONT_SMALL);
        lblUpcomingList.setForeground(UI.TEXT_SECONDARY);
        lblUpcomingList.setVerticalAlignment(SwingConstants.TOP);
        lblUpcomingList.setBorder(new EmptyBorder(10, 0, 0, 0));
        upCard.add(lblUpcomingList, BorderLayout.CENTER);
        row.add(upCard);

        JPanel rcCard = roundedCard(UI.BG_PANEL, null, 0);
        rcCard.setLayout(new BorderLayout());
        rcCard.setBorder(new EmptyBorder(16, 18, 14, 18));
        rcCard.add(sectionHeader("Recent Confirmed Reservations", "reservation"), BorderLayout.NORTH);
        lblRecentList = new JLabel("<html>Loading...</html>");
        lblRecentList.setFont(UI.FONT_SMALL);
        lblRecentList.setForeground(UI.TEXT_SECONDARY);
        lblRecentList.setVerticalAlignment(SwingConstants.TOP);
        lblRecentList.setBorder(new EmptyBorder(10, 0, 0, 0));
        rcCard.add(lblRecentList, BorderLayout.CENTER);
        row.add(rcCard);

        return row;
    }

    private JPanel buildBottomRow() {
        JPanel row = new JPanel(new GridLayout(1, 2, 14, 0));
        row.setBackground(UI.BG_DEEP);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 240));

        JPanel occCard = roundedCard(UI.BG_PANEL, null, 0);
        occCard.setLayout(new BorderLayout());
        occCard.setBorder(new EmptyBorder(16, 18, 16, 18));
        occCard.add(sectionHeader("Seat Class Occupancy", "seat"), BorderLayout.NORTH);

        JPanel bars = new JPanel();
        bars.setLayout(new BoxLayout(bars, BoxLayout.Y_AXIS));
        bars.setBackground(UI.BG_PANEL);
        bars.setBorder(new EmptyBorder(14, 0, 0, 0));

        pbFirst   = makeBar(new Color(0x8B5CF6));
        pbEconomy = makeBar(new Color(0x3B82F6));
        pbTotal   = makeBar(new Color(0x10B981));
        pctFirst   = pctLabel("—");
        pctEconomy = pctLabel("—");
        pctTotal   = pctLabel("—");

        bars.add(barRow("First Class", pbFirst,   pctFirst));
        bars.add(vgap(10));
        bars.add(barRow("Economy",     pbEconomy, pctEconomy));
        bars.add(vgap(10));
        bars.add(barRow("Overall",     pbTotal,   pctTotal));
        occCard.add(bars, BorderLayout.CENTER);
        row.add(occCard);

        revenueBreakdownPanel = roundedCard(UI.BG_PANEL, null, 0);
        revenueBreakdownPanel.setLayout(new BorderLayout());
        revenueBreakdownPanel.setBorder(new EmptyBorder(16, 18, 16, 18));
        revenueBreakdownPanel.add(sectionHeader("Revenue Breakdown", "money"), BorderLayout.NORTH);
        row.add(revenueBreakdownPanel);

        return row;
    }

    private void refreshDashboardData() {
        // Cancel any in-flight worker before starting a new one
        if (activeWorker != null && !activeWorker.isDone()) {
            activeWorker.cancel(false);
        }

        lblFlights.setText("…");
        lblReservations.setText("…");
        lblPassengers.setText("…");
        lblRevenue.setText("…");
        lblUpcomingList.setText("<html><font color='#8AA0BC'>Loading...</font></html>");
        lblRecentList.setText("<html><font color='#8AA0BC'>Loading...</font></html>");

        activeWorker = new SwingWorker<>() {
            List<Flight>         flights;
            List<Reservation>    reservations;
            List<Passenger>      passengers;
            List<Seat>           seats;
            List<Route>          routes;
            List<Airline>        airlines;
            List<Plane>          planes;
            String               loadError;

            @Override
            protected Void doInBackground() {
                try { flights      = new FlightDAO().getAll();      } catch (Exception e) { logErr("FlightDAO",      e); }
                try { reservations = new ReservationDAO().getAll(); } catch (Exception e) { logErr("ReservationDAO", e); }
                try { passengers   = new PassengerDAO().getAll();   } catch (Exception e) { logErr("PassengerDAO",   e); }
                try { seats        = new SeatDAO().getAll();         } catch (Exception e) { logErr("SeatDAO",        e); }
                try { routes       = new RouteDAO().getAll();        } catch (Exception e) { logErr("RouteDAO",       e); }
                try { airlines     = new AirlineDAO().getAll();      } catch (Exception e) { logErr("AirlineDAO",     e); }
                try { planes       = new PlaneDAO().getAll();        } catch (Exception e) { logErr("PlaneDAO",       e); }
                return null;
            }

            private void logErr(String dao, Exception e) {
                System.err.println("[Dashboard] " + dao + " failed: " + e.getMessage());
                if (loadError == null) loadError = dao;
            }

            @Override
            protected void done() {
                if (isCancelled()) return;

                // Null-safe fallbacks so partial data still renders
                if (flights      == null) flights      = List.of();
                if (reservations == null) reservations = List.of();
                if (passengers   == null) passengers   = List.of();
                if (seats        == null) seats        = List.of();
                if (routes       == null) routes       = List.of();
                if (airlines     == null) airlines     = List.of();
                if (planes       == null) planes       = List.of();

                // Stat cards
                lblFlights.setText(String.valueOf(flights.size()));

                long activeRsv = reservations.stream()
                    .filter(r -> "Confirmed".equalsIgnoreCase(r.getStatus())
                              || "Pending".equalsIgnoreCase(r.getStatus()))
                    .count();
                lblReservations.setText(String.valueOf(activeRsv));
                lblPassengers.setText(String.valueOf(passengers.size()));

                BigDecimal revenue = BigDecimal.ZERO;
                for (Reservation r : reservations) {
                    if (!"Confirmed".equalsIgnoreCase(r.getStatus())) continue;
                    for (Seat s : seats) {
                        if (s.getFlightID().equals(r.getFlightID())
                                && s.getSeatClass().equalsIgnoreCase(r.getSeatClass())
                                && s.getPrice() != null) {
                            revenue = revenue.add(s.getPrice());
                            break;
                        }
                    }
                }
                lblRevenue.setText("P" + String.format("%,.0f", revenue));

                refreshUpcomingFlights(flights, airlines, routes);
                refreshRecentReservations(reservations, passengers, seats);
                refreshSeatOccupancy(seats, flights, planes);
                refreshRevenueBreakdown(reservations, seats, flights, airlines);

                if (loadError != null) {
                    String warn = "<html><font color='#F59E0B'>⚠ Some data unavailable (" + loadError + ")</font></html>";
                    JOptionPane.showMessageDialog(AdminDashboard.this, warn,
                        "Partial Load Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
        };
        activeWorker.execute();
    }

    private void refreshUpcomingFlights(List<Flight> flights, List<Airline> airlines, List<Route> routes) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd  HH:mm");
        StringBuilder up = new StringBuilder("<html><table cellspacing='0' cellpadding='3'>");

        List<Flight> upcoming = flights.stream()
            .filter(f -> f.getDepartureDateTime() != null
                      && f.getDepartureDateTime().isAfter(LocalDateTime.now()))
            .sorted(Comparator.comparing(Flight::getDepartureDateTime))
            .limit(7)
            .collect(Collectors.toList());

        if (upcoming.isEmpty()) {
            up.append("<tr><td><font color='#8AA0BC'>No upcoming flights.</font></td></tr>");
        } else {
            for (Flight f : upcoming) {
                String airName = airlines.stream()
                    .filter(a -> a.getAirlineID().equals(f.getAirlineID()))
                    .map(Airline::getAirlineName).findFirst().orElse(f.getAirlineID());
                Route rt = routes.stream()
                    .filter(r -> r.getRouteID().equals(f.getRouteID()))
                    .findFirst().orElse(null);
                String origin = rt != null ? rt.getOrigin()      : "—";
                String dest   = rt != null ? rt.getDestination() : "—";
                boolean boarding   = f.getDepartureDateTime().isBefore(LocalDateTime.now().plusHours(2));
                String statusColor = boarding ? "#F59E0B" : "#10B981";
                String statusText  = boarding ? "Boarding" : "Scheduled";

                up.append(String.format(
                    "<tr><td width='50'><b><font color='#C8D8E8'>%s</font></b></td>" +
                    "<td width='180'>%s</td>" +
                    "<td width='130'><font color='#A8C0D8'>%s → %s</font></td>" +
                    "<td width='120'><font color='#8AA0BC'>%s</font></td>" +
                    "<td><font color='%s'><b>%s</b></font></td></tr>",
                    f.getFlightID(), airName, origin, dest,
                    f.getDepartureDateTime().format(dtf), statusColor, statusText));
            }
        }
        up.append("</table></html>");
        lblUpcomingList.setText(up.toString());
    }

    private void refreshRecentReservations(List<Reservation> reservations,
                                            List<Passenger> passengers, List<Seat> seats) {
        StringBuilder rc = new StringBuilder("<html><table cellspacing='0' cellpadding='3'>");

        List<Reservation> confirmed = reservations.stream()
            .filter(r -> "Confirmed".equalsIgnoreCase(r.getStatus()))
            .sorted((a, b) -> {
                try { return b.getReservationDate().compareTo(a.getReservationDate()); }
                catch (Exception ex) { return 0; }
            })
            .limit(7)
            .collect(Collectors.toList());

        if (confirmed.isEmpty()) {
            rc.append("<tr><td><font color='#8AA0BC'>No confirmed reservations.</font></td></tr>");
        } else {
            for (Reservation r : confirmed) {
                Passenger p = passengers.stream()
                    .filter(x -> x.getPassengerID() == r.getPassengerID())
                    .findFirst().orElse(null);
                String name = (p != null) ? p.getFirstName() + " " + p.getLastName() : "Unknown";
                String priceStr = seats.stream()
                    .filter(s -> s.getFlightID().equals(r.getFlightID())
                              && s.getSeatClass().equalsIgnoreCase(r.getSeatClass())
                              && s.getPrice() != null)
                    .map(s -> String.format("%,.0f", s.getPrice()))
                    .findFirst().orElse("—");
                boolean isFirst  = "First Class".equalsIgnoreCase(r.getSeatClass());
                String classColor = isFirst ? "#F59E0B" : "#3B82F6";
                String classShort = isFirst ? "FC"       : "EC";

                rc.append(String.format(
                    "<tr><td width='140'><b><font color='#C8D8E8'>%s</font></b></td>" +
                    "<td width='60'><font color='%s'>[%s]</font></td>" +
                    "<td width='60'><font color='#8AA0BC'>%s</font></td>" +
                    "<td><font color='#10B981'><b>P%s</b></font></td></tr>",
                    name, classColor, classShort, r.getFlightID(), priceStr));
            }
        }
        rc.append("</table></html>");
        lblRecentList.setText(rc.toString());
    }

    private void refreshSeatOccupancy(List<Seat> seats, List<Flight> flights, List<Plane> planes) {
        int fcCap = 0, fcAvail = 0, ecCap = 0, ecAvail = 0;
        for (Seat s : seats) {
            Flight f = flights.stream()
                .filter(fl -> fl.getFlightID().equals(s.getFlightID()))
                .findFirst().orElse(null);
            if (f == null) continue;
            Plane plane = planes.stream()
                .filter(pl -> pl.getPlaneID().equals(f.getPlaneID()))
                .findFirst().orElse(null);
            if (plane == null) continue;
            boolean isFC = s.getSeatClass().toLowerCase().contains("first");
            if (isFC) { fcCap += plane.getFirstClassSeats(); fcAvail += s.getAvailableSeats(); }
            else      { ecCap += plane.getEconomySeats();    ecAvail += s.getAvailableSeats(); }
        }
        int fcBooked  = Math.max(0, fcCap  - fcAvail);
        int ecBooked  = Math.max(0, ecCap  - ecAvail);
        int totCap    = fcCap + ecCap;
        int totBooked = fcBooked + ecBooked;

        int fcPct  = fcCap  > 0 ? (int)(fcBooked  * 100.0 / fcCap)  : 0;
        int ecPct  = ecCap  > 0 ? (int)(ecBooked  * 100.0 / ecCap)  : 0;
        int totPct = totCap > 0 ? (int)(totBooked * 100.0 / totCap) : 0;

        pbFirst.setValue(Math.min(100, fcPct));
        pbEconomy.setValue(Math.min(100, ecPct));
        pbTotal.setValue(Math.min(100, totPct));
        pctFirst.setText(fcCap  > 0 ? fcPct  + "%" : "N/A");
        pctEconomy.setText(ecCap > 0 ? ecPct  + "%" : "N/A");
        pctTotal.setText(totCap  > 0 ? totPct + "%" : "N/A");
    }

    private void refreshRevenueBreakdown(List<Reservation> reservations, List<Seat> seats,
                                          List<Flight> flights, List<Airline> airlines) {
        revenueBreakdownPanel.removeAll();
        revenueBreakdownPanel.add(sectionHeader("Revenue Breakdown", "money"), BorderLayout.NORTH);

        Map<String, BigDecimal> airlineRevenue = new HashMap<>();
        BigDecimal totalRev = BigDecimal.ZERO;
        for (Reservation r : reservations) {
            if (!"Confirmed".equalsIgnoreCase(r.getStatus())) continue;
            Flight f = flights.stream()
                .filter(fl -> fl.getFlightID().equals(r.getFlightID()))
                .findFirst().orElse(null);
            if (f == null) continue;
            BigDecimal price = seats.stream()
                .filter(s -> s.getFlightID().equals(r.getFlightID())
                          && s.getSeatClass().equalsIgnoreCase(r.getSeatClass())
                          && s.getPrice() != null)
                .map(Seat::getPrice).findFirst().orElse(BigDecimal.ZERO);
            airlineRevenue.merge(f.getAirlineID(), price, BigDecimal::add);
            totalRev = totalRev.add(price);
        }

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(UI.BG_PANEL);
        content.setBorder(new EmptyBorder(14, 0, 0, 0));

        if (totalRev.compareTo(BigDecimal.ZERO) == 0) {
            JLabel empty = new JLabel("No revenue data available.");
            empty.setFont(UI.FONT_SMALL);
            empty.setForeground(UI.TEXT_MUTED);
            content.add(empty);
        } else {
            final BigDecimal finalTotal = totalRev;
            airlineRevenue.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(5)
                .forEach(entry -> {
                    String airName = airlines.stream()
                        .filter(a -> a.getAirlineID().equals(entry.getKey()))
                        .map(Airline::getAirlineName).findFirst().orElse(entry.getKey());
                    BigDecimal rev = entry.getValue();
                    int pct = (int)(rev.doubleValue() * 100.0 / finalTotal.doubleValue());

                    JPanel row = new JPanel(new BorderLayout(10, 0));
                    row.setBackground(UI.BG_PANEL);
                    row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

                    JLabel nameLbl = new JLabel(airName);
                    nameLbl.setFont(UI.FONT_SMALL);
                    nameLbl.setForeground(UI.TEXT_PRIMARY);
                    nameLbl.setPreferredSize(new Dimension(140, 20));

                    JProgressBar bar = new JProgressBar(0, 100);
                    bar.setValue(pct);
                    bar.setStringPainted(false);
                    bar.setPreferredSize(new Dimension(0, 7));
                    bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 7));
                    bar.setBorder(BorderFactory.createEmptyBorder());
                    bar.setBackground(new Color(0xE2E8F0));
                    bar.setForeground(new Color(0xF59E0B));
                    bar.setOpaque(false);
                    bar.setBorderPainted(false);

                    JLabel pctLbl = new JLabel(String.format("P%,.0f (%d%%)", rev, pct), SwingConstants.RIGHT);
                    pctLbl.setFont(UI.FONT_SMALL);
                    pctLbl.setForeground(UI.TEXT_SECONDARY);
                    pctLbl.setPreferredSize(new Dimension(100, 20));

                    row.add(nameLbl, BorderLayout.WEST);
                    row.add(bar,     BorderLayout.CENTER);
                    row.add(pctLbl,  BorderLayout.EAST);
                    content.add(row);
                    content.add(vgap(6));
                });
        }
        revenueBreakdownPanel.add(content, BorderLayout.CENTER);
        revenueBreakdownPanel.revalidate();
        revenueBreakdownPanel.repaint();
    }

    private JPanel buildSidebar() {
        JPanel sb = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(SB_BG);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(SB_EDGE);
                g.fillRect(getWidth() - 1, 0, 1, getHeight());
            }
        };
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setPreferredSize(new Dimension(SB_WIDE, 0));
        sb.setOpaque(false);

        sb.add(buildBrand());
        sb.add(hRule());

        navRows  = new JPanel[NAV.length];
        navIcons = new JLabel[NAV.length];
        navTexts = new JLabel[NAV.length];

        for (int i = 0; i < NAV.length; i++) {
            navRows[i] = buildNavRow(i);
            sb.add(navRows[i]);
        }

        sb.add(Box.createVerticalGlue());
        sb.add(hRule());
        sb.add(buildUserBlock());
        return sb;
    }

    private JPanel buildBrand() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 16));
        left.setOpaque(false);

        JPanel logo = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UI.YELLOW);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(0x08111F));
                g2.setFont(UI.FONT_BOLD.deriveFont(13f));
                FontMetrics fm = g2.getFontMetrics();
                String t = "HAT";
                g2.drawString(t, (getWidth() - fm.stringWidth(t)) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        logo.setPreferredSize(new Dimension(38, 38));
        logo.setOpaque(false);

        JPanel txt = new JPanel();
        txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
        JLabel name = new JLabel("Happy Travel");
        name.setFont(UI.FONT_BOLD.deriveFont(14f));
        name.setForeground(UI.YELLOW);
        JLabel sub = new JLabel("Admin Panel");
        sub.setFont(UI.FONT_SMALL);
        sub.setForeground(SB_ICON);
        txt.add(name);
        txt.add(sub);

        left.add(logo);
        left.add(txt);
        p.add(left, BorderLayout.CENTER);

        JButton ham = buildHamburger();
        ham.addActionListener(e -> animateSidebar());
        JPanel hamWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 17));
        hamWrap.setOpaque(false);
        hamWrap.add(ham);
        p.add(hamWrap, BorderLayout.EAST);

        return p;
    }

    private JButton buildHamburger() {
        JButton b = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(SB_HOVER);
                    g2.fillRoundRect(2, 2, getWidth()-4, getHeight()-4, 6, 6);
                }
                g2.setColor(SB_ICON);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int cx = getWidth()/2, cy = getHeight()/2;
                g2.drawLine(cx-7, cy-5, cx+7, cy-5);
                g2.drawLine(cx-7, cy,   cx+7, cy);
                g2.drawLine(cx-7, cy+5, cx+7, cy+5);
                g2.dispose();
            }
        };
        b.setPreferredSize(new Dimension(34, 34));
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JPanel buildNavRow(int idx) {
        final int i = idx;
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean act = (activeTab == i);
                Point mp = getMousePosition();
                boolean hov = mp != null;
                int pad = 8;
                if (act) {
                    g2.setColor(SB_ACTIVE);
                    g2.fillRoundRect(pad, 3, getWidth()-pad*2, getHeight()-6, 10, 10);
                    // Active accent bar on left
                    g2.setColor(UI.YELLOW);
                    g2.fillRoundRect(pad, 3, 4, getHeight()-6, 4, 4);
                } else if (hov) {
                    g2.setColor(SB_HOVER);
                    g2.fillRoundRect(pad, 3, getWidth()-pad*2, getHeight()-6, 10, 10);
                }
            }
        };
        row.setOpaque(false);
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Increased row height from 46 → 52 to give icons breathing room
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        row.setPreferredSize(new Dimension(SB_WIDE, 52));

        boolean act = (idx == activeTab);

        JLabel iconLbl = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color iconColor = (activeTab == i) ? UI.YELLOW : SB_ICON;
                // Subtle icon background pill
                g2.setColor(new Color(iconColor.getRed(), iconColor.getGreen(), iconColor.getBlue(), 28));
                g2.fillRoundRect(6, 8, getWidth()-12, getHeight()-16, 8, 8);
                // Draw icon centred in a 22×22 area
                int ix = (getWidth() - 22) / 2;
                int iy = (getHeight() - 22) / 2;
                g2.setColor(iconColor);
                paintIcon(g2, NAV[idx][0], ix, iy, 22, 22);
                g2.dispose();
            }
        };
        iconLbl.setPreferredSize(new Dimension(46, 52));
        iconLbl.setOpaque(false);
        navIcons[idx] = iconLbl;

        JLabel textLbl = new JLabel(NAV[idx][1]);
        textLbl.setFont(act ? UI.FONT_BOLD.deriveFont(12f) : UI.FONT_BODY.deriveFont(12f));
        textLbl.setForeground(act ? UI.YELLOW : SB_TEXT);
        textLbl.setBorder(new EmptyBorder(0, 4, 0, 0));
        navTexts[idx] = textLbl;

        row.add(iconLbl);
        row.add(textLbl);

        row.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { switchTab(i); }
            public void mouseEntered(MouseEvent e) { row.repaint(); }
            public void mouseExited (MouseEvent e) { row.repaint(); }
        });
        return row;
    }

    private JPanel buildUserBlock() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        p.setBorder(new EmptyBorder(8, 16, 10, 12));

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        String displayName = "";
        try { displayName = Session.getInstance().getDisplayName(); } catch (Exception ignored) {}
        if (displayName == null || displayName.isBlank()) displayName = "Administrator";

        JLabel nameL = new JLabel(displayName);
        nameL.setFont(UI.FONT_BOLD.deriveFont(12f));
        nameL.setForeground(new Color(0x8AAAC8));

        JLabel roleL = new JLabel("Administrator");
        roleL.setFont(UI.FONT_SMALL);
        roleL.setForeground(SB_ICON);

        info.add(nameL);
        info.add(roleL);
        p.add(info, BorderLayout.CENTER);

        JButton signOut = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color ic = getModel().isRollover() ? UI.DANGER : SB_ICON;
                if (getModel().isRollover()) {
                    g2.setColor(new Color(UI.DANGER.getRed(), UI.DANGER.getGreen(),
                                UI.DANGER.getBlue(), 40));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }
                g2.setColor(ic);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int cx = getWidth()/2, cy = getHeight()/2;
                g2.drawLine(cx-6, cy, cx+5, cy);
                g2.drawLine(cx+2, cy-4, cx+6, cy);
                g2.drawLine(cx+2, cy+4, cx+6, cy);
                g2.drawRect(cx-7, cy-5, 6, 10);
                g2.dispose();
            }
        };
        signOut.setToolTipText("Sign Out");
        signOut.setContentAreaFilled(false);
        signOut.setBorderPainted(false);
        signOut.setFocusPainted(false);
        signOut.setPreferredSize(new Dimension(32, 32));
        signOut.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signOut.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to sign out?",
                "Confirm Sign Out",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                Session.getInstance().logout();
                new ui.LoginModule();
                dispose();
            }
        });

        JPanel rightWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        rightWrap.setOpaque(false);
        rightWrap.add(signOut);
        p.add(rightWrap, BorderLayout.EAST);

        return p;
    }

    private void animateSidebar() {
        sbExpanded = !sbExpanded;
        int target = sbExpanded ? SB_WIDE : SB_SLIM;
        if (sbTimer != null && sbTimer.isRunning()) sbTimer.stop();
        sbTimer = new Timer(10, null);
        sbTimer.addActionListener(e -> {
            int cur  = sidebar.getPreferredSize().width;
            int diff = target - cur;
            int step = diff / 4;
            if (Math.abs(step) < 2) step = Integer.signum(diff) * 2;
            int next = cur + step;
            if (sbExpanded ? next >= target : next <= target) { next = target; sbTimer.stop(); }
            sidebar.setPreferredSize(new Dimension(next, 0));
            boolean showText = next > SB_SLIM + 40;
            for (int i = 0; i < NAV.length; i++) {
                if (navTexts[i] != null) navTexts[i].setVisible(showText);
            }
            sidebar.revalidate();
            getContentPane().revalidate();
        });
        sbTimer.start();
    }

    void switchTab(int idx) {
        activeTab = idx;
        for (int i = 0; i < NAV.length; i++) {
            boolean act = (i == idx);
            if (navIcons[i] != null) navIcons[i].repaint();
            if (navTexts[i] != null) {
                navTexts[i].setFont(act ? UI.FONT_BOLD.deriveFont(12f) : UI.FONT_BODY.deriveFont(12f));
                navTexts[i].setForeground(act ? UI.YELLOW : SB_TEXT);
            }
            if (navRows[i] != null) navRows[i].repaint();
        }

        String key = NAV[idx][2];
        if (!"Dashboard".equals(key) && !loadedPanels.containsKey(key)) {
            buildOrGetPanel(key);
        }
        ((CardLayout) contentArea.getLayout()).show(contentArea, key);

        if (idx == 0) refreshDashboardData();
    }

    private static void paintIcon(Graphics2D g2, String type, int x, int y, int w, int h) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int cx = x + w/2, cy = y + h/2;
        int r = Math.min(w, h) / 2 - 2;

        switch (type) {
            case "dashboard":
                int s = r - 1;
                g2.drawRect(cx-s, cy-s, s, s);
                g2.drawRect(cx+1, cy-s, s, s);
                g2.drawRect(cx-s, cy+1, s, s);
                g2.drawRect(cx+1, cy+1, s, s);
                break;
            case "airline":
                g2.drawLine(cx-6, cy+2, cx+6, cy-2);
                g2.drawLine(cx-2, cy-4, cx+2, cy+4);
                g2.drawLine(cx-4, cy-1, cx-1, cy+1);
                g2.drawLine(cx+1, cy-1, cx+4, cy+1);
                break;
            case "flight":
                g2.drawRect(cx-r+2, cy-r+3, r*2-4, r*2-5);
                g2.drawLine(cx-r+2, cy-1, cx+r-2, cy-1);
                g2.drawLine(cx-1, cy-1, cx-1, cy+3);
                g2.drawLine(cx-1, cy+3, cx+2, cy+3);
                break;
            case "seat":
                g2.drawRect(cx-4, cy-5, 8, 6);
                g2.drawLine(cx-4, cy+1, cx-6, cy+5);
                g2.drawLine(cx+4, cy+1, cx+6, cy+5);
                g2.drawLine(cx-6, cy+5, cx+6, cy+5);
                break;
            case "price":
                g2.drawPolygon(new int[]{cx-5,cx+3,cx+5,cx-3}, new int[]{cy-5,cy-5,cy+3,cy+3}, 4);
                g2.drawLine(cx+5, cy-5, cx+5, cy-2);
                g2.fillOval(cx+3, cy-4, 2, 2);
                break;
            case "monitor":
                g2.drawRect(cx-6, cy-5, 12, 10);
                g2.drawLine(cx-4, cy+2, cx-1, cy-1);
                g2.drawLine(cx-1, cy-1, cx+2, cy+1);
                g2.drawLine(cx+2, cy+1, cx+4, cy-2);
                break;
            case "passenger":
                g2.drawOval(cx-3, cy-6, 6, 6);
                g2.drawArc(cx-5, cy, 10, 8, 0, 180);
                break;
            case "reservation":
                g2.drawRect(cx-5, cy-5, 10, 10);
                g2.drawLine(cx-2, cy-5, cx-2, cy+5);
                g2.drawLine(cx-2, cy-2, cx+3, cy-2);
                g2.drawLine(cx-2, cy+1, cx+3, cy+1);
                break;
            case "money":
                g2.drawOval(cx-4, cy-5, 8, 10);
                g2.drawLine(cx, cy-4, cx, cy+4);
                g2.drawLine(cx-2, cy-2, cx+2, cy-2);
                g2.drawLine(cx-2, cy+2, cx+2, cy+2);
                break;
            default:
                g2.drawOval(cx-r, cy-r, r*2, r*2);
                break;
        }
    }

    private JPanel roundedCard(Color bg, Color accent, int stripeH) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                if (accent != null && stripeH > 0) {
                    g2.setColor(accent);
                    g2.fillRoundRect(0, 0, getWidth()-1, stripeH+4, 12, 12);
                    g2.fillRect(0, stripeH, getWidth()-1, 4);
                }
                g2.setColor(UI.BORDER_CARD);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        return card;
    }

    private JPanel sectionHeader(String title, String iconType) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        p.setBackground(UI.BG_PANEL);

        JLabel iconLbl = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(UI.YELLOW.getRed(), UI.YELLOW.getGreen(), UI.YELLOW.getBlue(), 35));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(UI.YELLOW);
                paintIcon(g2, iconType, 4, 4, getWidth()-8, getHeight()-8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconLbl.setPreferredSize(new Dimension(26, 20));
        iconLbl.setOpaque(false);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UI.FONT_BOLD);
        titleLbl.setForeground(UI.TEXT_PRIMARY);

        p.add(iconLbl);
        p.add(titleLbl);
        return p;
    }

    private JPanel pageHeading(String title, String subtitle, String iconType) {
        JPanel p = new JPanel(new BorderLayout(14, 0));
        p.setBackground(UI.BG_DEEP);
        p.setBorder(new EmptyBorder(0, 0, 18, 0));

        JPanel badgeBox = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SB_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(UI.YELLOW);
                paintIcon(g2, iconType, 8, 8, getWidth()-16, getHeight()-16);
            }
        };
        badgeBox.setPreferredSize(new Dimension(48, 48));
        badgeBox.setOpaque(false);

        JPanel txt = new JPanel(new BorderLayout(0, 3));
        txt.setBackground(UI.BG_DEEP);
        JLabel tl = new JLabel(title);
        tl.setFont(UI.FONT_TITLE);
        tl.setForeground(UI.TEXT_PRIMARY);
        JLabel sl = new JLabel(subtitle);
        sl.setFont(UI.FONT_BODY);
        sl.setForeground(UI.TEXT_SECONDARY);
        txt.add(tl, BorderLayout.NORTH);
        txt.add(sl, BorderLayout.SOUTH);

        p.add(badgeBox, BorderLayout.WEST);
        p.add(txt, BorderLayout.CENTER);
        return p;
    }

    private JProgressBar makeBar(Color color) {
        JProgressBar pb = new JProgressBar(0, 100);
        pb.setValue(0);
        pb.setStringPainted(false);
        pb.setPreferredSize(new Dimension(0, 9));
        pb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 9));
        pb.setBorder(BorderFactory.createEmptyBorder());
        pb.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 35));
        pb.setForeground(color);
        pb.setOpaque(false);
        pb.setBorderPainted(false);
        return pb;
    }

    private JLabel pctLabel(String text) {
        JLabel l = new JLabel(text, SwingConstants.RIGHT);
        l.setFont(UI.FONT_SMALL);
        l.setForeground(UI.TEXT_SECONDARY);
        l.setPreferredSize(new Dimension(40, 20));
        return l;
    }

    private JPanel barRow(String label, JProgressBar bar, JLabel pct) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(UI.BG_PANEL);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        JLabel l = new JLabel(label);
        l.setFont(UI.FONT_SMALL);
        l.setForeground(UI.TEXT_SECONDARY);
        l.setPreferredSize(new Dimension(80, 20));
        row.add(l,   BorderLayout.WEST);
        row.add(bar, BorderLayout.CENTER);
        row.add(pct, BorderLayout.EAST);
        return row;
    }

    private Component vgap(int h) {
        JPanel g = new JPanel();
        g.setBackground(UI.BG_DEEP);
        g.setMaximumSize(new Dimension(Integer.MAX_VALUE, h));
        g.setPreferredSize(new Dimension(0, h));
        return g;
    }

    private JPanel hRule() {
        JPanel r = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(SB_EDGE);
                g.fillRect(12, 0, getWidth()-24, 1);
            }
        };
        r.setOpaque(false);
        r.setMaximumSize(new Dimension(Integer.MAX_VALUE, 8));
        r.setPreferredSize(new Dimension(0, 8));
        return r;
    }
}