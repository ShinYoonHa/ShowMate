package com.culture.CultureService;

import com.culture.CultureService.service.PlaceApiService;
import com.culture.CultureService.service.ShowApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CultureServiceApplication implements CommandLineRunner {

	@Autowired
	private ShowApiService showApiService;
	@Autowired
	private PlaceApiService placeApiService;

	public static void main(String[] args) {
		SpringApplication.run(CultureServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// 임의의 값으로 테스트
		//String stDate = "20240101";
		//String edDate = "20240730";
		//String page = "1";
		//String rows = "100";

		//showApiService.fetchAndSaveShowData(stDate, edDate, page, rows);
		//placeApiService.fetchAndSaveAwardData(stDate, edDate, page, rows);
	}
}
