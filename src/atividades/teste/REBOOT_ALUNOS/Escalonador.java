package atividades.teste.REBOOT_ALUNOS;

import java.util.*;

/** Missao 3. Todos chegam em t=0; custo de troca de contexto igual a zero. */
public class Escalonador {
    public static class Resultado {
        public final List<String> execucao = new ArrayList<>();
        public final List<String> conclusao = new ArrayList<>();
        public int tempoTotal;
    }
    public static Resultado executar(String[] nomes, int[] duracoes, int quantum) {
        if (nomes == null || duracoes == null || nomes.length != duracoes.length)
            throw new IllegalArgumentException("entradas incompativeis");
        if (quantum <= 0) throw new IllegalArgumentException("quantum deve ser positivo");
        for (int i = 0; i < nomes.length; i++) {
            if (nomes[i] == null || nomes[i].isEmpty() || duracoes[i] <= 0)
                throw new IllegalArgumentException("nome e duracao invalidos");
        }
        // Cada inteiro representa o indice de um processo.
        // ArrayDeque isola esta missao dos erros da FilaCircular.
        Deque<Integer> fila = new ArrayDeque<>();
        int[] restante = duracoes;
        for (int i = 0; i < nomes.length; i++) fila.addLast(i);
        Resultado resultado = new Resultado();
        while (!fila.isEmpty()) {
            int p = fila.removeFirst();
            int executado = quantum;
            resultado.execucao.add(nomes[p]);
            resultado.tempoTotal += executado;
            restante[p] -= executado;
            if (restante[p] > 0) fila.addFirst(p);
            else resultado.conclusao.add(nomes[p]);
        }
        return resultado;
    }
}
