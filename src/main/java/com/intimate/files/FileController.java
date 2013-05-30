package com.intimate.files;


import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@RooWebJson(jsonObject = File.class)
@Controller
@RequestMapping("/files")
public class FileController {
	protected Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private FileManager fileManager;

	@RequestMapping(value = "/upload/type/resumable", method = RequestMethod.GET)
	public void resumableChunkExists(
			HttpServletResponse response,
			@RequestParam("resumableChunkNumber") final Integer chunkNumber,
			@RequestParam("resumableChunkSize") final Long chunkSize,
			@RequestParam("resumableIdentifier") final String identifier,
			@SuppressWarnings("unused") @RequestParam("resumableFilename") final String filename,
			@RequestParam("ownerId") final String ownerId)
			throws IOException {

		if (fileManager.chunkExists(ownerId, identifier, chunkNumber, chunkSize)) {
			// do not upload chunk again
			response.setStatus(200);
		} else {
			// chunk not on the server, upload it
			response.setStatus(404);
		}
	}

	@RequestMapping(value = "/upload/type/resumable", method = RequestMethod.POST, consumes = "multipart/form-data")
	public void processResumableUpload(HttpServletResponse response,
			@RequestParam(value = "ownerId") final String ownerId,
			@RequestParam(value = "resumableChunkNumber") final Integer chunkNumber,
			@RequestParam(value = "resumableChunkSize") final Long chunkSize,
			@RequestParam(value = "resumableTotalSize") final Long totalSize,
			@RequestParam(value = "resumableIdentifier") final String identifier,
			@RequestParam(value = "resumableFilename") final String fileName,
			@RequestParam(value = "file") final MultipartFile file)
			throws IOException {
		
		if (!fileManager.isSupported(fileName)) {
			// cancel the whole upload
			response.setStatus(501);
			return;
		}

		/*
		try (InputStream is = file.getInputStream()) {
			fileManager.storeChunk(ownerId, identifier, chunkNumber, is);
		} catch (Exception e) {
			log.warn("Error writing a chunk of the file.", e);
			response.setStatus(HttpStatus.CONFLICT.value());
			return;
		}

		if (fileManager.allChunksUploaded(ownerId, identifier, chunkSize, totalSize)) {
			fileManager.mergeAndDeleteChunks(ownerId, fileName, identifier, chunkSize, totalSize);
		}
		*/

		response.setStatus(200);
	}

	@RequestMapping(value = "/upload/type/content", method = RequestMethod.PUT, consumes = "*/*")
	public void processContentUpload(HttpServletResponse response,
			@RequestParam(value = "ownerId") final String ownerId,
			@RequestParam(value = "fileName") final String fileName,
			@RequestHeader(value = "Content-Range", required = false) String contentRange,
			@RequestHeader(value = "Content-Length", required = false) String contentLength,
			@RequestHeader(value = "Content-Type", required = false) String mimetype,
			@RequestParam(value = "identifier") final String identifier,
			
			@RequestParam(value = "chunkNumber", required = false) Integer chunkNumber,
			@RequestParam(value = "chunkSize", required = false) Long chunkSize,
			@RequestParam(value = "totalSize", required = false) Long totalSize,
			byte[] content) throws IOException {
		
		log.debug("Processing content Upload filename {}", fileName);
		log.debug("Storing content rage of {}", contentRange);
		
		if (!fileManager.isSupported(fileName)) {
			// cancel the whole upload
			response.setStatus(501);
			return;
		}
		
		if (chunkNumber == null) {
			if (chunkSize == null) {
				if (!StringUtils.hasLength(contentLength)) {
					log.error("No chunk size or content-length in the request");
					response.setStatus(HttpStatus.LENGTH_REQUIRED.value());
					return;
				}
				
				chunkSize = Long.valueOf(contentLength);
			}
			
			if(!StringUtils.hasLength(contentRange)) {
	            contentRange="bytes 0-" + content.length + "/" + content.length;
	        }
			
	        ContentRangeHeader r = new ContentRangeHeader(contentRange);
	        log.debug("Parsed contentrange: {}", r);
	        
	        if (totalSize == null) {
	        	totalSize = r.getTotalSize();
	        }
	        
	        chunkNumber = Math.round((r.getRangeStart() + 1)/chunkSize);
	        
	        log.debug("Chunk number is: {}, ({})", chunkNumber, (r.getRangeStart() + 1)/chunkSize);
		}
		
		try {
			fileManager.storeChunk(ownerId, identifier, chunkNumber, content);
		} catch (Exception e) {
			log.warn("Error writing a chunk of the file.", e);
			response.setStatus(HttpStatus.CONFLICT.value());
			return;
		}

		if (fileManager.allChunksUploaded(ownerId, identifier, chunkSize, totalSize)) {
			fileManager.mergeAndDeleteChunks(ownerId, fileName, identifier, chunkSize, totalSize);
		}

		response.setStatus(200);		
		
		response.setStatus(HttpStatus.ACCEPTED.value());
	}
	
    @RequestMapping(headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> listJson(@RequestParam(value = "ownerId") final String ownerId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        Collection<File> result = fileManager.getFiles(ownerId);
        return new ResponseEntity<String>(File.toJsonArray(result), headers, HttpStatus.OK);
    }
	
}