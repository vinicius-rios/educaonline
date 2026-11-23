package model;

public class Assinatura extends Pagamento {
    public Assinatura(int id, Aluno aluno) {
        super(id, aluno);
    }

    @Override
    public double getValor() {
        return getAluno().valorMensalidade();
    }

    @Override
    public String getDescricao() {
        String plano = (getAluno() instanceof AlunoVip) ? "VIP" : "Padrão";
        return "Assinatura mensal " + plano;
    }
}
