package charlesmercado0522.weatherapp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.Scanner;

public class WeatherAppController implements Initializable {


    @FXML
    private TextField cityTextField;
    @FXML
    private Text city;
    @FXML
    private Text country;
    @FXML
    private GridPane gridPane;

    private String apiKey = "d2f8ca870fbd6621016b283c711bc4f3";
    private String initialURL = "https://api.openweathermap.org/data/2.5/forecast?q=Manila&appid=" + apiKey + "&cnt=16&units=metric";
    private String[] times;
    private JSONObject jsonObject;
    private JSONArray weatherList;

    int idx = 0;

    public void open24HrAfter24() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("weatherAppNext.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Weather after the initial 24 hours");
        stage.show();
    }

    public void openAdvisory() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("schoolAdvisory.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("School Weather Advisories");
        stage.show();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            jsonObject = parseJSONFromAPIResponse(initialURL);
        } catch (FileNotFoundException | ParseException e) {
            throw new RuntimeException(e);
        }
        weatherList = getWeatherList();
        times = getTimes();
        setImages();
        setTimes();
        setTemps();
        setWeather();
        setDetails();
    }

    @FXML
    public void getWeather() throws FileNotFoundException, ParseException {
        String cityName = cityTextField.getText().replaceAll(" ", "+");
        String apiURL = "https://api.openweathermap.org/data/2.5/forecast?q=" + cityName + "&appid=" + apiKey + "&cnt=16&units=metric";

        jsonObject = parseJSONFromAPIResponse(apiURL);
        if (jsonObject != null) {
            weatherList = getWeatherList();
            times = getTimes();
            setImages();
            setTimes();
            setTemps();
            setWeather();
            setDetails();
        } else {
            System.out.println("Error: Couldn't retrieve weather data.");
        }
    }


    public void checkIfEnterPressed() {
        cityTextField.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ENTER){
                try {
                    getWeather();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void setDetails() {
        try {
            JSONObject temp = (JSONObject) jsonObject.get("city");
            String cityName = (String) temp.get("name");
            String countryID = (String) temp.get("country");
            city.setText(cityName);
            country.setText(countryID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setImages() {
        try {
            for (int i = 0; i < weatherList.size()/2; i++) {
                String temp = weatherList.get(i+idx).toString();
                JSONParser parser = new JSONParser();
                JSONObject obj = (JSONObject) parser.parse(temp);
                JSONArray weather = (JSONArray) parser.parse(obj.get("weather").toString());
                obj = (JSONObject) parser.parse(weather.getFirst().toString());
                String filename = obj.get("icon").toString() + ".png";
                System.out.println(gridPane.getChildren().get(i).getClass());
                ImageView imageView = (ImageView) gridPane.getChildren().get(i);
                imageView.setImage(new Image(String.valueOf(getClass().getResource("icons/" + filename))));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTemps() {
        try {
            for (int i = 0; i < weatherList.size()/2; i++) {
                String temp = weatherList.get(idx + i).toString();
                JSONParser parser = new JSONParser();
                JSONObject obj = (JSONObject) parser.parse(temp);
                JSONObject main = (JSONObject) obj.get("main");
                String temperature = main.get("temp").toString();
                Text text = (Text) gridPane.getChildren().get(8*2 + i);
                text.setText(temperature + "°");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setWeather() {
        try {
            for (int i = 0; i < weatherList.size()/2; i++) {
                String temp = weatherList.get(i+idx).toString();
                JSONParser parser = new JSONParser();
                JSONObject obj = (JSONObject) parser.parse(temp);
                JSONArray weatherObj = (JSONArray) obj.get("weather");
                obj = (JSONObject) parser.parse(weatherObj.get(0).toString());
                String weather = (String) obj.get("main");
                Text text = (Text) gridPane.getChildren().get(3*8 + i);
                text.setText(weather);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTimes() {
        for (int i = 0; i < times.length; i++) {
            Text text = (Text) gridPane.getChildren().get(8 + i);
            text.setText(times[i]);
        }
    }

    public String[] getTimes() {
        try {
            String[] times = new String[16];
            System.out.println(weatherList.size());
            for (int i = 0; i < weatherList.size(); i++) {
                String temp = weatherList.get(i).toString();
                JSONParser parser = new JSONParser();
                JSONObject obj = (JSONObject) parser.parse(temp);
                long dt = Long.parseLong(obj.get("dt").toString());
                Instant instant = Instant.ofEpochSecond(dt);
                LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.of(ZoneId.systemDefault().getId()));
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                times[i] = localDateTime.format(formatter);
            }
            return times;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONArray getWeatherList() {
        try {
            return (JSONArray) jsonObject.get("list");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject parseJSONFromAPIResponse(String apiURL) throws FileNotFoundException, ParseException {
        JSONParser parser = new JSONParser();
        try {
            return (JSONObject) parser.parse(readAPIResponse(apiURL));
        } catch (Exception e) {
            Scanner scanner = new Scanner(new FileReader("storage/last-weather.json"));
            StringBuilder scanned = new StringBuilder();
            while (scanner.hasNext()){
                scanned.append(scanner.next());
            }
            return (JSONObject) parser.parse(scanned.toString());
        }
    }

    private String readAPIResponse(String apiURL) {
        try {
            HttpURLConnection apiConnection = fetchAPIResponse(apiURL);
            if (apiConnection.getResponseCode() != 200) {
                System.out.println("Connection Failed");
                return null;
            }
            new File("storage").mkdirs();
            FileWriter writer = new FileWriter("storage/last-weather.json");
            StringBuilder jsonRes = new StringBuilder();
            Scanner scanner = new Scanner(apiConnection.getInputStream());

            while (scanner.hasNext()) {
                String temp = scanner.nextLine();
                jsonRes.append(temp);
                writer.write(temp);
            }
            writer.close();
            scanner.close();
            return jsonRes.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private HttpURLConnection fetchAPIResponse(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            return con;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
