package com.vishu.coronavirustracker.services;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.function.ServerRequest.Headers;

import com.vishu.coronavirustracker.model.LocationStats;

@Service
public class CoronaVirusDataServices {
	
	private String VIRUS_DATA_URL= "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
	
	private List<LocationStats> allStats = new ArrayList<>();
	
	
	public List<LocationStats> getAllStats() {
		return allStats;
	}


	@PostConstruct
	@Scheduled(cron = "* * 1 * * *")
	public void fetchVirusData() throws IOException, InterruptedException {
		
		List<LocationStats> newStats = new ArrayList<>();
		
		// make an HTTP request using httpclient
		URL url = new URL(VIRUS_DATA_URL);
        CSVFormat csvFormat = CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase();

        try(CSVParser csvParser = CSVParser.parse(url, StandardCharsets.UTF_8, csvFormat)) {
            for(CSVRecord csvRecord : csvParser) {
                LocationStats locationStats = new LocationStats();
                locationStats.setState(csvRecord.get("Province/State"));
                locationStats.setCountry(csvRecord.get("Country/Region"));
                int latestCases = Integer.parseInt(csvRecord.get(csvRecord.size()-1));
                int prevDayCases = Integer.parseInt(csvRecord.get(csvRecord.size()-2));
                
                locationStats.setLatestTotalCases(latestCases);
                locationStats.setDifFromPrevDay(latestCases-prevDayCases);

                newStats.add(locationStats);
            }
            this.allStats=newStats;
        } catch (IOException e) {
            e.printStackTrace();
        }
		        
	}

}
