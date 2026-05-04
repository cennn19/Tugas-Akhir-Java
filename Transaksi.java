/**
 * Abstract Class Transaksi
 *
 * Blueprint dasar untuk semua jenis transaksi keuangan di LOKOST.
 * Mengimplementasikan interface ITransaksi — artinya class ini
 * bertanggung jawab memenuhi kontrak dari ITransaksi.
 *
 * Konsep yang diimplementasikan:
 * - Abstract Class  : class ini tidak bisa langsung di-instansiasi
 * - Interface       : implements ITransaksi
 * - Enkapsulasi     : field protected + getter, tidak ada setter
 * - Inheritance     : Pengeluaran akan extends class ini
 * - Polymorphism    : simpan() dan tampilkanDetail() abstract → di-override subclass
 */
public abstract class Transaksi implements ITransaksi {

    // =========================================
    // ENKAPSULASI: field protected supaya bisa
    // diakses subclass, tapi tidak bebas dari luar
    // =========================================
    protected String namaBarang;
    protected double nominal;
    protected int    idKategori;
    protected String tanggal;

    /**
     * Constructor: semua subclass wajib panggil super() ini.
     */
    public Transaksi(String namaBarang, double nominal, int idKategori, String tanggal) {
        this.namaBarang = namaBarang;
        this.nominal    = nominal;
        this.idKategori = idKategori;
        this.tanggal    = tanggal;
    }

    // =========================================
    // METHOD ABSTRACT dari ITransaksi:
    // Wajib di-override oleh setiap subclass.
    // Inilah inti polymorphism — tiap subclass
    // bisa punya implementasi simpan() yang berbeda.
    // =========================================
    @Override
    public abstract void simpan() throws Exception;

    @Override
    public abstract void tampilkanDetail();

    // =========================================
    // OVERRIDE default method dari ITransaksi.
    // Validasi: semua field wajib terisi dan valid.
    // =========================================
    @Override
    public boolean isValid() {
        return namaBarang != null
                && !namaBarang.trim().isEmpty()
                && nominal > 0
                && idKategori > 0
                && tanggal != null
                && !tanggal.trim().isEmpty();
    }

    // =========================================
    // GETTER: satu-satunya cara baca data dari luar.
    // Tidak ada setter — data hanya bisa diisi lewat constructor.
    // =========================================
    public String getNamaBarang() { return namaBarang; }
    public double getNominal()    { return nominal; }
    public int    getIdKategori() { return idKategori; }
    public String getTanggal()    { return tanggal; }
}