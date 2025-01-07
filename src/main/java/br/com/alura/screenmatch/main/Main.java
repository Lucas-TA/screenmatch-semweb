package br.com.alura.screenmatch.main;

import br.com.alura.screenmatch.model.Episode;
import br.com.alura.screenmatch.model.EpisodeData;
import br.com.alura.screenmatch.model.SeasonData;
import br.com.alura.screenmatch.model.SeriesData;
import br.com.alura.screenmatch.service.ApiConsumer;
import br.com.alura.screenmatch.service.DataConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
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
                .peek(e -> System.out.println("First filter (N/a)" + e))
                .sorted(Comparator.comparing(EpisodeData::rating).reversed())
                .peek(e -> System.out.println("Ordering " + e))
                .limit(5)
                .peek(e -> System.out.println("limiting " + e))
                .forEach(System.out::println);


        List<Episode> episodes = seasons.stream()
                .flatMap(t -> t.episodes().stream()
                        .map(d -> new Episode(t.numberOfSeason(), d))
                ).collect(Collectors.toList());

        episodes.forEach(System.out::println);

        System.out.println("Search for episode by title");
        var typedTitle = sc.nextLine();
        Optional<Episode> episodeSearched = episodes.stream()
                .filter(e -> e.getTitle().toUpperCase().contains(typedTitle.toUpperCase()))
                .findFirst();
        if (episodeSearched.isPresent()) {
            System.out.println("Episode found");
            System.out.println("Season: " + episodeSearched.get().getSeason());
        } else {
            System.out.println("Episode not found");
        }
//
//        System.out.println("Inform an year: ");
//        var year = sc.nextInt();
//        sc.nextLine();
//
//        LocalDate yearSearched = LocalDate.of(year, 1, 1);
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//
//        episodes.stream()
//                .filter(e -> e.getReleaseDate() != null && e.getReleaseDate().isAfter(yearSearched))
//                .forEach(e -> System.out.println(
//                        "Season: " + e.getSeason() + "Episode: " + e.getEpisodeNumber() + "Release year: " + e.getReleaseDate().format(formatter)
//                ));
        Map<Integer, Double> seasonRating = episodes.stream()
                .filter(e -> e.getRating() > 0.0)
                .collect(Collectors.groupingBy(Episode::getSeason, Collectors.averagingDouble(Episode::getRating)));
        System.out.println(seasonRating);

        DoubleSummaryStatistics est = episodes.stream()
                .filter(e -> e.getRating() > 0.0)
                .collect(Collectors.summarizingDouble(Episode::getRating));
        System.out.println("Average: " + est.getAverage());
        System.out.println("Best rating: " + est.getMax());
        System.out.println("Worst rating: " + est.getMin());
        System.out.println("Number of episodes: " + est.getCount());
    }
}
