public abstract class EntitasKeuangan implements ITransaksi {
    private String nama;
    private double nominal;

    public EntitasKeuangan(String nama, double nominal) {
        this.nama = nama;
        this.nominal = nominal;
    }

    public String getNama() { return nama; }
    public double getNominal() { return nominal; }
    
    public abstract String getJenisTransaksi();
}