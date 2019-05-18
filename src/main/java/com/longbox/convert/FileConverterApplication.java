package com.longbox.convert;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.apache.tika.Tika;

@SpringBootApplication
public class FileConverterApplication implements CommandLineRunner {

	private static Logger LOG = LoggerFactory.getLogger(FileConverterApplication.class);

	@Value("${working.dir}")
	String dirName;

	public static void main(String[] args) {
		SpringApplication.run(FileConverterApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		LOG.info(dirName);
		Path workingDir = Paths.get(dirName);
		Tika tika = new Tika();
		
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(workingDir)) {
			for (Path entry : stream) {
			System.out.println(Files.probeContentType(entry));
			
			System.out.println("From Tika: " + tika.detect(entry));
			}

		}
	}

}
