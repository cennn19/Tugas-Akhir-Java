// MODUL 6: Penggunaan Abstrak Class
public abstract class EntitasKeuangan implements ITransaksi {

    // Modul 4: Menyembunyikan Data
    private String nama;
    private double nominal;

    public EntitasKeuangan(String nama, double nominal) {
        // Modul 3: Membedakan Atribut
        this.nama = nama;
        this.nominal = nominal;
    }

    public String getNama() { return nama; }
    public double getNominal() { return nominal; }
    
    public abstract String getJenisTransaksi();
}