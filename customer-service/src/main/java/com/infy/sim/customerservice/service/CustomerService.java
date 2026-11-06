package com.infy.sim.customerservice.service;

import com.infy.sim.customerservice.client.SimClient;
import com.infy.sim.customerservice.dto.AddressDTO;
import com.infy.sim.customerservice.dto.CustomerActivationRequest;
import com.infy.sim.customerservice.dto.CustomerDetailsRequest;
import com.infy.sim.customerservice.dto.CustomerValidationRequest;
import com.infy.sim.customerservice.dto.MessageResponse;
import com.infy.sim.customerservice.entity.Address;
import com.infy.sim.customerservice.entity.Customer;
import com.infy.sim.customerservice.exception.InvalidDetailsException;
import com.infy.sim.customerservice.exception.NoRequestFoundException;
import com.infy.sim.customerservice.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    static final String NO_REQUEST = "No request placed for you.";
    static final String INVALID_DETAILS = "Invalid details";
    static final String INVALID_STATE = "Please select a valid state";
    static final String VALID_CUSTOMER = "Customer details validated";

    private final CustomerRepository customerRepository;
    private final SimClient simClient;

    public CustomerService(CustomerRepository customerRepository, SimClient simClient) {
        this.customerRepository = customerRepository;
        this.simClient = simClient;
    }

    /**
     * US04. Unknown email -> "No request placed for you."
     * Email belongs to a request but DOB differs -> "Invalid details" (email in use by someone else).
     */
    @Transactional(readOnly = true)
    public MessageResponse validate(CustomerValidationRequest request) {
        Customer customer = customerRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new NoRequestFoundException(NO_REQUEST));
        if (!customer.getDob().equals(request.dob())) {
            throw new InvalidDetailsException(INVALID_DETAILS);
        }
        return new MessageResponse(VALID_CUSTOMER);
    }

    /** US05. Names must match the details captured at purchase. */
    @Transactional(readOnly = true)
    public MessageResponse validateDetails(CustomerDetailsRequest request) {
        Customer customer = customerRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new NoRequestFoundException(NO_REQUEST));
        requireNamesMatch(customer, request.firstName(), request.lastName());
        return new MessageResponse(VALID_CUSTOMER);
    }

    /**
     * US06 + US08. Re-checks the customer, saves the address, then asks sim-service
     * (via Feign) to activate the SIM. If activation fails the exception propagates,
     * so the address update is rolled back with the transaction.
     */
    @Transactional
    public MessageResponse activate(CustomerActivationRequest request) {
        Customer customer = customerRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new NoRequestFoundException(NO_REQUEST));
        if (!customer.getDob().equals(request.dob())) {
            throw new InvalidDetailsException(INVALID_DETAILS);
        }
        requireNamesMatch(customer, request.firstName(), request.lastName());

        AddressDTO address = request.address();
        if (!IndianStates.isValid(address.state())) {
            throw new InvalidDetailsException(INVALID_STATE);
        }
        customer.setAddress(new Address(address.address().trim(), address.city().trim(),
                address.pinCode(), address.state().trim()));
        customerRepository.save(customer);

        return simClient.activate(new SimClient.SimActivationRequest(request.simNumber()));
    }

    private static void requireNamesMatch(Customer customer, String firstName, String lastName) {
        if (!customer.getFirstName().equalsIgnoreCase(firstName)
                || !customer.getLastName().equalsIgnoreCase(lastName)) {
            throw new InvalidDetailsException(INVALID_DETAILS);
        }
    }
}
