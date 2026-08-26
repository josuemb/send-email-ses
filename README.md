# send-email-ses

A command-line tool to send email through [Amazon Simple Email Service (SES)](https://aws.amazon.com/ses/),
built with the [AWS SDK for Java v2](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/sesv2/package-summary.html).
It supports both the **SES API** and **SMTP**, plain or with attachments, and can
send in batches for quick testing.

## Requirements

- **JDK 25+** — [Amazon Corretto](https://aws.amazon.com/corretto/) recommended.
- [Apache Maven](https://maven.apache.org/).
- An AWS account with a verified SES identity (see [Set up Amazon SES](#set-up-amazon-ses) below).

## Quick start

```bash
mvn verify                                    # build the fat jar -> target/send-email-ses.jar
export AWS_REGION=us-east-1                    # your SES region + IAM credentials
export AWS_ACCESS_KEY_ID=<your-access-key-id>
export AWS_SECRET_ACCESS_KEY=<your-secret-access-key>
java -jar target/send-email-ses.jar --from sender@example.com --to recipient@example.com
```

## Build

```bash
mvn verify
```

> Why `mvn verify` and not `mvn clean package`? See
> [Maven: verify or clean install](https://andresalmiray.com/maven-verify-or-clean-install/)
> by [@aalmiray](https://twitter.com/aalmiray).

The build produces a **fat jar** (all dependencies bundled) at
`target/send-email-ses.jar`, runnable directly from the command line:

- Linux/macOS: `java -jar ./target/send-email-ses.jar`
- Windows: `java -jar .\target\send-email-ses.jar`

Run it with no required options to see the help:

```
usage: send-email
 -a,--attachments <attachments>   Email attachment(s)
 -b,--body <body>                 Email body
 -f,--from <from>                 From email address
 -p,--protocol <protocol>         Protocol to send email (SMTP | API). Default: API.
 -r,--repetitions <repetitions>   Number of repetitions (emails to send)
 -s,--subject <subject>           Email subject
 -t,--to <to>                     To email address
```

## Set up Amazon SES

1. Have (or [create](https://portal.aws.amazon.com/billing/signup)) an AWS account.
2. [Set up SES](https://docs.aws.amazon.com/ses/latest/dg/setting-up.html) in your account.
3. [Create and verify a sending identity](https://docs.aws.amazon.com/ses/latest/dg/creating-identities.html).
4. To send to arbitrary addresses, [move SES out of the sandbox](https://docs.aws.amazon.com/ses/latest/dg/request-production-access.html). Otherwise use the [mailbox simulator](https://docs.aws.amazon.com/ses/latest/dg/send-an-email-from-console.html#send-email-simulator).
5. [Create an IAM user](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_users_create.html) without console access.
6. [Create an access key](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_credentials_access-keys.html) for that user; keep the key id, secret, and region.
7. Attach a policy granting **`ses:SendRawEmail`** (required by this tool) to the user.

## Configure credentials

Set the environment variables for your chosen protocol. **Use your own values — the examples below are placeholders.**

**API protocol:**

| Variable | Example |
|---|---|
| `AWS_REGION` | `us-east-1` |
| `AWS_ACCESS_KEY_ID` | `<your-access-key-id>` |
| `AWS_SECRET_ACCESS_KEY` | `<your-secret-access-key>` |

**SMTP protocol:**

| Variable | Example |
|---|---|
| `SMTP_HOST` | `email-smtp.us-east-1.amazonaws.com` |
| `SMTP_PORT` | `587` |
| `SMTP_USERNAME` | `<your-smtp-username>` |
| `SMTP_PASSWORD` | `<your-smtp-password>` |

## Usage examples

```bash
# Default subject and body
java -jar send-email-ses.jar --from sender@example.com --to recipient@example.com

# Custom subject and body
java -jar send-email-ses.jar --from sender@example.com --to recipient@example.com \
  --subject "Lorem Ipsum" --body "Lorem ipsum dolor sit amet..."

# With an attachment
java -jar send-email-ses.jar --from sender@example.com --to recipient@example.com \
  --subject "Lorem Ipsum" --body "Lorem ipsum..." --attachments "/path/to/file.pdf"

# Send 10 emails
java -jar send-email-ses.jar --from sender@example.com --to recipient@example.com --repetitions 10
```

## Output

Each run prints sending statistics:

```
INFO com.amazon.aws.App - Sending 1 emails...
INFO com.amazon.aws.App - Protocol used: SMTP.
INFO com.amazon.aws.App - Emails sent: 1.
INFO com.amazon.aws.App - Total duration: 0:00:02.226 (H:MM:SS.MS)
INFO com.amazon.aws.App - Average speed: 0.449 (emails/second)
```
