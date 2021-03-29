package shoppinglist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import shoppinglist.controller.controller;
import shoppinglist.entity.DaftarBelanja;
import shoppinglist.entity.DaftarBelanjaDetil;
import shoppinglist.repository.DaftarBelanjaRepo;
import shoppinglist.repository.DaftarBelanjaDetilRepo;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 *
 * @author fedlyseptian
 */

@SpringBootApplication
public class DemoShoppingListSpringBootApplication implements CommandLineRunner
{
    @Autowired
    private DaftarBelanjaRepo repo;

    @Autowired
    private DaftarBelanjaDetilRepo repoDetil;

    public static void main(String[] args)
    {
        SpringApplication.run(DemoShoppingListSpringBootApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception
    {
        Scanner scan = new Scanner(System.in);

        System.out.println();

        boolean stop = false;
        while (!stop)
        {
            System.out.println(
                    "\nSilakan pilih menu yang tersedia (1-7) : \n" +
                    "1. Lihat Semua Daftar Belanja\n" +
                    "2. Lihat Daftar Belanja Berdasarkan ID\n" +
                    "3. Lihat Daftar Belanja berdasarkan Judul\n" +
                    "4. Tambah Data Daftar Belanja Baru\n" +
                    "5. Update Data Daftar Belanja\n" +
                    "6. Hapus Data Daftar Belanja Berdasarkan ID\n" +
                    "7. Keluar\n" +
                    "================================================");
            String pilihanMenu = scan.nextLine();

            switch (pilihanMenu)
            {
                case "1":
                    controller.tampilkanSemuaDaftarBelanja(repo);
                    break;
                case "2":
                    controller.tampilkanDaftarBelanjaBerdasarkanId(repo);
                    break;
                case "3":
                    controller.tampilkanDaftarBelanjaBerdasarkanKemiripanJudul(repo);
                    break;
                case "4":
                    controller.tambahDaftarBelanja(repo);
                    break;
                case "5":
                    controller.updateDaftarBelanja(repo);
                    break;
                case "6":
                    controller.hapusDaftarBelanjaBerdasarkanId(repo, repoDetil);
                    break;
                default:
                    System.out.println("Exit success, thank you");
                    stop = true;
                    break;
            }
        }
    }
}
