package com.dacs1.customer.controller;


import com.dacs1.library.dto.AddressDetailDto;
import com.dacs1.library.model.Customer;
import com.dacs1.library.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin("*")
public class AddressRestController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/address-detail-select")
    public AddressDetailDto getAddressDetailSelect(@RequestParam("req")String username){
        Customer customer = customerService.findByUsername(username);

        AddressDetailDto addressDetailDto = new AddressDetailDto();
        String addressDetail = customer.getAddressDetail();

        if (addressDetail != null) {
            String[] address = addressDetail.split(" - ");

            if (address.length > 1) {
                String addressSelect = address[1];
                String[] childAddress = addressSelect.split(", ");

                addressDetailDto.setCityValue(childAddress[0]);
                addressDetailDto.setDistrictValue(childAddress[1]);
                addressDetailDto.setCommuneValue(childAddress[2]);
            }

        }
        return addressDetailDto;
    }

}
