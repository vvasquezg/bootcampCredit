package com.bootcamp.java.credit.web;

import com.bootcamp.java.credit.domain.Card;
import com.bootcamp.java.credit.domain.ClientDocument;
import com.bootcamp.java.credit.service.CardService;
import com.bootcamp.java.credit.web.mapper.CardMapper;
import com.bootcamp.java.credit.web.model.CardModel;
import com.bootcamp.java.credit.web.model.CreditModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/card")
public class CardController {
    @Value("${spring.application.name}")
    String name;

    @Value("${server.port}")
    String port;

    @Autowired
    private CardService cardService;

    @Autowired
    private CardMapper cardMapper;

    @GetMapping
    public Mono<ResponseEntity<Flux<CardModel>>> getAll(){
        log.info("getAll executed");
        return Mono.just(ResponseEntity.ok()
                .body(cardService.findAll()
                        .map(client -> cardMapper.entityToModel(client))));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<CardModel>> getById(@PathVariable String id){
        log.info("getById executed {}", id);
        Mono<Card> response = cardService.findById(id);
        return response
                .map(customer -> cardMapper.entityToModel(customer))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/getByCardNumber/{cardNumber}")
    public Mono<ResponseEntity<CardModel>> getByCardNumber(@PathVariable String cardNumber){
        log.info("getByCardNumber executed {}", cardNumber);
        Mono<Card> response = cardService.findTopByCardNumber(cardNumber);
        return response
                .map(customer -> cardMapper.entityToModel(customer))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<CardModel>> create(@Valid @RequestBody CardModel request){
        log.info("create executed {}", request);
        return cardService.create(cardMapper.modelToEntity(request))
                .map(client -> cardMapper.entityToModel(client))
                .flatMap(c ->
                        Mono.just(ResponseEntity.created(URI.create(String.format("http://%s:%s/%s/%s", name,
                                        port, "card", c.getId())))
                                .body(c)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/countCardByClient/{identityDocumentType}/{identityDocumentNumber}")
    public Mono<ResponseEntity<Long>> countCardByClient(@PathVariable String identityDocumentType, @PathVariable String identityDocumentNumber){
        log.info("countCardByClient executed {} - {}", identityDocumentType, identityDocumentNumber);

        Mono<Long> response = cardService.countCardByClient(new ClientDocument(identityDocumentType, identityDocumentNumber));
        return response
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/updateByCardNumber")
    public Mono<ResponseEntity<CardModel>> updateByCardNumber(@Valid @RequestBody CardModel request){
        log.info("updateByAccountNumber executed {}", request);
        return cardService.updateByCardNumber(cardMapper.modelToEntity(request))
                .map(client -> cardMapper.entityToModel(client))
                .flatMap(c ->
                        Mono.just(ResponseEntity.created(URI.create(String.format("http://%s:%s/%s/%s", name,
                                        port, "card", c.getId())))
                                .body(c)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
