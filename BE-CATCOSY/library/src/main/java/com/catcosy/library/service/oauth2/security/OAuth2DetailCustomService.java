package com.catcosy.library.service.oauth2.security;

import com.catcosy.library.repository.CustomerRepository;
import com.catcosy.library.repository.RoleRepository;
import com.catcosy.library.service.oauth2.OAuth2UserDetailFactory;
import com.catcosy.library.service.oauth2.OAuth2UserDetails;
import com.catcosy.library.model.Customer;
import com.catcosy.library.service.CustomerService;
import com.catcosy.library.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OAuth2DetailCustomService extends DefaultOAuth2UserService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MailService mailService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        return checkingUser(userRequest, oAuth2User);

    }

    private OAuth2User checkingUser(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        OAuth2UserDetails oAuth2UserDetails = OAuth2UserDetailFactory.getOAuth2Detail(
                userRequest.getClientRegistration().getRegistrationId(),
                oAuth2User.getAttributes());

        Map<String, Object> authentication = oAuth2User.getAttributes();

        if (ObjectUtils.isEmpty(oAuth2UserDetails)) try {
            throw new Exception();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Optional<Customer> customer = customerRepository.findByIdOAuth2AndProvider(
                oAuth2UserDetails.getId(),
                userRequest.getClientRegistration().getRegistrationId());

        Customer customerDetail;

        if (customer.isPresent()) {
            customerDetail = customer.get();
//
//            CustomerDto customerDto = customerService.findByEmail(oAuth2UserDetails.getEmail());
//
//
//            if(!customerDetail.getProvider().equals(customerDto.getProvider())) throw new RuntimeException("Email already exists, please create an account with another email!");

            if (!customerDetail.getProvider().equals(userRequest.getClientRegistration().getRegistrationId())) try {
                throw new Exception();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            customerDetail = updateOAuth2Customer(customerDetail, oAuth2UserDetails);
        } else {
            customerDetail = createNewOAuth2Customer(userRequest, oAuth2UserDetails);
            mailService.sendMailToCustomer(customerService.findByUsernameDto(customerDetail.getUsername()));
        }


        return new OAuth2CustomerDetailCustom(
                customerDetail.getId(),
                customerDetail.getUsername(),
                customerDetail.getPassword(),
                customerDetail.getRoles()
                        .stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList()),
                customerDetail.isActive());
    }

    private Customer createNewOAuth2Customer(OAuth2UserRequest userRequest, OAuth2UserDetails oAuth2UserDetails) {
        Customer customer = new Customer();
        customer.setUsername(oAuth2UserDetails.getId());
        customer.setFirstName(oAuth2UserDetails.getFirstName());
        customer.setLastName(oAuth2UserDetails.getLastName());
        customer.setEmail(oAuth2UserDetails.getEmail());
        customer.setRoles(Arrays.asList(roleRepository.findByName("CUSTOMER")));
        customer.setProvider(userRequest.getClientRegistration().getRegistrationId());
        customer.setIdOAuth2(oAuth2UserDetails.getId());
        if(oAuth2UserDetails.getAvatar() != null)
            customer.setImage(encodeImageToBase64(oAuth2UserDetails.getAvatar()));
        return customerRepository.save(customer);
    }

    private Customer updateOAuth2Customer(Customer customer, OAuth2UserDetails oAuth2UserDetails) {
        customer.setFirstName(oAuth2UserDetails.getFirstName());
        customer.setLastName(oAuth2UserDetails.getLastName());
        customer.setEmail(oAuth2UserDetails.getEmail());
        if(oAuth2UserDetails.getAvatar() != null)
            customer.setImage(encodeImageToBase64(oAuth2UserDetails.getAvatar()));
        return customerRepository.save(customer);
    }

    private String encodeImageToBase64(String stringUrl) {
        String base64Image = "";
        InputStream is = null;
        ByteArrayOutputStream baos = null;

        try {
            URL url = new URL(stringUrl);
            is = url.openStream();
            baos = new ByteArrayOutputStream();

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }

            byte[] imageBytes = baos.toByteArray();
            base64Image = Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (baos != null) {
                    baos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return base64Image;
    }
}
