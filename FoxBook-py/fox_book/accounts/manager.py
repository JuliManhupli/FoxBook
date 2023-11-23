from django.contrib.auth.models import BaseUserManager
from django.core.exceptions import ValidationError
from django.core.validators import validate_email
from django.utils.translation import gettext_lazy


class UserManager(BaseUserManager):
    def email_validator(self, email):
        try:
            validate_email(email)
        except ValidationError:
            raise ValueError(gettext_lazy("Please enter a valid email address"))

    def create_user(self, email, name, password, **extra_fields):
        if email:
            email = self.normalize_email(email)
            self.email_validator(email)
        else:
            raise ValueError(gettext_lazy("Email address is required"))
        if not name:
            raise ValueError(gettext_lazy("Name is required"))
        user = self.model(email=email, name=name, **extra_fields)
        user.set_password(password)
        user.save(using=self._db)
        return user

    def create_superuser(self, email, name, password, **extra_fields):
        extra_fields.setdefault('is_staff', True)
        extra_fields.setdefault('is_superuser', True)
        extra_fields.setdefault('is_verified', True)

        if extra_fields.get('is_staff') is not True:
            raise ValueError(gettext_lazy("Is_staff must be true for admin user"))

        if extra_fields.get('is_superuser') is not True:
            raise ValueError(gettext_lazy("Is_superuser must be true for admin user"))

        user = self.create_user(email, name, password, **extra_fields)
        user.save(using=self._db)
        return user
