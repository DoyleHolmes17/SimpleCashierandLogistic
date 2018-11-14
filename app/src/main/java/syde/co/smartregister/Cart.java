package syde.co.smartregister;

/**
 * Created by programmer on 25-Jul-17.
 */

public class Cart {

    private String idBarcode;
    private String namaProduk;
    private int hargaProduk;
    private int jumlahProduk;

    public Cart(String idBarcode, String namaProduk, int hargaProduk, int jumlahProduk) {
        this.idBarcode = idBarcode;
        this.namaProduk = namaProduk;
        this.hargaProduk = hargaProduk;
        this.jumlahProduk = jumlahProduk;
    }

    public Cart() {

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
	
	public int getJumlahProduk() {
        return jumlahProduk;
    }

    public void setJumlahProduk(int jumlahProduk) {
        this.jumlahProduk = jumlahProduk;
    }

    public String toString() {
        return (idBarcode + "," + namaProduk + "," + hargaProduk + "," + jumlahProduk);
    }
	
}
