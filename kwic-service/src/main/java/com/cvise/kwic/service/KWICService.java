package com.cvise.kwic.service;

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
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.cvise.kwic.exception.FileStorageException;
import com.cvise.kwic.exception.HttpFileTransferFailed;

import reactor.core.publisher.Mono;

@Service
public class KWICService {
	@Value("${user.home}")
    private String localFilesReferenceDir;
	@Value("${api.url.base.input}")
    private String inputApiBaseUrl;
	@Value("${api.url.base.circular-shift}")
    private String circularShiftApiBaseUrl;
	@Value("${api.url.base.sorting}")
    private String sortingApiBaseUrl;
	@Value("${api.url.base.output}")
    private String outputApiBaseUrl;
    
    public KWICService() {}    
    
    /**Returns a String of item id of text content of a file that
     * had been uploaded to the local file system before uploaded
     * to the Input service and stored in a persistent storage, then
     * delete the local copy of the file.
     * 
     * @param file MultipartFile to be uploaded.
     * @return String of item id stored inside Input service's storage.
     */
    public String uploadFileToInputService(MultipartFile file) {
    	String inputApi = inputApiBaseUrl + "api/v1/Input/File";
    	String inputApiMethod = "POST";
    	String inputApiContent = "multipart/form-data";
    	String inpuApiResponseContent = "text/plain";
    	
    	Path localFilePath = this.uploadFile(file);
    	MultipartBodyBuilder builder = new MultipartBodyBuilder();
    	builder.part("file", new FileSystemResource(localFilePath));
    	MultiValueMap<String, HttpEntity<?>> multipartData = builder.build();
    	
    	String inputId = WebClient.create(inputApi)
    			.method(HttpMethod.resolve(inputApiMethod))
			    .accept(MediaType.parseMediaType(inpuApiResponseContent))
			    .contentType(MediaType.parseMediaType(inputApiContent))
			    .body(BodyInserters.fromMultipartData(multipartData))
			    .retrieve()
			    .bodyToMono(String.class)
    	 		.block();
    	try {
			Files.deleteIfExists(localFilePath);
			return inputId;
		} catch (IOException e) {
			e.printStackTrace();
			throw new FileStorageException("Could not clean up file "
	            	+ localFilePath.toString()
	                + ". Please try again!");
		}
    }
    
    /**Returns a String of item id of text that had been uploaded to
     * the Input service and stored in a persistent storage.
     * 
     * @param text String to be uploaded.
     * @return String of item id stored inside Input service's storage.
     */
    public String uploadTextToInputService(String text) {
    	String inputApi = inputApiBaseUrl + "api/v1/Input/Text";
    	String inputApiMethod = "POST";
    	String inputApiContent = "text/plain";
    	String inpuApiResponseContent = "text/plain";
    	
    	String inputId = WebClient.create(inputApi)
    			.method(HttpMethod.resolve(inputApiMethod))
			    .accept(MediaType.parseMediaType(inpuApiResponseContent))
			    .contentType(MediaType.parseMediaType(inputApiContent))
			    .body(BodyInserters.fromObject(text))
			    .retrieve()
			    .bodyToMono(String.class)
    	 		.block();
    	return inputId;
    }

    /**Returns a String of item id of list of String that had been uploaded
     * to the Input service and stored in a persistent storage.
     * 
     * @param text String to be uploaded.
     * @return String of item id stored inside Input service's storage.
     */
    public String uploadListToInputService(List<String> listInput) {
    	String inputApi = inputApiBaseUrl + "api/v1/Input/Text";
    	String inputApiMethod = "POST";
    	String inputApiContent = "application/json";
    	String inpuApiResponseContent = "text/plain";
    	
    	String inputId = WebClient.create(inputApi)
    			.method(HttpMethod.resolve(inputApiMethod))
			    .accept(MediaType.parseMediaType(inpuApiResponseContent))
			    .contentType(MediaType.parseMediaType(inputApiContent))
			    .body(BodyInserters.fromObject(listInput))
			    .retrieve()
			    .bodyToMono(String.class)
    	 		.block();
    	return inputId;
    }

    
    /**Returns a String of item id of circular shifted text from file 
     * from a location had been given by Input service's file creator API. 
     * 
     * @param inputId String of item id from Input service's database.
     * @return String of circular shifted text for all keywords.
     */
    public String performCircularShiftByInputId(String inputId) {
    	String circularShiftApi = circularShiftApiBaseUrl + "api/v1/CircularShift/Input/File/" + inputId;
    	String circularShiftApiMethod = "POST";
    	String[][] circularShiftHeaders = new String[][] {
    		{ "data-file-creator-api", inputApiBaseUrl + "api/v1/Input/File/" },
			{ "data-file-creator-api-content-type", "text/plain" },
			{ "data-file-creator-api-method", "GET" }
    	};
        String keywordsId = WebClient.create(circularShiftApi)
    			.method(HttpMethod.resolve(circularShiftApiMethod))
    			.header(circularShiftHeaders[0][0], circularShiftHeaders[0][1])
    			.header(circularShiftHeaders[1][0], circularShiftHeaders[1][1])
    			.header(circularShiftHeaders[2][0], circularShiftHeaders[2][1])
			    .retrieve()
			    .bodyToMono(String.class)
    	 		.block();
       return keywordsId;
    }
    
