package service;

import excecoes.CertificadoNaoEmitidoException;
import excecoes.MatriculaDuplicadaException;
import excecoes.TurmaCheiaException;
import excecoes.*;
import exception.AcessoNegadoException;
import model.*;
import repository.ArquivoRepositorio;
import java.io.IOException;

import java.time.LocalDate;
import java.util.*;

public class EducaOnlineService {
    private final List<Aluno> alunos = new ArrayList<>();
    private final List<Curso> cursos = new ArrayList<>();
    private final List<Turma> turmas = new ArrayList<>();
    private final List<Matricula> matriculas = new ArrayList<>();
    private final List<Certificado> certificados = new ArrayList<>();
    private final List<Pagamento> pagamentos = new ArrayList<>();
    private final List<Chamado> chamados = new ArrayList<>();
    private final PriorityQueue<Chamado> filaChamados = new PriorityQueue<>();
    private final ArquivoRepositorio repositorio = new ArquivoRepositorio();

    private int proximoId = 1;

    private int novoId() { return proximoId++; }
        public EducaOnlineService() {
            carregar();
        }

        private void carregar() {
            alunos.addAll(repositorio.carregarAlunos());
            cursos.addAll(repositorio.carregarCursos());
            turmas.addAll(repositorio.carregarTurmas(cursos));
            matriculas.addAll(repositorio.carregarMatriculas(alunos, turmas));
            repositorio.carregarAvaliacoes(matriculas);
            pagamentos.addAll(repositorio.carregarPagamentos(alunos, cursos));
            atualizarProximoId();
        }

        public void salvar() throws IOException {
            repositorio.salvarTudo(alunos, cursos, turmas, matriculas, pagamentos);
        }

        private void atualizarProximoId() {
            int maior = 0;
            for (Aluno a : alunos) maior = Math.max(maior, a.getId());
            for (Curso c : cursos) {
                maior = Math.max(maior, c.getId());
                for (Modulo m : c.getModulos()) {
                    maior = Math.max(maior, m.getId());
                    for (Aula aula : m.getAulas()) maior = Math.max(maior, aula.getId());
                }
            }
            for (Turma t : turmas) maior = Math.max(maior, t.getId());
            for (Matricula m : matriculas) {
                for (Avaliacao av : m.getAvaliacoes()) maior = Math.max(maior, av.getId());
            }
            for (Pagamento p : pagamentos) maior = Math.max(maior, p.getId());
            proximoId = maior + 1;
        }

    public Aluno cadastrarAluno(String nome, String email, boolean vip) {
        Aluno aluno = vip ? new AlunoVip(novoId(), nome, email)
                : new AlunoPadrao(novoId(), nome, email);
        alunos.add(aluno);
        return aluno;
    }

    public Curso cadastrarCurso(String nome, String categoria, String area, int cargaHoraria,
                                boolean exclusivo, LocalDate lancamento,
                                int qtdModulos, int aulasPorModulo) {
        if (qtdModulos <= 0 || aulasPorModulo <= 0) {
            throw new IllegalArgumentException("Quantidade de módulos e aulas deve ser maior que zero");
        }
        Curso curso = new Curso(novoId(), nome, categoria, area, cargaHoraria, exclusivo, lancamento);
        for (int m = 1; m <= qtdModulos; m++) {
            Modulo modulo = new Modulo(novoId(), "Módulo " + m);
            for (int a = 1; a <= aulasPorModulo; a++) {
                modulo.adicionarAula(new Aula(novoId(), "Aula " + a + " do Módulo " + m));
            }
            curso.adicionarModulo(modulo);
        }
        cursos.add(curso);
        return curso;
    }

    public Turma criarTurma(String nome, int cursoId) {
        Turma turma = new Turma(novoId(), nome, buscarCurso(cursoId));
        turmas.add(turma);
        return turma;
    }

    public Matricula matricular(int alunoId, int turmaId)
            throws TurmaCheiaException, MatriculaDuplicadaException, AcessoNegadoException {
        Aluno aluno = buscarAluno(alunoId);
        Turma turma = buscarTurma(turmaId);
        if (!aluno.podeAcessar(turma.getCurso())) {
            throw new AcessoNegadoException(aluno.getNome() + " ainda não tem acesso ao curso "
                    + turma.getCurso().getNome() + " (exclusivo VIP ou lançamento recente).");
        }
        turma.matricular(aluno);
        Matricula matricula = new Matricula(aluno, turma);
        matriculas.add(matricula);
        return matricula;
    }

    public void concluirAula(int alunoId, int turmaId, int aulaId) {
        Matricula matricula = buscarMatricula(alunoId, turmaId);
        for (Modulo modulo : matricula.getTurma().getCurso().getModulos()) {
            for (Aula aula : modulo.getAulas()) {
                if (aula.getId() == aulaId) {
                    matricula.concluirAula(aula);
                    return;
                }
            }
        }
        throw new IllegalArgumentException("Aula não encontrada neste curso: " + aulaId);
    }

