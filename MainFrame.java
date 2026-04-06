import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class MainFrame extends JFrame {
    private JTable tabelDashboard, tabelStok;
    // Modul 7: GUI
    private JComboBox<String> comboKategoriInput, comboKategoriSet, comboKatStok, comboSatuan;
    // Modul 5: GUI
    private JTextField txtNamaBarang, txtNominal, txtTanggal, txtSetGlobal, txtSetKategori, txtSetAman, txtNamaStok;
    private JRadioButton radioKebutuhan, radioKeinginan;
    private JSpinner spinJumlah;
    private List<String[]> listKategoriInput, listSemuaKategori;
    private JPanel pnlSummaryCards, pnlChartBar;
    private JLabel lblInsightSmarter;
    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    private TableRowSorter<DefaultTableModel> sorterStok; 

    // --- PALET WARNA TAILWIND CSS (SaaS LOOK) ---
    private Color bgBody = new Color(248, 250, 252);        
    private Color bgCard = new Color(255, 255, 255);        
    private Color primaryColor = new Color(79, 70, 229);    
    private Color primaryHover = new Color(67, 56, 202);    
    private Color sidebarColor = new Color(15, 23, 42);     
    private Color sidebarHover = new Color(30, 41, 59);     
    private Color textDark = new Color(15, 23, 42);         
    private Color textMuted = new Color(100, 116, 139);     
    private Color borderColor = new Color(226, 232, 240);   

    // --- MODERN TYPOGRAPHY ---
    private Font fontTitle = new Font("Segoe UI", Font.BOLD, 26);
    private Font fontCardTitle = new Font("Segoe UI", Font.BOLD, 14);
    private Font fontUtama = new Font("Segoe UI", Font.PLAIN, 15);
    private Font fontMenu = new Font("Segoe UI", Font.BOLD, 14);

    public MainFrame() {
        setTitle("LOKOST - Smart Kost Management");
        setSize(1050, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(bgBody);

        add(createSidebar(), BorderLayout.WEST);

        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(bgBody);
        mainContentPanel.setBorder(new EmptyBorder(30, 40, 30, 40)); 

        mainContentPanel.add(createDashboardPanel(), "Dashboard");
        mainContentPanel.add(createInputPanel(), "Input");
        mainContentPanel.add(createStokPanel(), "Stok");
        mainContentPanel.add(createAnalitikPanel(), "Analitik"); 
        mainContentPanel.add(createSettingsPanel(), "Settings");

        add(mainContentPanel, BorderLayout.CENTER);
        SwingUtilities.invokeLater(this::cekPenggunaBaru);
    }

    class RoundedPanel extends JPanel {
        private int radius;
        public RoundedPanel(int radius, Color bgColor) {
            this.radius = radius; setOpaque(false); setBackground(bgColor);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground()); g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius); g2.dispose();
            super.paintComponent(g);
        }
    }

    private JPanel createWebCard(JPanel innerPanel, String title, String subtitle) {
        JPanel wrapper = new JPanel(new BorderLayout()); wrapper.setBackground(bgBody);
        JPanel headerPnl = new JPanel(new GridLayout(2, 1)); headerPnl.setBackground(bgBody); headerPnl.setBorder(new EmptyBorder(0, 0, 25, 0));
        
        JLabel lblTitle = new JLabel(title); lblTitle.setFont(fontTitle); lblTitle.setForeground(textDark);
        JLabel lblSub = new JLabel(subtitle); lblSub.setFont(fontUtama); lblSub.setForeground(textMuted);
        headerPnl.add(lblTitle); headerPnl.add(lblSub); wrapper.add(headerPnl, BorderLayout.NORTH);

        RoundedPanel card = new RoundedPanel(20, bgCard); card.setLayout(new BorderLayout()); card.setBorder(new EmptyBorder(25, 25, 25, 25));
        innerPanel.setOpaque(false); card.add(innerPanel, BorderLayout.CENTER); wrapper.add(card, BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(sidebarColor);
        sidebar.setPreferredSize(new Dimension(240, 0));

        JLabel appTitle = new JLabel("LOKOST.");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        appTitle.setForeground(Color.WHITE);
        appTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        appTitle.setBorder(new EmptyBorder(40, 0, 50, 0));
        sidebar.add(appTitle);

        sidebar.add(createMenuButton("Overview", "Dashboard"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebar.add(createMenuButton("Catat Transaksi", "Input"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebar.add(createMenuButton("Stok Logistik", "Stok"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebar.add(createMenuButton("Insight", "Analitik")); 
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebar.add(createMenuButton("Pengaturan", "Settings"));

        return sidebar;
    }

    private JButton createMenuButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setFont(fontMenu); btn.setForeground(new Color(203, 213, 225)); 
        btn.setBackground(sidebarColor); btn.setFocusPainted(false); btn.setBorderPainted(false); 
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); btn.setAlignmentX(Component.CENTER_ALIGNMENT); 
        btn.setMaximumSize(new Dimension(210, 45)); btn.setHorizontalAlignment(SwingConstants.LEFT); 
        btn.setBorder(new EmptyBorder(0, 25, 0, 0));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(sidebarHover); btn.setForeground(Color.WHITE); }
            public void mouseExited(MouseEvent e) { btn.setBackground(sidebarColor); btn.setForeground(new Color(203, 213, 225)); }
        });

        btn.addActionListener(e -> {
            cardLayout.show(mainContentPanel, cardName);
            if (cardName.equals("Analitik")) refreshAnalitik(); 
            if (cardName.equals("Dashboard")) refreshTabel();
            if (cardName.equals("Stok")) refreshStokTabel();
        });
        return btn;
    }

    private void styleModernTable(JTable table) {
        table.setFont(fontUtama); table.setRowHeight(45); table.setShowGrid(false); table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(238, 242, 255)); table.setSelectionForeground(textDark);
        
        JTableHeader header = table.getTableHeader(); header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(bgCard); header.setForeground(textMuted); header.setPreferredSize(new Dimension(100, 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor));

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer(); renderer.setBorder(new EmptyBorder(0, 10, 0, 10));
        table.setDefaultRenderer(Object.class, renderer);
    }

    private JPanel createDashboardPanel() {
        JPanel content = new JPanel(new BorderLayout(0, 20)); content.setOpaque(false);
        tabelDashboard = new JTable(); styleModernTable(tabelDashboard); refreshTabel(); 
        
        JButton btnRefresh = styleActionBtn("Segarkan Data", primaryColor, primaryHover); 
        btnRefresh.addActionListener(e -> refreshTabel());
        
        JScrollPane scrollPane = new JScrollPane(tabelDashboard); scrollPane.setBorder(BorderFactory.createLineBorder(borderColor));
        scrollPane.getViewport().setBackground(bgCard); content.add(scrollPane, BorderLayout.CENTER);
        
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT)); pnlBottom.setOpaque(false); pnlBottom.add(btnRefresh);
        content.add(pnlBottom, BorderLayout.SOUTH);
        return createWebCard(content, "Overview Keuangan", "Pantau sisa anggaran dan pergerakan uangmu bulan ini.");
    }

    private JPanel createInputPanel() {
        JPanel content = new JPanel(new GridBagLayout()); content.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(12, 12, 12, 12); gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.WEST;

        addFormLabel(content, "Jenis Pengeluaran", gbc, 0, 0);
        radioKebutuhan = new JRadioButton("Kebutuhan"); radioKeinginan = new JRadioButton("Keinginan"); radioKebutuhan.setOpaque(false); radioKeinginan.setOpaque(false);
        ButtonGroup bgTipe = new ButtonGroup(); bgTipe.add(radioKebutuhan); bgTipe.add(radioKeinginan);
        JPanel pnlRadio = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); pnlRadio.setOpaque(false); pnlRadio.add(radioKebutuhan); pnlRadio.add(Box.createRigidArea(new Dimension(20, 0))); pnlRadio.add(radioKeinginan);
        gbc.gridx = 1; gbc.gridy = 0; content.add(pnlRadio, gbc);

        addFormLabel(content, "Kategori Anggaran", gbc, 0, 1);
        comboKategoriInput = new JComboBox<>(); comboKategoriInput.setFont(fontUtama); gbc.gridx = 1; gbc.gridy = 1; content.add(comboKategoriInput, gbc);

        addFormLabel(content, "Nama Transaksi", gbc, 0, 2);
        txtNamaBarang = createWebTextField(); gbc.gridx = 1; gbc.gridy = 2; content.add(txtNamaBarang, gbc);

        addFormLabel(content, "Nominal (Rp)", gbc, 0, 3);
        txtNominal = createWebTextField(); gbc.gridx = 1; gbc.gridy = 3; content.add(txtNominal, gbc);

        addFormLabel(content, "Tanggal (YYYY-MM-DD)", gbc, 0, 4);
        txtTanggal = createWebTextField(); txtTanggal.setText(java.time.LocalDate.now().toString()); 
        gbc.gridx = 1; gbc.gridy = 4; content.add(txtTanggal, gbc);

        JButton btnSimpan = styleActionBtn("Simpan Transaksi", primaryColor, primaryHover);
        gbc.gridx = 1; gbc.gridy = 5; gbc.insets = new Insets(30, 12, 10, 12); content.add(btnSimpan, gbc);

        radioKebutuhan.addActionListener(e -> loadKategoriInput()); radioKeinginan.addActionListener(e -> loadKategoriInput());
        btnSimpan.addActionListener(e -> simpanData());

        JPanel wrapTop = new JPanel(new BorderLayout()); wrapTop.setOpaque(false); wrapTop.add(content, BorderLayout.NORTH);
        return createWebCard(wrapTop, "Catat Pengeluaran", "Masukkan detail transaksimu agar tercatat oleh sistem LOKOST.");
    }

    private JPanel createStokPanel() {
        JPanel content = new JPanel(new BorderLayout(0, 20)); content.setOpaque(false);

        RoundedPanel pnlInput = new RoundedPanel(15, bgBody); pnlInput.setLayout(new GridBagLayout());
        pnlInput.setBorder(new EmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(8, 10, 8, 10); gbc.fill = GridBagConstraints.HORIZONTAL;

        addFormLabel(pnlInput, "Nama Barang", gbc, 0, 0); txtNamaStok = createWebTextField(); gbc.gridx = 1; gbc.gridy = 0; pnlInput.add(txtNamaStok, gbc);
        addFormLabel(pnlInput, "Kategori", gbc, 2, 0); comboKatStok = new JComboBox<>(new String[]{"Makanan Instan", "Minuman", "Peralatan Mandi", "Kebersihan", "Obat", "Lainnya"}); comboKatStok.setFont(fontUtama); gbc.gridx = 3; gbc.gridy = 0; pnlInput.add(comboKatStok, gbc);
        addFormLabel(pnlInput, "Stok Awal", gbc, 0, 1); spinJumlah = new JSpinner(new SpinnerNumberModel(1, 0, 1000, 1)); spinJumlah.setFont(fontUtama); gbc.gridx = 1; gbc.gridy = 1; pnlInput.add(spinJumlah, gbc);
        addFormLabel(pnlInput, "Satuan", gbc, 2, 1); comboSatuan = new JComboBox<>(new String[]{"Pcs", "Bungkus", "Botol", "Sachet", "Dus", "Lembar"}); comboSatuan.setFont(fontUtama); gbc.gridx = 3; gbc.gridy = 1; pnlInput.add(comboSatuan, gbc);

        JButton btnTambahBarang = styleActionBtn("Tambahkan Barang", textDark, sidebarHover); gbc.gridx = 3; gbc.gridy = 2; pnlInput.add(btnTambahBarang, gbc);
        content.add(pnlInput, BorderLayout.NORTH);

        JPanel pnlSearch = new JPanel(new BorderLayout(15, 0)); pnlSearch.setOpaque(false); pnlSearch.setBorder(new EmptyBorder(0, 0, 10, 0));
        JLabel lblSearch = new JLabel("Cari Barang:"); lblSearch.setFont(fontCardTitle); lblSearch.setForeground(textMuted);
        JTextField txtSearchStok = createWebTextField(); txtSearchStok.setToolTipText("Ketik nama barang...");
        pnlSearch.add(lblSearch, BorderLayout.WEST); pnlSearch.add(txtSearchStok, BorderLayout.CENTER);

        tabelStok = new JTable(); styleModernTable(tabelStok);
        refreshStokTabel(); 

        txtSearchStok.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { search(); }
            private void search() {
                String text = txtSearchStok.getText();
                if (text.trim().length() == 0) { sorterStok.setRowFilter(null); } 
                else { sorterStok.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1)); }
            }
        });

        JScrollPane scrollStok = new JScrollPane(tabelStok); scrollStok.setBorder(BorderFactory.createLineBorder(borderColor)); scrollStok.getViewport().setBackground(bgCard);
        JPanel pnlCenter = new JPanel(new BorderLayout()); pnlCenter.setOpaque(false);
        pnlCenter.add(pnlSearch, BorderLayout.NORTH); pnlCenter.add(scrollStok, BorderLayout.CENTER);
        content.add(pnlCenter, BorderLayout.CENTER);

        JPanel pnlAksi = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10)); pnlAksi.setOpaque(false);
        JButton btnPakai = styleActionBtn("Ambil (-)", new Color(245, 158, 11), new Color(217, 119, 6)); 
        JButton btnTambah1 = styleActionBtn("Beli (+)", new Color(16, 185, 129), new Color(5, 150, 105)); 
        JButton btnHapusStok = styleActionBtn("Hapus Data", new Color(239, 68, 68), new Color(220, 38, 38)); 

        pnlAksi.add(btnPakai); pnlAksi.add(btnTambah1); pnlAksi.add(btnHapusStok); content.add(pnlAksi, BorderLayout.SOUTH);

        btnTambahBarang.addActionListener(e -> {
            try {
                String nama = txtNamaStok.getText(); if (nama.isEmpty()) return;
                int qtyAwal = (int) spinJumlah.getValue();
                DatabaseHelper.tambahStok(nama, comboKatStok.getSelectedItem().toString(), qtyAwal, comboSatuan.getSelectedItem().toString());
                txtNamaStok.setText(""); spinJumlah.setValue(1); refreshStokTabel(); 
                catatPengeluaranDariStok(nama, qtyAwal); 
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Gagal simpan barang!"); }
        });

        btnPakai.addActionListener(e -> prosesStokDinamis(false)); 
        btnTambah1.addActionListener(e -> prosesStokDinamis(true));
        
        btnHapusStok.addActionListener(e -> {
            int row = tabelStok.getSelectedRow(); if (row == -1) { JOptionPane.showMessageDialog(this, "Pilih barang di tabel!"); return; }
            int modelRow = tabelStok.convertRowIndexToModel(row);
            if (JOptionPane.showConfirmDialog(this, "Hapus barang ini dari lemari?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                try { DatabaseHelper.hapusStok(Integer.parseInt(tabelStok.getModel().getValueAt(modelRow, 0).toString())); refreshStokTabel(); } catch (Exception ex) {}
            }
        });

        return createWebCard(content, "Logistik & Inventaris", "Pantau persediaan barang-barang kamarmu dengan mudah.");
    }

    private void prosesStokDinamis(boolean isTambah) {
        int row = tabelStok.getSelectedRow(); 
        if (row == -1) { JOptionPane.showMessageDialog(this, "Pilih barang di tabel dulu!"); return; }
        
        int modelRow = tabelStok.convertRowIndexToModel(row); 
        try {
            int idBarang = Integer.parseInt(tabelStok.getModel().getValueAt(modelRow, 0).toString());
            String namaBarang = tabelStok.getModel().getValueAt(modelRow, 1).toString();
            
            String pesan = isTambah ? "Berapa banyak [" + namaBarang + "] yang DIBELI?" : "Berapa banyak [" + namaBarang + "] yang DIAMBIL?";
            String input = JOptionPane.showInputDialog(this, pesan, "1"); 

            if (input != null && !input.trim().isEmpty()) {
                int qty = Integer.parseInt(input);
                if (qty <= 0) { JOptionPane.showMessageDialog(this, "Jumlah harus lebih dari 0!"); return; }

                int perubahan = isTambah ? qty : -qty;
                DatabaseHelper.updateJumlahStok(idBarang, perubahan); 
                refreshStokTabel();
                
                if (!isTambah) {
                    int sisaSekarang = DatabaseHelper.getSisaStok(idBarang);
                    if (sisaSekarang <= 2) {
                        String warning = (sisaSekarang <= 0) ? "🚨 Stok " + namaBarang + " HABIS TOTAL!" : "⚠️ Stok " + namaBarang + " sisa " + sisaSekarang + " lagi.";
                        JOptionPane.showMessageDialog(this, warning, "Alarm Stok", JOptionPane.WARNING_MESSAGE);
                    }
                }
                
                if (isTambah) catatPengeluaranDariStok(namaBarang, qty); 
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Masukkan angka yang valid!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) { }
    }

    private JPanel createAnalitikPanel() {
        JPanel content = new JPanel(new BorderLayout(0, 20)); content.setOpaque(false);
        pnlSummaryCards = new JPanel(new GridLayout(1, 4, 20, 0)); pnlSummaryCards.setOpaque(false); content.add(pnlSummaryCards, BorderLayout.NORTH);

        RoundedPanel pnlInsight = new RoundedPanel(15, new Color(238, 242, 255)); 
        pnlInsight.setLayout(new BorderLayout(0, 10)); pnlInsight.setBorder(new EmptyBorder(20, 20, 20, 20));
        lblInsightSmarter = new JLabel("Memuat insight..."); lblInsightSmarter.setFont(fontUtama); lblInsightSmarter.setForeground(primaryHover);
        JLabel lblHeaderInsight = new JLabel("💡 AI Smart Insight"); lblHeaderInsight.setFont(fontCardTitle); lblHeaderInsight.setForeground(primaryColor);
        pnlInsight.add(lblHeaderInsight, BorderLayout.NORTH); pnlInsight.add(lblInsightSmarter, BorderLayout.CENTER);

        pnlChartBar = new JPanel(); pnlChartBar.setLayout(new BoxLayout(pnlChartBar, BoxLayout.Y_AXIS)); pnlChartBar.setOpaque(false);
        JPanel pnlTengah = new JPanel(new BorderLayout(0, 25)); pnlTengah.setOpaque(false); 
        pnlTengah.add(pnlInsight, BorderLayout.NORTH); 
        
        JScrollPane scrollChart = new JScrollPane(pnlChartBar); scrollChart.setBorder(null); scrollChart.setOpaque(false); scrollChart.getViewport().setOpaque(false);
        pnlTengah.add(scrollChart, BorderLayout.CENTER);
        content.add(pnlTengah, BorderLayout.CENTER);
        return createWebCard(content, "Analisis & Performa", "Laporan pengeluaranmu selama 5 hari terakhir.");
    }

    private JPanel createMiniCard(String title, String value, Color valueColor) {
        RoundedPanel card = new RoundedPanel(15, bgBody); card.setLayout(new GridLayout(2, 1, 0, 5)); 
        card.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel lblTitle = new JLabel(title); lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12)); lblTitle.setForeground(textMuted);
        JLabel lblValue = new JLabel(value); lblValue.setFont(new Font("Segoe UI", Font.BOLD, 18)); lblValue.setForeground(valueColor);
        card.add(lblTitle); card.add(lblValue); return card;
    }

    private void refreshAnalitik() {
        try {
            double budget = DatabaseHelper.getBudgetGlobal(); double total = DatabaseHelper.getTotalPengeluaranGlobal(); double sisa = budget - total; double aman = DatabaseHelper.getBatasAman();
            pnlSummaryCards.removeAll();
            pnlSummaryCards.add(createMiniCard("TOTAL TERPAKAI", String.format("Rp %,.0f", total), textDark));
            pnlSummaryCards.add(createMiniCard("SISA BUDGET", String.format("Rp %,.0f", sisa), (sisa < 0) ? new Color(220, 38, 38) : new Color(16, 185, 129)));
            pnlSummaryCards.add(createMiniCard("TOP KATEGORI", DatabaseHelper.getTopCategory(), textDark));
            pnlSummaryCards.add(createMiniCard("STATUS", (sisa < 0) ? "OVERBUDGET" : (sisa <= aman) ? "WARNING" : "AMAN", (sisa < 0) ? new Color(220, 38, 38) : (sisa <= aman) ? new Color(245, 158, 11) : new Color(16, 185, 129)));
            pnlSummaryCards.revalidate(); pnlSummaryCards.repaint();

            pnlChartBar.removeAll();
            List<String[]> dataKat = DatabaseHelper.getAnalitik7Hari(); java.util.Map<String, Double> data5Hari = DatabaseHelper.getPengeluaran5Hari();
            
            if (dataKat.isEmpty()) lblInsightSmarter.setText("<html>Belum ada transaksi. Keuangan stabil!</html>");
            else {
                double pct = (Double.parseDouble(dataKat.get(0)[1]) / budget) * 100;
                String pesan = "<html>" + ((sisa < 0) ? "<b>GAWAT!</b> Kamu Overbudget. Kategori <i>" + dataKat.get(0)[0] + "</i> paling menyedot uang." : "Kondisi stabil. Namun <i>" + dataKat.get(0)[0] + "</i> memakan " + String.format("%.1f", pct) + "% budget bulananmu.");
                String stokWarn = DatabaseHelper.getPeringatanStok();
                if (!stokWarn.isEmpty()) pesan += "<br><br><span style='color:#ef4444;'><b>" + stokWarn + "</b></span>";
                lblInsightSmarter.setText(pesan + "</html>");

                double max = 1.0; for (double v : data5Hari.values()) if (v > max) max = v;
                for (java.util.Map.Entry<String, Double> e : data5Hari.entrySet()) {
                    JPanel barPnl = new JPanel(new BorderLayout(15, 0)); barPnl.setOpaque(false); barPnl.setBorder(new EmptyBorder(8, 0, 8, 0));
                    JLabel lblTgl = new JLabel(e.getKey()); lblTgl.setPreferredSize(new Dimension(100, 20)); lblTgl.setFont(fontUtama); lblTgl.setForeground(textMuted);
                    JProgressBar bar = new JProgressBar(0, (int) max); bar.setValue(e.getValue().intValue()); bar.setStringPainted(true); bar.setString(String.format("Rp %,.0f", e.getValue())); bar.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    bar.setForeground(e.getValue() == 0 ? borderColor : primaryColor); bar.setBackground(bgBody); bar.setBorderPainted(false);
                    barPnl.add(lblTgl, BorderLayout.WEST); barPnl.add(bar, BorderLayout.CENTER); pnlChartBar.add(barPnl);
                }
            }
            pnlChartBar.revalidate(); pnlChartBar.repaint();
        } catch (Exception e) { lblInsightSmarter.setText("Gagal muat analitik."); }
    }

    private JPanel createSettingsPanel() {
        JPanel content = new JPanel(new GridBagLayout()); content.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(10, 10, 10, 10); gbc.fill = GridBagConstraints.HORIZONTAL;

        addFormLabel(content, "Total Budget Bulanan", gbc, 0, 0); txtSetGlobal = createWebTextField(); gbc.gridx = 1; gbc.gridy = 0; content.add(txtSetGlobal, gbc);
        addFormLabel(content, "Batas Warning (Zona Kuning)", gbc, 0, 1); txtSetAman = createWebTextField(); gbc.gridx = 1; gbc.gridy = 1; content.add(txtSetAman, gbc);
        
        JButton btnSaveGeneral = styleActionBtn("Simpan Pengaturan Umum", primaryColor, primaryHover); 
        gbc.gridx = 1; gbc.gridy = 2; content.add(btnSaveGeneral, gbc);

        gbc.gridwidth = 3; gbc.gridx = 0; gbc.gridy = 3; JSeparator sep = new JSeparator(); sep.setForeground(borderColor); content.add(sep, gbc); gbc.gridwidth = 1;

        addFormLabel(content, "Kategori Anggaran", gbc, 0, 4); comboKategoriSet = new JComboBox<>(); comboKategoriSet.setFont(fontUtama); gbc.gridwidth = 2; gbc.gridx = 1; gbc.gridy = 4; content.add(comboKategoriSet, gbc);
        gbc.gridwidth = 1; addFormLabel(content, "Batas Baru (Rp)", gbc, 0, 5); txtSetKategori = createWebTextField(); gbc.gridx = 1; gbc.gridy = 5; content.add(txtSetKategori, gbc);
        JButton btnKat = styleActionBtn("Simpan Batas Kategori", primaryColor, primaryHover); gbc.gridx = 2; gbc.gridy = 5; content.add(btnKat, gbc);

        gbc.gridwidth = 3; gbc.gridx = 0; gbc.gridy = 6; JSeparator sep2 = new JSeparator(); sep2.setForeground(borderColor); content.add(sep2, gbc); gbc.gridwidth = 1;

        addFormLabel(content, "Tindakan Berbahaya", gbc, 0, 7);
        JButton btnReset = styleActionBtn("RESET SEMUA DATA", new Color(220, 38, 38), new Color(185, 28, 28)); gbc.gridwidth = 2; gbc.gridx = 1; gbc.gridy = 7; content.add(btnReset, gbc);

        loadSemuaKategori();
        try { txtSetGlobal.setText(String.valueOf(DatabaseHelper.getBudgetGlobal())); txtSetAman.setText(String.valueOf(DatabaseHelper.getBatasAman())); } catch (Exception ignored) {}

        btnSaveGeneral.addActionListener(e -> {
            try { 
                DatabaseHelper.updateBudgetGlobal(Double.parseDouble(txtSetGlobal.getText())); 
                DatabaseHelper.updateBatasAman(Double.parseDouble(txtSetAman.getText())); 
                JOptionPane.showMessageDialog(this, "Pengaturan umum berhasil diperbarui!"); refreshTabel(); 
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Input harus angka!"); } 
        });

        btnKat.addActionListener(e -> { try { DatabaseHelper.updateBudgetKategori(Integer.parseInt(listSemuaKategori.get(comboKategoriSet.getSelectedIndex())[0]), Double.parseDouble(txtSetKategori.getText())); JOptionPane.showMessageDialog(this, "Tersimpan!"); refreshTabel(); } catch (Exception ex) {} });

        btnReset.addActionListener(e -> {
            try {
                if (JOptionPane.showConfirmDialog(this, "Yakin hapus SEMUA data?", "Konfirmasi 1", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION && 
                    JOptionPane.showConfirmDialog(this, "TIDAK BISA DIBATALKAN! Lanjut?", "Konfirmasi 2", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE) == JOptionPane.YES_OPTION) {
                    DatabaseHelper.resetSemuaData(); JOptionPane.showMessageDialog(this, "Sistem Direset."); refreshTabel(); refreshStokTabel();
                }
            } catch (Exception ex) {}
        });

        JPanel wrapTop = new JPanel(new BorderLayout()); wrapTop.setOpaque(false); wrapTop.add(content, BorderLayout.NORTH);
        return createWebCard(wrapTop, "Konfigurasi Sistem", "Atur batas keuangan dan manajemen data aplikasimu.");
    }

    private void addFormLabel(JPanel pnl, String text, GridBagConstraints gbc, int x, int y) {
        JLabel lbl = new JLabel(text); lbl.setFont(fontCardTitle); lbl.setForeground(textMuted); gbc.gridx = x; gbc.gridy = y; pnl.add(lbl, gbc);
    }
    private JTextField createWebTextField() {
        JTextField tf = new JTextField(20); tf.setFont(fontUtama); tf.setBackground(bgBody); tf.setForeground(textDark);
        tf.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(borderColor, 1, true), new EmptyBorder(10, 15, 10, 15))); return tf;
    }
    private JButton styleActionBtn(String text, Color bg, Color hover) {
        JButton btn = new JButton(text); btn.setFont(fontMenu); btn.setBackground(bg); btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false); btn.setBorder(new EmptyBorder(10, 20, 10, 20)); btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() { public void mouseEntered(MouseEvent e) { btn.setBackground(hover); } public void mouseExited(MouseEvent e) { btn.setBackground(bg); }}); return btn;
    }

    private void cekPenggunaBaru() {
        try { if (DatabaseHelper.getBudgetGlobal() == 0) {
            String nama = JOptionPane.showInputDialog(this, "Welcome to LOKOST!\nSiapa namamu?", "Setup", JOptionPane.QUESTION_MESSAGE);
            DatabaseHelper.updateNamaUser((nama != null && !nama.trim().isEmpty()) ? nama : "User");
            JOptionPane.showMessageDialog(this, "Halo " + DatabaseHelper.getNamaUser() + "!\nSilakan atur budget di menu Settings.", "Welcome", JOptionPane.INFORMATION_MESSAGE); 
        } } catch (Exception e) {}
    }

    private void refreshTabel() { try { tabelDashboard.setModel(DatabaseHelper.getDashboardTableModel()); } catch (Exception e) {} }
    
    private void refreshStokTabel() { 
        try { 
            if (tabelStok != null) {
                DefaultTableModel model = DatabaseHelper.getStokTableModel();
                tabelStok.setModel(model); 
                sorterStok = new TableRowSorter<>(model);
                tabelStok.setRowSorter(sorterStok);
            }
        } catch (Exception e) {} 
    }
    
    private void loadKategoriInput() { try { comboKategoriInput.removeAllItems(); listKategoriInput = DatabaseHelper.getKategori(radioKebutuhan.isSelected() ? "Kebutuhan" : "Keinginan"); for (String[] kat : listKategoriInput) comboKategoriInput.addItem(kat[1]); } catch (Exception e) {} }
    private void loadSemuaKategori() { try { comboKategoriSet.removeAllItems(); listSemuaKategori = DatabaseHelper.getAllKategori(); for (String[] kat : listSemuaKategori) comboKategoriSet.addItem(kat[1]); } catch (Exception e) {} }

    private void cekPeringatanBudget(int idKat, String namaKat) {
        try {
            double[] statusKat = DatabaseHelper.getBudgetStatusKategori(idKat);
            double batasKat = statusKat[0], sisaKat = statusKat[2];

            if (batasKat > 0) {
                if (sisaKat < 0) JOptionPane.showMessageDialog(this, "🚨 OVERBUDGET KATEGORI!\nJatah buat [" + namaKat + "] udah MINUS Rp " + Math.abs(sisaKat) + "!", "Warning Kategori", JOptionPane.ERROR_MESSAGE);
                else if (sisaKat == 0) JOptionPane.showMessageDialog(this, "❌ KATEGORI HABIS!\nJatah buat [" + namaKat + "] udah ludes Rp 0.", "Warning Kategori", JOptionPane.WARNING_MESSAGE);
                else if (sisaKat <= (batasKat * 0.2)) JOptionPane.showMessageDialog(this, "⚠️ KATEGORI MENIPIS!\nJatah [" + namaKat + "] sisa Rp " + sisaKat + " lagi.", "Warning Kategori", JOptionPane.WARNING_MESSAGE);
            }

            double sisaGlobal = DatabaseHelper.getBudgetGlobal() - DatabaseHelper.getTotalPengeluaranGlobal();
            double batasAmanGlobal = DatabaseHelper.getBatasAman();
            
            if (sisaGlobal < 0) JOptionPane.showMessageDialog(this, "🚨 OVERBUDGET TOTAL!\nUang bulananmu secara keseluruhan MINUS Rp " + Math.abs(sisaGlobal) + "!", "Warning Global", JOptionPane.ERROR_MESSAGE);
            else if (batasAmanGlobal > 0 && sisaGlobal <= batasAmanGlobal) JOptionPane.showMessageDialog(this, "⚠️ ZONA KUNING GLOBAL!\nTotal uangmu tinggal Rp " + sisaGlobal, "Warning Global", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ignored) {}
    }

    private void simpanData() {
        try {
            if (txtNamaBarang.getText().isEmpty() || comboKategoriInput.getSelectedIndex() == -1) return;
            int idKat = Integer.parseInt(listKategoriInput.get(comboKategoriInput.getSelectedIndex())[0]);
            String namaKat = listKategoriInput.get(comboKategoriInput.getSelectedIndex())[1];
            
            String tglInput = txtTanggal.getText();
            new Pengeluaran(txtNamaBarang.getText(), Double.parseDouble(txtNominal.getText()), idKat, tglInput).simpan(); 
            
            cekPeringatanBudget(idKat, namaKat);
            
            JOptionPane.showMessageDialog(this, "Transaksi Tersimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE); 
            txtNamaBarang.setText(""); txtNominal.setText(""); 
            txtTanggal.setText(java.time.LocalDate.now().toString()); 
            refreshTabel(); 
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "ALARM", JOptionPane.ERROR_MESSAGE); }
    }

    private void catatPengeluaranDariStok(String namaBarang, int qty) {
        if (JOptionPane.showConfirmDialog(this, "Catat pembelian [" + qty + " " + namaBarang + "] ke pengeluaran budget?", "Integrasi ERP", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                List<String[]> listKat = DatabaseHelper.getAllKategori();
                String[] namaKat = new String[listKat.size()]; for(int i=0; i<listKat.size(); i++) namaKat[i] = listKat.get(i)[1];
                JComboBox<String> cbKat = new JComboBox<>(namaKat); cbKat.setFont(fontUtama);
                JTextField txtHarga = createWebTextField();
                
                JTextField txtTglPop = createWebTextField();
                txtTglPop.setText(java.time.LocalDate.now().toString());
                
                Object[] popForm = {
                    "Tanggal Beli (YYYY-MM-DD):", txtTglPop,
                    "Pilih Kategori:", cbKat, 
                    "Total Harga Beli (Untuk " + qty + " items):", txtHarga 
                };
                
                if (JOptionPane.showConfirmDialog(this, popForm, "Catat Kas", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                    int idKat = Integer.parseInt(listKat.get(cbKat.getSelectedIndex())[0]);
                    String namaKategoriTerpilih = listKat.get(cbKat.getSelectedIndex())[1];

                    new Pengeluaran(namaBarang, Double.parseDouble(txtHarga.getText()), idKat, txtTglPop.getText()).simpan();
                    cekPeringatanBudget(idKat, namaKategoriTerpilih);
                    refreshTabel(); 
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error Input Harga!", "Error", JOptionPane.ERROR_MESSAGE); }
        }
    }

    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true)); }
}