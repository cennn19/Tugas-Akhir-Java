import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class MainFrame extends JFrame {
    private JTable tabelDashboard;
    private JComboBox<String> comboKategoriInput, comboKategoriSet;
    private JTextField txtNamaBarang, txtNominal, txtSetGlobal, txtSetKategori, txtSetAman;
    private JRadioButton radioKebutuhan, radioKeinginan;
    private List<String[]> listKategoriInput, listSemuaKategori;

    // --- PALET WARNA WEB APP MODERN ---
    private Color sidebarColor = new Color(30, 41, 59);    // Slate Dark
    private Color sidebarHover = new Color(51, 65, 85);    // Slate Lighter
    private Color bgColor = new Color(248, 250, 252);      // Sangat Abu-abu muda (Background Web)
    private Color primaryColor = new Color(37, 99, 235);   // Biru Modern (Tombol Aksi)
    private Color textColor = new Color(15, 23, 42);       // Teks Gelap
    private Font fontUtama = new Font("Segoe UI", Font.PLAIN, 14);
    private Font fontMenu = new Font("Segoe UI", Font.BOLD, 15);

    // Layout Manager untuk ganti-ganti halaman
    private CardLayout cardLayout;
    private JPanel mainContentPanel;

    public MainFrame() {
        setTitle("Logistik Kos - Web Dashboard");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. BUAT SIDEBAR (Kiri)
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // 2. BUAT MAIN CONTENT (Kanan - Pakai CardLayout)
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(bgColor);
        mainContentPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Masukkan halaman-halaman ke dalam CardLayout
        mainContentPanel.add(createDashboardPanel(), "Dashboard");
        mainContentPanel.add(createInputPanel(), "Input");
        mainContentPanel.add(createSettingsPanel(), "Settings");

        add(mainContentPanel, BorderLayout.CENTER);
        
        // Pengecekan pengguna baru
        SwingUtilities.invokeLater(this::cekPenggunaBaru);
    }

    // =========================================
    // SIDEBAR NAVIGATION (Ala Web App)
    // =========================================
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(sidebarColor);
        sidebar.setPreferredSize(new Dimension(220, 0));

        // Logo / Title App di Sidebar
        JLabel appTitle = new JLabel("LOKOST");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        appTitle.setForeground(Color.WHITE);
        appTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        appTitle.setBorder(new EmptyBorder(30, 10, 40, 10));
        sidebar.add(appTitle);

        // Tombol-tombol Menu
        sidebar.add(createMenuButton("📊  Dashboard", "Dashboard"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createMenuButton("✍️  Catat Transaksi", "Input"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createMenuButton("⚙️  Pengaturan", "Settings"));

        return sidebar;
    }

    // Fungsi membuat tombol menu Sidebar dengan efek Hover
    private JButton createMenuButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setFont(fontMenu);
        btn.setForeground(Color.WHITE);
        btn.setBackground(sidebarColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 45));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(0, 20, 0, 0)); // Padding kiri

        // Efek Hover Ala Web
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(sidebarHover); }
            public void mouseExited(MouseEvent e) { btn.setBackground(sidebarColor); }
        });

        // Aksi klik ganti halaman CardLayout
        btn.addActionListener(e -> cardLayout.show(mainContentPanel, cardName));
        return btn;
    }

    // =========================================
    // KONTEN HALAMAN (Dimasukkan ke dalam "Card" putih)
    // =========================================
    
    // Membungkus panel dalam kotak putih bersudut (Card UI)
    private JPanel createWebCard(JPanel innerPanel, String title) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(bgColor);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(textColor);
        lblTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        wrapper.add(lblTitle, BorderLayout.NORTH);

        innerPanel.setBackground(Color.WHITE);
        innerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240)), // Border tipis abu
            new EmptyBorder(20, 20, 20, 20) // Padding dalam
        ));
        wrapper.add(innerPanel, BorderLayout.CENTER);

        return wrapper;
    }

    // PANEL 1: DASHBOARD
    private JPanel createDashboardPanel() {
        JPanel content = new JPanel(new BorderLayout(0, 15));
        
        tabelDashboard = new JTable();
        tabelDashboard.setFont(fontUtama);
        tabelDashboard.setRowHeight(40); 
        tabelDashboard.setShowVerticalLines(false);
        tabelDashboard.setGridColor(new Color(241, 245, 249));
        
        JTableHeader header = tabelDashboard.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(bgColor);
        header.setForeground(textColor);
        header.setPreferredSize(new Dimension(100, 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(203, 213, 225)));

        refreshTabel(); 

        JButton btnRefresh = styleActionBtn("Segarkan Tabel");
        btnRefresh.addActionListener(e -> refreshTabel());

        content.add(new JScrollPane(tabelDashboard), BorderLayout.CENTER);
        
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBottom.setBackground(Color.WHITE);
        pnlBottom.add(btnRefresh);
        content.add(pnlBottom, BorderLayout.SOUTH);

        return createWebCard(content, "Ringkasan Anggaran");
    }

    // PANEL 2: INPUT PENGELUARAN
    private JPanel createInputPanel() {
        JPanel content = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        addFormLabel(content, "Jenis Pengeluaran", gbc, 0, 0);
        radioKebutuhan = new JRadioButton("Kebutuhan"); radioKeinginan = new JRadioButton("Keinginan");
        radioKebutuhan.setBackground(Color.WHITE); radioKeinginan.setBackground(Color.WHITE);
        radioKebutuhan.setFont(fontUtama); radioKeinginan.setFont(fontUtama);
        ButtonGroup bgTipe = new ButtonGroup(); bgTipe.add(radioKebutuhan); bgTipe.add(radioKeinginan);
        JPanel pnlRadio = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); pnlRadio.setBackground(Color.WHITE);
        pnlRadio.add(radioKebutuhan); pnlRadio.add(Box.createRigidArea(new Dimension(20, 0))); pnlRadio.add(radioKeinginan);
        gbc.gridx = 1; gbc.gridy = 0; content.add(pnlRadio, gbc);

        addFormLabel(content, "Kategori", gbc, 0, 1);
        comboKategoriInput = new JComboBox<>(); comboKategoriInput.setFont(fontUtama);
        gbc.gridx = 1; gbc.gridy = 1; content.add(comboKategoriInput, gbc);

        addFormLabel(content, "Nama Barang", gbc, 0, 2);
        txtNamaBarang = createWebTextField();
        gbc.gridx = 1; gbc.gridy = 2; content.add(txtNamaBarang, gbc);

        addFormLabel(content, "Harga (Rp)", gbc, 0, 3);
        txtNominal = createWebTextField();
        gbc.gridx = 1; gbc.gridy = 3; content.add(txtNominal, gbc);

        JButton btnSimpan = styleActionBtn("Simpan Transaksi");
        gbc.gridx = 1; gbc.gridy = 4; gbc.insets = new Insets(30, 12, 10, 12);
        content.add(btnSimpan, gbc);

        radioKebutuhan.addActionListener(e -> loadKategoriInput());
        radioKeinginan.addActionListener(e -> loadKategoriInput());
        btnSimpan.addActionListener(e -> simpanData());

        // Bungkus content ke dalam Card supaya ke atas (tidak di tengah banget)
        JPanel wrapTop = new JPanel(new BorderLayout());
        wrapTop.setBackground(Color.WHITE);
        wrapTop.add(content, BorderLayout.NORTH);

        return createWebCard(wrapTop, "Catat Pengeluaran Baru");
    }

    // PANEL 3: PENGATURAN
    private JPanel createSettingsPanel() {
        JPanel content = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Global Budget
        addFormLabel(content, "Total Budget Bulanan", gbc, 0, 0);
        txtSetGlobal = createWebTextField();
        gbc.gridx = 1; gbc.gridy = 0; content.add(txtSetGlobal, gbc);
        JButton btnGlobal = styleActionBtn("Simpan");
        gbc.gridx = 2; gbc.gridy = 0; content.add(btnGlobal, gbc);

        // Batas Aman
        addFormLabel(content, "Zona Peringatan Aman", gbc, 0, 1);
        txtSetAman = createWebTextField();
        gbc.gridx = 1; gbc.gridy = 1; content.add(txtSetAman, gbc);
        JButton btnAman = styleActionBtn("Simpan");
        gbc.gridx = 2; gbc.gridy = 1; content.add(btnAman, gbc);

        // Separator
        gbc.gridwidth = 3; gbc.gridx = 0; gbc.gridy = 2;
        content.add(new JSeparator(), gbc);
        gbc.gridwidth = 1;

        // Kategori
        addFormLabel(content, "Update Batas Kategori", gbc, 0, 3);
        comboKategoriSet = new JComboBox<>(); comboKategoriSet.setFont(fontUtama);
        gbc.gridwidth = 2; gbc.gridx = 1; gbc.gridy = 3; content.add(comboKategoriSet, gbc);
        
        gbc.gridwidth = 1;
        addFormLabel(content, "Batas Budget Per Kategori", gbc, 0, 4);
        txtSetKategori = createWebTextField();
        gbc.gridx = 1; gbc.gridy = 4; content.add(txtSetKategori, gbc);
        JButton btnKategori = styleActionBtn("Simpan");
        gbc.gridx = 2; gbc.gridy = 4; content.add(btnKategori, gbc);

        loadSemuaKategori();
        try { 
            txtSetGlobal.setText(String.valueOf(DatabaseHelper.getBudgetGlobal())); 
            txtSetAman.setText(String.valueOf(DatabaseHelper.getBatasAman()));
        } catch (Exception ignored) {}

        // Listeners
        btnGlobal.addActionListener(e -> {
            try { DatabaseHelper.updateBudgetGlobal(Double.parseDouble(txtSetGlobal.getText())); JOptionPane.showMessageDialog(this, "Tersimpan!"); refreshTabel(); } catch (Exception ex) { }
        });
        btnAman.addActionListener(e -> {
            try { DatabaseHelper.updateBatasAman(Double.parseDouble(txtSetAman.getText())); JOptionPane.showMessageDialog(this, "Tersimpan!"); } catch (Exception ex) { }
        });
        btnKategori.addActionListener(e -> {
            try {
                int id = Integer.parseInt(listSemuaKategori.get(comboKategoriSet.getSelectedIndex())[0]);
                DatabaseHelper.updateBudgetKategori(id, Double.parseDouble(txtSetKategori.getText()));
                JOptionPane.showMessageDialog(this, "Tersimpan!"); refreshTabel();
            } catch (Exception ex) { }
        });

        JPanel wrapTop = new JPanel(new BorderLayout()); wrapTop.setBackground(Color.WHITE);
        wrapTop.add(content, BorderLayout.NORTH);
        return createWebCard(wrapTop, "Konfigurasi Sistem");
    }

    // =========================================
    // UTILITAS & FUNGSI DATABASE
    // =========================================
    private void addFormLabel(JPanel panel, String text, GridBagConstraints gbc, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(100, 116, 139)); // Teks abu-abu khas label form web
        gbc.gridx = x; gbc.gridy = y;
        panel.add(lbl, gbc);
    }

    private JTextField createWebTextField() {
        JTextField tf = new JTextField(20);
        tf.setFont(fontUtama);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true), 
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        return tf;
    }

    private JButton styleActionBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(primaryColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(29, 78, 216)); }
            public void mouseExited(MouseEvent e) { btn.setBackground(primaryColor); }
        });
        return btn;
    }

    // --- FUNGSI DB TETAP SAMA ---
    private void cekPenggunaBaru() {
        try { if (DatabaseHelper.getBudgetGlobal() == 0) JOptionPane.showMessageDialog(this, "Halo! Batas anggaranmu masih Rp 0.\nSilakan ke menu 'Pengaturan' di sidebar.", "Selamat Datang", JOptionPane.INFORMATION_MESSAGE); } catch (Exception e) {}
    }

    private void refreshTabel() {
        try { tabelDashboard.setModel(DatabaseHelper.getDashboardTableModel()); } catch (Exception e) {}
    }

    private void loadKategoriInput() {
        try {
            comboKategoriInput.removeAllItems();
            listKategoriInput = DatabaseHelper.getKategori(radioKebutuhan.isSelected() ? "Kebutuhan" : "Keinginan");
            for (String[] kat : listKategoriInput) comboKategoriInput.addItem(kat[1]);
        } catch (Exception e) {}
    }

    private void loadSemuaKategori() {
        try {
            comboKategoriSet.removeAllItems();
            listSemuaKategori = DatabaseHelper.getAllKategori();
            for (String[] kat : listSemuaKategori) comboKategoriSet.addItem(kat[1]);
        } catch (Exception e) {}
    }

    private void simpanData() {
        try {
            String nama = txtNamaBarang.getText();
            double nominal = Double.parseDouble(txtNominal.getText());
            int index = comboKategoriInput.getSelectedIndex();
            if (index == -1 || nama.isEmpty()) return;

            int idKat = Integer.parseInt(listKategoriInput.get(index)[0]);
            new Pengeluaran(nama, nominal, idKat).simpan(); 

            try {
                double sisaUang = DatabaseHelper.getBudgetGlobal() - DatabaseHelper.getTotalPengeluaranGlobal();
                double batasAman = DatabaseHelper.getBatasAman();
                if (batasAman > 0 && sisaUang <= batasAman && sisaUang > 0) {
                    JOptionPane.showMessageDialog(this, "⚠️ ZONA AMAN!\nSisa uang tinggal Rp " + sisaUang, "Peringatan", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ignored) {} 

            JOptionPane.showMessageDialog(this, "Berhasil!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            txtNamaBarang.setText(""); txtNominal.setText(""); refreshTabel(); 
        } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Harga harus angka!");
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "ALARM", JOptionPane.ERROR_MESSAGE); }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}