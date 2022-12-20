package com.bootcamp.java.credit.service;

import com.bootcamp.java.credit.domain.ClientDocument;
import com.bootcamp.java.credit.service.exception.InvalidClientException;
import com.bootcamp.java.credit.service.exception.InvalidProductException;
import com.bootcamp.java.credit.web.model.ClientModel;
import com.bootcamp.java.credit.web.model.ProductParameterModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommonService {

    private WebClient getWCClient(){
        log.debug("getWebClient executed");
        return WebClient.builder()
                .baseUrl("http://localhost:9050/v1/client")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    private WebClient getWCParameter(){
        log.debug("getWebClient executed");
        return WebClient.builder()
                .baseUrl("http://localhost:9054/v1/productParameter")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    private WebClient getWCAccount(){
        log.debug("getWCAccount executed");
        return WebClient.builder()
                .baseUrl("http://localhost:9055/v1/account")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Mono<ClientModel> getClientByDocument(ClientDocument client){
        log.debug("getClientByDocument executed {}", client);
        return getWCClient().get().uri("/" + client.getIdentityDocumentNumber() + "/" + client.getIdentityDocumentType()).retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(new InvalidClientException())).bodyToMono(ClientModel.class);
    }

    public Mono<ProductParameterModel> getParameterByCodeAndTypeAndProfile(String code, String type, String profile) {
        log.debug("getParameterByCodeAndTypeAndProfile executed {} - {} - {}", code, type, profile);
        return getWCParameter().get().uri("/getByCodeAndTypeAndProfile/" + code + "/" + type + "/" + profile).retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(new InvalidProductException())).bodyToMono(ProductParameterModel.class);
    }

    public Mono<Long> countAccountByClient(ClientDocument client){
        log.debug("countAccountByClient executed {}", client);
        return getWCAccount().get().uri("/countAccountByClient/" + client.getIdentityDocumentType() + "/" + client.getIdentityDocumentNumber()).retrieve().bodyToMono(Long.class);
    }

}
