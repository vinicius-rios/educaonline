package repository;

import excecoes.MatriculaDuplicadaException;
import excecoes.TurmaCheiaException;
import model.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

public class ArquivoRepositorio {
    private static final Path PASTA = Paths.get("dados");
    private static final String SEP = ";";

    public void salvarTudo(List<Aluno> alunos, List<Curso> cursos, List<Turma> turmas,
                           List<Matricula> matriculas, List<Pagamento> pagamentos) throws IOException {
        Files.createDirectories(PASTA);

        List<String> linhas = new ArrayList<>();
        for (Aluno a : alunos) {
            linhas.add(juntar(a.getId(), a instanceof AlunoVip ? "VIP" : "PADRAO", a.getNome(), a.getEmail()));
        }
        gravar("alunos.csv", linhas);

        List<String> cursosCsv = new ArrayList<>();
        List<String> estrutura = new ArrayList<>();
        for (Curso c : cursos) {
            cursosCsv.add(juntar(c.getId(), c.getNome(), c.getCategoria(), c.getArea(),
                    c.getCargaHoraria(), c.isExclusivo(), c.getDataLancamento()));
            for (Modulo m : c.getModulos()) {
                estrutura.add(juntar("M", c.getId(), m.getId(), m.getNome()));
                for (Aula aula : m.getAulas()) {
                    estrutura.add(juntar("A", m.getId(), aula.getId(), aula.getTitulo()));
                }
            }
        }
        gravar("cursos.csv", cursosCsv);
        gravar("estrutura_cursos.csv", estrutura);

        linhas = new ArrayList<>();
        for (Turma t : turmas) {
            linhas.add(juntar(t.getId(), t.getNome(), t.getCurso().getId()));
        }
        gravar("turmas.csv", linhas);

        List<String> matriculasCsv = new ArrayList<>();
        List<String> avaliacoesCsv = new ArrayList<>();
        for (Matricula m : matriculas) {
            StringJoiner aulas = new StringJoiner(",");
            for (int id : m.getIdsAulasConcluidas()) aulas.add(String.valueOf(id));
            matriculasCsv.add(juntar(m.getAluno().getId(), m.getTurma().getId(), aulas));
            for (Avaliacao av : m.getAvaliacoes()) {
                avaliacoesCsv.add(juntar(m.getAluno().getId(), m.getTurma().getId(),
                        av.getTipo().equals("Prova") ? "PROVA" : "TRABALHO",
                        av.getId(), av.getModulo(), av.getPeso(), av.getNota()));
            }
        }
        gravar("matriculas.csv", matriculasCsv);
        gravar("avaliacoes.csv", avaliacoesCsv);

        linhas = new ArrayList<>();
        for (Pagamento p : pagamentos) {
            if (p instanceof PagamentoAvulso avulso) {
                linhas.add(juntar(p.getId(), "AVULSO", p.getAluno().getId(),
                        avulso.getCurso().getId(), p.getValor(), p.getData()));
            } else {
                linhas.add(juntar(p.getId(), "ASSINATURA", p.getAluno().getId(), 0, p.getValor(), p.getData()));
            }
        }
        gravar("pagamentos.csv", linhas);
    }

    public List<Aluno> carregarAlunos() {
        List<Aluno> lista = new ArrayList<>();
        for (String[] c : ler("alunos.csv")) {
            try {
                int id = Integer.parseInt(c[0]);
                lista.add(c[1].equals("VIP") ? new AlunoVip(id, c[2], c[3]) : new AlunoPadrao(id, c[2], c[3]));
            } catch (RuntimeException e) {
                avisoLinha("alunos.csv");
            }
        }
        return lista;
    }

    public List<Curso> carregarCursos() {
        List<Curso> lista = new ArrayList<>();
        Map<Integer, Curso> cursosPorId = new HashMap<>();
        for (String[] c : ler("cursos.csv")) {
            try {
                Curso curso = new Curso(Integer.parseInt(c[0]), c[1], c[2], c[3], Integer.parseInt(c[4]),
                        Boolean.parseBoolean(c[5]), LocalDate.parse(c[6]));
                lista.add(curso);
                cursosPorId.put(curso.getId(), curso);
            } catch (RuntimeException e) {
                avisoLinha("cursos.csv");
            }
        }
        Map<Integer, Modulo> modulosPorId = new HashMap<>();
        for (String[] c : ler("estrutura_cursos.csv")) {
            try {
                if (c[0].equals("M")) {
                    Curso curso = cursosPorId.get(Integer.parseInt(c[1]));
                    Modulo modulo = new Modulo(Integer.parseInt(c[2]), c[3]);
                    if (curso != null) {
                        curso.adicionarModulo(modulo);
                        modulosPorId.put(modulo.getId(), modulo);
                    }
                } else if (c[0].equals("A")) {
                    Modulo modulo = modulosPorId.get(Integer.parseInt(c[1]));
                    if (modulo != null) modulo.adicionarAula(new Aula(Integer.parseInt(c[2]), c[3]));
                }
            } catch (RuntimeException e) {
                avisoLinha("estrutura_cursos.csv");
            }
        }
        return lista;
    }

