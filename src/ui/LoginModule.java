package ui;

import dao.PassengerDAO;
import dao.StaffDAO;
import model.Passenger;
import model.Staff;
import ui.airline_management.AdminDashboard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.sql.SQLException;
import java.util.Random;

public class LoginModule extends JFrame {
    private JTextField     txtUsername;
    private JPasswordField txtPassword;
    private JLabel         lblStatus;
    private JPanel         errorBanner;
    private JPanel         formCard;
    private boolean        hasError = false;

    private static final Color DARK_NAVY  = new Color(0x08111F);
    private static final Color GOLD       = new Color(0xF5C518);
    private static final Color TEXT_LIGHT = new Color(0xF0F4FF);
    private static final Color TEXT_DIM   = new Color(0x3D567A);
    private static final Color BG_RIGHT   = new Color(0xEBF0FA);
    private static final Color CARD_BG    = new Color(0xFFFFFF);
    private static final Color FIELD_BG   = new Color(0xF4F7FC);
    private static final Color FIELD_BORD = new Color(0xCDD8EA);

    public LoginModule() {
        setTitle("Happy Travel (HAT) — Login");
        setSize(1280, 720);
        setMinimumSize(new Dimension(1100, 640));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel root = new JPanel(new GridLayout(1, 2));
        root.add(buildLeftPanel());
        root.add(buildRightPanel());

        setContentPane(root);
        setVisible(true);
    }

    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(0, 0, DARK_NAVY, 0, getHeight(), new Color(0x0C1A2E));
                g2.setPaint(gp);
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
                for (int y = 60; y < getHeight(); y += 60) g2.drawLine(0, y, getWidth(), y);

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
        inner.setBorder(new EmptyBorder(52, 52, 52, 48));

        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        brand.setOpaque(false);

        JLabel iconLbl = new JLabel("✈");
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        iconLbl.setForeground(GOLD);
        brand.add(iconLbl);
        brand.add(Box.createHorizontalStrut(10));

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
        inner.add(Box.createVerticalStrut(64));

        JLabel headline1 = new JLabel("Your journey");
        headline1.setFont(UI.FONT_TITLE.deriveFont(46f));
        headline1.setForeground(TEXT_LIGHT);
        headline1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel headline2 = new JLabel("starts here.");
        headline2.setFont(UI.FONT_TITLE.deriveFont(46f));
        headline2.setForeground(GOLD);
        headline2.setAlignmentX(Component.LEFT_ALIGNMENT);

        inner.add(headline1);
        inner.add(headline2);
        inner.add(Box.createVerticalStrut(18));

        JLabel subText = new JLabel("<html><div style='width:310px;line-height:1.6'>Book flights worldwide, manage reservations, and track your travel — all in one place.</div></html>");
        subText.setFont(UI.FONT_BODY);
        subText.setForeground(new Color(0x5b7fa6));
        subText.setAlignmentX(Component.LEFT_ALIGNMENT);
        inner.add(subText);

        panel.add(inner, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildRightPanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
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

        formCard = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(hasError ? new Color(0xE74C3C) : FIELD_BORD);
                g2.setStroke(new BasicStroke(hasError ? 1.8f : 1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
            }
        };
        JPanel card = formCard;
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(40, 44, 40, 44));
        card.setPreferredSize(new Dimension(420, 490));
        card.setMaximumSize(new Dimension(460, 530));

