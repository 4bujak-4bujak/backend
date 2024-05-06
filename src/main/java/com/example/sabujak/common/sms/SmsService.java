package com.example.sabujak.common.sms;

import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmsService {
    @Value("${coolsms.api.from}")
    private String from;

    private final DefaultMessageService defaultMessageService;

    @Async
    public void sendSms(String to, Message message) {

        message.setFrom(from);
        message.setTo(to);

        defaultMessageService.sendOne(new SingleMessageSendingRequest(message));
    }
}
