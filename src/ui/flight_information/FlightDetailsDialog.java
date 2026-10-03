package ui.flight_information;

import dao.PlaneDAO;
import dao.SeatDAO;
import model.*;
import ui.UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class FlightDetailsDialog extends JDialog {

    private final Flight flight;

    public FlightDetailsDialog(JFrame parent, Flight flight) {
        super(parent, true);
        this.flight = flight;

        setTitle("Flight Details");
        setSize(760, 640);
        setLocationRelativeTo(parent);
        setUndecorated(true);
        setBackground(new Color(0,0,0,0));

        setShape(new RoundRectangle2D.Double(0,0,760,640,28,28));

        JPanel outer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

                // Dialog Body Background
                g2.setColor(UI.BG_DEEP);
                g2.fillRoundRect(0,0,getWidth()-6,getHeight()-6,28,28);

                // Slightly Bluish Outer Border Modification
                g2.setColor(new Color(200, 200, 200)); 
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-7,getHeight()-7,28,28);

                g2.dispose();
            }
        };

        outer.setOpaque(false);
        outer.setBorder(new EmptyBorder(16,16,16,16));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content,BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(10,10,20,10));

        JScrollPane scroll = new JScrollPane(content);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        UI.styleScrollBar(scroll.getVerticalScrollBar());
        outer.add(scroll,BorderLayout.CENTER);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(0,0,16,0));
        topBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Flight Details");
        title.setFont(UI.FONT_TITLE);
        title.setForeground(UI.BLUE);

        JButton btnBack = UI.ghostButton("Back");
        btnBack.addActionListener(e -> dispose());

        topBar.add(title,BorderLayout.WEST);
        topBar.add(btnBack,BorderLayout.EAST);

        content.add(topBar);
        content.add(buildHeroCard());
        content.add(Box.createVerticalStrut(18));
        content.add(buildScheduleCard());
        content.add(Box.createVerticalStrut(18));
        content.add(buildSeatPricingCard());
        content.add(Box.createVerticalStrut(18));
        content.add(buildAircraftCard());

        add(outer);

        setVisible(true);
    }

    private JPanel buildHeroCard() {
        JPanel card = createCard();

        card.setLayout(new BorderLayout(18,18));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left,BoxLayout.Y_AXIS));

        JLabel airline = new JLabel(flight.getAirlineID());
        airline.setFont(UI.FONT_TITLE.deriveFont(28f));
        airline.setForeground(UI.BLUE);

        JLabel flightId = new JLabel("Flight " + flight.getFlightID());
        flightId.setFont(UI.FONT_SUBTITLE);
        flightId.setForeground(UI.TEXT_PRIMARY);

        left.add(airline);
        left.add(Box.createVerticalStrut(4));
        left.add(flightId);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0));
        right.setOpaque(false);
        right.add(statChip("Plane", flight.getPlaneID(), UI.YELLOW));
        right.add(statChip("Route", flight.getRouteID(), UI.SUCCESS));

        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(left,BorderLayout.WEST);
        card.add(right,BorderLayout.EAST);

        return card;
    }

    private JPanel buildScheduleCard() {
        JPanel card = createCard();
        card.setLayout(new BoxLayout(card,BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Flight Schedule");
        title.setFont(UI.FONT_SUBTITLE);
        title.setForeground(UI.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT); // Forced left alignment

        card.add(title);
        card.add(Box.createVerticalStrut(18));

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEEE, MMMM dd yyyy • hh:mm a");

        JPanel schedulePanel = new JPanel(new GridLayout(1,2,18,0));
        schedulePanel.setOpaque(false);
        schedulePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        schedulePanel.add(scheduleBlock(
                "Departure",
                flight.getDepartureDateTime().format(fmt),
                UI.YELLOW
        ));
        schedulePanel.add(scheduleBlock(
                "Arrival",
                flight.getArrivalDateTime().format(fmt),
                UI.SUCCESS
        ));

        card.add(schedulePanel);

        return card;
    }

    private JPanel buildSeatPricingCard() {
        JPanel card = createCard();
        card.setLayout(new BoxLayout(card,BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Seat Availability & Pricing");
        title.setFont(UI.FONT_SUBTITLE);
        title.setForeground(UI.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT); // Forced left alignment

        card.add(title);
        card.add(Box.createVerticalStrut(16));

        List<Seat> seats = new SeatDAO().getAll()
                .stream()
                .filter(s -> s.getFlightID().equals(flight.getFlightID()))
                .collect(Collectors.toList());

        if (seats.isEmpty()) {
            JLabel empty = new JLabel("No seat information available.");

            empty.setFont(UI.FONT_BODY);
            empty.setForeground(UI.TEXT_SECONDARY);
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);

            card.add(empty);

            return card;
        }

        JPanel grid = new JPanel(new GridLayout(0,2,14,14));
        grid.setOpaque(false);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (Seat s : seats) {
            grid.add(seatCard(s));
        }

        card.add(grid);

        return card;
    }

    private JPanel buildAircraftCard() {
        JPanel card = createCard();
        card.setLayout(new BoxLayout(card,BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Aircraft Information");
        title.setFont(UI.FONT_SUBTITLE);
        title.setForeground(UI.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT); // Forced left alignment

        card.add(title);
        card.add(Box.createVerticalStrut(16));

        Plane plane = new PlaneDAO().getAll()
                .stream()
                .filter(p -> p.getPlaneID().equals(flight.getPlaneID()))
                .findFirst()
                .orElse(null);

        if (plane == null) {
            JLabel empty = new JLabel("Plane information unavailable.");

            empty.setFont(UI.FONT_BODY);
            empty.setForeground(UI.TEXT_SECONDARY);
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);

            card.add(empty);

            return card;
        }

        JPanel info = new JPanel(new GridLayout(0,2,14,14));

        info.setOpaque(false);
        info.setAlignmentX(Component.LEFT_ALIGNMENT);

        info.add(infoCard("Plane ID", plane.getPlaneID()));
        info.add(infoCard("Aircraft Model", plane.getPlaneModel()));
        info.add(infoCard("Total Seats", String.valueOf(plane.getTotalSeats())));
        info.add(infoCard("First Class Seats", String.valueOf(plane.getFirstClassSeats())));
        info.add(infoCard("Economy Seats", String.valueOf(plane.getEconomySeats())));
        info.add(infoCard("Airline", plane.getAirlineID()));

        card.add(info);
        card.add(Box.createVerticalStrut(16));

        JLabel amenitiesTitle = new JLabel("Amenities");

        amenitiesTitle.setFont(UI.FONT_SUBTITLE);
        amenitiesTitle.setForeground(UI.TEXT_PRIMARY);
        amenitiesTitle.setAlignmentX(Component.LEFT_ALIGNMENT); // Forced left alignment

        JTextArea amenities = new JTextArea(plane.getAmenities());

        amenities.setWrapStyleWord(true);
        amenities.setLineWrap(true);
        amenities.setEditable(false);
        amenities.setOpaque(false);
        amenities.setFont(UI.FONT_BODY);
        amenities.setForeground(UI.TEXT_SECONDARY);
        amenities.setBorder(new EmptyBorder(8,0,0,0));
        amenities.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(amenitiesTitle);
        card.add(amenities);

        return card;
    }

    private JPanel scheduleBlock(String label,String value,Color accent) {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(
                        accent.getRed(),
                        accent.getGreen(),
                        accent.getBlue(),
                        18
                ));

                g2.fillRoundRect(0,0,getWidth(),getHeight(),18,18);
                g2.setColor(new Color(
                        accent.getRed(),
                        accent.getGreen(),
                        accent.getBlue(),
                        70
                ));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,18,18);
                g2.dispose();
            }
        };

        p.setOpaque(false);
        p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(16,18,16,18));

        JLabel lbl = new JLabel(label.toUpperCase());

        lbl.setFont(UI.FONT_LABEL);
        lbl.setForeground(UI.TEXT_PRIMARY);

        JLabel val = new JLabel(value);

        val.setFont(UI.FONT_SMALL);
        val.setForeground(UI.TEXT_PRIMARY);

        p.add(lbl);
        p.add(Box.createVerticalStrut(8));
        p.add(val);

        return p;
    }

    private JPanel seatCard(Seat s) {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(UI.BG_PANEL);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),16,16);

                g2.setColor(new Color(255,255,255,22));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,16,16);

                g2.dispose();
            }
        };

        p.setOpaque(false);
        p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(14,16,14,16));

        JLabel cls = new JLabel(s.getSeatClass());

        cls.setFont(UI.FONT_SUBTITLE);
        cls.setForeground(UI.TEXT_PRIMARY);

        JLabel price = new JLabel("₱ " + s.getPrice());

        price.setFont(UI.FONT_TITLE.deriveFont(22f));
        price.setForeground(UI.YELLOW);

        JLabel seats = new JLabel(s.getAvailableSeats() + " seats available");

        seats.setFont(UI.FONT_BODY);
        seats.setForeground(UI.TEXT_SECONDARY);

        p.add(cls);
        p.add(Box.createVerticalStrut(10));
        p.add(price);
        p.add(Box.createVerticalStrut(6));
        p.add(seats);

        return p;
    }

    private JPanel infoCard(String label,String value) {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(UI.BG_PANEL);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),14,14);

                g2.setColor(new Color(255,255,255,18));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,14,14);

                g2.dispose();
            }
        };

        p.setOpaque(false);
        p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(12,14,12,14));

        JLabel lbl = new JLabel(label.toUpperCase());

        lbl.setFont(UI.FONT_LABEL);
        lbl.setForeground(UI.TEXT_PRIMARY);

        JLabel val = new JLabel(value);

        val.setFont(UI.FONT_SUBTITLE);
        val.setForeground(UI.TEXT_PRIMARY);

        p.add(lbl);
        p.add(Box.createVerticalStrut(6));
        p.add(val);

        return p;
    }

    private JPanel statChip(String label,String value,Color accent) {
        JPanel chip = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(
                        accent.getRed(),
                        accent.getGreen(),
                        accent.getBlue(),
                        20
                ));

                g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);

                g2.setColor(new Color(
                        accent.getRed(),
                        accent.getGreen(),
                        accent.getBlue(),
                        80
                ));

                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);

                g2.dispose();
            }
        };

        chip.setOpaque(false);
        chip.setLayout(new BoxLayout(chip,BoxLayout.Y_AXIS));
        chip.setBorder(new EmptyBorder(8,14,8,14));

        JLabel lbl = new JLabel(label.toUpperCase());
        lbl.setFont(UI.FONT_LABEL);
        lbl.setForeground(UI.TEXT_PRIMARY);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel val = new JLabel(value);
        val.setFont(UI.FONT_BOLD);
        val.setForeground(UI.TEXT_PRIMARY);
        val.setAlignmentX(Component.CENTER_ALIGNMENT);

        chip.add(lbl);
        chip.add(Box.createVerticalStrut(4));
        chip.add(val);

        return chip;
    }

    private JPanel createCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(UI.BG_PANEL);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),22,22);

                g2.setColor(new Color(255,255,255,20));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,22,22);

                g2.dispose();
            }
        };

        card.setOpaque(false);
        card.setBorder(new EmptyBorder(22,22,22,22));

        return card;
    }
}