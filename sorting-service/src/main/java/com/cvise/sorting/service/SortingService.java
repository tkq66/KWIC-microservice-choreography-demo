package com.cvise.sorting.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.cvise.sorting.entity.SortedKeywordsInContext;
import com.cvise.sorting.exception.FileStorageException;
import com.cvise.sorting.exception.NotFoundException;
import com.cvise.sorting.exception.SortingFailed;
import com.cvise.sorting.repository.SortedKeywordsInContextRepository;
import com.cvise.sorting.repository.SortedKeywordsInContextRepositoryCustomImpl;
import com.google.code.externalsorting.ExternalSort;

import reactor.core.publisher.Mono;

import com.cvise.sorting.exception.HttpFileTransferFailed;
import com.cvise.sorting.exception.FileReadException;

@Service
public class SortingService {
	@Value("${user.home}")
    private String localFilesReferenceDir;
    @Value("${user.local.static.file.path}")
	private String staticFilePath;
	@Value("${user.local.static.file.endpoint}")
	private String staticFileEndpoint;
	
	private SortedKeywordsInContextRepository repository;
	private SortedKeywordsInContextRepositoryCustomImpl customRepository;
    public SortingService(SortedKeywordsInContextRepository repository,
    					  SortedKeywordsInContextRepositoryCustomImpl customRepository) {
    	this.repository = repository;
    	this.customRepository = customRepository;
    }
	
	/**Return list of string of sorted keywords in context by id.
     * 
     * @param id String of SortedKeywordsInContext id.
     * @return List of string of sorted keywords in context.
     */
    public List<String> getSortedKeywordsById(String id) {
    	SortedKeywordsInContext keywords = repository.findById(id).orElseThrow(NotFoundException::new);
    	return keywords.getKeywords();
    }
    
    /**Return URL to the static file of sorted keywords in context with a certain id.
     * 
     * @param id String of SortedKeywordsInContext id.
     * @param request HttpServletRequest to construct the base URL from.
     * @return String of URL to the static file location. 
     */
    public String saveSortedKeywordsToFileById(String id, HttpServletRequest request) {
    	String baseUrl = getStaticFilesUrlFromRequest(request);
    	List<String> keywordsList = this.getSortedKeywordsById(id);
    	String fileName = UUID.randomUUID().toString() + ".txt";
		String filePath = staticFilePath + fileName;
    	Path fileLocation = Paths.get(filePath);
    	try {
	    	Files.write(fileLocation, keywordsList);
	    	return baseUrl + fileName;
    	} catch (Exception e) {
            e.printStackTrace();
            throw new FileStorageException(
            	"Could not prepare file of input id " + id 
            	+ " for download at " + fileLocation.getFileName()
                + ". Please try again!");
        }
    }
    
    /**Sort keywords in context in a file then output the result to an output file.
     * 
     * @param path String of local file location of keywords in context.
     * @return String of local file location of sorted keywords in context.
     */
    public String sortAllKeywordsInContextFromFileToFile(String path) {
    	String outputFileName = UUID.randomUUID().toString() + ".txt";
		String outputFilePath = staticFilePath + outputFileName;
    	try {
			ExternalSort.mergeSortedFiles(ExternalSort.sortInBatch(new File(path)), new File(outputFilePath));
			return outputFilePath;
    	} catch (IOException e) {
			e.printStackTrace();
			throw new SortingFailed("Could not sort file "
					+ path
	                + " into file " + outputFilePath 
	                + " using com.google.code.externalsorting.ExternalSort. Please try again!");
		}
    }
    
    /**Store sorted keywords in context that was generated by the content of a text file
     * into a database, then remove the file.
     * 
     * @param path String of path to local file of text to perform sorting.
     * @return String of entity id for element that stored the sorted keywords.
     */
    public String storeAllSortedKeywordsInContextFromFileToDatabase(String path) {
    	Path localFilePath = Paths.get(path);
    	try (Stream<String> stream = Files.lines(localFilePath)) {
    		SortedKeywordsInContext newSortedKeywords = repository.save(new SortedKeywordsInContext(new ArrayList<String>()));
			String sortedKeywordsId = newSortedKeywords.getId();
			stream.forEach((line -> { customRepository.pushUniqueKeywordById(sortedKeywordsId, line); }));
			Files.deleteIfExists(localFilePath);
			return sortedKeywordsId;
		} catch (IOException e) {
			e.printStackTrace();
			throw new FileReadException("Could not store file "
				+ localFilePath.toString()
                + " as keywords in context into the database. Please try again!");
		}
    }
	
	/**Upload file from a URL location that was generated by
     * an API endpoint to a local location.
     * 
     * @param apiUrl String of API endpoint to make http request
     * 		  for the file location.
     * @param requestMethod String of REST verb to make the http
     * 		  request with.
     * @param contentType String of MIME of expected result from
     * 		  the http request. In this case, the MIME type should
     * 		  be "text/plain" since a String of URL of file location
     * 		  is expected from the API call.
     * @return String of path to local file 
     */
    public String uploadFileFromApiRequest(String apiUrl,
    									   String requestMethod,
    									   String contentType) {
    	String urlToDataFile = getStringFromHttpRequest(
    			apiUrl,
    			requestMethod,
    			contentType);
    	String pathToLocalFile = uploadFileFromLink(urlToDataFile);
    	return pathToLocalFile;
    }

    /**Make an http request to an URL that must respond with a String.
     * 
     * @param url String of API endpoint to make http request.
     * @param requestMethod String of REST verb to make the request with.
     * @param contentType String of MIME of expected return content type.
     * @return String response from the http request.
     */
    public String getStringFromHttpRequest(String url,
    									   String requestMethod,
    									   String contentType) {
    	Mono<String> result = WebClient.create(url)
			    .method(HttpMethod.resolve(requestMethod))
			    .accept(MediaType.parseMediaType(contentType))
			    .retrieve()
			    .bodyToMono(String.class);
    	return result.block();
    }
    
    /**Efficiently upload file from a given URL to a local location
     * by using the stream transfer function.  
     * 
     * @param url String of URL to data file.
     * @return String of path to local file 
     */
    public String uploadFileFromLink(String url) {
    	String localFileName = UUID.randomUUID().toString() + ".txt";
		String localFilePath = localFilesReferenceDir + File.separator + localFileName;
    	try {
    		// Connect to the external and local file location
    		InputStream conStream = new URL(url).openStream();
    		FileOutputStream fileOutputStream = new FileOutputStream(localFilePath);
    		
    		// Stream the file to local path
    		ReadableByteChannel readableByteChannel = Channels.newChannel(conStream);
			fileOutputStream.getChannel()
			  .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
			
			// Clean up
			fileOutputStream.close();
			
			// Return path if transfer happens properly
			return localFilePath;
		} catch (IOException e) {
			e.printStackTrace();
			throw new HttpFileTransferFailed(
	            	"Failed to download file from " + url
	            	+ " to " + localFilePath
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