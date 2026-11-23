package model;

public class PagamentoAvulso extends Pagamento {
    private final Curso curso;
    private final double valor;

    public PagamentoAvulso(int id, Aluno aluno, Curso curso, double valor) {
        super(id, aluno);
        if (curso == null) throw new IllegalArgumentException("Curso obrigatório");
        if (valor <= 0) throw new IllegalArgumentException("Valor inválido");
        this.curso = curso;
        this.valor = valor;
    }

    public Curso getCurso() {
        return curso;
    }

    @Override
    public double getValor() {
        return valor;
    }

    @Override
    public String getDescricao() {
        return "Curso avulso: " + curso.getNome();
    }
}
