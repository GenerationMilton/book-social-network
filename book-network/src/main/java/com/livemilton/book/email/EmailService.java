package src.main.java.com.livemilton.book.email;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.spring6.SpringTemplateEngine;
import jakarta.mail.internet;


import javax.naming.Context;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    //something to be added
    @Async
    public void sendEmail(
            String to,
            String username,
            EmailTemplateName emailTemplate,
            String confirmationUrl,
            String activationCode,
            String subject
    ) throws MessagingException{
        String templateName;
        if(emailTemplate ==null){
            templateName = "confirm-email";
        }else {
            templateName = emailTemplate.name();
        }
        //configure emailSender
        MimeMessage mimeMessage= mailSender.createMimeMessage();
        MimeMessageHelper helper= new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                StandardCharsets.UTF_8.name()
        );
        //properties
        Map<String, Object> properties = new HashMap<>();
        properties.put("username",username);
        properties.put("confirmationUrl", confirmationUrl);
        properties.put("activation_code", activationCode);

        //context
        Context contex = new Contex();
        contex.setVariables(properties);

        //helper properties
        helper.setFrom("milton.munozr@outlook.com");
        helper.setTo(to);
        helper.setSubject(subject);

        //template
        String template = templateName.process(templateName.context);

        helper.setText(template, true);

        mailSender.send(mimeMessage);
    }

}