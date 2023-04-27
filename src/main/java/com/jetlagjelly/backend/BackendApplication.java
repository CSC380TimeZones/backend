package com.jetlagjelly.backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();
		String MONGODB_USER = dotenv.get("MONGODB_USER");
		String MONGODB_PASSWORD = dotenv.get("MONGODB_PASSWORD");
		String MONGODB_LOCAL_PORT = dotenv.get("MONGODB_LOCAL_PORT");
		String MONGODB_HOSTNAME = dotenv.get("MONGODB_HOSTNAME");

		String DB_URL = "mongodb://" + MONGODB_USER + ":" + MONGODB_PASSWORD + "@" + MONGODB_HOSTNAME + ":"
				+ MONGODB_LOCAL_PORT + "/";
		System.out.println("Connecting to the database with the following URL: " + DB_URL);
		SpringApplication.run(BackendApplication.class, args);
	}

}
