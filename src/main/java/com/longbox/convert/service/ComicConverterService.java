package com.longbox.convert.service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.github.junrar.Junrar;
import com.github.junrar.exception.RarException;

@Component
public class ComicConverterService {

	@Autowired
	RabbitTemplate rabbitTemplate;

	@Value("${out.dir}")
	String outputDirName;

	public void convert(String comic) {
		Path rarComicPath = Paths.get(comic);
		Path outputDirPath = Paths.get(outputDirName);
		System.out.println("Comic to convert: " + rarComicPath.toString());
		try {
			extract(rarComicPath, outputDirPath);
		} catch (RarException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void extract(Path rarComic, Path outputPath) throws RarException, IOException {
		Junrar.extract(rarComic.toFile(), outputPath.toFile());
	}
}
