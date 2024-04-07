package classe;

public class Attribut {
    String nom;
    String type;
    String importation;

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getNom() {
        return this.nom;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public void setImportation(String importation) {
        this.importation = importation;
    }

    public String getImportation() {
        return this.importation;
    }

    public Attribut(String nom, String type, String importation) {
        this.setNom(nom);
        this.setType(type);
        this.setImportation(importation);
    }

    public Attribut(){}

}
