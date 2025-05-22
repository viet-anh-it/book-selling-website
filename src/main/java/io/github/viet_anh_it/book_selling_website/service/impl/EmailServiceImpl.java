package io.github.viet_anh_it.book_selling_website.service.impl;

import java.nio.charset.StandardCharsets;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import io.github.viet_anh_it.book_selling_website.enums.TokenTypeEnum;
import io.github.viet_anh_it.book_selling_website.model.User;
import io.github.viet_anh_it.book_selling_website.model.VerificationToken;
import io.github.viet_anh_it.book_selling_website.service.EmailService;
import io.github.viet_anh_it.book_selling_website.service.VerificationTokenService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailServiceImpl implements EmailService {

    JavaMailSender javaMailSender;
    TemplateEngine templateEngine;
    VerificationTokenService verificationTokenService;

    @Async
    @Override
    public void sendSimpleEmail(String receiverEmailAddress, String emailHeading, String emailContent)
            throws MailException {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(receiverEmailAddress);
        simpleMailMessage.setSubject(emailHeading);
        simpleMailMessage.setText(emailContent);
        this.javaMailSender.send(simpleMailMessage);
    }

    @Async
    @Override
    public void sendEmailTemplate(String receiverEmailAddress, String emailHeading, String templateName,
            Context thymeLeafContext) {
        try {
            MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());
            mimeMessageHelper.setTo(receiverEmailAddress);
            mimeMessageHelper.setSubject(emailHeading);
            String htmlEmailContent = this.templateEngine.process(templateName, thymeLeafContext);
            mimeMessageHelper.setText(htmlEmailContent, true);
            this.javaMailSender.send(mimeMessage);
        } catch (MessagingException exception) {
            throw new MailSendException(exception.getMessage(), exception);
        }
    }

    @Async
    @Override
    public void sendEmailWithHtmlContent(String receiverEmailAddress, String emailHeading, String htmlContent) {
        try {
            MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());
            mimeMessageHelper.setTo(receiverEmailAddress);
            mimeMessageHelper.setSubject(emailHeading);
            mimeMessageHelper.setText(htmlContent, true);
            this.javaMailSender.send(mimeMessage);
        } catch (MessagingException exception) {
            throw new MailSendException(exception.getMessage(), exception);
        }
    }

    @Async
    @Override
    public void sendAccountActivationEmailTemplate(User user) {
        VerificationToken accountActivationToken = this.verificationTokenService
                .createVerificationTokenForUser(user, TokenTypeEnum.ACTIVATE_ACCOUNT, 10 * 60);
        this.verificationTokenService.save(accountActivationToken);
        String accountActivationLink = "http://localhost:8080/activateAccount?token="
                + accountActivationToken.getTokenValue();
        Context thymeLeafContext = new Context();
        thymeLeafContext.setVariable("accountActivationLink", accountActivationLink);
        this.sendEmailTemplate(user.getEmail(), "Kích hoạt tài khoản của bạn",
                "accountActivationEmail", thymeLeafContext);
    }

    @Async
    @Override
    public void sendForgotPasswordEmailTemplate(User user) {
        VerificationToken forgotPasswordToken = this.verificationTokenService
                .createVerificationTokenForUser(user, TokenTypeEnum.FORGOT_PASSWORD, 10 * 60);
        this.verificationTokenService.save(forgotPasswordToken);
        String forgotPasswordLink = "http://localhost:8080/forgotPassword?token="
                + forgotPasswordToken.getTokenValue();
        Context thymeLeafContext = new Context();
        thymeLeafContext.setVariable("forgotPasswordLink", forgotPasswordLink);
        this.sendEmailTemplate(user.getEmail(), "Quên mật khẩu",
                "forgotPasswordEmail", thymeLeafContext);
    }

}
