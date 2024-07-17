package com.culture.CultureService;

import com.culture.CultureService.service.AwardApiService;
import com.culture.CultureService.service.PlaceApiService;
import com.culture.CultureService.service.GenreStatisticApiService;
import com.culture.CultureService.service.ShowApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class CultureServiceApplication implements CommandLineRunner {

	@Autowired
	private ShowApiService showApiService;
	@Autowired
	private PlaceApiService placeApiService;
	@Autowired
	private AwardApiService awardApiService;
	@Autowired
	private GenreStatisticApiService genreStatisticApiService;



	public static void main(String[] args) {
		SpringApplication.run(CultureServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// 임의의 값으로 테스트
//		String stDate = "20240101";
//		String edDate = "20240730";
//		String page = "1";
//		String rows = "1000";
//		placeApiService.fetchAndSavePlaceData(page, rows);
//		showApiService.fetchAndSaveShowData(stDate, edDate, page, rows);
//		awardApiService.fetchAndSaveAwardData(stDate, edDate, page, rows);
//		genreStatisticApiService.fetchAndSaveGenreStatistics(stDate, edDate);
	}
}

