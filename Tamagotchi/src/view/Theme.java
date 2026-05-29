package view;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class Theme {

    public static final Color BG_MAIN    = new Color(0x08, 0x03, 0x12);
    public static final Color BG_PANEL   = new Color(0x12, 0x08, 0x27);
    public static final Color BG_CARD    = new Color(0x16, 0x0A, 0x31);
    public static final Color BG_INPUT   = new Color(0x10, 0x07, 0x24);
    public static final Color BG_HEADER  = new Color(0x12, 0x08, 0x27);

    public static final Color PURPLE     = new Color(0x7B, 0x2F, 0xFF);
    public static final Color NEON_PURPLE = new Color(0xB0, 0x4D, 0xFF);
    public static final Color MAGENTA    = new Color(0xFF, 0x4F, 0xCF);
    public static final Color PINK       = new Color(0xFF, 0x5D, 0xB1);
    public static final Color HOT_PINK   = new Color(0xFF, 0x2F, 0xA8);
    public static final Color CYAN       = new Color(0x46, 0xE7, 0xFF);
    public static final Color YELLOW     = new Color(0xFF, 0xD8, 0x4D);
    public static final Color GREEN      = new Color(0x4D, 0xFF, 0x85);
    public static final Color RED        = new Color(0xFF, 0x4F, 0x6A);

    public static final Color ACCENT     = PURPLE;
    public static final Color ACCENT2    = NEON_PURPLE;
    public static final Color ACCENT3    = MAGENTA;
    public static final Color TEXT       = new Color(0xF5, 0xEF, 0xFF);
    public static final Color TEXT_DIM   = new Color(0xC8, 0xB8, 0xFF);
    public static final Color BORDER_LO  = new Color(0x3A, 0x17, 0x76);
    public static final Color BORDER_HI  = NEON_PURPLE;

    public static final Color HUNGER_CLR    = RED;
    public static final Color HAPPINESS_CLR = YELLOW;
    public static final Color ENERGY_CLR    = GREEN;

    public static final Color BTN_NORMAL = new Color(0x1B, 0x0C, 0x3B);
    public static final Color TABLE_ODD    = new Color(0x10, 0x07, 0x24);
    public static final Color TABLE_EVEN   = new Color(0x17, 0x0A, 0x34);
    public static final Color TABLE_SEL    = PURPLE;
    public static final Color TABLE_HEADER = new Color(0x20, 0x0B, 0x48);

    public static final Font FONT_TITLE    = new Font("Monospaced", Font.BOLD, 38);
    public static final Font FONT_SUBTITLE = new Font("Monospaced", Font.BOLD, 15);
    public static final Font FONT_BUTTON   = new Font("Monospaced", Font.BOLD, 14);
    public static final Font FONT_LABEL    = new Font("Monospaced", Font.PLAIN, 13);
    public static final Font FONT_STAT     = new Font("Monospaced", Font.BOLD, 13);
    public static final Font FONT_TABLE    = new Font("Monospaced", Font.PLAIN, 13);
    public static final Font FONT_HEADER   = new Font("Monospaced", Font.BOLD, 15);

    public static void apply() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        UIManager.put("Panel.background", BG_PANEL);
        UIManager.put("Label.foreground", TEXT);
        UIManager.put("Label.background", BG_PANEL);
        UIManager.put("OptionPane.background", BG_PANEL);
        UIManager.put("OptionPane.messageForeground", TEXT);
        UIManager.put("Button.background", BTN_NORMAL);
        UIManager.put("Button.foreground", TEXT);
        UIManager.put("Button.focus", new Color(0, 0, 0, 0));
        UIManager.put("TextField.background", BG_INPUT);
        UIManager.put("TextField.foreground", TEXT);
        UIManager.put("TextField.caretForeground", MAGENTA);
        UIManager.put("Spinner.background", BG_INPUT);
        UIManager.put("Spinner.foreground", TEXT);
        UIManager.put("Table.background", TABLE_ODD);
        UIManager.put("Table.foreground", TEXT);
        UIManager.put("Table.selectionBackground", TABLE_SEL);
        UIManager.put("Table.selectionForeground", TEXT);
        UIManager.put("Table.gridColor", BORDER_LO);
        UIManager.put("Table.font", FONT_TABLE);
        UIManager.put("TableHeader.background", TABLE_HEADER);
        UIManager.put("TableHeader.foreground", YELLOW);
        UIManager.put("TableHeader.font", FONT_HEADER);
        UIManager.put("ScrollPane.background", BG_MAIN);
        UIManager.put("ScrollPane.border", pixelBorder(NEON_PURPLE, BG_PANEL, 2));
        UIManager.put("Viewport.background", BG_MAIN);
        UIManager.put("ScrollBar.background", BG_PANEL);
        UIManager.put("ScrollBar.thumb", PURPLE);
        UIManager.put("ScrollBar.track", BG_MAIN);
        UIManager.put("Dialog.background", BG_PANEL);
        UIManager.put("FileChooser.background", BG_PANEL);
        UIManager.put("FileChooser.foreground", TEXT);
        UIManager.put("List.background", BG_INPUT);
        UIManager.put("List.foreground", TEXT);
        UIManager.put("ComboBox.background", BG_INPUT);
        UIManager.put("ComboBox.foreground", TEXT);
        UIManager.put("TitledBorder.titleColor", NEON_PURPLE);
        UIManager.put("TitledBorder.font", FONT_STAT);
    }

    public static JPanel neonPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                int w = getWidth();
                int h = getHeight();
                g2.setPaint(new GradientPaint(0, 0, BG_CARD, 0, h, BG_PANEL));
                g2.fillRect(0, 0, w, h);
                g2.setColor(withAlpha(Color.BLACK, 92));
                g2.fillRect(8, h - 10, Math.max(0, w - 16), 5);
                g2.setColor(withAlpha(MAGENTA, 24));
                g2.drawRect(9, 9, Math.max(0, w - 19), Math.max(0, h - 19));
                g2.setColor(withAlpha(Color.WHITE, 20));
                g2.drawLine(14, 13, Math.max(14, w - 15), 13);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setBackground(BG_PANEL);
        panel.setBorder(pixelBorder(NEON_PURPLE, PURPLE, 2));
        return panel;
    }

    public static Border pixelBorder(Color hi, Color lo, int thickness) {
        return new PixelBorder(hi, lo, thickness);
    }

    public static void paintOuterGlow(Graphics2D g2, Shape shape, Color color, int layers, int maxAlpha) {
        Stroke oldStroke = g2.getStroke();
        Object oldAA = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int safeLayers = Math.max(1, layers);
        for (int i = safeLayers; i >= 1; i--) {
            float stroke = i * 2.4f;
            int alpha = Math.max(6, maxAlpha / (i + 1));
            g2.setStroke(new BasicStroke(stroke, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
            g2.setColor(withAlpha(color, alpha));
            g2.draw(shape);
        }
        g2.setStroke(oldStroke);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAA);
    }

    public static void paintNeonLine(Graphics2D g2, int x1, int y1, int x2, int y2, Color color) {
        paintOuterGlow(g2, new Line2D.Float(x1, y1, x2, y2), color, 4, 90);
        Stroke oldStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(1.6f));
        g2.setColor(color);
        g2.drawLine(x1, y1, x2, y2);
        g2.setStroke(oldStroke);
    }

    public enum ButtonGlyph {
        FEED, PLAY, SLEEP, TRAIN, PAW, ADD, EDIT, DELETE, SAVE, CANCEL, ARROW
    }

    public static Border sectionBorder(String title) {
        return BorderFactory.createTitledBorder(
            pixelBorder(NEON_PURPLE, PURPLE, 2),
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            FONT_STAT,
            NEON_PURPLE
        );
    }

    public static JButton makeButton(String text, Color hoverColor) {
        return makeButton(text, hoverColor, null);
    }

    public static JButton makeButton(String text, Color hoverColor, ButtonGlyph glyph) {
        Color hc = hoverColor != null ? hoverColor : NEON_PURPLE;
        JButton btn = new JButton(text);
        btn.setUI(new NeonButtonUI(hc));
        btn.setFont(FONT_BUTTON);
        btn.setBackground(BTN_NORMAL);
        btn.setForeground(TEXT);
        btn.setFocusPainted(false);
        btn.setRolloverEnabled(true);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(13, 18, 13, 18));
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setIconTextGap(12);
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        if (glyph != null) {
            btn.setIcon(new NeonGlyphIcon(glyph, hc, 28));
        }
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                btn.putClientProperty("hover", Boolean.TRUE);
                btn.repaint();
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.putClientProperty("hover", Boolean.FALSE);
                btn.putClientProperty("pressed", Boolean.FALSE);
                btn.repaint();
            }
            @Override public void mousePressed(MouseEvent e) {
                btn.putClientProperty("pressed", Boolean.TRUE);
                btn.repaint();
            }
            @Override public void mouseReleased(MouseEvent e) {
                btn.putClientProperty("pressed", Boolean.FALSE);
                btn.repaint();
            }
        });
        return btn;
    }

    public static JButton makeButton(String text) {
        return makeButton(text, NEON_PURPLE);
    }

    public static JButton makePremiumButton(String text, Color glow, ButtonGlyph glyph) {
        JButton btn = makeButton(text, glow, glyph);
        btn.putClientProperty("premium", Boolean.TRUE);
        btn.setFont(FONT_BUTTON.deriveFont(Font.BOLD, 16f));
        btn.setBorder(BorderFactory.createEmptyBorder(16, 22, 16, 22));
        if (glyph != null) {
            btn.setIcon(new NeonGlyphIcon(glyph, glow, 34));
        }
        return btn;
    }

    public static void restyleButton(JButton btn, Color glow, ButtonGlyph glyph, boolean premium) {
        Color hc = glow != null ? glow : NEON_PURPLE;
        btn.setUI(new NeonButtonUI(hc));
        btn.putClientProperty("premium", premium ? Boolean.TRUE : Boolean.FALSE);
        if (glyph != null) {
            btn.setIcon(new NeonGlyphIcon(glyph, hc, premium ? 34 : 28));
        }
        btn.repaint();
    }

    public static JProgressBar makeProgressBar(Color fillColor) {
        JProgressBar bar = new AnimatedProgressBar(0, 100);
        bar.setStringPainted(true);
        bar.setFont(FONT_STAT);
        bar.setForeground(fillColor);
        bar.setBackground(BG_INPUT);
        bar.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        bar.setPreferredSize(new Dimension(0, 32));
        bar.setUI(new NeonProgressBarUI(fillColor));
        Timer animation = new Timer(70, e -> {
            Object phaseValue = bar.getClientProperty("neonPhase");
            int phase = phaseValue instanceof Integer ? (Integer) phaseValue : 0;
            bar.putClientProperty("neonPhase", (phase + 1) % 1000);
            bar.repaint();
        });
        bar.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) {
                if (bar.isDisplayable()) {
                    animation.start();
                } else {
                    animation.stop();
                }
            }
        });
        animation.start();
        return bar;
    }

    public static void styleTextField(JTextField tf) {
        tf.setBackground(BG_INPUT);
        tf.setForeground(TEXT);
        tf.setCaretColor(MAGENTA);
        tf.setFont(FONT_LABEL);
        tf.setBorder(pixelBorder(NEON_PURPLE, PURPLE, 1));
        tf.setSelectionColor(PURPLE);
        tf.setSelectedTextColor(TEXT);
    }

    public static void styleSpinner(JSpinner sp) {
        sp.setBackground(BG_INPUT);
        sp.setFont(FONT_LABEL);
        sp.setBorder(pixelBorder(NEON_PURPLE, PURPLE, 1));
        JComponent editor = sp.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
            tf.setBackground(BG_INPUT);
            tf.setForeground(TEXT);
            tf.setCaretColor(MAGENTA);
            tf.setFont(FONT_LABEL);
            tf.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        }
    }

    public static JLabel label(String text, Font font, Color fg) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(fg);
        l.setBackground(BG_PANEL);
        l.setOpaque(false);
        return l;
    }

    public static Color withAlpha(Color c, int alpha) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), Math.max(0, Math.min(255, alpha)));
    }

    private static class PixelBorder implements Border {
        private final Color hi;
        private final Color lo;
        private final int thickness;

        PixelBorder(Color hi, Color lo, int thickness) {
            this.hi = hi;
            this.lo = lo;
            this.thickness = Math.max(1, thickness);
        }

        @Override public Insets getBorderInsets(Component c) {
            int v = thickness * 4;
            return new Insets(v, v, v, v);
        }

        @Override public boolean isBorderOpaque() {
            return false;
        }

        @Override public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            int t = thickness;
            Rectangle2D.Float glowRect = new Rectangle2D.Float(
                x + t * 2,
                y + t * 2,
                Math.max(0, width - t * 4),
                Math.max(0, height - t * 4)
            );
            paintOuterGlow(g2, glowRect, hi, 3, 62);
            g2.setColor(withAlpha(Color.BLACK, 85));
            g2.drawRect(x + t, y + t, Math.max(0, width - t * 2 - 1), Math.max(0, height - t * 2 - 1));
            g2.setColor(lo);
            g2.fillRect(x + t * 2, y, width - t * 4, t);
            g2.fillRect(x + t * 2, y + height - t, width - t * 4, t);
            g2.fillRect(x, y + t * 2, t, height - t * 4);
            g2.fillRect(x + width - t, y + t * 2, t, height - t * 4);
            g2.setColor(hi);
            g2.fillRect(x + t * 3, y + t, width - t * 6, t);
            g2.fillRect(x + t * 3, y + height - t * 2, width - t * 6, t);
            g2.fillRect(x + t, y + t * 3, t, height - t * 6);
            g2.fillRect(x + width - t * 2, y + t * 3, t, height - t * 6);
            int s = t * 4;
            g2.fillRect(x + t * 2, y + t * 2, s, t);
            g2.fillRect(x + t * 2, y + t * 2, t, s);
            g2.fillRect(x + width - t * 2 - s, y + t * 2, s, t);
            g2.fillRect(x + width - t * 3, y + t * 2, t, s);
            g2.fillRect(x + t * 2, y + height - t * 3, s, t);
            g2.fillRect(x + t * 2, y + height - t * 2 - s, t, s);
            g2.fillRect(x + width - t * 2 - s, y + height - t * 3, s, t);
            g2.fillRect(x + width - t * 3, y + height - t * 2 - s, t, s);
            g2.dispose();
        }
    }

    private static class NeonButtonUI extends javax.swing.plaf.basic.BasicButtonUI {
        private final Color accent;

        NeonButtonUI(Color accent) {
            this.accent = accent;
        }

        @Override public void paint(Graphics g, JComponent c) {
            AbstractButton b = (AbstractButton) c;
            ButtonModel model = b.getModel();
            boolean hover = model.isRollover() || Boolean.TRUE.equals(b.getClientProperty("hover"));
            boolean pressed = model.isPressed() || Boolean.TRUE.equals(b.getClientProperty("pressed"));
            boolean premium = Boolean.TRUE.equals(b.getClientProperty("premium"));
            boolean enabled = b.isEnabled();
            Graphics2D g2 = (Graphics2D) g.create();
            int w = c.getWidth();
            int h = c.getHeight();
            int offset = pressed ? 1 : 0;

            Shape outer = new Rectangle2D.Float(8 + offset, 8 + offset, Math.max(0, w - 17), Math.max(0, h - 17));
            int glowAlpha = enabled ? (premium ? (hover ? 125 : 88) : (hover ? 94 : 54)) : 28;
            paintOuterGlow(g2, outer, accent, premium ? 5 : 4, glowAlpha);
            if (premium) {
                paintOuterGlow(g2, new Rectangle2D.Float(13 + offset, 13 + offset, Math.max(0, w - 27), Math.max(0, h - 27)),
                    MAGENTA, 3, hover ? 90 : 56);
            }

            Color top = pressed ? BG_INPUT : (premium ? new Color(0x25, 0x0B, 0x4C) : BTN_NORMAL);
            Color bottom = pressed ? new Color(0x0B, 0x04, 0x18) : (premium ? new Color(0x3A, 0x0D, 0x5D) : new Color(0x11, 0x07, 0x29));
            g2.setPaint(new GradientPaint(0, 8, top, 0, h - 8, bottom));
            g2.fillRect(9 + offset, 9 + offset, Math.max(0, w - 18), Math.max(0, h - 18));

            g2.setColor(withAlpha(Color.BLACK, pressed ? 120 : 80));
            g2.drawLine(12 + offset, h - 13 + offset, Math.max(12, w - 13 + offset), h - 13 + offset);
            g2.setColor(withAlpha(Color.WHITE, premium ? 35 : 22));
            g2.drawLine(13 + offset, 12 + offset, Math.max(13, w - 14 + offset), 12 + offset);

            g2.setColor(enabled ? (hover ? accent : PURPLE) : BORDER_LO);
            g2.drawRect(7 + offset, 7 + offset, Math.max(0, w - 15), Math.max(0, h - 15));
            g2.setColor(withAlpha(enabled ? accent : TEXT_DIM, enabled ? (hover ? 210 : 130) : 58));
            g2.drawRect(11 + offset, 11 + offset, Math.max(0, w - 23), Math.max(0, h - 23));
            if (premium) {
                g2.setColor(withAlpha(HOT_PINK, hover ? 220 : 150));
                g2.drawLine(18 + offset, 16 + offset, Math.max(18, w - 19 + offset), 16 + offset);
                g2.setColor(withAlpha(YELLOW, hover ? 130 : 80));
                g2.fillRect(Math.max(18, w - 36 + offset), h / 2 - 2 + offset, 18, 4);
            }
            g2.dispose();
            super.paint(g, c);
        }
    }

    private static class NeonGlyphIcon implements Icon {
        private final ButtonGlyph glyph;
        private final Color color;
        private final int size;

        NeonGlyphIcon(ButtonGlyph glyph, Color color, int size) {
            this.glyph = glyph;
            this.color = color;
            this.size = size;
        }

        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }

        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.translate(x, y);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            paintOuterGlow(g2, new Rectangle2D.Float(4, 4, size - 8, size - 8), color, 3, 72);
            g2.setColor(withAlpha(color, 70));
            g2.fillRect(4, 4, size - 8, size - 8);
            g2.setColor(color);
            int s = size;
            switch (glyph) {
                case FEED:
                    g2.fillRect(s / 2 - 8, s / 2 - 5, 16, 10);
                    g2.fillRect(s / 2 - 5, s / 2 - 10, 10, 20);
                    g2.setColor(TEXT);
                    g2.fillRect(s / 2 - 2, s / 2 - 2, 4, 4);
                    break;
                case PLAY:
                    g2.fillPolygon(
                        new int[]{s / 2 - 6, s / 2 - 6, s / 2 + 9},
                        new int[]{s / 2 - 10, s / 2 + 10, s / 2},
                        3
                    );
                    break;
                case SLEEP:
                    g2.drawArc(s / 2 - 11, s / 2 - 11, 20, 20, 80, 230);
                    g2.fillRect(s / 2 + 4, s / 2 - 12, 8, 3);
                    g2.fillRect(s / 2 + 8, s / 2 - 8, 8, 3);
                    break;
                case TRAIN:
                    g2.fillRect(s / 2 - 13, s / 2 - 3, 26, 6);
                    g2.fillRect(s / 2 - 16, s / 2 - 8, 5, 16);
                    g2.fillRect(s / 2 + 11, s / 2 - 8, 5, 16);
                    g2.fillRect(s / 2 - 4, s / 2 - 10, 8, 20);
                    break;
                case PAW:
                    g2.fillOval(s / 2 - 8, s / 2 - 2, 16, 13);
                    g2.fillOval(s / 2 - 13, s / 2 - 10, 7, 8);
                    g2.fillOval(s / 2 - 4, s / 2 - 13, 7, 8);
                    g2.fillOval(s / 2 + 6, s / 2 - 10, 7, 8);
                    break;
                case ADD:
                    g2.fillRect(s / 2 - 10, s / 2 - 2, 20, 4);
                    g2.fillRect(s / 2 - 2, s / 2 - 10, 4, 20);
                    break;
                case EDIT:
                    g2.fillRect(s / 2 - 9, s / 2 + 4, 18, 4);
                    g2.drawLine(s / 2 - 7, s / 2 + 3, s / 2 + 8, s / 2 - 12);
                    g2.fillRect(s / 2 + 7, s / 2 - 13, 5, 5);
                    break;
                case DELETE:
                    g2.fillRect(s / 2 - 10, s / 2 - 10, 20, 4);
                    g2.fillRect(s / 2 - 7, s / 2 - 5, 14, 16);
                    g2.setColor(BG_INPUT);
                    g2.fillRect(s / 2 - 3, s / 2 - 2, 2, 10);
                    g2.fillRect(s / 2 + 3, s / 2 - 2, 2, 10);
                    break;
                case SAVE:
                    g2.fillRect(s / 2 - 10, s / 2 - 11, 20, 22);
                    g2.setColor(BG_INPUT);
                    g2.fillRect(s / 2 - 5, s / 2 - 8, 10, 5);
                    g2.fillRect(s / 2 - 6, s / 2 + 3, 12, 6);
                    break;
                case CANCEL:
                    g2.setStroke(new BasicStroke(4f));
                    g2.drawLine(s / 2 - 8, s / 2 - 8, s / 2 + 8, s / 2 + 8);
                    g2.drawLine(s / 2 + 8, s / 2 - 8, s / 2 - 8, s / 2 + 8);
                    break;
                case ARROW:
                    g2.fillPolygon(
                        new int[]{s / 2 - 8, s / 2 + 8, s / 2 - 8},
                        new int[]{s / 2 - 10, s / 2, s / 2 + 10},
                        3
                    );
                    g2.fillRect(s / 2 - 12, s / 2 - 3, 12, 6);
                    break;
            }
            g2.dispose();
        }
    }

    private static class AnimatedProgressBar extends JProgressBar {
        private int displayValue;
        private boolean ready;
        private Timer valueTimer;

        AnimatedProgressBar(int min, int max) {
            super(min, max);
            displayValue = super.getValue();
            putClientProperty("displayValue", displayValue);
            ready = true;
        }

        @Override public void setValue(int value) {
            int target = Math.max(getMinimum(), Math.min(getMaximum(), value));
            super.setValue(target);
            if (!ready) {
                displayValue = target;
                putClientProperty("displayValue", displayValue);
                return;
            }
            animateDisplayTo(target);
        }

        private void animateDisplayTo(int target) {
            if (valueTimer != null && valueTimer.isRunning()) {
                valueTimer.stop();
            }
            valueTimer = new Timer(18, null);
            valueTimer.addActionListener(e -> {
                int diff = target - displayValue;
                if (diff == 0) {
                    valueTimer.stop();
                    return;
                }
                int step = Math.max(1, Math.abs(diff) / 7);
                displayValue += diff > 0 ? step : -step;
                if ((diff > 0 && displayValue > target) || (diff < 0 && displayValue < target)) {
                    displayValue = target;
                }
                putClientProperty("displayValue", displayValue);
                repaint();
            });
            valueTimer.start();
        }
    }

    private static class NeonProgressBarUI extends BasicProgressBarUI {
        private final Color fill;

        NeonProgressBarUI(Color fill) {
            this.fill = fill;
        }

        @Override protected void paintDeterminate(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g.create();
            int w = progressBar.getWidth();
            int h = progressBar.getHeight();
            int innerX = 4;
            int innerY = 7;
            int innerW = Math.max(0, w - 8);
            int innerH = Math.max(1, h - 14);
            Object displayValue = progressBar.getClientProperty("displayValue");
            int visualValue = displayValue instanceof Integer ? (Integer) displayValue : progressBar.getValue();
            float pct = progressBar.getMaximum() == 0 ? 0f :
                    (float) visualValue / (float) progressBar.getMaximum();
            int fillW = Math.round(innerW * pct);
            Object phaseValue = progressBar.getClientProperty("neonPhase");
            int phase = phaseValue instanceof Integer ? (Integer) phaseValue : 0;
            int sweep = phase % 42;
            float pulse = (float) ((Math.sin(phase * 0.18) + 1.0) / 2.0);
            g2.setPaint(new GradientPaint(0, 0, BG_INPUT, 0, h, new Color(0x08, 0x03, 0x18)));
            g2.fillRect(0, 0, w, h);

            Shape track = new Rectangle2D.Float(innerX, innerY, innerW - 1, innerH - 1);
            paintOuterGlow(g2, track, fill, 3, 48);
            g2.setColor(withAlpha(Color.BLACK, 115));
            g2.fillRect(innerX, innerY, innerW, innerH);
            g2.setColor(withAlpha(PURPLE, 105));
            g2.drawRect(innerX, innerY, innerW - 1, innerH - 1);
            g2.setColor(withAlpha(Color.WHITE, 22));
            g2.drawLine(innerX + 1, innerY + 1, Math.max(innerX + 1, innerX + innerW - 2), innerY + 1);

            if (fillW > 0) {
                g2.setColor(withAlpha(fill, 54 + Math.round(pulse * 26)));
                g2.fillRect(innerX, innerY - 3, fillW, innerH + 6);
                g2.setPaint(new GradientPaint(innerX, innerY, fill.brighter(), innerX + Math.max(1, fillW), innerY, fill.darker()));
                g2.fillRect(innerX, innerY, fillW, innerH);
                g2.setColor(withAlpha(Color.WHITE, 45));
                g2.drawLine(innerX + 1, innerY + 2, innerX + Math.max(1, fillW - 2), innerY + 2);

                g2.setColor(withAlpha(Color.WHITE, 36));
                for (int x = innerX - 40 + sweep; x < innerX + fillW; x += 42) {
                    Polygon shine = new Polygon();
                    shine.addPoint(x, innerY);
                    shine.addPoint(x + 12, innerY);
                    shine.addPoint(x + 2, innerY + innerH);
                    shine.addPoint(x - 10, innerY + innerH);
                    g2.fillPolygon(shine);
                }

                g2.setColor(withAlpha(fill, 185 + Math.round(pulse * 45)));
                for (int x = innerX + 9 + (phase % 13); x < innerX + fillW; x += 13) {
                    g2.fillRect(x, innerY + 1, 2, innerH - 2);
                }
                int tipX = Math.min(innerX + fillW, innerX + innerW - 1);
                g2.setColor(TEXT);
                g2.fillRect(tipX - 2, innerY - 1, 3, innerH + 2);
                g2.setColor(withAlpha(fill, 120 + Math.round(pulse * 75)));
                g2.fillRect(Math.max(innerX, tipX - 7), innerY - 2, 6, innerH + 4);
                Theme.paintOuterGlow(g2, new Rectangle2D.Float(tipX - 3, innerY - 1, 5, innerH + 2), fill, 2, 46 + Math.round(pulse * 34));
            }

            g2.setColor(withAlpha(MAGENTA, 70));
            for (int x = innerX + 4; x < innerX + innerW; x += 26) {
                g2.drawLine(x, innerY + innerH + 3, Math.min(x + 7, innerX + innerW), innerY + innerH + 3);
            }
            String text = visualValue + "%";
            g2.setFont(progressBar.getFont());
            FontMetrics fm = g2.getFontMetrics();
            int tx = w - fm.stringWidth(text) - 8;
            int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.setColor(withAlpha(Color.BLACK, 180));
            g2.drawString(text, tx + 1, ty + 1);
            g2.setColor(TEXT);
            g2.drawString(text, tx, ty);
            g2.dispose();
        }

        @Override protected Color getSelectionBackground() { return TEXT; }
        @Override protected Color getSelectionForeground() { return TEXT; }
    }
}
