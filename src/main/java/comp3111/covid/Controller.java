package comp3111.covid;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

/**
 * Building on the sample skeleton for 'ui.fxml' Controller Class generated by SceneBuilder 
 */
public class Controller {

    @FXML
    private Tab tabTaskZero;

    @FXML
    private TextField textfieldISO;

    @FXML
    private Button buttonConfirmedDeaths;

    @FXML
    private TextField textfieldDataset;

    @FXML
    private Button buttonRateOfVaccination;

    @FXML
    private Button buttonConfirmedCases;

    @FXML
    private Tab tabReport1;

    @FXML
    private Tab tabReport2;

    @FXML
    private Tab tabReport3;

    @FXML
    private Tab tabApp1;

    @FXML
    private Tab tabApp2;

    @FXML
    private Tab tabApp3;

    @FXML
    private TextArea textAreaConsole;

  

    /**
     *  Task Zero
     *  To be triggered by the "Confirmed Cases" button on the Task Zero Tab 
     *  
     */
    @FXML
    void doConfirmedCases(ActionEvent event) {
    	String iDataset = textfieldDataset.getText();
    	String iISO = textfieldISO.getText();
    	String oReport = DataAnalysis.getConfirmedCases(iDataset, iISO);
    	textAreaConsole.setText(oReport);
    }

  
    /**
     *  Task Zero
     *  To be triggered by the "Confirmed Deaths" button on the Task Zero Tab
     *  
     */
    @FXML
    void doConfirmedDeaths(ActionEvent event) {
    	String iDataset = textfieldDataset.getText();
    	String iISO = textfieldISO.getText();
    	String oReport = DataAnalysis.getConfirmedDeaths(iDataset, iISO);
    	textAreaConsole.setText(oReport);
    }

  
    /**
     *  Task Zero
     *  To be triggered by the "Rate of Vaccination" button on the Task Zero Tab
     *  
     */
    @FXML
    void doRateOfVaccination(ActionEvent event) {
    	String iDataset = textfieldDataset.getText();
    	String iISO = textfieldISO.getText();
    	String oReport = DataAnalysis.getRateOfVaccination(iDataset, iISO);
    	textAreaConsole.setText(oReport);
    }  

}

