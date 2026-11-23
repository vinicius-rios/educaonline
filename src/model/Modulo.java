package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Modulo {
    private final int id;
    private final String nome;
    private final List<Aula> aulas = new ArrayList<>();

    public Modulo(int id, String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do módulo inválido");
        }
        this.id = id;
        this.nome = nome;
    }

    public void adicionarAula(Aula aula) {
        if (aula == null) throw new IllegalArgumentException("Aula obrigatória");
        aulas.add(aula);
    }

    public int getId() {
        return id;
    }
    public String getNome() {
        return nome;
    }
    public List<Aula> getAulas() {
        return Collections.unmodifiableList(aulas);
    }
}