    public List<Turma> carregarTurmas(List<Curso> cursos) {
        List<Turma> lista = new ArrayList<>();
        for (String[] c : ler("turmas.csv")) {
            try {
                int cursoId = Integer.parseInt(c[2]);
                Curso curso = cursos.stream().filter(x -> x.getId() == cursoId).findFirst().orElse(null);
                if (curso == null) continue;
                lista.add(new Turma(Integer.parseInt(c[0]), c[1], curso));
            } catch (RuntimeException e) {
                avisoLinha("turmas.csv");
            }
        }
        return lista;
    }

    public List<Matricula> carregarMatriculas(List<Aluno> alunos, List<Turma> turmas) {
        List<Matricula> lista = new ArrayList<>();
        for (String[] c : ler("matriculas.csv")) {
            try {
                int alunoId = Integer.parseInt(c[0]);
                int turmaId = Integer.parseInt(c[1]);
                Aluno aluno = alunos.stream().filter(x -> x.getId() == alunoId).findFirst().orElse(null);
                Turma turma = turmas.stream().filter(x -> x.getId() == turmaId).findFirst().orElse(null);
                if (aluno == null || turma == null) continue;

                turma.matricular(aluno);
                Matricula matricula = new Matricula(aluno, turma);

                if (!c[2].isBlank()) {
                    Set<Integer> idsConcluidos = new HashSet<>();
                    for (String id : c[2].split(",")) idsConcluidos.add(Integer.parseInt(id.trim()));
                    for (Modulo modulo : turma.getCurso().getModulos()) {
                        for (Aula aula : modulo.getAulas()) {
                            if (idsConcluidos.contains(aula.getId())) matricula.concluirAula(aula);
                        }
                    }
                }
                lista.add(matricula);
            } catch (RuntimeException | TurmaCheiaException | MatriculaDuplicadaException e) {
                avisoLinha("matriculas.csv");
            }
        }
        return lista;
    }

    public void carregarAvaliacoes(List<Matricula> matriculas) {
        for (String[] c : ler("avaliacoes.csv")) {
            try {
                int alunoId = Integer.parseInt(c[0]);
                int turmaId = Integer.parseInt(c[1]);
                Matricula matricula = matriculas.stream()
                        .filter(m -> m.getAluno().getId() == alunoId && m.getTurma().getId() == turmaId)
                        .findFirst().orElse(null);
                if (matricula == null) continue;

                int id = Integer.parseInt(c[3]);
                String modulo = c[4];
                double peso = Double.parseDouble(c[5]);
                double nota = Double.parseDouble(c[6]);
                matricula.adicionarAvaliacao(c[2].equals("PROVA")
                        ? new Prova(id, modulo, peso, nota)
                        : new Trabalho(id, modulo, peso, nota));
            } catch (RuntimeException e) {
                avisoLinha("avaliacoes.csv");
            }
        }
    }

    public List<Pagamento> carregarPagamentos(List<Aluno> alunos, List<Curso> cursos) {
        List<Pagamento> lista = new ArrayList<>();
        for (String[] c : ler("pagamentos.csv")) {
            try {
                int id = Integer.parseInt(c[0]);
                int alunoId = Integer.parseInt(c[2]);
                int cursoId = Integer.parseInt(c[3]);
                double valor = Double.parseDouble(c[4]);
                LocalDate data = LocalDate.parse(c[5]);
                Aluno aluno = alunos.stream().filter(x -> x.getId() == alunoId).findFirst().orElse(null);
                if (aluno == null) continue;

                if (c[1].equals("ASSINATURA")) {
                    lista.add(new Assinatura(id, aluno, data));
                } else {
                    Curso curso = cursos.stream().filter(x -> x.getId() == cursoId).findFirst().orElse(null);
                    if (curso == null) continue;
                    lista.add(new PagamentoAvulso(id, aluno, curso, valor, data));
                }
            } catch (RuntimeException e) {
                avisoLinha("pagamentos.csv");
            }
        }
        return lista;
    }

    private String juntar(Object... partes) {
        StringJoiner joiner = new StringJoiner(SEP);
        for (Object parte : partes) {
            joiner.add(String.valueOf(parte).replace(SEP, ",").replace("\n", " "));
        }
        return joiner.toString();
    }

    private void gravar(String arquivo, List<String> linhas) throws IOException {
        Files.write(PASTA.resolve(arquivo), linhas, StandardCharsets.UTF_8);
    }

    private List<String[]> ler(String arquivo) {
        List<String[]> resultado = new ArrayList<>();
        Path caminho = PASTA.resolve(arquivo);
        if (!Files.exists(caminho)) return resultado;
        try {
            for (String linha : Files.readAllLines(caminho, StandardCharsets.UTF_8)) {
                if (!linha.isBlank()) resultado.add(linha.split(SEP, -1));
            }
        } catch (IOException e) {
            System.out.println("Aviso: não foi possível ler " + arquivo + " (" + e.getMessage() + ")");
        }
        return resultado;
    }

    private void avisoLinha(String arquivo) {
        System.out.println("Aviso: linha inválida em " + arquivo + " foi ignorada.");
    }
}