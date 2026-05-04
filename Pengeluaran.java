import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Class Pengeluaran
 *
 * Subclass dari abstract class Transaksi.
 * Secara otomatis juga mengimplementasikan interface ITransaksi
 * karena diwarisi dari Transaksi.
 *
 * Konsep yang diimplementasikan:
 * - Inheritance     : extends Transaksi
 * - Polymorphism    : @Override simpan() dan tampilkanDetail()
 * - Enkapsulasi     : akses field lewat getter dari superclass
 * - Abstract Class  : Transaksi adalah abstract, Pengeluaran adalah realisasinya
 * - Interface       : ITransaksi diwarisi dari Transaksi, kontraknya dipenuhi di sini
 *
 * PENTING: Constructor sengaja dipertahankan signature-nya agar
 * semua pemanggilan dari MainFrame.java tidak perlu diubah sama sekali.
 */
public class Pengeluaran extends Transaksi {

    /**
     * Constructor Pengeluaran.
     * Memanggil super() untuk mengisi field di parent class (Transaksi).
     *
     * Pemanggilan dari MainFrame tetap sama seperti sebelumnya:
     *   new Pengeluaran("Mie Instan", 3500, 2, "2025-01-15").simpan();
     */
    public Pengeluaran(String namaBarang, double nominal, int idKategori, String tanggal) {
        super(namaBarang, nominal, idKategori, tanggal);
    }

    /**
     * POLYMORPHISM — Override simpan() dari abstract class Transaksi.
     *
     * Transaksi mendeklarasikan simpan() sebagai abstract (tidak ada isi).
     * Di sini diisi dengan logika INSERT ke tabel transaksi di database.
     *
     * Kalau nanti ada class Pemasukan extends Transaksi,
     * simpan()-nya bisa berbeda (misal INSERT ke tabel pemasukan).
     * Itulah inti polymorphism: satu method, banyak perilaku.
     */
    @Override
    public void simpan() throws Exception {

        // Pakai isValid() yang diwarisi dari Transaksi (implements ITransaksi)
        if (!isValid()) {
            throw new Exception(
                "Data tidak valid! Pastikan nama transaksi, nominal (> 0), " +
                "kategori, dan tanggal sudah terisi dengan benar."
            );
        }

        String sql = "INSERT INTO transaksi (nama_barang, nominal, id_kategori, tanggal) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Akses data lewat getter (enkapsulasi) dari superclass Transaksi
            pstmt.setString(1, getNamaBarang());
            pstmt.setDouble(2, getNominal());
            pstmt.setInt(3,    getIdKategori());
            pstmt.setString(4, getTanggal());
            pstmt.executeUpdate();
        }
    }

    /**
     * POLYMORPHISM — Override tampilkanDetail() dari interface ITransaksi.
     *
     * ITransaksi mewajibkan method ini ada.
     * Versi Pengeluaran mencetak detail dengan label [PENGELUARAN]
     * agar berbeda dari subclass lain yang mungkin ditambahkan ke depannya.
     */
    @Override
    public void tampilkanDetail() {
        System.out.println("=== Detail Pengeluaran ===");
        System.out.println("Nama    : " + getNamaBarang());
        System.out.printf( "Nominal : Rp%,.0f%n", getNominal());
        System.out.println("Kategori: " + getIdKategori());
        System.out.println("Tanggal : " + getTanggal());
        System.out.println("Status  : " + (isValid() ? "Valid" : "Tidak Valid"));
        System.out.println("==========================");
    }
}