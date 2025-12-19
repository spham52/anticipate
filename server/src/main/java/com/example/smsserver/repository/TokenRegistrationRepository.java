package com.example.smsserver.repository;

import com.example.smsserver.model.TokenRegistration;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface TokenRegistrationRepository extends MongoRepository<TokenRegistration, String> {

    @Query("{tokenID: '?0'}")
    TokenRegistration findByTokenID(String tokenID);

    @Query("{userID:  '?0'}")
    TokenRegistration findByUserID(String userID);

    boolean getTokenRegistrationByUserID(String userID);

    boolean existsByUserID(String userID);
}
