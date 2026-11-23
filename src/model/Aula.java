package model;

public class Aula {
    private final int id;
    private final String titulo;

    public Aula(int id, String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("Título da aula inválido");
        }
        this.id = id;
        this.titulo = titulo;
    }

    public int getId() {
        return id;
    }
    public String getTitulo() {
        return titulo;
    }
}