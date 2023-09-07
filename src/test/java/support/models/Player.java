package support.models;
import com.github.javafaker.Faker;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class Player {
	private String currencyCode;
	private String email;
	private String name;
	private String password;
	private String surname;
	private String username;

	private Faker faker = new Faker();

	public Player() {
		this.currencyCode = faker.currency().code();
		this.email = faker.internet().emailAddress();
		this.name = faker.name().firstName();
		this.password = faker.internet().password();
		this.surname = faker.name().lastName();
		this.username = faker.name().username();
	}

	@Override
	public String toString() {
		return this.currencyCode + "\n" + this.email + "\n" + this.name + "\n" + this.password + "\n" + this.surname + "\n" + this.username;
	}

	public Map toMap() {
		return Map.of(
				"currency_code", this.currencyCode,
				"email", this.email,
				"name", this.name,
				"password_change", this.password,
				"password_repeat", this.password,
				"surname", this.surname,
				"username", this.username
		);
	}

	public static void main(String[] args) {
		Player player = new Player();
		System.out.println(player.toMap());
	}
}
