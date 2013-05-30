package com.intimate.files;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

public class FileManager {
	protected Logger log = LoggerFactory.getLogger(getClass());
	
	private final String dataDirectory;

	public FileManager(String dataDirectory) {
		this.dataDirectory = dataDirectory;
		Path dataDir = Paths.get(dataDirectory);

		try {
			Files.createDirectories(dataDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean chunkExists(String ownerId, String identifier, Integer chunkNumber, Long chunkSize) throws IOException {
		Path chunkFile = Paths.get(dataDirectory, ownerId, identifier, chunkNumber.toString());
		
		log.debug("Checking file " + chunkFile.getFileName());
		
		if (Files.exists(chunkFile)) {
			long size = (Long) Files.getAttribute(chunkFile, "basic:size");
			return size == chunkSize;
		}
		return false;
	}

	public boolean isSupported(@SuppressWarnings("unused") String resumableFilename) {
		return true;
	}

	public void storeChunk(String ownerId, String identifier, Integer chunkNumber, InputStream inputStream) throws IOException {
		Path chunkFile = Paths.get(dataDirectory, ownerId, identifier, chunkNumber.toString());
		try {
			Files.createDirectories(chunkFile.getParent());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Files.copy(inputStream, chunkFile, StandardCopyOption.REPLACE_EXISTING);
	}

	public boolean allChunksUploaded(String ownerId, String identifier, Long chunkSize, Long totalSize) {
		long noOfChunks = totalSize / chunkSize;

		/*
		for (int chunkNo = 1; chunkNo <= noOfChunks; chunkNo++) {
			if (!Files.exists(Paths.get(dataDirectory, ownerId, identifier, String.valueOf(chunkNo)))) {
				return false;
			}
		}
		*/
		return true;

	}

	public void mergeAndDeleteChunks(String ownerId, String fileName, String identifier, Long chunkSize, final Long totalSize) throws IOException {
		long noOfChunks = totalSize / chunkSize;
		
		if (noOfChunks == 0 && totalSize > 0) {
			noOfChunks = 1;
		}

		Path newFilePath = Paths.get(dataDirectory, ownerId, fileName);
		if (Files.exists(newFilePath)) {
			Files.delete(newFilePath);
		}
		/*
		try (BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(newFilePath.toFile()))) {
			for (int chunkNo = 1; chunkNo <= noOfChunks; chunkNo++) {
				Path chunkPath = Paths.get(dataDirectory, ownerId, identifier, String.valueOf(chunkNo));
				long copied = Files.copy(chunkPath, bos);
				
				log.debug("Copied {} bytes", copied);
				if (copied > 0) {
					delete(chunkPath, 1000, 10);
				}
			}
		} 
		*/

		delete(Paths.get(dataDirectory, ownerId, identifier), 1000, 10);
	}
	
	@Async
	private void delete(Path path, long waitTime, int attempts) throws IOException {
		while (attempts > 0 && Files.exists(path)) {
			try {
				attempts--;
				Files.delete(path);
			} catch(IOException e) {
				log.warn("Entry {} cannot be deleted yet. Will try again later.", path);
			}
			
			try {
				Thread.sleep(waitTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (Files.exists(path) && Files.isDirectory(path)) {
			Files.walkFileTree(path, new FileVisitor<Path>() {
				@Override
				public FileVisitResult postVisitDirectory(Path dir,
						IOException e) throws IOException {
					if (e == null) {
		                 Files.delete(dir);
		                 return FileVisitResult.CONTINUE;
		             } else {
		                 // directory iteration failed
		                 throw e;
		             }
				}

				@Override
				public FileVisitResult preVisitDirectory(Path dir,
						BasicFileAttributes attrs) throws IOException {
					// TODO Auto-generated method stub
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file,
						BasicFileAttributes attrs) throws IOException {
					Files.delete(file);
		            return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file,
						IOException exc) throws IOException {
					// TODO Auto-generated method stub
					return FileVisitResult.CONTINUE;
				}
			});
		}
	}
	
	public void storeChunk(String ownerId, String identifier, Integer chunkNumber, byte[] content) throws IOException {
		Path chunkFile = Paths.get(dataDirectory, ownerId, identifier, chunkNumber.toString());
		try {
			Files.createDirectories(chunkFile.getParent());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Files.write(chunkFile, content);
	}
	
	public Collection<File> getFiles(String ownerId) {
		Path ownerDir = Paths.get(dataDirectory, ownerId);
		
		Collection<java.io.File> ioFiles = FileUtils.listFiles(ownerDir.toFile(), FileFilterUtils.trueFileFilter(), FileFilterUtils.falseFileFilter());
		Collection<File> files = new ArrayList<File>(); 
		for (java.io.File ioFile : ioFiles) {
			File file = new File();
			file.setName(ioFile.getName());
			
			files.add(file);
		}
		
		return files;
	}
}