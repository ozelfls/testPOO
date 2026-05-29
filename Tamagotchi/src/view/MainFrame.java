package view;

import controller.GameController;
import model.Pet;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;

public class MainFrame extends JFrame {

    private final GameController controller = new GameController();

    private JLabel lblNome;
    private JLabel lblTipo;
    private JProgressBar barHunger;
    private JProgressBar barHappiness;
    private JProgressBar barEnergy;
    private PetImagePanel petImagePanel;

    public MainFrame() {
        super("Tamagotchi Cyber");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        getContentPane().setBackground(Theme.BG_MAIN);
        setLayout(new BorderLayout(0, 0));
        buildUi();
        loadPet();
        pack();
        setMinimumSize(new Dimension(560, 720));
        setLocationRelativeTo(null);
    }

    private void buildUi() {
        JPanel shell = new JPanel(new BorderLayout(0, 14)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(Theme.withAlpha(Theme.NEON_PURPLE, 26));
                for (int y = 18; y < getHeight(); y += 18) {
                    g2.drawLine(16, y, getWidth() - 16, y);
                }
                g2.setColor(Theme.withAlpha(Theme.MAGENTA, 30));
                g2.drawRect(10, 10, getWidth() - 21, getHeight() - 21);
                g2.dispose();
            }
        };
        shell.setBackground(Theme.BG_MAIN);
        shell.setBorder(BorderFactory.createCompoundBorder(
            Theme.pixelBorder(Theme.NEON_PURPLE, Theme.PURPLE, 3),
            BorderFactory.createEmptyBorder(16, 18, 16, 18)
        ));
        shell.add(buildHeader(), BorderLayout.NORTH);
        shell.add(buildCenter(), BorderLayout.CENTER);
        shell.add(buildActions(), BorderLayout.SOUTH);
        add(shell, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new GridLayout(2, 1, 0, 4)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(Theme.withAlpha(Theme.NEON_PURPLE, 70));
                g2.drawLine(26, getHeight() - 2, getWidth() - 26, getHeight() - 2);
                g2.setColor(Theme.withAlpha(Theme.MAGENTA, 34));
                g2.fillRect(28, 10, getWidth() - 56, getHeight() - 22);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(8, 8, 10, 8));

