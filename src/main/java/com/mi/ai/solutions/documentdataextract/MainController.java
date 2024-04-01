package com.mi.ai.solutions.documentdataextract;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import dev.langchain4j.chain.ConversationalRetrievalChain;
@RestController
@RequestMapping("/aidocs")
public class MainController {

//    @Autowired
//    private ConversationalRetrievalChain conversationalRetrievalChain;

    @Autowired
    private ConversationalRetrievalChain conversationalRetrievalChainAws;

    @PostMapping("/inquire")
    public String inquireUsingAWS(@RequestBody String question) {
        var answer = conversationalRetrievalChainAws.execute(question);
        return answer;
    }

    @GetMapping("/sayhi")
    public String sayHi(){
        return "hi";
    }

}
