import random

from django.core.mail import EmailMessage
from django.conf import settings

from .models import User, OneTimePassword, PasswordReset


def generate_otp():
    return ''.join(str(random.randint(0, 9)) for _ in range(6))


def send_code_to_user(email, create_code=True, otp_code=None, purpose='verification'):
    subject = "One time passcode"
    user = User.objects.get(email=email)

    if create_code:
        otp_code = generate_otp()

        if purpose == 'verification':
            OneTimePassword.objects.create(user=user, code=otp_code)
        elif purpose == 'password_reset':
            PasswordReset.objects.create(user=user, code=otp_code)

    email_body = f"Привіт {user.name}!\nБудь ласка, введіть код {otp_code} щоб підтвердити пошту."
    from_email = settings.DEFAULT_FROM_EMAIL

    send_email = EmailMessage(subject=subject, body=email_body, from_email=from_email, to=[email])
    send_email.send(fail_silently=True)