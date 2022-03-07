package com.mindhub.homebanking;


import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.time.LocalDateTime;

import static com.mindhub.homebanking.models.TransactionType.credito;
import static com.mindhub.homebanking.models.TransactionType.debito;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Bean
	CommandLineRunner initialData(ClientRepository clientRepository, AccountRepository accountRepository,
								  TransactionRepository transactionRepository){
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

			Transaction transaction1 = new Transaction(credito, "Transaccion 1", LocalDateTime.now(), 1200, cuenta1);
			Transaction transaction2 = new Transaction(credito, "Transaccion 2", LocalDateTime.now(), 1500, cuenta1);
			Transaction transaction3 = new Transaction(debito, "Transaccion 3", LocalDateTime.now(), -2000, cuenta1);
			Transaction transaction4 = new Transaction(debito, "Transaccion 1", LocalDateTime.now().plusDays(1), -3300, cuenta2);
            Transaction transaction5 = new Transaction(credito, "Transaccion 2", LocalDateTime.now().plusDays(1), 12500, cuenta2);
            Transaction transaction6 = new Transaction(debito, "Transaccion 3", LocalDateTime.now().plusDays(1), -78000, cuenta2);

            transactionRepository.save(transaction1);
			transactionRepository.save(transaction2);
			transactionRepository.save(transaction3);
			transactionRepository.save(transaction4);
            transactionRepository.save(transaction5);
            transactionRepository.save(transaction6);
		};
	}
}
