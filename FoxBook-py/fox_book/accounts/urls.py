from django.contrib import admin
from django.urls import path, include

from .views import home, UserRegisterAPIView

urlpatterns = [
    path('register/', UserRegisterAPIView.as_view(), name='register'),
    # path('login', UserAPIView.as_view(), name='register'),
    path('', home, name='home'),
]
