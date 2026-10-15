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
        Deque<Integer> fila = new ArrayDeque<>();

        // MUDOU: antes era "int[] restante = duracoes;"
        // Isso só criava outra referência para o mesmo array, então o loop
        // alterava o array do chamador. O clone() cria uma cópia independente.
        int[] restante = duracoes.clone();

        for (int i = 0; i < nomes.length; i++) fila.addLast(i);
        Resultado resultado = new Resultado();

        while (!fila.isEmpty()) {
            int p = fila.removeFirst();

            // MUDOU: antes era "int executado = quantum;"
            // Se o processo precisa de menos tempo que o quantum, ele só
            // executa o que falta. Sem isso tempoTotal ficava inflado e
            // restante[p] ficava negativo.
            int executado = Math.min(quantum, restante[p]);

            resultado.execucao.add(nomes[p]);
            resultado.tempoTotal += executado;
            restante[p] -= executado;

            // MUDOU: antes era "fila.addFirst(p);" (esta era a causa do seu teste falhar)
            // addFirst devolvia o processo para a FRENTE da fila, então ele rodava
            // de novo na hora (P1,P1,P1,P2,P2,P3). Em round-robin ele vai para o
            // FINAL da fila, para os outros processos terem a vez.
            if (restante[p] > 0) fila.addLast(p);
            else resultado.conclusao.add(nomes[p]);
        }
        return resultado;
    }
}
