package com.bootcamp.java.credit.service;

import com.bootcamp.java.credit.domain.Card;
import com.bootcamp.java.credit.domain.ClientDocument;
import com.bootcamp.java.credit.domain.Credit;
import com.bootcamp.java.credit.repository.CardRepository;
import com.bootcamp.java.credit.service.exception.InvalidClientException;
import com.bootcamp.java.credit.service.exception.InvalidProductException;
import com.bootcamp.java.credit.web.mapper.CardMapper;
import com.bootcamp.java.credit.web.model.ClientModel;
import com.bootcamp.java.credit.web.model.ProductParameterModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CardService {
    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private CardMapper cardMapper;

    @Autowired
    private CommonService commonService;

    public Flux<Card> findAll(){
        log.debug("findAll executed");
        return cardRepository.findAll();
    }

    public Mono<Card> findTopByCardNumber(String cardNumber){
        log.debug("findTopByCardNumber executed {}", cardNumber);
        return cardRepository.findTopByCardNumber(cardNumber);
    }

    public Mono<Card> findById(String id){
        log.debug("findById executed {}", id);
        return cardRepository.findById(id);
    }

    public Mono<Card> create(Card card) {
        log.debug("create executed {}", card);

        return validateClient(card.getClient()).flatMap(clientModel -> validateProductCode(card.getProductCode(), clientModel.getClientType(), clientModel.getClientProfile())
                .flatMap(productParameterModel -> {
                    Mono<Boolean> maxAccountValidated = validateMaxProduct(card.getClient(), card.getProductCode(), productParameterModel.getMaxProduct());
                    Mono<Boolean> accountRequired = validateAccount(productParameterModel.getAccountRequired(), card.getClient());
                    Mono<Boolean> cardRequired = validateCard(productParameterModel.getCardRequired(), card.getClient());

                    return Mono.zip(maxAccountValidated, accountRequired, cardRequired)
                            .flatMap(objects -> !objects.getT1() ? Mono.error(new Exception("Max. Account")) : !objects.getT2()
                                    ? Mono.error(new Exception("Account created is required")) : !objects.getT3() ? Mono.error(new Exception("Card created is required")) :
                                    cardRepository.save(card));
                }));
    }

    public Mono<Card> updateByCardNumber(Card card) {
        log.debug("updateByCardNumber executed {}", card);
        return cardRepository.findTopByCardNumber(card.getCardNumber())
                .flatMap(dbCard -> {
                    cardMapper.update(dbCard, card);
                    return cardRepository.save(dbCard);
                });
    }

    private Mono<ClientModel> validateClient(ClientDocument client){
        log.debug("validateClient executed {}", client);
        return commonService.getClientByDocument(client)
                .switchIfEmpty(Mono.error(new InvalidClientException()))
                .flatMap(Mono::just);
    }

    private Mono<ProductParameterModel> validateProductCode(String code, String type, String profile){
        return commonService.getParameterByCodeAndTypeAndProfile(code, type, profile)
                .switchIfEmpty(Mono.error(new InvalidProductException()))
                .flatMap(Mono::just);
    }

    private Mono<Boolean> validateMaxProduct(ClientDocument client, String productCode, Long maxProduct){
        log.debug("validateMaxProduct executed {} - {} - {}", client, productCode, maxProduct);
        Mono<Long> countClientProduct = countByClientAndProductCode(client, productCode);

        return countClientProduct.flatMap(aLong -> aLong >= maxProduct ? Mono.just(false) : Mono.just(true));
    }

    public Mono<Long> countByClientAndProductCode(ClientDocument client, String productCode){
        log.debug("countByClientAndProductCode executed {} - {}", client, productCode);
        return cardRepository.countByClientAndProductCode(client, productCode);
    }

    private Mono<Boolean> validateAccount(Boolean accountRequired, ClientDocument client) {
        return accountRequired ?
                commonService.countAccountByClient(client).flatMap(aLong -> aLong > 0 ? Mono.just(true) : Mono.just(false))
                : Mono.just(true);
    }

    private Mono<Boolean> validateCard(Boolean cardRequired, ClientDocument client) {
        return cardRequired ?
                countCardByClient(client).flatMap(aLong -> aLong > 0 ? Mono.just(true) : Mono.just(false))
                : Mono.just(true);
    }

    public Mono<Long> countCardByClient(ClientDocument client){
        log.debug("countCardByClient executed {}", client);
        return cardRepository.countByClient(client);
    }

}
