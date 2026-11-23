package model;

import java.time.LocalDateTime;

public class Chamado implements Comparable<Chamado> {
    private final int id;
    private final Aluno aluno;
    private final String descricao;
    private final LocalDateTime dataAbertura;
    private StatusChamado status;

    public Chamado(int id, Aluno aluno, String descricao) {
        if (aluno == null) {
            throw new IllegalArgumentException("Aluno obrigatório");
        }
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("Descrição inválida");
        }
        this.id = id;
        this.aluno = aluno;
        this.descricao = descricao;
        this.dataAbertura = LocalDateTime.now();
        this.status = StatusChamado.ABERTO;
    }

    public void iniciarAtendimento() {
        if (status != StatusChamado.ABERTO) {
            throw new IllegalStateException("Só é possível atender um chamado aberto.");
        }
        status = StatusChamado.EM_ATENDIMENTO;
    }

    public void resolver() {
        if (status != StatusChamado.EM_ATENDIMENTO) {
            throw new IllegalStateException("O chamado precisa estar em atendimento para ser resolvido.");
        }
        status = StatusChamado.RESOLVIDO;
    }

    @Override
    public int compareTo(Chamado outro) {
        int porPrioridade = Integer.compare(aluno.prioridadeSuporte(), outro.aluno.prioridadeSuporte());
        if (porPrioridade != 0) return porPrioridade;
        return dataAbertura.compareTo(outro.dataAbertura);
    }

    public int getId() {
        return id;
    }
    public Aluno getAluno() {
        return aluno;
    }
    public String getDescricao() {
        return descricao;
    }
    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }
    public StatusChamado getStatus() {
        return status;
    }
}
