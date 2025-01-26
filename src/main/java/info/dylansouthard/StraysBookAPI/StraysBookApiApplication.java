package info.dylansouthard.StraysBookAPI;

import info.dylansouthard.StraysBookAPI.model.SimpleUser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StraysBookApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(StraysBookApiApplication.class, args);
		SimpleUser user = new SimpleUser();
		user.setUsername("username");
		String name = user.getUsername();
	}

}
