package com.cvise.kwic.controller;

import java.nio.file.Path;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cvise.kwic.service.KWICService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/v1")
public class MainController {
	@Value("${user.home}")
    private String localFilesReferenceDir;
	
	KWICService service;
    public MainController(KWICService kwicService) {
    	this.service = kwicService;
    }
    
    @PostMapping(path = "/KWIC/Text/File",
			 	 consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
			 	 produces = {MediaType.TEXT_PLAIN_VALUE})
    public String getKeywordsInContextFromFile(@RequestBody MultipartFile input) {
    	String inputId = service.uploadFileToInputService(input);
    	String keywordsId = service.performCircularShiftByInputId(inputId);
    	String sortedKeywordsId = service.performSortingByKeywordsId(keywordsId);
    	String outputText = service.getOutputTextBySortedKeywordsId(sortedKeywordsId);
    	return outputText;
	}
    
    @PostMapping(path = "/KWIC/Text/Text",
		 	 	 consumes = {MediaType.TEXT_PLAIN_VALUE},
		 	 	 produces = {MediaType.TEXT_PLAIN_VALUE})
	public String getKeywordsInContextFromText(@RequestBody String input) {
		String inputId = service.uploadTextToInputService(input);
		String keywordsId = service.performCircularShiftByInputId(inputId);
		String sortedKeywordsId = service.performSortingByKeywordsId(keywordsId);
		String outputText = service.getOutputTextBySortedKeywordsId(sortedKeywordsId);
		return outputText;
	}
    
    @PostMapping(path = "/KWIC/Text/List",
		 	 	 consumes = {MediaType.APPLICATION_JSON_VALUE},
		 	 	 produces = {MediaType.TEXT_PLAIN_VALUE})
	public String getKeywordsInContextFromList(@RequestBody List<String> input) {
		String inputId = service.uploadListToInputService(input);
		String keywordsId = service.performCircularShiftByInputId(inputId);
		String sortedKeywordsId = service.performSortingByKeywordsId(keywordsId);
		String outputText = service.getOutputTextBySortedKeywordsId(sortedKeywordsId);
		return outputText;
    }
    
    @PostMapping(path = "/KWIC/List/File",
		 	 consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
		 	 produces = {MediaType.APPLICATION_JSON_VALUE})
	public List<String> getKeywordsInContextListFromFile(@RequestBody MultipartFile input) {
		String inputId = service.uploadFileToInputService(input);
		String keywordsId = service.performCircularShiftByInputId(inputId);
		String sortedKeywordsId = service.performSortingByKeywordsId(keywordsId);
		List<String> outputList = service.getOutputLisBySortedKeywordsId(sortedKeywordsId);
		return outputList;
	}
	
	@PostMapping(path = "/KWIC/List/Text",
		 	 	 consumes = {MediaType.TEXT_PLAIN_VALUE},
		 	 	 produces = {MediaType.APPLICATION_JSON_VALUE})
	public List<String> getKeywordsInContextListFromText(@RequestBody String input) {
		String inputId = service.uploadTextToInputService(input);
		String keywordsId = service.performCircularShiftByInputId(inputId);
		String sortedKeywordsId = service.performSortingByKeywordsId(keywordsId);
		List<String> outputList = service.getOutputLisBySortedKeywordsId(sortedKeywordsId);
		return outputList;
	}
	
	@PostMapping(path = "/KWIC/List/List",
		 	 	 consumes = {MediaType.APPLICATION_JSON_VALUE},
		 	 	 produces = {MediaType.APPLICATION_JSON_VALUE})
	public List<String> getKeywordsInContextListFromList(@RequestBody List<String> input) {
		String inputId = service.uploadListToInputService(input);
		String keywordsId = service.performCircularShiftByInputId(inputId);
		String sortedKeywordsId = service.performSortingByKeywordsId(keywordsId);
		List<String> outputList = service.getOutputLisBySortedKeywordsId(sortedKeywordsId);
		return outputList;
	}
    
    @PostMapping(path = "/KWIC/Download/File/",
    			consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
				produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public Mono<ResponseEntity<InputStreamResource>> getKeywordsInContextFileFromFile(@RequestBody MultipartFile input) {        
    	String inputId = service.uploadFileToInputService(input);
		String keywordsId = service.performCircularShiftByInputId(inputId);
		String sortedKeywordsId = service.performSortingByKeywordsId(keywordsId);
		Path localFilePath = service.getLocalCopyOfOutputFile(sortedKeywordsId);
		return service.createDownloadStreamObjectFromPath(localFilePath);
	}
    
	@PostMapping(path = "/KWIC/Download/Text",
		 	 	 consumes = {MediaType.TEXT_PLAIN_VALUE},
		 	 	 produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
	public Mono<ResponseEntity<InputStreamResource>> getKeywordsInContextFileFromText(@RequestBody String input) {
		String inputId = service.uploadTextToInputService(input);
		String keywordsId = service.performCircularShiftByInputId(inputId);
		String sortedKeywordsId = service.performSortingByKeywordsId(keywordsId);
		Path localFilePath = service.getLocalCopyOfOutputFile(sortedKeywordsId);
		return service.createDownloadStreamObjectFromPath(localFilePath);
	}
	
	@PostMapping(path = "/KWIC/Download/List",
		 	 consumes = {MediaType.APPLICATION_JSON_VALUE},
		 	 produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
	public Mono<ResponseEntity<InputStreamResource>> getKeywordsInContextFileFromList(@RequestBody List<String> input) {
		String inputId = service.uploadListToInputService(input);
		String keywordsId = service.performCircularShiftByInputId(inputId);
		String sortedKeywordsId = service.performSortingByKeywordsId(keywordsId);
		Path localFilePath = service.getLocalCopyOfOutputFile(sortedKeywordsId);
		return service.createDownloadStreamObjectFromPath(localFilePath);
	}
}
