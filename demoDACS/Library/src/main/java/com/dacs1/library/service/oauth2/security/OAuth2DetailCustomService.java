package com.dacs1.library.service.oauth2.security;

import com.dacs1.library.model.Customer;
import com.dacs1.library.repository.CustomerRepository;
import com.dacs1.library.repository.RoleRepository;
import com.dacs1.library.service.CustomerService;
import com.dacs1.library.service.MailService;
import com.dacs1.library.service.oauth2.OAuth2UserDetailFactory;
import com.dacs1.library.service.oauth2.OAuth2UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
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
                        .map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList()));
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
        return customerRepository.save(customer);
    }

    private Customer updateOAuth2Customer(Customer customer, OAuth2UserDetails oAuth2UserDetails) {
        customer.setFirstName(oAuth2UserDetails.getFirstName());
        customer.setLastName(oAuth2UserDetails.getLastName());
        customer.setEmail(oAuth2UserDetails.getEmail());
        return customerRepository.save(customer);
    }
}
