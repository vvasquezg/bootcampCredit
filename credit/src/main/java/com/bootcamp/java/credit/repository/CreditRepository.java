package com.bootcamp.java.credit.repository;

import com.bootcamp.java.credit.domain.ClientDocument;
import com.bootcamp.java.credit.domain.Credit;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CreditRepository extends ReactiveMongoRepository<Credit, String> {
    Mono<Long> countByClientAndProductCode(ClientDocument client, String productCode);
    Mono<Credit> findTopByCreditNumber(String creditNumber);
}
