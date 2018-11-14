package syde.co.smartregister;

/**
 * Created by programmer on 25-Jul-17.
 */

public class Kasir {

    private String idBarcode;
    private String namaProduk;
    private int hargaProduk;

    public Kasir(String idBarcode, String namaProduk, int hargaProduk) {
        this.idBarcode = idBarcode;
        this.namaProduk = namaProduk;
        this.hargaProduk = hargaProduk;
    }

    public Kasir() {

    }

    public String getIdBarcode() {
        return idBarcode;
    }

    public void setIdBarcode(String idBarcode) {
        this.idBarcode = idBarcode;
    }

    public String getNamaProduk() {
        return namaProduk;
    }

    public void setNamaProduk(String namaProduk) {
        this.namaProduk = namaProduk;
    }

    public int getHargaProduk() {
        return hargaProduk;
    }

    public void setHargaProduk(int hargaProduk) {
        this.hargaProduk = hargaProduk;
    }

    public String toString() {
        return (idBarcode + ", " + namaProduk + "," + hargaProduk);
    }
}
