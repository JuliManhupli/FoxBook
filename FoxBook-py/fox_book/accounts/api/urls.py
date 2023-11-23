from django.urls import path
from rest_framework_simplejwt.views import (
    TokenRefreshView,
)

from .views import getRoutes, MyTokenObtainPairView

urlpatterns = [
    path('', getRoutes),
    path('token/', MyTokenObtainPairView.as_view(), name='token_obtain_pair'),
    path('token/refresh/', TokenRefreshView.as_view(), name='token_refresh'),
]