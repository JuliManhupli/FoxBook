from django.utils import timezone
from django.http import HttpResponse
from rest_framework import status
from rest_framework.exceptions import NotFound, ValidationError
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response
from rest_framework.generics import GenericAPIView

from .serializers import UserRegisterSerializer, UserLoginSerializer, ResendVerificationCodeSerializer, \
    PasswordResetRequestSerializer, PasswordResetConfirmSerializer
from .models import OneTimePassword, User, PasswordReset
from .utils import send_code_to_user, generate_otp


def home(request):
    return HttpResponse("API is working")


class UserRegisterAPIView(GenericAPIView):
    serializer_class = UserRegisterSerializer

    def post(self, request, *args, **kwargs):
        user_data = request.data

        serializer = self.serializer_class(data=user_data)
        if serializer.is_valid(raise_exception=True):
            serializer.save()
            user = serializer.data

            send_code_to_user(user['email'])
            return Response({
                'data': user,
                'message': f"Ми відправили код підтвердження на пошту {user['email']}"
            }, status=status.HTTP_201_CREATED)

        return Response(
            serializer.errors,
            status=status.HTTP_400_BAD_REQUEST)


class VerifyUserEmail(GenericAPIView):

    def post(self, request):
        otp_code = request.data.get('otp')
        try:
            user_code_obj = OneTimePassword.objects.get(code=otp_code)
            user = user_code_obj.user
            if not user.is_verified:
                user.is_verified = True
                user.save()
                return Response({
                    "message": "Пошту успішно підтверджено!",

                }, status=status.HTTP_200_OK)

            return Response({
                "message": "Код вже був використаний",
            }, status=status.HTTP_204_NO_CONTENT)

        except OneTimePassword.DoesNotExist:
            return Response({
                "message": "Код не було знайдено"
            }, status=status.HTTP_404_NOT_FOUND)


class ResendVerificationView(GenericAPIView):
    serializer_class = ResendVerificationCodeSerializer

    def post(self, request, *args, **kwargs):
        serializer = self.serializer_class(data=request.data)
        serializer.is_valid(raise_exception=True)
        email = serializer.validated_data['email']

        try:
            user = User.objects.get(email=email)
            if user.is_verified:
                return Response({"message": "Акаунт вже підтверджено"}, status=status.HTTP_400_BAD_REQUEST)

            # Спробуйте отримати існуючий OneTimePassword або створити новий
            otp_instance, created = OneTimePassword.objects.get_or_create(user=user)

            if not created:
                # Якщо існуючий OneTimePassword, оновіть його код
                otp_instance.code = generate_otp()
                otp_instance.save()
                send_code_to_user(email, False, otp_instance.code)
            else:
                send_code_to_user(email)

            return Response({"message": f"Ми відправили новий код підтвердження на пошту {email}"},
                            status=status.HTTP_200_OK)

        except User.DoesNotExist:
            return Response({"message": "Користувача з такою поштою не знайдено"}, status=status.HTTP_404_NOT_FOUND)


class PasswordResetRequestView(GenericAPIView):
    serializer_class = PasswordResetRequestSerializer

    def post(self, request, *args, **kwargs):
        serializer = self.serializer_class(data=request.data)
        serializer.is_valid(raise_exception=True)
        email = serializer.validated_data['email']
        print(email)
        try:
            user = User.objects.get(email=email)

            if not user.is_verified:
                return Response({"message": "Пошта не підтверджена"}, status=status.HTTP_400_BAD_REQUEST)

            send_code_to_user(email, purpose='password_reset')

            return Response({"message": f"Ми відправили код для скидання пароля на пошту {email}"},
                            status=status.HTTP_200_OK)

        except User.DoesNotExist:
            return Response({"message": "Користувача з такою поштою не знайдено"}, status=status.HTTP_404_NOT_FOUND)


class PasswordResetVerifyView(GenericAPIView):
    # serializer_class = serializers.CharField()

    def post(self, request, *args, **kwargs):
        code = request.data.get('code')
        try:
            reset_instance = PasswordReset.objects.get(code=code)
            if reset_instance.is_expired():
                raise ValidationError("Час дії коду вийшов")

            return Response({"message": "Код скидання пароля правильний"},
                            status=status.HTTP_200_OK)
        except PasswordReset.DoesNotExist:
            raise NotFound("Код не було знайдено")


class PasswordResetSetPasswordView(GenericAPIView):
    serializer_class = PasswordResetConfirmSerializer

    def post(self, request, *args, **kwargs):
        serializer = self.serializer_class(data=request.data)
        serializer.is_valid(raise_exception=True)
        code = serializer.validated_data['code']
        new_password = serializer.validated_data['new_password']

        try:
            reset_instance = PasswordReset.objects.get(code=code)
            user = reset_instance.user
            user.set_password(new_password)
            user.save()

            # Optionally, delete the PasswordReset instance to prevent reuse
            reset_instance.delete()

            return Response({"message": "Пароль успішно скинутий"},
                            status=status.HTTP_200_OK)
        except PasswordReset.DoesNotExist:
            raise NotFound("Код не знайдено")


class LoginUserView(GenericAPIView):
    serializer_class = UserLoginSerializer

    def post(self, request, *args, **kwargs):
        serializer = self.serializer_class(data=request.data, context={"request": request})
        serializer.is_valid(raise_exception=True)
        return Response(serializer.data, status=status.HTTP_200_OK)


class TestAuthenticationView(GenericAPIView):
    permission_classes = [IsAuthenticated]

    def get(self, request, *args, **kwargs):
        data = {
            'message': 'Все працює!'
        }
        return Response(data, status=status.HTTP_200_OK)