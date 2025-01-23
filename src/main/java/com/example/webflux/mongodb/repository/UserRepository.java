package com.example.webflux.mongodb.repository;

import com.example.webflux.mongodb.domain.User;

public interface UserRepository extends ReactiveMongoRepository<User, String>{
    Mono<User> findByUsername(String username);
}