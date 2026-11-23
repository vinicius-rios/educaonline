package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Matricula {
    public static final double MEDIA_MINIMA = 7.0;
    public static final double FREQUENCIA_MINIMA = 75.0;
    private final Aluno aluno;
    private final Turma turma;
    private final Set<Integer> aulasConcluidas = new HashSet<>();
    private final List<Avaliacao> avaliacoes = new ArrayList<>();

    public Matricula(Aluno aluno, Turma turma) {
        if (aluno == null || turma == null) {
            throw new IllegalArgumentException("Aluno e turma são obrigatórios");
        }
        this.aluno = aluno;
        this.turma = turma;
    }
    public void concluirAula(Aula aula) {
        if (aula == null) {
            throw new IllegalArgumentException("Aula obrigatória");
        }
        aulasConcluidas.add(aula.getId());
    }
    public void adicionarAvaliacao(Avaliacao avaliacao) {
        if (avaliacao == null) {
            throw new IllegalArgumentException("Avaliação obrigatória");
        }
        avaliacoes.add(avaliacao);
    }
    public double calcularMediaFinal() {
        double somaPesos = avaliacoes.stream().mapToDouble(Avaliacao::getPeso).sum();
        if (somaPesos == 0) return 0;
        double somaNotas = avaliacoes.stream().mapToDouble(a -> a.getNota() * a.getPeso()).sum();
        return somaNotas / somaPesos;
    }

    public double percentualConcluido() {
        int total = turma.getCurso().getTotalAulas();
        if (total == 0) return 0;
        return (aulasConcluidas.size() * 100.0) / total;
    }

    public boolean aprovado() {
        return calcularMediaFinal() >= MEDIA_MINIMA && percentualConcluido() >= FREQUENCIA_MINIMA;
    }

    public Aluno getAluno() {
        return aluno;
    }
    public Turma getTurma() {
        return turma;
    }
    public List<Avaliacao> getAvaliacoes() {
        return Collections.unmodifiableList(avaliacoes);
    }
    public int getQuantidadeAulasConcluidas() {
        return aulasConcluidas.size();
    }
}
