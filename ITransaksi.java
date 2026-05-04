/**
 * Interface ITransaksi
 *
 * Kontrak wajib yang harus dipenuhi oleh semua jenis transaksi di LOKOST.
 * Setiap class yang implements interface ini WAJIB mengisi:
 *   - simpan()          : logika menyimpan ke database
 *   - tampilkanDetail() : logika menampilkan info transaksi
 *
 * Konsep: Interface
 */
// Modul 6: Penggunaan Interface
public interface ITransaksi {

    // Method wajib diimplementasikan oleh setiap class
    void simpan() throws Exception;
    void tampilkanDetail();

    /**
     * Default method: validasi data dasar sebelum disimpan.
     * Boleh di-override di subclass, tapi tidak wajib.
     * Ini fitur interface Java 8+ (default method).
     */
    default boolean isValid() {
        return true;
    }
}