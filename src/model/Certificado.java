package model;

import excecoes.CertificadoNaoEmitidoException;

import java.time.LocalDate;
import java.util.UUID;

public class Certificado {
    private final String codigoValidacao;
    private final Aluno aluno;
    private final Curso curso;
    private final double mediaFinal;
    private final LocalDate dataEmissao;

    public Certificado(Matricula matricula) throws CertificadoNaoEmitidoException {
        if (!matricula.aprovado()) {
            throw new CertificadoNaoEmitidoException(String.format(
                    "Aluno não aprovado (média %.1f, %.0f%% das aulas concluídas). " +
                            "É preciso média %.1f e %.0f%% das aulas.",
                    matricula.calcularMediaFinal(), matricula.percentualConcluido(),
                    Matricula.MEDIA_MINIMA, Matricula.FREQUENCIA_MINIMA));
        }
        this.aluno = matricula.getAluno();
        this.curso = matricula.getTurma().getCurso();
        this.mediaFinal = matricula.calcularMediaFinal();
        this.dataEmissao = LocalDate.now();
        this.codigoValidacao = "EDU-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public String getCodigoValidacao() { return codigoValidacao; }
    public Aluno getAluno() {
        return aluno;
    }
    public Curso getCurso() {
        return curso;
    }
    public double getMediaFinal() {
        return mediaFinal;
    }
    public LocalDate getDataEmissao() {
        return dataEmissao;
    }
}