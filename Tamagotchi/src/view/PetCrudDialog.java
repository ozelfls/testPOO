package view;

import controller.GameController;
import dao.PetDAO;
import model.Pet;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.List;

public class PetCrudDialog extends JDialog {

    private final GameController controller;
    private final PetDAO petDAO = new PetDAO();

    private JTable table;
    private DefaultTableModel tableModel;

    public PetCrudDialog(Frame owner, GameController controller) {
        super(owner, "Gerenciar Pets", true);
        this.controller = controller;
        setMinimumSize(new Dimension(720, 480));
        setSize(760, 540);
        setResizable(true);
        setLocationRelativeTo(owner);
        getContentPane().setBackground(Theme.BG_MAIN);
        buildUi();
        loadPets();
    }

    // -------------------------------------------------------------------------
    // UI construction
    // -------------------------------------------------------------------------

    private void buildUi() {
        setLayout(new BorderLayout(0, 0));
        add(buildHeader(),  BorderLayout.NORTH);
        add(buildTable(),   BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
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
            BorderFactory.createEmptyBorder(14, 20, 14, 20)
        ));

        JLabel title = new JLabel("GERENCIAR PETS", SwingConstants.LEFT);
        title.setFont(Theme.FONT_HEADER);
        title.setForeground(Theme.ACCENT);
        title.setOpaque(false);

        JLabel hint = new JLabel("Selecione um pet para editar, excluir ou usar.", SwingConstants.RIGHT);
        hint.setFont(Theme.FONT_SUBTITLE);
        hint.setForeground(Theme.TEXT_DIM);
        hint.setOpaque(false);

