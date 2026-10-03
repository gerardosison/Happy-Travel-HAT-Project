package ui.flight_information;

import model.Plane;
import ui.UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class PlaneDetailsDialog extends JDialog {
    private final Plane plane;
    
    public PlaneDetailsDialog(JFrame parent, Plane plane) {
        super(parent, true);
        this.plane = plane;

        setTitle("Plane Details");
        setSize(760, 640);
        setLocationRelativeTo(parent);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        setShape(new RoundRectangle2D.Double(0, 0, 760, 640, 28, 28));

        JPanel outer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Dialog Body Background
                g2.setColor(UI.BG_DEEP);
                g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 28, 28);

                // Slightly Bluish Outer Border Modification
                g2.setColor(new Color(200, 200, 200));
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, getWidth() - 7, getHeight() - 7, 28, 28);

                g2.dispose();
            }
        };

        outer.setOpaque(false);
        outer.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(10, 10, 20, 10));

        JScrollPane scroll = new JScrollPane(content);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        UI.styleScrollBar(scroll.getVerticalScrollBar());
        outer.add(scroll, BorderLayout.CENTER);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(0, 0, 16, 0));
        topBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Plane Details");
        title.setFont(UI.FONT_TITLE);
        title.setForeground(UI.BLUE);

        JButton btnBack = UI.ghostButton("Back");
        btnBack.addActionListener(e -> dispose());

        topBar.add(title, BorderLayout.WEST);
        topBar.add(btnBack, BorderLayout.EAST);

        content.add(topBar);
        content.add(buildHeroCard());
        content.add(Box.createVerticalStrut(18));
        content.add(buildSpecsCard());
        content.add(Box.createVerticalStrut(18));
        content.add(buildAmenitiesCard());

        add(outer);

        setVisible(true);
    }

    private JPanel buildHeroCard() {
        JPanel card = createCard();
        card.setLayout(new BorderLayout(18, 18));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel modelName = new JLabel(plane.getPlaneModel());
        modelName.setFont(UI.FONT_TITLE.deriveFont(28f));
        modelName.setForeground(UI.BLUE);

        JLabel planeId = new JLabel("Plane ID: " + plane.getPlaneID());
        planeId.setFont(UI.FONT_SUBTITLE);
        planeId.setForeground(UI.TEXT_PRIMARY);

        left.add(modelName);
        left.add(Box.createVerticalStrut(4));
        left.add(planeId);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.add(statChip("Airline", plane.getAirlineID(), UI.YELLOW));

        card.add(left, BorderLayout.WEST);
        card.add(right, BorderLayout.EAST);

        return card;
    }

    private JPanel buildSpecsCard() {
        JPanel card = createCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Aircraft Seating Configuration");
        title.setFont(UI.FONT_SUBTITLE);
        title.setForeground(UI.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT); // Forced left alignment

        card.add(title);
        card.add(Box.createVerticalStrut(16));

        JPanel info = new JPanel(new GridLayout(0, 2, 14, 14));
        info.setOpaque(false);
        info.setAlignmentX(Component.LEFT_ALIGNMENT);

        info.add(infoCard("Total Seats", String.valueOf(plane.getTotalSeats())));
        info.add(infoCard("First Class Seats", String.valueOf(plane.getFirstClassSeats())));
        info.add(infoCard("Economy Seats", String.valueOf(plane.getEconomySeats())));

        card.add(info);
        return card;
    }

    private JPanel buildAmenitiesCard() {
        JPanel card = createCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel amenitiesTitle = new JLabel("Amenities");
        amenitiesTitle.setFont(UI.FONT_SUBTITLE);
        amenitiesTitle.setForeground(UI.TEXT_PRIMARY);
        amenitiesTitle.setAlignmentX(Component.LEFT_ALIGNMENT); // Forced left alignment

        card.add(amenitiesTitle);
        card.add(Box.createVerticalStrut(8));

        String amenitiesText = plane.getAmenities();
        if (amenitiesText == null || amenitiesText.trim().isEmpty()) {
            amenitiesText = "No additional amenities configured for this aircraft type.";
        }

        JTextArea amenities = new JTextArea(amenitiesText);
        amenities.setWrapStyleWord(true);
        amenities.setLineWrap(true);
        amenities.setEditable(false);
        amenities.setOpaque(false);
        amenities.setFont(UI.FONT_BODY);
        amenities.setForeground(UI.TEXT_SECONDARY);
        amenities.setBorder(new EmptyBorder(4, 0, 0, 0));
        amenities.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(amenities);
        return card;
    }

    private JPanel infoCard(String label, String value) {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(UI.BG_PANEL);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

                g2.setColor(new Color(255, 255, 255, 18));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
            }
        };

        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(12, 14, 12, 14));

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

    private JPanel statChip(String label, String value, Color accent) {
        JPanel chip = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 20));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 80));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };

        chip.setOpaque(false);
        chip.setLayout(new BoxLayout(chip, BoxLayout.Y_AXIS));
        chip.setBorder(new EmptyBorder(8, 14, 8, 14));

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
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(UI.BG_PANEL);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);

                g2.setColor(new Color(255, 255, 255, 20));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);
                g2.dispose();
            }
        };

        card.setOpaque(false);
        card.setBorder(new EmptyBorder(22, 22, 22, 22));
        return card;
    }
}