package classe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import connecting.Connexion;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.CollationElementIterator;
import java.sql.*;
import java.io.*;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.nio.file.Paths;
import java.nio.file.Path;
import database_details.DatabaseDetails;
import database_details.ForeignKeyDetails;

public class ClassGenerator {
    private static final String TEMPLATE_FILE = "./ClassTemplate.txt";
    private static String CONFIG_FILE;
    private String definition;
    private String endLine;
    private String endBlock;
    private String startBlock;
    private String endClass;
    private String startClass;
    private String endPackage;
    private String startPackage;
    private String extension;
    private String prive;
    private String publie;
    private String protect;
    private String annotation;
    private String impor;
    private String pack;
    private String packName;
    private String thi;
    private String attribution;
    private String retour;


    private static final Map<String, String> CONFIG_MAP = new HashMap<>();

    static {
        CONFIG_MAP.put("SPRING", "config_file/javaconfig.xml");
        CONFIG_MAP.put("C#", "config_file/csconfig.xml");
    }

    public ClassGenerator(String techno) throws Exception {
        this.CONFIG_FILE = this.getConfigFil(techno);
        this.setParameter();
    }

    public String getConfigFil(String techno) {
        String csType = CONFIG_MAP.get(techno.toUpperCase());
        return (csType != null) ? csType : "";
    }

    public void setParameter() throws Exception {
        this.definition = this.getValue("util", "definition");
        this.endLine = this.getValue("util", "endLine");
        this.endBlock = this.getValue("util", "endBlock");
        this.startBlock = this.getValue("util", "startBlock");
        this.endClass = this.getValue("util", "endClass");
        this.startClass = this.getValue("util", "startClass");
        this.endPackage = this.getValue("util", "endPackage");
        this.startPackage = this.getValue("util", "startPackage");
        this.extension = this.getValue("util", "extension");
        this.prive = this.getValue("util", "private");
        this.publie = this.getValue("util", "public");
        this.protect = this.getValue("util", "protected");
        this.impor = this.getValue("util", "import");
        this.pack = this.getValue("util", "package");
        this.packName = this.getValue("util", "packageName");
        this.thi = this.getValue("util", "this");
        this.attribution = this.getValue("util", "attribution");
        this.retour = this.getValue("util", "return");
        this.annotation = this.getValue("util", "annotation");
    }

    public HashMap<String, ArrayList<Attribut>> data() throws Exception {
        ArrayList<DatabaseDetails> details = DatabaseDetails.getDatabaseDetailsFromDatabase();
        HashMap<String, ArrayList<Attribut>> hs = new HashMap<String, ArrayList<Attribut>>();
        for (int i = 0; i < details.size(); i++) {
            DatabaseDetails databaseDetails = details.get(i);
            if (hs.containsKey(databaseDetails.getTableName())) {
                ArrayList<Attribut> arr = hs.get(databaseDetails.getTableName());
                arr.add(new Attribut(databaseDetails.getColumnName(),
                        this.getValue("databaseRef", databaseDetails.getColumnType()),
                        this.getValue("import", databaseDetails.getColumnType())));
                hs.put(databaseDetails.getTableName(), arr);
            } else {
                ArrayList<Attribut> arr = new ArrayList<Attribut>();
                arr.add(new Attribut(databaseDetails.getColumnName(),
                        this.getValue("databaseRef", databaseDetails.getColumnType()),
                        this.getValue("import", databaseDetails.getColumnType())));
                hs.put(databaseDetails.getTableName(), arr);
            }
        }
        return hs;
    }

    public static String changeMaj1erLettre ( String lettre){
        return lettre.substring(0,1).toUpperCase() + lettre.substring(1);

    }

    private static String mapColumnType(String dbType) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(CONFIG_FILE);

