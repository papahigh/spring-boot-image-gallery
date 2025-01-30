package gallery.api;

import org.springframework.boot.SpringApplication;

public class TestImageGalleryApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(ImageGalleryApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
