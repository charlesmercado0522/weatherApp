package charlesmercado0522.weatherapp;

import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class WeatherAppController implements Initializable {

    public Text city;
    public Text country;
    public ImageView img1, img2, img3, img4, img5;
    public Text time1, time2, time3, time4, time5;
    public Text temp1, temp2, temp3, temp4, temp5;
    public Text weather1, weather2, weather3, weather4, weather5;
    public GridPane gridPane;

    public String initialURL = "https://api.openweathermap.org/data/2.5/forecast?q=Manila&appid=d2f8ca870fbd6621016b283c711bc4f3&cnt=5&units=metric";
    String[] times;
    public JSONObject jsonObject;
    public JSONArray weatherList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        jsonObject = parseJSONFromAPIResponse();
        weatherList = getWeatherList();
        times = getTimes();
        setImages();
        setTimes();
        setTemps();
        setWeather();
        setDetails();
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

    public void setImages(){
        try {
        for (int i = 0; i < weatherList.size(); i++) {
            String temp = weatherList.get(i).toString();
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(temp);
            JSONArray weather = (JSONArray) parser.parse(obj.get("weather").toString());
            obj = (JSONObject) parser.parse(weather.getFirst().toString());
            String filename = obj.get("icon").toString() +".png";
            ImageView imageView = (ImageView) gridPane.getChildren().get(i);
            imageView.setImage(new Image(String.valueOf(getClass().getResource("icons/"+filename))));
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTemps() {
        try {
            for (int i = 0; i < weatherList.size(); i++) {
                String temp = weatherList.get(i).toString();
                JSONParser parser = new JSONParser();
                JSONObject obj = (JSONObject) parser.parse(temp);
                JSONObject main = (JSONObject) obj.get("main");
                Double temperature = (Double) main.get("temp");
                Text text = (Text) gridPane.getChildren().get(10+i);
                text.setText(temperature + "°");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setWeather() {
        try {
            for (int i = 0; i < weatherList.size(); i++) {
                String temp = weatherList.get(i).toString();
                JSONParser parser = new JSONParser();
                JSONObject obj = (JSONObject) parser.parse(temp);
                JSONArray weatherObj = (JSONArray) obj.get("weather");
                obj = (JSONObject) parser.parse(weatherObj.get(0).toString());
                String weather = (String) obj.get("main");
                Text text = (Text) gridPane.getChildren().get(15+i);
                text.setText(weather);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTimes(){
        for (int i = 0; i < times.length; i++) {
            Text text = (Text) gridPane.getChildren().get(5+i);
            text.setText(times[i]);
        }
    }

    public String[] getTimes() {
        try {
            String[] times = new String[5];
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

    public JSONArray getWeatherList(){
        try {
            return (JSONArray) jsonObject.get("list");
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject parseJSONFromAPIResponse(){
        try {
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(readAPIResponse());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String readAPIResponse(){
        try {
            HttpURLConnection apiConnection = fetchAPIResponse(initialURL);
            if (apiConnection.getResponseCode() != 200) {
                System.out.println("Connection Failed");
                return null;
            }
            StringBuilder jsonRes = new StringBuilder();
            Scanner scanner = new Scanner(apiConnection.getInputStream());

            while (scanner.hasNext()){
                jsonRes.append(scanner.nextLine());
            }
            scanner.close();
            return jsonRes.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private HttpURLConnection fetchAPIResponse(String urlString){
        try {
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            return con;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
