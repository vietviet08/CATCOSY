package com.dacs1.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDetailDto {
    private String cityValue;
    private String districtValue;
    private String communeValue;
}
