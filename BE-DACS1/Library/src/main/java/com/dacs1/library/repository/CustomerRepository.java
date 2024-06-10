package com.dacs1.library.repository;

import com.dacs1.library.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Customer findByUsername(String username);

    @Query(value = "select * from customers where username = :username and is_active = 1", nativeQuery = true)
    Customer findByUsernameAndActive(String username);


//    @Query(value = "select * from customers where idoath2 = :idOAth2 and provider = :provider", nativeQuery = true)
    Optional<Customer> findByIdOAuth2AndProvider( String idOAuth2, String provider);

    Customer findByResetPasswordToken(String resetPasswordToken);


    Customer findByEmailAndProvider(String email, String provider);

    Customer findByEmail(String email);


}