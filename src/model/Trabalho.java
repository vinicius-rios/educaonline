package model;

public class Trabalho extends Avaliacao {
    public Trabalho(int id, String modulo, double peso, double nota) {
        super(id, modulo, peso, nota);
    }

    @Override
    public String getTipo() {
        return "Trabalho";
    }
}