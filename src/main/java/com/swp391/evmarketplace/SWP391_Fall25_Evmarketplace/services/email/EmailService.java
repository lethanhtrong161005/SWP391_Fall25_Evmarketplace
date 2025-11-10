package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.email;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.config.EmailProperties;
import io.micrometer.common.lang.Nullable;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.activation.DataSource;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;
    private final EmailProperties emailProperties;
    private final TemplateEngine templateEngine;

    public void sendHtmlEmail(
            String to, String subject, String html,
            @Nullable String textFallback,
            @Nullable String[] cc, @Nullable String[] bcc,
            @Nullable String replyTo,
            @Nullable DataSource attachment, @Nullable String attachmentName
    ) {
        try{
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, StandardCharsets.UTF_8.name());
            helper.setFrom(new InternetAddress(emailProperties.getFrom(), emailProperties.getFromName(), StandardCharsets.UTF_8.name()));
            helper.setTo(to);
            if (cc != null && cc.length > 0) helper.setCc(cc);
            if (bcc != null && bcc.length > 0) helper.setBcc(bcc);
            if (replyTo != null && !replyTo.isBlank()) helper.setReplyTo(replyTo);
            helper.setSubject(subject);

            if (textFallback != null && !textFallback.isBlank()) {
                helper.setText(textFallback, html);
            } else {
                helper.setText(html, true);
            }

            if (attachment != null && attachmentName != null) {
                helper.addAttachment(attachmentName, attachment);
            }

            mailSender.send(msg);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void sendHtmlEmail(String to, String subject, String html) {
        sendHtmlEmail(to, subject, html, null, null, null, null, null, null);
    }

    public void sendOtpEmail(String to, String otp) {
        int expiresMinutes = 5;
        String brand = emailProperties.getFromName() != null ? emailProperties.getFromName() : "ReEV";

        Context ctx = new Context(Locale.getDefault());
        ctx.setVariable("brand", brand);
        ctx.setVariable("otp", otp);
        ctx.setVariable("expiresMinutes", expiresMinutes);


        String html = templateEngine.process("mail/email-otp", ctx);

        String text = "Your " + brand + " OTP is: " + otp + "\n"
                + "This code expires in " + expiresMinutes + " minutes.\n"
                + "If you didn’t request this code, ignore this email.";

        sendHtmlEmail(to, "ReEV OTP Verification", html, text, null, null, null, null, null);
    }


}
