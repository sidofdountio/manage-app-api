package com.sidof.manageApp.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED;

/**
 * <blockquote><pre>
 * Author   : @Dountio
 * LinkedIn : @SidofDountio
 * GitHub   : @SidofDountio
 * Version  : V1.0
 * Email    : sidofdountio406@gmail.com
 * Licence  : All Right Reserved SIDOF
 * Since    : 4/28/25
 * </blockquote></pre>
 */

@Service
@Slf4j
public class EmailService {
    private static final String PASSWORD_RESET_REQUEST = "Reset Password Request";
    private static final String NEW_USER_ACCOUNT_VERIFICATION = "New User Account Verification";
    String templateName;
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    @Value("${application.mailing.email}")
    private String setFrom;
    @Value("${application.front-end.activation-url}")
    private String activationUrl;
    @Value("${application.front-end.password-url}")
    private String changePassword;

    public EmailService(JavaMailSender mailSender, SpringTemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Async
    public void sendVerificationEmail(String email, String firstName, String activationCode) throws MessagingException {

        if (activationCode == null) {
            log.info("Activation code is {}", activationCode);
            throw new IllegalStateException(" Activation code is null");
        }
        templateName = "account";
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, MULTIPART_MODE_MIXED, UTF_8.name());
        Map<String, Object> model = new HashMap<>();
        model.put("activationCode", activationCode);
        model.put("url", activationUrl);
        model.put("firstName", firstName);
        Context context = new Context();
        context.setVariables(model);
        mimeMessageHelper.setFrom(setFrom);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
        String html = templateEngine.process(templateName, context);
        mimeMessageHelper.setText(html, true);
        mailSender.send(mimeMessage);

    }

    @Async
    public void sendPasswordResetEmail(String firstName, String email) throws MessagingException {
        try {
            templateName = "password";
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, MULTIPART_MODE_MIXED, UTF_8.name());
            Map<String, Object> model = new HashMap<>();
            model.put("url", changePassword);
            model.put("firstName", firstName);
            Context context = new Context();
            context.setVariables(model);
            mimeMessageHelper.setFrom(setFrom);
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject(PASSWORD_RESET_REQUEST);
            String html = templateEngine.process(templateName, context);
            mimeMessageHelper.setText(html, true);
            mailSender.send(mimeMessage);
        } catch (RuntimeException e) {
            log.error("Unable to send email");
            throw new MessagingException("Unable to send email");
        }
    }
}
