package model;

import java.time.LocalDate;

public class Assinatura extends Pagamento {
    public Assinatura(int id, Aluno aluno, LocalDate data) {
        super(id, aluno, data);
    }

    @Override
    public double getValor() { return getAluno().valorMensalidade(); }

    @Override
    public String getDescricao() {
        String plano = (getAluno() instanceof AlunoVip) ? "VIP" : "Padrão";
        return "Assinatura mensal " + plano;
    }
}