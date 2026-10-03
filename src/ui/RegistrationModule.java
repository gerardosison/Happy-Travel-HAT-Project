package ui;

import dao.PassengerDAO;
import model.Passenger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.time.LocalDate;

public class RegistrationModule extends JFrame {

    private static final Color DARK_NAVY  = new Color(0x08111F);
    private static final Color GOLD       = new Color(0xF5C518);
    private static final Color TEXT_LIGHT = new Color(0xF0F4FF);
    private static final Color TEXT_DIM   = new Color(0x3D567A);
    private static final Color BG_RIGHT   = new Color(0xEBF0FA);
    private static final Color CARD_BG    = new Color(0xFFFFFF);
    private static final Color FIELD_BG   = new Color(0xF4F7FC);
    private static final Color FIELD_BORD = new Color(0xCDD8EA);

    private JTextField     txtFirst, txtLast, txtEmail, txtPhone, txtAddress, txtUser;
    private JPasswordField txtPass, txtConfirm;
    private JLabel         lblStatus;
    private JLabel         revFirst, revLast, revEmail, revPhone, revAddress, revUser;

    public RegistrationModule() {
        setTitle("Happy Travel (HAT) — Register");
        setSize(1280, 720);
        setMinimumSize(new Dimension(1100, 640));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel root = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(0, 0, 0, 0);
        gbc.fill    = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        gbc.gridx = 0; gbc.weightx = 0.30;
        root.add(buildLeftPanel(), gbc);

        gbc.gridx = 1; gbc.weightx = 0.70;
        root.add(buildRightPanel(), gbc);

        setContentPane(root);
        setVisible(true);
    }

    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setPaint(new GradientPaint(0, 0, DARK_NAVY, 0, getHeight(), new Color(0x0C1A2E)));
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(GOLD);
                g2.fillRect(0, 0, getWidth(), 5);

                g2.setStroke(new BasicStroke(1f));
                g2.setColor(new Color(0x1A3357));
                g2.draw(new Ellipse2D.Float(-120, -120, 440, 440));
                g2.draw(new Ellipse2D.Float(-60, -60, 280, 280));
                g2.draw(new Ellipse2D.Float(-100, getHeight() - 180, 300, 300));
                g2.draw(new Ellipse2D.Float(getWidth() - 120, 80, 160, 160));
                g2.draw(new Ellipse2D.Float(getWidth() - 60, 100, 80, 80));

                g2.setColor(new Color(0x112035));
                g2.setStroke(new BasicStroke(0.5f));
                for (int y = 60; y < getHeight(); y += 60)
                    g2.drawLine(0, y, getWidth(), y);

