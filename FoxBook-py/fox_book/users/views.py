from rest_framework.decorators import permission_classes, api_view
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response

from .serializers import UserProfileSerializer

@api_view(['GET'])
@permission_classes([IsAuthenticated])
def get_user_profile(request):
    user = request.user  # Assuming the user is authenticated
    serializer = UserProfileSerializer(user)
    return Response(serializer.data)
