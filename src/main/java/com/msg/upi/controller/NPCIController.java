package com.msg.upi.controller;

import com.msg.upi.model.npci.NPCIFetchBalanceResponse;
import com.msg.upi.service.NCPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/upi/1.0", consumes = {MediaType.APPLICATION_JSON_VALUE})
public class NPCIController {
    private NCPIService ncpiService;

    @Autowired
    public NPCIController(NCPIService ncpiService) {
        this.ncpiService = ncpiService;
    }

    @RequestMapping(value = "callback/fetchBalance", method = RequestMethod.POST)
    public void fetchBalanceCallback(@RequestBody NPCIFetchBalanceResponse npciFetchBalanceResponse) {
        System.out.println("Callback received for request -->" + npciFetchBalanceResponse.getUid());
        ncpiService.callbackFetchBalance(npciFetchBalanceResponse);
    }
}
