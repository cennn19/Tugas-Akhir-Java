import java.util.List;
import java.util.Scanner;

public class MainApp {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        boolean jalan = true;

       while (jalan) {
            System.out.println("\n=== APP LOGISTIK KOS ===");
            System.out.println("1. Catat Kebutuhan");
            System.out.println("2. Catat Keinginan");
            System.out.println("3. Dashboard Laporan");
            System.out.println("4. Atur Batas Budget"); // <-- MENU BARU
            System.out.println("0. Keluar");
            System.out.print("Pilih menu: ");
            int pilihan = input.nextInt(); input.nextLine();

            if (pilihan == 0) {
                System.out.println("Aplikasi ditutup. Semangat hematnya!");
                break;
            } else if (pilihan == 3) {
                try { DatabaseHelper.lihatDashboard(); } catch (Exception e) { System.out.println("Gagal: " + e.getMessage()); }
                continue;
            } else if (pilihan == 4) { // <-- LOGIKA MENU BARU
                System.out.println("\n--- ATUR BUDGET ---");
                System.out.println("1. Budget Keseluruhan (Bulanan)");
                System.out.println("2. Budget Per Kategori");
                System.out.print("Pilih: ");
                int pilBudget = input.nextInt(); input.nextLine();

                try {
                    if (pilBudget == 1) {
                        System.out.print("Masukkan Batas Budget Bulanan Baru: Rp");
                        double budgetBaru = input.nextDouble();
                        DatabaseHelper.updateBudgetGlobal(budgetBaru);
                    } else if (pilBudget == 2) {
                        // Tampilkan semua kategori dulu biar user bisa milih
                        DatabaseHelper.lihatDashboard(); 
                        System.out.print("Masukkan ID Kategori yang mau diubah: ");
                        int idKat = input.nextInt();
                        System.out.print("Masukkan Batas Budget Baru: Rp");
                        double budgetKatBaru = input.nextDouble();
                        DatabaseHelper.updateBudgetKategori(idKat, budgetKatBaru);
                    }
                } catch (Exception e) {
                    System.err.println("Gagal update budget: " + e.getMessage());
                }
                continue;

            } else if (pilihan == 1 || pilihan == 2) {
                // ... (KODE CATAT KEBUTUHAN/KEINGINAN TETAP SAMA SEPERTI SEBELUMNYA) ...
                String tipe = (pilihan == 1) ? "Kebutuhan" : "Keinginan";
                try {
                    List<String[]> kategori = DatabaseHelper.getKategori(tipe);
                    System.out.println("\n-- Kategori " + tipe + " --");
                    for (int i = 0; i < kategori.size(); i++) {
                        System.out.println((i+1) + ". " + kategori.get(i)[1]);
                    }
                    System.out.print("Pilih no kategori: ");
                    int noKat = input.nextInt(); input.nextLine();
                    int idKat = Integer.parseInt(kategori.get(noKat - 1)[0]);

                    System.out.print("Nama Barang: "); String nama = input.nextLine();
                    System.out.print("Nominal: Rp"); double nominal = input.nextDouble();

                    // Polymorphism
                    EntitasKeuangan transaksi = new Pengeluaran(nama, nominal, idKat);
                    transaksi.tampilkanDetail();
                    
                    // Eksekusi (akan memicu trigger jika limit habis)
                    transaksi.simpan(); 

                } catch (Exception e) {
                    System.err.println("\n[ALARM/GAGAL] " + e.getMessage());
                }
            } else {
                System.out.println("Pilihan tidak valid!");
            }
        }
        input.close();

        
    }
}