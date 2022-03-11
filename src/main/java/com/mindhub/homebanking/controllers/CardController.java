package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("api/")
public class CardController {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private ClientRepository clientRepository;

    @RequestMapping("/clients/currents/cards")
    public List<CardDTO> getCards(Authentication authentication){
        Client client = this.clientRepository.findByEmail(authentication.getName());
        return client.getCards().stream().map(CardDTO::new).collect(toList());
    }

    @PostMapping("/clients/current/cards")
    private ResponseEntity<Object> createCard(@RequestParam CardType cardType, @RequestParam CardColor cardColor,
                                              Authentication authentication){

        Client client = this.clientRepository.findByEmail(authentication.getName());

        int numberCards= cardRepository.countByClientAndType(client, cardType);
        if(numberCards >=3){
            return new ResponseEntity<>("Ha llegado al límite de tarjetas "+cardType +".", HttpStatus.FORBIDDEN);
        }
        String cardNumber = (int) ((Math.random() * (9999-1000))+1000) + "-" + (int) ((Math.random() * (9999-1000))+1000) + "-" + (int) ((Math.random() * (9999-1000))+1000) + "-" +(int) ((Math.random() * (9999-1000))+1000);
        int cvv = (int) ((Math.random() * (999-100))+100);
        Card card = new Card(client.getFirstName()+ " "+ client.getLastName(), cardType, cardColor, cardNumber, cvv, LocalDateTime.now(), LocalDateTime.now().plusYears(5), client);
        cardRepository.save(card);

        return new ResponseEntity<>("Se ha creado su tarjeta",HttpStatus.CREATED);
    }
}