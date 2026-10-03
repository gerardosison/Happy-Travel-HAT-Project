package ui.reservation;

import dao.*;
import model.*;
import ui.*;
import ui.flight_information.PassengerDashboard;
import utility.TicketGenerator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ReservationForm extends JFrame {

    private String flightId;
    private Flight currentFlight;
    private Route currentRoute;
    private JComboBox<String> cmbSeatClass;
    private JSpinner spnQuantity; // Added for seat counter
    private JLabel lblPrice, lblTicketPreview, lblPassenger;
    private JComboBox<String> cmbPassenger;
    private List<Passenger> passengerList;
    private List<Seat> seatList;

    public ReservationForm(Flight flightObj, Plane planeObj) {
        if (flightObj == null) {
            JOptionPane.showMessageDialog(null, "Invalid Flight Selection.", "Error", JOptionPane.ERROR_MESSAGE);
            goBack();
            return;
        }

        this.currentFlight = flightObj;
        this.flightId = flightObj.getFlightID();
        
        this.currentRoute = new RouteDAO().getAll().stream()
                .filter(r -> r.getRouteID().equals(currentFlight.getRouteID()))
                .findFirst()
                .orElse(null);

        setTitle("Happy Travel Reservation");
        setSize(750, 800); // Expanded slightly horizontally and vertically for balance
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UI.BG_DEEP);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UI.BG_DEEP);
        header.setBorder(new EmptyBorder(20, 40, 20, 40));

        JLabel lblTitle = new JLabel("New Reservation");
        lblTitle.setFont(UI.FONT_TITLE);
        lblTitle.setForeground(UI.BLUE);
        header.add(lblTitle, BorderLayout.WEST);

        JButton btnBack = UI.ghostButton("← Back");
        btnBack.addActionListener(e -> goBack());
        header.add(btnBack, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(UI.WHITE);
        card.setBorder(UI.CARD_BORDER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 24, 10, 24);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 3; // Shifted gridwidth to 3 to accommodate the row layout columns smoothly

        JLabel lblFlight = UI.subtitleLabel("Flight Number: " + flightId);
        card.add(lblFlight, gbc);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        gbc.gridy++;
        String routeText = (currentRoute != null) ? currentRoute.getOrigin() + " → " + currentRoute.getDestination() : "Unknown Route";
        JLabel lblRoute = new JLabel("Route: " + routeText);
        lblRoute.setFont(UI.FONT_SUBTITLE.deriveFont(16f));
        lblRoute.setForeground(UI.TEXT_PRIMARY);
        card.add(lblRoute, gbc);

        gbc.gridy++;
        JLabel lblDepTime = new JLabel("Estimated Departure: " + currentFlight.getDepartureDateTime().format(dtf));
        lblDepTime.setFont(UI.FONT_BODY);
        lblDepTime.setForeground(UI.TEXT_SECONDARY);
        card.add(lblDepTime, gbc);

        gbc.gridy++;
        JLabel lblArrTime = new JLabel("Estimated Arrival: " + currentFlight.getArrivalDateTime().format(dtf));
        lblArrTime.setFont(UI.FONT_BODY);
        lblArrTime.setForeground(UI.TEXT_SECONDARY);
        card.add(lblArrTime, gbc);

        // Form Row Form Headers
        gbc.gridy++; gbc.gridwidth = 1; gbc.weightx = 0.4; gbc.insets = new Insets(20, 24, 4, 24);
        card.add(makeLabel("Passenger Info"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.4;
        card.add(makeLabel("Seat Class Selection"), gbc);
        gbc.gridx = 2; gbc.weightx = 0.2;
        card.add(makeLabel("Seats (1-10)"), gbc);

        // Form Interactive Inputs Row
        gbc.gridy++; gbc.insets = new Insets(0, 24, 12, 24);
        gbc.gridx = 0;

        boolean isStaff = Session.getInstance().isStaff();
        if (isStaff) {
            passengerList = new PassengerDAO().getAll();
            String[] names = passengerList.stream().map(Passenger::getFullName).toArray(String[]::new);
            cmbPassenger = new JComboBox<>(names);
            UI.styleCombo(cmbPassenger);
            card.add(cmbPassenger, gbc);
        } else {
            Passenger p = Session.getInstance().getPassenger();
            lblPassenger = new JLabel(p != null ? p.getFullName() : "Anonymous Guest");
            lblPassenger.setFont(UI.FONT_BODY);
            lblPassenger.setForeground(UI.TEXT_PRIMARY);
            card.add(lblPassenger, gbc);
        }

        gbc.gridx = 1;
        seatList = new SeatDAO().getAll().stream()
                .filter(s -> s.getFlightID().equals(flightId))
                .collect(Collectors.toList());
                
        String[] classes = seatList.stream().map(Seat::getSeatClass).toArray(String[]::new);
        cmbSeatClass = new JComboBox<>(classes);
        UI.styleCombo(cmbSeatClass);
        cmbSeatClass.addActionListener(e -> updatePrice());
        card.add(cmbSeatClass, gbc);

        // Modern Customized Spinner Integration
        gbc.gridx = 2;
        SpinnerModel spinnerModel = new SpinnerNumberModel(1, 1, 10, 1); // Range limits 1-10 (Defaulting to 1 for pricing clarity)
        spnQuantity = new JSpinner(spinnerModel);
        styleSpinner(spnQuantity);
        spnQuantity.addChangeListener(e -> updatePrice());
        card.add(spnQuantity, gbc);

        // Price Summary Layout Components
        gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 3; gbc.insets = new Insets(16, 24, 4, 24);
        card.add(makeLabel("Price Summary"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 24, 12, 24);
        lblPrice = new JLabel("Loading price estimates...");
        lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblPrice.setForeground(UI.YELLOW_DARK);
        card.add(lblPrice, gbc);

        // Code Generation Architecture Components
        gbc.gridy++; gbc.insets = new Insets(16, 24, 4, 24);
        card.add(makeLabel("System Ticket Code Preview (First Ticket)"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 24, 12, 24);
        lblTicketPreview = new JLabel("—");
        lblTicketPreview.setFont(UI.FONT_MONO);
        lblTicketPreview.setForeground(UI.TEXT_SECONDARY);
        card.add(lblTicketPreview, gbc);

        // Action Buttons Setup Block
        gbc.gridy++; gbc.insets = new Insets(24, 24, 8, 24);
        JButton btnConfirm = UI.goldButton("Confirm Booking");
        btnConfirm.setPreferredSize(new Dimension(260, 44));
        btnConfirm.addActionListener(this::confirmBooking);
        card.add(btnConfirm, gbc);

        gbc.gridy++; gbc.insets = new Insets(4, 24, 16, 24);
        JButton btnCancel = UI.ghostButton("Cancel");
        btnCancel.setPreferredSize(new Dimension(260, 40));
        btnCancel.addActionListener(e -> goBack());
        card.add(btnCancel, gbc);

        add(card, BorderLayout.CENTER);
        
        updatePrice();
        setVisible(true);
    }

    private void styleSpinner(JSpinner spinner) {
        spinner.setFont(UI.FONT_BODY);
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField textField = ((JSpinner.DefaultEditor) editor).getTextField();
            textField.setEditable(false);
            textField.setBackground(UI.WHITE);
            textField.setForeground(UI.TEXT_PRIMARY);
            textField.setHorizontalAlignment(JTextField.CENTER);
        }
        spinner.setBorder(BorderFactory.createLineBorder(UI.TEXT_SECONDARY.brighter(), 1));
        spinner.setPreferredSize(new Dimension(80, 36));
    }

    private void updatePrice() {
        String selectedClass = (String) cmbSeatClass.getSelectedItem();
        if (selectedClass == null || spnQuantity == null) return;
        
        int quantity = (Integer) spnQuantity.getValue();
        
        Seat seat = seatList.stream()
                .filter(s -> s.getSeatClass().equalsIgnoreCase(selectedClass))
                .findFirst()
                .orElse(null);
                
        if (seat != null) {
            double totalCost = seat.getPrice().doubleValue() * quantity;
            lblPrice.setText(String.format("PHP %.2f total [%d left]", totalCost, seat.getAvailableSeats()));
            
            String destination = (currentRoute != null) ? currentRoute.getDestination() : "DEST";
            lblTicketPreview.setText(TicketGenerator.generate(currentFlight, destination));
        } else {
            lblPrice.setText("Pricing details unavailable.");
            lblTicketPreview.setText("—");
        }
    }

    private void confirmBooking(ActionEvent e) {
        String seatClass = (String) cmbSeatClass.getSelectedItem();
        int requestedQuantity = (Integer) spnQuantity.getValue();

        Seat seat = seatList.stream()
                .filter(s -> s.getSeatClass().equalsIgnoreCase(seatClass))
                .findFirst()
                .orElse(null);
                
        if (seat == null || seat.getAvailableSeats() < requestedQuantity) {
            String avail = (seat == null) ? "0" : String.valueOf(seat.getAvailableSeats());
            JOptionPane.showMessageDialog(this, "Not enough vacant inventory. Requested: " + requestedQuantity + ", Available: " + avail, "Sold Out", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int passengerId;
        String passName = "";
        if (Session.getInstance().isStaff() && cmbPassenger != null) {
            Passenger selected = passengerList.get(cmbPassenger.getSelectedIndex());
            passengerId = selected.getPassengerID();
            passName = selected.getFullName();
        } else {
            Passenger p = Session.getInstance().getPassenger();
            if (p == null) {
                JOptionPane.showMessageDialog(this, "Authorization validation active identity missing.", "Login Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            passengerId = p.getPassengerID();
            passName = p.getFullName();
        }

        String destination = (currentRoute != null) ? currentRoute.getDestination() : "DEST";
        String sampleTicketNo = TicketGenerator.generate(currentFlight, destination);
        double singlePrice = seat.getPrice().doubleValue();
        double totalCost = singlePrice * requestedQuantity;

        // Core business model persistence transactional entries matching inventory changes
        for (int i = 0; i < requestedQuantity; i++) {
            // Generate a distinct sequential variant identifier for additional instances if multiple seats booked
            String uniqueTicketNo = (requestedQuantity == 1) ? sampleTicketNo : sampleTicketNo + "-" + (i + 1);
            
            int resId = (int)(System.currentTimeMillis() % 100000) + i;
            Reservation res = new Reservation(resId, passengerId, flightId, seatClass, LocalDate.now(), "Purchase", "Confirmed", uniqueTicketNo);
            new ReservationDAO().add(res);

            int logId = (int)(System.currentTimeMillis() % 100000) + i;
            int staffId = Session.getInstance().isStaff() ? Session.getInstance().getStaff().getStaffID() : 0;
            String channel = Session.getInstance().isStaff() ? "Over-the-Counter" : "Online";
            TransactionLog log = new TransactionLog(logId, resId, passengerId, staffId, "Purchase", LocalDate.now(), java.time.LocalTime.now(), channel);
            new TransactionLogDAO().add(log);
        }

        // Apply aggregate updates to local models
        seat.setAvailableSeats(seat.getAvailableSeats() - requestedQuantity);
        new SeatDAO().update(seat);

        // Build Multi-Ticket Structural Card Display Dialog View
        String routeInfo = (currentRoute != null) ? currentRoute.getOrigin() + " to " + currentRoute.getDestination() : "";
        String ticketDisplay = (requestedQuantity == 1) ? sampleTicketNo : sampleTicketNo + " [× " + requestedQuantity + " Slots]";
        
        String msg = String.format(
            " TICKET BASE  : %s\n" +
            " PASSENGER    : %s\n" +
            " FLIGHT NO    : %s\n" +
            " ROUTE        : %s\n" +
            " SEAT CLASS   : %s\n" +
            " QUANTITY     : %d seat(s)\n" +
            " TOTAL FARE   : PHP %.2f\n",
            ticketDisplay, passName, flightId, routeInfo, seatClass, requestedQuantity, totalCost
        );

        JOptionPane.showMessageDialog(this, new JTextArea(msg), "Booking Success Receipt", JOptionPane.INFORMATION_MESSAGE);
        goBack();
    }

    private void goBack() {
        dispose();
        if (!Session.getInstance().isStaff()) {
            new PassengerDashboard();
        }
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UI.FONT_LABEL);
        lbl.setForeground(UI.TEXT_SECONDARY);
        return lbl;
    }
}