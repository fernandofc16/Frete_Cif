package br.com.fexus.fretecif.Extra;

public class Information {

    private String data, empresa, empresaDestiny,  notaFiscal, peso, valor;
    private int isColetaJuciliane;

    public Information() {
        //Empty class creation
    }

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

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getEmpresaDestiny() {
        return empresaDestiny;
    }

    public void setEmpresaDestiny(String empresaDestiny) {
        this.empresaDestiny = empresaDestiny;
    }

    public String getNotaFiscal() {
        return notaFiscal;
    }

    public void setNotaFiscal(String notaFiscal) {
        this.notaFiscal = notaFiscal;
    }

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public int isColetaJuciliane() {
        return isColetaJuciliane;
    }
}