        NodeList databaseRefList = document.getElementsByTagName("databaseRef");
        if (databaseRefList != null) {
            Element databaseRef = (Element) databaseRefList.item(0);
            NodeList elements = databaseRef.getElementsByTagName(dbType);
            if (elements != null && elements.getLength() > 0) {
                return elements.item(0).getTextContent();
            }
        }
        return dbType;
    }

    public static Attribut[] getAllFK(String table, Connection con) throws Exception {
        Attribut[] list_colonne = null;
        try {
            DatabaseMetaData metaData = con.getMetaData();
            ResultSet rs = metaData.getColumns(null, null, table, null);
            int counteur = 0;
            while (rs.next()) {
                counteur++;
            }
            list_colonne = new Attribut[counteur];
            rs = metaData.getColumns(null, null, table, null);
            int index = 0;
            while (rs.next()) {
                Attribut colonne = new Attribut();
                String val = rs.getString("COLUMN_NAME");
                ResultSet rsGetForeignKey = metaData.getImportedKeys(null, null, table);
                boolean isForeignKey = false;
                while (rsGetForeignKey.next()) {
                    String foreignKeyName = rsGetForeignKey.getString("FKCOLUMN_NAME");
                    if (val.equalsIgnoreCase(foreignKeyName)) {
                        String referencesTable = rsGetForeignKey.getString("PKTABLE_NAME");
                        colonne.setType(referencesTable);
                        colonne.setNom(val);
                        isForeignKey = true;
                        break;
                    }
                }
                rsGetForeignKey.close();
                list_colonne[index] = colonne;
                index++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list_colonne;
    }

    public static Attribut[] getAllColonne(String table, Connection con) throws Exception {
        Attribut[] list_colonne = null;
        try {
            DatabaseMetaData metaData = con.getMetaData();
            ResultSet rs = metaData.getColumns(null, null, table, null);
            int counteur = 0;
            while (rs.next()) {
                counteur++;
            }
            list_colonne = new Attribut[counteur];
            rs = metaData.getColumns(null, null, table, null);
            int index = 0;
            while (rs.next()) {
                Attribut colonne = new Attribut();
                colonne.setNom(mapColumnType(rs.getString("COLUMN_NAME")));
                colonne.setType(mapColumnType(rs.getString("TYPE_NAME")));
                ResultSet rsGetForeignKey = metaData.getImportedKeys(null, null, table);
                boolean isForeignKey = false;
                while (rsGetForeignKey.next()) {
                    String foreignKeyName = rsGetForeignKey.getString("FKCOLUMN_NAME");
                    if (colonne.getNom().equalsIgnoreCase(foreignKeyName)) {
                        String referencesTable = rsGetForeignKey.getString("PKTABLE_NAME");
                        String vide = null;
                        colonne.setType(vide);
                        colonne.setNom(vide);
                        isForeignKey = true;
                        break;
                    }
                }
                rsGetForeignKey.close();
                list_colonne[index] = colonne;
                index++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list_colonne;
    }

    public static Attribut[] getColonnes(String table, Connection con) throws Exception {
        Attribut[] list_colonne = null;
        try {
            DatabaseMetaData metaData = con.getMetaData();
            ResultSet rs = metaData.getColumns(null, null, table, null);

            // Récupérer toutes les clés étrangères de la table
            ResultSet foreignKeys = metaData.getImportedKeys(null, null, table);
            List<String> foreignKeyNames = new ArrayList<>();
            while (foreignKeys.next()) {
                foreignKeyNames.add(foreignKeys.getString("FKCOLUMN_NAME"));
            }

            // Récupérer toutes les clés primaires de la table
            ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, table);
            List<String> primaryKeyNames = new ArrayList<>();
            while (primaryKeys.next()) {
                primaryKeyNames.add(primaryKeys.getString("COLUMN_NAME"));
            }

            int counteur = 0;
            while (rs.next()) {
                // Ignorer les colonnes qui sont des clés étrangères ou des clés primaires
                String columnName = rs.getString("COLUMN_NAME");
                if (!foreignKeyNames.contains(columnName) && !primaryKeyNames.contains(columnName)) {
                    counteur++;
                }
            }
            list_colonne = new Attribut[counteur];
            rs = metaData.getColumns(null, null, table, null);
            int index = 0;
            while (rs.next()) {
                // Ignorer les colonnes qui sont des clés étrangères ou des clés primaires
                String columnName = rs.getString("COLUMN_NAME");
                if (!foreignKeyNames.contains(columnName) && !primaryKeyNames.contains(columnName)) {
                    Attribut colonne = new Attribut();
                    colonne.setNom(columnName);
                    String val = rs.getString(6);
                    colonne.setType(mapColumnType(val));
                    list_colonne[index] = colonne;
                    index++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list_colonne;
    }

    public static Attribut getFirstStringColumnAfterId(String table, Connection con) throws Exception {
        Attribut colonne = new Attribut();
        try {
            DatabaseMetaData metaData = con.getMetaData();
            ResultSet rs = metaData.getColumns(null, null, table, null);
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                String columnType = rs.getString("TYPE_NAME");
                if (columnType.equalsIgnoreCase("text") || columnType.equalsIgnoreCase("varchar")) {
                    colonne.setNom(columnName);
                    colonne.setType(mapColumnType(columnType));
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return colonne;
    }

    public static Attribut getPrimaryKeyColumn(String table, Connection con) throws Exception {
        Attribut colonne = new Attribut();
        try {
            DatabaseMetaData metaData = con.getMetaData();
            ResultSet rs = metaData.getPrimaryKeys(null, null, table);
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                colonne.setNom(columnName);
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return colonne;
    }

    private boolean isForeignKey(String tableName, String columnName) throws Exception {
        ArrayList<DatabaseDetails> details = DatabaseDetails.getDatabaseDetailsFK();

        for (DatabaseDetails databaseDetails : details) {
            //System.out.println("check foreign keys table: " + databaseDetails.getTableName());

            if (databaseDetails.getTableName().equals(tableName) && databaseDetails.getForeignKeys() != null) {
                //System.out.println("aon ah");

                for (ForeignKeyDetails foreignKeyDetail : databaseDetails.getForeignKeys()) {
                    //System.out.println("prems: " + foreignKeyDetail.getFkColumnName());
                    //System.out.println(" column: " + columnName + "  foreign key: " + foreignKeyDetail.getFkColumnName());
                    // System.out.println("table " + tableName);

                    if (foreignKeyDetail.getFkColumnName().equals(columnName)) {
                        System.out.println("foreign key mety");
                        return true;
                    }
                }
            }
        }
        return false;
    }


    private String getReferencedTableName(String tableName, String columnName) {
        ArrayList<DatabaseDetails> detailseh;

        try {
            detailseh = DatabaseDetails.getDatabaseDetailsFromDatabase();

            for (DatabaseDetails databaseDetails : detailseh) {
                //System.out.println("hafa "+ databaseDetails.getTableName());
                //System.out.println("depart 1 " + databaseDetails.getForeignKeys());
                if (databaseDetails.getForeignKeys() != null) {
                    //System.out.println("depart " + columnName);
                    for (ForeignKeyDetails foreignKeyDetail : databaseDetails.getForeignKeys()) {
                        //System.out.println("ato ve");
                        //System.out.println("fk " + foreignKeyDetail.getFkColumnName());
                        System.out.println("table " + foreignKeyDetail.getFkTableName());

                        return foreignKeyDetail.getFkTableName();

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public String importe(HashMap<String, ArrayList<Attribut>> hs, String tableName) throws Exception {
        String importation = "";
        ArrayList<Attribut> attribut = hs.get(tableName);
        for (int i = 0; i < attribut.size(); i++) {
            if (!importation.contains(attribut.get(i).getImportation())) {
                importation += impor + " " + attribut.get(i).getImportation() + endLine + "\n";
            }
        }
        String[] util = this.necessaryImport();
        for (int i = 0; i < util.length; i++) {
            importation += impor + " " + util[i] + endLine + "\n";
        }
        return importation;
    }

    public String[] necessaryImport() throws Exception {
        String[] nec = new String[0];
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = (Document) builder.parse(CONFIG_FILE);

            NodeList databaseRefList = ((org.w3c.dom.Document) document).getElementsByTagName("necessaryImport");
            if (databaseRefList != null) {
                Element databaseRef = (Element) databaseRefList.item(0);
                NodeList elements = databaseRef.getElementsByTagName("utilImport");
                if (elements != null) {
                    nec = new String[elements.getLength()];
                    for (int i = 0; i < elements.getLength(); i++) {
                        Node elmt = elements.item(i);
                        if (elmt != null) {
                            nec[i] = elmt.getTextContent();
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return nec;
    }

    public String attribut(HashMap<String, ArrayList<Attribut>> hs, String tableName, Connection con) throws Exception {
        String attributes = "";
        Attribut[] attribute = this.getAllColonne(tableName,con);
        Attribut[] foreignK = this.getAllFK(tableName,con);
        attributes += "\t@Id\n\t@GeneratedValue(strategy = GenerationType.IDENTITY)\n";
        for (int i = 0 ; i < attribute.length; i ++){
            if(attribute[i].getType() != null){
                attributes += "\t" + prive + " " + attribute[i].getType() + " " + attribute[i].getNom() + endLine
                        + "\n";
            }
        }
        for (int j = 0 ; j < foreignK.length; j ++){
            if(foreignK[j].getType()!= null){
                attributes += "\t @ManyToOne" + "\n";
                attributes += "\t @JoinColumn(name = \"" + foreignK[j].getNom() + "\", nullable = false)" + "\n";
                attributes += "\t" + prive + " " + capitalizeFirstLetter(foreignK[j].getType()) + " " + foreignK[j].getType() + endLine + "\n";
            }
        }


        return attributes;
    }

    public void generate() {
        try {
            HashMap<String, ArrayList<Attribut>> hs = this.data();
            Set<Entry<String, ArrayList<Attribut>>> entrees = hs.entrySet();
            for (Entry<String, ArrayList<Attribut>> table : entrees) {
                String tableName = table.getKey();
                this.generate(tableName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generate(String tableName) {
        try {
            String base = "postgres";
            Connection con = Connexion.getConnection(base);
            Attribut[] data = this.getAllFK(tableName,con);
            List<String> nom_foreignList = new ArrayList<>();
            nom_foreignList.add(tableName);
            for (int i = 0; i < data.length; i++) {
                String nom_foreign = data[i].getType();
                if(nom_foreign != null) {
                    nom_foreignList.add(nom_foreign);
                    //System.out.println(" nom table " + nom_foreign);
                }

            }
            for(int i = 0 ; i < nom_foreignList.size(); i++){

                String tableNames = nom_foreignList.get(i);
                HashMap<String, ArrayList<Attribut>> hs = this.data();
                String attributs = this.attribut(hs, tableNames, con);
                String imports = this.importe(hs, tableNames);
                String className = capitalizeFirstLetter(tableNames);
                String template = loadTemplateFromFile();
                String packa = this.pack();
                String setters = this.setter(hs, tableNames, con);
                String getters = this.getter(hs, tableNames, con);
                String constructor = this.constructeur(hs, tableNames, con);
                String emptyconstructor = this.constructeurvide(hs, tableNames);
                template = template.replace("##PACKAGE##", packa);
                template = template.replace("##STARTPACKAGE##", startPackage);
                template = template.replace("##DEFINITION##", definition);
                template = template.replace("#CLASS_NAME#", className);
                template = template.replace("##STARTCLASS##", startClass);
                template = template.replace("#ATTRIBUTS#", attributs);
                template = template.replace("##IMPORTS##", imports);
                template = template.replace("#SETTERS#", setters);
                template = template.replace("#GETTERS#", getters);
                template = template.replace("#CONSTRUCTOR#", constructor);
                template = template.replace("#EMPTYCONSTRUCTOR#", emptyconstructor);
                template = template.replace("##ENDCLASS##", endClass);
                template = template.replace("##ENDPACKAGE##", endPackage);
                template = template.replace("##ANNOTATION##", annotation);
                //System.out.println(constructor);

                File folder = new File(packName);
                if (!folder.exists()) {
                    if (folder.mkdirs()) {
                        try (BufferedWriter writer = new BufferedWriter(
                                new FileWriter(packName + "/" + className + extension))) {
                            writer.write(template);
                        } catch (Exception e) {
                            throw e;
                        }
                    } else {
                        throw new Exception("Error while creating folder");
                    }
                } else {
                    try (BufferedWriter writer = new BufferedWriter(
                            new FileWriter(packName + "/" + className + extension))) {
                        writer.write(template);
                    } catch (Exception e) {
                        throw e;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String loadTemplateFromFile() throws IOException {
        StringBuilder template = new StringBuilder();
        Path projectRoot = Paths.get("../..").toAbsolutePath();
        System.setProperty("user.dir", projectRoot.toString());
        try (BufferedReader reader = new BufferedReader(new FileReader(TEMPLATE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                template.append(line).append("\n");
            }
        }
        return template.toString();
    }

    public String setter(HashMap<String, ArrayList<Attribut>> hs, String tableName, Connection con) throws Exception {
        String setter = "";
        Attribut[] foreignK = this.getAllFK(tableName,con);
        Attribut[] attribute = this.getAllColonne(tableName,con);
        for (int i = 0; i < attribute.length; i++) {
            if(attribute[i].getType() != null){
                setter += "\t" + publie + " void set" + capitalizeFirstLetter(attribute[i].getNom()) + "( "
                        + attribute[i].getType() + " " + attribute[i].getNom() + " )\n\t" + startBlock + "\n\t\t"
                        + thi
                        + attribute[i].getNom() + attribution
                        + attribute[i].getNom() + endLine + " \n\t" + endBlock + "\n\n";
            }
        }
        for (int j = 0 ; j < foreignK.length; j ++){
            if(foreignK[j].getType()!= null){
                setter += "\t" + publie + " void set" +  capitalizeFirstLetter(foreignK[j].getType()) + "( "
                        + capitalizeFirstLetter(foreignK[j].getType()) + " " + foreignK[j].getType() + " )\n\t" + startBlock + "\n\t\t"
                        + thi
                        + foreignK[j].getType() + attribution
                        + foreignK[j].getType() + endLine + " \n\t" + endBlock + "\n\n";
            }
        }

        return setter;
    }

    public String getter(HashMap<String, ArrayList<Attribut>> hs, String tableName, Connection con) throws Exception {
        String getter = "";
        Attribut[] foreignK = this.getAllFK(tableName, con);
        Attribut[] attribute = this.getAllColonne(tableName,con);
        for (int i = 0; i < attribute.length; i++) {
            if(attribute[i].getType() != null){
                getter += "\t" + publie + " " + attribute[i].getType() + " get"
                        + capitalizeFirstLetter(attribute[i].getNom()) + "()\n\t" + startBlock + "\n\t\t"
                        + retour + " "
                        + thi
                        + attribute[i].getNom() + endLine + " \n\t" + endBlock + "\n\n";
            }

        }
        for (int j = 0 ; j < foreignK.length; j ++){
            if(foreignK[j].getType()!= null){
                getter += "\t" + publie + " " + capitalizeFirstLetter(foreignK[j].getType()) + " get"
                        + capitalizeFirstLetter(foreignK[j].getType()) + "()\n\t" + startBlock + "\n\t\t"
                        + retour + " "
                        + foreignK[j].getType() + endLine + " \n\t" + endBlock + "\n\n";
            }
        }
        return getter;
    }

    public String constructeur(HashMap<String, ArrayList<Attribut>> hs, String tableName, Connection con) throws Exception {
        String constructor = "\t" + publie + " " + capitalizeFirstLetter(tableName) + "(";
        Attribut[] foreignK = this.getAllFK(tableName, con);
        Attribut[] attribute = this.getAllColonne(tableName, con);

        int attributeLength = attribute.length;
        attribute = Arrays.copyOf(attribute, attribute.length + foreignK.length);

        for (int j = 0; j < foreignK.length; j++) {
            attribute[attributeLength + j] = new Attribut();
            attribute[attributeLength + j].setNom(foreignK[j].getType());
            attribute[attributeLength + j].setType(capitalizeFirstLetter(foreignK[j].getType()));
        }

        boolean isFirstAttribute = true;

        for (int j = 0; j < attribute.length; j++) {
            if (attribute[j].getNom() != null) {
                if (!isFirstAttribute) {
                    constructor += ", ";
                }
                constructor += attribute[j].getType() + " " + attribute[j].getNom();
                isFirstAttribute = false;
            }
        }

        constructor += " )\n\t" + startBlock;

        for (int j = 0; j < attribute.length; j++) {
            if (attribute[j].getNom() != null) {
                constructor += "\n\t\t";
                constructor += thi + "set" + capitalizeFirstLetter(attribute[j].getNom()) + "("
                        + attribute[j].getNom()
                        + ")" + endLine + " ";
            }
        }

        constructor += "\n\t" + endBlock;
        return constructor;
    }


    public String constructeurvide(HashMap<String, ArrayList<Attribut>> hs, String tableName) throws Exception {
        String constructor = "\t" + publie + " " + capitalizeFirstLetter(tableName) + "()\n\t" + startBlock + "\n\n\t"
                + endBlock;
        return constructor;
    }

    private static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    public String pack() throws Exception {
        String packa = "";
        packa += pack + " " + packName;
        return packa;
    }

    public String getValue(String parent, String element) throws Exception {
        String type = "";
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = (Document) builder.parse(CONFIG_FILE);

            NodeList databaseRefList = ((org.w3c.dom.Document) document).getElementsByTagName(parent);
            if (databaseRefList != null) {
                Element databaseRef = (Element) databaseRefList.item(0);
                if (databaseRef != null) {
                    Node elmt = databaseRef.getElementsByTagName(element).item(0);
                    if (elmt != null) {
                        type = elmt.getTextContent();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return type;
    }
}