                g2.setColor(new Color(0xF5C518, false));
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.08f));
                g2.setStroke(new BasicStroke(120f));
                g2.drawLine(-100, getHeight() + 100, getWidth() + 200, -200);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }
        };
        panel.setBackground(DARK_NAVY);
        panel.setLayout(new BorderLayout());

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBorder(new EmptyBorder(40, 36, 40, 36));

        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        brand.setOpaque(false);
        JLabel iconLbl = new JLabel("✈");
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        iconLbl.setForeground(GOLD);
        brand.add(iconLbl);
        brand.add(Box.createHorizontalStrut(8));

        JPanel brandText = new JPanel();
        brandText.setOpaque(false);
        brandText.setLayout(new BoxLayout(brandText, BoxLayout.Y_AXIS));
        JLabel brandName = new JLabel("HAPPY TRAVEL");
        brandName.setFont(UI.FONT_SUBTITLE);
        brandName.setForeground(GOLD);
        JLabel brandSub = new JLabel("HAT AIRLINE RESERVATION SYSTEM");
        brandSub.setFont(UI.FONT_SMALL);
        brandSub.setForeground(TEXT_DIM);
        brandText.add(brandName);
        brandText.add(brandSub);
        brand.add(brandText);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        inner.add(brand);
        inner.add(Box.createVerticalStrut(40));

        JLabel headline1 = new JLabel("Join the");
        headline1.setFont(UI.FONT_TITLE.deriveFont(42f));
        headline1.setForeground(TEXT_LIGHT);
        headline1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel headline2 = new JLabel("journey.");
        headline2.setFont(UI.FONT_TITLE.deriveFont(42f));
        headline2.setForeground(GOLD);
        headline2.setAlignmentX(Component.LEFT_ALIGNMENT);

        inner.add(headline1);
        inner.add(headline2);
        inner.add(Box.createVerticalStrut(14));

        JLabel subText = new JLabel(
            "<html><div style='width:260px;color:#5b7fa6;line-height:1.5;'>" +
            "Create your passenger account and start booking flights worldwide — all in one place." +
            "</div></html>");
        subText.setFont(UI.FONT_BODY);
        subText.setAlignmentX(Component.LEFT_ALIGNMENT);
        inner.add(subText);
        inner.add(Box.createVerticalGlue());

        panel.add(inner, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildRightPanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(BG_RIGHT);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(new Color(0xB8C8DC));
                for (int x = 15; x < getWidth(); x += 22)
                    for (int y = 15; y < getHeight(); y += 22)
                        g2.fillOval(x, y, 2, 2);

                float[] dash = {18f, 8f};
                g2.setColor(GOLD);
                g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, dash, 0f));
                g2.drawLine(0, 4, getWidth(), 4);

                g2.setStroke(new BasicStroke(1.5f));
                g2.setColor(new Color(0xB8C8DC));
                g2.draw(new Arc2D.Float(getWidth() - 180, -110, 260, 260, 180, 180, Arc2D.OPEN));
                g2.draw(new Arc2D.Float(getWidth() - 100, -60, 140, 140, 180, 180, Arc2D.OPEN));
                g2.draw(new Arc2D.Float(-130, getHeight() - 180, 240, 240, 0, 180, Arc2D.OPEN));

                g2.setColor(DARK_NAVY);
                g2.fillOval(getWidth() / 2 - 14, -14, 28, 28);
                g2.fillOval(getWidth() / 2 - 14, getHeight() - 14, 28, 28);

                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.10f));
                int[] barW = {2, 3, 2, 4, 2, 3, 2, 4, 2, 3, 2, 4, 2, 3, 2};
                int[] barH = {28, 22, 30, 18, 26, 30, 20, 28, 30, 16, 24, 30, 18, 26, 30};
                int bx = 28;
                for (int i = 0; i < barW.length; i++) {
                    g2.setColor(DARK_NAVY);
                    g2.fillRect(bx, getHeight() - 24 - barH[i], barW[i], barH[i]);
                    bx += barW[i] + 2;
                }
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

                int tagW = 120, tagH = 48;
                int tx = getWidth() - tagW - 18, ty = getHeight() - tagH - 18;
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(tx, ty, tagW, tagH, 10, 10);
                float[] dashTag = {4f, 3f};
                g2.setColor(FIELD_BORD);
                g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, dashTag, 0f));
                g2.drawRoundRect(tx, ty, tagW, tagH, 10, 10);
                g2.setColor(new Color(0x9aabc0));
                g2.setFont(UI.FONT_LABEL);
                g2.drawString("BOARDING PASS", tx + 12, ty + 16);
                g2.setColor(DARK_NAVY);
                g2.setFont(UI.FONT_BOLD);
                g2.drawString("HAT-2025", tx + 12, ty + 34);
            }
        };
        panel.setOpaque(false);

        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(FIELD_BORD);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout());

        JPanel scrollContent = new JPanel();
        scrollContent.setOpaque(false);
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setBorder(new EmptyBorder(36, 40, 36, 40));

        JPanel badgeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        badgeRow.setOpaque(false);
        badgeRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel eyebrow = new JLabel("  PASSENGER REGISTRATION  ");
        eyebrow.setFont(UI.FONT_LABEL);
        eyebrow.setForeground(GOLD);
        eyebrow.setBackground(DARK_NAVY);
        eyebrow.setOpaque(true);
        eyebrow.setBorder(new EmptyBorder(4, 8, 4, 8));
        badgeRow.add(eyebrow);
        badgeRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollContent.add(badgeRow);
        scrollContent.add(Box.createVerticalStrut(14));

        JLabel cardTitle = new JLabel("Create your account");
        cardTitle.setFont(UI.FONT_TITLE.deriveFont(26f));
        cardTitle.setForeground(DARK_NAVY);
        cardTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollContent.add(cardTitle);
        scrollContent.add(Box.createVerticalStrut(4));

        JLabel cardDesc = new JLabel("Complete all sections below to register");
        cardDesc.setFont(UI.FONT_BODY);
        cardDesc.setForeground(new Color(0x6b7c99));
        cardDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollContent.add(cardDesc);
        scrollContent.add(Box.createVerticalStrut(30));

        scrollContent.add(buildSectionHeader("I", "About Happy Travel (HAT)"));
        scrollContent.add(Box.createVerticalStrut(12));
        scrollContent.add(buildAboutSection());
        scrollContent.add(Box.createVerticalStrut(28));

        scrollContent.add(buildSectionHeader("II", "Terms and Conditions"));
        scrollContent.add(Box.createVerticalStrut(12));
        scrollContent.add(buildTermsSection());
        scrollContent.add(Box.createVerticalStrut(28));

        scrollContent.add(buildSectionHeader("III", "Fill Up Your Details"));
        scrollContent.add(Box.createVerticalStrut(12));
        scrollContent.add(buildFormSection());
        scrollContent.add(Box.createVerticalStrut(28));

        scrollContent.add(buildSectionHeader("IV", "Review Your Information"));
        scrollContent.add(Box.createVerticalStrut(12));
        scrollContent.add(buildReviewSection());
        scrollContent.add(Box.createVerticalStrut(20));

        lblStatus = new JLabel(" ");
        lblStatus.setFont(UI.FONT_BODY);
        lblStatus.setForeground(new Color(0xC0392B));
        lblStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollContent.add(lblStatus);
        scrollContent.add(Box.createVerticalStrut(10));

        JButton btnSubmit = buildDarkButton("Create My Account");
        btnSubmit.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSubmit.addActionListener(this::handleRegister);
        scrollContent.add(btnSubmit);
        scrollContent.add(Box.createVerticalStrut(14));

        scrollContent.add(buildSignInRow());
        scrollContent.add(Box.createVerticalStrut(10));

        JLabel footerNote = new JLabel("Staff & Admin accounts are managed by your system administrator.");
        footerNote.setFont(UI.FONT_SMALL);
        footerNote.setForeground(new Color(0x9aabc0));
        footerNote.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollContent.add(footerNote);

        JScrollPane scroller = new JScrollPane(scrollContent);
        scroller.setOpaque(false);
        scroller.getViewport().setOpaque(false);
        scroller.setBorder(null);
        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroller.getVerticalScrollBar().setUnitIncrement(14);
        scroller.getVerticalScrollBar().setPreferredSize(new Dimension(6, 0));
        SwingUtilities.invokeLater(() -> scroller.getVerticalScrollBar().setValue(0));

        card.add(scroller, BorderLayout.CENTER);

        JPanel cardWrapper = new JPanel(new BorderLayout());
        cardWrapper.setOpaque(false);
        cardWrapper.setBorder(new EmptyBorder(24, 24, 24, 24));
        cardWrapper.add(card, BorderLayout.CENTER);

        panel.add(cardWrapper, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildSectionHeader(String number, String title) {
        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel numBadge = new JLabel(number) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(DARK_NAVY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        numBadge.setFont(UI.FONT_LABEL);
        numBadge.setForeground(GOLD);
        numBadge.setBorder(new EmptyBorder(3, 9, 3, 9));
        numBadge.setOpaque(false);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UI.FONT_BOLD);
        titleLbl.setForeground(DARK_NAVY);

        row.add(numBadge);
        row.add(titleLbl);
        wrapper.add(row);
        wrapper.add(Box.createVerticalStrut(6));

        JSeparator sep = new JSeparator();
        sep.setForeground(FIELD_BORD);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        wrapper.add(sep);

        return wrapper;
    }

    private JPanel buildAboutSection() {
        JPanel infoBox = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xF0F6FF));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(FIELD_BORD);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.setColor(new Color(0x1A5CB0));
                g2.fillRect(0, 8, 4, getHeight() - 16);
                g2.dispose();
            }
        };
        infoBox.setOpaque(false);
        infoBox.setLayout(new BorderLayout());
        infoBox.setBorder(new EmptyBorder(14, 18, 14, 14));
        infoBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        JLabel about = new JLabel(
            "<html><div style='width:340px; color:#3D567A; line-height:1.7'>" +
            "Happy Travel (HAT) is an online airline reservation and flight information system. " +
            "It allows registered passengers to book flights to any destination worldwide.<br><br>" +
            "The system captures data about passengers, airlines, planes, routes, flights, seats, " +
            "reservations, and transactions. Over-the-counter bookings are processed by staff, " +
            "while online bookings are made directly by registered passengers like you." +
            "</div></html>");
        about.setFont(UI.FONT_BODY);
        infoBox.add(about, BorderLayout.CENTER);
        return infoBox;
    }

    private JPanel buildTermsSection() {
        JPanel termsBox = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xFFFDF0));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(0xE8D86A));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.setColor(GOLD);
                g2.fillRect(0, 8, 4, getHeight() - 16);
                g2.dispose();
            }
        };
        termsBox.setOpaque(false);
        termsBox.setLayout(new BorderLayout());
        termsBox.setBorder(new EmptyBorder(14, 18, 14, 14));
        termsBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        termsBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        JLabel terms = new JLabel(
            "<html><div style='width:340px; color:#3D567A; line-height:1.7'>" +
            "1. Account Registration<br>" +
            "By registering, you agree to provide accurate and complete information. Your username must be unique.<br><br>" +
            "2. Privacy<br>" +
            "Your personal data (name, email, phone, address) is stored securely and used solely for reservation purposes.<br><br>" +
            "3. Reservations<br>" +
            "All bookings are subject to seat availability. Confirmed reservations generate a unique ticket number.<br><br>" +
            "4. Cancellations &amp; Refunds<br>" +
            "Cancellations are permitted before flight departure. Refunds are processed per airline policy.<br><br>" +
            "5. Acceptable Use<br>" +
            "Accounts are for personal use only. Misuse or fraudulent activity will result in account suspension." +
            "</div></html>");
        terms.setFont(UI.FONT_BODY);
        termsBox.add(terms, BorderLayout.CENTER);
        return termsBox;
    }

    private JPanel buildFormSection() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(fieldLabel("  FIRST NAME"));        p.add(Box.createVerticalStrut(6));
        txtFirst = styledField("e.g. Juan");       p.add(txtFirst);   p.add(Box.createVerticalStrut(12));

        p.add(fieldLabel("  LAST NAME"));          p.add(Box.createVerticalStrut(6));
        txtLast = styledField("e.g. dela Cruz");   p.add(txtLast);    p.add(Box.createVerticalStrut(12));

        p.add(fieldLabel("  EMAIL"));              p.add(Box.createVerticalStrut(6));
        txtEmail = styledField("email@example.com"); p.add(txtEmail); p.add(Box.createVerticalStrut(12));

        p.add(fieldLabel("  PHONE"));              p.add(Box.createVerticalStrut(6));
        txtPhone = styledField("+63 9XX XXX XXXX"); p.add(txtPhone);  p.add(Box.createVerticalStrut(12));

        p.add(fieldLabel("  ADDRESS"));            p.add(Box.createVerticalStrut(6));
        txtAddress = styledField("Street, City, Province");
        txtAddress.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        p.add(txtAddress); p.add(Box.createVerticalStrut(12));

        p.add(fieldLabel("  USERNAME"));           p.add(Box.createVerticalStrut(6));
        txtUser = styledField("Choose a username");
        txtUser.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        p.add(txtUser); p.add(Box.createVerticalStrut(12));

        p.add(fieldLabel("  PASSWORD"));           p.add(Box.createVerticalStrut(6));
        txtPass = styledPasswordField("Password"); p.add(txtPass);    p.add(Box.createVerticalStrut(12));

        p.add(fieldLabel("  CONFIRM PASSWORD"));   p.add(Box.createVerticalStrut(6));
        txtConfirm = styledPasswordField("Confirm"); p.add(txtConfirm);

        return p;
    }

    private JPanel buildReviewSection() {
        JPanel outer = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(FIELD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(FIELD_BORD);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        outer.setOpaque(false);
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setBorder(new EmptyBorder(16, 18, 16, 18));
        outer.setAlignmentX(Component.LEFT_ALIGNMENT);
        outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        JLabel note = new JLabel("Review your details before submitting — tab through fields to update.");
        note.setFont(UI.FONT_SMALL);
        note.setForeground(new Color(0x9aabc0));
        note.setAlignmentX(Component.LEFT_ALIGNMENT);
        outer.add(note);
        outer.add(Box.createVerticalStrut(14));

        revFirst   = revVal(); revLast    = revVal();
        revEmail   = revVal(); revPhone   = revVal();
        revAddress = revVal(); revUser    = revVal();

        outer.add(reviewRow("Full Name", revFirst, revLast));   outer.add(Box.createVerticalStrut(9));
        outer.add(reviewRow("Email",     revEmail, null));       outer.add(Box.createVerticalStrut(9));
        outer.add(reviewRow("Phone",     revPhone, null));       outer.add(Box.createVerticalStrut(9));
        outer.add(reviewRow("Address",   revAddress, null));     outer.add(Box.createVerticalStrut(9));
        outer.add(reviewRow("Username",  revUser, null));

        FocusAdapter updater = new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) { refreshReview(); }
        };
        txtFirst.addFocusListener(updater);
        txtLast.addFocusListener(updater);
        txtEmail.addFocusListener(updater);
        txtPhone.addFocusListener(updater);
        txtAddress.addFocusListener(updater);
        txtUser.addFocusListener(updater);

        return outer;
    }

    private JLabel revVal() {
        JLabel l = new JLabel("—");
        l.setFont(UI.FONT_BODY);
        l.setForeground(DARK_NAVY);
        return l;
    }

    private JPanel reviewRow(String label, JLabel val1, JLabel val2) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));

        JLabel lbl = new JLabel(label + ":");
        lbl.setFont(UI.FONT_BOLD);
        lbl.setForeground(new Color(0x4a5878));
        lbl.setPreferredSize(new Dimension(88, 20));

        JPanel valPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        valPanel.setOpaque(false);
        valPanel.add(val1);
        if (val2 != null) valPanel.add(val2);

        row.add(lbl, BorderLayout.WEST);
        row.add(valPanel, BorderLayout.CENTER);
        return row;
    }

    private void refreshReview() {
        String first = txtFirst.getText().trim();
        String last  = txtLast.getText().trim();
        revFirst.setText(first.isEmpty() ? "—" : first);
        revLast.setText(last.isEmpty() ? "" : last);
        revEmail.setText(orDash(txtEmail.getText().trim()));
        revPhone.setText(orDash(txtPhone.getText().trim()));
        revAddress.setText(orDash(txtAddress.getText().trim()));
        revUser.setText(orDash(txtUser.getText().trim()));
    }

    private String orDash(String s) { return s.isEmpty() ? "—" : s; }

    private JLabel fieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UI.FONT_LABEL);
        lbl.setForeground(new Color(0x4a5878));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField styledField(String placeholder) {
        JTextField tf = new JTextField(22) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    g2.setColor(new Color(0xB0BDD0));
                    g2.setFont(UI.FONT_BODY);
                    g2.drawString(placeholder, 14, 22);
                }
            }
            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isFocusOwner() ? GOLD : FIELD_BORD);
                g2.setStroke(new BasicStroke(isFocusOwner() ? 2f : 1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            }
        };
        tf.setFont(UI.FONT_BODY);
        tf.setForeground(DARK_NAVY);
        tf.setBackground(FIELD_BG);
        tf.setOpaque(false);
        tf.setBorder(new EmptyBorder(10, 12, 10, 12));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        tf.setPreferredSize(new Dimension(180, 42));
        tf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { tf.repaint(); }
            public void focusLost(FocusEvent e)   { tf.repaint(); }
        });
        return tf;
    }

    private JPasswordField styledPasswordField(String placeholder) {
        JPasswordField pf = new JPasswordField(22) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
                if (getPassword().length == 0 && !isFocusOwner()) {
                    g2.setColor(new Color(0xB0BDD0));
                    g2.setFont(UI.FONT_BODY);
                    g2.drawString(placeholder, 14, 22);
                }
            }
            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isFocusOwner() ? GOLD : FIELD_BORD);
                g2.setStroke(new BasicStroke(isFocusOwner() ? 2f : 1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            }
        };
        pf.setFont(UI.FONT_BODY);
        pf.setForeground(DARK_NAVY);
        pf.setBackground(FIELD_BG);
        pf.setOpaque(false);
        pf.setBorder(new EmptyBorder(10, 12, 10, 12));
        pf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        pf.setPreferredSize(new Dimension(180, 42));
        pf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { pf.repaint(); }
            public void focusLost(FocusEvent e)   { pf.repaint(); }
        });
        return pf;
    }

    private JButton buildDarkButton(String label) {
        JButton btn = new JButton(label) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isPressed()  ? new Color(0x0A1F3A)
                         : getModel().isRollover() ? new Color(0x122844)
                         : DARK_NAVY;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                GradientPaint shimmer = new GradientPaint(
                    0, 0, new Color(0xF5C518, false),
                    getWidth() / 2, 0, new Color(0xF5C518));
                g2.setPaint(shimmer);
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(getWidth() / 4, 0, getWidth() * 3 / 4, 0);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(UI.FONT_BUTTON);
        btn.setForeground(GOLD);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(14, 24, 14, 24));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        btn.setPreferredSize(new Dimension(388, 52));
        return btn;
    }

    private JPanel buildSignInRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel have = new JLabel("Already have an account? ");
        have.setFont(UI.FONT_BODY);
        have.setForeground(new Color(0x6b7c99));

        JLabel sign = new JLabel("Sign in");
        sign.setFont(UI.FONT_BOLD);
        sign.setForeground(GOLD);
        sign.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sign.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) { new LoginModule(); dispose(); }
            public void mouseEntered(java.awt.event.MouseEvent e) { sign.setForeground(GOLD.darker()); }
            public void mouseExited(java.awt.event.MouseEvent e)  { sign.setForeground(GOLD); }
        });

        row.add(have);
        row.add(sign);
        return row;
    }

    private void handleRegister(ActionEvent e) {
        String first = txtFirst.getText().trim();
        String last  = txtLast.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String addr  = txtAddress.getText().trim();
        String user  = txtUser.getText().trim();
        String pass  = new String(txtPass.getPassword());
        String conf  = new String(txtConfirm.getPassword());

        if (first.isEmpty() || last.isEmpty() || email.isEmpty()) {
            lblStatus.setText("First name, last name, and email are required.");
            return;
        }
        if (user.isEmpty() || pass.isEmpty()) {
            lblStatus.setText("Username and password are required for online registration.");
            return;
        }
        if (!pass.equals(conf)) {
            lblStatus.setText("Passwords do not match.");
            return;
        }

        Passenger p = new Passenger(
            0, last, first, email,
            phone.isEmpty() ? null : phone,
            addr.isEmpty()  ? null : addr,
            LocalDate.now(), user, pass
        );

        boolean success = new PassengerDAO().add(p);

        if (success) {
            JOptionPane.showMessageDialog(this,
                "Account created successfully!\nPassenger ID: " + p.getPassengerID() + "\nPlease sign in.",
                "Registration Successful", JOptionPane.INFORMATION_MESSAGE);
            new LoginModule();
            dispose();
        } else {
            lblStatus.setText("Registration failed. Email or username may already be in use.");
            JOptionPane.showMessageDialog(this,
                "Database error. Please check:\n" +
                "1. PostgreSQL is running\n" +
                "2. The PASSENGER table exists\n" +
                "3. DB credentials in DBConnection.java are correct\n" +
                "4. Email and username are not already taken.",
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}