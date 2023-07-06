package com.amazon.aws;

/**
 * Class to contain all SMTP settings needed to connect to Simple Email
 * Services.
 */
public class SmtpSettings {
    private String host;
    private int port;
    private String userName;
    private String password;

    /**
     * Default constructor with the mandatory parameters.
     * 
     * @param host     Host name to connect to.
     * @param port     Port number to connect to.
     * @param userName User name to use to connect.
     * @param password Password to use to connect.
     */
    public SmtpSettings(String host, int port, String userName, String password) {
        this.host = host;
        this.port = port;
        this.setUserName(userName);
        this.password = password;
    }

    /**
     * Get the host name.
     * 
     * @return
     */
    public String getHost() {
        return host;
    }

    /**
     * Set the host name.
     * 
     * @param host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Get the port number.
     * 
     * @return
     */
    public int getPort() {
        return port;
    }

    /**
     * Set the port number.
     * 
     * @param port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Get the user name.
     * 
     * @return
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Set the user name.
     * 
     * @param userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Get the password.
     * 
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the password.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
