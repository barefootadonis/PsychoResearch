import java.util.ArrayList;
import java.util.Scanner;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public record SearchPage(ArrayList<ResearchPaper> papers, int totalResults) {}

    public static SearchPage searchPapers(String query, int page) throws IOException, InterruptedException {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = "https://api.openalex.org/works?search=" + encodedQuery
                + "&per_page=10&page=" + page;
        String apiKey = System.getenv("OPENALEX_API_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            throw new IOException("Set OPENALEX_API_KEY in IntelliJ.");
        }

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + apiKey)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("OpenAlex returned status" + response.statusCode());
        }

        JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonArray results = root.getAsJsonArray("results");
        int totalResults = root.getAsJsonObject("meta").get("count").getAsInt();

        ArrayList<ResearchPaper> papers = new ArrayList<>();

        for (int paperIndex = 0; paperIndex < results.size(); paperIndex++) {
            JsonObject result = results.get(paperIndex).getAsJsonObject();

            String title = result.get("title").getAsString();
            int year = result.get("publication_year").getAsInt();
            String link = result.get("id").getAsString();

            ArrayList<String> authorNames = new ArrayList<>();
            JsonArray authorships = result.getAsJsonArray("authorships");

            for (int authorIndex = 0; authorIndex < authorships.size(); authorIndex++) {
                JsonObject authorship = authorships.get(authorIndex).getAsJsonObject();
                JsonObject author = authorship.getAsJsonObject("author");
                authorNames.add(author.get("display_name").getAsString());
            }

            String authors = authorNames.isEmpty()
                    ? "Unknown"
                    : String.join(", ", authorNames);

            papers.add(new ResearchPaper(title, authors, year, link));
        }

        return new SearchPage(papers, totalResults);
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8080), 0);
        Gson gson = new Gson();

        server.createContext("/", exchange -> {
            switch (exchange.getRequestURI().getPath()) {
                case "/" -> sendFile(exchange, "index.html", "text/html");
                case "/style.css" -> sendFile(exchange, "style.css", "text/css");
                default -> send(exchange, 404, "text/plain", "Page not found.");
            }
        });

        server.createContext("/papers", exchange -> {
            String query = queryParameter(exchange, "query", "psychoinformatics");
            if (query.isBlank()) {
                query = "psychoinformatics";
            }

            int page;
            try {
                page = Integer.parseInt(queryParameter(exchange, "page", "1"));
                if (page < 1) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                send(exchange, 400, "application/json",
                        "{\"error\":\"Page must be a positive whole number.\"}");
                return;
            }

            String json;
            try {
                json = gson.toJson(searchPapers(query, page));
            } catch (Exception e) {
                send(exchange, 502, "application/json",
                        "{\"error\":\"Could not load papers from OpenAlex.\"}");
                return;
            }

            send(exchange, 200, "application/json", json);
        });

        server.start();
        System.out.println("PsychoResearch is running at http://localhost:8080");
    }

    private static void sendFile(HttpExchange exchange, String filename, String type)
            throws IOException {
        Path file = Path.of(filename);

        if (!Files.isRegularFile(file)) {
            send(exchange, 404, "text/plain", "Could not find " + filename);
            return;
        }

        send(exchange, 200, type, Files.readString(file));
    }

    private static void send(HttpExchange exchange, int status, String type, String body)
            throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", type + "; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);

        try (var output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }

    private static String queryParameter(HttpExchange exchange, String name, String fallback) {
        String rawQuery = exchange.getRequestURI().getRawQuery();

        if (rawQuery != null) {
            for (String parameter : rawQuery.split("&")) {
                String[] pair = parameter.split("=", 2);
                if (pair.length == 2 && pair[0].equals(name)) {
                    return URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
                }
            }
        }

        return fallback;
    }

}


/*
        Original console version of PsychoResearch. Kept as reference for creating sample papers, reading input with Scanner, and filtering by title and publication year.
    System.out.println("Welcome to PsychoResearch");
        searchPapers();

        ResearchPaper initial = new ResearchPaper("Test Title", "Test Authors", 2018, "www.pinterest.com");

        ResearchPaper two = new ResearchPaper("Jack and the Beanstalk", "Mrs. Somebody", 1998, "www.jandbs.com");

        ResearchPaper three = new ResearchPaper("My kink is karma", "Chapell Roan", 2024, "www.chapellroan.com");

        ArrayList<ResearchPaper> papers = new ArrayList<>();
        papers.add(initial);
        papers.add(two);
        papers.add(three);

        Scanner scanner =  new Scanner(System.in);
        System.out.println("Enter a title to search for: ");
        String searchTerm = scanner.nextLine();

        int minimumYear = 0;
        while (true) {
            System.out.println("Published from which year? Press Enter to skip:");
            String searchYear = scanner.nextLine().trim();

            if (searchYear.isEmpty()) {
            break;
            }
            try {
                minimumYear = Integer.parseInt(searchYear);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid year or leave it blank");
            }
        }

        boolean found = false;

        for (ResearchPaper paper : papers) {
            if (paper.getTitle().toLowerCase().contains(searchTerm.toLowerCase()) && paper.getPublicationYear() >= minimumYear) {
                displayPaper(paper);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No papers found");
        }
    }

    public static void displayPaper(ResearchPaper paper) {
        System.out.println("Title: " + paper.getTitle());
        System.out.println("Authors: " + paper.getAuthors());
        System.out.println("Publication year: " + paper.getPublicationYear());
        System.out.println("Link: " + paper.getLink());
        System.out.println();
    } */