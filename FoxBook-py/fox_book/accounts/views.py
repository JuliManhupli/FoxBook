from django.http import HttpResponse
from django.contrib.auth import authenticate
from rest_framework import status
from rest_framework.parsers import JSONParser
from rest_framework.permissions import AllowAny, IsAuthenticated
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework_simplejwt.tokens import RefreshToken
from rest_framework.generics import GenericAPIView

from .serializers import UserRegisterSerializer, UserLoginSerializer
from .models import OneTimePassword
from .utils import send_code_to_user


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