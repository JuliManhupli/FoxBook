from django.shortcuts import get_object_or_404
from rest_framework.decorators import permission_classes, api_view
from rest_framework.generics import ListAPIView
from rest_framework.pagination import PageNumberPagination
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response

from .models import FavoriteBook
from .serializers import UserProfileSerializer
from books.models import Book
from books.serializers import BookSerializer


@api_view(['GET'])
@permission_classes([IsAuthenticated])
def get_user_profile(request):
    user = request.user  # Assuming the user is authenticated
    serializer = UserProfileSerializer(user)
    return Response(serializer.data)


class CustomPageNumberPagination(PageNumberPagination):
    page_size = 5  # Number of items to return per page
    max_page_size = 100


@permission_classes([IsAuthenticated])
class FavouriteBooksList(ListAPIView):
    serializer_class = BookSerializer
    pagination_class = CustomPageNumberPagination

    def get_queryset(self):
        user = self.request.user
        favourite_books_ids = FavoriteBook.objects.filter(user=user).values_list('book__id', flat=True)
        queryset = Book.objects.filter(pk__in=favourite_books_ids)
        print(queryset)
        # Get filter parameters from the request
        genres = self.request.query_params.get('genres', None)
        author = self.request.query_params.get('author', None)
        sorting = self.request.query_params.get('sorting', None)

        # Apply filters
        if genres:
            genres = genres.split(',')
            queryset = queryset.filter(genre__in=genres)

        if author:
            queryset = queryset.filter(type=author)

        if sorting and sorting != "Без сортувань":
            # Sort by the selected sorting option
            if sorting == 'Назва(А-Я)':
                queryset = queryset.order_by('title')
            elif sorting == 'Назва(Я-А)':
                queryset = queryset.order_by('-title')
            elif sorting == 'Автор(А-Я)':
                queryset = queryset.order_by('author')
            elif sorting == 'Автор(Я-А)':
                queryset = queryset.order_by('-author')
            elif sorting == 'Оцінка(За зростанням)':
                queryset = queryset.order_by('rating')
            elif sorting == 'Оцінка(За спаданням)':
                queryset = queryset.order_by('-rating')
        print(queryset)
        return queryset


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
