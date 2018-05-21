package visualization.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import visualization.Main;
import visualization.utils.Tools;

import java.io.*;
import java.nio.file.Files;
import java.util.Objects;

import static visualization.utils.Tools.ParserType.CANDC;

/**
 * Controller for the input view.
 *
 * @author Nathan Joubert
 * @author Thomas Guesdon
 * @author Gaétan Basile
 * @see visualization.view
 */
public class InputController implements Stageable {
    /**
     * Button for adding a sentence to the list.
     */
    @FXML
    public Button addSentenceButton;
    /**
     * Button for starting the processing of the sentences in the list.
     */
    @FXML
    public Button startProcessingButton;
    /**
     * MenuItem for setting the location of ccg2lambda.
     */
    @FXML
    public MenuItem setccg2lambdaLocationItem;
    /**
     * MenuItem for choosing the template
     */
    @FXML
    public Menu menuTemplate;
    /**
     * Radio menu template for the template event
     */
    @FXML
    public RadioMenuItem radioTemplateEvent;
    /**
     * Radio menu template for the template classic
     */
    @FXML
    public RadioMenuItem radioTemplateClassic;
    /**
     * Item for setting C&C location
     */
    public MenuItem setCandCLocationItem;
    /**
     * Item for setting easyCCG location
     */
    public MenuItem setEasyCGCLocationItem;
    /**
     * Item for setting depCCG location
     */
    public MenuItem setdepCCGLocationItem;
    /**
     * Item for setting Jigg location
     */
    public MenuItem setJiggLocationItem;
    /**
     * Radio menu parser for only C&C
     */
    @FXML
    private RadioMenuItem radioCandCOnlyItem;
    /**
     * Radio menu parser all EN parser
     */
    @FXML
    private RadioMenuItem radioALL_EN_ParserItem;
    /**
     * Radio menu for JA parser
     */
    @FXML
    private RadioMenuItem radioJA_ParserItem;

    @FXML
    public MenuItem showInformationItem;
    /**
     * MenuItem for showing the readme for ccg2lambda (on the web)
     */
    @FXML
    public MenuItem showReadMeItem;
    /**
     * TextField enabling the input of sentences from the user.
     */
    @FXML
    private TextField sentenceField;
    /**
     * Progress bar indicating the completion status of the task.
     */
    @FXML
    private ProgressBar visualizationProgressBar;
    /**
     * Progress of the conversion process.
     */
    private SimpleDoubleProperty progress = new SimpleDoubleProperty(0.0);
    /**
     * View of all the sentences the user has input.
     */
    @FXML
    private ListView<String> listSentences;
    /**
     * List of all the sentences.
     */
    private ObservableList<String> listSentencesItems = FXCollections.observableArrayList();
    /**
     * input stream for calling our scripts
     */
    private InputStream is;
    /**
     * Enables knowing if the host OS is windows.
     */
    private final boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    /**
     * The view this controller manages.
     */
    private Stage view;

    /**
     * For first time program is launch, install the virtual environment for python
     */
    private boolean firstTime;
    /**
     * boolean for when C&C location is change by the user
     */
    private boolean CandCDefined = false;
    /**
     * boolean for when easyCCG location is change by the user
     */
    private boolean easyCCGDefined = false;
    /**
     * boolean for when depCCG location is change by the user
     */
    private boolean depCCGDefined = false;

    /**
     * boolean for when jigg location is change by the user
     */
    private boolean jiggDefined = false;

