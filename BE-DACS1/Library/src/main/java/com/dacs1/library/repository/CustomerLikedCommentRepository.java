package com.dacs1.library.repository;

import com.dacs1.library.model.CustomerLikedComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerLikedCommentRepository extends JpaRepository<CustomerLikedComment, Long> {

    @Query(value = "select * from customer_liked_comment ckc where ckc.customer_id = :idCustomer and ckc.rate_id = :idRate", nativeQuery = true)
    CustomerLikedComment checkLikedComment(@Param("idCustomer") Long idCustomer,
                                           @Param("idRate") Long idRate);

}
