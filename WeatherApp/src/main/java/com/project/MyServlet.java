package com.project;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.net.URLEncoder;
//import java.io.PrintWriter;

public class MyServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public MyServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        //API Key
        String apiKey = "c440a14db91ad2248c55ec1ebb767d8d";
        // Get the city from the form input
        String city = request.getParameter("cityname");
        String encodedCity = URLEncoder.encode(city, "UTF-8");

        // Create the URL for the OpenWeatherMap API request
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + encodedCity + "&appid=" + apiKey;
        
//          // Set the content type to HTML
//        response.setContentType("text/html");
//
//        // Get the PrintWriter to write the response
//        PrintWriter out = response.getWriter();
//
//        // Write the HTML content
//        out.println("<html>");
//        out.println("<head><title>Welcome Page</title></head>");
//        out.println("<body>");
//        out.println("<h1>Welcome to the Servlet Application!</h1>");
//        out.println(city);
//        out.println("<p>This content is generated from the servlet.</p>");
//        out.println("</body>");
//        out.println("</html>");
//
//        // Close the PrintWriter
//        out.close();

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStream inputStream = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);
            // System.out.println(reader);

            Scanner scanner = new Scanner(reader);
            StringBuilder responseContent = new StringBuilder();

            while (scanner.hasNext()) {
                responseContent.append(scanner.nextLine());
            }

            //System.out.println(responseContent);
            scanner.close();

            // Parse the JSON response to extract temperature, date, and humidity
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(responseContent.toString(), JsonObject.class);

            //Date & Time
            long dateTimestamp = jsonObject.get("dt").getAsLong() * 1000;
            String date = new Date(dateTimestamp).toString();

            //Temperature
            double temperatureKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
            int temperatureCelsius = (int) (temperatureKelvin - 273.15);

            //Humidity
            int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();

            //Wind Speed
            double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();

            //Weather Condition
            String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();

            // Set the data as request attributes (for sending to the jsp page)
            request.setAttribute("date", date);
            request.setAttribute("city", city);
            request.setAttribute("temperature", temperatureCelsius);
            request.setAttribute("weatherCondition", weatherCondition);
            request.setAttribute("humidity", humidity);
            request.setAttribute("windSpeed", windSpeed);
            request.setAttribute("weatherData", responseContent.toString());

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Forward the request to the weather.jsp page for rendering
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}
