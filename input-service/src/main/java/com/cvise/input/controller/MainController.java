package com.cvise.input.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cvise.input.service.InputService;

import java.nio.file.Path;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api/v1")
public class MainController {
	InputService service;

    public MainController(InputService service) {
        this.service = service;
    }
    
    @GetMapping(path = "/Input/{id}",
				produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<String> getInputById(@PathVariable String id) {
    	return this.service.getInputById(id);
    }
    
    @GetMapping(path = "/Input/File/{id}",
    			produces = {MediaType.TEXT_PLAIN_VALUE})
    public String getLinkToCorpusFile(@PathVariable String id, HttpServletRequest request) {
    	String urlToFile = this.service.saveInputToFileById(id, request);
    	return urlToFile;
    }

    @PostMapping(path = "/Input/File",
				 consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
				 produces = {MediaType.TEXT_PLAIN_VALUE})
    public String storeDataFromFile(@RequestParam("file") MultipartFile input) {
    	Path localFilePath = service.uploadFile(input);
		String storedDataLink = this.service.saveFileToDatabase(localFilePath);
        return storedDataLink;
    }
    
    @PostMapping(path = "/Input/Text",
				 consumes = {MediaType.TEXT_PLAIN_VALUE},
				 produces = {MediaType.TEXT_PLAIN_VALUE})
    public String storeDataFromText(@RequestBody String input) {
		String storedDataLink = this.service.saveTextToDatabase(input);
        return storedDataLink;
    }
    
    @PostMapping(path = "/Input/List",
				 consumes = {MediaType.APPLICATION_JSON_VALUE},
				 produces = {MediaType.TEXT_PLAIN_VALUE})
    public String storeDataFromTextList(@RequestBody List<String> input) {
		String storedDataLink = this.service.saveTextListToDatabase(input);
        return storedDataLink;
    }
}
