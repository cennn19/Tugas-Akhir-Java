import java.sql.Connection;
import java.sql.PreparedStatement;

public class Pengeluaran {
    private String namaBarang;
    private double nominal;
    private int idKategori;
    private String tanggal;

    public Pengeluaran(String namaBarang, double nominal, int idKategori, String tanggal) {
        this.namaBarang = namaBarang;
        this.nominal = nominal;
        this.idKategori = idKategori;
        this.tanggal = tanggal;
    }

    public void simpan() throws Exception {
        String sql = "INSERT INTO transaksi (nama_barang, nominal, id_kategori, tanggal) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseHelper.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.namaBarang);
            pstmt.setDouble(2, this.nominal);
            pstmt.setInt(3, this.idKategori);
            pstmt.setString(4, this.tanggal);
            pstmt.executeUpdate();
        }
    }
}