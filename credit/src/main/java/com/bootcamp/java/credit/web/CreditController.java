package com.bootcamp.java.credit.web;

import com.bootcamp.java.credit.domain.Credit;
import com.bootcamp.java.credit.service.CreditService;
import com.bootcamp.java.credit.web.mapper.CreditMapper;
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
@RequestMapping("/v1/credit")
public class CreditController {
    @Value("${spring.application.name}")
    String name;

    @Value("${server.port}")
    String port;

    @Autowired
    private CreditService creditService;

    @Autowired
    private CreditMapper creditMapper;

    @GetMapping
    public Mono<ResponseEntity<Flux<CreditModel>>> getAll(){
        log.info("getAll executed");
        return Mono.just(ResponseEntity.ok()
                .body(creditService.findAll()
                        .map(client -> creditMapper.entityToModel(client))));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<CreditModel>> getById(@PathVariable String id){
        log.info("getById executed {}", id);
        Mono<Credit> response = creditService.findById(id);
        return response
                .map(customer -> creditMapper.entityToModel(customer))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/getByCreditNumber/{creditNumber}")
    public Mono<ResponseEntity<CreditModel>> getByCreditNumber(@PathVariable String creditNumber){
        log.info("getByCreditNumber executed {}", creditNumber);
        Mono<Credit> response = creditService.findTopByCreditNumber(creditNumber);
        return response
                .map(customer -> creditMapper.entityToModel(customer))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<CreditModel>> create(@Valid @RequestBody CreditModel request){
        log.info("create executed {}", request);
        return creditService.create(creditMapper.modelToEntity(request))
                .map(client -> creditMapper.entityToModel(client))
                .flatMap(c ->
                        Mono.just(ResponseEntity.created(URI.create(String.format("http://%s:%s/%s/%s", name,
                                        port, "credit", c.getId())))
                                .body(c)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/updateByCreditNumber")
    public Mono<ResponseEntity<CreditModel>> updateByCreditNumber(@Valid @RequestBody CreditModel request){
        log.info("updateByAccountNumber executed {}", request);
        return creditService.updateByCreditNumber(creditMapper.modelToEntity(request))
                .map(client -> creditMapper.entityToModel(client))
                .flatMap(c ->
                        Mono.just(ResponseEntity.created(URI.create(String.format("http://%s:%s/%s/%s", name,
                                        port, "credit", c.getId())))
                                .body(c)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
