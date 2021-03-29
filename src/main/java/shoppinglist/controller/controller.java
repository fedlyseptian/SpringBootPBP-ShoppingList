package shoppinglist.controller;

import jdk.vm.ci.meta.Local;
import org.springframework.data.domain.Sort;
import shoppinglist.entity.DaftarBelanja;
import shoppinglist.entity.DaftarBelanjaDetil;
import shoppinglist.repository.DaftarBelanjaDetilRepo;
import shoppinglist.repository.DaftarBelanjaRepo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 *
 * @author fedlyseptian
 */

public class controller {
    public static Scanner scan = new Scanner(System.in);

    public static long bacaIdTerakhir(DaftarBelanjaRepo repo)
    {
        DaftarBelanja daftarBelanja = repo.findTopByOrderByIdDesc();
        return daftarBelanja.getId() + 1;
    }

    public static void tampilkanSemuaDaftarBelanja(DaftarBelanjaRepo repo)
    {
        System.out.println("\nSemua Daftar Belanja");

        List<DaftarBelanja> semuaDaftarBelanja = repo.findAll(Sort.by(Sort.Direction.ASC, "id"));

        for (DaftarBelanja db : semuaDaftarBelanja)
        {
            System.out.println(db.getId() + " - " + db.getJudul());

            List<DaftarBelanjaDetil> listBarangBelanjaDetil = db.getDaftarBarang();
            for (DaftarBelanjaDetil barang : listBarangBelanjaDetil)
            {
                System.out.println("\t  " + barang.getNamaBarang() + " " + barang.getByk() + " " + barang.getSatuan());
            }
        }
        System.out.println();
    }

    public static void bacaSemuaJudul(DaftarBelanjaRepo repo)
    {
        System.out.println();

        List<DaftarBelanja> semuaDaftarBelanja = repo.findAll(Sort.by(Sort.Direction.ASC, "id"));

        for (DaftarBelanja db : semuaDaftarBelanja)
        {
            System.out.println(db.getId() + " - " + db.getJudul());
        }
        System.out.println();
    }

    public static void tampilkanDaftarBelanjaBerdasarkanId(DaftarBelanjaRepo repo)
    {
        System.out.println("\nMasukkan ID DaftarBelanja (0 untuk batal) : ");
        long id = Long.parseLong(scan.nextLine());

        if (id != 0)
        {
            System.out.println("ID : " + id);

            Optional<DaftarBelanja> optionalDb = repo.findById(id);
            if (optionalDb.isPresent())
            {
                DaftarBelanja db = optionalDb.get();
                System.out.println("\tJudul : "  + db.getJudul() + "\n\tWaktu : " + db.getTanggal());
            }
            else
            {
                System.out.println("Daftar Belanja dengan id (" + id + ") tidak ditemukan");
            }
            System.out.println();
        }
    }

    public static void tampilkanDaftarBelanjaBerdasarkanKemiripanJudul(DaftarBelanjaRepo repo)
    {
        System.out.println("\nMasukkan Judul DaftarBelanja (0 untuk batal) :");
        String judul = scan.nextLine().trim();

        if (!judul.equals("0"))
        {
            System.out.println("Kemiripan : " + judul);

            List<DaftarBelanja> containingDB = repo.findByJudulIgnoreCaseContaining(judul);

            if (containingDB.isEmpty())
            {
                System.out.println("Daftar Belanja dengan kemiripan judul (" + judul + ") tidak ditemukan");
            }
            else
            {
                for (DaftarBelanja db : containingDB)
                {
                    System.out.println(db.getId() + " - " + db.getJudul());
                }
            }
            System.out.println();
        }
    }

