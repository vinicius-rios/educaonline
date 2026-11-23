package model;

public abstract class Aluno {
    private final int id;
    private String nome;
    private String email;

    protected Aluno(int id, String nome, String email) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome inválido");
        }
        this.id = id;
        this.nome = nome;
        this.email = email;
    }

    public int getId() {
        return id;
    }
    public String getNome() {
        return nome;
    }
    public String getEmail() {
        return email;
    }

    public abstract boolean podeAcessar(Curso curso);
    public abstract int prioridadeSuporte();
    public abstract double valorMensalidade();
}
