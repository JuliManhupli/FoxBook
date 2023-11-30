from django.urls import path

from .views import home, UserRegisterAPIView, VerifyUserEmail, LoginUserView, TestAuthenticationView, \
    ResendVerificationView, PasswordResetRequestView, PasswordResetVerifyView, \
    PasswordResetSetPasswordView

app_name = "accounts"
urlpatterns = [
    path('register/', UserRegisterAPIView.as_view(), name='register'),
    path('verify-email/', VerifyUserEmail.as_view(), name='verify'),
    path('resend-verification/', ResendVerificationView.as_view(), name='resend-verification'),
    path('login/', LoginUserView.as_view(), name='login'),
    path('password-reset-request/', PasswordResetRequestView.as_view(), name='password-reset-request'),
    path('password-reset/verify/', PasswordResetVerifyView.as_view(), name='password-reset-verify'),
    path('password-reset/set-password/', PasswordResetSetPasswordView.as_view(), name='password-reset-set-password'),

    path('profile/', TestAuthenticationView.as_view(), name='profile'),
    path('', home, name='home'),
]
