package com.connectly.Connectly_member_service.utils.emailValidation;

import java.util.regex.Pattern;


/*
* Local Part:
^[a-zA-Z0-9_+&*-]+: The local part starts with alphanumeric characters, underscores (_), plus (+), ampersand (&), asterisk (*), or hyphen (-).
(?:\.[a-zA-Z0-9_+&*-]+)*: Allows for additional segments separated by dots (.).
* Domain Part:
@(?:[a-zA-Z0-9-]+\.)+: The domain part starts with @ and includes alphanumeric characters and hyphens (-), followed by a dot (.).
[a-zA-Z]{2,7}$: The top-level domain (TLD) must be between 2 and 7 characters long (e.g., .com, .org, .co.uk).
*Anchors:
^ and $ ensure the entire string is matched, preventing partial matches.
*Case Insensitivity:
The regex itself is case-insensitive for the domain part, but you can still use Pattern.CASE_INSENSITIVE if needed.
*
* Note:
**Valid Emails:
user@example.com
user.name+tag+sorting@example.com
user@sub.example.com
user@123.123.123.123
user@[IPv6:2001:db8::1]
**Invalid Emails:
user@.com (missing domain)
user@com (missing TLD)
user@example..com (double dot)
user@example.c (TLD too short)
user@example.abcdefgh (TLD too long)
user@example,com (invalid character)
* */
public final class MailValidation {
    private static final Pattern EMAIL_REGEX = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    public static boolean isValidEmail(String email) {
        return EMAIL_REGEX.matcher(email).matches();
    }
}





