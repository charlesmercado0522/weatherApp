package charlesmercado0522.weatherapp;

import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

    public String initialURL = "https://api.openweathermap.org/data/2.5/forecast?q=Manila&appid=d2f8ca870fbd6621016b283c711bc4f3&cnt=5&units=metric";
    public Text city;
    public Text country;
    public ImageView img1, img2, img3, img4, img5;
    public Text time1, time2, time3, time4, time5;
    public Text temp1, temp2, temp3, temp4, temp5;
    public Text weather1, weather2, weather3, weather4, weather5;

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
        String[] filenames = new String[weatherList.size()];
        for (int i = 0; i < weatherList.size(); i++) {
            String temp = weatherList.get(i).toString();
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(temp);
            JSONArray weather = (JSONArray) parser.parse(obj.get("weather").toString());
            obj = (JSONObject) parser.parse(weather.getFirst().toString());
            String filename = obj.get("icon").toString() +".png";
            filenames[i] = filename;
        }
        img1.setImage(new Image(Objects.requireNonNull(getClass().getResource("icons/" + filenames[0])).toString()));
        img2.setImage(new Image(Objects.requireNonNull(getClass().getResource("icons/" + filenames[1])).toString()));
        img3.setImage(new Image(Objects.requireNonNull(getClass().getResource("icons/" + filenames[2])).toString()));
        img4.setImage(new Image(Objects.requireNonNull(getClass().getResource("icons/" + filenames[3])).toString()));
        img5.setImage(new Image(Objects.requireNonNull(getClass().getResource("icons/" + filenames[4])).toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTemps() {
        try {
            Double[] temps = new Double[5];
            for (int i = 0; i < weatherList.size(); i++) {
                String temp = weatherList.get(i).toString();
                JSONParser parser = new JSONParser();
                JSONObject obj = (JSONObject) parser.parse(temp);
                JSONObject main = (JSONObject) obj.get("main");
                Double temperature = (Double) main.get("temp");
                temps[i] = temperature;
            }
            temp1.setText(temps[0] + "°");
            temp2.setText(temps[1] + "°");
            temp3.setText(temps[2] + "°");
            temp4.setText(temps[3] + "°");
            temp5.setText(temps[4] + "°");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setWeather() {
        try {
            String[] weathers = new String[5];
            for (int i = 0; i < weatherList.size(); i++) {
                String temp = weatherList.get(i).toString();
                JSONParser parser = new JSONParser();
                JSONObject obj = (JSONObject) parser.parse(temp);
                JSONArray weatherObj = (JSONArray) obj.get("weather");
                obj = (JSONObject) parser.parse(weatherObj.get(0).toString());
                String weather = (String) obj.get("main");
                weathers[i] = weather;
            }
            weather1.setText(weathers[0]);
            weather2.setText(weathers[1]);
            weather3.setText(weathers[2]);
            weather4.setText(weathers[3]);
            weather5.setText(weathers[4]);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTimes(){
        time1.setText(times[0]);
        time2.setText(times[1]);
        time3.setText(times[2]);
        time4.setText(times[3]);
        time5.setText(times[4]);
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
