from django.contrib import admin
from django.urls import path, include

from .views import home, UserRegisterAPIView, VerifyUserEmail, LoginUserView, TestAuthenticationView

urlpatterns = [
    path('register/', UserRegisterAPIView.as_view(), name='register'),
    path('verify-email/', VerifyUserEmail.as_view(), name='verify'),
    path('login/', LoginUserView.as_view(), name='login'),
    path('profile/', TestAuthenticationView.as_view(), name='profile'),
    path('', home, name='home'),
]
