package com.catcosy.library.service;

import com.catcosy.library.dto.VoucherDto;
import com.catcosy.library.model.Voucher;

import java.util.List;
import java.util.Optional;

public interface VoucherService {

    List<Voucher> getAllVoucher();

    Optional<Voucher> getVoucherById(Long id);

    VoucherDto getVoucherDtoById(Long id);

    Voucher saveVoucher(Voucher voucher);

    Voucher updateVoucher(Voucher voucher);

    Voucher deleteVoucher(Long id);

    Voucher activateVoucher(Long id);

    double checkCartToApplyVoucher(String code, Long cartId);

    void applyVoucher(String code, Long idOrder);

    boolean checkEmailVoucher(Long idOld, String newEmail);

}
