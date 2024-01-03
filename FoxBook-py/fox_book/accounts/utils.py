import random

from django.core.mail import EmailMultiAlternatives
from django.conf import settings
from django.template.loader import render_to_string
from django.utils.html import strip_tags

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

    html_message = render_to_string('email_template.html', {'user': user, 'otp_code': otp_code, 'purpose': purpose})

    plain_message = strip_tags(html_message)

    # відправка на пошту
    from_email = settings.EMAIL_HOST_USER
    to_email = [user.email]

    mail = EmailMultiAlternatives(subject, plain_message, from_email, to_email)
    mail.attach_alternative(html_message, "text/html")
    mail.send()
