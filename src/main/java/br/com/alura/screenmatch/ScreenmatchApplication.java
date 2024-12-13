package br.com.alura.screenmatch;

import br.com.alura.screenmatch.model.EpisodeData;
import br.com.alura.screenmatch.model.SeasonData;
import br.com.alura.screenmatch.model.SeriesData;
import br.com.alura.screenmatch.service.ApiConsumer;
import br.com.alura.screenmatch.service.DataConverter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		var apiConsumer = new ApiConsumer();
		var json = apiConsumer.getDataFrom("https://www.omdbapi.com/?t=the+flash&apikey=6ebb20a8");
		System.out.println(json);
		DataConverter dataConverter = new DataConverter();
		SeriesData data = dataConverter.convertData(json, SeriesData.class);
		System.out.println(data);
		json = apiConsumer.getDataFrom("https://www.omdbapi.com/?t=the+flash&season=1&episode=2&apikey=6ebb20a8");
		EpisodeData episodeData = dataConverter.convertData(json, EpisodeData.class);
		System.out.println(episodeData);

		List<SeasonData> seasons = new ArrayList<>();

		for (int i = 1; i <= data.totalSeasons(); i++) {
			json = apiConsumer.getDataFrom("https://www.omdbapi.com/?t=the+flash&season="+ i +"&apikey=6ebb20a8");
			SeasonData seasonData = dataConverter.convertData(json, SeasonData.class);
			seasons.add(seasonData);
		}
		seasons.forEach(System.out::println);
	}
}