package com.example.demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

	private ArrayList<File> listOfFiles = new ArrayList<>();

	@GetMapping("/create")
	public String createFile() throws IOException {
		
		FileOutputStream fop = null;
		File file=null;
		String content = "This is the text content";
		
		try {

			file = new File("/persistent/testfile.txt");
			fop = new FileOutputStream(file);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			// get the content in bytes
			byte[] contentInBytes = content.getBytes();

			fop.write(contentInBytes);
			fop.flush();
			fop.close();

			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fop != null) {
					fop.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return file.getAbsolutePath();

	}

	@GetMapping("/list")
	public List<String> getAllFiles() throws IOException {
		listOfFiles = new ArrayList<>();
		String location = "/persistent";

		System.out.println("Checking in: " + location);
		walk(location);

		List<String> fileNames = new ArrayList<>();

		for(File f: listOfFiles) {
			fileNames.add(f.getAbsoluteFile().toString());
		}
		return fileNames;
	}

	public void walk(String path) throws IOException {

		File root = new File( path );
		File[] list = root.listFiles();

		if (list == null) return;

		for ( File f : list ) {
			if ( f.isDirectory() ) {
				walk( f.getAbsolutePath() );
				System.out.println( "Dir:" + f.getAbsoluteFile() );
				listOfFiles.add(f.getAbsoluteFile());
			}
			else {
				System.out.println( "File:" + f.getAbsoluteFile() );
				listOfFiles.add(f.getAbsoluteFile());
			}
		}		
	}


	@RequestMapping(path = "/download/{file}", method = RequestMethod.GET)
	public ResponseEntity<Resource> download(@PathVariable("file") String file) throws IOException {

		file = "/persistent/" + file;

		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

		return ResponseEntity.ok()
				.contentLength(file.length())
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(resource);
	}

}
