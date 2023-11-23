from django.contrib.auth import authenticate
from rest_framework import serializers
from rest_framework.exceptions import AuthenticationFailed

from .models import User


class UserRegisterSerializer(serializers.ModelSerializer):
    password = serializers.CharField(max_length=68, min_length=6, write_only=True)
    password2 = serializers.CharField(max_length=68, min_length=6, write_only=True)

    class Meta:
        model = User
        fields = ['email', 'name', 'password', 'password2']

        # extra_kwargs = {'password': {'write_only': True}}  # пароль буде входити у лише Post-запит

    def validate(self, attrs):
        password = attrs.get('password', '')
        password2 = attrs.get('password2', '')
        if password != password2:
            raise serializers.ValidationError("Password don`t match")
        return super().validate(attrs)

    def create(self, validated_data):
        user = User.objects.create_user(
            email=validated_data['email'],
            name=validated_data.get('name'),
            password=validated_data.get('password')
        )
        return user


class UserLoginSerializer(serializers.ModelSerializer):
    email = serializers.EmailField(max_length=255)
    password = serializers.CharField(max_length=68, min_length=6, write_only=True)
    name = serializers.CharField(max_length=255, read_only=True)
    access_token = serializers.CharField(max_length=255, read_only=True)
    refresh_token = serializers.CharField(max_length=255, read_only=True)

    class Meta:
        model = User
        fields = ['email', 'password', 'name', 'access_token', 'refresh_token']

    def validate(self, attrs):
        email = attrs.get('email')
        password = attrs.get('password')
        request = self.context.get('request')
        user = authenticate(request, email=email, password=password)

        if not user:
            raise AuthenticationFailed("Неправильні дані! Спробуйте знову")
        if not user.is_verified:
            raise AuthenticationFailed("Пошта не підтверджена")
        user_tokens = user.tokens()

        return {
            'email': user.email,
            'name': user.name,
            'refresh_token': user_tokens.get('refresh'),
            'access_token': user_tokens.get('access'),
        }


class ResendVerificationCodeSerializer(serializers.Serializer):
    email = serializers.EmailField()
