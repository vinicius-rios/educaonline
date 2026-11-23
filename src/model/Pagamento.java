package model;

import java.time.LocalDate;

public abstract class Pagamento {
    private final int id;
    private final Aluno aluno;
    private final LocalDate data;

    protected Pagamento(int id, Aluno aluno) {
        if (aluno == null) {
            throw new IllegalArgumentException("Aluno obrigatório");
        }
        this.id = id;
        this.aluno = aluno;
        this.data = LocalDate.now();
    }

    public int getId() {
        return id;
    }
    public Aluno getAluno() {
        return aluno;
    }
    public LocalDate getData() {
        return data;
    }
    public abstract double getValor();
    public abstract String getDescricao();
}