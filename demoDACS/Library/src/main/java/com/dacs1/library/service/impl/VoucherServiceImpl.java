package com.dacs1.library.service.impl;

import com.dacs1.library.model.*;
import com.dacs1.library.repository.CartRepository;
import com.dacs1.library.repository.OrderRepository;
import com.dacs1.library.repository.VoucherRepository;
import com.dacs1.library.service.CartService;
import com.dacs1.library.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class VoucherServiceImpl implements VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public List<Voucher> getAllVoucher() {
        return voucherRepository.findAll();
    }

    @Override
    public Optional<Voucher> getVoucherById(Long id) {
        return voucherRepository.findById(id);
    }

    @Override
    public Voucher saveVoucher(Voucher voucher) {


        return voucherRepository.save(voucher);

    }

    @Override
    public Voucher updateVoucher(Voucher v) {
        Voucher voucher = voucherRepository.getReferenceById(v.getId());


        return null;
    }

    @Override
    public Voucher deleteVoucher(Long id) {
        Voucher voucher = voucherRepository.getReferenceById(id);
        voucher.setActivated(false);
        return voucherRepository.save(voucher);
    }

    @Override
    public Voucher activateVoucher(Long id) {
        Voucher voucher = voucherRepository.getReferenceById(id);
        voucher.setActivated(true);
        return voucherRepository.save(voucher);
    }

    @Override
    public double checkCartToApplyVoucher(String code, Long cartId) {
        try {
            Cart cart = cartRepository.getReferenceById(cartId);
            if (cart == null) return 0.0;

            Voucher voucher = voucherRepository.findByCodeVoucher(code);
            if (voucher == null) return 0.0;

            int totalItem = 0;
            Set<CartItem> cartItems = cart.getItems();
            for (CartItem cartItem : cartItems) totalItem += cartItem.getQuantity();

            if (voucher.isUsed() ||
                    !voucher.isActivated() ||
                    voucher.getUsageLimits() == 0 ||
                    !new Date().before(voucher.getExpiryDate()) ||
                    cart.getTotalPrice() < voucher.getMinimumPrice() ||
                     totalItem < voucher.getMinimumTotalProduct() ) return 0.0;
            else if (!voucher.getForEmailCustomer().isEmpty()) {
                if (voucher.getForEmailCustomer().equals(cart.getCustomer().getEmail()))
                    return calculatePriceSaleWithCart(voucher, cart);
                else return 0.0;
            } else return calculatePriceSaleWithCart(voucher, cart);
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }

    }

    @Override
    public void applyVoucher(String code, Long idOrder) {
        try {
            Order order = orderRepository.getReferenceById(idOrder);
            if (order == null) return;

            Voucher voucher = voucherRepository.findByCodeVoucher(code);

            if (voucher == null) return;

            int totalItem = 0;
            List<OrderDetail> orderDetails = order.getOrderDetails();
            for (OrderDetail orderDetail : orderDetails) totalItem += orderDetail.getQuantity();

            if (voucher.isUsed() ||
                    !voucher.isActivated() ||
                    voucher.getUsageLimits() == 0 ||
                    !new Date().before(voucher.getExpiryDate()) ||
                    order.getTotalPrice() < voucher.getMinimumPrice() ||
                    totalItem < voucher.getMinimumTotalProduct() ) ;
            else if (voucher.getForEmailCustomer().equals(order.getCustomer().getEmail())) {
                double priceSale = calculatePriceSale(voucher, order);
                voucher.setUsed(true);
                voucher.setUsageLimits(0);
                voucherRepository.save(voucher);
                order.setTotalPrice(order.getTotalPrice() - priceSale);
                order.setDiscountPrice(priceSale);
                orderRepository.save(order);
            } else {
                Double priceSale = calculatePriceSale(voucher, order);

                voucher.setUsageLimits(voucher.getUsageLimits() - 1);
                if (voucher.getUsageLimits() == 0) voucher.setUsed(true);
                voucherRepository.save(voucher);

                order.setTotalPrice(order.getTotalPrice() - priceSale);
                order.setDiscountPrice(priceSale);
                orderRepository.save(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double calculatePriceSale(Voucher voucher, Order order) {
        double priceSale = 0.0;
        if (voucher.getPrice() > 0 || voucher.getPrice() != null) priceSale = voucher.getPrice();
        else if (voucher.getPercentOfTotalPrice() > 0)
            priceSale = order.getTotalPrice() * ((double) voucher.getPercentOfTotalPrice() / 100);
        return priceSale;
    }

    private double calculatePriceSaleWithCart(Voucher voucher, Cart cart) {
        double priceSale = 0.0;
        if (voucher.getPrice() > 0 || voucher.getPrice() != null) priceSale = voucher.getPrice();
        else if (voucher.getPercentOfTotalPrice() > 0)
            priceSale = cart.getTotalPrice() * ((double) voucher.getPercentOfTotalPrice() / 100);

        return priceSale;
    }

}