    public void lancarAvaliacao(int alunoId, int turmaId, int tipo, String modulo, double peso, double nota) {
        Matricula matricula = buscarMatricula(alunoId, turmaId);
        Avaliacao avaliacao = (tipo == 1)
                ? new Prova(novoId(), modulo, peso, nota)
                : new Trabalho(novoId(), modulo, peso, nota);
        matricula.adicionarAvaliacao(avaliacao);
    }

    public Certificado emitirCertificado(int alunoId, int turmaId) throws CertificadoNaoEmitidoException {
        Certificado certificado = new Certificado(buscarMatricula(alunoId, turmaId));
        certificados.add(certificado);
        return certificado;
    }

    public Chamado abrirChamado(int alunoId, String descricao) {
        Chamado chamado = new Chamado(novoId(), buscarAluno(alunoId), descricao);
        chamados.add(chamado);
        filaChamados.add(chamado);
        return chamado;
    }

    public Chamado atenderProximoChamado() {
        Chamado chamado = filaChamados.poll();
        if (chamado == null) throw new IllegalStateException("Não há chamados abertos na fila.");
        chamado.iniciarAtendimento();
        return chamado;
    }

    public void resolverChamado(int chamadoId) {
        Chamado chamado = chamados.stream().filter(c -> c.getId() == chamadoId).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Chamado não encontrado: " + chamadoId));
        chamado.resolver();
    }

    public Pagamento registrarAssinatura(int alunoId) {
        Pagamento pagamento = new Assinatura(novoId(), buscarAluno(alunoId), LocalDate.now());
        pagamentos.add(pagamento);
        return pagamento;
    }

    public Pagamento registrarPagamentoAvulso(int alunoId, int cursoId, double valor) {
        Pagamento pagamento = new PagamentoAvulso(novoId(), buscarAluno(alunoId), buscarCurso(cursoId), valor);
        pagamentos.add(pagamento);
        return pagamento;
    }

    public String relatorioDesempenho() {
        if (matriculas.isEmpty()) return "Nenhuma matrícula registrada.";
        StringBuilder sb = new StringBuilder("=== RELATÓRIO DE DESEMPENHO ===\n");
        for (Matricula m : matriculas) {
            sb.append(String.format("%s | %s (%s) | média: %.1f | aulas: %.0f%% | %s%n",
                    m.getAluno().getNome(), m.getTurma().getCurso().getNome(), m.getTurma().getNome(),
                    m.calcularMediaFinal(), m.percentualConcluido(),
                    m.aprovado() ? "APROVADO" : "NÃO APROVADO"));
        }
        return sb.toString();
    }

    public String relatorioFinanceiro() {
        if (pagamentos.isEmpty()) return "Nenhum pagamento registrado.";
        StringBuilder sb = new StringBuilder("=== HISTÓRICO FINANCEIRO ===\n");
        double total = 0;
        double assinaturas = 0;
        Map<String, Double> porCurso = new LinkedHashMap<>();
        for (Pagamento p : pagamentos) {
            sb.append(String.format("%s | %s | %s | R$ %.2f%n",
                    p.getData(), p.getAluno().getNome(), p.getDescricao(), p.getValor()));
            total += p.getValor();
            if (p instanceof PagamentoAvulso avulso) {
                porCurso.merge(avulso.getCurso().getNome(), p.getValor(), Double::sum);
            } else {
                assinaturas += p.getValor();
            }
        }
        sb.append("\n=== RENDIMENTO ===\n");
        for (Map.Entry<String, Double> e : porCurso.entrySet()) {
            sb.append(String.format("Curso %s (avulso): R$ %.2f%n", e.getKey(), e.getValue()));
        }
        sb.append(String.format("Assinaturas: R$ %.2f%n", assinaturas));
        sb.append(String.format("TOTAL: R$ %.2f%n", total));
        return sb.toString();
    }

    public List<Aluno> getAlunos() { return Collections.unmodifiableList(alunos); }
    public List<Curso> getCursos() { return Collections.unmodifiableList(cursos); }
    public List<Turma> getTurmas() { return Collections.unmodifiableList(turmas); }

    public Turma buscarTurma(int id) {
        return turmas.stream().filter(t -> t.getId() == id).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Turma não encontrada: " + id));
    }

    private Aluno buscarAluno(int id) {
        return alunos.stream().filter(a -> a.getId() == id).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado: " + id));
    }

    private Curso buscarCurso(int id) {
        return cursos.stream().filter(c -> c.getId() == id).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Curso não encontrado: " + id));
    }

    private Matricula buscarMatricula(int alunoId, int turmaId) {
        return matriculas.stream()
                .filter(m -> m.getAluno().getId() == alunoId && m.getTurma().getId() == turmaId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Matrícula não encontrada para esse aluno e turma."));
    }
}
