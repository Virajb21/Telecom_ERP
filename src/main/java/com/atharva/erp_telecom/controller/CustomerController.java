package com.atharva.erp_telecom.controller;


import com.atharva.erp_telecom.entity.Customer;
import com.atharva.erp_telecom.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService){
        this.customerService = customerService;
    }

    @GetMapping("/get")
    public Object getCustomers(@RequestParam(value = "id",required = false) String customerId){
        // Get single customer by ID
        if(customerId!=null) {
            Optional<Customer> fetchedCustomer = customerService.getCustomerById(Integer.parseInt(customerId));
            return fetchedCustomer.map(ResponseEntity::ok)
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
        // Get All the customers
        else{
            Optional<List<Customer>> fetchedCustomers = Optional.ofNullable(customerService.getAllCustomers());
            return fetchedCustomers.map(ResponseEntity::ok)
                    .orElseGet(()->new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
    }

    @PostMapping("/new")
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer){
        Customer responseFromDB = customerService.createCustomer(customer);
        return new ResponseEntity<Customer>(responseFromDB,HttpStatus.CREATED);
    }
}
