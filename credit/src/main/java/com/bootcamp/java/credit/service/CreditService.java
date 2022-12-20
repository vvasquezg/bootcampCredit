package com.bootcamp.java.credit.service;

import com.bootcamp.java.credit.domain.Card;
import com.bootcamp.java.credit.domain.ClientDocument;
import com.bootcamp.java.credit.domain.Credit;
import com.bootcamp.java.credit.repository.CreditRepository;
import com.bootcamp.java.credit.service.exception.InvalidClientException;
import com.bootcamp.java.credit.service.exception.InvalidProductException;
import com.bootcamp.java.credit.web.mapper.CreditMapper;
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
public class CreditService {
    @Autowired
    private CreditRepository creditRepository;

    @Autowired
    private CreditMapper creditMapper;

    @Autowired
    private CommonService commonService;

    @Autowired
    private CardService cardService;

    public Flux<Credit> findAll(){
        log.debug("findAll executed");
        return creditRepository.findAll();
    }

    public Mono<Credit> findById(String id){
        log.debug("findById executed {}", id);
        return creditRepository.findById(id);
    }

    public Mono<Credit> findTopByCreditNumber(String creditNumber){
        log.debug("findTopByCreditNumber executed {}", creditNumber);
        return creditRepository.findTopByCreditNumber(creditNumber);
    }

    public Mono<Credit> create(Credit credit) {
        log.debug("create executed {}", credit);

        return validateClient(credit.getClient()).flatMap(clientModel -> validateProductCode(credit.getProductCode(), clientModel.getClientType(), clientModel.getClientProfile())
                .flatMap(productParameterModel -> {
                    Mono<Boolean> maxAccountValidated = validateMaxProduct(credit.getClient(), credit.getProductCode(), productParameterModel.getMaxProduct());
                    Mono<Boolean> accountRequired = validateAccount(productParameterModel.getAccountRequired(), credit.getClient());
                    Mono<Boolean> cardRequired = validateCard(productParameterModel.getCardRequired(), credit.getClient());

                    return Mono.zip(maxAccountValidated, accountRequired, cardRequired)
                            .flatMap(objects -> !objects.getT1() ? Mono.error(new Exception("Max. Account")) : !objects.getT2()
                                    ? Mono.error(new Exception("Account created is required")) : !objects.getT3() ? Mono.error(new Exception("Card created is required")) :
                                    creditRepository.save(credit));
                }));
    }

    public Mono<Credit> updateByCreditNumber(Credit credit) {
        log.debug("updateByCreditNumber executed {}", credit);
        return creditRepository.findTopByCreditNumber(credit.getCreditNumber())
                .flatMap(dbCredit -> {
                    creditMapper.update(dbCredit, credit);
                    return creditRepository.save(dbCredit);
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
        return creditRepository.countByClientAndProductCode(client, productCode);
    }

    private Mono<Boolean> validateAccount(Boolean accountRequired, ClientDocument client) {
        return accountRequired ?
                commonService.countAccountByClient(client).flatMap(aLong -> aLong > 0 ? Mono.just(true) : Mono.just(false))
                : Mono.just(true);
    }

    private Mono<Boolean> validateCard(Boolean cardRequired, ClientDocument client) {
        return cardRequired ?
                cardService.countCardByClient(client).flatMap(aLong -> aLong > 0 ? Mono.just(true) : Mono.just(false))
                : Mono.just(true);
    }

}
