package com.catcosy.library.service.impl;

import com.catcosy.library.repository.StatisticsRepository;
import com.catcosy.library.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private StatisticsRepository statisticsRepository;

    @Override
    public int getTotalCustomer() {
        return statisticsRepository.totalCustomer();
    }

    @Override
    public int getTotalOrdersDelivered() {
        return statisticsRepository.totalOrdersDelivered();
    }

    @Override
    public double getTotalPriceOrders() {
        return statisticsRepository.totalPrice();
    }

    @Override
    public int getTotalProduct() {
        return statisticsRepository.totalProduct();
    }

    @Override
    public int getTotalProductByCategory(String nameCategory) {
        return statisticsRepository.totalProductByCategory(nameCategory);
    }
}
