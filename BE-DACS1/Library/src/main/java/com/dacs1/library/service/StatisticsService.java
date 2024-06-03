package com.dacs1.library.service;

public interface StatisticsService {

    int getTotalCustomer();
    int getTotalOrdersDelivered();
    double getTotalPriceOrders();
    int getTotalProduct();
    int getTotalProductByCategory(String nameCategory);

}
