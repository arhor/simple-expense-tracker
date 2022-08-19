package com.github.arhor.simple.expense.tracker.web.controller;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RestController
@RequestMapping("/")
public class SimpleController {

    @GetMapping("/hello/{id}")
    public Mono<String> hello(
        final ServerHttpRequest request,
        final UriComponentsBuilder uriBuilder
    ) {
//        log.info("URI-1: {}", request..uriBuilder().build());
//        log.info("URI-2: {}", request.uriBuilder().build(id));
        return Mono.just("Hello there!");
    }
}
