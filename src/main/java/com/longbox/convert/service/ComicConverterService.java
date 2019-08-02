package com.longbox.convert.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FilenameUtils;
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

	@Value("${temp.dir}")
	String tempDirName;

	@Value("${zip.dir}")
	String zipDirName;

	public void convert(String comic) {
		Path rarComicPath = Paths.get(comic);
		Path tempDirPath = Paths.get(tempDirName);
		Path zipDirPath = Paths.get(zipDirName);
		Path zipDirPathFinal = Paths.get(zipDirPath.toString() + "/"
				+ FilenameUtils.getBaseName(rarComicPath.getFileName().toString()) + ".cbz");
		System.out.println("Comic to convert: " + rarComicPath.toString());
		try {
			List<Path> contents = extract(rarComicPath, tempDirPath);
			zip(contents, zipDirPathFinal);

		} catch (RarException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private List<Path> extract(Path rarComic, Path outputPath) throws RarException, IOException {
		List<Path> paths = new ArrayList<Path>();
		List<File> files = Junrar.extract(rarComic.toFile(), outputPath.toFile());

		for (File file : files) {
			paths.add(file.toPath());
		}
		return paths;
	}

	private void zip(List<Path> contents, Path zipDirPathFinal) throws IOException {
		try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipDirPathFinal))) {
			contents.forEach(path -> {
				ZipEntry zipEntry = new ZipEntry(path.toString());
				try {
					zos.putNextEntry(zipEntry);
					zos.write(Files.readAllBytes(path));
					zos.closeEntry();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			});
		}
	}
}
