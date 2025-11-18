package com.example.smsserver.repository;

import com.example.smsserver.model.RegistrationToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface TokenRegistrationRepository extends MongoRepository<RegistrationToken, String> {

    @Query("{tokenID: '?0'}")
    RegistrationToken findByTokenID(String tokenID);

    @Query("{userID:  '?0'}")
    RegistrationToken findByUserID(String userID);
}
