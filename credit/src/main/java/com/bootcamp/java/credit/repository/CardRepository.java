package com.bootcamp.java.credit.repository;

import com.bootcamp.java.credit.domain.Card;
import com.bootcamp.java.credit.domain.ClientDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CardRepository extends ReactiveMongoRepository<Card, String> {
    Mono<Long> countByClient(ClientDocument client);
    Mono<Long> countByClientAndProductCode(ClientDocument client, String productCode);
    Mono<Card> findTopByCardNumber(String cardNumber);
}
