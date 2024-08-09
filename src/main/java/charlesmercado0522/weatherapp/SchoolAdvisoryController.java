package charlesmercado0522.weatherapp;

import charlesmercado0522.weatherapp.types.Advisories;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SchoolAdvisoryController implements Initializable {
    public TableView<Advisories> table;
    public TableColumn<Advisories, String> advisoryField;
    public TableColumn<Advisories, String> dateField;
    public Button addAdvisory;
    public TextArea advisoryArea;
    @FXML
    private ChoiceBox<String> schoolSelector;

    ArrayList<ArrayList<String>> schoolList = new ArrayList<>();
    ArrayList<String> schoolNames = new ArrayList<>();
    int entries = 0;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadList();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        schoolSelector.getItems().addAll(schoolNames);

        advisoryField.setCellValueFactory(new PropertyValueFactory<Advisories,String>("Advisory"));
        dateField.setCellValueFactory(new PropertyValueFactory<Advisories,String>("Date"));
    }

    public void loadTable() throws FileNotFoundException {
        String school = schoolSelector.getValue();
        String filename = "";
        for (int i = 0; i < schoolList.size(); i++) {
            if (Objects.equals(schoolNames.get(i),school)){
                filename = schoolList.get(i).getFirst();
                break;
            }
        }
        ArrayList<Advisories> advisories = new ArrayList<>();
        Scanner scanner = new Scanner(new FileReader("storage/advisory/"+filename+".csv"));
        while (scanner.hasNextLine()){
            String line = scanner.nextLine();
            String[] lineSplit = line.split(",");
            lineSplit[0] = lineSplit[0].replace(";", ",");
            Advisories advisory = new Advisories(lineSplit[0], lineSplit[1]);
            advisories.add(advisory);
        }
        ObservableList<Advisories> olAdvisories = FXCollections.observableArrayList(advisories);
        table.setItems(olAdvisories);
    }

    public void loadList() throws FileNotFoundException {
        Scanner scanner = new Scanner(new FileReader("storage/advisory/schoolList.csv"));
        int x = 0;
        while (scanner.hasNextLine()){
            String line = scanner.nextLine();
            String[] lineArr =  line.split(",");
            ArrayList<String> lineList = new ArrayList<>();
            lineList.add(lineArr[0]);
            lineList.add(lineArr[1]);
            schoolNames.add(lineArr[1]);
            schoolList.add(lineList);
            x++;
        }
    }

    public void createAdvisory() throws IOException {
        String school = schoolSelector.getValue();
        if(school==null) {
            return;
        }
        String filename = "";
        for (int i = 0; i < schoolList.size(); i++) {
            if (Objects.equals(schoolNames.get(i),school)){
                filename = schoolList.get(i).getFirst();
                break;
            }
        }

        StringBuilder saves = getAdvisories();
        if (saves==null){
            return;
        }
        String text = advisoryArea.getText();
        text = text.replace(",", ";");
        Date d = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy - HH:mm");
        String date = simpleDateFormat.format(d);
        StringBuilder entry = new StringBuilder(text + "," + date + "\n");
        entry.append(saves);
        System.out.println(entry);
        FileWriter writer = new FileWriter("storage/advisory/"+filename+".csv");
        writer.write(entry.toString());
        writer.close();
        loadTable();
    }

    public StringBuilder getAdvisories() throws FileNotFoundException {
        String school = schoolSelector.getValue();
        if(school==null) {
            return null;
        }
        String filename = "";
        for (int i = 0; i < schoolList.size(); i++) {
            if (Objects.equals(schoolNames.get(i),school)){
                filename = schoolList.get(i).getFirst();
                break;
            }
        }

        Scanner scanner = new Scanner(new FileReader("storage/advisory/"+filename+".csv"));

        StringBuilder entry = new StringBuilder();
        while (scanner.hasNextLine()) {
            entry.append(scanner.nextLine() + "\n");
        }

        return entry;
    }

}
