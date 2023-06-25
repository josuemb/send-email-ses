package com.amazon.aws;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;

import javax.mail.MessagingException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.services.sesv2.SesV2AsyncClient;

/**
 * Application class
 */
public class App {
        private static final Logger logger = LoggerFactory.getLogger(App.class);

        /**
         * send-ses-email Command main method.
         * 
         * @param args Command line arguments @see {@link #getEmailInfo(String[])} for
         *             more details.
         */
        public static void main(String[] args) {                
                MeasureDuration md = new MeasureDuration();
                SendEmailInfo emailInfo;
                try {
                        emailInfo = getEmailInfo(args);
                } catch (IllegalArgumentException e) {
                        System.exit(1);
                        return;
                }
                logger.info("Command Started");
                String processSendEmail = "SendEmail";
                md.start(processSendEmail);
                logger.info("Sending {} emails...", emailInfo.getRepetitions());
                sendEmail(emailInfo);
                md.finish(processSendEmail);
                Duration totalDuration = md.getDuration(processSendEmail);
                String totalDurationStr = md.getDurationString(processSendEmail);
                Duration avgDuration = totalDuration.dividedBy(emailInfo.getRepetitions());
                String strAvgDuration = DurationFormatter.getDurationString(avgDuration);
                String durationSecondsStr = String.format("%d.%03d", totalDuration.toSeconds(),
                                totalDuration.toMillisPart());
                float durationSeconds = 0;
                try {
                        durationSeconds = Float.parseFloat(durationSecondsStr);
                } catch (NullPointerException | NumberFormatException e) {
                        logger.error("Error converting duration in seconds to float", e);
                }
                float speedEmailsPerSecond = emailInfo.getRepetitions() / durationSeconds;
                String AvgSpeed = String.format("%.3f", speedEmailsPerSecond);
                logger.info("Process: {} finished.", processSendEmail);
                logger.info("Emails sent: {}.", emailInfo.getRepetitions());
                logger.info("Total duration: {} (H:MM:SS.MS)", totalDurationStr);
                logger.info("Average duration: {} (H:MM:SS.MS)", strAvgDuration);
                logger.info("Average speed: {} (emails/second)", AvgSpeed);
                md.clear();
                logger.info("Command Finished");
        }

        /**
         * Read command line arguments and trform them into a SendEmailInfo object
         * 
         * @param args Command line arguments
         * @return new object of type {@link com.amazon.aws.SendEmailInfo} with
         *         all data to send
         *         the email
         * @throws IllegalArgumentException
         */
        private static SendEmailInfo getEmailInfo(String[] args) throws IllegalArgumentException {
                logger.debug("getEmailInfo Started");
                // Command line options
                Options options = new Options();

                Option fromOption = Option.builder("f").longOpt("from").argName("from").hasArg().required(true)
                                .desc("From email address").build();
                options.addOption(fromOption);

                Option toOption = Option.builder("t").longOpt("to").argName("to").hasArg().required(true)
                                .desc("To email address").build();
                options.addOption(toOption);

                Option subjectOption = Option.builder("s").longOpt("subject").argName("subject").hasArg()
                                .required(false)
                                .desc("Email subject").build();
                options.addOption(subjectOption);

                Option bodyOption = Option.builder("b").longOpt("body").argName("body").hasArg().required(false)
                                .desc("Email body").build();
                options.addOption(bodyOption);

                Option attachmentsOption = Option.builder("a").longOpt("attachments").argName("attachments").hasArg()
                                .required(false)
                                .desc("Email attachement(s)").build();
                options.addOption(attachmentsOption);

                Option repetitionsOption = Option.builder("r").longOpt("repetitions").argName("repetitions").hasArg()
                                .required(false)
                                .desc("Number of repetitions (emails to be sent)").build();
                options.addOption(repetitionsOption);

                CommandLineParser parser = new DefaultParser();
                HelpFormatter formatter = new HelpFormatter();

                CommandLine commandLine;
                try {
                        commandLine = parser.parse(options, args);
                } catch (ParseException e) {
                        System.out.println(e.getMessage());
                        formatter.printHelp("send-email", options);
                        throw new IllegalArgumentException();
                }

                String from = commandLine.getOptionValue("from");
                String to = commandLine.getOptionValue("to");
                String subject = commandLine.getOptionValue("subject", "Test email");
                String body = Optional.ofNullable(commandLine.getOptionValue("body"))
                                .orElse("This is just a testing email. Do you received it?");
                String attachment = commandLine.getOptionValue("attachments");
                SendEmailInfo sendEmailInfo = new SendEmailInfo(from, to, subject, body);
                sendEmailInfo.setAttachments(attachment);
                String strRepetitions = commandLine.getOptionValue("repetitions", "1");
                try {
                        int repetitions = Integer.parseInt(strRepetitions);
                        sendEmailInfo.setRepetitions(repetitions);
                } catch (NumberFormatException e) {
                        sendEmailInfo.setRepetitions(1);
                }
                logger.debug("getEmailInfo Finished");
                return sendEmailInfo;
        }

        /**
         * Send the email n times according to the comand line parameters.
         * 
         * @param sendEmailInfo Information got from command line and covnerted into a
         *                      {@link com.amazon.aws.SendEmailInfo} object
         * @return true en case everything goes well, false in case an error ocurred
         */
        private static boolean sendEmail(SendEmailInfo sendEmailInfo) {
                logger.debug("sendEmail Started");
                boolean emailSentOk = false;
                SesV2AsyncClient client = SesEmailUtils.createAsyncClient();
                Flux.range(1, sendEmailInfo.getRepetitions())
                                .flatMap(index -> {
                                        try {
                                                return Mono.just(SesEmailUtils.sendEmail(client,
                                                                sendEmailInfo.getFrom(),
                                                                sendEmailInfo.getTo(), sendEmailInfo.getSubject(),
                                                                sendEmailInfo.getBody(),
                                                                sendEmailInfo.getAttachments()));
                                        } catch (MessagingException | IOException e) {
                                                logger.error("Error sending email", e);
                                        }
                                        return null;
                                })
                                .parallel()
                                .runOn(Schedulers.parallel())
                                .subscribe(response -> logger.debug(response.toString()));
                emailSentOk = true;
                logger.debug("sendEmail Finished");
                return emailSentOk;
        }
}