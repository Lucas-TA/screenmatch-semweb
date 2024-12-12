package br.com.alura.screenmatch;

import br.com.alura.screenmatch.model.SerieData;
import br.com.alura.screenmatch.service.ApiConsumer;
import br.com.alura.screenmatch.service.DataConverter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		var apiConsumer = new ApiConsumer();
		var json = apiConsumer.getDataFrom("https://www.omdbapi.com/?t=harry+potter&apikey=6ebb20a8");
		System.out.println(json);
		DataConverter dataConverter = new DataConverter();
		SerieData data = dataConverter.convertData(json, SerieData.class);
		System.out.println(data);
	}
}