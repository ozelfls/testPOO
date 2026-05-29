package view;

import controller.GameController;
import model.Pet;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MainFrame extends JFrame {

    private final GameController controller = new GameController();

    private JLabel lblNome;
    private JLabel lblTipo;
    private JLabel lblEvolution;
    private JLabel lblMood;
    private JLabel lblClock;
    private JProgressBar barHunger;
    private JProgressBar barHappiness;
    private JProgressBar barEnergy;
    private PetImagePanel petImagePanel;
    private JButton btnMenu;
    private JButton btnSettings;
    private JButton btnDormir;
    private JButton btnCrud;
    private JPanel sideMenu;
    private JPanel settingsPanel;
    private JPanel themeListPanel;
    private Timer sideMenuTimer;
    private Timer settingsTimer;
    private Timer clockTimer;
    private Timer pulseTimer;
    private Timer needsTimer;
    private boolean sideMenuOpen;
    private boolean settingsOpen;
    private boolean animationsEnabled = true;
    private boolean scanlinesEnabled = true;
    private VisualTheme activeTheme = VisualTheme.CYBER_NEON;
    private int sideMenuWidth;
    private int settingsWidth;
    private float visualPulse;

    private static final int SIDE_MENU_TARGET_WIDTH = 236;
    private static final int SETTINGS_TARGET_WIDTH = 252;
    private static final DateTimeFormatter CLOCK_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private enum VisualTheme {
        PIXEL_RETRO("Pixel Retro", Theme.YELLOW, Theme.PINK, new Color(0xFF, 0x8B, 0x3D), new Color(0x1B, 0x10, 0x24), new Color(0x2A, 0x12, 0x2D)),
        CYBER_NEON("Cyber Neon", Theme.MAGENTA, Theme.NEON_PURPLE, Theme.HOT_PINK, new Color(0x0E, 0x04, 0x1F), new Color(0x25, 0x0C, 0x49)),
        COZY_NATURE("Cozy Nature", Theme.GREEN, new Color(0x77, 0xC9, 0x8A), new Color(0xFF, 0xD8, 0x4D), new Color(0x08, 0x18, 0x15), new Color(0x12, 0x2D, 0x24));

        final String label;
        final Color primary;
        final Color secondary;
        final Color highlight;
        final Color deep;
        final Color panel;

        VisualTheme(String label, Color primary, Color secondary, Color highlight, Color deep, Color panel) {
            this.label = label;
            this.primary = primary;
            this.secondary = secondary;
            this.highlight = highlight;
            this.deep = deep;
            this.panel = panel;
        }
    }

    public MainFrame() {
        super("Tamagotchi Cyber");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        getContentPane().setBackground(Theme.BG_MAIN);
        setLayout(new BorderLayout(0, 0));
        buildUi();
        startClock();
        startMicroEffects();
        loadPet();
        startNeedsTimer();
        pack();
        setMinimumSize(new Dimension(560, 720));
        setLocationRelativeTo(null);
    }

    private void buildUi() {
        JPanel shell = new JPanel(new BorderLayout(0, 14)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                int w = getWidth();
                int h = getHeight();
                g2.setPaint(new GradientPaint(0, 0, activeTheme.deep, 0, h, Theme.BG_MAIN));
                g2.fillRect(0, 0, w, h);
                Theme.paintOuterGlow(g2, new Rectangle2D.Float(12, 12, Math.max(0, w - 25), Math.max(0, h - 25)), activeTheme.primary, 5, 78);
                Theme.paintOuterGlow(g2, new Rectangle2D.Float(22, 22, Math.max(0, w - 45), Math.max(0, h - 45)), activeTheme.secondary, 4, 54);
                g2.setColor(Theme.withAlpha(Color.BLACK, 120));
                g2.drawRect(18, 18, Math.max(0, w - 37), Math.max(0, h - 37));
                g2.setColor(Theme.withAlpha(activeTheme.secondary, 22));
                for (int y = 18; y < getHeight(); y += 18) {
                    g2.drawLine(16, y, getWidth() - 16, y);
                }
                g2.setColor(Theme.withAlpha(activeTheme.primary, 56));
                g2.drawRect(10, 10, Math.max(0, w - 21), Math.max(0, h - 21));
                g2.setColor(Theme.withAlpha(activeTheme.highlight, 75));
                g2.drawLine(34, 24, Math.max(34, w - 35), 24);
                g2.setColor(Theme.withAlpha(Color.WHITE, 18));
                g2.drawLine(28, 28, Math.max(28, w - 29), 28);
                g2.dispose();
            }
        };
        shell.setBackground(Theme.BG_MAIN);
        shell.setBorder(BorderFactory.createCompoundBorder(
            Theme.pixelBorder(activeTheme.secondary, activeTheme.primary, 3),
            BorderFactory.createEmptyBorder(16, 18, 16, 18)
        ));
        shell.add(buildTopBar(), BorderLayout.NORTH);
        shell.add(buildConsoleBody(), BorderLayout.CENTER);
        shell.add(buildActions(), BorderLayout.SOUTH);
        add(shell, BorderLayout.CENTER);
    }

    private JPanel buildTopBar() {
        JPanel topBar = new JPanel(new BorderLayout(10, 0)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                int w = getWidth();
                int h = getHeight();
                Rectangle2D.Float rail = new Rectangle2D.Float(8, 6, Math.max(0, w - 16), Math.max(0, h - 12));
                Theme.paintOuterGlow(g2, rail, activeTheme.primary, 4, 70);
                g2.setPaint(new GradientPaint(0, 0, activeTheme.panel, 0, h, activeTheme.deep));
                g2.fill(rail);
                g2.setColor(Theme.withAlpha(activeTheme.secondary, 150));
                g2.draw(rail);
                g2.setColor(Theme.withAlpha(Color.WHITE, 24));
                g2.drawLine(18, 11, Math.max(18, w - 19), 11);
                g2.setColor(Theme.withAlpha(activeTheme.highlight, 135));
                g2.fillRect(22, h - 12, 56, 3);
                g2.fillRect(Math.max(22, w - 78), h - 12, 56, 3);
                g2.dispose();
            }
        };
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        topBar.setPreferredSize(new Dimension(0, 72));

        btnMenu = Theme.makeButton("\u2630", activeTheme.secondary);
        btnMenu.setFont(Theme.FONT_TITLE.deriveFont(Font.BOLD, 22f));
        btnMenu.setPreferredSize(new Dimension(62, 48));
        btnMenu.addActionListener(e -> toggleSideMenu());

        lblNome = new JLabel("[ TAMA ]", SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                for (int i = 4; i >= 1; i--) {
                    g2.setColor(Theme.withAlpha(activeTheme.primary, 18 + i * 14));
                    g2.drawString(getText(), x - i, y);
                    g2.drawString(getText(), x + i, y);
                }
                g2.setColor(Theme.withAlpha(Color.BLACK, 180));
                g2.drawString(getText(), x + 2, y + 2);
                g2.setColor(Theme.TEXT);
                g2.drawString(getText(), x, y);
                g2.setColor(Theme.YELLOW);
                g2.fillRect(Math.max(0, x - 36), y - 12, 22, 3);
                g2.fillRect(Math.min(getWidth() - 22, x + fm.stringWidth(getText()) + 14), y - 12, 22, 3);
                g2.dispose();
            }
        };
        lblNome.setFont(Theme.FONT_TITLE.deriveFont(Font.BOLD, 30f));
        lblNome.setForeground(Theme.TEXT);
        lblNome.setOpaque(false);

        JPanel right = new JPanel(new BorderLayout(8, 0));
        right.setOpaque(false);
        lblClock = new JLabel("--:--", SwingConstants.CENTER);
        lblClock.setFont(Theme.FONT_HEADER.deriveFont(Font.BOLD, 16f));
        lblClock.setForeground(Theme.CYAN);
        lblClock.setBorder(BorderFactory.createCompoundBorder(
            Theme.pixelBorder(Theme.CYAN, Theme.BORDER_LO, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        btnSettings = Theme.makeButton("\u2699", activeTheme.primary);
        btnSettings.setFont(Theme.FONT_TITLE.deriveFont(Font.BOLD, 22f));
        btnSettings.setPreferredSize(new Dimension(62, 48));
        btnSettings.addActionListener(e -> toggleSettingsPanel());
        right.add(lblClock, BorderLayout.CENTER);
        right.add(btnSettings, BorderLayout.EAST);

        topBar.add(btnMenu, BorderLayout.WEST);
        topBar.add(lblNome, BorderLayout.CENTER);
        topBar.add(right, BorderLayout.EAST);
        return topBar;
    }

    private JPanel buildConsoleBody() {
        JPanel body = new JPanel(new BorderLayout(10, 0));
        body.setOpaque(false);
        sideMenu = buildSideMenu();
        settingsPanel = buildSettingsPanel();
        setAnimatedWidth(sideMenu, 0);
        setAnimatedWidth(settingsPanel, 0);
        body.add(sideMenu, BorderLayout.WEST);
        body.add(buildCenter(), BorderLayout.CENTER);
        body.add(settingsPanel, BorderLayout.EAST);
        return body;
    }

    private JPanel buildSideMenu() {
        JPanel menu = Theme.neonPanel(new BorderLayout(0, 10));
        menu.setBorder(BorderFactory.createCompoundBorder(
            Theme.pixelBorder(Theme.MAGENTA, Theme.PURPLE, 2),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JLabel title = new JLabel("TEMAS", SwingConstants.LEFT);
        title.setFont(Theme.FONT_HEADER.deriveFont(Font.BOLD, 17f));
        title.setForeground(Theme.TEXT);
        menu.add(title, BorderLayout.NORTH);

        themeListPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        themeListPanel.setOpaque(false);
        refreshThemeCards();
        menu.add(themeListPanel, BorderLayout.CENTER);

        JLabel hint = new JLabel("Sessao visual", SwingConstants.CENTER);
        hint.setFont(Theme.FONT_LABEL);
        hint.setForeground(Theme.TEXT_DIM);
        menu.add(hint, BorderLayout.SOUTH);
        return menu;
    }

    private JPanel buildSettingsPanel() {
        JPanel panel = Theme.neonPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            Theme.pixelBorder(Theme.CYAN, Theme.PURPLE, 2),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 10, 0);

        JLabel title = new JLabel("CONFIG", SwingConstants.LEFT);
        title.setFont(Theme.FONT_HEADER.deriveFont(Font.BOLD, 17f));
        title.setForeground(Theme.CYAN);
        gbc.gridy = 0;
        panel.add(title, gbc);

        gbc.gridy = 1;
        panel.add(settingsLine("Volume", "PREVISTO"), gbc);
        gbc.gridy = 2;
        panel.add(settingsLine("Efeitos sonoros", "PREVISTO"), gbc);
        gbc.gridy = 3;
        panel.add(settingsLine("Tema", "VISUAL"), gbc);
        gbc.gridy = 4;
        panel.add(settingsLine("Backup", "PREVISTO"), gbc);
        gbc.gridy = 5;
        panel.add(settingsLine("Exportacao", "PREVISTO"), gbc);

        JCheckBox anim = new JCheckBox("Animacoes");
        anim.setOpaque(false);
        anim.setFont(Theme.FONT_LABEL);
        anim.setForeground(Theme.TEXT);
        anim.setSelected(true);
        anim.addActionListener(e -> animationsEnabled = anim.isSelected());
        gbc.gridy = 6;
        panel.add(anim, gbc);

        JCheckBox scan = new JCheckBox("Scanlines");
        scan.setOpaque(false);
        scan.setFont(Theme.FONT_LABEL);
        scan.setForeground(Theme.TEXT);
        scan.setSelected(true);
        scan.addActionListener(e -> {
            scanlinesEnabled = scan.isSelected();
            petImagePanel.repaint();
        });
        gbc.gridy = 7;
        panel.add(scan, gbc);

        gbc.gridy = 8;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        panel.add(spacer, gbc);

        gbc.gridy = 9;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(settingsLine("Sobre", "Tamagotchi Cyber"), gbc);
        return panel;
    }

    private JPanel settingsLine(String label, String value) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createCompoundBorder(
            Theme.pixelBorder(Theme.NEON_PURPLE, Theme.BORDER_LO, 1),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        JLabel left = new JLabel(label);
        left.setFont(Theme.FONT_LABEL);
        left.setForeground(Theme.TEXT);
        JLabel right = new JLabel(value, SwingConstants.RIGHT);
        right.setFont(Theme.FONT_LABEL.deriveFont(Font.BOLD, 11f));
        right.setForeground(Theme.TEXT_DIM);
        row.add(left, BorderLayout.CENTER);
        row.add(right, BorderLayout.EAST);
        return row;
    }

    private void refreshThemeCards() {
        if (themeListPanel == null) {
            return;
        }
        themeListPanel.removeAll();
        for (VisualTheme theme : VisualTheme.values()) {
            themeListPanel.add(new ThemeCard(theme));
        }
        themeListPanel.revalidate();
        themeListPanel.repaint();
    }

    private void applyVisualTheme(VisualTheme theme) {
        activeTheme = theme;
        refreshThemeCards();
        if (btnMenu != null) {
            Theme.restyleButton(btnMenu, activeTheme.secondary, null, false);
        }
        if (btnSettings != null) {
            Theme.restyleButton(btnSettings, activeTheme.primary, null, false);
        }
        if (btnDormir != null) {
            Theme.restyleButton(btnDormir, activeTheme.secondary, Theme.ButtonGlyph.SLEEP, false);
        }
        if (btnCrud != null) {
            Theme.restyleButton(btnCrud, activeTheme.primary, Theme.ButtonGlyph.PAW, true);
        }
        revalidate();
        repaint();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new GridLayout(2, 1, 0, 4)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                int w = getWidth();
                int h = getHeight();
                Rectangle2D.Float titleBed = new Rectangle2D.Float(42, 8, Math.max(0, w - 84), Math.max(0, h - 24));
                Theme.paintOuterGlow(g2, titleBed, Theme.MAGENTA, 4, 92);
                g2.setPaint(new GradientPaint(0, 8, Theme.withAlpha(Theme.MAGENTA, 42), 0, h, Theme.withAlpha(Theme.NEON_PURPLE, 18)));
                g2.fill(titleBed);
                Theme.paintNeonLine(g2, 26, h - 3, Math.max(26, w - 27), h - 3, Theme.NEON_PURPLE);
                g2.setColor(Theme.HOT_PINK);
                paintHeaderPixels(g2, 18, 18);
                paintHeaderPixels(g2, w - 58, 18);
                g2.setColor(Theme.withAlpha(Theme.YELLOW, 105));
                g2.fillRect(64, 16, 28, 3);
                g2.fillRect(Math.max(64, w - 92), 16, 28, 3);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(8, 8, 10, 8));

        lblNome = new JLabel("[TAMA]", SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                for (int i = 5; i >= 1; i--) {
                    g2.setColor(Theme.withAlpha(Theme.MAGENTA, 18 + i * 12));
                    g2.drawString(getText(), x - i, y);
                    g2.drawString(getText(), x + i, y);
                    g2.drawString(getText(), x, y - i / 2);
                    g2.drawString(getText(), x, y + i / 2);
                }
                g2.setColor(Theme.withAlpha(Color.BLACK, 180));
                g2.drawString(getText(), x + 3, y + 3);
                g2.setColor(Theme.HOT_PINK);
                g2.drawString(getText(), x - 1, y);
                g2.setColor(Theme.TEXT);
                g2.drawString(getText(), x, y);
                g2.setColor(Theme.YELLOW);
                g2.fillRect(Math.max(0, x - 54), y - 14, 32, 4);
                g2.fillRect(Math.min(getWidth() - 32, x + fm.stringWidth(getText()) + 22), y - 14, 32, 4);
                g2.setColor(Theme.NEON_PURPLE);
                g2.fillRect(Math.max(0, x - 44), y - 5, 18, 3);
                g2.fillRect(Math.min(getWidth() - 18, x + fm.stringWidth(getText()) + 26), y - 5, 18, 3);
                g2.dispose();
            }
        };
        lblNome.setFont(Theme.FONT_TITLE);
        lblNome.setForeground(Theme.NEON_PURPLE);
        lblNome.setOpaque(false);

        lblTipo = new JLabel("Tipo: --", SwingConstants.CENTER);
        lblTipo.setFont(Theme.FONT_SUBTITLE);
        lblTipo.setForeground(Theme.TEXT_DIM);
        lblTipo.setOpaque(false);

        header.add(lblNome);
        header.add(lblTipo);
        return header;
    }

    private static void paintHeaderPixels(Graphics2D g2, int x, int y) {
        g2.fillRect(x, y, 8, 8);
        g2.fillRect(x + 12, y + 8, 8, 8);
        g2.fillRect(x + 24, y, 8, 8);
        g2.fillRect(x + 8, y + 22, 20, 4);
    }

    private void toggleSideMenu() {
        sideMenuOpen = !sideMenuOpen;
        animateWidth(sideMenu, sideMenuOpen ? SIDE_MENU_TARGET_WIDTH : 0, true);
    }

    private void toggleSettingsPanel() {
        settingsOpen = !settingsOpen;
        animateWidth(settingsPanel, settingsOpen ? SETTINGS_TARGET_WIDTH : 0, false);
    }

    private void animateWidth(JPanel panel, int target, boolean leftPanel) {
        Timer current = leftPanel ? sideMenuTimer : settingsTimer;
        if (current != null && current.isRunning()) {
            current.stop();
        }
        Timer timer = new Timer(12, null);
        timer.addActionListener(e -> {
            int currentWidth = leftPanel ? sideMenuWidth : settingsWidth;
            int step = target > currentWidth ? 18 : -18;
            int next = currentWidth + step;
            if ((step > 0 && next >= target) || (step < 0 && next <= target)) {
                next = target;
                timer.stop();
            }
            if (leftPanel) {
                sideMenuWidth = next;
            } else {
                settingsWidth = next;
            }
            setAnimatedWidth(panel, next);
            panel.getParent().revalidate();
            panel.getParent().repaint();
        });
        if (leftPanel) {
            sideMenuTimer = timer;
        } else {
            settingsTimer = timer;
        }
        timer.start();
    }

    private void setAnimatedWidth(JPanel panel, int width) {
        panel.setPreferredSize(new Dimension(width, 0));
        panel.setMinimumSize(new Dimension(width, 0));
        panel.setMaximumSize(new Dimension(width, Integer.MAX_VALUE));
    }

    private void startClock() {
        clockTimer = new Timer(1000, e -> lblClock.setText(LocalTime.now().format(CLOCK_FORMAT)));
        lblClock.setText(LocalTime.now().format(CLOCK_FORMAT));
        clockTimer.start();
    }

    private void startMicroEffects() {
        pulseTimer = new Timer(90, e -> {
            if (!animationsEnabled) {
                return;
            }
            visualPulse += 0.12f;
            if (visualPulse > Math.PI * 2) {
                visualPulse = 0f;
            }
            if (petImagePanel != null) {
                petImagePanel.setPulse(visualPulse);
            }
        });
        pulseTimer.start();
    }

    private void startNeedsTimer() {
        needsTimer = new Timer(60_000, e -> {
            try {
                controller.atualizarNecessidadesPorTempo();
                refreshView();
            } catch (SQLException ex) {
                needsTimer.stop();
                JOptionPane.showMessageDialog(this,
                    "Erro ao atualizar necessidades: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        needsTimer.start();
    }

    private JPanel buildCenter() {
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        gbc.gridy = 0;
        gbc.weighty = 0.61;
        gbc.insets = new Insets(0, 0, 8, 0);
        center.add(buildImageArea(), gbc);

        gbc.gridy = 1;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(0, 10, 8, 10);
        center.add(buildInfoPanel(), gbc);

        gbc.gridy = 2;
        gbc.weighty = 0.39;
        gbc.insets = new Insets(0, 0, 0, 0);
        center.add(buildStats(), gbc);

        return center;
    }

    private JPanel buildImageArea() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));

        petImagePanel = new PetImagePanel();
        petImagePanel.setPreferredSize(new Dimension(450, 350));
        petImagePanel.setMinimumSize(new Dimension(320, 255));
        wrapper.add(petImagePanel, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildInfoPanel() {
        JPanel info = Theme.neonPanel(new GridLayout(3, 1, 0, 4));
        info.setBorder(BorderFactory.createCompoundBorder(
            Theme.pixelBorder(Theme.CYAN, Theme.PURPLE, 1),
            BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        lblTipo = new JLabel("Tipo: --", SwingConstants.CENTER);
        lblTipo.setFont(Theme.FONT_SUBTITLE);
        lblTipo.setForeground(Theme.TEXT_DIM);

        lblEvolution = new JLabel("Estagio 0/5  ○ ○ ○ ○ ○", SwingConstants.CENTER);
        lblEvolution.setFont(Theme.FONT_STAT);
        lblEvolution.setForeground(Theme.CYAN);

        lblMood = new JLabel("Estado: SEM PET", SwingConstants.CENTER);
        lblMood.setFont(Theme.FONT_STAT.deriveFont(Font.BOLD, 14f));
        lblMood.setForeground(Theme.TEXT_DIM);

        info.add(lblTipo);
        info.add(lblEvolution);
        info.add(lblMood);
        return info;
    }

    private JPanel buildStats() {
        JPanel stats = Theme.neonPanel(new GridBagLayout());
        stats.setBackground(Theme.BG_PANEL);
        stats.setBorder(BorderFactory.createCompoundBorder(
            Theme.sectionBorder("  VITAIS  "),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        barHunger = Theme.makeProgressBar(Theme.HUNGER_CLR);
        barHappiness = Theme.makeProgressBar(Theme.HAPPINESS_CLR);
        barEnergy = Theme.makeProgressBar(Theme.ENERGY_CLR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 0, 4, 0);

        gbc.gridy = 0;
        stats.add(wrapStat("\u2665 FOME", barHunger, Theme.HUNGER_CLR), gbc);
        gbc.gridy = 1;
        stats.add(wrapStat("\u25C6 FELICIDADE", barHappiness, Theme.HAPPINESS_CLR), gbc);
        gbc.gridy = 2;
        stats.add(wrapStat("\u26A1 ENERGIA", barEnergy, Theme.ENERGY_CLR), gbc);
        return stats;
    }

    private JPanel wrapStat(String label, JProgressBar bar, Color color) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(Theme.FONT_STAT);
        lbl.setForeground(color);
        lbl.setPreferredSize(new Dimension(150, 30));
        row.add(lbl, BorderLayout.WEST);
        row.add(bar, BorderLayout.CENTER);
        return row;
    }

    private JPanel buildActions() {
        JPanel outer = new JPanel(new BorderLayout(0, 10));
        outer.setOpaque(false);
        outer.setBorder(BorderFactory.createEmptyBorder(14, 8, 0, 8));

        JPanel grid = new JPanel(new GridLayout(2, 2, 10, 10));
        grid.setOpaque(false);

        JButton btnAlimentar = Theme.makeButton("ALIMENTAR", Theme.HUNGER_CLR, Theme.ButtonGlyph.FEED);
        JButton btnBrincar = Theme.makeButton("BRINCAR", new Color(0x7A, 0x62, 0xFF), Theme.ButtonGlyph.PLAY);
        btnDormir = Theme.makeButton("DORMIR", activeTheme.secondary, Theme.ButtonGlyph.SLEEP);
        JButton btnExercitar = Theme.makeButton("EXERCITAR", Theme.ENERGY_CLR, Theme.ButtonGlyph.TRAIN);

        Dimension actionSize = new Dimension(180, 58);
        btnAlimentar.setPreferredSize(actionSize);
        btnBrincar.setPreferredSize(actionSize);
        btnDormir.setPreferredSize(actionSize);
        btnExercitar.setPreferredSize(actionSize);

        btnAlimentar.addActionListener(e -> applyAction("alimentar"));
        btnBrincar.addActionListener(e -> applyAction("brincar"));
        btnDormir.addActionListener(e -> applyAction("dormir"));
        btnExercitar.addActionListener(e -> applyAction("exercitar"));

        grid.add(btnAlimentar);
        grid.add(btnBrincar);
        grid.add(btnDormir);
        grid.add(btnExercitar);

        btnCrud = Theme.makePremiumButton("GERENCIAR  PETS  >", activeTheme.primary, Theme.ButtonGlyph.PAW);
        btnCrud.setPreferredSize(new Dimension(430, 68));
        btnCrud.addActionListener(e -> openCrud());

        outer.add(grid, BorderLayout.CENTER);
        outer.add(btnCrud, BorderLayout.SOUTH);
        return outer;
    }

    private void loadPet() {
        try {
            controller.carregarPet();
            refreshView();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Erro ao carregar pet: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshView() {
        Pet p = controller.getPetAtual();
        if (p == null) {
            lblNome.setText("[ TAMA ]");
            lblTipo.setText("Tipo: --");
            lblEvolution.setText("Estagio 0/5  ○ ○ ○ ○ ○");
            lblMood.setText("Estado: SEM PET");
            lblMood.setForeground(Theme.TEXT_DIM);
            barHunger.setValue(0);
            barHappiness.setValue(0);
            barEnergy.setValue(0);
            petImagePanel.setPetState(null, Theme.TEXT_DIM);
            petImagePanel.clear("SEM PET");
            return;
        }
        lblNome.setText("[ TAMA ]");
        lblTipo.setText("Tipo: " + (p.getTipoUsuario() != null ? p.getTipoUsuario() : "--"));
        int stage = deriveStage(p);
        lblEvolution.setText("Estagio " + stage + "/5  " + stageDots(stage));
        String mood = deriveMood(p);
        Color moodColor = moodColor(mood);
        lblMood.setText("Estado: " + mood);
        lblMood.setForeground(moodColor);
        barHunger.setValue(p.getHunger());
        barHappiness.setValue(p.getHappiness());
        barEnergy.setValue(p.getEnergy());
        petImagePanel.setPetState(p, moodColor);
        updateImage(p.getImageData());
    }

    private int deriveStage(Pet p) {
        int average = (p.getHunger() + p.getHappiness() + p.getEnergy()) / 3;
        if (average >= 86) return 5;
        if (average >= 68) return 4;
        if (average >= 50) return 3;
        if (average >= 30) return 2;
        return 1;
    }

    private String stageDots(int stage) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            if (i > 1) sb.append(' ');
            sb.append(i <= stage ? '●' : '○');
        }
        return sb.toString();
    }

    private String deriveMood(Pet p) {
        int average = (p.getHunger() + p.getHappiness() + p.getEnergy()) / 3;
        if (average >= 90) return "Radiante";
        if (average >= 70) return "Feliz";
        if (average >= 50) return "Bem";
        if (average >= 30) return "Cansado";
        if (average >= 15) return "Triste";
        return "Critico";
    }

    private Color moodColor(String mood) {
        switch (mood) {
            case "Radiante": return Theme.CYAN;
            case "Critico": return Theme.HUNGER_CLR;
            case "Cansado": return Theme.NEON_PURPLE;
            case "Triste": return Theme.HOT_PINK;
            case "Feliz": return Theme.HAPPINESS_CLR;
            case "Bem": return Theme.GREEN;
            default: return Theme.TEXT_DIM;
        }
    }

    private void updateImage(byte[] data) {
        if (data == null || data.length == 0) {
            petImagePanel.clear("SEM IMAGEM");
            return;
        }
        try {
            BufferedImage bi = ImageIO.read(new ByteArrayInputStream(data));
            if (bi == null) throw new IOException("Formato de imagem nao suportado");
            petImagePanel.setImage(bi);
        } catch (IOException ex) {
            petImagePanel.clear("IMAGEM INVALIDA");
        }
    }

    private void applyAction(String action) {
        if (controller.getPetAtual() == null) {
            JOptionPane.showMessageDialog(this,
                "Nenhum pet ativo. Crie um pet em Gerenciar Pets.",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            switch (action) {
                case "alimentar": controller.alimentar(); break;
                case "brincar": controller.brincar(); break;
                case "dormir": controller.dormir(); break;
                case "exercitar": controller.exercitar(); break;
            }
            petImagePanel.flashAction();
            refreshView();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Erro ao aplicar acao: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openCrud() {
        PetCrudDialog dlg = new PetCrudDialog(this, controller);
        dlg.setVisible(true);
        refreshView();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Theme.apply();
            new MainFrame().setVisible(true);
        });
    }

    private class ThemeCard extends JPanel {
        private final VisualTheme theme;

        ThemeCard(VisualTheme theme) {
            this.theme = theme;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                    applyVisualTheme(theme);
                }
            });
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth();
            int h = getHeight();
            boolean active = theme == activeTheme;
            Rectangle2D.Float card = new Rectangle2D.Float(3, 3, Math.max(0, w - 7), Math.max(0, h - 7));
            Theme.paintOuterGlow(g2, card, active ? theme.primary : Theme.BORDER_LO, active ? 4 : 2, active ? 78 : 34);
            g2.setPaint(new GradientPaint(0, 0, new Color(0x13, 0x07, 0x26), 0, h, new Color(0x08, 0x03, 0x12)));
            g2.fill(card);
            g2.setColor(active ? theme.primary : Theme.BORDER_LO);
            g2.draw(card);

            int tx = 14;
            int ty = 14;
            g2.setPaint(new GradientPaint(tx, ty, theme.primary, tx + 70, ty + 38, theme.secondary));
            g2.fillRect(tx, ty, 72, 38);
            g2.setColor(Theme.withAlpha(Color.BLACK, 80));
            for (int y = ty + 5; y < ty + 38; y += 7) {
                g2.drawLine(tx + 5, y, tx + 67, y);
            }
            g2.setColor(Theme.TEXT);
            g2.setFont(Theme.FONT_LABEL.deriveFont(Font.BOLD, 13f));
            g2.drawString(theme.label, 96, 33);
            if (active) {
                g2.setColor(Theme.GREEN);
                g2.fillRect(w - 26, 18, 9, 9);
                g2.setColor(Theme.withAlpha(Theme.GREEN, 100));
                g2.drawRect(w - 29, 15, 14, 14);
            }
            g2.dispose();
        }
    }

    private class PetImagePanel extends JPanel {
        private BufferedImage image;
        private String emptyText = "SEM IMAGEM";
        private int hunger;
        private int happiness;
        private int energy;
        private Color moodGlow = Theme.TEXT_DIM;
        private float pulse;
        private int flash;

        PetImagePanel() {
            setBackground(Theme.BG_CARD);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        }

        void setImage(BufferedImage image) {
            this.image = image;
            repaint();
        }

        void clear(String text) {
            this.image = null;
            this.emptyText = text;
            repaint();
        }

        void setPetState(Pet pet, Color moodColor) {
            if (pet == null) {
                hunger = 0;
                happiness = 0;
                energy = 0;
            } else {
                hunger = pet.getHunger();
                happiness = pet.getHappiness();
                energy = pet.getEnergy();
            }
            moodGlow = moodColor != null ? moodColor : Theme.TEXT_DIM;
            repaint();
        }

        void setPulse(float pulse) {
            this.pulse = pulse;
            if (flash > 0) {
                flash--;
            }
            repaint();
        }

        void flashAction() {
            flash = 8;
            repaint();
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth();
            int h = getHeight();
            Rectangle screen = paintMonitor(g2, w, h);

            Shape oldClip = g2.getClip();
            g2.setClip(screen);
            if (image != null) {
                double scale = Math.min((screen.width - 18) / (double) image.getWidth(), (screen.height - 18) / (double) image.getHeight());
                int iw = Math.max(1, (int) Math.round(image.getWidth() * scale));
                int ih = Math.max(1, (int) Math.round(image.getHeight() * scale));
                int x = screen.x + (screen.width - iw) / 2;
                int y = screen.y + (screen.height - ih) / 2;
                applyImageRenderingHints(g2, image, scale);
                g2.drawImage(image, x, y, iw, ih, null);
            } else {
                paintPlaceholder(g2, screen);
            }
            g2.setClip(oldClip);

            paintGlass(g2, screen);
            paintFrameDetails(g2, w, h, screen);
            g2.dispose();
        }

        private void applyImageRenderingHints(Graphics2D g2, BufferedImage source, double scale) {
            boolean tinySprite = source.getWidth() <= 96 && source.getHeight() <= 96 && scale >= 2.0;
            if (tinySprite) {
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            } else {
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            }
        }

        private Rectangle paintMonitor(Graphics2D g2, int w, int h) {
            Rectangle2D.Float outer = new Rectangle2D.Float(14, 12, Math.max(0, w - 28), Math.max(0, h - 24));
            int pulseAlpha = animationsEnabled ? (int) (18 + Math.sin(pulse) * 14) : 10;
            Theme.paintOuterGlow(g2, outer, moodGlow, 6, 88 + pulseAlpha + flash * 6);
            Theme.paintOuterGlow(g2, new Rectangle2D.Float(24, 22, Math.max(0, w - 48), Math.max(0, h - 44)), activeTheme.secondary, 4, 70 + pulseAlpha);

            g2.setColor(Theme.withAlpha(Color.BLACK, 155));
            g2.fillRect(18, 18, Math.max(0, w - 30), Math.max(0, h - 28));
            g2.setPaint(new GradientPaint(0, 18, activeTheme.panel, 0, h - 18, activeTheme.deep));
            g2.fillRect(18, 14, Math.max(0, w - 36), Math.max(0, h - 32));

            g2.setColor(Theme.withAlpha(activeTheme.primary, 190));
            g2.drawRect(18, 16, Math.max(0, w - 37), Math.max(0, h - 33));
            g2.setColor(Theme.withAlpha(activeTheme.secondary, 220));
            g2.drawRect(25, 23, Math.max(0, w - 51), Math.max(0, h - 47));
            g2.setColor(Theme.withAlpha(Color.WHITE, 24));
            g2.drawLine(28, 27, Math.max(28, w - 29), 27);
            g2.setColor(Theme.withAlpha(Color.BLACK, 120));
            g2.drawLine(28, h - 26, Math.max(28, w - 29), h - 26);

            Rectangle screen = new Rectangle(38, 48, Math.max(1, w - 76), Math.max(1, h - 104));
            Theme.paintOuterGlow(g2, new Rectangle2D.Float(screen.x, screen.y, screen.width, screen.height), activeTheme.highlight, 4, 66);
            g2.setPaint(new GradientPaint(screen.x, screen.y, activeTheme.deep.brighter(), screen.x, screen.y + screen.height, new Color(0x04, 0x01, 0x0B)));
            g2.fillRect(screen.x, screen.y, screen.width, screen.height);
            g2.setColor(Theme.withAlpha(Theme.CYAN, 18));
            g2.fillRect(screen.x + 7, screen.y + 7, Math.max(0, screen.width - 14), Math.max(0, screen.height - 14));
            g2.setColor(Theme.withAlpha(Color.BLACK, 110));
            g2.drawRect(screen.x + 4, screen.y + 4, Math.max(0, screen.width - 9), Math.max(0, screen.height - 9));

            g2.setFont(Theme.FONT_LABEL.deriveFont(Font.BOLD, 11f));
            g2.setColor(Theme.withAlpha(Theme.CYAN, 170));
            g2.drawString("PET-LINK", 42, 35);
            g2.setColor(Theme.withAlpha(moodGlow, 185));
            g2.drawString("SIGNAL", Math.max(42, w - 94), 35);
            return screen;
        }

        private void paintPlaceholder(Graphics2D g2, Rectangle screen) {
            int cx = screen.x + screen.width / 2;
            int cy = screen.y + screen.height / 2 - 22;
            Theme.paintOuterGlow(g2, new Rectangle2D.Float(cx - 48, cy - 34, 96, 72), activeTheme.primary, 4, 58);
            g2.setColor(Theme.withAlpha(Theme.TEXT_DIM, 72));
            g2.fillRect(cx - 42, cy - 28, 84, 58);
            g2.setColor(Theme.BG_CARD);
            g2.fillRect(cx - 30, cy - 16, 60, 34);
            g2.setColor(activeTheme.primary);
            g2.drawRect(cx - 44, cy - 30, 88, 62);
            g2.setColor(Theme.CYAN);
            g2.fillRect(cx - 14, cy - 8, 8, 8);
            g2.fillRect(cx + 8, cy - 8, 8, 8);
            g2.setColor(Theme.HOT_PINK);
            g2.fillRect(cx - 18, cy + 14, 36, 4);
            g2.setColor(Theme.withAlpha(Theme.YELLOW, 150));
            g2.drawString("EMPTY SLOT", cx - 36, cy - 42);

            g2.setFont(Theme.FONT_HEADER.deriveFont(Font.BOLD, 19f));
            FontMetrics fm = g2.getFontMetrics();
            int tx = screen.x + (screen.width - fm.stringWidth(emptyText)) / 2;
            int ty = cy + 66;
            g2.setColor(Theme.withAlpha(activeTheme.secondary, 100));
            g2.drawString(emptyText, tx - 1, ty);
            g2.drawString(emptyText, tx + 1, ty);
            g2.setColor(Theme.TEXT);
            g2.drawString(emptyText, tx, ty);

            String sub = "Adicione uma imagem no arquivo de pets";
            if ("SEM PET".equals(emptyText)) {
                sub = "Clique em Gerenciar Pets para criar";
            }
            g2.setFont(Theme.FONT_LABEL);
            FontMetrics sfm = g2.getFontMetrics();
            int sx = screen.x + (screen.width - sfm.stringWidth(sub)) / 2;
            g2.setColor(Theme.TEXT_DIM);
            g2.drawString(sub, sx, ty + 24);
        }

        private void paintGlass(Graphics2D g2, Rectangle screen) {
            if (scanlinesEnabled) {
                g2.setColor(Theme.withAlpha(activeTheme.primary, 18));
                for (int y = screen.y + 5; y < screen.y + screen.height - 4; y += 8) {
                    g2.drawLine(screen.x + 8, y, screen.x + screen.width - 8, y);
                }
            }
            g2.setColor(Theme.withAlpha(Theme.CYAN, 18));
            Polygon reflection = new Polygon();
            reflection.addPoint(screen.x + screen.width / 2, screen.y + 6);
            reflection.addPoint(screen.x + screen.width - 10, screen.y + 6);
            reflection.addPoint(screen.x + screen.width / 2 + 20, screen.y + screen.height - 12);
            reflection.addPoint(screen.x + screen.width / 2 - 40, screen.y + screen.height - 12);
            g2.fillPolygon(reflection);
            g2.setColor(Theme.withAlpha(Color.BLACK, 105));
            g2.drawRect(screen.x, screen.y, screen.width - 1, screen.height - 1);
            g2.setColor(Theme.withAlpha(activeTheme.secondary, 175));
            g2.drawRect(screen.x + 2, screen.y + 2, screen.width - 5, screen.height - 5);
        }

        private void paintFrameDetails(Graphics2D g2, int w, int h, Rectangle screen) {
            int s = 32;
            g2.setColor(activeTheme.highlight);
            g2.fillRect(20, 18, s, 3);
            g2.fillRect(20, 18, 3, s);
            g2.fillRect(w - 20 - s, 18, s, 3);
            g2.fillRect(w - 23, 18, 3, s);
            g2.fillRect(20, h - 21, s, 3);
            g2.fillRect(20, h - 18 - s, 3, s);
            g2.fillRect(w - 20 - s, h - 21, s, 3);
            g2.fillRect(w - 23, h - 18 - s, 3, s);

            g2.setColor(activeTheme.secondary);
            g2.fillRect(screen.x - 10, screen.y + 16, 5, 42);
            g2.fillRect(screen.x + screen.width + 5, screen.y + screen.height - 58, 5, 42);
            paintLed(g2, w / 2 - 34, h - 40, Theme.HUNGER_CLR, hunger);
            paintLed(g2, w / 2 - 8, h - 40, Theme.HAPPINESS_CLR, happiness);
            paintLed(g2, w / 2 + 18, h - 40, Theme.ENERGY_CLR, energy);
            Theme.paintNeonLine(g2, 46, h - 34, Math.max(46, w / 2 - 46), h - 34, activeTheme.primary);
            Theme.paintNeonLine(g2, Math.min(w - 46, w / 2 + 46), h - 34, Math.max(46, w - 47), h - 34, activeTheme.secondary);
        }

        private void paintLed(Graphics2D g2, int x, int y, Color color, int value) {
            int alpha = value <= 25 && animationsEnabled
                ? (int) (80 + Math.abs(Math.sin(pulse * 2.0)) * 120)
                : 150;
            Theme.paintOuterGlow(g2, new Rectangle2D.Float(x, y, 10, 10), color, 3, alpha);
            g2.setColor(Theme.withAlpha(color, alpha));
            g2.fillRect(x, y, 10, 10);
            g2.setColor(Theme.withAlpha(Color.WHITE, 55));
            g2.fillRect(x + 2, y + 2, 3, 3);
        }
    }
}
