package lab01.Atividade1;

import java.util.ArrayList;

public class Projeto {

    private String nome;
    private Gerente gerente;
    private ArrayList<Funcionario> funcionarios;
    private boolean estaFinalizado;

    public Projeto(String nome, Gerente gerente, ArrayList<Funcionario> funcionarios, boolean estaFinalizado) {
        this.nome = nome;
        this.gerente = gerente;
        this.funcionarios = funcionarios;
        this.estaFinalizado = estaFinalizado;
    }

    public String getNome() {
        return nome;
    }

    public void adicionarFuncionario(Funcionario f){
        funcionarios.add(f);
    }

    public double calcularSalarioGerente(){
        int qtdFuncionarios = funcionarios.size();
        double salarioBase = gerente.getSalario();
        double salarioFinal = salarioBase + (salarioBase * qtdFuncionarios * 0.01);
        if(estaFinalizado){
            salarioFinal += salarioBase * 0.1;
        }
        return salarioFinal;
    }

    public double calcularSalarioFuncionarios(){

        double salarioTotal = 0;

        if(estaFinalizado){
            for(Funcionario f : funcionarios){
                double salarioFuncionario = f.getSalario() + (f.getSalario() * 0.1);
                salarioTotal += salarioFuncionario;
            }
            return salarioTotal;
        }
        for(Funcionario f : funcionarios){
            salarioTotal += f.getSalario();
        }
        return salarioTotal;
    }
    public double calcularCustoTotal(){
        double func = calcularSalarioFuncionarios();
        double gerente = calcularSalarioGerente();
        double total = func + gerente;
        return total;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Projeto: ").append(nome).append("\n");
        sb.append("Gerente: ").append(gerente.getNome())
                .append(" - Salário final: R$ ")
                .append(calcularSalarioGerente()).append("\n");

        sb.append("Funcionários:\n");
        for (Funcionario f : funcionarios) {
            double salario = f.getSalario();

            if (estaFinalizado) {
                salario += salario * 0.10;
            }

            sb.append(" - ")
                    .append(f.getNome())
                    .append(" - Salário: R$ ")
                    .append(salario)
                    .append("\n");
        }

        sb.append("Custo total do projeto: R$ ")
                .append(calcularCustoTotal());

        return sb.toString();
    }
}
