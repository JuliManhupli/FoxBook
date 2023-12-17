from django.shortcuts import get_object_or_404
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
    book = get_object_or_404(Book, pk=book_id)
    print("user")
    print(user)
    if FavoriteBook.objects.filter(user=user, book=book).exists():
        print("+")
        return Response({'isInFavorites': True})
    else:
        print("-")
        return Response({'isInFavorites': False})


@api_view(['POST'])
@permission_classes([IsAuthenticated])
def add_book_to_favorites(request, book_id):
    user = request.user
    book = get_object_or_404(Book, pk=book_id)

    if FavoriteBook.objects.filter(user=user, book=book).exists():
        return Response({'message': 'Book is already in favorites'})

    favorite_book = FavoriteBook(user=user, book=book)
    favorite_book.save()

    return Response({'message': 'Book added to favorites successfully'})


@api_view(['DELETE'])
@permission_classes([IsAuthenticated])
def remove_book_from_favorites(request, book_id):
    user = request.user
    book = get_object_or_404(Book, pk=book_id)

    favorite_book = get_object_or_404(FavoriteBook, user=user, book=book)
    favorite_book.delete()

    return Response({'message': 'Book removed from favorites successfully'})
