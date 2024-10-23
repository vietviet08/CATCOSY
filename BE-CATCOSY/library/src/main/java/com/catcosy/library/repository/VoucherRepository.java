package com.catcosy.library.repository;

import com.catcosy.library.model.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    Voucher findByCodeVoucher(String codeVoucher);
}
