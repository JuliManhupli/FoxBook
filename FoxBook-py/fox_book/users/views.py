import random

from django.db.models import Count
from django.shortcuts import get_object_or_404
from rest_framework.decorators import permission_classes, api_view
from rest_framework.generics import ListAPIView
from rest_framework.pagination import PageNumberPagination
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response

from .models import FavoriteBook, Library, ReadingSettings
from .serializers import UserProfileSerializer, BookInProgressSerializer
from books.models import Book
from books.serializers import BookSerializer

from collections import Counter


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
        try:
            user = self.request.user
            favourite_books_ids = FavoriteBook.objects.filter(user=user).values_list('book__id', flat=True)
            queryset = Book.objects.filter(pk__in=favourite_books_ids)

            genres = self.request.query_params.get('genres', None)
            author = self.request.query_params.get('author', None)
            sorting = self.request.query_params.get('sorting', None)

            if genres:
                genres = genres.split(',')
                queryset = queryset.filter(genre__in=genres)

            if author:
                queryset = queryset.filter(type=author)

            if sorting and sorting != "Без сортувань":
                # Define a dictionary to map sorting options to fields
                sorting_options = {
                    'Назва(А-Я)': 'title',
                    'Назва(Я-А)': '-title',
                    'Автор(А-Я)': 'author',
                    'Автор(Я-А)': '-author',
                    'Оцінка(За зростанням)': 'rating',
                    'Оцінка(За спаданням)': '-rating',
                }

                # Use get() to get the corresponding field or default to None
                sort_field = sorting_options.get(sorting)

                if sort_field:
                    queryset = queryset.order_by(sort_field)

            return queryset

        except Exception as e:
            return Book.objects.none()


@permission_classes([IsAuthenticated])
class UserBooksListView(ListAPIView):
    serializer_class = BookInProgressSerializer
    pagination_class = CustomPageNumberPagination

    def get_queryset(self):
        try:
            user = self.request.user
            queryset = Book.objects.filter(library__user=user)
            print(queryset)

            genres = self.request.query_params.get('genres', None)
            author = self.request.query_params.get('author', None)
            sorting = self.request.query_params.get('sorting', None)

            if genres:
                genres = genres.split(',')
                queryset = queryset.filter(genre__in=genres)

            if author:
                queryset = queryset.filter(type=author)

            if sorting and sorting != "Без сортувань":
                # Define a dictionary to map sorting options to fields
                sorting_options = {
                    'Назва(А-Я)': 'title',
                    'Назва(Я-А)': '-title',
                    'Автор(А-Я)': 'author',
                    'Автор(Я-А)': '-author',
                    'Оцінка(За зростанням)': 'rating',
                    'Оцінка(За спаданням)': '-rating',
                }

                # Use get() to get the corresponding field or default to None
                sort_field = sorting_options.get(sorting)

                if sort_field:
                    queryset = queryset.order_by(sort_field)

            return queryset

        except Exception as e:
            return Book.objects.none()


@api_view(['POST'])
@permission_classes([IsAuthenticated])
def add_book_to_library(request, book_id):
    user = request.user
    book = get_object_or_404(Book, pk=book_id)

    if Library.objects.filter(user=user, book=book).exists():
        return Response({'message': 'Book is already library'})

    library_book = Library(user=user, book=book)
    library_book.save()

    return Response({'message': 'Book added to library'})


@api_view(['GET'])
@permission_classes([IsAuthenticated])
def get_recommendations(request):
    try:
        user = request.user
        queryset = Book.objects.filter(library__user=user)

        genre_list = list(book['genre'] for book in queryset.values('genre'))
        counts = Counter(genre_list)
        most_common_element = max(counts, key=counts.get)

        genre_book_data = Book.objects.filter(genre=most_common_element)
        serializer = BookSerializer(genre_book_data, many=True)
        serialized_data = serializer.data

        if len(serialized_data) >= 5:
            recommendations = random.sample(serialized_data, 5)
        else:
            recommendations = serialized_data

        return Response({'recommendations': recommendations})

    except Exception as e:
        serializer = BookSerializer(Book.objects.all(), many=True)
        serialized_data = serializer.data
        recommendations = random.sample(serialized_data, 5)
        return Response({'recommendations': recommendations})


@api_view(['GET'])
@permission_classes([IsAuthenticated])
def get_user_rating(request, book_id):
    user = request.user
    book = get_object_or_404(Book, pk=book_id)

    try:
        library_entry = Library.objects.get(user=user, book=book)
        user_rating = library_entry.user_rating if library_entry.user_rating else -1
        print(user_rating)
        return Response({'user_rating': user_rating})
    except Library.DoesNotExist:
        return Response({'user_rating': -1})


@api_view(['GET'])
@permission_classes([IsAuthenticated])
def get_reading_progress(request, book_id):
    user = request.user
    book = get_object_or_404(Book, pk=book_id)

    try:
        library_entry = Library.objects.get(user=user, book=book)
        reading_progress = library_entry.reading_progress
        print(reading_progress)
        return Response({'reading_progress': reading_progress})
    except Library.DoesNotExist:
        return Response({'reading_progress': 0})


@api_view(['POST'])
@permission_classes([IsAuthenticated])
def update_reading_progress(request, book_id):
    user = request.user
    book = get_object_or_404(Book, pk=book_id)

    library_book, created = Library.objects.get_or_create(user=user, book=book)

    if 'reading_progress' in request.data:
        reading_progress = request.data['reading_progress']
        print(reading_progress)
        library_book.reading_progress = reading_progress
        library_book.save()

        return Response({'message': 'Reading progress updated successfully'})
    else:
        return Response({'message': 'Reading progress not provided in the request'})


@api_view(['POST'])
@permission_classes([IsAuthenticated])
def add_reading_settings(request):
    user = request.user
    data = request.data
    print(data)
    bg_color = data.get('bg_color')
    text_color = data.get('text_color')
    text_size = data.get('text_size')
    text_font = data.get('text_font')

    try:
        reading_settings = ReadingSettings.objects.get(user=user)
        reading_settings.bg_color = bg_color
        reading_settings.text_color = text_color
        reading_settings.text_size = text_size
        reading_settings.text_font = text_font
        reading_settings.save()
    except ReadingSettings.DoesNotExist:
        reading_settings = ReadingSettings(user=user, bg_color=bg_color, text_color=text_color, text_size=text_size,
                                           text_font=text_font)
        reading_settings.save()
    print(reading_settings)
    return Response({'message': 'Reading settings added successfully'})


@api_view(['GET'])
@permission_classes([IsAuthenticated])
def get_reading_settings_text(request):
    user = request.user
    try:
        reading_settings = ReadingSettings.objects.get(user=user)
        response_data = {
            'text_color': reading_settings.text_color,
            'text_size': reading_settings.text_size,
            'text_font': reading_settings.text_font,
        }
    except ReadingSettings.DoesNotExist:
        response_data = {
            'text_color': "black",
            'text_size': 44,
            'text_font': "inter",
        }
    print(response_data)
    return Response(response_data)


@api_view(['GET'])
@permission_classes([IsAuthenticated])
def get_reading_settings_bg(request):
    user = request.user
    try:
        reading_settings = ReadingSettings.objects.get(user=user)
        response_data = {
            'bg_color': reading_settings.bg_color,
        }
    except ReadingSettings.DoesNotExist:
        response_data = {
            'bg_color': "beige",
        }
    print(response_data)
    return Response(response_data)


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
