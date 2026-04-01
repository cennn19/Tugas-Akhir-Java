import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

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

    // Method baru untuk GUI: Mengambil data View dan mengubahnya jadi Model Tabel
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
}