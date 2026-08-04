package lab01.Atividade1;

public class Funcionario {

    private String nome;
    private double salario;


    public Funcionario(String nome, double salario) {
        if(salario > 0){
            this.nome = nome;
            this.salario = salario;
        }
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        if(salario > 0){
            this.salario = salario;
        }
    }

    public String getNome() {
        return nome;
    }
}
