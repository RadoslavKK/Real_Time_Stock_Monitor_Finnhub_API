package src;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class StockTracker {
    private static final String API_KEY = loadApiKey();
    private static final String BASE_URL = loadUrl();
    private static final HttpClient client = HttpClient.newHttpClient();

    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String RESET = "\u001B[0m";

    private static List<StockInfo> stockList = new ArrayList<>();

    public static void main(String[] args) {
        if (API_KEY.isEmpty() || BASE_URL.isEmpty()) {
            System.err.println("Missing API key or Base URL. Please check the resource files.");
            return;
        }

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        stockList.add(new StockInfo("Amazon", "AMZN", 0, 0, 2, 3000.0, true));
        stockList.add(new StockInfo("Apple", "AAPL", 0, 0, 5, 150.0, true));
        stockList.add(new StockInfo("Boeing", "BA", 0, 0, 3, 210.0, true));
        stockList.add(new StockInfo("Coca-Cola", "KO", 0, 0, 12, 60.0, true));
        stockList.add(new StockInfo("Facebook", "META", 0, 0, 8, 200.0, true));
        stockList.add(new StockInfo("Google", "GOOGL", 0, 0, 6, 2500.0, true));
        stockList.add(new StockInfo("Microsoft", "MSFT", 0, 0, 4, 280.0, true));
        stockList.add(new StockInfo("NVIDIA", "NVDA", 0, 0, 7, 400.0, true));
        stockList.add(new StockInfo("Tesla", "TSLA", 0, 0, 10, 600.0, true));

        Runnable task = () -> {
            try {
                System.out.print("\033[H\033[2J");
                System.out.flush();

                for (StockInfo stock : stockList) {
                    if (!stock.isActive()) continue;

                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(BASE_URL + stock.getStockSymbol() + "&token=" + API_KEY))
                            .GET()
                            .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();

                    if (json.has("c") && !json.get("c").isJsonNull()) {
                        double currentPrice = json.get("c").getAsDouble();
                        stock.setPreviousPrice(stock.getCurrentPrice());
                        stock.setCurrentPrice(currentPrice);
                    } else {
                        System.out.println("Warning: No valid price data for " + stock.getStockSymbol());
                        continue;
                    }
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                LocalTime sofiaTime = LocalTime.now(ZoneId.of("Europe/Sofia"));
                ZonedDateTime newYorkTime = ZonedDateTime.now(ZoneId.of("America/New_York"));

                System.out.println("\nLocal Time in Sofia : " + sofiaTime.format(formatter));
                System.out.println("New York City Time  : " + newYorkTime.format(formatter) + "\n");

                System.out.println("Your Stock Portfolio:\n");
                System.out.printf("%-20s %-10s %-12s %-15s %-15s %-10s %-15s%n",
                        "Company Name", "Symbol", "Current $", "Previous $", "Amount Owned", "Buy @ $", "Profit/Loss $");

                System.out.println(
                        "----------------------------------------------------------------------------------------------------------------");

                for (StockInfo s : stockList) {
                    if (s.getBuyPrice() <= 0) {
                        System.out.println("Invalid buy price for " + s.getCompanyName());
                        continue;
                    }

                    double profitLoss = (s.getCurrentPrice() - s.getBuyPrice()) * s.getAmountOwned();
                    String color = profitLoss >= 0 ? GREEN : RED;

                    System.out.printf(
                            "%-20s %-10s %-12.2f %-15.2f %-15d %-10.2f " + color + "%-15.2f" + RESET + "%n",
                            s.getCompanyName(),
                            s.getStockSymbol(),
                            s.getCurrentPrice(),
                            s.getPreviousPrice(),
                            s.getAmountOwned(),
                            s.getBuyPrice(),
                            profitLoss);
                }

            } catch (Exception e) {
                System.err.println("Error fetching stock price: ");
                e.printStackTrace();
            }
        };

        scheduler.scheduleAtFixedRate(task, 0, 15, TimeUnit.SECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutting down scheduler...");
            scheduler.shutdown();
        }));
    }

    public static String loadApiKey() {
        try {
            Path path = Paths.get("resources/apikey.txt");
            return new String(Files.readAllBytes(path)).trim();
        } catch (IOException e) {
            System.err.println("Error reading the API key file: ");
            e.printStackTrace();
            return "";
        }
    }

    public static String loadUrl() {
        try {
            Path path = Paths.get("resources/baseurl.txt");
            return new String(Files.readAllBytes(path)).trim();
        } catch (IOException e) {
            System.err.println("Error reading the Base URL file: ");
            e.printStackTrace();
            return "";
        }
    }
}
