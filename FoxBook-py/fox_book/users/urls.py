from django.urls import path

from .views import get_user_profile

app_name = "accounts"
urlpatterns = [
    path('profile/', get_user_profile, name='get_user_profile'),
]
