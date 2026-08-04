package lab01.Atividade1;

import java.util.ArrayList;

public class App {
    static void main() {

        Gerente gerente = new Gerente("Carlos", 8000.00);
        Funcionario f1 = new Funcionario("Ana", 3000.00);
        Funcionario f2 = new Funcionario("Bruno", 3200.00);
        Funcionario f3 = new Funcionario("Cláudia", 3500.00);

        ArrayList<Funcionario> equipe = new ArrayList<>();
        equipe.add(f1);
        equipe.add(f2);

        Projeto projeto = new Projeto("Sistema de Gestão", gerente, equipe, true);

        projeto.adicionarFuncionario(f3);

        System.out.println(projeto);
    }
}
