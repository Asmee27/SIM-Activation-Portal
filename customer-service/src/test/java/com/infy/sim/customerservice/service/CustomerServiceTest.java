package com.infy.sim.customerservice.service;

import com.infy.sim.customerservice.client.SimClient;
import com.infy.sim.customerservice.dto.AddressDTO;
import com.infy.sim.customerservice.dto.CustomerActivationRequest;
import com.infy.sim.customerservice.dto.CustomerDetailsRequest;
import com.infy.sim.customerservice.dto.CustomerValidationRequest;
import com.infy.sim.customerservice.dto.MessageResponse;
import com.infy.sim.customerservice.entity.Customer;
import com.infy.sim.customerservice.exception.InvalidDetailsException;
import com.infy.sim.customerservice.exception.NoRequestFoundException;
import com.infy.sim.customerservice.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    private static final String EMAIL = "mihir@infy.com";
    private static final LocalDate DOB = LocalDate.of(1990, 12, 12);
    private static final AddressDTO ADDRESS = new AddressDTO("12 MG Road", "411001", "Pune", "Maharashtra");

    @Mock
    private CustomerRepository repository;

    @Mock
    private SimClient simClient;

    private CustomerService service;
    private Customer customer;

    @BeforeEach
    void setUp() {
        service = new CustomerService(repository, simClient);
        customer = new Customer();
        customer.setEmail(EMAIL);
        customer.setDob(DOB);
        customer.setFirstName("Mihir");
        customer.setLastName("Khandekar");
    }

    @Test
    void validatesExistingRequest() {
        when(repository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(customer));

        assertEquals(CustomerService.VALID_CUSTOMER,
                service.validate(new CustomerValidationRequest(EMAIL, DOB)).message());
    }

    @Test
    void unknownEmailMeansNoRequest() {
        when(repository.findByEmailIgnoreCase("x@y.com")).thenReturn(Optional.empty());

        assertEquals(CustomerService.NO_REQUEST, assertThrows(NoRequestFoundException.class,
                () -> service.validate(new CustomerValidationRequest("x@y.com", DOB))).getMessage());
    }

    @Test
    void emailWithDifferentDobIsInvalid() {
        when(repository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(customer));

        assertEquals(CustomerService.INVALID_DETAILS, assertThrows(InvalidDetailsException.class,
                () -> service.validate(new CustomerValidationRequest(EMAIL, DOB.minusYears(1)))).getMessage());
    }

    @Test
    void rejectsWrongName() {
        when(repository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(customer));

        assertThrows(InvalidDetailsException.class,
                () -> service.validateDetails(new CustomerDetailsRequest(EMAIL, "Rahul", "Khandekar")));
    }

    @Test
    void activationSavesAddressAndCallsSimService() {
        when(repository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(customer));
        when(simClient.activate(any())).thenReturn(new MessageResponse("SIM activated successfully"));

        MessageResponse response = service.activate(
                new CustomerActivationRequest("Mihir", "Khandekar", EMAIL, DOB, ADDRESS, "1234567891234"));

        assertEquals("SIM activated successfully", response.message());
        assertEquals("Pune", customer.getAddress().getCity());
        verify(simClient).activate(new SimClient.SimActivationRequest("1234567891234"));
    }

    @Test
    void activationRejectsUnknownState() {
        when(repository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(customer));
        AddressDTO badState = new AddressDTO("12 MG Road", "411001", "Pune", "Atlantis");

        assertThrows(InvalidDetailsException.class, () -> service.activate(
                new CustomerActivationRequest("Mihir", "Khandekar", EMAIL, DOB, badState, "1234567891234")));
        verify(simClient, never()).activate(any());
    }
}
