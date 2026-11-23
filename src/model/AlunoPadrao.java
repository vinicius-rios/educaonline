package model;
import java.time.LocalDate;

public class AlunoPadrao extends Aluno {
    public AlunoPadrao(int id, String nome, String email) {
        super(id, nome, email);
    }

    @Override
    public boolean podeAcessar(Curso curso) {
        if (curso.isExclusivo()){
            return false;
        }
        return !LocalDate.now().isBefore(curso.getDataLancamento().plusDays(15));
    }

    @Override
    public int prioridadeSuporte() {
        return 2; }

    @Override
    public double valorMensalidade() {
        return 79.90; }
}
