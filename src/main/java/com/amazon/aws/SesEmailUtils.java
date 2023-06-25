package com.amazon.aws;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.sesv2.SesV2AsyncClient;
import software.amazon.awssdk.services.sesv2.model.EmailContent;
import software.amazon.awssdk.services.sesv2.model.RawMessage;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;
import software.amazon.awssdk.services.sesv2.model.SendEmailResponse;

/**
 * Utility class with static methods to use Amazon Simple Email Services.
 * 
 * @see https://aws.amazon.com/ses/
 * @see https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/home.html
 * @see https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/sesv2/package-summary.html
 * 
 */
public class SesEmailUtils {
    /**
     * Internal logger
     */
    private static final Logger logger = LoggerFactory.getLogger(SesEmailUtils.class);

    /**
     * Create a new {@link SesV2AsyncClient} object with convenient defaults.
     * 
     * This is using Dafult region provider chain to select a region. To know hot to
     * set region see:
     * This is using default credentials providers chain. To know hot to set
     * credentials see:
     * {@link https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/region-selection.html}
     * and then find the seciont called "Default region provider chain" into.
     * {@link https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/credentials-chain.html}
     * 
     * @return New created client.
     */
    public static SesV2AsyncClient createAsyncClient() {
        logger.debug("createAsyncClient Started");
        // Create Async Client
        SesV2AsyncClient client = SesV2AsyncClient.builder().build();
        logger.debug("createAsyncClient Finished");
        return client;
    };

    /**
     * Build a new {@link Message} according to the given email parameters.
     * 
     * @param session     {@link Session} object to create the message. It could be
     *                    a new default session when we are using Amazon Simple
     *                    Email Service.
     * @param from        Email from address (origin)
     * @param to          Email to address (destination)
     * @param subject     Email subject
     * @param body        Email Body
     * @param attachments Attachment file path to be sent. In case you want to send
     *                    several, you need to provide several paths divided by OS
     *                    file separator. IN case you send null or empty String
     *                    there will not be attached files into the email message
     * @return new created {@link Message} object
     * @throws AddressException
     * @throws MessagingException
     */
    private static Message createMessage(Session session, String from, String to, String subject, String body,
            String attachments) throws AddressException, MessagingException {
        logger.debug("createMessage Started");
        // Create email message
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);

        // Create multipart message
        Multipart multipart = new MimeMultipart();

        // Create body part
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setText(body);

        // Add body to multipart
        multipart.addBodyPart(bodyPart);

        // If attachment given
        if (attachments != null && attachments.isEmpty() == false) {
            // Create attachment part
            MimeBodyPart attachmentPart = new MimeBodyPart();
            // Create attachment
            DataSource source = new FileDataSource(attachments);
            attachmentPart.setDataHandler(new DataHandler(source));
            attachmentPart.setFileName(source.getName());
            // Add attachment to multipart
            multipart.addBodyPart(attachmentPart);
        }

        // Add multipart to message
        message.setContent(multipart);
        logger.debug("createMessage Finished");
        return message;
    }

    /**
     * Send an email usin Amazon Simple Email Service and API V2 according to the
     * given parameters.
     * 
     * @param from        Email from address (origin)
     * @param to          Email to address (destination)
     * @param subject     Email subject
     * @param body        Email Body
     * @param attachments Attachment file path to be sent. In case you want to send
     *                    several, you need to provide several paths divided by OS
     *                    file separator. IN case you send null or empty String
     *                    there will not be attached files into the email message
     * @return {@link SendEmailResponse} with the response given by Amazon Simple
     *         Email Service.
     * @throws AddressException
     * @throws MessagingException
     * @throws IOException
     */
    public static SendEmailResponse sendEmail(SesV2AsyncClient client, String from, String to,
            String subject, String body,
            String attachments) throws AddressException, MessagingException, IOException {
        logger.debug("sendEmail Started");
        Session session = Session.getDefaultInstance(new Properties());
        Message message = createMessage(session, from, to, subject, body, attachments);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        message.writeTo(outputStream);
        byte[] messageByteArray = outputStream.toByteArray();
        outputStream.close();
        SdkBytes sdkBytes = SdkBytes.fromByteArray(messageByteArray);
        RawMessage rawMessage = RawMessage.builder().data(sdkBytes).build();
        EmailContent emailContent = EmailContent.builder().raw(rawMessage).build();
        SendEmailRequest sendEmailRequest = SendEmailRequest.builder().content(emailContent).build();
        logger.debug("sendEmail Finished");
        return client.sendEmail(sendEmailRequest).join();
    }
}
