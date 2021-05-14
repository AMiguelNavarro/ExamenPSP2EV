package com.svalero.examen.controller;

import com.svalero.examen.domain.Job;
import com.svalero.examen.service.JobService;
import com.svalero.examen.utils.Alerts;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class AppController implements Initializable {

    /* ELEMENTOS DE JAVAFX - SCENE BUILDER */
    public ListView lvJobs;
    public TextField tfDescription;
    public Button btSearch1, btSearch2, btExport;
    public ComboBox cbLocation;
    public Text txtType, txtCompany;
    public CheckBox checkBoxFullTime;

    /* ATRIBUTOS DE CLASE PROPIOS */
    private ObservableList<Job> observableListAllJobs;
    private List<Job> arrayListAllJobs; // Para trabajar con streams
    private JobService jobService;
    private Job jobSelected;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        jobService = new JobService();
        arrayListAllJobs = new ArrayList<>();

        observableListAllJobs = FXCollections.observableArrayList();
        lvJobs.setItems(observableListAllJobs);
        getAllJobs();
    }




    /* ------------------- MÉTODOS DE FXML ------------------- */

    @FXML
    private void getSelectedJob(Event event) {
        jobSelected = (Job) lvJobs.getSelectionModel().getSelectedItem();
        if (!validateJobSelected()) {
            return;
        }
        addDescriptionOfSelectedJob(jobSelected);
    }

    @FXML
    private void findByDescription(Event event) {
        String description = tfDescription.getText();
        if (description.isBlank()) {
            Alerts.showErrorAlert("Debes escribir un lenguaje de programación");
            return;
        }
        getJobsByDescription(description);
    }

    @FXML
    private void findByLocation(Event event) {
        String location = (String) cbLocation.getSelectionModel().getSelectedItem();
        String type;
        if (location.isBlank()) {
            Alerts.showErrorAlert("No puedes dejar la localización vacía");
            return;
        }
        if (checkBoxFullTime.isSelected()){
            type = "true";
        } else {
            type = "false";
        }
        getJobsByLocation(location, type);
    }

    @FXML
    private void exportCSV(Event event) {
        File file = export();
        if (file != null) {
            Alerts.showInfoAlert("Archivo exportado con éxito en: " + file.getAbsolutePath());
        } else {
            Alerts.showErrorAlert("Error al exportar los datos");
        }
    }

    @FXML
    private void reset(Event event) {
        getAllJobs();
    }


    /* --------------------------------------------------------- */
    /* --------------------------------------------------------- */
    /* --------------------------------------------------------- */
    /* ------------------- MÉTODOS PROPIOS DE CLASE ------------------- */

    private void getAllJobs() {
        lvJobs.getItems().clear();
        observableListAllJobs.clear();
        arrayListAllJobs.clear();

        jobService.getAllJobs()
                .flatMap(Observable::from)
                .doOnCompleted(() -> {
                    /* Lo pongo en un Platform.runLater para que no de error de hilos */
                    Platform.runLater(() -> {
                        System.out.println("Se carga listado general");
                        /* Aprovecho ArrayList que cargo con la llamada inicial y hago stream y mapeo a locations, así cargo el comboBox*/
                        cbLocation.setItems(arrayListAllJobs.stream()
                                .map(Job::getLocation)
                                .distinct()
                                .collect(Collectors.toCollection(FXCollections::observableArrayList)));

                            cbLocation.getSelectionModel().select(0);
                    });
                })
                .doOnError(throwable -> System.out.println(throwable.getMessage()))
                .subscribeOn(Schedulers.from(Executors.newCachedThreadPool()))
                .subscribe(job -> {
                    Platform.runLater(() -> {
                        observableListAllJobs.add(job);
                        arrayListAllJobs.add(job);
                    });
                });
    }

    private void getJobsByDescription(String description) {
        lvJobs.getItems().clear();
        observableListAllJobs.clear();

        jobService.getJobsByDescription(description)
                .flatMap(Observable::from)
                .doOnCompleted(() -> {
                    Platform.runLater(() -> {
                        System.out.println("Se carga listado con lenguaje: " + description);
                        if (observableListAllJobs.isEmpty()) {
                            Alerts.showInfoAlert("No existen trabajos para el lenguaje: " + description);
                        }
                    });
                })
                .doOnError(throwable -> System.out.println(throwable.getMessage()))
                .subscribeOn(Schedulers.from(Executors.newCachedThreadPool()))
                .subscribe(job -> {
                    Platform.runLater(() -> {
                        observableListAllJobs.add(job);
                    });
                });
    }

    private void getJobsByLocation(String location, String type) {
        lvJobs.getItems().clear();
        observableListAllJobs.clear();

        jobService.getJobsByLocation(location, type)
                .flatMap(Observable::from)
                .doOnCompleted(() ->{
                    Platform.runLater(() -> {
                        if (type.equals("true")) {
                            System.out.println("Se carga listado con localización: " + location + " y de tipo FULL TIME UNICAMENTE");
                        } else {
                            System.out.println("Se carga listado con localización: " + location + " de CUALQUIER tipo");
                        }
                    });
                })
                .doOnError(throwable -> System.out.println(throwable.getMessage()))
                .subscribeOn(Schedulers.from(Executors.newCachedThreadPool()))
                .subscribe(job -> {
                    Platform.runLater(() -> {
                        observableListAllJobs.add(job);
                    });
                });
    }


    private boolean validateJobSelected() {
        if (jobSelected == null) {
            Alerts.showErrorAlert("Selecciona un trabajo para poder ver los detalles");
            return false;
        } else {
            return true;
        }
    }

    private void addDescriptionOfSelectedJob(Job selected) {
        txtType.setText(selected.getType());
        txtCompany.setText(selected.getCompany());
    }

    private File export() {
        File file = null;
        try {
            FileChooser fileChooser = new FileChooser();
            file = fileChooser.showSaveDialog(null);
            FileWriter fileWriter = new FileWriter(file + ".csv");
            CSVPrinter printer = new CSVPrinter(fileWriter, CSVFormat.DEFAULT.withHeader("Título", "Compañia", "Localización"));

            for (Job job : observableListAllJobs) {
                printer.printRecord(
                        job.getTitle(),
                        job.getCompany(),
                        job.getLocation()
                );
            }

            printer.close();

        } catch (IOException e) {
            System.out.println("ERROR " + e.getMessage());
        }
        return file;
    }

}
