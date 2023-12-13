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

    def validate(self, attrs):
        password = attrs.get('password', '')
        password2 = attrs.get('password2', '')
        if password != password2:
            raise serializers.ValidationError("Паролі не співпадають")
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

    class Meta:
        model = User
        fields = ['email', 'password']


class ResendVerificationCodeSerializer(serializers.Serializer):
    email = serializers.EmailField()


class PasswordResetRequestSerializer(serializers.Serializer):
    email = serializers.EmailField()


class PasswordResetConfirmSerializer(serializers.Serializer):
    code = serializers.CharField(max_length=6)
    new_password = serializers.CharField(max_length=68, min_length=6, write_only=True)
    new_password2 = serializers.CharField(max_length=68, min_length=6, write_only=True)

    def validate(self, attrs):
        password = attrs.get('new_password', '')
        password2 = attrs.get('new_password2', '')
        if password != password2:
            raise serializers.ValidationError("Паролі не співпадають")
        return super().validate(attrs)
