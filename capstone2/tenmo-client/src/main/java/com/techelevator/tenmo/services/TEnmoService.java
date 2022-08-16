package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class TEnmoService {

    private static String API_BASE_URL = "http://localhost:8080/";
    private RestTemplate restTemplate = new RestTemplate();
    private AuthenticatedUser currentUser;

    public void setCurrentUser(AuthenticatedUser currentUser) {
        this.currentUser = currentUser;
    }

    public AuthenticatedUser getCurrentUser() { return currentUser; }

    public BigDecimal getBalance(int id) {
        ResponseEntity<BigDecimal> response = restTemplate.exchange(API_BASE_URL + "accounts/" + id, HttpMethod.GET, authEntity(), BigDecimal.class);
        return response.getBody();
    }

    public List<User> listOfUsers() {
        ResponseEntity<User[]> response = restTemplate.exchange( API_BASE_URL +  "users", HttpMethod.GET, authEntity(), User[].class);
        User[] userArray = response.getBody();
        return Arrays.asList(userArray);
    }

    public void sendTransfer(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);

        ResponseEntity<Void> response = restTemplate.exchange( API_BASE_URL + "transfers/", HttpMethod.POST, entity, Void.class);
    }

    public String getUsername(int id) {
        ResponseEntity<String> response = restTemplate.exchange(API_BASE_URL + "accounts/username/" + id, HttpMethod.GET, authEntity(), String.class);
        return response.getBody();
    }

    public List<Transfer> listOfTransfers(int id) {
        ResponseEntity<Transfer[]> response = restTemplate.exchange( API_BASE_URL +  "transfers/" + id, HttpMethod.GET, authEntity(), Transfer[].class);
        Transfer[] transferArray = response.getBody();
        return Arrays.asList(transferArray);
    }

    public String getTransferTypeString(int id) {
        ResponseEntity<String> response = restTemplate.exchange(API_BASE_URL + "transfer/transfer_type/" + id, HttpMethod.GET, authEntity(), String.class);
        return response.getBody();
    }

    public String getTransferStatusString(int id) {
        ResponseEntity<String> response = restTemplate.exchange(API_BASE_URL + "transfer/transfer_status/" + id, HttpMethod.GET, authEntity(), String.class);
        return response.getBody();
    }


    public Transfer getTransferDetails(int transfer_id) {
        ResponseEntity<Transfer> response = restTemplate.exchange( API_BASE_URL +  "transferDetails/" + transfer_id, HttpMethod.GET, authEntity(), Transfer.class);
        return response.getBody();
    }


    private HttpEntity<Void> authEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        return new HttpEntity<>(headers);
    }






}
