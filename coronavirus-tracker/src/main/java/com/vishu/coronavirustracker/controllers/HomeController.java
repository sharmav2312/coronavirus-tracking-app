package com.vishu.coronavirustracker.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.vishu.coronavirustracker.model.LocationStats;
import com.vishu.coronavirustracker.services.CoronaVirusDataServices;

@Controller
public class HomeController {
	
	@Autowired
	CoronaVirusDataServices coronaVirusDataServices;
	
	@GetMapping("/")
	public String home(Model model) {
		List<LocationStats> allStats = coronaVirusDataServices.getAllStats();
		int totalReportedCases = allStats.stream().mapToInt(stat -> stat.getLatestTotalCases())
				.sum();
		int totalNewCases = allStats.stream().mapToInt(stat -> stat.getDifFromPrevDay())
				.sum();
		
		model.addAttribute("locationStats", allStats);
		model.addAttribute("totalReportedCases", totalReportedCases);
		model.addAttribute("getDifFromPrevDay", totalNewCases);
		
		
		return "home";
	}

}
