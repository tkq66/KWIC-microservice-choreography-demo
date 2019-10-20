package com.cvise.circular_shift.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cvise.circular_shift.service.CircularShiftService;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api/v1")
public class MainController {
	CircularShiftService service;

    public MainController(CircularShiftService service) {
        this.service = service;
    }
    
    @GetMapping(path = "/CircularShift/{id}",
				produces = {MediaType.APPLICATION_JSON_VALUE})
	public List<String> getKeywordsById(@PathVariable String id) {
		return this.service.getKeywordsById(id);
	}
    
    @GetMapping(path = "/CircularShift/File/{id}",
				produces = {MediaType.TEXT_PLAIN_VALUE})
	public String getLinkToKeywordsFile(@PathVariable String id, HttpServletRequest request) {
		String urlToFile = this.service.saveKeywordsToFileById(id, request);
		return urlToFile;
	}
    
    @PostMapping("/CircularShift/Input/File/{id}")
	public String performCircularShiftOnDataGenFromApi(
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
		String dataId = service.storeAllKeywordsInContextFromFileToDatabase(localFilePath);
		return dataId;
    }
}
