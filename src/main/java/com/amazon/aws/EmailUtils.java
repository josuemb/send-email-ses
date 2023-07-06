package com.amazon.aws;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
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
public class EmailUtils {
    /**
     * Internal logger
     */
    private static final Logger logger = LoggerFactory.getLogger(EmailUtils.class);

    private static final String ENV_SMTP_HOST = "SMTP_HOST";
    private static final String ENV_SMTP_PORT = "SMTP_PORT";
    private static final String ENV_SMTP_USERNAME = "SMTP_USERNAME";
    private static final String ENV_SMTP_PASSWORD = "SMTP_PASSWORD";

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
    public static SesV2AsyncClient createV2AsyncClient() {
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
    public static SendEmailResponse sendEmailWithApi(SesV2AsyncClient client, String from, String to,
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

    /**
     * Create SMTP session with Simple Email Service service and API V2 using
     * STARTTLS
     * 
     * @return New created {@link Session} object.
     */
    public static Session createSmtpSession() {
        logger.debug("createSmtpSession Started");
        Session session = null;
        // Get SMTP settings from environment variables
        SmtpSettings settings = getSmtpSettings();
        if (settings == null) {
            logger.error("Cannot get SMTP Settings");
            return session;
        }
        // Setup JavaMail to use Amazon SES
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", settings.getHost());
        props.put("mail.smtp.port", settings.getPort());

        // Create a new authenticated instance
        session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(settings.getUserName(),
                        settings.getPassword());
            }
        });

        logger.debug("createSmtpSession Finished");
        return session;
    }

    /**
     * Get SMTP settings from environment variables.
     * 
     * @see {@link https://docs.aws.amazon.com/ses/latest/DeveloperGuide/smtp-credentials.html}
     * @return New object with SMT settings. In case of error it will return null.
     */
    private static SmtpSettings getSmtpSettings() {
        logger.debug("sendEmail Started");
        SmtpSettings settings = null;
        String host = null;
        String strPort = null;
        int port = 0;
        String userName = null;
        String password = null;
        boolean settingsError = false;

        try {
            host = System.getenv(ENV_SMTP_HOST);
            if (host == null || host.trim().isEmpty()) {
                settingsError = true;
                logger.error(String.format("La variable de ambiente %s no existe o está vacía", ENV_SMTP_HOST));
            }
            strPort = System.getenv(ENV_SMTP_PORT);
            if (strPort == null || strPort.trim().isEmpty()) {
                settingsError = true;
                logger.error(String.format("La variable de ambiente %s no existe o está vacía", ENV_SMTP_PORT));
            }
            try {
                port = Integer.valueOf(strPort);
            } catch (NumberFormatException e) {
                settingsError = true;
                logger.error(String.format("La variable de ambiente %s no es un valor entero", ENV_SMTP_PORT));
            }
            userName = System.getenv(ENV_SMTP_USERNAME);
            if (userName == null || userName.trim().isEmpty()) {
                settingsError = true;
                logger.error(String.format("La variable de ambiente %s no existe o está vacía", ENV_SMTP_USERNAME));
            }
            password = System.getenv(ENV_SMTP_PASSWORD);
            if (strPort == null || strPort.trim().isEmpty()) {
                settingsError = true;
                logger.error(String.format("La variable de ambiente %s no existe o está vacía", ENV_SMTP_PORT));
            }
        } catch (NullPointerException | SecurityException e) {
            logger.error("Error reading SMTP settings from environment variables", e);
            return settings;
        }
        if (settingsError == false) {
            settings = new SmtpSettings(host, port, userName, password);
        }
        logger.debug("sendEmail Finished");
        return settings;
    }

    /**
     * Send email to Simple Email Service using SMTP and Java Mail API.
     * 
     * @param session     Object with previously opened session to be reused by all
     *                    the calls.
     * @param from        Email from address (origin)
     * @param to          Email to address (destination)
     * @param subject     Email subject
     * @param body        Email Body
     * @param attachments Attachment file path to be sent. In case you want to send
     *                    several, you need to provide several paths divided by OS
     *                    file separator. IN case you send null or empty String
     *                    there will not be attached files into the email message
     * @return true if email was sent successfully, false otherwise.
     * @throws AddressException
     * @throws MessagingException
     */
    public static boolean sendEmailWithSmtp(Session session, String from, String to, String subject, String body,
            String attachments) throws AddressException, MessagingException {
        logger.debug("sendEmail Started");
        boolean emailSentOk = false;
        Message message = null;
        message = createMessage(session, from, to, subject, body, attachments);
        Transport.send(message);
        emailSentOk = true;
        logger.debug("sendEmail Finished");
        return emailSentOk;
    }
}
