from django.http import HttpResponse
from django.shortcuts import render
from rest_framework import status
from rest_framework.parsers import JSONParser
from rest_framework.permissions import AllowAny
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework_simplejwt.tokens import RefreshToken
from rest_framework.generics import GenericAPIView

from .serializers import UserRegisterSerializer
from .models import User
from .utils import send_code_to_user


def home(request):
    return HttpResponse("API is working")


class UserRegisterAPIView(GenericAPIView):

    serializer_class = UserRegisterSerializer

    def post(self, request, *args, **kwargs):
        user_data = request.data
        print(user_data)

        serializer = self.serializer_class(data=user_data)
        if serializer.is_valid(raise_exception=True):
            serializer.save()
            user = serializer.data

            send_code_to_user(user['email'])
            print(user)
            return Response({
                'data': user,
                'message': f"Ми відправили код підтвердження на пошту {user['email']}"
            }, status=status.HTTP_201_CREATED)

        return Response(
            serializer.errors,
            status=status.HTTP_400_BAD_REQUEST)

