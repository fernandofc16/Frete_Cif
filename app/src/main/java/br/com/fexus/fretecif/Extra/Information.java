package br.com.fexus.fretecif.Extra;

public class Information {

    private String data, empresa, empresaDestiny,  notaFiscal, peso, valor;
    private int isColetaJuciliane;

    public Information(String data, String empresa, String empresaDestiny, String notaFiscal, String peso, String valor, int isColetaJuciliane) {
        this.data = data;
        this.empresa = empresa;
        this.empresaDestiny = empresaDestiny;
        this.notaFiscal = notaFiscal;
        this.peso = peso;
        this.valor = valor;
        this.isColetaJuciliane = isColetaJuciliane;
    }

    public String getData() { return data; }

    public void setData(String data) {
        this.data = data;
    }

    public String getEmpresa() {
        return empresa;
    }

    public String getEmpresaDestiny() {
        return empresaDestiny;
    }

    public String getNotaFiscal() {
        return notaFiscal;
    }

    public String getPeso() {
        return peso;
    }

    public String getValor() {
        return valor;
    }

    public int isColetaJuciliane() {
        return isColetaJuciliane;
    }
}
