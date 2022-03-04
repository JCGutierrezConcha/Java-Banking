package com.mindhub.homebanking;

//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication
//public class HomebankingApplication {

//	public static void main(String[] args) {
//		SpringApplication.run(HomebankingApplication.class, args);
//	}
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Account;

//}


import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.time.LocalDateTime;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Bean
	CommandLineRunner initialData(ClientRepository clientRepository, AccountRepository accountRepository){
		return ( args ) ->{
			Client cliente1 = new Client("Melba", "Morel", "melba@mindhub.com");
			Client cliente2 = new Client("Raquel", "Concha", "rconcha@mail.com");

			clientRepository.save(cliente1);
			clientRepository.save(cliente2);

			Account cuenta1 = new Account("JC12345", LocalDateTime.now(), 5000, cliente1);
			Account cuenta2 = new Account("JC23489", LocalDateTime.now().plusDays(1), 7500, cliente1);
			Account cuenta3 = new Account("JC12377", LocalDateTime.now(), 10000, cliente2);
			Account cuenta4 = new Account("JC23567", LocalDateTime.now().plusDays(1), 3500, cliente2);

			accountRepository.save(cuenta1);
			accountRepository.save(cuenta2);
			accountRepository.save(cuenta3);
			accountRepository.save(cuenta4);

		};
	}


}
