package model;

import java.time.LocalDate;

    public class Curso {
        private final int id;
        private String nome;
        private String categoria;
        private String area;
        private int cargaHoraria;
        private boolean exclusivo;
        private LocalDate dataLancamento;

        public Curso(int id, String nome, String categoria, String area,int cargaHoraria, boolean exclusivo, LocalDate dataLancamento) {
            if (nome == null || nome.isBlank()) {
                throw new IllegalArgumentException("Nome inválido");
            }
            if (cargaHoraria <= 0) {
                throw new IllegalArgumentException("Carga horária inválida");
            }
            this.id = id;
            this.nome = nome;
            this.categoria = categoria;
            this.area = area;
            this.cargaHoraria = cargaHoraria;
            this.exclusivo = exclusivo;
            this.dataLancamento = dataLancamento;
        }

        public int getId() {
            return id;
        }
        public String getNome() {
            return nome;
        }
        public String getCategoria() {
            return categoria;
        }
        public String getArea() {
            return area;
        }
        public int getCargaHoraria() {
            return cargaHoraria;
        }
        public boolean isExclusivo() {
            return exclusivo;
        }
        public LocalDate getDataLancamento() {
            return dataLancamento;
        }
    }

