package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;


@RestController()
@RequestMapping("api/")
public class LoanController {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ClientLoanRepository clientLoanRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping("/loans")
    public List<LoanDTO> getLoans(){
        return loanRepository.findAll().stream().map(LoanDTO::new).collect(toList());
    }

    @Transactional
    @PostMapping("/loans")
    public ResponseEntity<Object> createLoan(@RequestBody LoanApplicationDTO loanApp, Authentication authentication) {

        Loan prestamoOfertado = loanRepository.findById(loanApp.getLoanId());
        Client client = this.clientRepository.findByEmail(authentication.getName());
        Account account = this.accountRepository.findByNumber(loanApp.getToAccountNumber());

        if (prestamoOfertado == null) {
            return new ResponseEntity<>("Este tipo de préstamo no existe", HttpStatus.FORBIDDEN);
        }

        if (loanApp.getToAccountNumber().isEmpty()) {
            return new ResponseEntity<>("Ingrese una cuenta de destino", HttpStatus.FORBIDDEN);
        }

        if (loanApp.getLoanId() == 0) {
            return new ResponseEntity<>("Debe escoger un tipo de prestamo", HttpStatus.FORBIDDEN);
        }

        if (loanApp.getAmount() == 0) {
            return new ResponseEntity<>("Debe ingresar un monto", HttpStatus.FORBIDDEN);
        }

        if (loanApp.getPayments() == 0) {
            return new ResponseEntity<>("Debe escoger una cantidad de cuotas", HttpStatus.FORBIDDEN);
        }

        //El monto solicitado es mas grande que el monto máximo
        if (loanApp.getAmount() > prestamoOfertado.getMaxAmount()) {
            return new ResponseEntity<>("El monto máximo de este préstamo es: " + prestamoOfertado.getMaxAmount(), HttpStatus.FORBIDDEN);
        }

        //Verificar la cantidad de cuotas se encuentre disponible entre las del préstamo
        if (!prestamoOfertado.getPayments().contains(loanApp.getPayments())) {
            return new ResponseEntity<>("Debe escoger una cuota válida: ", HttpStatus.FORBIDDEN);
        }

        //Verificar que la cuenta de destino exista
        if (accountRepository.findByNumber(loanApp.getToAccountNumber()) == null) {
            return new ResponseEntity<>("La cuenta de destino no existe: ", HttpStatus.FORBIDDEN);
        }

        //Verificar que la cuenta de destino pertenezca al cliente autenticado
        if (!client.getAccounts().contains(account)) {
            return new ResponseEntity<>("La cuenta no pertenece al cliente: " + client.getFirstName() + " " + client.getLastName(), HttpStatus.FORBIDDEN);
        }

        ClientLoan clientLoan = new ClientLoan(loanApp.getAmount()*1.2, loanApp.getPayments(), client, prestamoOfertado);
        clientLoanRepository.save(clientLoan);

        Transaction transactionCredito = new Transaction(TransactionType.credito,"Préstamo " + prestamoOfertado.getName() , LocalDateTime.now(), loanApp.getAmount(), account);
        transactionRepository.save(transactionCredito);

        account.setBalance(account.getBalance() + loanApp.getAmount());
        accountRepository.save(account);

        return new ResponseEntity<>("Prestamo creado con éxito", HttpStatus.CREATED);
    }
}
