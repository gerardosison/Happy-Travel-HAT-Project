package ui;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public class UI {

    // Primary
    public static final Color BLUE = new Color(0, 90, 163);

    // Secondary
    public static final Color YELLOW         = new Color(0xFFD200);   // primary yellow
    public static final Color YELLOW_HOVER   = new Color(0xFFE15A);   // lighter yellow
    public static final Color YELLOW_DARK    = new Color(0xE6BE00);   // pressed yellow

    // Backgrounds
    public static final Color BG_DARK_DEEP   = new Color(0x252d3c);
    public static final Color BG_DEEP        = new Color(0xebeff5);   // light page background
    public static final Color BG_PANEL       = new Color(0xFFFFFF);   // white panel
    public static final Color BG_CARD        = new Color(0xF8FAFC);   // light card background

    // Body / text
    public static final Color WHITE          = Color.WHITE;
    public static final Color TEXT_PRIMARY   = new Color(0x0F172A);   // dark body text
    public static final Color TEXT_SECONDARY = new Color(0x64748B);   // muted gray-blue
    public static final Color TEXT_MUTED     = new Color(0x94A3B8);   // dim text

    // Accents
    public static final Color BORDER_SUBTLE  = new Color(0xD6E0EE);
    public static final Color BORDER_CARD    = new Color(0xE2E8F0);   // subtle border
    public static final Color SUCCESS        = new Color(0x22C55E);
    public static final Color DANGER         = new Color(0xEF4444);
    public static final Color WARNING        = new Color(0xF97316);
    public static final Color TABLE_ALT_ROW  = new Color(0xF8FAFC);
    
    // Typography
    public static Font FONT_TITLE    = loadFont("util/fonts/Inter_18pt-Bold.ttf",    24);
    public static Font FONT_SUBTITLE = loadFont("util/fonts/Inter_18pt-Bold.ttf",    16);
    public static Font FONT_BODY     = loadFont("util/fonts/Inter_18pt-Regular.ttf", 13);
    public static Font FONT_SMALL    = loadFont("util/fonts/Inter_18pt-Regular.ttf", 11);
    public static Font FONT_LABEL    = loadFont("util/fonts/Inter_18pt-Bold.ttf",    10);
    public static Font FONT_BOLD     = loadFont("util/fonts/Inter_18pt-Bold.ttf",    13);
    public static Font FONT_BUTTON   = loadFont("util/fonts/Inter_18pt-Bold.ttf",    13);
    public static Font FONT_MONO     = new Font("Consolas", Font.PLAIN, 12);

    // Borders
    public static final Border CARD_BORDER =
        BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_SUBTLE, 1, true),
            new EmptyBorder(16, 20, 16, 20));

    public static final Border FIELD_BORDER =
        BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_SUBTLE, 1, true),
            new EmptyBorder(6, 10, 6, 10));


    public static Font loadFont(String path, float size) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, new File(path));
            return font.deriveFont(size);
        } catch (Exception e) {
            return new Font("SansSerif", Font.PLAIN, (int) size);
        }
    }

    public static JButton goldButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isPressed() ? YELLOW_DARK
                         : getModel().isRollover() ? YELLOW_HOVER : YELLOW;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BUTTON);
        btn.setForeground(TEXT_PRIMARY);
        btn.setContentAreaFilled(false); btn.setBorderPainted(false);
        btn.setFocusPainted(false);      btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(9, 24, 9, 24));
        return btn;
    }

    public static JButton ghostButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 210, 0, 30));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }
                g2.setColor(getModel().isRollover() ? YELLOW : BORDER_SUBTLE);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BUTTON);
        btn.setForeground(TEXT_PRIMARY);
        btn.setContentAreaFilled(false); btn.setBorderPainted(false);
        btn.setFocusPainted(false);      btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(9, 24, 9, 24));
        return btn;
    }

    public static JButton dangerButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? DANGER.brighter() : DANGER);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BUTTON);
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false); btn.setBorderPainted(false);
        btn.setFocusPainted(false);      btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(9, 24, 9, 24));
        return btn;
    }

    public static JButton smallYellowButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? YELLOW_HOVER : YELLOW);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_LABEL);
        btn.setForeground(TEXT_PRIMARY);
        btn.setContentAreaFilled(false); btn.setBorderPainted(false);
        btn.setFocusPainted(false);      btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(5, 14, 5, 14));
        return btn;
    }

    public static JButton smallOutlineButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 210, 0, 18));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }
                g2.setColor(new Color(200, 210, 230));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_LABEL);
        btn.setForeground(TEXT_PRIMARY);
        btn.setContentAreaFilled(false); btn.setBorderPainted(false);
        btn.setFocusPainted(false);      btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(5, 10, 5, 10));
        return btn;
    }

    public static JTextField styledField(int columns) {
        JTextField tf = new JTextField(columns);
        tf.setFont(FONT_BODY);
        tf.setForeground(TEXT_PRIMARY);
        tf.setBackground(BG_CARD);
        tf.setCaretColor(YELLOW);
        tf.setBorder(FIELD_BORDER);
        return tf;
    }

    public static JPasswordField styledPasswordField(int columns) {
        JPasswordField pf = new JPasswordField(columns);
        pf.setFont(FONT_BODY);
        pf.setForeground(TEXT_PRIMARY);
        pf.setBackground(BG_CARD);
        pf.setCaretColor(YELLOW);
        pf.setBorder(FIELD_BORDER);
        return pf;
    }

    public static JLabel formLabel(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.RIGHT);
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(TEXT_SECONDARY);
        return lbl;
    }

    public static JLabel titleLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_TITLE);
        lbl.setForeground(YELLOW);
        return lbl;
    }

    public static JLabel subtitleLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_SUBTITLE);
        lbl.setForeground(TEXT_PRIMARY);
        return lbl;
    }

    public static void styleTable(JTable table) {
        table.setBackground(BG_PANEL);
        table.setForeground(TEXT_PRIMARY);
        table.setFont(FONT_BODY);
        table.setRowHeight(36);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(0xDCEBFF));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setFillsViewportHeight(true);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, value, sel, focus, row, col);
                setFont(FONT_BODY);
                setBorder(new EmptyBorder(0, 14, 0, 14));
                if (sel) {
                    setBackground(new Color(0xDCEBFF));
                    setForeground(TEXT_PRIMARY);
                } else {
                    setBackground(row % 2 == 0 ? BG_PANEL : TABLE_ALT_ROW);
                    setForeground(TEXT_PRIMARY);
                }
                return this;
            }
        });

        JTableHeader header = table.getTableHeader();
        header.setBackground(BG_CARD);
        header.setForeground(YELLOW);
        header.setFont(FONT_LABEL);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, YELLOW));
        header.setReorderingAllowed(false);
    }

    public static void styleScrollPane(JScrollPane sp) {
        sp.setBorder(new RoundedBorder(12, BORDER_SUBTLE));
        sp.getViewport().setBackground(BG_PANEL);
        sp.setBackground(BG_PANEL);
        styleScrollBar(sp.getVerticalScrollBar());
        styleScrollBar(sp.getHorizontalScrollBar());
    }

    public static void styleScrollBar(JScrollBar sb) {
        sb.setPreferredSize(new Dimension(8, 8));
        sb.setBackground(BG_PANEL);
        sb.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                thumbColor = new Color(180, 190, 210);
                trackColor = BG_PANEL;
            }
            @Override protected JButton createDecreaseButton(int o) { return zeroBtn(); }
            @Override protected JButton createIncreaseButton(int o) { return zeroBtn(); }
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                g2.fillRoundRect(r.x + 1, r.y + 2, r.width - 2, r.height - 4, 8, 8);
                g2.dispose();
            }
            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
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

    public static void styleCombo(JComboBox<?> combo) {
        combo.setBackground(Color.WHITE);
        combo.setForeground(TEXT_PRIMARY);
        combo.setFont(FONT_BODY);
        combo.setBorder(BorderFactory.createLineBorder(UI.BG_DEEP, 1));

        combo.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton();
                button.setBackground(Color.WHITE);
                button.setBorder(BorderFactory.createEmptyBorder());
                button.setContentAreaFilled(false);
                button.setFocusPainted(false);
         
                button.setIcon(new Icon() {
                    @Override
                    public void paintIcon(Component c, Graphics g, int x, int y) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(TEXT_PRIMARY); // Inherits your primary text color
                        g2.setStroke(new BasicStroke(1.5f));
                        
                        // Draws a simple modern "v" chevron
                        g2.drawLine(x + 4, y + 6, x + 8, y + 10);
                        g2.drawLine(x + 8, y + 10, x + 12, y + 6);
                        g2.dispose();
                    }
                    @Override
                    public int getIconWidth() { return 16; }
                    @Override
                    public int getIconHeight() { return 16; }
                });
                return button;
            }

            @Override
            protected ComboPopup createPopup() {
                BasicComboPopup popup = (BasicComboPopup) super.createPopup();
                popup.setBorder(BorderFactory.createLineBorder(UI.BG_DEEP, 1));
                return popup;
            }
        });

        ((JLabel) combo.getRenderer()).setBackground(Color.WHITE);
    }

    public static void applyGlobalDefaults() {
        UIManager.put("Panel.background",              BG_DEEP);
        UIManager.put("OptionPane.background",         BG_PANEL);
        UIManager.put("OptionPane.messageForeground",  TEXT_PRIMARY);
        UIManager.put("Label.foreground",              TEXT_PRIMARY);
        UIManager.put("TextField.background",          BG_CARD);
        UIManager.put("TextField.foreground",          TEXT_PRIMARY);
        UIManager.put("TextField.caretForeground",     YELLOW);
        UIManager.put("PasswordField.background",      BG_CARD);
        UIManager.put("PasswordField.foreground",      TEXT_PRIMARY);
        UIManager.put("ComboBox.background",           BG_CARD);
        UIManager.put("ComboBox.foreground",           TEXT_PRIMARY);
        UIManager.put("ScrollPane.background",         BG_PANEL);
        UIManager.put("Viewport.background",           BG_PANEL);
        UIManager.put("Table.background",              BG_PANEL);
        UIManager.put("Table.foreground",              TEXT_PRIMARY);
        UIManager.put("TableHeader.background",        BG_CARD);
        UIManager.put("TableHeader.foreground",        YELLOW);
        UIManager.put("TabbedPane.background",         BG_PANEL);
        UIManager.put("TabbedPane.foreground",         TEXT_SECONDARY);
        UIManager.put("TabbedPane.selected",           BG_CARD);
        UIManager.put("TabbedPane.selectedForeground", TEXT_PRIMARY);
        UIManager.put("SplitPane.background",          BG_DEEP);
        UIManager.put("Button.background",             YELLOW);
        UIManager.put("Button.foreground",             TEXT_PRIMARY);
    }

    public static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color color;

        public RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color  = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.2f));

            g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);

            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
        }
    }

    public static JButton closeButton() {
        JButton b = new JButton("✕") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isRollover()) {
                    g2.setColor(new Color(239, 68, 68, 50));
                    g2.fillOval(0, 0, getWidth(), getHeight());
                }

                g2.dispose();

                super.paintComponent(g);
            }
        };

        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setForeground(TEXT_SECONDARY);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(32, 32));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return b;
    }
}