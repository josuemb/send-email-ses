# send-email-ses
This is a demo to send email with [Amazon Simple Email Service](https://aws.amazon.com/ses/) API V2 and [AWS JDK V2](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/sesv2/package-summary.html).

# Compilation
## Prerequisites
To compile send-email-ses you will ned:
- JDK ver 8.0+ (Java 19 is recommended).
- [Apache Maven](https://maven.apache.org/).
## How to compile it
To compile send-email-ses you will need to execute next maven command:

<code>mvn verify</code></br>

Wait a minute...

Why did not just execute

<code>mvn clean package</code>?

See (https://andresalmiray.com/maven-verify-or-clean-install/) from [@aalmiray](https://twitter.com/aalmiray).

Once compilates successfully you can use the fat jar (a jar file with all dependencies included) like a command line tool to test by sending emails using  [Amazon Simple Email Service](https://aws.amazon.com/ses/).

The fat jar will be located in the target directory inside the main project folder ant it could be executed like.
- <code>java -jar ./target/send-email-ses.jar</code> (in Linux/MacOs)
- <code>java -jar .\target\send-email-ses.jar</code> (in Windows)

And it will show the coomand help, like this:

<code>
PS C:\Temp\send-email-ses> java -jar .\target\send-email-ses.jar</br>
Missing required options: f, t</br>
usage: send-email</br>
 -a,--attachments <attachments>   Email attachement(s)</br>
 -b,--body <body>                 Email body</br>
 -f,--from <from>                 From email address</br>
 -r,--repetitions <repetitions>   Number of repetitions (emails to be
                                  sent)</br>
 -s,--subject <subject>           Email subject</br>
 -t,--to <to>                     To email address</br>
</code>

## Creating and configuring [Amazon Simple Email Service](https://aws.amazon.com/ses/)
In order create and get [Amazon Simple Email Service](https://aws.amazon.com/ses/)  ready to be used you will need to do the next steps:
1. Having a valid AWS Account or acreate a new one. See: https://portal.aws.amazon.com/billing/signup.
2. Set up a new [Amazon Simple Email Service](https://aws.amazon.com/ses/) in your account. See: https://docs.aws.amazon.com/ses/latest/dg/setting-up.html or have a oreviously created one.
3. Create and Verify [Amazon Simple Email Service](https://aws.amazon.com/ses/) Identity: https://docs.aws.amazon.com/ses/latest/dg/creating-identities.html.
4. In case you want to send emails to any email address you need to move [Amazon Simple Email Service](https://aws.amazon.com/ses/) out of the sanbox (production mode). See: https://docs.aws.amazon.com/ses/latest/dg/request-production-access.html. Otherwise, you can just test sending emails using the simulator, like this: https://docs.aws.amazon.com/ses/latest/dg/send-an-email-from-console.html#send-email-simulator.
5. Create a new [https://docs.aws.amazon.com/iam/index.html](IAM) user without console access: https://docs.aws.amazon.com/IAM/latest/UserGuide/id_users_create.html.
6. Create a new access key for the recently created user: https://docs.aws.amazon.com/IAM/latest/UserGuide/id_credentials_access-keys.html and keep the access key and password as well as the region you created it, you will need them to send emails later.
7. Create a sending authorization policy and assign it to the recently created user. Make sure to enable <code>ses:SendRawEmail</code> permission that is needed to use this demo. Note: To assign this IAM policy you will need the recently created user ARN. See: https://docs.aws.amazon.com/IAM/latest/UserGuide/id_users.html#id_users_create_aws-identifiers.

## Configure send-email-ses to connect to [Amazon Simple Email Service](https://aws.amazon.com/ses/)
To enable send-email-ses to send emails using your [Amazon Simple Email Service](https://aws.amazon.com/ses/) verified identity you need to create the next environment varibles:
1. <code>AWS_REGION</code> (set the region were your created the [Amazon Simple Email Service](https://aws.amazon.com/ses/) verified identity). </br>Example value: <code>us-east-1</code>.
2. <code>AWS_ACCESS_KEY_ID</code> (set the access key of your IAM credentials created to use the service). </br>Example value: <code>FKGRTI6GFKXGT6TMVJ5E</code>
3. <code>AWS_SECRET_ACCESS_KEY</code> (Set the secret of your IAM credentials created to use the service). </br>Example value: <code>YOhg+FzepvjtftLNJGUlP4IXkwSkAr+ZQmMgCC4RE</code>.

## Use send-email-ses to send test emails

### To send an email with default subject and content
<code>java -jar send-email-ses.jar --from sender@mail.contoso.com --to email@provider.com</code>.</br>

### To send an email with custom subject and content
<code>java -jar send-email-ses.jar --from sender@mail.contoso.com --to email@provider.com '' --subject "Lorem Ipsum" --body "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."</code>.</br>

### To send an email with custom subject and content and an attachment
<code>java -jar send-email-ses.jar --from sender@mail.contoso.com --to email@provider.com '' --subject "Lorem Ipsum" --body "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua." --attachments "/home/johndoe/lorem-ipsum.pdf"</code>.</br>

### To send 10 emails with default subject and content
<code>java -jar send-email-ses.jar --from sender@mail.contoso.com --to email@provider.com --repetitions 10</code>.</br>

### Summary of sent emails
The execution of send-email-ses will show you some useful statistics like this:</br>
<code>
[main] INFO com.amazon.aws.App - Emails sent: 3.</br>
[main] INFO com.amazon.aws.App - Total duration: 0:00:02.824 (H:MM:SS.MS)</br>
[main] INFO com.amazon.aws.App - Average duration: 0:00:00.941 (H:MM:SS.MS)</br>
[main] INFO com.amazon.aws.App - Average speed: 1.062 (emails/second)</br>
[main] INFO com.amazon.aws.App - Command Finished</br>
</code>