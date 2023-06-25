package com.amazon.aws;

/**
 * Stores the process to send the email.
 */
public class SendEmailInfo {
    private String from;
    private String to;
    private String subject;
    private String body;
    private String attachments;
    private int repetitions = 1;

    /**
     * Create a new object with the mandatory parameters
     * 
     * @param from    Origin email address
     * @param to      Destination email adress(es)
     * @param subject Email subject
     * @param body    Email body
     */
    public SendEmailInfo(String from, String to, String subject, String body) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.body = body;
    }

    /**
     * Get from email address
     * 
     * @return From email address
     */
    public String getFrom() {
        return from;
    }

    /**
     * Set from email address
     * 
     * @param from From email address
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * Get to email address
     * 
     * @return To email address
     */
    public String getTo() {
        return to;
    }

    /**
     * Set to email address
     * 
     * @param to To email address
     */
    public void setTo(String to) {
        this.to = to;
    }

    /**
     * Get email subject
     * 
     * @return Email subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Set email subject
     * 
     * @param subject Email subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Get emil body
     * 
     * @return Email body
     */
    public String getBody() {
        return body;
    }

    /**
     * Set email body
     * 
     * @param body Email body
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Get attachment(s) path(s). In case there are more than one, they are
     * separated by System path separator.
     * 
     * @return Attachment(s)
     */
    public String getAttachments() {
        return attachments;
    }

    /**
     * Get attachment(s) path(s).
     * 
     * @param attachmentPath Attachment(s) path(s). In case there are more than one,
     *                       they are separated by System path separator.
     */
    public void setAttachments(String attachmentPath) {
        this.attachments = attachmentPath;
    }

    /**
     * Get number of repetitions to send emails (numbers of emails to be sent)
     * 
     * @return Repetitions
     */
    public int getRepetitions() {
        return repetitions;
    }

    /**
     * Set number of repetitions to send emails (numbers of emails to be sent)
     * 
     * @param repetitions Numbers of emails to be sent
     */
    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }
}
