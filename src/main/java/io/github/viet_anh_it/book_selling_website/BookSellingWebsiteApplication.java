package io.github.viet_anh_it.book_selling_website;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class BookSellingWebsiteApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookSellingWebsiteApplication.class, args);
	}

}
