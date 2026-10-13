package atividades.teste.REBOOT_ALUNOS;

import java.util.NoSuchElementException;

/** Missao 2. inicio = proximo a sair; fim = ultimo inserido. */
public class FilaCircular {
    private final String[] dados;
    private int inicio = 0;
    private int fim = -1;
    private int tamanho = 0;

    public FilaCircular(int capacidade) {
        if (capacidade <= 0) throw new IllegalArgumentException("capacidade deve ser positiva");
        dados = new String[capacidade];
    }
    public int tamanho() { return tamanho; }
    public boolean vazia() { return tamanho == 0; }
    public boolean cheia() { return fim == dados.length - 1; }
    public void inserir(String valor) {
        if (valor == null) throw new IllegalArgumentException("valor nulo");
        if (cheia()) throw new IllegalStateException("fila cheia");
        fim = fim + 1;
        dados[fim] = valor;
        tamanho++;
    }
    public String remover() {
        if (vazia()) throw new NoSuchElementException("fila vazia");
        String valor = dados[inicio];
        dados[inicio] = null;
        inicio = inicio + 1;
        tamanho--;
        return valor;
    }
    public String frente() {
        if (vazia()) throw new NoSuchElementException("fila vazia");
        return dados[inicio];
    }
}