        lblNome = new JLabel("[TAMA]", SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setFont(getFont());
                g2.setColor(Theme.withAlpha(Theme.NEON_PURPLE, 115));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), x - 2, y);
                g2.drawString(getText(), x + 2, y);
                g2.dispose();
                super.paintComponent(g);
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

    private JPanel buildCenter() {
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        gbc.gridy = 0;
        gbc.weighty = 0.58;
        gbc.insets = new Insets(0, 0, 12, 0);
        center.add(buildImageArea(), gbc);

        gbc.gridy = 1;
        gbc.weighty = 0.42;
        gbc.insets = new Insets(0, 0, 0, 0);
        center.add(buildStats(), gbc);

        return center;
    }

    private JPanel buildImageArea() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(4, 16, 4, 16));

        petImagePanel = new PetImagePanel();
        petImagePanel.setPreferredSize(new Dimension(420, 330));
        petImagePanel.setMinimumSize(new Dimension(300, 240));
        wrapper.add(petImagePanel, BorderLayout.CENTER);
        return wrapper;
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

        JButton btnAlimentar = Theme.makeButton("\uD83C\uDF56 ALIMENTAR", Theme.HUNGER_CLR);
        JButton btnBrincar = Theme.makeButton("\uD83C\uDFAE BRINCAR", Theme.HAPPINESS_CLR);
        JButton btnDormir = Theme.makeButton("\u263E DORMIR", Theme.NEON_PURPLE);
        JButton btnExercitar = Theme.makeButton("\u25B0 EXERCITAR", Theme.ENERGY_CLR);

        Dimension actionSize = new Dimension(180, 54);
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

        JButton btnCrud = Theme.makeButton("\uD83D\uDC3E GERENCIAR PETS", Theme.MAGENTA);
        btnCrud.setPreferredSize(new Dimension(420, 58));
        btnCrud.setFont(Theme.FONT_BUTTON.deriveFont(Font.BOLD, 15f));
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
            lblNome.setText("[TAMA]");
            lblTipo.setText("Tipo: --");
            barHunger.setValue(0);
            barHappiness.setValue(0);
            barEnergy.setValue(0);
            petImagePanel.clear("SEM PET");
            return;
        }
        lblNome.setText("[TAMA]");
        lblTipo.setText("Tipo: " + (p.getTipoUsuario() != null ? p.getTipoUsuario() : "--"));
        barHunger.setValue(p.getHunger());
        barHappiness.setValue(p.getHappiness());
        barEnergy.setValue(p.getEnergy());
        updateImage(p.getImageData());
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

    private static class PetImagePanel extends JPanel {
        private BufferedImage image;
        private String emptyText = "SEM IMAGEM";

        PetImagePanel() {
            setBackground(Theme.BG_CARD);
            setBorder(Theme.pixelBorder(Theme.NEON_PURPLE, Theme.MAGENTA, 3));
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

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth();
            int h = getHeight();

            g2.setColor(Theme.withAlpha(Theme.NEON_PURPLE, 34));
            g2.fillRect(14, 14, Math.max(0, w - 28), Math.max(0, h - 28));
            g2.setColor(Theme.withAlpha(Theme.MAGENTA, 28));
            for (int y = 20; y < h - 20; y += 10) {
                g2.drawLine(20, y, w - 20, y);
            }

            if (image != null) {
                double scale = Math.min((w - 58) / (double) image.getWidth(), (h - 58) / (double) image.getHeight());
                int iw = Math.max(1, (int) Math.round(image.getWidth() * scale));
                int ih = Math.max(1, (int) Math.round(image.getHeight() * scale));
                int x = (w - iw) / 2;
                int y = (h - ih) / 2;
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                g2.drawImage(image, x, y, iw, ih, null);
            } else {
                paintPlaceholder(g2, w, h);
            }

            paintCorners(g2, w, h);
            g2.dispose();
        }

        private void paintPlaceholder(Graphics2D g2, int w, int h) {
            int cx = w / 2;
            int cy = h / 2 - 16;
            g2.setColor(Theme.withAlpha(Theme.TEXT_DIM, 85));
            g2.fillRect(cx - 34, cy - 22, 68, 44);
            g2.setColor(Theme.BG_CARD);
            g2.fillRect(cx - 24, cy - 12, 48, 24);
            g2.setColor(Theme.MAGENTA);
            g2.drawRect(cx - 36, cy - 24, 72, 48);
            g2.setColor(Theme.NEON_PURPLE);
            g2.fillRect(cx - 7, cy - 6, 6, 6);
            g2.fillRect(cx + 7, cy - 6, 6, 6);
            g2.fillRect(cx - 12, cy + 10, 24, 4);

            g2.setFont(Theme.FONT_HEADER.deriveFont(Font.BOLD, 18f));
            FontMetrics fm = g2.getFontMetrics();
            int tx = (w - fm.stringWidth(emptyText)) / 2;
            int ty = cy + 62;
            g2.setColor(Theme.withAlpha(Theme.NEON_PURPLE, 100));
            g2.drawString(emptyText, tx - 1, ty);
            g2.drawString(emptyText, tx + 1, ty);
            g2.setColor(Theme.TEXT);
            g2.drawString(emptyText, tx, ty);
        }

        private void paintCorners(Graphics2D g2, int w, int h) {
            int s = 28;
            g2.setColor(Theme.NEON_PURPLE);
            g2.fillRect(18, 18, s, 3);
            g2.fillRect(18, 18, 3, s);
            g2.fillRect(w - 18 - s, 18, s, 3);
            g2.fillRect(w - 21, 18, 3, s);
            g2.fillRect(18, h - 21, s, 3);
            g2.fillRect(18, h - 18 - s, 3, s);
            g2.fillRect(w - 18 - s, h - 21, s, 3);
            g2.fillRect(w - 21, h - 18 - s, 3, s);
        }
    }
}
