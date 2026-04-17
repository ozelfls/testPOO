package view;

import controller.GameController;
import model.Pet;

import java.sql.SQLException;
import java.util.Scanner;

public class GameView {

    private final GameController controller = new GameController();

    public static void main(String[] args) {
        new GameView().run();
    }

    private void run() {
        try {
            controller.carregarPet();
        } catch (SQLException e) {
            System.out.println("Erro ao carregar pet do banco: " + e.getMessage());
            return;
        }

        Scanner sc = new Scanner(System.in);
        while (true) {
            Pet p = controller.getPetAtual();
            if (p == null) {
                System.out.println("Nenhum pet encontrado no banco.");
                break;
            }

            exibirStatus(p);

            System.out.println("Escolha uma acao:");
            System.out.println("1 - Alimentar");
            System.out.println("2 - Brincar");
            System.out.println("3 - Dormir");
            System.out.println("4 - Exercitar");
            System.out.println("0 - Sair");
            System.out.print("Opcao: ");

            String opcao = sc.nextLine();
            if ("0".equals(opcao)) {
                break;
            }

            try {
                switch (opcao) {
                    case "1":
                        controller.alimentar();
                        break;
                    case "2":
                        controller.brincar();
                        break;
                    case "3":
                        controller.dormir();
                        break;
                    case "4":
                        controller.exercitar();
                        break;
                    default:
                        System.out.println("Opcao invalida.");
                }
            } catch (SQLException e) {
                System.out.println("Erro ao aplicar acao: " + e.getMessage());
                break;
            }
        }

        sc.close();
        System.out.println("Encerrando Tamagotchi.");
    }

    private void exibirStatus(Pet p) {
        System.out.println("========================================");
        System.out.println("Pet: " + p.getNome() + "  | Tipo: " + p.getTipoUsuario());
        System.out.println("----------------------------------------");
        System.out.println("Fome      : " + p.getHunger());
        System.out.println("Felicidade: " + p.getHappiness());
        System.out.println("Energia   : " + p.getEnergy());
        System.out.println("========================================");
    }
}

