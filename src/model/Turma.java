package model;

import excecoes.MatriculaDuplicadaException;
import excecoes.TurmaCheiaException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Turma {
    public static final int LIMITE_ALUNOS = 40;
    private final int id;
    private final String nome;
    private final Curso curso;
    private final List<Aluno> alunos = new ArrayList<>();

    public Turma(int id, String nome, Curso curso) {
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome da turma inválido");
        if (curso == null) throw new IllegalArgumentException("Curso obrigatório");
        this.id = id;
        this.nome = nome;
        this.curso = curso;
    }

    public void matricular(Aluno aluno) throws TurmaCheiaException, MatriculaDuplicadaException {
        boolean jaMatriculado = alunos.stream().anyMatch(a -> a.getId() == aluno.getId());
        if (jaMatriculado) {
            throw new MatriculaDuplicadaException(aluno.getNome() + " já está matriculado nesta turma.");
        }
        if (alunos.size() >= LIMITE_ALUNOS) {
            throw new TurmaCheiaException("A turma " + nome + " atingiu o limite de " + LIMITE_ALUNOS + " alunos.");
        }
        alunos.add(aluno);
    }

    public int getId() {
        return id;
    }
    public String getNome() {
        return nome;
    }
    public Curso getCurso() {
        return curso;
    }
    public List<Aluno> getAlunos() {
        return Collections.unmodifiableList(alunos);
    }
    public int getVagasDisponiveis() {
        return LIMITE_ALUNOS - alunos.size();
    }
}