package com.cvise.input.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cvise.input.entity.Input;
import com.cvise.input.exception.FileReadException;
import com.cvise.input.exception.FileStorageException;
import com.cvise.input.exception.NotFoundException;
import com.cvise.input.repository.InputRepository;
import com.cvise.input.repository.InputRepositoryCustomImpl;

@Service
public class InputService {
	@Value("${user.home}")
    private String localFilesReferenceDir;
    @Value("${user.local.static.file.path}")
	private String staticFilePath;
	@Value("${user.local.static.file.endpoint}")
	private String staticFileEndpoint;
    
    private InputRepository repository;
    private InputRepositoryCustomImpl customRepository;
    public InputService(InputRepository inputRepository,
    					InputRepositoryCustomImpl customInputRepository) {
        this.repository = inputRepository;
       	this.customRepository = customInputRepository;
    }
    
    /**Return list of string of Input corpus by id.
     * 
     * @param id String of Input id.
     * @return List of string of text in the corpus.
     */
    public List<String> getInputById(String id) {
    	Input input = repository.findById(id).orElseThrow(NotFoundException::new);
    	return input.getCorpus();
    }
    
    /**Return URL to the static file of corpus content of Input with a certain id.
     * 
     * @param id String of Input id to get corpus data from.
     * @param request HttpServletRequest to construct the base URL from.
     * @return String of URL to the static file location. 
     */
    public String saveInputToFileById(String id, HttpServletRequest request) {
    	String baseUrl = getStaticFilesUrlFromRequest(request);
    	List<String> inputCorpus = this.getInputById(id);
    	String fileName = UUID.randomUUID().toString() + ".txt";
		String filePath = staticFilePath + fileName;
    	Path fileLocation = Paths.get(filePath);
    	try {
	    	Files.write(fileLocation, inputCorpus);
	    	return baseUrl + fileName;
    	} catch (Exception e) {
            e.printStackTrace();
            throw new FileStorageException(
            	"Could not prepare file of input id " + id 
            	+ " for download at " + fileLocation.getFileName()
                + ". Please try again!");
        }
    }
	
	/**Transfer text line by line from a String into the corpus database.
	 * 
	 * @param text String to read input from.
	 * @return String of the element id in the database if transfer is successful.
	 */
	public String saveTextToDatabase(String text) {
		Input newInput = repository.save(new Input(new ArrayList<String>()));
		String inputId = newInput.getId();
		List<String> lines = Arrays.asList(text.split("\\r?\\n"));
		for (String line : lines) {
			customRepository.pushUniqueCorpusById(inputId, line);
		}
		return inputId;
	}
	
	/**Transfer elements from a list into the corpus database.
	 * 
	 * @param textList List of String to read input from.
	 * @return String of the element id in the database if transfer is successful.
	 */
	public String saveTextListToDatabase(List<String> textList) {
		System.out.println(textList);
		Input newInput = repository.save(new Input(new ArrayList<String>()));
		String inputId = newInput.getId();
		for (String line : textList) {
			customRepository.pushUniqueCorpusById(inputId, line);
		}
		return inputId;
	}
	
	/**Transfer text file content line by line into the corpus database
	 * then remove the temp file.
	 * 
	 * @param localFilePath Path to temp file to read input from.
	 * @return String of the element id in the database if transfer is successful.
	 */
	public String saveFileToDatabase(Path localFilePath) {
		try (Stream<String> stream = Files.lines(localFilePath)) {
			Input newInput = repository.save(new Input(new ArrayList<String>()));
			String inputId = newInput.getId();
			stream.forEach((line -> { customRepository.pushUniqueCorpusById(inputId, line); }));
			Files.deleteIfExists(localFilePath);
			return inputId;
		} catch (IOException e) {
			e.printStackTrace();
			throw new FileReadException("Could not store file "
				+ localFilePath.getFileName()
                + " into database. Please try again!");
		}
	}
	
	/**Uploads file to a default local location.
	 * 
	 * @param file MultipartFile object that was passed to the controller.
	 * @return Path to the local location the file is downloaded to. 
	 */
	public Path uploadFile(MultipartFile file) {
		try {
			String fileName = UUID.randomUUID().toString() + ".txt";
			String filePath = localFilesReferenceDir + File.separator + fileName;
        	Path copyLocation = Paths.get(filePath);
        	Files.copy(file.getInputStream(),
        			   copyLocation,
        			   StandardCopyOption.REPLACE_EXISTING);		 
        	return copyLocation;    
        } catch (Exception e) {
            e.printStackTrace();
            throw new FileStorageException("Could not store file "
            	+ file.getOriginalFilename()
                + ". Please try again!");
        }
	}
	
	/**Generate URL string to the location to static files being hosted.
	 * 
	 * @param request HttpServletRequest from the controller to extract info from.
	 * @return String of URL pointing to static files location.
	 */
	public String getStaticFilesUrlFromRequest(HttpServletRequest request) {
		return String.format("%s://%s:%d%s",
			request.getScheme(),
			request.getServerName(),
			request.getServerPort(),
			staticFileEndpoint);
	}
}
