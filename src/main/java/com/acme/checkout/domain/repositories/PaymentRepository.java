package com.acme.checkout.domain.repositories;

import com.acme.checkout.domain.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {

    @Query("{ 'customerGuid' : ?0, 'guid' : ?1 }")
    Payment findByGuid(String customerGuid, String guid);

}
