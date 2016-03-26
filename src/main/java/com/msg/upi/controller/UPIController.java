package com.msg.upi.controller;

import com.msg.upi.model.BalanceResponse;
import com.msg.upi.model.BalanceRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/upi/1.0", consumes = {MediaType.APPLICATION_JSON_VALUE})
public class UPIController {

    @RequestMapping(value = "fetchBalance", method = RequestMethod.POST)
    public
    ResponseEntity<?> fetchBalance(@RequestBody BalanceRequest balanceRequest) {
        System.out.println("Request -->" + balanceRequest.getUid());

        BalanceResponse balanceResponse = new BalanceResponse(balanceRequest.getUid());

        return new ResponseEntity<>(balanceResponse, HttpStatus.OK);
    }
}
