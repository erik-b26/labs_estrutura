package atividades.teste.REBOOT_ALUNOS;

import atividades.teste.REBOOT_ALUNOS.Escalonador;
import atividades.teste.REBOOT_ALUNOS.FilaCircular;
import atividades.teste.REBOOT_ALUNOS.Inventario;

import java.util.*;
import java.util.concurrent.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

/** Infraestrutura dos testes. Nao editar para resolver as missoes. */
public class Testes {
    interface Acao { void executar() throws Exception; }
    static class Caso {
        int missao; String titulo, cenario, mensagem; boolean passou;
        Caso(int m, String t, String c) { missao=m; titulo=t; cenario=c; }
    }
    static final List<Caso> casos = new ArrayList<>();
    static int filtro=0;
    static void igual(Object esperado, Object obtido) {
        if (!Objects.equals(esperado, obtido))
            throw new AssertionError("Esperado: " + esperado + " | Obtido: " + obtido);
    }
    static void vetor(int[] esperado, int[] obtido) { igual(Arrays.toString(esperado), Arrays.toString(obtido)); }
    static void excecao(Class<? extends Throwable> tipo, Acao acao) throws Exception {
        try { acao.executar(); }
        catch (Throwable e) {
            if (tipo.isInstance(e)) return;
            throw new AssertionError("Esperada excecao " + tipo.getSimpleName() + "; obtida " + e.getClass().getSimpleName());
        }
        throw new AssertionError("Esperada excecao " + tipo.getSimpleName() + "; nenhuma foi lancada");
    }
    static void teste(int m, String titulo, String cenario, Acao acao) {
        if (filtro != 0 && filtro != m) return;
        Caso c = new Caso(m, titulo, cenario); casos.add(c);
        ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
            Thread t=new Thread(r); t.setDaemon(true); return t;
        });
        Future<?> f=executor.submit(() -> { acao.executar(); return null; });
        try { f.get(1500, TimeUnit.MILLISECONDS); c.passou=true; c.mensagem="Resultado esperado confirmado."; }
        catch (TimeoutException e) { c.mensagem="Tempo limite de 1,5 s: verifique laco que nao termina ou execucao excessiva."; }
        catch (ExecutionException e) { Throwable causa=e.getCause(); c.mensagem=causa.getClass().getSimpleName()+": "+causa.getMessage(); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); c.mensagem="Teste interrompido."; }
        finally { f.cancel(true); executor.shutdownNow(); }
        System.out.println((c.passou?"[OK] ":"[FALHOU] ")+m+" - "+titulo+"\n  "+c.mensagem);
    }
    static Inventario inventario(int capacidade, int... itens) {
        Inventario v=new Inventario(capacidade); for(int n:itens) v.inserir(v.tamanho(),n); return v;
    }
    static FilaCircular fila(int capacidade, String... itens) {
        FilaCircular f=new FilaCircular(capacidade); for(String n:itens) f.inserir(n); return f;
    }
    static void saida(FilaCircular f, String... valores) {
        for(String s:valores) igual(s,f.remover()); igual(0,f.tamanho()); igual(true,f.vazia());
    }
    static void validar(Escalonador.Resultado r, int tempo, String execucao, String conclusao) {
        igual(execucao, String.join(",",r.execucao));
        igual(conclusao, String.join(",",r.conclusao)); igual(tempo,r.tempoTotal);
    }
    public static void main(String[] args) throws Exception {
        if (args.length>0 && !args[0].equals("todas")) {
            try { filtro=Integer.parseInt(args[0]); } catch(NumberFormatException e) { filtro=-1; }
            if(filtro<1||filtro>3) {System.err.println("Use: java -cp build Testes [1|2|3|todas]"); System.exit(2);}
        }
        teste(1,"Inserir no fim","Capacidade 6; inserir 10,20,30 no fim; tamanho 3, capacidade 6.",()->{
            Inventario v=inventario(6,10,20,30); vetor(new int[]{10,20,30},v.itens()); igual(3,v.tamanho());igual(6,v.capacidade());
        });
        teste(1,"Inserir no inicio","[10,20,30]; inserir 5 no indice 0 -> [5,10,20,30].",()->{
            Inventario v=inventario(6,10,20,30);v.inserir(0,5);vetor(new int[]{5,10,20,30},v.itens());
        });
        teste(1,"Inserir no meio","[10,20,30,40]; inserir 25 no indice 2 -> [10,20,25,30,40].",()->{
            Inventario v=inventario(6,10,20,30,40);v.inserir(2,25);vetor(new int[]{10,20,25,30,40},v.itens());
        });
        teste(1,"Remover do inicio","[10,20,30]; remover indice 0 devolve 10 e deixa [20,30].",()->{
            Inventario v=inventario(5,10,20,30);igual(10,v.remover(0));vetor(new int[]{20,30},v.itens());
        });
        teste(1,"Remover do meio","[10,20,30,40]; remover indice 1 devolve 20 e deixa [10,30,40].",()->{
            Inventario v=inventario(6,10,20,30,40);igual(20,v.remover(1));vetor(new int[]{10,30,40},v.itens());
        });
        teste(1,"Remover o ultimo","[10,20]; remover indice 1 -> [10], tamanho 1.",()->{
            Inventario v=inventario(3,10,20);igual(20,v.remover(1));vetor(new int[]{10},v.itens());igual(1,v.tamanho());
        });
        teste(1,"Indices invalidos","Rejeitar inserir -1 ou tamanho+1 e remover tamanho; preservar [7].",()->{
            Inventario v=inventario(3,7);excecao(IndexOutOfBoundsException.class,()->v.inserir(-1,1));excecao(IndexOutOfBoundsException.class,()->v.inserir(2,1));excecao(IndexOutOfBoundsException.class,()->v.remover(1));vetor(new int[]{7},v.itens());
        });
        teste(1,"Capacidade atingida","Capacidade 2, [7,8]; inserir no fim deve lancar IllegalStateException sem alterar os dados.",()->{
            Inventario v=inventario(2,7,8);excecao(IllegalStateException.class,()->v.inserir(2,9));vetor(new int[]{7,8},v.itens());igual(2,v.tamanho());
        });
        teste(1,"Zero e repeticoes","[0,0,2]; inserir -1 no indice 1 -> [0,-1,0,2].",()->{
            Inventario v=inventario(5,0,0,2);v.inserir(1,-1);vetor(new int[]{0,-1,0,2},v.itens());
        });
        teste(1,"Esvaziar e reutilizar","Remover unico item; rejeitar nova remocao; inserir 99; rejeitar capacidade zero.",()->{
            Inventario v=inventario(1,4);igual(4,v.remover(0));igual(0,v.tamanho());excecao(IndexOutOfBoundsException.class,()->v.remover(0));v.inserir(0,99);vetor(new int[]{99},v.itens());excecao(IllegalArgumentException.class,()->new Inventario(0));
        });
        teste(2,"Ordem FIFO","Inserir A,B,C; retirar A,B,C; terminar vazia.",()->saida(fila(4,"A","B","C"),"A","B","C"));
        teste(2,"Consultar nao remove","Frente de A,B e A em duas consultas; tamanho continua 2.",()->{
            FilaCircular f=fila(4,"A","B");igual("A",f.frente());igual("A",f.frente());igual(2,f.tamanho());
        });
        teste(2,"Volta ao inicio","Capacidade 4; A,B,C,D; retirar A,B; inserir E,F; saida C,D,E,F.",()->{
            FilaCircular f=fila(4,"A","B","C","D");igual("A",f.remover());igual("B",f.remover());f.inserir("E");f.inserir("F");saida(f,"C","D","E","F");
        });
        teste(2,"Cheia sem sobrescrever","Capacidade 2; A,B; rejeitar C e manter saida A,B.",()->{
            FilaCircular f=fila(2,"A","B");igual(true,f.cheia());excecao(IllegalStateException.class,()->f.inserir("C"));saida(f,"A","B");
        });
        teste(2,"Operar vazia","Remover e consultar fila vazia lancam NoSuchElementException; tamanho 0.",()->{
            FilaCircular f=fila(2);excecao(NoSuchElementException.class,()->f.remover());excecao(NoSuchElementException.class,()->f.frente());igual(0,f.tamanho());
        });
        teste(2,"Capacidade um","Capacidade 1; inserir A, retirar; inserir B, retirar; repetir sem erro.",()->{
            FilaCircular f=fila(1,"A");igual("A",f.remover());f.inserir("B");saida(f,"B");
        });
        teste(2,"Trinta reutilizacoes","Capacidade 3; inserir e retirar um valor por vez, durante 30 ciclos.",()->{
            FilaCircular f=fila(3);for(int i=0;i<30;i++){f.inserir("P"+i);igual("P"+i,f.remover());igual(0,f.tamanho());}
        });
        teste(2,"Entradas invalidas","Rejeitar capacidade zero e valor null sem alterar tamanho.",()->{
            excecao(IllegalArgumentException.class,()->new FilaCircular(0));FilaCircular f=fila(3);excecao(IllegalArgumentException.class,()->f.inserir(null));igual(0,f.tamanho());
        });
        teste(2,"Liberar uma vaga","Capacidade 2; A,B; retirar A; cheia deve ser false e tamanho deve ser 1.",()->{
            FilaCircular f=fila(2,"A","B");f.remover();igual(false,f.cheia());igual(1,f.tamanho());igual("B",f.frente());
        });
        teste(2,"Ciclos completos","Capacidade 3; preencher e esvaziar em ordem, por quatro rodadas.",()->{
            FilaCircular f=fila(3);for(int k=0;k<4;k++){for(int i=0;i<3;i++)f.inserir(k+"-"+i);igual(true,f.cheia());for(int i=0;i<3;i++)igual(k+"-"+i,f.remover());igual(true,f.vazia());}
        });
        teste(3,"Cenario base","Duracoes 5,3,1; q=2; execucao P1,P2,P3,P1,P2,P1; conclusao P3,P2,P1; tempo 9.",()->{
            validar(Escalonador.executar(new String[]{"P1","P2","P3"},new int[]{5,3,1},2),9,"P1,P2,P3,P1,P2,P1","P3,P2,P1");
        });
        teste(3,"Menor que quantum","Duracoes 1,4; q=3; execucao P1,P2,P2; conclusao P1,P2; tempo 5.",()->{
            validar(Escalonador.executar(new String[]{"P1","P2"},new int[]{1,4},3),5,"P1,P2,P2","P1,P2");
        });
        teste(3,"Um unico processo","Duracao 5; q=2; executa P1 tres vezes; tempo 5, uma conclusao.",()->{
            validar(Escalonador.executar(new String[]{"P1"},new int[]{5},2),5,"P1,P1,P1","P1");
        });
        teste(3,"Fila inicial vazia","Nenhum processo, q=2: listas vazias e tempo 0.",()->{
            validar(Escalonador.executar(new String[]{},new int[]{},2),0,"","");
        });
        teste(3,"Multiplos exatos","Duracoes 4,2; q=2; execucao A,B,A; conclusao B,A; tempo 6.",()->{
            validar(Escalonador.executar(new String[]{"A","B"},new int[]{4,2},2),6,"A,B,A","B,A");
        });
        teste(3,"Preservar entrada","Duracoes originais [2,4] devem continuar [2,4] depois da simulacao.",()->{
            int[] d={2,4};Escalonador.executar(new String[]{"A","B"},d,2);vetor(new int[]{2,4},d);
        });
        teste(3,"Quantum invalido","q=0 ou q=-1 deve lancar IllegalArgumentException.",()->{
            excecao(IllegalArgumentException.class,()->Escalonador.executar(new String[]{"A"},new int[]{2},0));excecao(IllegalArgumentException.class,()->Escalonador.executar(new String[]{"A"},new int[]{2},-1));
        });
        teste(3,"Entrada invalida","Rejeitar duracao zero, negativa, vetores incompativeis e nome nulo.",()->{
            excecao(IllegalArgumentException.class,()->Escalonador.executar(new String[]{"A"},new int[]{0},2));excecao(IllegalArgumentException.class,()->Escalonador.executar(new String[]{"A"},new int[]{-1},2));excecao(IllegalArgumentException.class,()->Escalonador.executar(new String[]{"A"},new int[]{},2));excecao(IllegalArgumentException.class,()->Escalonador.executar(new String[]{null},new int[]{1},2));
        });
        teste(3,"Quantum maior que todos","Duracoes 1,2,3; q=10; cada um executa uma vez; tempo 6.",()->{
            validar(Escalonador.executar(new String[]{"A","B","C"},new int[]{1,2,3},10),6,"A,B,C","A,B,C");
        });
        teste(3,"Alternancia justa","Duracoes 2,2,2; q=1; execucao A,B,C,A,B,C; conclusao A,B,C; tempo 6.",()->{
            validar(Escalonador.executar(new String[]{"A","B","C"},new int[]{2,2,2},1),6,"A,B,C,A,B,C","A,B,C");
        });
        long ok=casos.stream().filter(c->c.passou).count();
        String resumo=ok+" / "+casos.size()+" testes aprovados";
        Files.write(Paths.get("relatorio.html"), html(resumo).getBytes(StandardCharsets.UTF_8));
        System.out.println("\n"+resumo+". Abra relatorio.html no navegador (F5 para atualizar).");
        System.exit(ok==casos.size()?0:1);
    }
    static String esc(String s) {return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;").replace("'","&#39;");}
    static String html(String resumo) throws Exception {
        String modelo=new String(Files.readAllBytes(Paths.get("painel-modelo.html")),StandardCharsets.UTF_8);
        StringBuilder linhas=new StringBuilder();
        for(Caso c:casos) {
            linhas.append("<details class='teste "+(c.passou?"ok":"falha")+"' data-missao='"+c.missao+"'><summary><span>"+(c.passou?"PASSOU":"REVISAR")+"</span> "+c.missao+" · "+esc(c.titulo)+"</summary><p><b>Cenario:</b> "+esc(c.cenario)+"</p><pre>"+esc(c.mensagem)+"</pre></details>");
        }
        return modelo.replace("@@RESUMO@@",esc(resumo)).replace("@@DATA@@",esc(java.time.LocalDateTime.now().toString().replace('T',' '))).replace("@@CASOS@@",linhas.toString());
    }
}
