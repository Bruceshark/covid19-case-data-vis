package comp3111.covid.controller;

import comp3111.covid.core.*;
import comp3111.covid.core.data.CSVFileOperator;
import comp3111.covid.core.data.DailyStatistics;
import comp3111.covid.core.data.SortPolicy;
import comp3111.covid.core.data.SortPolicyE;
import comp3111.covid.core.tabtype.ChartType;
import comp3111.covid.core.uisetters.ChartSetter;
import comp3111.covid.ui.CheckListViewWithList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Chart Controller Class for chart tabs
 */
public class ChartController {

    private ChartType type;

    private CSVFileOperator fileOperator;

    /**
     * The line chart. Made public for testing purposes.
     */
    @FXML
    public LineChart<Number, Number> chart;

    @FXML
    private NumberAxis chartX;

    @FXML
    private NumberAxis chartY;

    @FXML
    private TextField chartText;

    @FXML
    private CheckListViewWithList<String> chartCountryList;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    /**
     * The current tab. Made public for testing purposes.
     */
    @FXML
    public Tab pane;

    @FXML
    private ChoiceBox<SortPolicy> choiceBox;

    public ChartController() {

    }

    /**
     * Initialize some properties of a chart tab
     * @param type Chart Type
     * @param fileOperator csv file operator
     */
    public void initData(ChartType type, CSVFileOperator fileOperator) {
        this.type = type;
        this.fileOperator = fileOperator;

        switch (type) {
            case A:
                chart.setTitle("Cumulative Confirmed COVID-19 Cases (per 1M)");
                chartY.setLabel("Confirmed Cases Per Million");
                pane.setText("Cases Chart");
                break;
            case B:
                chart.setTitle("Cumulative Confirmed COVID-19 Deaths (per 1M)");
                chartY.setLabel("Confirmed Deaths Per Million");
                pane.setText("Death Chart");
                break;
            case C:
                chart.setTitle("Vaccination Rate on Certain Day");
                chartY.setLabel("Vaccination Rate");
                pane.setText("Vaccination Rate Chart");
                break;
        }

        startDatePicker.getEditor().setDisable(true);
        startDatePicker.getEditor().setOpacity(1);
        startDatePicker.setValue(utils.dateToLocalDate(fileOperator.getMinimumDate()));
        endDatePicker.getEditor().setDisable(true);
        endDatePicker.getEditor().setOpacity(1);
        endDatePicker.setValue(utils.dateToLocalDate(fileOperator.getMaximumDate()));

        final Callback<DatePicker, DateCell> cellFactoryEnd =
                new Callback<DatePicker, DateCell>() {
                    @Override
                    public DateCell call(final DatePicker datePicker) {
                        return new DateCell() {
                            @Override
                            public void updateItem(LocalDate item, boolean empty) {
                                super.updateItem(item, empty);

                                if (item.isBefore(
                                        startDatePicker.getValue().plusDays(1))
                                ) {
                                    setDisable(true);
                                    getStyleClass().add("disabled-date");
                                }
                                if (item.isAfter(utils.dateToLocalDate(fileOperator.getMaximumDate()))) {
                                    setDisable((true));
                                    getStyleClass().add("disabled-date");
                                }
                            }
                        };
                    }
                };
        final Callback<DatePicker, DateCell> cellFactoryStart =
                new Callback<DatePicker, DateCell>() {
                    @Override
                    public DateCell call(final DatePicker datePicker) {
                        return new DateCell() {
                            @Override
                            public void updateItem(LocalDate item, boolean empty) {
                                super.updateItem(item, empty);

                                if (item.isBefore(
                                        utils.dateToLocalDate(fileOperator.getMinimumDate()))
                                ) {
                                    setDisable(true);
                                    getStyleClass().add("disabled-date");
                                }
                                if (item.isAfter(utils.dateToLocalDate(fileOperator.getMaximumDate()).minusDays(1))) {
                                    setDisable((true));
                                    getStyleClass().add("disabled-date");
                                }
                            }
                        };
                    }
                };

        startDatePicker.setDayCellFactory(cellFactoryStart);
        endDatePicker.setDayCellFactory(cellFactoryEnd);

        startDatePicker.valueProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue.isAfter(endDatePicker.getValue()) || newValue.isEqual(endDatePicker.getValue()))
                endDatePicker.setValue(startDatePicker.getValue().plusDays(1));
        }));

        choiceBox.getItems().clear();
        choiceBox.getItems().add(new SortPolicy("Country name", SortPolicyE.NAME));
        choiceBox.getItems().add(new SortPolicy("Population", SortPolicyE.POP));
        choiceBox.getItems().add(new SortPolicy("Population density", SortPolicyE.POP_D));
        choiceBox.getItems().add(new SortPolicy("Median age", SortPolicyE.MED));
        choiceBox.getItems().add(new SortPolicy("GDP per capita", SortPolicyE.GDP));
        choiceBox.getSelectionModel().select(0);
        choiceBox.setOnAction((event) -> {
            SortPolicy selectedItem = choiceBox.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                List<String> countryNames = fileOperator.searchCountry(chartText.getText(), selectedItem.policyE);
                chartCountryList.update(countryNames);}


        });

        chartText.setText("");
        chartText.textProperty().addListener((observable, oldValue, newValue) -> {
            //String countryName = tableAText.getText();
            List<String> countryNamesAdd = fileOperator.searchCountry(newValue,
                    choiceBox.getSelectionModel().getSelectedItem().policyE);
            chartCountryList.update(countryNamesAdd);
            chartText.requestFocus();
        });

        List<String> countryNames = fileOperator.getAllCountries();

        chartCountryList.init(countryNames);
        chart.getData().clear();
    }

    public void initialize() {

    }

    @FXML
    void doConfirmChart(ActionEvent event) {
    	if (type == ChartType.A) {
    		ChartSetter.setGraphPropeties(chart, startDatePicker, endDatePicker);
    	} else if (type == ChartType.B) {
    		ChartSetter.setGraphPropeties(chart, startDatePicker, endDatePicker);
        } else if (type == ChartType.C) {
        	ChartSetter.setGraphPropeties_C(chart, startDatePicker, endDatePicker);
        }
        // save state of checked
        chartCountryList.saveState();
        List<String> countryNames = chartCountryList.getCheckedItems();
        Map<String, List<DailyStatistics>> countryTrendMap = fileOperator.getCountryTrendMap(countryNames,
                utils.localDateToDate(startDatePicker.getValue()), utils.localDateToDate(endDatePicker.getValue()));
        if (countryTrendMap.size() <= 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select at least one country", ButtonType.YES);
            alert.show();
            return;
        }
        if (type == ChartType.A) {
            ChartSetter.updateGraph_A(chart, countryTrendMap);
        } else if (type == ChartType.B) {
            ChartSetter.updateGraph_B(chart, countryTrendMap);
        } else if (type == ChartType.C) {
            ChartSetter.updateGraph_C(chart, countryTrendMap);
        }

    }

}
