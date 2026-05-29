package view;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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

    public static final Font FONT_TITLE    = new Font("Monospaced", Font.BOLD, 34);
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
        JPanel panel = new JPanel(layout);
        panel.setBackground(BG_PANEL);
        panel.setBorder(pixelBorder(NEON_PURPLE, PURPLE, 2));
        return panel;
    }

    public static Border pixelBorder(Color hi, Color lo, int thickness) {
        return new PixelBorder(hi, lo, thickness);
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
        Color hc = hoverColor != null ? hoverColor : NEON_PURPLE;
        JButton btn = new JButton(text);
        btn.setUI(new NeonButtonUI(hc));
        btn.setFont(FONT_BUTTON);
        btn.setBackground(BTN_NORMAL);
        btn.setForeground(TEXT);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        btn.setMargin(new Insets(0, 0, 0, 0));
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

    public static JProgressBar makeProgressBar(Color fillColor) {
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setStringPainted(true);
        bar.setFont(FONT_STAT);
        bar.setForeground(fillColor);
        bar.setBackground(BG_INPUT);
        bar.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        bar.setPreferredSize(new Dimension(0, 28));
        bar.setUI(new NeonProgressBarUI(fillColor));
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
            boolean hover = Boolean.TRUE.equals(b.getClientProperty("hover"));
            boolean pressed = Boolean.TRUE.equals(b.getClientProperty("pressed"));
            Graphics2D g2 = (Graphics2D) g.create();
            int w = c.getWidth();
            int h = c.getHeight();
            int offset = pressed ? 1 : 0;
            g2.setColor(withAlpha(accent, hover ? 70 : 32));
            g2.fillRect(6, 6, Math.max(0, w - 12), Math.max(0, h - 12));
            g2.setColor(pressed ? BG_INPUT : BTN_NORMAL);
            g2.fillRect(8 + offset, 8 + offset, Math.max(0, w - 16), Math.max(0, h - 16));
            g2.setColor(hover ? accent : PURPLE);
            g2.drawRect(7 + offset, 7 + offset, Math.max(0, w - 15), Math.max(0, h - 15));
            g2.setColor(withAlpha(accent, hover ? 180 : 90));
            g2.drawRect(10 + offset, 10 + offset, Math.max(0, w - 21), Math.max(0, h - 21));
            g2.dispose();
            super.paint(g, c);
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
            int innerX = 2;
            int innerY = 6;
            int innerW = Math.max(0, w - 4);
            int innerH = Math.max(1, h - 12);
            float pct = progressBar.getMaximum() == 0 ? 0f :
                    (float) progressBar.getValue() / (float) progressBar.getMaximum();
            int fillW = Math.round(innerW * pct);
            g2.setColor(BG_INPUT);
            g2.fillRect(0, 0, w, h);
            g2.setColor(withAlpha(fill, 60));
            g2.fillRect(innerX, innerY - 2, fillW, innerH + 4);
            g2.setColor(fill);
            g2.fillRect(innerX, innerY, fillW, innerH);
            g2.setColor(withAlpha(fill, 185));
            for (int x = innerX + 8; x < fillW; x += 12) {
                g2.fillRect(x, innerY, 2, innerH);
            }
            g2.setColor(PURPLE);
            g2.drawRect(innerX, innerY, innerW - 1, innerH - 1);
            String text = progressBar.getValue() + "%";
            g2.setFont(progressBar.getFont());
            FontMetrics fm = g2.getFontMetrics();
            int tx = w - fm.stringWidth(text) - 8;
            int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.setColor(TEXT);
            g2.drawString(text, tx, ty);
            g2.dispose();
        }

        @Override protected Color getSelectionBackground() { return TEXT; }
        @Override protected Color getSelectionForeground() { return TEXT; }
    }
}