        JLabel titleLbl = new JLabel("Welcome back");
        titleLbl.setFont(UI.FONT_TITLE.deriveFont(30f));
        titleLbl.setForeground(DARK_NAVY);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titleLbl);
        card.add(Box.createVerticalStrut(5));

        JLabel descLbl = new JLabel("Sign in to access your reservations");
        descLbl.setFont(UI.FONT_BODY);
        descLbl.setForeground(new Color(0x6b7c99));
        descLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(descLbl);
        card.add(Box.createVerticalStrut(30));

        JLabel userLabel = fieldLabel("  USERNAME");
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(userLabel);
        card.add(Box.createVerticalStrut(7));
        txtUsername = styledField("Enter your username");
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(txtUsername);
        card.add(Box.createVerticalStrut(18));

        JLabel passLabel = fieldLabel("  PASSWORD");
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(passLabel);
        card.add(Box.createVerticalStrut(7));
        txtPassword = styledPasswordField("Enter your password");
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(txtPassword);
        card.add(Box.createVerticalStrut(10));

        errorBanner = new JPanel(new BorderLayout(10, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xFDF0F0));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(0xE74C3C));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.fillRoundRect(0, 0, 4, getHeight(), 8, 8);
                g2.fillRect(2, 0, 2, getHeight());
            }
        };
        errorBanner.setOpaque(false);
        errorBanner.setBorder(new EmptyBorder(10, 14, 10, 14));
        errorBanner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        errorBanner.setVisible(false);
        errorBanner.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel errorIcon = new JLabel("✕");
        errorIcon.setFont(UI.FONT_BOLD);
        errorIcon.setForeground(new Color(0xE74C3C));

        lblStatus = new JLabel(" ");
        lblStatus.setFont(UI.FONT_BODY);
        lblStatus.setForeground(new Color(0x922B21));

        errorBanner.add(errorIcon, BorderLayout.WEST);
        errorBanner.add(lblStatus, BorderLayout.CENTER);
        card.add(errorBanner);
        card.add(Box.createVerticalStrut(8));

        JButton btnLogin = buildSignInButton();
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.addActionListener(this::handleLogin);
        card.add(btnLogin);
        card.add(Box.createVerticalStrut(14));

        JPanel divider = buildDivider();
        divider.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(divider);
        card.add(Box.createVerticalStrut(14));

        JButton btnRegister = buildRegisterButton();
        btnRegister.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRegister.addActionListener(e -> { new RegistrationModule(); dispose(); });
        card.add(btnRegister);

        panel.add(card);
        return panel;
    }

    private JLabel fieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UI.FONT_LABEL);
        lbl.setForeground(new Color(0x4a5878));
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
                Color borderColor = hasError ? new Color(0xE74C3C) : isFocusOwner() ? GOLD : FIELD_BORD;
                float width = (hasError || isFocusOwner()) ? 2f : 1.5f;
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(width));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            }
        };
        tf.setFont(UI.FONT_BODY);
        tf.setForeground(DARK_NAVY);
        tf.setBackground(FIELD_BG);
        tf.setOpaque(false);
        tf.setBorder(new EmptyBorder(12, 14, 12, 14));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        tf.setPreferredSize(new Dimension(332, 48));
        tf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { clearError(); tf.repaint(); }
            public void focusLost(FocusEvent e) { tf.repaint(); }
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
                Color borderColor = hasError ? new Color(0xE74C3C) : isFocusOwner() ? GOLD : FIELD_BORD;
                float width = (hasError || isFocusOwner()) ? 2f : 1.5f;
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(width));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            }
        };
        pf.setFont(UI.FONT_BODY);
        pf.setForeground(DARK_NAVY);
        pf.setBackground(FIELD_BG);
        pf.setOpaque(false);
        pf.setBorder(new EmptyBorder(12, 14, 12, 14));
        pf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        pf.setPreferredSize(new Dimension(332, 48));
        pf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { clearError(); pf.repaint(); }
            public void focusLost(FocusEvent e) { pf.repaint(); }
        });
        return pf;
    }

    private JButton buildSignInButton() {
        JButton btn = new JButton("Sign In") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isPressed()  ? new Color(0x0A1F3A)
                         : getModel().isRollover() ? new Color(0x122844)
                         : DARK_NAVY;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                GradientPaint shimmer = new GradientPaint(0, 0, new Color(0xF5C518, false),
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
        btn.setPreferredSize(new Dimension(332, 52));
        return btn;
    }

    private JButton buildRegisterButton() {
        JButton btn = new JButton("Create a Passenger Account") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover() ? new Color(0xE0E8F4) : Color.WHITE;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(getModel().isRollover() ? DARK_NAVY : FIELD_BORD);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(UI.FONT_BUTTON);
        btn.setForeground(DARK_NAVY);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(12, 24, 12, 24));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btn.setPreferredSize(new Dimension(332, 48));
        return btn;
    }

    private JPanel buildDivider() {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        JSeparator left  = new JSeparator(); left.setForeground(FIELD_BORD);
        JSeparator right = new JSeparator(); right.setForeground(FIELD_BORD);
        JLabel or = new JLabel("or", SwingConstants.CENTER);
        or.setFont(UI.FONT_LABEL);
        or.setForeground(new Color(0x9aabc0));
        p.add(left,  BorderLayout.WEST);
        p.add(or,    BorderLayout.CENTER);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    private void shakeCard() {
        Point origin = formCard.getLocation();
        int[] offsets = {-10, 10, -8, 8, -5, 5, -3, 3, 0};
        final int[] step = {0};
        javax.swing.Timer shaker = new javax.swing.Timer(30, null);
        shaker.addActionListener(e2 -> {
            if (step[0] < offsets.length) {
                formCard.setLocation(origin.x + offsets[step[0]], origin.y);
                step[0]++;
            } else {
                formCard.setLocation(origin);
                shaker.stop();
            }
        });
        shaker.start();
    }

    private void showError(String message) {
        hasError = true;
        lblStatus.setText(message);
        errorBanner.setVisible(true);
        txtUsername.setBackground(new Color(0xFDF0F0));
        txtPassword.setBackground(new Color(0xFDF0F0));
        formCard.repaint();
        shakeCard();
    }

    private void clearError() {
        hasError = false;
        lblStatus.setText(" ");
        errorBanner.setVisible(false);
        txtUsername.setBackground(FIELD_BG);
        txtPassword.setBackground(FIELD_BG);
        formCard.repaint();
    }

    private static final String[] EMPTY_QUOTES = {
        "Don't leave us hanging — fill in both fields!",
        "Username AND password. Both. We're waiting ✈",
        "Can't board the plane without a ticket — enter your credentials.",
        "Almost there! Just need a username and password.",
        "Your boarding pass requires both fields to be filled."
    };

    private static final String[] INVALID_QUOTES = {
        "Wrong credentials. Double-check and try again.",
        "Hmm, we don't recognize that. Check for typos?",
        "Access denied — invalid username or password.",
        "Those credentials didn't match our records. Try again!",
        "No match found. Maybe check your caps lock?"
    };

    private static final String[] DB_ERROR_QUOTES = {
        "Connection hiccup on our end. Please try again.",
        "Something went wrong with our servers. Try again shortly.",
        "Couldn't reach the database — please retry."
    };

    private final Random rng = new Random();

    private void handleLogin(ActionEvent e) {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        clearError();

        if (username.isEmpty() || password.isEmpty()) {
            showError(EMPTY_QUOTES[rng.nextInt(EMPTY_QUOTES.length)]);
            return;
        }

        try {
            Staff staff = new StaffDAO().findByCredentials(username, password);
            if (staff != null) {
                Session.getInstance().loginAsStaff(staff);
                new AdminDashboard();
                dispose();
                return;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showError(DB_ERROR_QUOTES[rng.nextInt(DB_ERROR_QUOTES.length)]);
            return;
        }

        try {
            Passenger passenger = new PassengerDAO().findByCredentials(username, password);
            if (passenger != null) {
                Session.getInstance().loginAsPassenger(passenger);
                new ui.flight_information.PassengerDashboard();
                dispose();
                return;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(DB_ERROR_QUOTES[rng.nextInt(DB_ERROR_QUOTES.length)]);
            return;
        }

        showError(INVALID_QUOTES[rng.nextInt(INVALID_QUOTES.length)]);
    }
}