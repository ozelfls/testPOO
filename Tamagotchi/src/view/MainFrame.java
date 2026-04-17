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
    private JLabel lblImage;

    public MainFrame() {
        super("Tamagotchi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        getContentPane().setBackground(Theme.BG_MAIN);
        setLayout(new BorderLayout(0, 0));
        buildUi();
        loadPet();
        pack();
        setMinimumSize(new Dimension(540, 640));
        setLocationRelativeTo(null);
    }

    // -------------------------------------------------------------------------
    // UI construction
    // -------------------------------------------------------------------------

    private void buildUi() {
        add(buildHeader(),  BorderLayout.NORTH);
        add(buildCenter(),  BorderLayout.CENTER);
        add(buildActions(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new GridLayout(2, 1, 0, 6)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0,           new Color(58, 38, 108),
                    0, getHeight(), new Color(18, 12, 42)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setOpaque(true);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, Theme.ACCENT),
            BorderFactory.createEmptyBorder(18, 24, 18, 24)
        ));

        lblNome = new JLabel("TAMAGOTCHI", SwingConstants.CENTER);
        lblNome.setFont(Theme.FONT_TITLE);
        lblNome.setForeground(Theme.ACCENT);
        lblNome.setOpaque(false);

        lblTipo = new JLabel("Carregando...", SwingConstants.CENTER);
        lblTipo.setFont(Theme.FONT_SUBTITLE);
        lblTipo.setForeground(Theme.TEXT_DIM);
        lblTipo.setOpaque(false);

        header.add(lblNome);
        header.add(lblTipo);
        return header;
    }

    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 14));
        center.setBackground(Theme.BG_MAIN);
        center.setBorder(BorderFactory.createEmptyBorder(20, 26, 12, 26));
        center.add(buildImageArea(), BorderLayout.CENTER);
        center.add(buildStats(),     BorderLayout.SOUTH);
        return center;
    }

    private JPanel buildImageArea() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Theme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.ACCENT, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        lblImage = new JLabel("Sem imagem", SwingConstants.CENTER);
        lblImage.setFont(Theme.FONT_LABEL);
        lblImage.setForeground(Theme.TEXT_DIM);
        lblImage.setPreferredSize(new Dimension(240, 240));
        lblImage.setBackground(Theme.BG_CARD);
        lblImage.setOpaque(true);
        card.add(lblImage, BorderLayout.CENTER);

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapper.setBackground(Theme.BG_MAIN);
        wrapper.add(card);
        return wrapper;
    }

    private JPanel buildStats() {
        JPanel stats = new JPanel(new GridLayout(3, 1, 0, 8));
        stats.setBackground(Theme.BG_MAIN);
        stats.setBorder(BorderFactory.createCompoundBorder(
            Theme.sectionBorder("  VITAIS  "),
            BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));

        barHunger    = Theme.makeProgressBar(Theme.HUNGER_CLR);
        barHappiness = Theme.makeProgressBar(Theme.HAPPINESS_CLR);
        barEnergy    = Theme.makeProgressBar(Theme.ENERGY_CLR);

        stats.add(wrapStat("FOME       ", barHunger));
        stats.add(wrapStat("FELICIDADE ", barHappiness));
        stats.add(wrapStat("ENERGIA    ", barEnergy));
        return stats;
    }

    private JPanel wrapStat(String label, JProgressBar bar) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(Theme.BG_MAIN);

        JLabel lbl = new JLabel(label);
        lbl.setFont(Theme.FONT_STAT);
        lbl.setForeground(Theme.TEXT_DIM);
        lbl.setPreferredSize(new Dimension(122, 30));
        lbl.setBackground(Theme.BG_MAIN);
        lbl.setOpaque(true);

        row.add(lbl, BorderLayout.WEST);
        row.add(bar, BorderLayout.CENTER);
        return row;
    }

    private JPanel buildActions() {
        JPanel outer = new JPanel(new BorderLayout(0, 8));
        outer.setBackground(Theme.BG_MAIN);
        outer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, Theme.ACCENT.darker()),
            BorderFactory.createEmptyBorder(14, 16, 16, 16)
        ));

        JPanel row1 = new JPanel(new GridLayout(1, 4, 8, 0));
        row1.setBackground(Theme.BG_MAIN);

        JButton btnAlimentar = Theme.makeButton("ALIMENTAR", Theme.HUNGER_CLR);
        JButton btnBrincar   = Theme.makeButton("BRINCAR",   Theme.HAPPINESS_CLR);
        JButton btnDormir    = Theme.makeButton("DORMIR",    Theme.ENERGY_CLR);
        JButton btnExercitar = Theme.makeButton("EXERCITAR", Theme.ACCENT2);

        btnAlimentar.addActionListener(e -> applyAction("alimentar"));
        btnBrincar.addActionListener(e   -> applyAction("brincar"));
        btnDormir.addActionListener(e    -> applyAction("dormir"));
        btnExercitar.addActionListener(e -> applyAction("exercitar"));

        row1.add(btnAlimentar);
        row1.add(btnBrincar);
        row1.add(btnDormir);
        row1.add(btnExercitar);

        JPanel row2 = new JPanel(new BorderLayout());
        row2.setBackground(Theme.BG_MAIN);

        JButton btnCrud = Theme.makeButton("GERENCIAR PETS", Theme.ACCENT);
        btnCrud.addActionListener(e -> openCrud());
        row2.add(btnCrud, BorderLayout.CENTER);

        outer.add(row1, BorderLayout.CENTER);
        outer.add(row2, BorderLayout.SOUTH);
        return outer;
    }

    // -------------------------------------------------------------------------
    // Controller bridge
    // -------------------------------------------------------------------------

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
            lblNome.setText("TAMAGOTCHI");
            lblTipo.setText("Nenhum pet cadastrado");
            barHunger.setValue(0);
            barHappiness.setValue(0);
            barEnergy.setValue(0);
            lblImage.setText("Sem pet");
            lblImage.setIcon(null);
            return;
        }
        lblNome.setText("[ " + p.getNome().toUpperCase() + " ]");
        lblTipo.setText("Tipo: " + (p.getTipoUsuario() != null ? p.getTipoUsuario() : "--"));
        barHunger.setValue(p.getHunger());
        barHappiness.setValue(p.getHappiness());
        barEnergy.setValue(p.getEnergy());
        updateImage(p.getImageData());
    }

    private void updateImage(byte[] data) {
        if (data == null || data.length == 0) {
            lblImage.setIcon(null);
            lblImage.setText("Sem imagem");
            return;
        }
        try {
            BufferedImage bi = ImageIO.read(new ByteArrayInputStream(data));
            if (bi == null) throw new IOException("Formato de imagem não suportado");
            Image img = bi.getScaledInstance(240, 240, Image.SCALE_SMOOTH);
            lblImage.setIcon(new ImageIcon(img));
            lblImage.setText("");
        } catch (IOException ex) {
            lblImage.setIcon(null);
            lblImage.setText("Imagem inválida");
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
                case "alimentar": controller.alimentar();  break;
                case "brincar":   controller.brincar();    break;
                case "dormir":    controller.dormir();     break;
                case "exercitar": controller.exercitar();  break;
            }
            refreshView();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Erro ao aplicar ação: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openCrud() {
        PetCrudDialog dlg = new PetCrudDialog(this, controller);
        dlg.setVisible(true);
        refreshView();
    }

    // -------------------------------------------------------------------------
    // Entry point
    // -------------------------------------------------------------------------

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Theme.apply();
            new MainFrame().setVisible(true);
        });
    }
}