    public static void tambahDaftarBelanja(DaftarBelanjaRepo repo)
    {
        System.out.println("Judul (0 untuk batal) : ");
        String judul = scan.nextLine().trim();

        if (!judul.equals("0"))
        {
            long idDaftarBelanja = bacaIdTerakhir(repo);
            LocalDateTime waktu = LocalDateTime.now().withNano(0);

            DaftarBelanja daftarBelanja = new DaftarBelanja();
            daftarBelanja.setId(idDaftarBelanja);
            daftarBelanja.setJudul(judul);
            daftarBelanja.setTanggal(waktu);
            repo.save(daftarBelanja);

            boolean isStop = false;
            int count = 0;
            while (!isStop)
            {
                DaftarBelanjaDetil detilBarang = new DaftarBelanjaDetil();
                count++;

                System.out.println("\nMasukkan Data Barang : ");
                System.out.println(count + ". ");

                System.out.println("Nama Barang : ");
                String namabarang = scan.nextLine().trim();

                System.out.println("Jumlah      : ");
                float jumlah = scan.nextFloat();

                scan.nextLine();

                System.out.println("Satuan      : ");
                String satuan = scan.nextLine();

                System.out.println("Memo        : ");
                String memo = scan.nextLine().trim();

                System.out.println("Simpan ? (Y/N)");
                String isSimpan = scan.nextLine().trim();

                if (isSimpan.equals("y") || isSimpan.equals("Y"))
                {
                    detilBarang.setId(daftarBelanja.getId(), count);
                    detilBarang.setNamaBarang(namabarang);
                    detilBarang.setByk(jumlah);
                    detilBarang.setSatuan(satuan);
                    detilBarang.setMemo(memo);
                    daftarBelanja.addDaftarBarang(detilBarang);
                }
                else
                {
                    count--;
                }

                System.out.println("Tambah Lagi ? (Y/N)");
                String isLanjut = scan.nextLine().trim();

                if (!isLanjut.equals("y") || !isLanjut.equals("Y"))
                {
                    isStop = true;
                }
            }

            // Mengirim detil barang ke database
            repo.save(daftarBelanja);

            System.out.println("Data baru berhasil disimpan ke database\n");
            System.out.println("ID    : " + daftarBelanja.getId());
            System.out.println("Judul : " + daftarBelanja.getJudul());
            System.out.println("Waktu : " + daftarBelanja.getTanggal());

            System.out.println();
        }
    }

    private static void bacaListBarangDaftarBelanjaDetil(List<DaftarBelanjaDetil> daftarBarang)
    {
        System.out.println();
        for (DaftarBelanjaDetil barang : daftarBarang)
        {
            System.out.println(
                    barang.getId().getNoUrut() + ". \n" +
                    "Nama Barang : " + barang.getNamaBarang() +
                    "\nJumlah Barang : " + barang.getByk() + " " + barang.getSatuan() +
                    "\nMemo : " + barang.getMemo() + "\n"
            );
        }
    }

    private static void bacaBarangDaftarBelanjaDetil(DaftarBelanjaDetil barang)
    {
        System.out.println(
                "\n" +
                barang.getId().getNoUrut() + ". \n" +
                "Nama Barang : " + barang.getNamaBarang() +
                "\nJumlah Barang : " + barang.getByk() + " " + barang.getSatuan() +
                "\nMemo : " + barang.getMemo() +
                "\n"
        );
    }

