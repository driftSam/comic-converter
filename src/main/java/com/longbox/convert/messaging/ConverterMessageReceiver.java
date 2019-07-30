package com.longbox.convert.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConverterMessageReceiver {

	@Autowired
	ComicConverterService service;

	public void receiveMessage(String message) {
		System.out.println("Received <" + message + ">");

	}

}