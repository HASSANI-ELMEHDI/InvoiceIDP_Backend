package ensias.tim.phd.invoice_IDP.layer2;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class LlmService {
    private final ChatClient chatClient;

    LlmService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }
    public String extractStructuredData(String prompt) {
        return chatClient.prompt()
                .system("You are an AI assistant trained to extract structured data from invoices.")
                .user(prompt)
                .call()
                .content();
    }
}