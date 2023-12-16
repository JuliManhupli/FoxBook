from rest_framework.decorators import permission_classes, api_view
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response

from .models import FavoriteBook
from .serializers import UserProfileSerializer
from books.models import Book


@api_view(['GET'])
@permission_classes([IsAuthenticated])
def get_user_profile(request):
    user = request.user  # Assuming the user is authenticated
    serializer = UserProfileSerializer(user)
    return Response(serializer.data)


@api_view(['GET'])
@permission_classes([IsAuthenticated])
def check_if_book_in_favorites(request, book_id):
    user = request.user
    print("user")
    print(user)
    try:
        favorite_book = FavoriteBook.objects.get(user=user, book__id=book_id)
        return Response({'isInFavorites': True})
    except FavoriteBook.DoesNotExist:
        return Response({'isInFavorites': False})


@api_view(['POST'])
@permission_classes([IsAuthenticated])
def add_book_to_favorites(request, book_id):
    user = request.user
    book = Book.objects.get(pk=book_id)

    if FavoriteBook.objects.filter(user=user, book=book).exists():
        return Response({'message': 'Book is already in favorites'})

    favorite_book = FavoriteBook(user=user, book=book)
    favorite_book.save()

    return Response({'message': 'Book added to favorites successfully'})


@api_view(['DELETE'])
@permission_classes([IsAuthenticated])
def remove_book_from_favorites(request, book_id):
    user = request.user
    book = Book.objects.get(pk=book_id)

    try:
        favorite_book = FavoriteBook.objects.get(user=user, book=book)
        favorite_book.delete()
        return Response({'message': 'Book removed from favorites successfully'})
    except FavoriteBook.DoesNotExist:
        return Response({'message': 'Book is not in favorites'})
