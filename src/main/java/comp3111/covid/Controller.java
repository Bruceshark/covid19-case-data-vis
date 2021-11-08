package comp3111.covid;

import comp3111.covid.Core.CSVFileOperator;
import comp3111.covid.Core.DailyStatistics;
import comp3111.covid.Core.TableSetter;
import comp3111.covid.Core.utils;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import org.controlsfx.control.CheckListView;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.*;
import java.util.List;

/**
 * Building on the sample skeleton for 'ui.fxml' Controller Class generated by SceneBuilder
 */
public class Controller {

    private static CSVFileOperator fileOperator;

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

    @FXML
    private DatePicker tableADatePicker;

    @FXML
    private Label tableATitle;

    @FXML
    private TableView<DailyStatistics> tableA;

    @FXML
    private TableColumn<DailyStatistics, String> tableACountry;

    @FXML
    private TableColumn<DailyStatistics, Integer> tableATotalCases;

    @FXML
    private TableColumn<DailyStatistics, Double> tableATotalCasesPerM;

    @FXML
    private CheckListView<String> tableACountryList;

    @FXML
    private Button doConfirmTableA;

    @FXML
    private LineChart chartB;

    @FXML
    private NumberAxis chartBX;

    @FXML
    private NumberAxis chartBY;

    @FXML
    private Button chartBButton;

    public void initialize() {
//        disable the text input of the date picker, but not make it gray
        tableADatePicker.getEditor().setDisable(true);
        tableADatePicker.getEditor().setOpacity(1);

        List<String> countryNames = fileOperator.getAllCountries();
        tableACountryList.setItems(FXCollections.observableArrayList(countryNames));
    }

    static {
        try {
            fileOperator = new CSVFileOperator("src"+File.separator+"main"+File.separator+"resources"+File.separator+"COVID_Dataset_v1.0.csv");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            String currentPath = null;
            try {
                currentPath = new java.io.File(".").getCanonicalPath();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.out.println("Current dir:" + currentPath);

            String currentDir = System.getProperty("user.dir");
            System.out.println("Current dir using System:" + currentDir);
        }
    }

    @FXML
    void doConfirmTableA(ActionEvent event) {
        tableACountry.setCellValueFactory(new PropertyValueFactory<DailyStatistics, String>("country"));
        tableATotalCases.setCellValueFactory(new PropertyValueFactory<DailyStatistics, Integer>("cumulativeInfected"));
        tableATotalCasesPerM.setCellValueFactory(new PropertyValueFactory<DailyStatistics, Double>("infectedPerMillion"));
        Boolean setterRes = TableSetter.setter(fileOperator, tableADatePicker, tableACountryList, tableA);
        if (!setterRes) return;
        String dateString = utils.localDateToString(tableADatePicker.getValue(), "MMMM dd, uuuu");
        tableATitle.setText("Number of Confirmed COVID-19 Cases as of " + dateString);
    }

    private static Calendar start;

    static {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = null;
        try {
            startDate = formatter.parse("2020-01-20");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date endDate = null;


        start = Calendar.getInstance();
        start.setTime(startDate);

    }

    @FXML
    void chartBGo(ActionEvent event) throws InterruptedException, ParseException {
        chartBX.setTickLabelFormatter(
                new StringConverter<Number>() {
                    @Override
                    public String toString(Number object) {
                        SimpleDateFormat a = new SimpleDateFormat("yy/MM/dd");
                        return a.format(new Date(object.longValue()));
                    }

                    @Override
                    public Number fromString(String string) {
                        return 0;
                    }
                }
        );
        chartBX.setAutoRanging(false); // manually set X-axis range and tick width
        SimpleDateFormat a = new SimpleDateFormat("yyyy/MM/dd");
        try {
            chartBX.setLowerBound(a.parse("2020/01/01").getTime());
            chartBX.setUpperBound(start.getTime().getTime());
            chartBX.setTickUnit(a.parse("1970/02/01").getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        chartB.setCreateSymbols(false); // do not show the symbols

        //chartB.getData().clear();
        List<String> countryNames = new ArrayList<>();
        countryNames.add("United Kingdom");
        countryNames.add("United States");
        countryNames.add("Hong Kong");
        countryNames.add("Israel");
        countryNames.add("World");
        countryNames.add("India");
        countryNames.add("Japan");
        countryNames.add("Singapore");
        HashSet<List<DailyStatistics>> countryTrendSet = fileOperator.getCountryTrendSet(countryNames);
        List<XYChart.Series<Number, Number>> seriesList = new ArrayList<>();
        if (chartB.getData().size() == 0) {
            for (List<DailyStatistics> countryTrend : countryTrendSet) {
                XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
                series1.setName(countryTrend.get(0).getCountry());
                seriesList.add(series1);
                chartB.getData().add(series1);
            }
        }



        Date date = start.getTime();
        int i = 0;
        for (List<DailyStatistics> countryTrend : countryTrendSet) {
            XYChart.Series<Number, Number> series1 = (XYChart.Series<Number, Number>) chartB.getData().get(i);
            for (DailyStatistics dailyStatistics : countryTrend) {
                if (dailyStatistics.getDate().equals(date)) {
                    series1.getData().add(new XYChart.Data(dailyStatistics.getDate().getTime(), dailyStatistics.getDeathPerMillion()));
                }

            }
            i++;
        }

        start.add(Calendar.DATE, 1);

        /*
        for (DailyStatistics dailyStatistics : countryTrend) {
            series1.getData().add(new XYChart.Data(dailyStatistics.getDate().getTime(), dailyStatistics.getDeathPerMillion()));
        }*/
        // customize tickLabelFormatter for showing dates


    }


    /**
     * Task Zero
     * To be triggered by the "Confirmed Cases" button on the Task Zero Tab
     */
    @FXML
    void doConfirmedCases(ActionEvent event) {
        String iDataset = textfieldDataset.getText();
        String iISO = textfieldISO.getText();
        String oReport = DataAnalysis.getConfirmedCases(iDataset, iISO);
        textAreaConsole.setText(oReport);
    }


    /**
     * Task Zero
     * To be triggered by the "Confirmed Deaths" button on the Task Zero Tab
     */
    @FXML
    void doConfirmedDeaths(ActionEvent event) {
        String iDataset = textfieldDataset.getText();
        String iISO = textfieldISO.getText();
        String oReport = DataAnalysis.getConfirmedDeaths(iDataset, iISO);
        textAreaConsole.setText(oReport);
    }


    /**
     * Task Zero
     * To be triggered by the "Rate of Vaccination" button on the Task Zero Tab
     */
    @FXML
    void doRateOfVaccination(ActionEvent event) {
        String iDataset = textfieldDataset.getText();
        String iISO = textfieldISO.getText();
        String oReport = DataAnalysis.getRateOfVaccination(iDataset, iISO);
        textAreaConsole.setText(oReport);
    }


}

