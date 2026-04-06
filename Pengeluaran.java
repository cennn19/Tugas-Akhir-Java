public class Pengeluaran extends EntitasKeuangan {
    // Modul 1 Penggunaan tipe data
    private int idKategori;

    public Pengeluaran(String nama, double nominal, int idKategori) {
        super(nama, nominal);
        this.idKategori = idKategori;
    }

    @Override
    public String getJenisTransaksi() { return "Pengeluaran"; }

    @Override
    public void tampilkanDetail() {
        System.out.println("[-] " + getNama() + " | Rp" + getNominal());
    }

    @Override
    public void simpan() throws Exception {
        // Modul 2 Pemanggilan Method
        DatabaseHelper.simpanPengeluaran(getNama(), getNominal(), idKategori);
    }
}