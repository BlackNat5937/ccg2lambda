package visualization.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

import java.io.*;


public class inputController {

    @FXML
    private TextField sentenceField;

    @FXML
    private ProgressBar visualizationProgressBar;

    @FXML
    private ListView<String> listSentences;

    private ObservableList<String> listSentencesItems = FXCollections.observableArrayList();

    public void addSentence(){
        if(sentenceField.getText() != null && sentenceField.getText() != ""){
            listSentencesItems.add(sentenceField.getText().toString());
            listSentences.setItems(listSentencesItems);
        }
    }

    public void visualize(){
        writeTxt();
        launchScript();
    }

    public void launchScript(){
        //script
    }

    public void writeTxt(){
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("../sentences.txt"), "utf-8"))) {

            //Browse the list and write each items to "sentences.txt"
            for(String s : listSentencesItems){
                writer.write(s);

                //add a dot if there isn't
                char[] sentenceChar = s.toCharArray();
                if(sentenceChar[sentenceChar.length -1] != '.'){
                    writer.write(".");
                }

                //next line
                ((BufferedWriter) writer).newLine();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
