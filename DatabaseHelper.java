import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

// Modul 2 : Pembuatan class berisi method
public class DatabaseHelper {
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/lokost", "root", "");
    }

    public static void simpanPengeluaran(String nama, double nominal, int idKategori) throws Exception {
        String sql = "{CALL sp_tambah_pengeluaran(?, ?, ?)}";
        try (Connection conn = getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, nama);
            stmt.setDouble(2, nominal);
            stmt.setInt(3, idKategori);
            stmt.execute();
            System.out.println("Berhasil mencatat pengeluaran!");
        } catch (SQLException e) {
            if ("45000".equals(e.getSQLState())) {
                throw new Exception(e.getMessage()); 
            } else {
                throw new Exception("Error DB: " + e.getMessage());
            }
        }
    }

    public static void lihatDashboard() throws Exception {
        String query = "SELECT * FROM v_ringkasan_pengeluaran";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("\n=== LAPORAN SISA BUDGET ===");
            while (rs.next()) {
                System.out.printf("%-15s | Sisa: Rp%,.2f\n", rs.getString("nama_kategori"), rs.getDouble("sisa_anggaran"));
            }
            System.out.println("===========================");
        }
    }

    // Method untuk GUI: Mengambil data View dan mengubahnya jadi Model Tabel
    public static DefaultTableModel getDashboardTableModel() throws Exception {
        String[] kolom = {"Nama Kategori", "Tipe", "Batas Anggaran", "Terpakai", "Sisa Anggaran"};
        DefaultTableModel model = new DefaultTableModel(kolom, 0);
        String query = "SELECT * FROM v_ringkasan_pengeluaran";
        
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("nama_kategori"),
                    rs.getString("tipe_kategori"),
                    "Rp " + rs.getDouble("batas_anggaran"),
                    "Rp " + rs.getDouble("total_pengeluaran"),
                    "Rp " + rs.getDouble("sisa_anggaran")
                });
            }
        }
        return model;
    }

    public static List<String[]> getKategori(String tipe) throws Exception {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT id, nama_kategori FROM kategori WHERE tipe_kategori = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tipe);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new String[]{rs.getString("id"), rs.getString("nama_kategori")});
            }
        }
        return list;
    }

    // Method update budget global
    public static void updateBudgetGlobal(double limitBaru) throws Exception {
        String sql = "{CALL sp_atur_budget_global(?)}";
        try (Connection conn = getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setDouble(1, limitBaru);
            stmt.execute();
            System.out.println("Budget Keseluruhan berhasil diperbarui!");
        }
    }

    // Method update budget kategori
    public static void updateBudgetKategori(int idKategori, double limitBaru) throws Exception {
        String sql = "{CALL sp_atur_budget_kategori(?, ?)}";
        try (Connection conn = getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, idKategori);
            stmt.setDouble(2, limitBaru);
            stmt.execute();
            System.out.println("Budget Kategori berhasil diperbarui!");
        }
    }

    // =========================================
    // METHOD TAMBAHAN UNTUK MENU PENGATURAN
    // =========================================

    // Method untuk mengambil batas budget bulanan (Global)
    public static double getBudgetGlobal() throws Exception {
        String sql = "SELECT batas_keseluruhan FROM pengaturan WHERE id = 1";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble("batas_keseluruhan");
            }
        }
        return 0; // Kembalikan 0 jika belum diset
    }

    // Method untuk mengambil SEMUA kategori (tanpa filter tipe) buat menu Pengaturan
    public static List<String[]> getAllKategori() throws Exception {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT id, nama_kategori FROM kategori";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new String[]{rs.getString("id"), rs.getString("nama_kategori")});
            }
        }
        return list;
    }

    public static double getBatasAman() throws Exception {
        String sql = "SELECT batas_aman FROM pengaturan WHERE id = 1";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble("batas_aman");
        }
        return 0;
    }

    // Method update Batas Aman
    public static void updateBatasAman(double limit) throws Exception {
        String sql = "{CALL sp_atur_batas_aman(?)}";
        try (Connection conn = getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setDouble(1, limit);
            stmt.execute();
        }
    }

    // Method untuk hitung TOTAL pengeluaran saat ini (untuk dicek vs Zona Aman)
    public static double getTotalPengeluaranGlobal() throws Exception {
        String sql = "SELECT SUM(nominal) FROM transaksi";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        }
        return 0;
    }

    // Method untuk mengambil kategori paling banyak pengeluarannya
    public static String getTopCategory() throws Exception {
        String sql = "SELECT k.nama_kategori FROM transaksi t " +
                     "JOIN kategori k ON t.id_kategori = k.id " +
                     "GROUP BY k.id ORDER BY SUM(t.nominal) DESC LIMIT 1";
        try (Connection conn = getConnection(); java.sql.Statement stmt = conn.createStatement(); java.sql.ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getString(1);
        }
        return "Belum Ada Transaksi";
    }

    public static void resetSemuaData() throws Exception {
        String sql = "{CALL sp_reset_seluruh_data()}";
        try (Connection conn = getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.execute();
        }
    }

    // Ambil nama user
    public static String getNamaUser() throws Exception {
        String sql = "SELECT nama_user FROM pengaturan WHERE id = 1";
        try (Connection conn = getConnection(); java.sql.Statement stmt = conn.createStatement(); java.sql.ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                String nama = rs.getString("nama_user");
                return (nama == null || nama.trim().isEmpty()) ? "Pengguna Baru" : nama;
            }
        }
        return "Pengguna Baru";
    }

    // Simpan nama user
    public static void updateNamaUser(String nama) throws Exception {
        String sql = "UPDATE pengaturan SET nama_user = ? WHERE id = 1";
        try (Connection conn = getConnection(); java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nama);
            pstmt.executeUpdate();
        }
    }

    // =========================================
    // METHOD UNTUK TAB ANALITIK INSIGHT
    // =========================================

    // 1. Ambil data pengeluaran 7 hari terakhir per kategori (Untuk Teks Insight AI)
    public static List<String[]> getAnalitik7Hari() throws Exception {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT k.nama_kategori, COALESCE(SUM(t.nominal), 0) as total " +
                     "FROM kategori k LEFT JOIN transaksi t ON k.id = t.id_kategori " +
                     "AND t.tanggal >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
                     "GROUP BY k.id ORDER BY total DESC";
        
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                // Hanya masukkan yang totalnya lebih dari 0 supaya insightnya akurat
                if (rs.getDouble("total") > 0) {
                    list.add(new String[]{rs.getString("nama_kategori"), String.valueOf(rs.getDouble("total"))});
                }
            }
        }
        return list;
    }

    // 2. Ambil data pengeluaran 5 HARI TERAKHIR (Berdasarkan Tanggal untuk Grafik Batang)
    public static java.util.Map<String, Double> getPengeluaran5Hari() throws Exception {
        java.util.Map<String, Double> dataHarian = new java.util.LinkedHashMap<>();
        
        // Siapkan 5 tanggal terakhir
        java.time.LocalDate hariIni = java.time.LocalDate.now();
        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (int i = 4; i >= 0; i--) {
            dataHarian.put(hariIni.minusDays(i).format(fmt), 0.0);
        }

        // Timpa dengan data asli dari database jika ada transaksi
        String sql = "SELECT tanggal, SUM(nominal) as total FROM transaksi " +
                     "WHERE tanggal >= DATE_SUB(CURDATE(), INTERVAL 4 DAY) " +
                     "GROUP BY tanggal";
                     
        try (Connection conn = getConnection(); java.sql.Statement stmt = conn.createStatement(); java.sql.ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String tgl = rs.getString("tanggal");
                if (dataHarian.containsKey(tgl)) {
                    dataHarian.put(tgl, rs.getDouble("total"));
                }
            }
        }
        return dataHarian;
    }

    // =========================================
    // METHOD UNTUK CRUD STOK LOGISTIK BARANG
    // =========================================

    // 1. READ: Ambil data untuk Tabel Stok
    public static DefaultTableModel getStokTableModel() throws Exception {
        String[] kolom = {"ID", "Nama Barang", "Kategori", "Jumlah", "Satuan", "Status"};
        DefaultTableModel model = new DefaultTableModel(kolom, 0) {
            @Override // Bikin tabel gak bisa diedit manual dengan klik ganda
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        String sql = "SELECT * FROM stok_barang ORDER BY nama_barang ASC";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int jumlah = rs.getInt("jumlah");
                String status = (jumlah == 0) ? "HABIS ❌" : (jumlah <= 2) ? "MENIPIS ⚠️" : "AMAN ✅";
                
                model.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("nama_barang"), rs.getString("kategori_barang"),
                    jumlah, rs.getString("satuan"), status
                });
            }
        }
        return model;
    }

    // 2. CREATE: Tambah Barang Baru
    public static void tambahStok(String nama, String kategori, int jumlah, String satuan) throws Exception {
        String sql = "INSERT INTO stok_barang (nama_barang, kategori_barang, jumlah, satuan) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nama); pstmt.setString(2, kategori);
            pstmt.setInt(3, jumlah); pstmt.setString(4, satuan);
            pstmt.executeUpdate();
        }
    }

    // 3. UPDATE: Ubah Jumlah (+1 atau -1)
    public static void updateJumlahStok(int id, int perubahan) throws Exception {
        // perubahan bisa angka 1 (tambah) atau -1 (kurangi)
        String sql = "UPDATE stok_barang SET jumlah = jumlah + (?) WHERE id = ? AND (jumlah + (?) >= 0)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, perubahan); pstmt.setInt(2, id); pstmt.setInt(3, perubahan);
            pstmt.executeUpdate();
        }
    }

    // 4. DELETE: Hapus Barang dari daftar
    public static void hapusStok(int id) throws Exception {
        String sql = "DELETE FROM stok_barang WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id); pstmt.executeUpdate();
        }
    }

    // Radar Pendeteksi Stok Menipis (Untuk AI Insight)
    public static String getPeringatanStok() throws Exception {
        java.util.List<String> barangMenipis = new java.util.ArrayList<>();
        String sql = "SELECT nama_barang FROM stok_barang WHERE jumlah <= 2";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                barangMenipis.add(rs.getString("nama_barang"));
            }
        }
        if (barangMenipis.isEmpty()) return "";
        return "📦 PERINGATAN LOGISTIK: " + String.join(", ", barangMenipis) + " sudah menipis/habis!";
    }

    // Method untuk mengecek sisa stok barang secara real-time
    public static int getSisaStok(int idBarang) throws Exception {
        String sql = "SELECT jumlah FROM stok_barang WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idBarang);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("jumlah");
        }
        return 0;
    }

    // Method baru untuk cek status budget per kategori
    public static double[] getBudgetStatusKategori(int idKategori) throws Exception {
        // Mengembalikan array: [Batas, Terpakai, Sisa]
        String sql = "SELECT k.batas_anggaran, COALESCE(SUM(t.nominal), 0) as terpakai " +
                     "FROM kategori k LEFT JOIN transaksi t ON k.id = t.id_kategori " +
                     "WHERE k.id = ? GROUP BY k.id";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idKategori);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                double batas = rs.getDouble("batas_anggaran");
                double terpakai = rs.getDouble("terpakai");
                return new double[]{batas, terpakai, batas - terpakai};
            }
        }
        return new double[]{0, 0, 0};
    }

    // Method baru untuk validasi logika alokasi budget
    public static double hitungSisaBudgetUntukKategori(int idKategoriDikecualikan) throws Exception {
        // Jumlahkan semua batas_anggaran, KECUALI kategori yang sedang mau di-edit ini
        String sql = "SELECT COALESCE(SUM(batas_anggaran), 0) FROM kategori WHERE id != ?";
        try (Connection conn = getConnection(); java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idKategoriDikecualikan);
            java.sql.ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        }
        return 0;
    }

    // =========================================
    // METHOD UNTUK TAB HISTORI KESELURUHAN
    // =========================================

    public static DefaultTableModel getHistoriTableModel() throws Exception {
        String[] kolom = {"ID", "Tanggal", "Kategori", "Nama Transaksi", "Nominal"};
        DefaultTableModel model = new DefaultTableModel(kolom, 0) {
            @Override // Bikin tabel gak bisa diedit manual
            public boolean isCellEditable(int row, int column) { return false; }
        };

        // Ambil semua data dari transaksi, gabungkan dengan nama kategori, urutkan dari yang paling baru
        String query = "SELECT t.id, t.tanggal, k.nama_kategori, t.nama_barang, t.nominal " +
                       "FROM transaksi t JOIN kategori k ON t.id_kategori = k.id " +
                       "ORDER BY t.tanggal DESC, t.id DESC";

        try (Connection conn = getConnection(); java.sql.Statement stmt = conn.createStatement(); java.sql.ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("tanggal"),
                    rs.getString("nama_kategori"),
                    rs.getString("nama_barang"),
                    "Rp " + String.format("%,.0f", rs.getDouble("nominal")).replace(",", ".")
                });
            }
        }
        return model;
    }

    public static void hapusTransaksi(int idTransaksi) throws Exception {
        String sql = "DELETE FROM transaksi WHERE id = ?";
        try (Connection conn = getConnection(); java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idTransaksi);
            pstmt.executeUpdate();
        }
    }
}