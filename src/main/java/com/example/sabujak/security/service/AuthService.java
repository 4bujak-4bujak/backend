package com.example.sabujak.security.service;

import com.example.sabujak.common.email.service.MailService;
import com.example.sabujak.common.image.MemberImage;
import com.example.sabujak.common.redis.service.RedisService;
import com.example.sabujak.common.sms.SmsService;
import com.example.sabujak.company.repository.CompanyRepository;
import com.example.sabujak.member.dto.request.MemberRequestDto;
import com.example.sabujak.member.entity.Member;
import com.example.sabujak.member.repository.MemberRepository;
import com.example.sabujak.security.dto.request.VerifyRequestDto;
import com.example.sabujak.security.exception.AuthException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.message.model.Message;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.example.sabujak.security.constants.SecurityConstants.*;
import static com.example.sabujak.security.exception.AuthErrorCode.*;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final CompanyRepository companyRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RedisService redisService;
    private final MailService mailService;
    private final SmsService smsService;
    private final SpringTemplateEngine templateEngine;

    @Transactional
    public void signUp(MemberRequestDto.SignUp signUp) {
        if (memberRepository.existsByMemberEmail(signUp.email())) {
            throw new AuthException(EMAIL_ALREADY_EXISTS);
        } else if (memberRepository.existsByMemberPhone(signUp.memberPhone())) {
            throw new AuthException(PHONE_ALREADY_EXISTS);
        } else if (!companyRepository.existsByCompanyEmailDomain(getEmailDomain(signUp.email()))) {
            throw new AuthException(UNCONTRACTED_COMPANY);
        }
        String encryptedPassword = bCryptPasswordEncoder.encode(signUp.password());
        Member member = signUp.toEntity(encryptedPassword);
        member.setImage(MemberImage.createDefaultMemberImage());
        memberRepository.save(member);
    }

    public void requestEmailVerify(VerifyRequestDto.Email email) throws MessagingException {
        if (memberRepository.existsByMemberEmail(email.emailAddress())) {
            throw new AuthException(EMAIL_ALREADY_EXISTS);
        } else if (!companyRepository.existsByCompanyEmailDomain(getEmailDomain(email.emailAddress()))) {
            throw new AuthException(UNCONTRACTED_COMPANY);
        }

        String verifyCode = generateVerifyCode();

        int expiredAtSeconds = EMAIL_CODE_EXPIRATION_MILLIS / 1000;
        LocalDateTime expiredAt = LocalDateTime.now().plusSeconds(expiredAtSeconds);

        redisService.set(EMAIL_CODE_PREFIX + email.emailAddress(), verifyCode, (long) EMAIL_CODE_EXPIRATION_MILLIS);


        mailService.sendEmail(createCodeMailForm(email.emailAddress(), expiredAt, verifyCode));
    }

    private String getEmailDomain(String email) {
        return email.split("@")[1];
    }

    private MimeMessage createCodeMailForm(String toEmail, LocalDateTime expiredAt, String verifyCode) throws MessagingException {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        MimeMessage message = mailService.createEmptyMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");

        Context context = new Context();
        context.setVariable("verifyCode", verifyCode);
        context.setVariable("expiredAt", expiredAt.format(formatter));

        String html = templateEngine.process("EmailVerificationForm", context);

        helper.setTo(toEmail);
        helper.setSubject("[Offispace] 인증번호를 안내해 드립니다.");
        helper.setText(html, true);
        helper.setFrom("zoomin3022@gmail.com");

        return message;
    }

    public boolean verifyEmailCode(VerifyRequestDto.EmailCode emailCode) {
        String codeInRedis = redisService.get(EMAIL_CODE_PREFIX + emailCode.emailAddress(), String.class)
                .orElseThrow(() -> new AuthException(EXPIRED_EMAIL_CODE));

        if (!codeInRedis.equals(emailCode.code())) {
            throw new AuthException(INVALID_EMAIL_CODE);
        }
        return true;
    }

    public void requestPhoneVerify(VerifyRequestDto.Phone phone) {
        if (memberRepository.existsByMemberPhone(phone.phoneNumber())) {
            throw new AuthException(PHONE_ALREADY_EXISTS);
        }
        String verificationCode = generateVerifyCode();

        redisService.set(PHONE_CODE_PREFIX + phone.phoneNumber(), verificationCode, (long) PHONE_CODE_EXPIRATION_MILLIS);

        smsService.sendSms(phone.phoneNumber(), createSmsVerificationText(verificationCode));
    }

    private String generateVerifyCode() {
        SecureRandom random = new SecureRandom();
        int randomNumber = random.nextInt(1000000);
        return String.format("%06d", randomNumber);
    }

    private Message createSmsVerificationText(String verificationCode) {
        Message message = new Message();

        StringBuilder sb = new StringBuilder();
        sb.append("[Offispace] 본인확인 인증번호\n");
        sb.append("[").append(verificationCode).append("]\n").append("를 입력해주세요\n");

        message.setText(sb.toString());

        return message;
    }

    public boolean verifyPhoneCode(VerifyRequestDto.PhoneCode phoneCode) {
        String codeInRedis = redisService.get(PHONE_CODE_PREFIX + phoneCode.phoneNumber(), String.class)
                .orElseThrow(() -> new AuthException(EXPIRED_PHONE_CODE));

        if (!codeInRedis.equals(phoneCode.code())) {
            throw new AuthException(INVALID_PHONE_CODE);
        }
        return true;
    }
}
