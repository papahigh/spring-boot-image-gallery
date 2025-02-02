package io.github.papahigh;

import org.springframework.boot.SpringApplication;


public class TestImageGalleryApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(ImageGalleryService::main).with(TestcontainersConfiguration.class).run(args);
	}
}
