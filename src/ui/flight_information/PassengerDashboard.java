package ui.flight_information;

import dao.*;
import model.*;
import ui.reservation.*;
import ui.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class PassengerDashboard extends JFrame {

    private JTable tblReservations;
    private DefaultTableModel resModel;
    private JPanel cardListPanel;
    private JTextField txtOrigin, txtDest, txtDate;
    private JLabel lblWelcome;

    public PassengerDashboard() {
        setTitle("Happy Travel Passenger Dashboard");
        setSize(1280, 720);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UI.BG_DEEP);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(UI.BG_DARK_DEEP);
        main.setBorder(new EmptyBorder(20, 30, 20, 30));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UI.BG_DARK_DEEP);
        header.setBorder(new EmptyBorder(0, 0, 18, 0));

        JPanel logoArea = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logoArea.setBackground(UI.BG_DARK_DEEP);

        JLabel logo = new JLabel("Happy Travel");
        logo.setFont(UI.FONT_TITLE.deriveFont(26f));
        logo.setForeground(UI.WHITE);
        logoArea.add(logo);

        String name = Session.getInstance().getDisplayName();
        lblWelcome = new JLabel("Welcome back, " + name);
        lblWelcome.setFont(UI.FONT_BODY);
        lblWelcome.setForeground(UI.TEXT_MUTED);

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        headerRight.setBackground(UI.BG_DARK_DEEP);

        JButton btnLogout = UI.goldButton("Log out");
        btnLogout.addActionListener(e -> {
            Session.getInstance().logout();
            new LoginModule();
            dispose();
        });

        headerRight.add(lblWelcome);
        headerRight.add(btnLogout);

        header.add(logoArea, BorderLayout.WEST);
        header.add(headerRight, BorderLayout.EAST);
        main.add(header, BorderLayout.NORTH);

        JTabbedPane tabs = buildModernTabbedPane();
        tabs.addTab(" Search Flights ", buildSearchPanel());
        tabs.addTab(" My Reservations ", buildReservationsPanel());

        main.add(tabs, BorderLayout.CENTER);
        add(main);

        doSearch();
        loadReservations();
        setVisible(true);
    }

    private JTabbedPane buildModernTabbedPane() {
        JTabbedPane tabs = new JTabbedPane() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UI.BG_PANEL);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        tabs.setOpaque(false);
        tabs.setFont(UI.FONT_SUBTITLE);
        tabs.setForeground(Color.DARK_GRAY);
        tabs.setBackground(UI.BG_PANEL);

        tabs.setUI(new BasicTabbedPaneUI() {
            @Override
            protected void installDefaults() {
                super.installDefaults();
                highlight = UI.BG_PANEL;
                lightHighlight = UI.BG_PANEL;
                shadow = UI.BG_PANEL;
                darkShadow = UI.BG_PANEL;
                focus = UI.BG_PANEL;
            }

            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isSelected) {
                    GradientPaint gp = new GradientPaint(x, y, UI.YELLOW, x + w, y, UI.YELLOW_HOVER);
                    g2.setPaint(gp);
                } else {
                    g2.setColor(new Color(0xEEF4FF));
                }
                g2.fillRoundRect(x + 2, y + 2, w - 4, h + 4, 12, 12);
                g2.dispose();
            }

            @Override
            protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {}

            @Override
            protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) {}

            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(210, 220, 235));
                Insets insets = getContentBorderInsets(tabPlacement);
                int x = tabPane.getX() + insets.left;
                int y = tabPane.getY() + insets.top + calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
                int w = tabPane.getWidth() - insets.left - insets.right;
                int h = tabPane.getHeight() - insets.top - insets.bottom - calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
                g2.drawRoundRect(x, y, w - 1, h - 1, 12, 12);
                g2.dispose();
            }

            @Override
            protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
                return 30;
            }
        });

        tabs.setBorder(new EmptyBorder(6, 6, 2, 6));
        return tabs;
    }

    private JPanel buildSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(16, 12, 12, 12));

        JPanel searchCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UI.BG_DEEP);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(220, 225, 235));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        searchCard.setOpaque(false);
        

        txtOrigin = modernField("Origin city...");
        txtDest = modernField("Destination city...");
        txtDate = modernField("YYYY-MM-DD");

        searchCard.add(fieldBlock("From", txtOrigin));
        searchCard.add(separatorArrow());
        searchCard.add(fieldBlock("To", txtDest));
        searchCard.add(fieldBlock("Date", txtDate));

        JButton btnSearch = UI.goldButton("Search Flights");
        btnSearch.addActionListener(e -> doSearch());

        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        btnWrap.setOpaque(false);
        btnWrap.add(btnSearch);
        searchCard.add(btnWrap);

        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(searchCard, BorderLayout.NORTH);

        cardListPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UI.BG_DEEP);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(220, 225, 235));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        cardListPanel.setBackground(UI.BG_DEEP);
        cardListPanel.setBorder(new EmptyBorder(12, 4, 12, 4));

        JScrollPane sp = new JScrollPane(cardListPanel);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        UI.styleScrollBar(sp.getVerticalScrollBar());

        panel.add(sp, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildReservationsPanel() {
        JPanel panel = new JPanel(new BorderLayout()) {};
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(16, 12, 12, 12));

        String[] cols = {"Ticket #", "Flight", "Seat Class", "Date", "Status"};
        resModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblReservations = new JTable(resModel);
        UI.styleTable(tblReservations);

        // Custom Header Render Customizations
        JTableHeader header = tblReservations.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 38)); // Expanded header height
        
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(UI.FONT_LABEL.deriveFont(Font.BOLD, 13f));
                setForeground(UI.BLUE); // Enforce UI.BLUE text color
                setBackground(Color.WHITE);
                setHorizontalAlignment(JLabel.CENTER);
                // Apply UI.BG_DEEP line cell borders to custom header blocks
                setBorder(new MatteBorder(0, 0, 2, column == table.getColumnCount() - 1 ? 0 : 1, UI.BG_DEEP));
                return this;
            }
        };
        header.setDefaultRenderer(headerRenderer);

        JScrollPane sp = new JScrollPane(tblReservations);
        sp.setOpaque(false);
        sp.getViewport().setBackground(Color.WHITE);
        sp.setBorder(new UI.RoundedBorder(14, UI.BG_DEEP));
        UI.styleScrollBar(sp.getVerticalScrollBar());

        panel.add(sp, BorderLayout.CENTER);
        return panel;
    }

    private void doSearch() {
        cardListPanel.removeAll();

        String origin = txtOrigin == null ? "" : txtOrigin.getText().trim().toLowerCase();
        String dest = txtDest == null ? "" : txtDest.getText().trim().toLowerCase();

        List<Flight> flights = new FlightDAO().getAll();
        List<Route> routes = new RouteDAO().getAll();
        List<Seat> seats = new SeatDAO().getAll();
        List<Plane> planes = new PlaneDAO().getAll();

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        int col = 0;
        int row = 0;

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;
        gbc.weighty = 0;

        boolean hasResults = false;

        for (Flight f : flights) {
            Route r = routes.stream()
                    .filter(x -> x.getRouteID().equals(f.getRouteID()))
                    .findFirst()
                    .orElse(null);

            if (r == null) continue;
            if (!origin.isEmpty() && !r.getOrigin().toLowerCase().contains(origin)) continue;
            if (!dest.isEmpty() && !r.getDestination().toLowerCase().contains(dest)) continue;

            Plane planeObj = planes.stream()
                    .filter(p -> p.getPlaneID().equals(f.getPlaneID()))
                    .findFirst()
                    .orElse(null);

            List<Seat> fs = seats.stream()
                    .filter(s -> s.getFlightID().equals(f.getFlightID()) || s.getFlightID().equals(f.getFlightID()))
                    .collect(Collectors.toList());

            String firstPrice = fs.stream()
                    .filter(s -> s.getSeatClass().equalsIgnoreCase("First Class"))
                    .findFirst()
                    .map(s -> "PHP " + s.getPrice())
                    .orElse("N/A");

            String econPrice = fs.stream()
                    .filter(s -> s.getSeatClass().equalsIgnoreCase("Economy"))
                    .findFirst()
                    .map(s -> "PHP " + s.getPrice())
                    .orElse("N/A");

            JPanel card = buildFlightCard(
                    f.getFlightID(),
                    f.getAirlineID(),
                    r.getOrigin(),
                    r.getDestination(),
                    f.getDepartureDateTime().format(fmt),
                    f.getArrivalDateTime().format(fmt),
                    firstPrice,
                    econPrice,
                    f,
                    planeObj
            );

            gbc.gridx = col;
            gbc.gridy = row;
            cardListPanel.add(card, gbc);

            col++;
            if (col == 2) {
                col = 0;
                row++;
            }
            hasResults = true;
        }

        GridBagConstraints filler = new GridBagConstraints();
        filler.gridx = 0;
        filler.gridy = row + 1;
        filler.gridwidth = 2;
        filler.weighty = 1.0;
        filler.fill = GridBagConstraints.VERTICAL;
        cardListPanel.add(Box.createVerticalGlue(), filler);

        if (!hasResults) {
            JLabel noResults = new JLabel("No flights found.");
            noResults.setFont(UI.FONT_SUBTITLE);
            noResults.setForeground(UI.TEXT_MUTED);
            noResults.setHorizontalAlignment(SwingConstants.CENTER);

            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.gridwidth = 2;
            c.fill = GridBagConstraints.BOTH;
            c.weightx = 1;
            c.weighty = 1;
            cardListPanel.add(noResults, c);
        }

        cardListPanel.revalidate();
        cardListPanel.repaint();
    }

    private JPanel buildFlightCard(String flightId, String airline, String origin, String destination, String dep, String arr, String firstPrice, String econPrice,
                                   Flight flightObj, Plane planeObj) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 12));
                g2.fillRoundRect(4, 6, getWidth() - 6, getHeight() - 4, 20, 20);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 20, 20);
                g2.dispose();
            }
        };

        card.setOpaque(false);
        card.setBorder(new EmptyBorder(18, 20, 14, 20));
        card.setPreferredSize(new Dimension(520, 210));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JLabel lblAirline = new JLabel(airline);
        lblAirline.setFont(UI.FONT_SUBTITLE);
        lblAirline.setForeground(new Color(0x005BAC));

        JLabel lblId = new JLabel("Flight " + flightId);
        lblId.setFont(UI.FONT_SMALL);
        lblId.setForeground(UI.TEXT_SECONDARY);

        topRow.add(lblAirline, BorderLayout.WEST);
        topRow.add(lblId, BorderLayout.EAST);

        JPanel routeRow = new JPanel(new GridBagLayout());
        routeRow.setOpaque(false);
        routeRow.setBorder(new EmptyBorder(10, 0, 6, 0));

        JPanel originBlock = cityBlock(origin, dep, true);
        JPanel arrowBlock = buildArrowBlock();
        JPanel destBlock = cityBlock(destination, arr, false);

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.gridx = 0;
        gc.weightx = 0.38;
        routeRow.add(originBlock, gc);

        gc.gridx = 1;
        gc.weightx = 0.24;
        routeRow.add(arrowBlock, gc);

        gc.gridx = 2;
        gc.weightx = 0.38;
        routeRow.add(destBlock, gc);

        JSeparator sep = new JSeparator();
        sep.setForeground(UI.BORDER_CARD);

        JPanel bottomRow = new JPanel(new BorderLayout());
        bottomRow.setOpaque(false);
        bottomRow.setBorder(new EmptyBorder(8, 0, 0, 0));

        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pricePanel.setOpaque(false);
        pricePanel.add(priceChip("Economy", econPrice, new Color(59, 130, 246)));
        pricePanel.add(Box.createHorizontalStrut(8));
        pricePanel.add(priceChip("First Class", firstPrice, UI.YELLOW));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 6));
        btnRow.setOpaque(false);
        btnRow.setAlignmentY(Component.CENTER_ALIGNMENT);

        JButton btnView = UI.smallOutlineButton("Details");
        JButton btnPlane = UI.smallOutlineButton("Plane");
        JButton btnBook = UI.smallYellowButton("Book");

        btnView.addActionListener(e -> new FlightDetailsDialog(this, flightObj));
        btnPlane.addActionListener(e -> new PlaneDetailsDialog(this, planeObj));
        btnBook.addActionListener(e -> {
            dispose();
            new ReservationForm(flightObj, planeObj);
        });

        btnRow.add(btnView);
        btnRow.add(btnPlane);
        btnRow.add(btnBook);

        bottomRow.add(pricePanel, BorderLayout.WEST);
        bottomRow.add(btnRow, BorderLayout.EAST);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        body.add(topRow);
        body.add(routeRow);
        body.add(sep);
        body.add(bottomRow);

        card.add(body, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(new EmptyBorder(17, 19, 13, 19));
                card.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(new EmptyBorder(18, 20, 14, 20));
                card.repaint();
            }
        });
        return card;
    }

    private JPanel cityBlock(String city, String time, boolean leftAlign) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel lblCity = new JLabel(city.toUpperCase());
        lblCity.setFont(UI.FONT_SUBTITLE.deriveFont(18f));
        lblCity.setForeground(new Color(0x102A43));
        lblCity.setAlignmentX(leftAlign ? Component.LEFT_ALIGNMENT : Component.RIGHT_ALIGNMENT);

        JLabel lblTime = new JLabel(time);
        lblTime.setFont(UI.FONT_SMALL);
        lblTime.setForeground(UI.TEXT_SECONDARY);
        lblTime.setAlignmentX(leftAlign ? Component.LEFT_ALIGNMENT : Component.RIGHT_ALIGNMENT);

        p.add(lblCity);
        p.add(Box.createVerticalStrut(2));
        p.add(lblTime);

        return p;
    }

    private JPanel buildArrowBlock() {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int y = getHeight() / 2;
                g2.setColor(new Color(0x005BAC));
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{4, 3}, 0));
                g2.drawLine(12, y, getWidth() - 12, y);

                int ax = getWidth() - 12;
                int[] xp = {ax, ax - 7, ax - 7};
                int[] yp = {y, y - 5, y + 5};
                g2.fillPolygon(xp, yp, 3);

                g2.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 16));
                String icon = "✈";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(icon, (getWidth() - fm.stringWidth(icon)) / 2, y + 5);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(80, 50));
        return p;
    }

    private JPanel priceChip(String label, String price, Color accent) {
        JPanel chip = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 15));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 60));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        chip.setOpaque(false);
        chip.setLayout(new BoxLayout(chip, BoxLayout.Y_AXIS));
        chip.setBorder(new EmptyBorder(4, 10, 4, 10));

        JLabel lbl = new JLabel(label.toUpperCase());
        lbl.setFont(UI.FONT_LABEL);
        lbl.setForeground(accent.darker());
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel priceLbl = new JLabel(price);
        priceLbl.setFont(UI.FONT_SUBTITLE.deriveFont(14f));
        priceLbl.setForeground(new Color(0x102A43));
        priceLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        chip.add(lbl);
        chip.add(priceLbl);

        return chip;
    }

    private void loadReservations() {
        resModel.setRowCount(0);
        List<Reservation> list = new ReservationDAO().getAll();
        int custId = Session.getInstance().getPassenger() != null ? Session.getInstance().getPassenger().getPassengerID() : -1;

        for (Reservation r : list) {
            if (r.getPassengerID() == custId) {
                resModel.addRow(new Object[]{
                        r.getTicketNumber(),
                        r.getFlightID(),
                        r.getSeatClass(),
                        r.getReservationDate(),
                        r.getStatus()
                });
            }
        }
    }

    private JTextField modernField(String placeholder) {
        JTextField f = UI.styledField(14);
        f.setToolTipText(placeholder);
        f.setBackground(Color.WHITE);
        f.setForeground(UI.TEXT_SECONDARY);
        f.setCaretColor(UI.YELLOW_DARK);
        return f;
    }

    private JPanel fieldBlock(String labelText, JTextField field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel lbl = new JLabel(labelText.toUpperCase());
        lbl.setFont(UI.FONT_LABEL);
        lbl.setForeground(UI.TEXT_PRIMARY);

        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(lbl);
        p.add(field);
        return p;
    }

    private JLabel separatorArrow() {
        JLabel arr = new JLabel("→");
        arr.setFont(UI.FONT_SUBTITLE.deriveFont(20f));
        arr.setForeground(UI.BLUE);
        arr.setBorder(new EmptyBorder(14, 4, 0, 4));
        return arr;
    }
}