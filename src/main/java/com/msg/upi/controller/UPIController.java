package com.msg.upi.controller;

import com.msg.upi.model.BalanceRequest;
import com.msg.upi.model.BalanceResponse;
import com.msg.upi.service.NCPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
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
        String requestId = balanceRequest.getUid();

        System.out.println("Request -->" + requestId);
        CountDownLatch latch = new CountDownLatch(1);

        StopWatch sw = new StopWatch("UPIRequestId#"+ requestId);
        sw.start();

        ncpiService.requestFetchBalance(balanceRequest, latch);

        try {
            latch.await(5, TimeUnit.MINUTES);
            BalanceResponse balanceResponse = ncpiService.processResponse(requestId);

            sw.stop();
            System.out.println(sw.shortSummary());

            return new ResponseEntity<>(balanceResponse, HttpStatus.OK);
        }
        catch (InterruptedException e) {
            System.out.println("Will not wait any more now.");
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }


}
