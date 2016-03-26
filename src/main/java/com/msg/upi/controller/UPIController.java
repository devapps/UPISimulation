package com.msg.upi.controller;

import com.msg.upi.model.BalanceRequest;
import com.msg.upi.model.BalanceResponse;
import com.msg.upi.service.NCPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/upi/1.0", consumes = {MediaType.APPLICATION_JSON_VALUE})
public class UPIController {

    private NCPIService ncpiService;

    @Autowired
    public UPIController(NCPIService ncpiService) {
        this.ncpiService = ncpiService;
    }

    @RequestMapping(value = "fetchBalance", method = RequestMethod.POST)
    public ResponseEntity<?> fetchBalance(@RequestBody BalanceRequest balanceRequest) {
        System.out.println("Request -->" + balanceRequest.getUid());
        CountDownLatch latch = new CountDownLatch(1);

        ncpiService.requestFetchBalance(balanceRequest, latch);
        try {
            latch.await(1, TimeUnit.MINUTES);

            return new ResponseEntity<>(new BalanceResponse(balanceRequest.getUid()), HttpStatus.OK);
        }
        catch (InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }


}
