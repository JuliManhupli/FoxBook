from django.urls import path

from .views import home, UserRegisterAPIView, VerifyUserEmail, LoginUserView, TestAuthenticationView, \
    ResendVerificationView

urlpatterns = [
    path('register/', UserRegisterAPIView.as_view(), name='register'),
    path('verify-email/', VerifyUserEmail.as_view(), name='verify'),
    path('resend-verification/', ResendVerificationView.as_view(), name='resend-verification'),
    path('login/', LoginUserView.as_view(), name='login'),
    path('profile/', TestAuthenticationView.as_view(), name='profile'),
    path('', home, name='home'),
]
