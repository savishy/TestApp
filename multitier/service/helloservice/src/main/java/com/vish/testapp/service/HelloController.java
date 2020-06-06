package com.vish.testapp.service;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Controller;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {

  @Autowired
  private CustomerRepository repository;

  private static final String template = "Hello, %s!";
  private final AtomicLong counter = new AtomicLong();

  @GetMapping("/customers")
  @ResponseBody
  public ArrayList<Customer> getCustomers(
    @RequestParam(name="id",required=false) Long id,
    @RequestParam(name="lastName",required=false) String lastName
  ) {
    ArrayList<Customer> retVal = new ArrayList<Customer>();
    if (id == null && lastName == null) {
      for (Customer customer : repository.findAll()) {
        retVal.add(customer);
      }
    }
    if (id != null) {
      Optional<Customer> customer = repository.findById(id);
      if (customer.isPresent()) retVal.add(customer.get());
    }
    else if (lastName != null){
      retVal.addAll(repository.findByLastName(lastName));
    }

    return retVal;
  }


  @GetMapping("/hello-world")
  @ResponseBody
  public Response sayHello(@RequestParam(name="name", required=false, defaultValue="Stranger") String name) {
    return new Response(counter.incrementAndGet(), String.format(template, name));
  }

}