    /**Returns a String of item id of sorted text from file from a location
     * that had been given by CircularShift service's file creator API.
     * 
     * @param keywordsId String of item id from CircularShift service's database.
     * @return String of sorted keywords.
     */
    public String performSortingByKeywordsId(String keywordsId) {
    	String sortingApi = sortingApiBaseUrl + "api/v1/Sorting/CircularShift/File/" + keywordsId;
  	  	String sortingApiMethod = "POST";
  	  	String[][] sortingHeaders = new String[][] {
  	  		{ "data-file-creator-api", circularShiftApiBaseUrl + "api/v1/CircularShift/File/" },
  	  		{ "data-file-creator-api-content-type", "text/plain" },
  	  		{ "data-file-creator-api-method", "GET" }
  	  	};
  	  	String sortedKeywordsId = WebClient.create(sortingApi)
  	  		.method(HttpMethod.resolve(sortingApiMethod))
  	  		.header(sortingHeaders [0][0], sortingHeaders [0][1])
  	  		.header(sortingHeaders [1][0], sortingHeaders [1][1])
  	  		.header(sortingHeaders [2][0], sortingHeaders [2][1])
  	  		.retrieve()
  	  		.bodyToMono(String.class)
  	  		.block(); 
  	  	return sortedKeywordsId;
    }
    
    /**Returns a String of sorted keywords in context from file from a location
     * that had been given by Sorting service's file creator API.
     * 
     * @param sortedKeywordsId String of item id from Sorting service's database.
     * @return String of keywords in context.
     */
    public String getOutputTextBySortedKeywordsId(String sortedKeywordsId) {
    	String outputApi = outputApiBaseUrl + "api/v1/Output/Sorting/Text/" + sortedKeywordsId;
	  	String outputApiMethod = "GET";
	  	String[][] outputHeaders = new String[][] {
	  		{ "data-file-creator-api", sortingApiBaseUrl + "api/v1/Sorting/File/" },
			{ "data-file-creator-api-content-type", "text/plain" },
			{ "data-file-creator-api-method", "GET" }
	  	};
	  	String KWICOutput = WebClient.create(outputApi)
	  		.method(HttpMethod.resolve(outputApiMethod))
	  		.header(outputHeaders [0][0], outputHeaders [0][1])
	  		.header(outputHeaders [1][0], outputHeaders [1][1])
	  		.header(outputHeaders [2][0], outputHeaders [2][1])
			.retrieve()
			.bodyToMono(String.class)
	  	 	.block();
	  	return KWICOutput;
    }
    
    /**Returns a List of String of sorted keywords in context from file from a location
    * that had been given by Sorting service's file creator API.
    * 
    * @param sortedKeywordsId String of item id from Sorting service's database.
    * @return List of String of keywords in context.
    */
   public List<String> getOutputLisBySortedKeywordsId(String sortedKeywordsId) {
   	String outputApi = outputApiBaseUrl + "api/v1/Output/Sorting/Text/" + sortedKeywordsId;
	  	String outputApiMethod = "GET";
	  	String[][] outputHeaders = new String[][] {
	  		{ "data-file-creator-api", sortingApiBaseUrl + "api/v1/Sorting/File/" },
			{ "data-file-creator-api-content-type", "text/plain" },
			{ "data-file-creator-api-method", "GET" }
	  	};
	  	List<String> KWICOutput = WebClient.create(outputApi)
	  		.method(HttpMethod.resolve(outputApiMethod))
	  		.header(outputHeaders [0][0], outputHeaders [0][1])
	  		.header(outputHeaders [1][0], outputHeaders [1][1])
	  		.header(outputHeaders [2][0], outputHeaders [2][1])
			.retrieve()
			.bodyToFlux(String.class)
			.collectList()
			.block();
	  	return KWICOutput;
   }
    
    /**Returns a Path to local sorted keywords in context file from location
     * that had been given by Sorting service's file creator API that had been
     * transferred copied to the local file system,
     * 
     * @param sortedKeywordsId String of item id from Sorting service's database.
     * @return Path of local keywords in context file.
     */
    public Path getLocalCopyOfOutputFile(String sortedKeywordsId) {
    	String outputApi = outputApiBaseUrl + "api/v1/Output/Sorting/File/" + sortedKeywordsId;
	  	String outputApiMethod = "GET";
	  	String[][] outputHeaders = new String[][] {
	  		{ "data-file-creator-api", sortingApiBaseUrl + "api/v1/Sorting/File/" },
			{ "data-file-creator-api-content-type", "text/plain" },
			{ "data-file-creator-api-method", "GET" }
	  	};
	  	String fileLocation = WebClient.create(outputApi)
	  		.method(HttpMethod.resolve(outputApiMethod))
	  		.header(outputHeaders [0][0], outputHeaders [0][1])
	  		.header(outputHeaders [1][0], outputHeaders [1][1])
	  		.header(outputHeaders [2][0], outputHeaders [2][1])
			.retrieve()
			.bodyToMono(String.class)
	  	 	.block();
	  	String localFileLocation = uploadFileFromLink(fileLocation);
	  	return Paths.get(localFileLocation);
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
    
    /**Returns a Mono stream from a local file, streaming contents of the local file
     * through the Mono API. Returning the result from the controller will result
     * in a file download.
     * 
     * @param localFilePath Path to the local file to stream.
     * @return Mono stream of ResponseEntity of file InputStream to download file.
     */
    public Mono<ResponseEntity<InputStreamResource>> createDownloadStreamObjectFromPath(Path localFilePath) {
    	return Mono.defer(() -> {
			try {
				return Mono.just(Files.newInputStream(localFilePath));
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}})
				   .map(it -> ResponseEntity.ok()
						   					.header(HttpHeaders.CONTENT_DISPOSITION,
						   							"attachment; filename=\""
						   							+ localFilePath.getFileName() + "\"")
						   					.body(new InputStreamResource(it)));
    }
}
