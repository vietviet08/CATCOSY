package com.dacs1.library.service.impl;

import com.dacs1.library.repository.StatisticsRepository;
import com.dacs1.library.service.StatisticsService;
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