        header.add(title, BorderLayout.WEST);
        header.add(hint,  BorderLayout.EAST);
        return header;
    }

    private JScrollPane buildTable() {
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nome", "Tipo"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setBackground(Theme.TABLE_ODD);
        table.setForeground(Theme.TEXT);
        table.setFont(Theme.FONT_TABLE);
        table.setRowHeight(30);
        table.setShowVerticalLines(false);
        table.setGridColor(Theme.BORDER_LO);
        table.setSelectionBackground(Theme.TABLE_SEL);
        table.setSelectionForeground(Theme.TEXT);
        table.setDefaultRenderer(Object.class, new StripedRenderer());

        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(0).setMaxWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(260);
        table.getColumnModel().getColumn(2).setPreferredWidth(320);

        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setBackground(Theme.TABLE_HEADER);
        tableHeader.setForeground(Theme.ACCENT2);
        tableHeader.setFont(Theme.FONT_HEADER);
        tableHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Theme.ACCENT2));
        tableHeader.setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Theme.TABLE_ODD);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setBackground(Theme.BG_MAIN);
        return scroll;
    }

    private JPanel buildButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panel.setBackground(Theme.BG_MAIN);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, Theme.ACCENT.darker()),
            BorderFactory.createEmptyBorder(12, 16, 14, 16)
        ));

        JButton btnNovo    = Theme.makeButton("NOVO",      Theme.ACCENT);
        JButton btnEditar  = Theme.makeButton("EDITAR",    Theme.ACCENT2);
        JButton btnExcluir = Theme.makeButton("EXCLUIR",   Theme.HUNGER_CLR);
        JButton btnUsar    = Theme.makeButton("USAR ESTE", Theme.ENERGY_CLR);

        btnNovo.addActionListener(e    -> novoPet());
        btnEditar.addActionListener(e  -> editarPet());
        btnExcluir.addActionListener(e -> excluirPet());
        btnUsar.addActionListener(e    -> usarPet());

        panel.add(btnNovo);
        panel.add(btnEditar);
        panel.add(btnExcluir);
        panel.add(btnUsar);
        return panel;
    }

    // -------------------------------------------------------------------------
    // Table striped renderer
    // -------------------------------------------------------------------------

    private static class StripedRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int col) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            setFont(Theme.FONT_TABLE);
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            if (isSelected) {
                setBackground(Theme.TABLE_SEL);
                setForeground(Theme.TEXT);
            } else {
                setBackground(row % 2 == 0 ? Theme.TABLE_ODD : Theme.TABLE_EVEN);
                setForeground(col == 0 ? Theme.TEXT_DIM : Theme.TEXT);
            }
            return this;
        }
    }

    // -------------------------------------------------------------------------
    // CRUD actions
    // -------------------------------------------------------------------------

    private void loadPets() {
        try {
            List<Pet> pets = petDAO.listAll();
            tableModel.setRowCount(0);
            for (Pet p : pets) {
                tableModel.addRow(new Object[]{p.getId(), p.getNome(), p.getTipoUsuario()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Erro ao carregar pets: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void novoPet() {
        PetFormDialog form = new PetFormDialog(this, null);
        form.setVisible(true);
        loadPets();
    }

    private void editarPet() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um pet para editar.");
            return;
        }
        int id = (Integer) tableModel.getValueAt(row, 0);
        try {
            Pet p = petDAO.buscarPorId(id);
            if (p != null) {
                PetFormDialog form = new PetFormDialog(this, p);
                form.setVisible(true);
                loadPets();
                controller.carregarPet();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Erro ao carregar pet: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirPet() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um pet para excluir.");
            return;
        }
        int id = (Integer) tableModel.getValueAt(row, 0);
        int opt = JOptionPane.showConfirmDialog(this,
            "Deseja excluir o pet selecionado?",
            "Confirmar exclusão",
            JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            try {
                petDAO.delete(id);
                loadPets();
                controller.carregarPet();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Erro ao excluir pet: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void usarPet() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um pet para usar.");
            return;
        }
        int id = (Integer) tableModel.getValueAt(row, 0);
        try {
            controller.carregarPetPorId(id);
            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Erro ao carregar pet: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================================
    // Inner form dialog — Novo / Editar pet
    // =========================================================================

    private static class PetFormDialog extends JDialog {

        private final PetDAO petDAO = new PetDAO();
        private Pet pet;

        private JTextField txtNome;
        private JTextField txtTipo;
        private JSpinner spHunger;
        private JSpinner spHappiness;
        private JSpinner spEnergy;
        private JLabel lblImageName;
        private JLabel lblImagePreview;
        private byte[] chosenImageData;

        PetFormDialog(Dialog owner, Pet pet) {
            super(owner, pet == null ? "Novo Pet" : "Editar Pet", true);
            this.pet = pet;
            setMinimumSize(new Dimension(520, 520));
            setSize(560, 560);
            setResizable(true);
            setLocationRelativeTo(owner);
            getContentPane().setBackground(Theme.BG_MAIN);
            buildUi();
            if (pet != null) populateForm();
        }

        private void buildUi() {
            setLayout(new BorderLayout(0, 0));
            add(buildFormHeader(),  BorderLayout.NORTH);
            add(buildForm(),        BorderLayout.CENTER);
            add(buildFormButtons(), BorderLayout.SOUTH);
        }

        private JPanel buildFormHeader() {
            JPanel header = new JPanel(new BorderLayout()) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    GradientPaint gp = new GradientPaint(
                        0, 0,           new Color(52, 32, 92),
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
                BorderFactory.createEmptyBorder(12, 18, 12, 18)
            ));
            JLabel title = new JLabel(pet == null ? "NOVO PET" : "EDITAR PET", SwingConstants.LEFT);
            title.setFont(Theme.FONT_HEADER);
            title.setForeground(Theme.ACCENT);
            title.setOpaque(false);
            header.add(title, BorderLayout.WEST);
            return header;
        }

        private JPanel buildForm() {
            JPanel outer = new JPanel(new BorderLayout(14, 0));
            outer.setBackground(Theme.BG_MAIN);
            outer.setBorder(BorderFactory.createEmptyBorder(16, 20, 8, 20));

            // ── Campos ──────────────────────────────────────────────────────
            JPanel form = new JPanel(new GridLayout(0, 2, 8, 10));
            form.setBackground(Theme.BG_MAIN);
            form.setBorder(Theme.sectionBorder("  DADOS DO PET  "));

            txtNome     = new JTextField();
            txtTipo     = new JTextField();
            spHunger    = new JSpinner(new SpinnerNumberModel(50, 0, 100, 1));
            spHappiness = new JSpinner(new SpinnerNumberModel(50, 0, 100, 1));
            spEnergy    = new JSpinner(new SpinnerNumberModel(50, 0, 100, 1));

            Theme.styleTextField(txtNome);
            Theme.styleTextField(txtTipo);
            Theme.styleSpinner(spHunger);
            Theme.styleSpinner(spHappiness);
            Theme.styleSpinner(spEnergy);

            lblImageName = new JLabel("Nenhuma imagem selecionada");
            lblImageName.setFont(Theme.FONT_LABEL);
            lblImageName.setForeground(Theme.TEXT_DIM);
            lblImageName.setBackground(Theme.BG_INPUT);
            lblImageName.setOpaque(true);
            lblImageName.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_LO, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));

            JButton btnChooseImage = Theme.makeButton("Escolher...", Theme.ACCENT2);
            btnChooseImage.addActionListener(e -> chooseImage());

            form.add(makeFormLabel("Nome:"));        form.add(txtNome);
            form.add(makeFormLabel("Tipo:"));        form.add(txtTipo);
            form.add(makeFormLabel("Fome (0-100):")); form.add(spHunger);
            form.add(makeFormLabel("Felicidade:")); form.add(spHappiness);
            form.add(makeFormLabel("Energia:"));    form.add(spEnergy);
            form.add(makeFormLabel("Imagem:"));     form.add(lblImageName);
            form.add(new JLabel(""));               form.add(btnChooseImage);

            // ── Preview de imagem ────────────────────────────────────────────
            lblImagePreview = new JLabel("Preview", SwingConstants.CENTER);
            lblImagePreview.setFont(Theme.FONT_LABEL);
            lblImagePreview.setForeground(Theme.TEXT_DIM);
            lblImagePreview.setPreferredSize(new Dimension(140, 140));
            lblImagePreview.setMinimumSize(new Dimension(120, 120));
            lblImagePreview.setBackground(Theme.BG_CARD);
            lblImagePreview.setOpaque(true);
            lblImagePreview.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.ACCENT, 1),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)));

            JPanel previewPanel = new JPanel(new BorderLayout());
            previewPanel.setBackground(Theme.BG_MAIN);
            previewPanel.setBorder(Theme.sectionBorder("  PREVIEW  "));
            previewPanel.add(lblImagePreview, BorderLayout.CENTER);

            outer.add(form,         BorderLayout.CENTER);
            outer.add(previewPanel, BorderLayout.EAST);
            return outer;
        }

        private JLabel makeFormLabel(String text) {
            JLabel l = new JLabel(text, SwingConstants.RIGHT);
            l.setFont(Theme.FONT_STAT);
            l.setForeground(Theme.ACCENT2);
            l.setBackground(Theme.BG_MAIN);
            l.setOpaque(true);
            return l;
        }

        private JPanel buildFormButtons() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            panel.setBackground(Theme.BG_MAIN);
            panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, Theme.ACCENT.darker()),
                BorderFactory.createEmptyBorder(12, 16, 14, 16)
            ));

            JButton btnSalvar   = Theme.makeButton("SALVAR",   Theme.ENERGY_CLR);
            JButton btnCancelar = Theme.makeButton("CANCELAR", Theme.HUNGER_CLR);

            btnSalvar.addActionListener(e   -> save());
            btnCancelar.addActionListener(e -> dispose());

            panel.add(btnSalvar);
            panel.add(btnCancelar);
            return panel;
        }

        private void populateForm() {
            txtNome.setText(pet.getNome());
            txtTipo.setText(pet.getTipoUsuario());
            spHunger.setValue(pet.getHunger());
            spHappiness.setValue(pet.getHappiness());
            spEnergy.setValue(pet.getEnergy());
            chosenImageData = pet.getImageData();
            if (chosenImageData != null && chosenImageData.length > 0) {
                lblImageName.setText("Imagem salva (" + chosenImageData.length / 1024 + " KB)");
                lblImageName.setForeground(Theme.ENERGY_CLR);
                showPreview(chosenImageData);
            }
        }

        private void chooseImage() {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Imagens (PNG, JPG, GIF, BMP)", "png", "jpg", "jpeg", "gif", "bmp"));
            chooser.setBackground(Theme.BG_PANEL);
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File f = chooser.getSelectedFile();
                try {
                    byte[] bytes = Files.readAllBytes(f.toPath());
                    // Valida antes de aceitar
                    BufferedImage test = ImageIO.read(new ByteArrayInputStream(bytes));
                    if (test == null) throw new IOException("Arquivo não é uma imagem válida");
                    chosenImageData = bytes;
                    lblImageName.setText(f.getName() + " (" + bytes.length / 1024 + " KB)");
                    lblImageName.setForeground(Theme.ENERGY_CLR);
                    showPreview(chosenImageData);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this,
                        "Erro ao ler imagem: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private void showPreview(byte[] data) {
            try {
                BufferedImage bi = ImageIO.read(new ByteArrayInputStream(data));
                if (bi == null) return;
                Image scaled = bi.getScaledInstance(130, 130, Image.SCALE_SMOOTH);
                lblImagePreview.setIcon(new ImageIcon(scaled));
                lblImagePreview.setText("");
            } catch (IOException ignored) {
                lblImagePreview.setText("Inválida");
            }
        }

        private void save() {
            String nome        = txtNome.getText().trim();
            String tipoUsuario = txtTipo.getText().trim();
            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Informe o nome do pet.");
                return;
            }
            if (tipoUsuario.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Informe o tipo do pet.");
                return;
            }
            if (pet == null) pet = new Pet();
            pet.setNome(nome);
            pet.setTipoUsuario(tipoUsuario);
            pet.setHunger((Integer) spHunger.getValue());
            pet.setHappiness((Integer) spHappiness.getValue());
            pet.setEnergy((Integer) spEnergy.getValue());
            pet.setImageData(chosenImageData);

            try {
                if (pet.getId() == 0) {
                    petDAO.insert(pet);
                } else {
                    petDAO.update(pet);
                }
                dispose();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Erro ao salvar pet: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
