package controllers;

import client.CustomersClient;
import errors.CustomFeignException;
import jakarta.validation.constraints.Email;
import models.Customer;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

// Controller for Customers endpoints
@RestController
@RequestMapping("/customers")
public class CustomersController {
    private final CustomersClient customersClient;

    public CustomersController(CustomersClient customersClient) {
        this.customersClient = customersClient;
    }

    @PostMapping
    public ResponseEntity<Object> addCustomer(@RequestBody Customer customer) {
        try {
            Object result = customersClient.addCustomer(customer);
            return ResponseEntity.ok(result);
        } catch (CustomFeignException e) {
            try {
                // Convert the response body to the appropriate format
                String responseBody = StreamUtils.copyToString(e.getBody().asInputStream(), StandardCharsets.UTF_8);
                return ResponseEntity.status(e.getStatus()).body(responseBody);
            } catch (IOException ioException) {
                // Fallback if we can't read the response body
                return ResponseEntity.status(e.getStatus()).build();
            }
        }
    }

//        @GetMapping
//        public ResponseEntity<Object> getCustomers(@RequestParam(required = false) String userId) {
//            if (userId != null) {
//                return ResponseEntity.ok(customersClient.getCustomerByUserId(userId));
//            }
//            return ResponseEntity.ok(customersClient.getAllCustomers());
//        }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getCustomerById(@PathVariable String id) {
        try {
            Object result = customersClient.getCustomerById(id);
            return ResponseEntity.ok(result);
        } catch (CustomFeignException e) {
            try {
                String responseBody = StreamUtils.copyToString(e.getBody().asInputStream(), StandardCharsets.UTF_8);
                return ResponseEntity.status(e.getStatus()).body(responseBody);
            } catch (IOException ioException) {
                return ResponseEntity.status(e.getStatus()).build();
            }
        }
    }

    @GetMapping
    public ResponseEntity<Object> getCustomerByUserId(@RequestParam("userId") @Email String userId) {
        try {
            Object result = customersClient.getCustomerByUserId(userId);
            return ResponseEntity.ok(result);
        } catch (CustomFeignException e) {
            try {
                String responseBody = StreamUtils.copyToString(e.getBody().asInputStream(), StandardCharsets.UTF_8);
                return ResponseEntity.status(e.getStatus()).body(responseBody);
            } catch (IOException ioException) {
                return ResponseEntity.status(e.getStatus()).build();
            }
        }
    }
}
