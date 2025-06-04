package io.github.viet_anh_it.book_selling_website.service;

import org.springframework.mail.MailException;
import org.thymeleaf.context.Context;

import io.github.viet_anh_it.book_selling_website.enums.OrderStatusEnum;
import io.github.viet_anh_it.book_selling_website.model.User;

public interface EmailService {
    void sendSimpleEmail(String receiverEmailAddress, String emailHeading, String emailContent) throws MailException;

    void sendEmailWithHtmlContent(String receiverEmailAddress, String emailHeading, String htmlContent);

    void sendEmailTemplate(String receiverEmailAddress, String emailHeading, String templateName,
            Context thymeLeafContext);

    void sendAccountActivationEmailTemplate(User user);

    void sendForgotPasswordEmailTemplate(User user);

    void sendOrderApprovalEmail(User user);

    void sendOrderRejectionEmail(User user);

    void sendUpdateOrderStatusEmail(User user, OrderStatusEnum orderStatusEnum);
}
