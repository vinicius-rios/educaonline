package model;

public class Prova extends Avaliacao {
    public Prova(int id, String modulo, double peso, double nota) {
        super(id, modulo, peso, nota);
    }

    @Override
    public String getTipo() { return "Prova"; }
}
