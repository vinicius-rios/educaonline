package model;

public abstract class Avaliacao {
    private final int id;
    private final String modulo;
    private final double peso;
    private double nota;

    protected Avaliacao(int id, String modulo, double peso, double nota) {
        if (peso <= 0){
            throw new IllegalArgumentException("Peso deve ser maior que zero");
        }
        this.id = id;
        this.modulo = modulo;
        this.peso = peso;
        setNota(nota);
    }

    public void setNota(double nota) {
        if (nota < 0 || nota > 10) {
            throw new IllegalArgumentException("Nota deve estar entre 0 e 10");
        }
        this.nota = nota;
    }

    public int getId() {
        return id;
    }
    public String getModulo() {
        return modulo;
    }
    public double getPeso() {
        return peso;
    }
    public double getNota() {
        return nota;
    }

    public abstract String getTipo();
}