    public static void updateDaftarBelanja(DaftarBelanjaRepo repo)
    {
        bacaSemuaJudul(repo);

        System.out.println("Masukkan ID DaftarBelanja yang ingin di update data nya (0 untuk batal) : ");
        long id = Long.parseLong(scan.nextLine());

        if (id != 0)
        {
            System.out.println("ID : " + id);
            Optional<DaftarBelanja> optionalDb = repo.findById(id);
            if (optionalDb.isPresent())
            {
                DaftarBelanja daftarBelanja = optionalDb.get();
                System.out.println(
                        "Judul : " + daftarBelanja.getJudul() +
                        "\nWaktu : " + daftarBelanja.getTanggal() +
                        "\n"
                );

                LocalDateTime waktuUpdate = LocalDateTime.now().withNano(0);
                System.out.println("Waktu update : " + waktuUpdate);

                System.out.println("Judul Baru : ");
                String judulBaru = scan.nextLine().trim();

                daftarBelanja.setJudul(judulBaru);
                daftarBelanja.setTanggal(waktuUpdate);

                List<DaftarBelanjaDetil> daftarBarang = daftarBelanja.getDaftarBarang();

                boolean isStop = false;
                while (!isStop)
                {
                    bacaListBarangDaftarBelanjaDetil(daftarBarang);

                    System.out.println("Masukkan ID barang yang ingin di update (0 untuk batal) : ");
                    int noUrutBarang = Integer.parseInt(scan.nextLine());

                    if (noUrutBarang != 0)
                    {
                        DaftarBelanjaDetil barang = daftarBelanja.getBarang(noUrutBarang);
                        bacaBarangDaftarBelanjaDetil(barang);

                        System.out.println("Masukkan Data Baru");
                        System.out.println("Nama Barang : ");
                        String namaBarangBaru = scan.nextLine().trim();

                        System.out.println("Jumlah : ");
                        float jumlahBaru = scan.nextFloat();

                        scan.nextLine();

                        System.out.println("Satuan : ");
                        String satuanBaru = scan.nextLine();

                        System.out.println("Memo : ");
                        String memoBaru = scan.nextLine().trim();

                        System.out.println("Simpan Data Baru Tersebut? (Y/N)");
                        String konfirmasi = scan.nextLine().trim();

                        if(konfirmasi.equals("y") || konfirmasi.equals("Y"))
                        {
                            barang.setNamaBarang(namaBarangBaru);
                            barang.setByk(jumlahBaru);
                            barang.setSatuan(satuanBaru);
                            barang.setMemo(memoBaru);
                            daftarBelanja.setBarang(barang, noUrutBarang);
                        }

                        System.out.println("Apakah Anda ingin melakukan update lagi? (Y/N)");
                        String lanjut = scan.nextLine().trim();

                        if (!lanjut.equals("y") || !lanjut.equals("Y"))
                        {
                            isStop = true;
                        }
                    }
                    else
                    {
                        isStop = true;
                    }
                }
                //Menyimpan Data Baru Daftar Belanja Ke Database
                repo.save(daftarBelanja);

                System.out.println("\nData berhasil di update, dengan data sebagai berikut : ");
                System.out.println("ID    : " + daftarBelanja.getId());
                System.out.println("Judul : " + daftarBelanja.getJudul());
                System.out.println("Waktu : " + daftarBelanja.getTanggal());

                System.out.println("Barang : ");
                for (DaftarBelanjaDetil barang : daftarBarang)
                {
                    System.out.println(
                            barang.getNamaBarang() + " " +
                            barang.getByk() + " " +
                            barang.getSatuan()
                    );
                }
            }
        }
        else
        {
            System.out.println("Daftar belanja dengan id (" + id + ") tidak ditemukan");
        }
    }

    public static void hapusDaftarBelanjaBerdasarkanId(DaftarBelanjaRepo repo, DaftarBelanjaDetilRepo repoDetil)
    {
        bacaSemuaJudul(repo);

        System.out.println("Masukkan ID DaftarBelanja yang ingin dihapus data nya (0 untuk batal) :");
        long id = Long.parseLong(scan.nextLine());

        if (id != 0)
        {
            System.out.println("ID : " + id);
            Optional<DaftarBelanja> optionalDb = repo.findById(id);
            if (optionalDb.isPresent())
            {
                DaftarBelanja daftarBelanja = optionalDb.get();
                System.out.println("\tJudul : "  + daftarBelanja.getJudul() + "\n\tWaktu : " + daftarBelanja.getTanggal() + "\n");

                System.out.println("Apakah anda yakin ingin menghapus data dari ID " + id + " ? (Y/N)");
                String konfirmasiHapus = scan.nextLine();

                if (konfirmasiHapus.equals("y") || konfirmasiHapus.equals("Y"))
                {
                    // Menghapus Detil Belanja
                    repoDetil.deleteByDaftarbelanja_id(daftarBelanja.getId());

                    // Menghapus Daftar Belanja
                    repo.deleteById(id);
                    System.out.println("Data berhasil dihapus");
                }
                else
                {
                    System.out.println("Data tidak jadi dihapus");
                }
            }
            else
            {
                System.out.println("Daftar Belanja dengan id (" + id + ") tidak ditemukan");
            }
            System.out.println();
        }
    }

}
