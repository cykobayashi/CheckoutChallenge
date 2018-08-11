package com.acme.checkout.domain.repositories;

import com.acme.checkout.domain.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

	@Query("{ 'email' : ?0 }")
	User findByEmail(String email);

	@Query("{ 'guid' : ?0 }")
	User findByGuid(String guid);

}
