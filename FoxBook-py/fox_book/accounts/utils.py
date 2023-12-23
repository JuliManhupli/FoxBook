import random

from django.core.mail import send_mail
from django.conf import settings

from .models import User, OneTimePassword, PasswordReset


def generate_otp():
    return ''.join(str(random.randint(0, 9)) for _ in range(6))


def send_code_to_user(email, create_code=True, otp_code=None, purpose='verification'):
    subject = "Підтвердження електронної пошти FoxBook"
    user = User.objects.get(email=email)

    if create_code:
        otp_code = generate_otp()

        if purpose == 'verification':
            OneTimePassword.objects.create(user=user, code=otp_code)
        elif purpose == 'password_reset':
            subject = "Код для скидання паролю FoxBook"
            PasswordReset.objects.create(user=user, code=otp_code)

    email_body = f"Привіт {user.name}!\nБудь ласка, введіть код {otp_code}, щоб підтвердити пошту."
    from_email = settings.EMAIL_HOST_USER

    send_mail(subject, email_body, from_email, [user], fail_silently=False)
