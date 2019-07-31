package com.longbox.convert.service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;

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
		archive = new Archive(Files.newInputStream(rarComic));

		if (archive != null) {
			archive.getMainHeader().print();
			FileHeader fh = archive.nextFileHeader();

			while (fh != null) {
				File output = Paths.get(outputDir + fh.getFileNameString().trim()).toFile();
				System.out.println("output abs path: " + output.getAbsolutePath());
				OutputStream os = Files.newOutputStream(output.toPath());

				archive.extractFile(fh, os);
				fh = archive.nextFileHeader();
			}
		}

	}
}
