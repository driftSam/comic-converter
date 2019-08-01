package com.longbox.convert.service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.github.junrar.Archive;
import com.github.junrar.Junrar;
import com.github.junrar.exception.RarException;

@Component
public class ComicConverterService {

	@Autowired
	RabbitTemplate rabbitTemplate;
	private Archive archive;

	@Value("${out.dir}")
	String outputDir;

	public void convert(String comicPath) {
		Path rarComic = Paths.get(comicPath);
		System.out.println("Comic to convert: " + rarComic.toString());
		try {
			extract(rarComic);
		} catch (RarException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void extract(Path rarComic) throws RarException, IOException {
		Junrar.extract(rarComic.toFile(), Paths.get(outputDir).toFile());
	}
}
