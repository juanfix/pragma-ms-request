package co.com.pragma.sqs.sender.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "adapters.sqs")
public record SQSSenderProperties(Map<String, QueueProperties> queues){
    public record QueueProperties(String region, String queueUrl, String endpoint) {}
}
