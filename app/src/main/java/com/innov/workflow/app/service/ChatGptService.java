package com.innov.workflow.app.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class ChatGptService {

    @Value(value = "${gpt.apiKey}")
    private String apiKey;


}