    /**
     * Initializes the view.
     */
    @FXML
    public void initialize() {
        try {
            checkConfigAndInitializeEnvironment();
            initListView();
            initMenuTemplate();
            visualizationProgressBar.progressProperty().bindBidirectional(progress);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * initialize the template selection
     */
    private void initMenuTemplate() {
        radioTemplateClassic.setSelected(true);
        radioTemplateEvent.setSelected(false);

        radioCandCOnlyItem.setSelected(true);
        radioALL_EN_ParserItem.setSelected(false);
        radioJA_ParserItem.setSelected(false);

    }

    /**
     * Initializes the context menus for each listView item.
     */
    private void initListView() {
        listSentences.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>();

            ContextMenu contextMenu = new ContextMenu();

            MenuItem deleteItem = new MenuItem("Delete sentence");
            deleteItem.setOnAction(event -> listSentencesItems.remove(
                    listSentences.getSelectionModel().getSelectedIndex()
            ));

            contextMenu.getItems().add(deleteItem);

            cell.textProperty().bind(cell.itemProperty());

            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    cell.setContextMenu(null);
                } else {
                    cell.setContextMenu(contextMenu);
                }
            });
            return cell;
        });
    }


    /**
     * Adds the sentence in the textField to the list.
     */
    public void addSentence() {
        if (sentenceField.getText() != null && !Objects.equals(sentenceField.getText(), "")) {
            listSentencesItems.add(sentenceField.getText());
            listSentences.setItems(listSentencesItems);
            sentenceField.setText("");
        }
    }

    /**
     * Opens a window for consulting results.
     */
    private void openResultsWindow() {
        try {
            System.out.println(getClass().getClassLoader().getResource("visualization/view/output.fxml"));
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("visualization/view/output.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(Tools.windowTitleBase);
            stage.setScene(new Scene(root));
            Stageable controller = loader.getController();
            stage.setMinWidth(Tools.windowSize[0].doubleValue());
            stage.setMinHeight(Tools.windowSize[1].doubleValue());
            stage.show();
            controller.initStage(stage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Launches processing of the sentences in the listView.
     */
    public void visualize() {
        writeTxt();
        progress.set(0.25);
        if (!isWindows) {
            launchScript();
            switch (Main.selectedParserType) {
                case CANDC:
                    switch (Main.selectedTemplateType) {
                        case CLASSIC:
                            Main.xmlSemanticsFile = new File(Main.ccg2lambdaLocation + "/parsed/sentences.txt.sem.xml");
                            break;
                        case EVENT:
                            Main.xmlSemanticsFile = new File(Main.ccg2lambdaLocation + "/parsed/sentences.txt.sem.xml");
                            break;
                    }
                    break;

                case ALL:
                    switch (Main.selectedTemplateType) {
                        case CLASSIC:
                            Main.xmlSemanticsFile = new File(Main.ccg2lambdaLocation + "/en_parsed/sentences.txt.sem.xml");
                            break;
                        case EVENT:
                            Main.xmlSemanticsFile = new File(Main.ccg2lambdaLocation + "/en_parsed/sentences.txt.sem.xml");
                            break;
                    }
                    break;

                case JA:
                    switch (Main.selectedTemplateType) {
                        case CLASSIC:
                            Main.xmlSemanticsFile = new File(Main.ccg2lambdaLocation + "/ja_parsed/sentences.txt.sem.xml");
                            break;
                        case EVENT:
                            Main.xmlSemanticsFile = new File(Main.ccg2lambdaLocation + "/ja_parsed/sentences.txt.sem.xml");
                            break;
                    }
                    break;
            }


            openResultsWindow();
        } else {
            progress.set(0.0);
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setContentText("Windows isn't available yet");
            a.showAndWait();
        }
    }

    /**
     * Writes the sentences to the sentences.txt file.
     */
    private void writeTxt() {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(Main.ccg2lambdaLocation + "/sentences.txt"), "utf-8"))) {

            //Browse the list and write each items to "sentences.txt"
            for (String s : listSentencesItems) {
                s = s.toLowerCase();
                writer.write(s);

                //add a dot if there isn't
                char[] sentenceChar = s.toCharArray();
                if (sentenceChar[sentenceChar.length - 1] != '.') {
                    writer.write(".");
                }

                //next line
                ((BufferedWriter) writer).newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Launches the python scripts, using ccg2lambda.
     */
    private void launchScript() {
        //script
        System.out.println(System.getProperty("os.name"));

        System.out.println("  template type : " + Main.selectedTemplateType);

        String ccg2lambdaPath = Main.ccg2lambdaLocation.getAbsolutePath();
        Process process;

        File parsedDirectoryEN = new File(Main.ccg2lambdaLocation + "/en_parsed");
        File resultDirectoryEN = new File(Main.ccg2lambdaLocation + "/en_results");

        try {
            if (parsedDirectoryEN.exists() && resultDirectoryEN.exists()) {
                /*
                  Check if their is file in the directory, if yes, delete them
                 */
                for (File file : parsedDirectoryEN.listFiles()) {
                    Files.deleteIfExists(file.toPath());
                }
                for (File file : resultDirectoryEN.listFiles()) {
                    Files.deleteIfExists(file.toPath());
                }
                /*
                  If the file already exist, delete them
                 */
                Files.deleteIfExists(parsedDirectoryEN.toPath());
                Files.deleteIfExists(resultDirectoryEN.toPath());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        File parsedDirectoryDefault = new File(Main.ccg2lambdaLocation + "/parsed");
        File resultDirectoryDefault = new File(Main.ccg2lambdaLocation + "/results");

        try {
            if (parsedDirectoryDefault.exists() && resultDirectoryDefault.exists()) {
                /*
                  Check if their is file in the directory, if yes, delete them
                 */
                for (File file : parsedDirectoryDefault.listFiles()) {
                    Files.deleteIfExists(file.toPath());
                }
                for (File file : resultDirectoryDefault.listFiles()) {
                    Files.deleteIfExists(file.toPath());
                }
                /*
                  If the file already exist, delete them
                 */
                Files.deleteIfExists(parsedDirectoryDefault.toPath());
                Files.deleteIfExists(resultDirectoryDefault.toPath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        File parsedDirectoryJA = new File(Main.ccg2lambdaLocation + "/ja_parsed");
        File resultDirectoryJA = new File(Main.ccg2lambdaLocation + "/ja_results");

        try {
            if (parsedDirectoryJA.exists() && resultDirectoryJA.exists()) {
                /*
                  Check if their is file in the directory, if yes, delete them
                 */
                for (File file : parsedDirectoryJA.listFiles()) {
                    Files.deleteIfExists(file.toPath());
                }
                for (File file : resultDirectoryJA.listFiles()) {
                    Files.deleteIfExists(file.toPath());
                }
                /*
                  If the file already exist, delete them
                 */
                Files.deleteIfExists(parsedDirectoryJA.toPath());
                Files.deleteIfExists(resultDirectoryJA.toPath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        switch (Main.selectedParserType) {
            case ALL:


                if (Main.selectedTemplateType == Tools.TemplateType.CLASSIC) {

                    try {

                        System.out.println("python ALL parser classic script");

                        File scriptParserClassic_EMNLP2015 = File.createTempFile("scriptParserClassic_EMNLP2015", ".sh");
                        scriptParserClassic_EMNLP2015.deleteOnExit();
                        is = getClass().getClassLoader().getResourceAsStream("visualization/scripts/scriptParserClassic_EMNLP2015.sh");
                        copyRessourceToTmpFile(is, scriptParserClassic_EMNLP2015);
                        scriptParserClassic_EMNLP2015.setExecutable(true);
                        process = new ProcessBuilder(scriptParserClassic_EMNLP2015.getPath(), ccg2lambdaPath).start();

                        progress.set(1.00);
                        process.waitFor();

                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }


                } else if (Main.selectedTemplateType == Tools.TemplateType.EVENT) {

                    System.out.println("python parser event script");
                    try {
                        File scriptParserEvent = File.createTempFile("scriptParserEvent", ".sh");
                        scriptParserEvent.deleteOnExit();
                        is = getClass().getClassLoader().getResourceAsStream("visualization/scripts/scriptParserEvent.sh");
                        copyRessourceToTmpFile(is, scriptParserEvent);
                        scriptParserEvent.setExecutable(true);
                        process = new ProcessBuilder(scriptParserEvent.getPath(), ccg2lambdaPath).start();

                        progress.set(1.00);
                        process.waitFor();

                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case CANDC:


                if (Main.selectedTemplateType == Tools.TemplateType.CLASSIC) {
                    try {

                        System.out.println("python ONLY C&C classic script");

                        File ParserDefaultClassic_EMNLP2015 = File.createTempFile("ParserDefaultClassic_EMNLP2015", ".sh");
                        ParserDefaultClassic_EMNLP2015.deleteOnExit();
                        is = getClass().getClassLoader().getResourceAsStream("visualization/scripts/scriptDefaultParser/scriptParserDefaultClassic_EMNLP2015.sh");
                        copyRessourceToTmpFile(is, ParserDefaultClassic_EMNLP2015);
                        ParserDefaultClassic_EMNLP2015.setExecutable(true);
                        process = new ProcessBuilder(ParserDefaultClassic_EMNLP2015.getPath(), ccg2lambdaPath).start();

                        progress.set(1.00);
                        process.waitFor();

                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (Main.selectedTemplateType == Tools.TemplateType.EVENT) {
                    try {
                        System.out.println("python ONLY C&C event script");

                        File scriptParserDefaultEvent = File.createTempFile("scriptParserDefaultEvent", ".sh");
                        scriptParserDefaultEvent.deleteOnExit();
                        is = getClass().getClassLoader().getResourceAsStream("visualization/scripts/scriptDefaultParser/scriptParserDefaultEvent.sh");
                        copyRessourceToTmpFile(is, scriptParserDefaultEvent);
                        scriptParserDefaultEvent.setExecutable(true);
                        process = new ProcessBuilder(scriptParserDefaultEvent.getPath(), ccg2lambdaPath).start();


                        progress.set(1.00);
                        process.waitFor();

                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case JA:
                if (Main.selectedTemplateType == Tools.TemplateType.CLASSIC) {
                    try {

                        System.out.println("python JA classic script");

                        File scriptJAParserClassic = File.createTempFile("scriptJAParserClassic", ".sh");
                        scriptJAParserClassic.deleteOnExit();
                        is = getClass().getClassLoader().getResourceAsStream("visualization/scripts/ja_scriptParser/scriptJAParserClassic.sh");
                        copyRessourceToTmpFile(is, scriptJAParserClassic);
                        scriptJAParserClassic.setExecutable(true);
                        process = new ProcessBuilder(scriptJAParserClassic.getPath(), ccg2lambdaPath).start();

                        progress.set(1.00);
                        process.waitFor();

                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (Main.selectedTemplateType == Tools.TemplateType.EVENT) {
                    try {
                        System.out.println("python JA event script");

                        File scriptJAParserEvent = File.createTempFile("scriptJAParserEvent", ".sh");
                        scriptJAParserEvent.deleteOnExit();
                        is = getClass().getClassLoader().getResourceAsStream("visualization/scripts/ja_scriptParser/scriptJAParserEvent.sh");
                        copyRessourceToTmpFile(is, scriptJAParserEvent);
                        scriptJAParserEvent.setExecutable(true);
                        process = new ProcessBuilder(scriptJAParserEvent.getPath(), ccg2lambdaPath).start();

                        progress.set(1.00);
                        process.waitFor();

                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }


    /**
     * Check if the user already got the python virtual environment, if not, the software install it
     *
     * @throws IOException
     * @throws InterruptedException
     */
    private void checkConfigAndInitializeEnvironment() throws IOException, InterruptedException {
        File py3Directory = new File("py3");
        firstTime = (!py3Directory.exists() && !py3Directory.isDirectory()) || (!Tools.configFile.exists());
        Process process;
        if (firstTime) {
            //boolean ok = Tools.configFile.mkdirs();
            System.out.println("------------------------First Time ----------------------------");


            /*
              If the file already exist, delete them
             */

            File ConfigDirectory = new File("./config");
            if (ConfigDirectory.isDirectory()) {
                try {
                    for (File file : ConfigDirectory.listFiles()) {
                        Files.deleteIfExists(file.toPath());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Files.deleteIfExists(Tools.configFile.toPath());


            System.out.println("ccg2lambda location");
            new File(ConfigDirectory.toPath().toString()).mkdir();
            boolean ok = true;
            try {
                ok = Tools.configFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!ok)
                throw new IOException();

            Alert firstTimeAlertccg2lambda = new Alert(Alert.AlertType.WARNING);
            firstTimeAlertccg2lambda.setTitle("First time configuration needed");
            firstTimeAlertccg2lambda.setHeaderText("First time configuration ccglambda");
            firstTimeAlertccg2lambda.setContentText(
                    "Configuration file is missing and/or corrupted." + '\n' +
                            "Please redo the configuration."
            );
            firstTimeAlertccg2lambda.showAndWait();
            File f1 = setccg2lambdaLocation();

            FileWriter fw = new FileWriter(Tools.configFile);

            fw.write(f1.getAbsolutePath());
            fw.close();

            Files.deleteIfExists(Tools.configCandC.toPath());

            System.out.println("C&C location");
            boolean okCandC = true;
            try {
                okCandC = Tools.configCandC.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!okCandC)
                throw new IOException();

            Alert firstTimeAlertCandC = new Alert(Alert.AlertType.WARNING);
            firstTimeAlertCandC.setTitle("First time configuration needed");
            firstTimeAlertCandC.setHeaderText("First time configuration C&C");
            firstTimeAlertCandC.setContentText(
                    "Configuration file is missing and/or corrupted." + '\n' +
                            "Please redo the configuration."
            );
            firstTimeAlertCandC.showAndWait();
            File f2 = setCandCLocation();
            System.out.println("------------------------------------------------ " + f2.toPath().toString());

            FileWriter fwCandC = new FileWriter(Tools.configCandC);

            fwCandC.write(f2.getAbsolutePath());
            fwCandC.close();
            CandCDefined = true;


            System.out.println("python virtual");

            Alert firstTimePy3 = new Alert(Alert.AlertType.WARNING);
            firstTimePy3.setTitle("First time configuration needed");
            firstTimePy3.setHeaderText("First time configuration Python Virtual Environment ");
            firstTimePy3.setContentText(
                    "Configuration file is missing and/or corrupted." + '\n' +
                            "Please redo the configuration."
            );
            firstTimePy3.showAndWait();
            setPy3Location();
            System.out.println("------------------------------------------------ " + Main.pythonLocation.toPath().toString());


            File pythonVirtual = File.createTempFile("pythonVirtual", ".sh");
            pythonVirtual.deleteOnExit();
            is = getClass().getClassLoader().getResourceAsStream("visualization/scripts/pythonVirtual.sh");
            copyRessourceToTmpFile(is, pythonVirtual);
            pythonVirtual.setExecutable(true);

            String pythonLocation = Main.pythonLocation.getAbsolutePath();

            process = new ProcessBuilder(pythonVirtual.getPath(), pythonLocation).start();

            process.waitFor();
            Alert py3InstallEnded = new Alert(Alert.AlertType.CONFIRMATION);
            py3InstallEnded.setTitle("First time configuration success");
            py3InstallEnded.setContentText(
                    "Configuration is now complete." + '\n' +
                            "ccg2lambda location registered & python 3 virtual environment installed in :" + '\n' +
                            py3Directory.getAbsolutePath());
            py3InstallEnded.showAndWait();
            firstTime = false;

            System.out.println("------------------------First Time END ----------------------------");
        } else {
            BufferedReader br = new BufferedReader(new FileReader(Tools.configFile));
            String ccg2lambdaPath = br.readLine();
            Main.ccg2lambdaLocation = ccg2lambdaPath != null ? new File(ccg2lambdaPath) : null;
        }
    }


    private void copyRessourceToTmpFile(InputStream source, File destination) {
        InputStreamReader fr = null;
        FileWriter fw = null;
        try {
            fr = new InputStreamReader(source);
            fw = new FileWriter(destination);

            int c;
            do {
                c = fr.read();
                if (c != -1) {
                    fw.write(c);
                }
            } while (c != -1);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * define the EN parser location in parser_location.txt
     */
    private void defineEN_ALL_ParserLocation() {
        System.out.println("EN parser location location");
        boolean okParserLocation;

        try {
            okParserLocation = Tools.configENParserLocation.createNewFile();
            System.out.println("  okParserLocation : " + okParserLocation);
            if (!okParserLocation) {
                System.out.println("erreur sur ok");
                throw new IOException();
            }
            FileWriter fwParserLocation;
            fwParserLocation = new FileWriter(Tools.configENParserLocation);
            fwParserLocation.write("candc:" + Main.ccgCandCLocation.toPath() + "\n");
            fwParserLocation.write("easyccg:" + Main.easyCCGLocation.toPath() + "\n");
            fwParserLocation.write("depccg:" + Main.depccgLocation + "\n");
            fwParserLocation.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * define the JA parser location in parser_location_ja.txt
     */
    private void defineJA_ParserLocation() {
        System.out.println("JA parser location location");
        boolean okJA_ParserLocation;

        try {
            okJA_ParserLocation = Tools.configJAParserLocation.createNewFile();
            System.out.println("  okParserLocation : " + okJA_ParserLocation);
            if (!okJA_ParserLocation) {
                System.out.println("erreur sur ok");
                throw new IOException();
            }
            FileWriter fwParserLocation;
            fwParserLocation = new FileWriter(Tools.configJAParserLocation);
            fwParserLocation.write("jigg:" + Main.jiggLocation + "\n");
            fwParserLocation.write("depccg:" + Main.depccgLocation + "\n");
            fwParserLocation.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fires when the return key is pressed.
     *
     * @param ae the event triggered by this action
     */
    public void enterPressed(ActionEvent ae) {
        addSentence();
    }

    /**
     * See the information about the software
     */
    public void displayInformation() {
        Alert popupInfo = new Alert(Alert.AlertType.INFORMATION);
        popupInfo.setTitle("About");
        popupInfo.setHeaderText("About this software ");
        popupInfo.setContentText("This software has been created by Gaétan BASILE, Thomas GUESDON and Nathan JOUBERT for the Bekki Lab at the Ochanomizu University" + "\n"
                + "Using ccg2lambda created by Pascual MARTINEZ-GOMEZ, Koji MINESHIMA, Yusuke MIYAO and Daisuke BEKKI, " + "\n"
                + "a tool to derive formal semantic representations of natural language sentences given CCG derivation trees and semantic templates.");
        popupInfo.getDialogPane().setMinWidth(1000);
        popupInfo.getDialogPane().setMinHeight(100);
        popupInfo.showAndWait();
    }

    /**
     * Redirect to the displayReadme
     */
    public void displayReadme() {
        String url = "https://github.com/BlackNat5937/ccg2lambda/tree/master/Visualization#ccg2lambda-visualize--composing-semantic-representations-guided-by-ccg-derivations";
        Main.openLink(url);
    }

    /**
     * for choosing the ccg2lambda location file
     *
     * @return
     */
    @FXML
    public File setccg2lambdaLocation() {
        DirectoryChooser locationChooser = new DirectoryChooser();
        locationChooser.setTitle("select ccg2lambda installation directory");
        File selected = null;
        while (selected == null)
            selected = locationChooser.showDialog(view);
        if (selected.isDirectory()) {
            if (selected.canRead() && selected.canExecute() && selected.canWrite())
                Main.ccg2lambdaLocation = selected;
            Tools.createPathFileCandC();
            Tools.createPathFileALL_EN();
            Tools.createPathFileJA();
        }
        if (CandCDefined && easyCCGDefined) {
            defineEN_ALL_ParserLocation();
        }
        System.out.println("  ||location mainCCG2lambda : " + Main.ccg2lambdaLocation);
        return Main.ccg2lambdaLocation;
    }

    /**
     * for choosing the Python location
     */
    private void setPy3Location() {
        FileChooser locationChooser = new FileChooser();
        locationChooser.setTitle("select python directory");
        File selected = null;
        while (selected == null)
            selected = locationChooser.showOpenDialog(view);
        if (selected.isFile()) {
            if (selected.canRead() && selected.canExecute() && selected.canWrite())
                System.out.println("selected " + selected);
            Main.pythonLocation = selected;
        }
        System.out.println(Main.pythonLocation);
    }


    /**
     * for choosing the C&C location file
     *
     * @return
     */
    @FXML
    public File setCandCLocation() {
        DirectoryChooser locationChooser = new DirectoryChooser();
        locationChooser.setTitle("select CCG Parser Cand directory");
        File selected = null;
        while (selected == null)
            selected = locationChooser.showDialog(view);
        if (selected.isDirectory()) {
            if (selected.canRead() && selected.canExecute() && selected.canWrite())
                Main.ccgCandCLocation = selected;
        }
        if (CandCDefined && easyCCGDefined && depCCGDefined) {
            defineEN_ALL_ParserLocation();
        }
        System.out.println(Main.ccgCandCLocation);
        return Main.ccgCandCLocation;
    }

    /**
     * choosing the easyCCG location file
     *
     * @return
     */
    @FXML
    private File setEasyCCGLocation() {
        easyCCGDefined = true;
        DirectoryChooser locationChooser = new DirectoryChooser();
        locationChooser.setTitle("select easyCCG Parser directory");
        File selected = null;
        while (selected == null)
            selected = locationChooser.showDialog(view);
        if (selected.isDirectory()) {
            if (selected.canRead() && selected.canExecute() && selected.canWrite())
                Main.easyCCGLocation = selected;
        }
        if (CandCDefined && easyCCGDefined && depCCGDefined) {
            defineEN_ALL_ParserLocation();
        }
        System.out.println(Main.easyCCGLocation);
        return Main.easyCCGLocation;
    }

    /**
     * choosing the depCCG location file
     *
     * @return
     */
    @FXML
    private File setdepCCGLocation() {
        depCCGDefined = true;
        DirectoryChooser locationChooser = new DirectoryChooser();
        locationChooser.setTitle("select depCCG Parser directory");
        File selected = null;
        while (selected == null)
            selected = locationChooser.showDialog(view);
        if (selected.isDirectory()) {
            if (selected.canRead() && selected.canExecute() && selected.canWrite())
                Main.depccgLocation = selected;
        }
        if (CandCDefined && easyCCGDefined && depCCGDefined) {
            defineEN_ALL_ParserLocation();
        } else if (depCCGDefined && jiggDefined) {
            defineJA_ParserLocation();
        }
        System.out.println(Main.depccgLocation);
        return Main.depccgLocation;
    }

    @FXML
    private File setJiggLocation() {
        DirectoryChooser locationChooser = new DirectoryChooser();
        locationChooser.setTitle("select Jigg Parser directory");
        File selected = null;
        while (selected == null)
            selected = locationChooser.showDialog(view);
        if (selected.isDirectory()) {
            if (selected.canRead() && selected.canExecute() && selected.canWrite())
                Main.jiggLocation = selected;
        }
        if (depCCGDefined && jiggDefined) {
            defineJA_ParserLocation();
        }
        System.out.println(Main.jiggLocation);
        return Main.jiggLocation;
    }

    /**
     * use only C&C parser
     */
    @FXML
    private void setCandCOnly() {
        if (Tools.configCandC == null) {
            Tools.createPathFileCandC();
        }

        radioCandCOnlyItem.setSelected(true);
        radioALL_EN_ParserItem.setSelected(false);
        radioJA_ParserItem.setSelected(false);
        System.out.println("|_|_|_|_|_|_|_|_|_ Only C&C Parser");
        Main.selectedParserType = CANDC;

    }

    /**
     * use all the EN parsers  setEN_AllParser
     */
    @FXML
    private void setEN_AllParser() {
        radioCandCOnlyItem.setSelected(false);
        radioALL_EN_ParserItem.setSelected(true);
        radioJA_ParserItem.setSelected(false);
        System.out.println("|_|_|_|_|_|_|_|_|_ ALL EN Parser");


        Main.selectedParserType = Tools.ParserType.ALL;

        if (Tools.configENParserLocation == null || Tools.configEasyCCG == null) {
            Tools.createPathFileALL_EN();
        }

        if (!Tools.configENParserLocation.exists()) {


            try {
                Files.deleteIfExists(Tools.configEasyCCG.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Files.deleteIfExists(Tools.configENParserLocation.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("easyCCG location");
            boolean okEasyCCG = true;
            try {
                okEasyCCG = Tools.configEasyCCG.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!okEasyCCG)
                try {
                    throw new IOException();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            Alert firstTimeAlertCandC = new Alert(Alert.AlertType.WARNING);
            firstTimeAlertCandC.setTitle("First time configuration needed");
            firstTimeAlertCandC.setHeaderText("First time configuration C&C");
            firstTimeAlertCandC.setContentText(
                    "Configuration file is missing and/or corrupted." + '\n' +
                            "Please redo the configuration."
            );
            try {
                firstTimeAlertCandC.showAndWait();
                File f2 = setCandCLocation();
                System.out.println("------------------------------------------------ " + f2.toPath().toString());
                FileWriter fwCandC;
                fwCandC = new FileWriter(Tools.configCandC);
                fwCandC.write(f2.getAbsolutePath());
                fwCandC.close();
                CandCDefined = true;
            } catch (IOException e) {
                e.printStackTrace();
            }

            Alert firstTimeAlertEasyCCG = new Alert(Alert.AlertType.WARNING);
            firstTimeAlertEasyCCG.setTitle("First time configuration needed");
            firstTimeAlertEasyCCG.setHeaderText("First time configuration easyCCG ");
            firstTimeAlertEasyCCG.setContentText(
                    "Configuration file is missing and/or corrupted." + '\n' +
                            "Please redo the configuration."
            );
            try {
                firstTimeAlertEasyCCG.showAndWait();
                File f3 = setEasyCCGLocation();
                FileWriter fwEasyCCG;
                fwEasyCCG = new FileWriter(Tools.configEasyCCG);
                fwEasyCCG.write(f3.getAbsolutePath());
                fwEasyCCG.close();
                easyCCGDefined = true;
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("depCCG location");
            Alert firstTimeAlertDepCCG = new Alert(Alert.AlertType.WARNING);
            firstTimeAlertDepCCG.setTitle("First time configuration needed");
            firstTimeAlertDepCCG.setHeaderText("First time configuration depCCG ");
            firstTimeAlertDepCCG.setContentText(
                    "Configuration file is missing and/or corrupted." + '\n' +
                            "Please redo the configuration."
            );
            firstTimeAlertDepCCG.showAndWait();
            File f4 = setdepCCGLocation();

            depCCGDefined = true;


            defineEN_ALL_ParserLocation();
        }
    }


    /**
     * use all the JA parsers
     */
    @FXML
    private void setJAParser() {
        if (Tools.configJAParserLocation == null) {
            Tools.createPathFileJA();
        }

        radioCandCOnlyItem.setSelected(false);
        radioALL_EN_ParserItem.setSelected(false);
        radioJA_ParserItem.setSelected(true);

        System.out.println("|_|_|_|_|_|_|_|_|_ JA Parser");

        Main.selectedParserType = Tools.ParserType.JA;

        if ((!Tools.configJAParserLocation.exists())) {

            System.out.println("depCCG location");
            Alert firstTimeAlertDepCCG = new Alert(Alert.AlertType.WARNING);
            firstTimeAlertDepCCG.setTitle("First time configuration needed");
            firstTimeAlertDepCCG.setHeaderText("First time configuration depCCG ");
            firstTimeAlertDepCCG.setContentText(
                    "Configuration file is missing and/or corrupted." + '\n' +
                            "Please redo the configuration."
            );

            firstTimeAlertDepCCG.showAndWait();
            File f4 = setdepCCGLocation();
            depCCGDefined = true;


            System.out.println("jigg location");
            Alert firstTimeAlertJigg = new Alert(Alert.AlertType.WARNING);
            firstTimeAlertJigg.setTitle("First time configuration needed");
            firstTimeAlertJigg.setHeaderText("First time configuration Jigg ");
            firstTimeAlertJigg.setContentText(
                    "Configuration file is missing and/or corrupted." + '\n' +
                            "Please redo the configuration."
            );
            firstTimeAlertJigg.showAndWait();
            File f5 = setJiggLocation();
            jiggDefined = true;


            defineJA_ParserLocation();
        }
    }

    @Override
    public void initStage(Stage primaryStage) {
        this.view = primaryStage;
    }

    /**
     * For setting the template
     */
    private void setTemplate() {
        if (radioTemplateEvent.isSelected()) {
            System.out.println("||||||||||||||||| template event");
            Main.selectedTemplateType = Tools.TemplateType.EVENT;
        } else if (radioTemplateClassic.isSelected()) {
            System.out.println("||||||||||||||||| template classic");
            Main.selectedTemplateType = Tools.TemplateType.CLASSIC;
        }
    }

    /**
     * set the event template
     */
    public void setTemplateEvent() {
        radioTemplateEvent.setSelected(true);
        radioTemplateClassic.setSelected(false);
        setTemplate();
    }

    /**
     * set the classic template
     */
    public void setTemplateClassic() {
        radioTemplateClassic.setSelected(true);
        radioTemplateEvent.setSelected(false);
        setTemplate();
    }

}
