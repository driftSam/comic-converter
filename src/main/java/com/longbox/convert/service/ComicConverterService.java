package com.longbox.convert.service;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
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

	@Value("${trash.dir}")
	String trashDirName;

	public void convert(String comic) {
		Path rarComicPath = Paths.get(comic);
		Path tempDirPath = Paths.get(tempDirName);
		Path zipDirPath = Paths.get(zipDirName);
		Path zipDirPathWithName = Paths.get(zipDirPath.toString() + "/"
				+ FilenameUtils.getBaseName(rarComicPath.getFileName().toString()) + ".cbz");
		System.out.println("Comic to convert: " + rarComicPath.toString());
		try {
			List<Path> contents = extract(rarComicPath, tempDirPath);
			zip(contents, zipDirPathWithName);
			Files.move(rarComicPath, Paths.get(trashDirName + rarComicPath.getFileName()));
			cleanupTemp(tempDirPath);

		} catch (RarException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private List<Path> extract(Path rarComic, Path outputPath) throws RarException, IOException {
		List<Path> paths = new ArrayList<Path>();
		List<File> files = Junrar.extract(rarComic.toFile(), outputPath.toFile());

		for (File file : files) {
			if (!file.isDirectory()) {
				RandomAccessFile raf = new RandomAccessFile(file, "rw");
				FileChannel channel = raf.getChannel();
				channel.lock();
				raf.close();
				paths.add(file.toPath());
			}

		}
		return paths;
	}

	private void zip(List<Path> contents, Path zipDirPathFinal) throws IOException {
		try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipDirPathFinal))) {
			contents.forEach(path -> {
				Path sourceDir = path.getParent();
				ZipEntry zipEntry = new ZipEntry(sourceDir.relativize(path).toString());
				try {
					zos.putNextEntry(zipEntry);
					zos.write(Files.readAllBytes(path));
					zos.closeEntry();
					Files.delete(path);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			});
		}
	}

	private void cleanupTemp(Path tempDirPath) throws IOException {
		Files.walk(tempDirPath).forEach(path -> {
			if (Files.isDirectory(path)) {
				System.out.println(path.toString());
				try {
					if (!Files.newDirectoryStream(path).iterator().hasNext())
						Files.delete(path);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
}
