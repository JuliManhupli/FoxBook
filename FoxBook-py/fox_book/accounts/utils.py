import random

from django.core.mail import EmailMessage
from django.conf import settings

from .models import User, OneTimePassword


def generate_otp():
    return ''.join(str(random.randint(0, 9)) for _ in range(6))


def send_code_to_user(email):
    subject = "One time passcode for Email verification"
    otp_code = generate_otp()

    user = User.objects.get(email=email)
    email_body = f"Привіт {user.name}!\nБудь ласка, підтвердить вашу пошту за допомогою коду {otp_code}"
    from_email = settings.DEFAULT_FROM_EMAIL

    OneTimePassword.objects.create(user=user, code=otp_code)
    send_email = EmailMessage(subject=subject, body=email_body, from_email=from_email, to=[email])
    send_email.send(fail_silently=True)
