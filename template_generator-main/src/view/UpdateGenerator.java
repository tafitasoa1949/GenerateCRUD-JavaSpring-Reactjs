package view;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import classe.Attribut;
import classe.ClassGenerator;
import connecting.Connexion;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class UpdateGenerator {
    private static final String TEMPLATE_FILE = "./ReactUpdateTemplate.txt";
    private final String CONFIG_FILE;
    private String url;
    private String updateUrl;
    private String methode;
    private String updateMethode;
    private String listLink;
    private String cssContent;
    private String dossier;
    private String extension;
    private String forms;
    private String formstate;
    private String formsObject;
    private String fetchDataPk;
    private String fetchData;

    private static final Map<String, String> CONFIG_MAP = new HashMap<>();

    static {
        CONFIG_MAP.put("SPRING", "config_file/springupdate.xml");
    }

    public UpdateGenerator(String techno) throws Exception {
        this.CONFIG_FILE = this.getConfigFil(techno);
        this.setParameter();

    }

    public void setParameter() throws Exception {
        this.url = this.getValue("util", "url");
        this.updateUrl = this.getValue("util", "updateUrl");
        this.methode = this.getValue("methods", "List");
        this.updateMethode = this.getValue("methods", "Update");
        this.extension = this.getValue("util", "extension");
        this.dossier = this.getValue("util", "dossier");
        this.listLink = this.getValue("util", "listLink");
        this.cssContent = this.getOneValue("cssContent");

    }

    public String getConfigFil(String techno) {
        String csType = CONFIG_MAP.get(techno.toUpperCase());
        return (csType != null) ? csType : "";
    }

    public void generate(String className) throws Exception {
        try {
            String base = "postgres";
            Connection con = Connexion.getConnection(base);

            className = UpdateGenerator.capitalizeFirstLetter(className);
            String template = loadTemplateFromFile();
            this.forms = this.getAllValueToForms(className.toLowerCase(), con);
            this.formstate = this.getAllValueToFormsState(className.toLowerCase(), con);
            this.formsObject = this.getAllValueToObject(className.toLowerCase(), con);
            this.fetchDataPk = this.getFetchDataPk(className.toLowerCase(), con);
            this.fetchData = this.insertFetchIfExists(className.toLowerCase(), con);

            template = template.replace("#URL#", url);
            template = template.replace("#UPDATE_URL#", updateUrl);
            template = template.replace("#UPDATE_METHOD#", updateMethode);
            template = template.replace("#LIST_LINK#", listLink);
            template = template.replace("#METHOD_LIST#", methode);
            template = template.replace("#CLASS_NAME_2#", className.toLowerCase());
            template = template.replace("#FORMS#", forms);
            template = template.replace("#FORMS_STATE#", formstate);
            template = template.replace("#FORMS_OBJECT#", formsObject);
            template = template.replace("#FETCH_PK_DATA#", fetchDataPk);
            template = template.replace("#FETCH_DATA#", fetchData);

            File folder = new File(dossier);
            if (!folder.exists()) {
                if (folder.mkdirs()) {
                    try (BufferedWriter writer = new BufferedWriter(
                            new FileWriter(dossier + "/Update" + extension))) {
                        writer.write(template);
                    } catch (Exception e) {
                        throw e;
                    }
                    try (BufferedWriter writer = new BufferedWriter(
                            new FileWriter(dossier + "/Update.css"))) {
                        writer.write(cssContent);
                    } catch (Exception e) {
                        throw e;
                    }
                } else {
                    throw new Exception("Error while creating folder");
                }
            } else {
                try (BufferedWriter writer = new BufferedWriter(
                        new FileWriter(dossier + "/Update" + extension))) {
                    writer.write(template);
                } catch (Exception e) {
                    throw e;
                }
                try (BufferedWriter writer = new BufferedWriter(
                        new FileWriter(dossier + "/Update.css"))) {
                    writer.write(cssContent);
                } catch (Exception e) {
                    throw e;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String loadTemplateFromFile() throws Exception {
        StringBuilder template = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(TEMPLATE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                template.append(line).append("\n");
            }
        }
        return template.toString();
    }

    private static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    private boolean checkClasse(String className) {
        boolean result = false;
        String filename = className + extension;
        Path filePath = Paths.get("./dossier", filename);
        if (Files.exists(filePath)) {
            result = true;
        }
        return result;
    }

    public String getValue(String parent, String element) throws Exception {
        String type = "";
        Path projectRoot = Paths.get(".").toAbsolutePath();
        System.setProperty("user.dir", projectRoot.toString());

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

    public String getOneValue(String parent) throws Exception {
        String type = "";
        Path projectRoot = Paths.get(".").toAbsolutePath();
        System.setProperty("user.dir", projectRoot.toString());

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = (Document) builder.parse(CONFIG_FILE);

            Element element = (Element) document.getElementsByTagName(parent).item(0);
            if (element != null) {
                type = element.getTextContent();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return type;
    }

    public String getAllValueToForms(String tableName, Connection connection) throws Exception {
        String allForms = "";
        Path projectRoot = Paths.get(".").toAbsolutePath();
        System.setProperty("user.dir", projectRoot.toString());
        Attribut[] attributs = ClassGenerator.getColonnes(tableName, connection);
        Attribut[] attributsFK = ClassGenerator.getAllFK(tableName, connection);
        try {
            for (int i = 0 ; i < attributs.length; i++) {
                allForms += "\t<div className=\"form-group\">\n";
                allForms += "\t\t<label for=\"" + attributs[i].getNom() + "\">" + attributs[i].getNom() + "</label>\n";
                allForms += "\t\t<input type=\""+ this.getValue("type", attributs[i].getType()) + "\" className=\"form-control\" id=\"" + attributs[i].getNom() + "\" name=\"" + attributs[i].getNom() + "\" placeholder=\"" + attributs[i].getNom() + "\" value={item."+ attributs[i].getNom() +"} onChange={handleChange} />\n";
                allForms += "\t</div>\n";
            }
            for (int i = 0 ; i < attributsFK.length; i++) {
                if(attributsFK[i].getNom() != null) {
                    Attribut[] attributsF = ClassGenerator.getColonnes(attributsFK[i].getType(), connection);
                    Attribut attribut = ClassGenerator.getFirstStringColumnAfterId(attributsFK[i].getType(), connection);
                    Attribut attributPK = ClassGenerator.getPrimaryKeyColumn(attributsFK[i].getType(), connection);
                    allForms += "\t<div className=\"form-group\">\n";
                    allForms += "\t\t<label for=\"" + attributsFK[i].getNom() + "\">" + attributsFK[i].getNom() + "</label>\n";
                    allForms += "\t\t<select className=\"form-control\" id=\"" + attributsFK[i].getNom() + "\" name=\"" + attributsFK[i].getNom() + "\" value={item." + attributPK.getNom() + "} onChange={handleChange} >\n";
                    allForms += "\t\t{" + attributsFK[i].getType() + ".map((item) => (\n";
                    allForms += "\t\t\t<option value={item." + attributPK.getNom() + "}>" + "{item." + attribut.getNom() + "}</option>\n";
                    allForms += "\t\t))}\n";
                    allForms += "\t\t</select>\n";
                    allForms += "\t</div>";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allForms;
    }

    public String getAllValueToObject(String tableName, Connection connection) throws Exception {
        String allForms = "";
        Path projectRoot = Paths.get(".").toAbsolutePath();
        System.setProperty("user.dir", projectRoot.toString());
        Attribut[] attributs = ClassGenerator.getColonnes(tableName, connection);
        Attribut[] attributsFK = ClassGenerator.getAllFK(tableName, connection);
        try {
            for (int i = 0 ; i < attributs.length; i++) {
                allForms += "\t\t" + attributs[i].getNom() + ": formData.get('" + attributs[i].getNom() + "'),\n";
            }
            for (int i = 0 ; i < attributsFK.length; i++) {
                if(attributsFK[i].getNom() != null) {
                    Attribut attributsF = ClassGenerator.getPrimaryKeyColumn(attributsFK[i].getType(), connection);
                    allForms += "\t\t" + attributsFK[i].getType() + ": {\n";
                    allForms += "\t\t\t" + attributsF.getNom() + ": formData.get('" + attributsFK[i].getNom() + "'),\n";
                    allForms += "\t\t},\n";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allForms;
    }

    public String getFetchDataPk(String tableName, Connection connection) throws Exception {
        String allForms = "";
        Path projectRoot = Paths.get(".").toAbsolutePath();
        System.setProperty("user.dir", projectRoot.toString());
        //Attribut[] attributs = ClassGenerator.getColonnes(tableName, connection);
        Attribut[] attributsFK = ClassGenerator.getAllFK(tableName, connection);
        try {
            for (int i = 0 ; i < attributsFK.length; i++) {
                if(attributsFK[i].getNom() != null) {
                    allForms += "const fetchData" + this.capitalizeFirstLetter(attributsFK[i].getType()) + " = () => {" + "\n";
                    allForms += "\t" + "let url = \"http://localhost:8080/" + attributsFK[i].getType() + "s\"" + "\n";
                    allForms += "\t" + "fetch(url)" + "\n";
                    allForms += "\t" + ".then((response) => response.json())" + "\n";
                    allForms += "\t" + ".then((data) => {" + "\n";
                    allForms += "\t" + "\t" + "if(data.length > 0) {" + "\n";
                    allForms += "\t" + "\t" + "\t" + "const objectKeys = Object.keys(data[0])" + "\n";
                    allForms += "\t" + "\t" + "\t" + "setColumns(objectKeys)" + "\n";
                    allForms += "\t" + "\t" + "\t" + "set" + this.capitalizeFirstLetter(attributsFK[i].getType()) + "(data)" + "\n";
                    allForms += "\t" + "\t" + "}" + "\n";
                    allForms += "\t" + "})" + "\n";
                    allForms += "\t" + ".catch((error) => {" + "\n";
                    allForms += "\t" + "\t" + "console.error('Error:', error);" + "\n";
                    allForms += "\t" + "});" + "\n";
                    allForms += "}" + "\n";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allForms;
    }

    public String insertFetchIfExists(String tableName, Connection connection) throws Exception {
        String allForms = "";
        Path projectRoot = Paths.get(".").toAbsolutePath();
        System.setProperty("user.dir", projectRoot.toString());
        Attribut[] attributs = ClassGenerator.getColonnes(tableName, connection);
        Attribut[] attributsFK = ClassGenerator.getAllFK(tableName, connection);
        try {
            for (int i = 0 ; i < attributsFK.length; i++) {
                if(attributsFK[i].getNom() != null) {
                    allForms += "\t" + "\t" + "fetchData" + this.capitalizeFirstLetter(attributsFK[i].getType()) + "()" + "\n";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allForms;
    }

    public String getAllValueToFormsState(String tableName, Connection connection) throws Exception {
        String allForms = "";
        Path projectRoot = Paths.get(".").toAbsolutePath();
        System.setProperty("user.dir", projectRoot.toString());
        Attribut[] attributs = ClassGenerator.getColonnes(tableName, connection);
        Attribut[] attributsFK = ClassGenerator.getAllFK(tableName, connection);
        for (int i = 0 ; i < attributsFK.length; i++) {
            if(attributsFK[i].getNom() != null) {
                allForms += "\tconst [ " + attributsFK[i].getType() + ", set" + this.capitalizeFirstLetter(attributsFK[i].getType()) + " ] = useState([]);\n";
            }
        }
        return allForms;
    }

}
