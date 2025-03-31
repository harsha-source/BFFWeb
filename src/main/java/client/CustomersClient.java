package client;

import configuration.FeignConfig;
import models.Customer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Feign client for Customers Service (via ALB)
@FeignClient(name = "customers-service", url = "http://internal-bookstore-dev-InternalALB-1706046400.us-east-1.elb.amazonaws.com:3000/customers", configuration = FeignConfig.class)
public interface CustomersClient {

    @PostMapping
    Object addCustomer(Customer customer);

    @GetMapping("/{id}")
    Object getCustomerById(@PathVariable("id") String id);

    @GetMapping
    Object getCustomerByUserId(@RequestParam("userId") String userId);

    @GetMapping("/status")
    Object getStatus();
}
