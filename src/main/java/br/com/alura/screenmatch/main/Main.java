package br.com.alura.screenmatch.main;

import br.com.alura.screenmatch.model.EpisodeData;
import br.com.alura.screenmatch.model.SeasonData;
import br.com.alura.screenmatch.model.SeriesData;
import br.com.alura.screenmatch.service.ApiConsumer;
import br.com.alura.screenmatch.service.DataConverter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    private final Scanner sc = new Scanner(System.in);
    private final ApiConsumer apiConsumer = new ApiConsumer();
    private final DataConverter dataConverter = new DataConverter();

    public void showMenu() {
        String ADDRESS = "https://www.omdbapi.com/?t=";
        String API_KEY = "&apikey=6ebb20a8";

        System.out.println("Write the name of the Title you are looking for");

        var serieName = sc.nextLine();
        var json = apiConsumer.getDataFrom(ADDRESS + serieName.replace(" ", "+") + API_KEY);

        SeriesData data = dataConverter.convertData(json, SeriesData.class);
        System.out.println(data);

        List<SeasonData> seasons = new ArrayList<>();

        for (int i = 1; i <= data.totalSeasons(); i++) {
            json = apiConsumer.getDataFrom(ADDRESS + serieName.replace(" ", "+") + "&season=" + i + API_KEY);
            SeasonData seasonData = dataConverter.convertData(json, SeasonData.class);
            seasons.add(seasonData);
        }

        seasons.forEach(t -> t.episodes().forEach(e -> System.out.println(e.title())));

        List<EpisodeData> episodesData = seasons.stream()
                .flatMap(t -> t.episodes().stream())
                .collect(Collectors.toList());

        System.out.println("\n Top 5 Episodes:");

        episodesData.stream()
                .filter(e -> !e.rating().equalsIgnoreCase("N/A"))
                .sorted(Comparator.comparing(EpisodeData::rating).reversed())
                .limit(5)
                .forEach(System.out::println);
    }
}
