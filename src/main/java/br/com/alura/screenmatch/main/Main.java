package br.com.alura.screenmatch.main;

import br.com.alura.screenmatch.model.SeasonData;
import br.com.alura.screenmatch.model.SeriesData;
import br.com.alura.screenmatch.service.ApiConsumer;
import br.com.alura.screenmatch.service.DataConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private final Scanner sc = new Scanner(System.in);
    private final ApiConsumer apiConsumer = new ApiConsumer();
    private final DataConverter dataConverter = new DataConverter();
    private final String ADDRESS = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6ebb20a8";

    public void showMenu() {
        var menu = """
                1 - Search series
                2 - Search episodes
                
                0 - Exit
                """;
        System.out.println(menu);
        var option = sc.nextInt();
        sc.nextLine();

        switch (option) {
            case 1:
                searchSerieWeb();
                break;
            case 2:
                searchEpisodeBySerie();
                break;
            case 0:
                System.out.println("Logging out...");
                break;
            default:
                System.out.println("Invalid option");
        }
    }

    private void searchSerieWeb() {
        SeriesData data = getSerieData();
        System.out.println(data);
    }

    private SeriesData getSerieData() {
        System.out.println("Search for the name of the show:");
        var nameSerie = sc.nextLine();
        var json = apiConsumer.getDataFrom(ADDRESS + nameSerie.replace(" ", "+") + API_KEY);
        SeriesData data = dataConverter.convertData(json, SeriesData.class);
        return data;
    }

    private void searchEpisodeBySerie(){
        SeriesData seriesData = getSerieData();
        List<SeasonData> seasons = new ArrayList<>();

        for (int i = 1; i <= seriesData.totalSeasons(); i++) {
            var json = apiConsumer.getDataFrom(ADDRESS + seriesData.title().replace(" ", "+") + "&season=" + i + API_KEY);
            SeasonData seasonData = dataConverter.convertData(json, SeasonData.class);
            seasons.add(seasonData);
        }
        seasons.forEach(System.out::println);
    }
}
