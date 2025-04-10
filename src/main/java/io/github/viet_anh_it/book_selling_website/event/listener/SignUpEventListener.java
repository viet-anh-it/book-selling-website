package io.github.viet_anh_it.book_selling_website.event.listener;

import java.time.Instant;
import java.util.UUID;

import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import io.github.viet_anh_it.book_selling_website.enums.TokenTypeEnum;
import io.github.viet_anh_it.book_selling_website.event.SignUpEvent;
import io.github.viet_anh_it.book_selling_website.model.User;
import io.github.viet_anh_it.book_selling_website.model.VerificationToken;
import io.github.viet_anh_it.book_selling_website.service.UserService;
import io.github.viet_anh_it.book_selling_website.service.VerificationTokenService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SignUpEventListener implements ApplicationListener<SignUpEvent> {

        UserService userService;
        JavaMailSender javaMailSender;
        VerificationTokenService verificationTokenService;

        @Async
        @Override
        @SuppressWarnings("null")
        public void onApplicationEvent(SignUpEvent event) {
                User user = this.userService.findByEmail(event.getUserEmail()).get();
                String accountRegistrationVerificationTokenString = UUID.randomUUID().toString();
                VerificationToken verificationTokenEntity = VerificationToken
                                .builder()
                                .tokenValue(accountRegistrationVerificationTokenString)
                                .expiresAt(Instant.now().plusSeconds(5 * 60))
                                .type(TokenTypeEnum.REGISTRATION_CONFIRMATION)
                                .user(user)
                                .build();
                this.verificationTokenService.save(verificationTokenEntity);

                String receiverEmailAddress = event.getUserEmail();
                String emailSubject = "Account Registration Confirmation Email";
                String accountRegistrationConfirmationLink = event.getAppContextPath()
                                + "/confirm-registration?accountRegistrationVerificationTokenString="
                                + accountRegistrationVerificationTokenString;

                SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
                simpleMailMessage.setTo(receiverEmailAddress);
                simpleMailMessage.setSubject(emailSubject);
                simpleMailMessage.setText("Click this link to activate your account: http://localhost:8080"
                                + accountRegistrationConfirmationLink);
                this.javaMailSender.send(simpleMailMessage);
        }

}
