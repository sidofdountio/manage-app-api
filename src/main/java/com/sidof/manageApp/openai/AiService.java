package com.sidof.manageApp.openai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

/**
 * <blockquote><pre>
 * Author   : @Dountio
 * LinkedIn : @SidofDountio
 * GitHub   : @SidofDountio
 * Version  : V1.0
 * Email    : sidofdountio406@gmail.com
 * Licence  : All Right Reserved BIS
 * Since    : 5/22/25
 * </blockquote></pre>
 */


@Service
public class AiService {
    private final ChatClient chatClient;


    public AiService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String chat(String prompt){
        return chatClient
                .prompt(prompt)
                .call()
                .content();
    }
}
