package app;

import excecoes.CertificadoNaoEmitidoException;
import excecoes.MatriculaDuplicadaException;
import excecoes.TurmaCheiaException;
import excecoes.*;
import model.*;
import service.EducaOnlineService;

import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final EducaOnlineService servico = new EducaOnlineService();

    public static void main(String[] args) {
        int opcao = -1;
        while (opcao != 0) {
            exibirMenu();
            opcao = lerInt("Escolha: ");
            try {
                switch (opcao) {
                    case 1 -> cadastrarAluno();
                    case 2 -> cadastrarCurso();
                    case 3 -> criarTurma();
                    case 4 -> matricular();
                    case 5 -> concluirAula();
                    case 6 -> lancarAvaliacao();
                    case 7 -> emitirCertificado();
                    case 8 -> abrirChamado();
                    case 9 -> atenderChamado();
                    case 10 -> resolverChamado();
                    case 11 -> registrarPagamento();
                    case 12 -> System.out.println(servico.relatorioDesempenho());
                    case 13 -> System.out.println(servico.relatorioFinanceiro());
                    case 0 -> System.out.println("Encerrando...");
                    default -> System.out.println("Opção inválida.");
                }
            } catch (IllegalArgumentException | IllegalStateException | TurmaCheiaException
                     | MatriculaDuplicadaException | exception.AcessoNegadoException | CertificadoNaoEmitidoException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private static void exibirMenu() {
        System.out.println("\n===== EDUCAONLINE =====");
        System.out.println("1  - Cadastrar aluno");
        System.out.println("2  - Cadastrar curso");
        System.out.println("3  - Criar turma");
        System.out.println("4  - Matricular aluno");
        System.out.println("5  - Concluir aula");
        System.out.println("6  - Lançar avaliação");
        System.out.println("7  - Emitir certificado");
        System.out.println("8  - Abrir chamado de suporte");
        System.out.println("9  - Atender próximo chamado");
        System.out.println("10 - Resolver chamado");
        System.out.println("11 - Registrar pagamento");
        System.out.println("12 - Relatório de desempenho");
        System.out.println("13 - Relatório financeiro");
        System.out.println("0  - Sair");
    }

    private static void cadastrarAluno() {
        String nome = lerTexto("Nome: ");
        String email = lerTexto("E-mail: ");
        boolean vip = lerInt("Tipo (1-Padrão, 2-VIP): ") == 2;
        Aluno aluno = servico.cadastrarAluno(nome, email, vip);
        System.out.println("Aluno cadastrado com ID " + aluno.getId());
    }

    private static void cadastrarCurso() {
        String nome = lerTexto("Nome do curso: ");
        String categoria = lerTexto("Categoria: ");
        String area = lerTexto("Área de conhecimento: ");
        int carga = lerInt("Carga horária (h): ");
        boolean exclusivo = lerInt("Exclusivo VIP? (1-Sim, 2-Não): ") == 1;
        int dias = lerInt("Lançado há quantos dias? (0 = hoje): ");
        int modulos = lerInt("Quantidade de módulos: ");
        int aulas = lerInt("Aulas por módulo: ");
        Curso curso = servico.cadastrarCurso(nome, categoria, area, carga, exclusivo,
                LocalDate.now().minusDays(dias), modulos, aulas);
        System.out.println("Curso cadastrado com ID " + curso.getId());
    }

    private static void criarTurma() {
        listarCursos();
        int cursoId = lerInt("ID do curso: ");
        String nome = lerTexto("Nome da turma: ");
        Turma turma = servico.criarTurma(nome, cursoId);
        System.out.println("Turma criada com ID " + turma.getId());
    }

    private static void matricular() throws TurmaCheiaException, MatriculaDuplicadaException, exception.AcessoNegadoException {
        listarAlunos();
        listarTurmas();
        int alunoId = lerInt("ID do aluno: ");
        int turmaId = lerInt("ID da turma: ");
        servico.matricular(alunoId, turmaId);
        System.out.println("Matrícula realizada com sucesso.");
    }

    private static void concluirAula() {
        int alunoId = lerInt("ID do aluno: ");
        int turmaId = lerInt("ID da turma: ");
        Turma turma = servico.buscarTurma(turmaId);
        for (Modulo modulo : turma.getCurso().getModulos()) {
            for (Aula aula : modulo.getAulas()) {
                System.out.println(aula.getId() + " - " + aula.getTitulo());
            }
        }
        int aulaId = lerInt("ID da aula concluída: ");
        servico.concluirAula(alunoId, turmaId, aulaId);
        System.out.println("Aula registrada como concluída.");
    }

    private static void lancarAvaliacao() {
        int alunoId = lerInt("ID do aluno: ");
        int turmaId = lerInt("ID da turma: ");
        int tipo = lerInt("Tipo (1-Prova, 2-Trabalho): ");
        String modulo = lerTexto("Módulo: ");
        double peso = lerDouble("Peso: ");
        double nota = lerDouble("Nota (0 a 10): ");
        servico.lancarAvaliacao(alunoId, turmaId, tipo, modulo, peso, nota);
        System.out.println("Avaliação lançada.");
    }

    private static void emitirCertificado() throws CertificadoNaoEmitidoException {
        int alunoId = lerInt("ID do aluno: ");
        int turmaId = lerInt("ID da turma: ");
        Certificado c = servico.emitirCertificado(alunoId, turmaId);
        System.out.println("\n=== CERTIFICADO ===");
        System.out.println("Aluno: " + c.getAluno().getNome());
        System.out.println("Curso: " + c.getCurso().getNome());
        System.out.printf("Média final: %.1f%n", c.getMediaFinal());
        System.out.println("Emitido em: " + c.getDataEmissao());
        System.out.println("Código de validação: " + c.getCodigoValidacao());
    }

    private static void abrirChamado() {
        listarAlunos();
        int alunoId = lerInt("ID do aluno: ");
        String descricao = lerTexto("Descrição do problema: ");
        Chamado chamado = servico.abrirChamado(alunoId, descricao);
        System.out.println("Chamado aberto com ID " + chamado.getId());
    }

    private static void atenderChamado() {
        Chamado c = servico.atenderProximoChamado();
        String tipo = c.getAluno() instanceof AlunoVip ? " [VIP]" : "";
        System.out.println("Atendendo chamado " + c.getId() + " de " + c.getAluno().getNome() + tipo
                + ": " + c.getDescricao());
    }

    private static void resolverChamado() {
        int id = lerInt("ID do chamado: ");
        servico.resolverChamado(id);
        System.out.println("Chamado resolvido.");
    }

    private static void registrarPagamento() {
        listarAlunos();
        int alunoId = lerInt("ID do aluno: ");
        int tipo = lerInt("Tipo (1-Assinatura mensal, 2-Curso avulso): ");
        if (tipo == 1) {
            Pagamento p = servico.registrarAssinatura(alunoId);
            System.out.printf("%s registrada: R$ %.2f%n", p.getDescricao(), p.getValor());
        } else {
            listarCursos();
            int cursoId = lerInt("ID do curso: ");
            double valor = lerDouble("Valor (R$): ");
            Pagamento p = servico.registrarPagamentoAvulso(alunoId, cursoId, valor);
            System.out.printf("%s registrado: R$ %.2f%n", p.getDescricao(), p.getValor());
        }
    }

    private static void listarAlunos() {
        System.out.println("--- Alunos ---");
        for (Aluno a : servico.getAlunos()) {
            System.out.println(a.getId() + " - " + a.getNome() + (a instanceof AlunoVip ? " [VIP]" : ""));
        }
    }

    private static void listarCursos() {
        System.out.println("--- Cursos ---");
        for (Curso c : servico.getCursos()) {
            System.out.println(c.getId() + " - " + c.getNome() + (c.isExclusivo() ? " [exclusivo VIP]" : ""));
        }
    }

    private static void listarTurmas() {
        System.out.println("--- Turmas ---");
        for (Turma t : servico.getTurmas()) {
            System.out.println(t.getId() + " - " + t.getNome() + " (" + t.getCurso().getNome()
                    + ", vagas: " + t.getVagasDisponiveis() + ")");
        }
    }

    private static int lerInt(String msg) {
        while (true) {
            System.out.print(msg);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Digite um número inteiro válido.");
            }
        }
    }

    private static double lerDouble(String msg) {
        while (true) {
            System.out.print(msg);
            try {
                return Double.parseDouble(sc.nextLine().trim().replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Digite um número válido.");
            }
        }
    }
    private static String lerTexto(String msg) {
        System.out.print(msg);
        return sc.nextLine().trim();
    }
}