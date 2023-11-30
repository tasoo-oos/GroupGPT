package com.example.kingo_bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static com.example.kingo_bot.llm.LangChain.print;

@SpringBootApplication
public class KingoBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(KingoBotApplication.class, args);
		print();
	}
}
