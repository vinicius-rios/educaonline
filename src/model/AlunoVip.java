package model;

import java.time.LocalDate;

public class AlunoVip extends Aluno {
    public AlunoVip(int id, String nome, String email) {
        super(id, nome, email);
    }

    @Override
    public boolean podeAcessar(Curso curso) {
        return !LocalDate.now().isBefore(curso.getDataLancamento());
    }

    @Override
    public int prioridadeSuporte() {
        return 1;
    }

    @Override
    public double valorMensalidade() {
        return 129.90;
    }
}
