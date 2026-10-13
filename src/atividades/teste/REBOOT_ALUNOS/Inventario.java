package atividades.teste.REBOOT_ALUNOS;

import java.util.Arrays;

/** Missao 1. Edite apenas esta classe. Capacidade fixa, vetor compacto. */
public class Inventario {
    private final int[] dados;
    private int tamanho;

    public Inventario(int capacidade) {
        if (capacidade <= 0) throw new IllegalArgumentException("capacidade deve ser positiva");
        dados = new int[capacidade];
    }
    public int tamanho() { return tamanho; }
    public int capacidade() { return dados.length; }
    public int[] itens() { return Arrays.copyOf(dados, tamanho); }

    public void inserir(int indice, int valor) {
        if (indice < 0 || indice > tamanho) throw new IndexOutOfBoundsException("indice invalido");
        if (tamanho == dados.length) throw new IllegalStateException("inventario cheio");
        // Desloque sem perder os valores anteriores.
        for (int i = tamanho - 1; i >= indice; i--) {
            dados[i + 1] = dados[i];
        }
        dados[indice] = valor;
        tamanho++;
    }

    public void inserirNoInicio(int valor) {
        if (tamanho == dados.length) throw new IllegalStateException("inventario cheio");

        // Desloca da direita para a esquerda, sem perder valores
        for (int i = tamanho - 1; i >= 0; i--) {
            dados[i + 1] = dados[i];
        }

        dados[0] = valor;
        tamanho++;
    }

    public void inserirNoMeio(int indice, int valor){
        if (indice < 0 || indice > tamanho) throw new IndexOutOfBoundsException("indice invalido");
        if (tamanho == dados.length) throw new IllegalStateException("inventario cheio");

        for (int i = tamanho - 1; i >= indice; i--) {
            dados[i + 1] = dados[i];
        }
        dados[indice] = valor;
        tamanho++;
    }

    public int remover(int indice) {
        if (indice < 0 || indice >= tamanho) throw new IndexOutOfBoundsException("indice invalido");
        int removido = dados[indice];
        for (int i = indice; i < tamanho -1; i++) {
            dados[i] = dados[i + 1];
        }
        dados[tamanho - 1] = -1;
        tamanho--;
        return removido;
    }
}
