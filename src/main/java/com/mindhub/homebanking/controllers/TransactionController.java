package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.TransactionType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@RestController()
@RequestMapping("api/")
public class TransactionController {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Transactional
    @PostMapping("/transactions")
    private ResponseEntity<Object> createTransaction(@RequestParam double amount,
                                                     @RequestParam String description,
                                                     @RequestParam String fromAccountNumber,
                                                     @RequestParam String toAccountNumber,
                                                     Authentication authentication){

        Client client = this.clientRepository.findByEmail(authentication.getName());
        Account accountOrigen = this.accountRepository.findByNumber(fromAccountNumber);
        Account accountDestino = this.accountRepository.findByNumber(toAccountNumber);

        //Que no hayan campos vacíos
        if (amount == 0 || description.isEmpty() || fromAccountNumber.isEmpty() || toAccountNumber.isEmpty()) {
            return new ResponseEntity<>("Faltan datos", HttpStatus.FORBIDDEN);
        }
        //Que las cuentas no sea iguales
        if (fromAccountNumber== toAccountNumber) {
            return new ResponseEntity<>("Cuenta de destino y origen iguales", HttpStatus.FORBIDDEN);
        }
        //Que exista la cuenta de origen
        if(accountOrigen == null){
            return new ResponseEntity<>("Cuenta origen no existe", HttpStatus.FORBIDDEN);
        }
        //Que exista la cuenta de destino
        if(accountDestino == null){
            return new ResponseEntity<>("Cuenta destino no existe", HttpStatus.FORBIDDEN);
        }
        //Que la cuenta de origen tenga saldo suficiente
        if(accountOrigen.getBalance() < amount){
            return new ResponseEntity<>("Saldo insuficiente", HttpStatus.FORBIDDEN);
        }
        //Que la cuenta de origen pertenezca al cliente autenticado
        boolean ctaCliente = false;
        for(Account cuentas :client.getAccounts()){
            if(cuentas.getNumber() == accountOrigen.getNumber())
            {
                ctaCliente= true;
            }
        }
        if (!ctaCliente){
            return new ResponseEntity<>("El cliente no tiene esa cuenta", HttpStatus.FORBIDDEN);
        }

        Transaction transactionDebito = new Transaction(TransactionType.debito, description, LocalDateTime.now(), amount*-1, accountOrigen);
        Transaction transactionCredito = new Transaction(TransactionType.credito, description, LocalDateTime.now(), amount, accountDestino);

        transactionRepository.save(transactionDebito);
        transactionRepository.save(transactionCredito);

        //Actualizar saldo
        accountOrigen.setBalance(accountOrigen.getBalance() - amount);
        accountDestino.setBalance(accountDestino.getBalance() + amount);
        accountRepository.save(accountOrigen);
        accountRepository.save(accountDestino);

        return new ResponseEntity<>("Transacción realizada con éxito", HttpStatus.CREATED);
    }
}
