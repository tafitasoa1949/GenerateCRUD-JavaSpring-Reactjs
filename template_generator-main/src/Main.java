import java.util.Scanner;

import classe.ClassGenerator;
import controller.ControllerGenerator;
import repository.RepositoryGenerator;
import view.ListeGenerator;
import view.UpdateGenerator;
import view.FormGenerator;

public class Main {
    public static void main(String[] args) throws Exception {
        while(true) {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Entrez la technologie : ");
            String technologie = scanner.nextLine();

            System.out.println("Entrez le nom de la classe à générer : ");
            String nomClasse = scanner.nextLine();

            new ClassGenerator(technologie).generate(nomClasse);
            new RepositoryGenerator(technologie).generate(nomClasse);
            new ControllerGenerator(technologie).generate(nomClasse);
            new ListeGenerator(technologie).generate(nomClasse);
            new FormGenerator(technologie).generate(nomClasse);
            new UpdateGenerator(technologie).generate(nomClasse);
            System.out.println("Génération terminée");
            System.out.println("Voulez-vous continuer ? (O/N) [par défaut O]");
            String continuer = scanner.nextLine();
            if(continuer.equalsIgnoreCase("N")) {
                break;
            }else{
                continue;
            }
        }
    }
}