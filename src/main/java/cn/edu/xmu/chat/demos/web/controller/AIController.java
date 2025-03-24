package cn.edu.xmu.chat.demos.web.controller;

import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AIController {

    @Autowired
    @Qualifier("ollamaChatClient")
    private OllamaChatClient  ollamaChatClient;
    @GetMapping("/aiChat")
    public String ollamaChat(@RequestParam String msg) {
        String response = this.ollamaChatClient.call(msg);
        return response;
    }
}
