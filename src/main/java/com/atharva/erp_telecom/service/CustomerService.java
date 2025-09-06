package com.atharva.erp_telecom.service;


import com.atharva.erp_telecom.entity.Customer;
import com.atharva.erp_telecom.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomerService {
    // Better practice to use depenedency injection in the constructor instead of field
    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository){
        this.customerRepository = customerRepository;
    }

    public Optional<Customer> getCustomerById(int customerId){
        return customerRepository.findById(customerId);
    }

    public Customer createCustomer(Customer customer){
        return customerRepository.save(customer);
    }

    public List<Customer> getAllCustomers(){
        return customerRepository.findAll();
    }

}
