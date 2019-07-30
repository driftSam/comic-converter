package com.longbox.convert.service;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ComicConverterService {

	@Autowired
	RabbitTemplate rabbitTemplate;

	public void convert(String comicPath) {
		Path rarComic = Paths.get(comicPath);
		System.out.println("Comic to convert: " + rarComic.toString());
	}
}
