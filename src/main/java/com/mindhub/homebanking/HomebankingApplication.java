package com.mindhub.homebanking;


import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.time.LocalDateTime;
import java.util.Arrays;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Bean
	CommandLineRunner initialData(ClientRepository clientRepository,
								  AccountRepository accountRepository,
								  TransactionRepository transactionRepository,
								  LoanRepository loanRepository,
		                          ClientLoanRepository clientloanRepository,
								  CardRepository cardRepository) {
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

			Transaction transaction1 = new Transaction(TransactionType.credito, "Transaccion 1", LocalDateTime.now(), 1200, cuenta1);
			Transaction transaction2 = new Transaction(TransactionType.credito, "Transaccion 2", LocalDateTime.now(), 1500, cuenta1);
			Transaction transaction3 = new Transaction(TransactionType.debito, "Transaccion 3", LocalDateTime.now(), -2000, cuenta1);
			Transaction transaction4 = new Transaction(TransactionType.debito, "Transaccion 1", LocalDateTime.now().plusDays(1), -3300, cuenta2);
            Transaction transaction5 = new Transaction(TransactionType.credito, "Transaccion 2", LocalDateTime.now().plusDays(1), 12500, cuenta2);
            Transaction transaction6 = new Transaction(TransactionType.debito, "Transaccion 3", LocalDateTime.now().plusDays(1), -78000, cuenta2);

            transactionRepository.save(transaction1);
			transactionRepository.save(transaction2);
			transactionRepository.save(transaction3);
			transactionRepository.save(transaction4);
            transactionRepository.save(transaction5);
            transactionRepository.save(transaction6);

			Loan loan1= new Loan("Hipotecario", 500000, Arrays.asList(12,24,36,48,60));
			Loan loan2= new Loan("Personal", 100000, Arrays.asList(6,12,24));
			Loan loan3= new Loan("Automotriz", 300000, Arrays.asList(6,12,24,36));

			loanRepository.save(loan1);
			loanRepository.save(loan2);
			loanRepository.save(loan3);

			ClientLoan clientloan1 = new ClientLoan(400000, 60, cliente1, loan1);
			ClientLoan clientloan2 = new ClientLoan(50000, 12, cliente1, loan2);
			ClientLoan clientloan3 = new ClientLoan(100000, 24, cliente2, loan2);
			ClientLoan clientloan4 = new ClientLoan(20000, 36, cliente2, loan3);

			clientloanRepository.save(clientloan1);
			clientloanRepository.save(clientloan2);
			clientloanRepository.save(clientloan3);
			clientloanRepository.save(clientloan4);

			Card card1 = new Card(cliente1.getFirstName() + " " + cliente1.getLastName(), CardType.DEBIT, CardColor.GOLD, "2771 1418 2534 1415", 322, LocalDateTime.now(), LocalDateTime.now().plusYears(5), cliente1);
			Card card2 = new Card(cliente1.getFirstName() + " " + cliente1.getLastName(), CardType.CREDIT, CardColor.TITANIUM, "2233 6578 1478 3122", 785, LocalDateTime.now(), LocalDateTime.now().plusYears(5), cliente1);
			Card card3 = new Card(cliente2.getFirstName() + " " + cliente2.getLastName(), CardType.CREDIT, CardColor.SILVER, "7766 2455 2732 2322", 691, LocalDateTime.now(), LocalDateTime.now().plusYears(5), cliente2);

			cardRepository.save(card1);
			cardRepository.save(card2);
			cardRepository.save(card3);

		};
	}
}
