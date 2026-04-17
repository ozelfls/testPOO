package view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Theme {

    // -------------------------------------------------------------------------
    // Palette
    // -------------------------------------------------------------------------
    public static final Color BG_MAIN    = new Color(22, 18, 48);
    public static final Color BG_PANEL   = new Color(32, 26, 66);
    public static final Color BG_CARD    = new Color(44, 36, 88);
    public static final Color BG_INPUT   = new Color(28, 22, 58);
    public static final Color BG_HEADER  = new Color(56, 42, 102);

    public static final Color ACCENT     = new Color(255, 130, 205); // neon pink
    public static final Color ACCENT2    = new Color(200, 160, 255); // soft violet
    public static final Color ACCENT3    = new Color(150, 240, 255); // cyan

    public static final Color TEXT       = new Color(255, 255, 252); // quase branco — mais legível
    public static final Color TEXT_DIM   = new Color(200, 188, 235); // muted — mais claro que antes

    public static final Color BORDER_LO  = new Color(78, 60, 135);
    public static final Color BORDER_HI  = new Color(135, 100, 210);

    public static final Color HUNGER_CLR    = new Color(255,  90,  90);
    public static final Color HAPPINESS_CLR = new Color(245, 255, 145);
    public static final Color ENERGY_CLR    = new Color( 90, 255, 140);

    public static final Color BTN_NORMAL = new Color(56, 42, 108);

    public static final Color TABLE_ODD    = new Color(32, 26, 66);
    public static final Color TABLE_EVEN   = new Color(40, 32, 80);
    public static final Color TABLE_SEL    = new Color(105, 72, 180);
    public static final Color TABLE_HEADER = new Color(56, 42, 102);

    // -------------------------------------------------------------------------
    // Fonts — tamanhos maiores, mais legíveis
    // -------------------------------------------------------------------------
    public static final Font FONT_TITLE    = new Font("Monospaced", Font.BOLD,  26);
    public static final Font FONT_SUBTITLE = new Font("SansSerif",  Font.PLAIN, 15);
    public static final Font FONT_BUTTON   = new Font("SansSerif",  Font.BOLD,  14);
    public static final Font FONT_LABEL    = new Font("SansSerif",  Font.PLAIN, 14);
    public static final Font FONT_STAT     = new Font("SansSerif",  Font.BOLD,  14);
    public static final Font FONT_TABLE    = new Font("SansSerif",  Font.PLAIN, 14);
    public static final Font FONT_HEADER   = new Font("Monospaced", Font.BOLD,  15);

    // -------------------------------------------------------------------------
    // Global UIManager defaults
    // -------------------------------------------------------------------------
    public static void apply() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        UIManager.put("Panel.background",             BG_PANEL);
        UIManager.put("Label.foreground",             TEXT);
        UIManager.put("Label.background",             BG_PANEL);
        UIManager.put("OptionPane.background",        BG_PANEL);
        UIManager.put("OptionPane.messageForeground", TEXT);
        UIManager.put("Button.background",            BTN_NORMAL);
        UIManager.put("Button.foreground",            TEXT);
        UIManager.put("Button.focus",                 new Color(0, 0, 0, 0));
        UIManager.put("Button.border",                BorderFactory.createEmptyBorder(12, 26, 12, 26));
        UIManager.put("TextField.background",         BG_INPUT);
        UIManager.put("TextField.foreground",         TEXT);
        UIManager.put("TextField.caretForeground",    ACCENT);
        UIManager.put("TextField.border",
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_LO, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        UIManager.put("Spinner.background",           BG_INPUT);
        UIManager.put("Spinner.foreground",           TEXT);
        UIManager.put("Table.background",             TABLE_ODD);
        UIManager.put("Table.foreground",             TEXT);
        UIManager.put("Table.selectionBackground",    TABLE_SEL);
        UIManager.put("Table.selectionForeground",    TEXT);
        UIManager.put("Table.gridColor",              BORDER_LO);
        UIManager.put("Table.font",                   FONT_TABLE);
        UIManager.put("TableHeader.background",       TABLE_HEADER);
        UIManager.put("TableHeader.foreground",       ACCENT2);
        UIManager.put("TableHeader.font",             FONT_HEADER);
        UIManager.put("ScrollPane.background",        BG_MAIN);
        UIManager.put("ScrollPane.border",            BorderFactory.createLineBorder(BORDER_LO, 1));
        UIManager.put("Viewport.background",          BG_MAIN);
        UIManager.put("ScrollBar.background",         BG_PANEL);
        UIManager.put("ScrollBar.thumb",              BORDER_HI);
        UIManager.put("ScrollBar.track",              BG_PANEL);
        UIManager.put("Dialog.background",            BG_PANEL);
        UIManager.put("FileChooser.background",       BG_PANEL);
        UIManager.put("FileChooser.foreground",       TEXT);
        UIManager.put("List.background",              BG_INPUT);
        UIManager.put("List.foreground",              TEXT);
        UIManager.put("ComboBox.background",          BG_INPUT);
        UIManager.put("ComboBox.foreground",          TEXT);
        UIManager.put("TitledBorder.titleColor",      ACCENT2);
        UIManager.put("TitledBorder.font",            FONT_STAT);
    }

    // -------------------------------------------------------------------------
    // Factory helpers
    // -------------------------------------------------------------------------

    private static Color lerp(Color a, Color b, float t) {
        t = Math.max(0f, Math.min(1f, t));
        return new Color(
            (int)(a.getRed()   + (b.getRed()   - a.getRed())   * t),
            (int)(a.getGreen() + (b.getGreen() - a.getGreen()) * t),
            (int)(a.getBlue()  + (b.getBlue()  - a.getBlue())  * t)
        );
    }

    /**
     * Botão arredondado com animação de hover suave.
     * Raio aumentado para 28px — visual mais "pill-shaped" e moderno.
     */
    public static JButton makeButton(String text, Color hoverColor) {
        final Color hc = (hoverColor != null) ? hoverColor : ACCENT;
        final float[] progress = {0f};
        final int STEPS = 14;
        final Timer[] timer = {null};

        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
                if (progress[0] > 0.05f) {
                    g2.setColor(new Color(
                        hc.getRed(), hc.getGreen(), hc.getBlue(),
                        (int)(55 * progress[0])));
                    g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 26, 26);
                }
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {}
        };

        btn.setFont(FONT_BUTTON);
        btn.setBackground(BTN_NORMAL);
        btn.setForeground(TEXT);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(12, 26, 12, 26));

        Runnable stopTimer = () -> { if (timer[0] != null) { timer[0].stop(); timer[0] = null; } };

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                stopTimer.run();
                timer[0] = new Timer(10, ev -> {
                    progress[0] = Math.min(1f, progress[0] + 1f / STEPS);
                    btn.setBackground(lerp(BTN_NORMAL, hc, progress[0]));
                    btn.setForeground(lerp(TEXT, BG_MAIN, progress[0]));
                    btn.repaint();
                    if (progress[0] >= 1f) stopTimer.run();
                });
                timer[0].start();
            }
            @Override public void mouseExited(MouseEvent e) {
                stopTimer.run();
                timer[0] = new Timer(10, ev -> {
                    progress[0] = Math.max(0f, progress[0] - 1f / STEPS);
                    btn.setBackground(lerp(BTN_NORMAL, hc, progress[0]));
                    btn.setForeground(lerp(TEXT, BG_MAIN, progress[0]));
                    btn.repaint();
                    if (progress[0] <= 0f) stopTimer.run();
                });
                timer[0].start();
            }
            @Override public void mousePressed(MouseEvent e) {
                stopTimer.run();
                btn.setBackground(hc.darker());
                btn.repaint();
            }
            @Override public void mouseReleased(MouseEvent e) {
                btn.setBackground(lerp(BTN_NORMAL, hc, progress[0]));
                btn.repaint();
            }
        });
        return btn;
    }

    public static JButton makeButton(String text) {
        return makeButton(text, ACCENT);
    }

    public static JProgressBar makeProgressBar(Color fillColor) {
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setStringPainted(true);
        bar.setFont(FONT_STAT);
        bar.setForeground(fillColor);
        bar.setBackground(BG_CARD);
        bar.setBorder(BorderFactory.createLineBorder(BORDER_LO, 1));
        bar.setPreferredSize(new Dimension(0, 30));
        bar.setUI(new BasicProgressBarUI() {
            @Override protected Color getSelectionBackground() { return BG_MAIN; }
            @Override protected Color getSelectionForeground() { return BG_MAIN; }
        });
        return bar;
    }

    public static void styleTextField(JTextField tf) {
        tf.setBackground(BG_INPUT);
        tf.setForeground(TEXT);
        tf.setCaretColor(ACCENT);
        tf.setFont(FONT_LABEL);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LO, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
    }

    public static void styleSpinner(JSpinner sp) {
        sp.setBackground(BG_INPUT);
        sp.setFont(FONT_LABEL);
        sp.setBorder(BorderFactory.createLineBorder(BORDER_LO, 1));
        JComponent editor = sp.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
            tf.setBackground(BG_INPUT);
            tf.setForeground(TEXT);
            tf.setCaretColor(ACCENT);
            tf.setFont(FONT_LABEL);
            tf.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        }
    }

    public static Border sectionBorder(String title) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACCENT2, 1),
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            FONT_STAT,
            ACCENT2
        );
    }

    public static JLabel label(String text, Font font, Color fg) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(fg);
        l.setBackground(BG_PANEL);
        l.setOpaque(false);
        return l;
    }
}
