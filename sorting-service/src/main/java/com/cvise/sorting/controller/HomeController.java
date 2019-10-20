package com.cvise.sorting.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cvise.sorting.service.SortingService;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api/v1")
public class HomeController {
	SortingService service;

    public HomeController(SortingService service) {
        this.service = service;
    }
    
    @GetMapping(path = "/Sorting/{id}",
			produces = {MediaType.APPLICATION_JSON_VALUE})
	public List<String> getSortedKeywordsById(@PathVariable String id) {
		return this.service.getSortedKeywordsById(id);
	}
	
	@GetMapping(path = "/Sorting/File/{id}",
			produces = {MediaType.TEXT_PLAIN_VALUE})
	public String saveSortedKeywordsToFileById(@PathVariable String id, HttpServletRequest request) {
		String urlToFile = this.service.saveSortedKeywordsToFileById(id, request);
		return urlToFile;
	}
	
	@PostMapping("/Sorting/CircularShift/File/{id}")
	public String performSortingOnDataGenFromApi(
			@RequestHeader(value="data-file-creator-api") String urlToGetDataFile,
			@RequestHeader(value="data-file-creator-api-method") String methodToGetDataFile,
			@RequestHeader(value="data-file-creator-api-content-type") String contentTypeToGetDataFile,
			@PathVariable String id) {        
		String contextualSlash = (urlToGetDataFile.charAt(urlToGetDataFile.length() - 1) == '/')
									? ""
									: "/";
		String apiUrlToGetDataFile = urlToGetDataFile + contextualSlash + id;
		String localFilePath = service.uploadFileFromApiRequest(apiUrlToGetDataFile,
										 						methodToGetDataFile,
										 						contentTypeToGetDataFile);
		String keywordsInContextFilePath = service.sortAllKeywordsInContextFromFileToFile(localFilePath);
		String dataId = service.storeAllSortedKeywordsInContextFromFileToDatabase(keywordsInContextFilePath);
		return dataId;
	}
}
