import logging
import random

from django.db.models import Avg
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
    # бере інформацію про профіль користувача
    user = request.user
    serializer = UserProfileSerializer(user)
    return Response(serializer.data)


class CustomPageNumberPagination(PageNumberPagination):
    page_size = 5  # кількість елементів на сторінці
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
                # Визначаємо сортування
                sorting_options = {
                    'Назва(А-Я)': 'title',
                    'Назва(Я-А)': '-title',
                    'Автор(А-Я)': 'author',
                    'Автор(Я-А)': '-author',
                    'Оцінка(За зростанням)': 'rating',
                    'Оцінка(За спаданням)': '-rating',
                }

                # get(), щоб отримати відповідне поле, або None
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
            queryset = Book.objects.filter(library__user=user, library__visible=True)

            genres = self.request.query_params.get('genres', None)
            author = self.request.query_params.get('author', None)
            sorting = self.request.query_params.get('sorting', None)

            if genres:
                genres = genres.split(',')
                queryset = queryset.filter(genre__in=genres)

            if author:
                queryset = queryset.filter(type=author)

            if sorting and sorting != "Без сортувань":
                # Визначаємо сортування
                sorting_options = {
                    'Назва(А-Я)': 'title',
                    'Назва(Я-А)': '-title',
                    'Автор(А-Я)': 'author',
                    'Автор(Я-А)': '-author',
                    'Оцінка(За зростанням)': 'rating',
                    'Оцінка(За спаданням)': '-rating',
                }

                # get(), щоб отримати відповідне поле, або No
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
        library_book = Library.objects.get(user=user, book=book)
        library_book.visible = True
        library_book.save()
        return Response({'message': ''})

    library_book = Library(user=user, book=book)
    library_book.save()

    return Response({'message': 'Кингу додано до бібліотеки!'})


@api_view(['POST'])
@permission_classes([IsAuthenticated])
def remove_book_from_library(request, book_id):
    user = request.user
    book = get_object_or_404(Book, pk=book_id)

    try:
        library_book = Library.objects.get(user=user, book=book)
        library_book.reading_progress = 0
        library_book.visible = False
        library_book.save()

        return Response({'message': 'Кингу видалено з бібліотеки!'})

    except Library.DoesNotExist:
        return Response({'message': 'Помилка! Книгу в бібліотеці не знайдено.'}, status=404)


@api_view(['GET'])
@permission_classes([IsAuthenticated])
def check_if_book_in_library(request, book_id):
    user = request.user
    book = get_object_or_404(Book, pk=book_id)

    try:
        library_book = Library.objects.get(user=user, book=book)
        return Response({'check_book': library_book.visible})

    except Library.DoesNotExist:
        return Response({'check_book': False})


@api_view(['GET'])
@permission_classes([IsAuthenticated])
def continue_reading(request):
    try:
        user = request.user
        queryset = Library.objects.filter(user=user, visible=True)
        books_in_progress = [library_entry.book for library_entry in queryset]
        serializer = BookInProgressSerializer(books_in_progress, many=True, context={'request': request})
        serialized_data = serializer.data
        random.shuffle(serialized_data)
        book_to_read = serialized_data[:1]
        return Response({'book_to_read': book_to_read})

    except Exception as e:
        return Response({'book_to_read': Book.objects.none()})


@api_view(['GET'])
@permission_classes([IsAuthenticated])
def get_recommendations(request):
    try:
        user = request.user
        queryset = Book.objects.filter(library__user=user).order_by('title')

        genre_list = list(book['genre'] for book in queryset.values('genre'))
        recommend_genre_len = 0
        recommendations = []

        if genre_list:

            while genre_list:
                counts = Counter(genre_list)
                most_common_element = max(counts, key=counts.get)

                genre_book_data = Book.objects.filter(genre=most_common_element).order_by('?')
                serializer = BookSerializer(genre_book_data, many=True)
                serialized_data = serializer.data

                recommend_genre_len += len(serialized_data)

                if recommend_genre_len >= 5:
                    random.shuffle(serialized_data)
                    recommendations.extend(serialized_data[:5])
                    recommendations = recommendations[:5]
                    break
                else:
                    recommendations.extend(serialized_data)
                    genre_list.remove(most_common_element)

        else:
            all_book_data = Book.objects.all().order_by('?')
            random_books = all_book_data[:5]
            serializer = BookSerializer(random_books, many=True)
            recommendations = serializer.data
            random.shuffle(recommendations)

        return Response({'recommendations': recommendations})

    except Exception as e:
        return Response({'message': 'Помилка! рекомендацій не знайдено.'})


@api_view(['GET'])
@permission_classes([IsAuthenticated])
def get_user_rating(request, book_id):
    user = request.user
    book = get_object_or_404(Book, pk=book_id)

    try:
        library_entry = Library.objects.get(user=user, book=book)
        user_rating = library_entry.user_rating if library_entry.user_rating else -1
        return Response({'user_rating': user_rating})
    except Library.DoesNotExist:
        return Response({'user_rating': -2})


@api_view(['POST'])
@permission_classes([IsAuthenticated])
def update_user_rating(request, book_id):
    user = request.user
    book = get_object_or_404(Book, pk=book_id)

    try:
        library_book = Library.objects.get(user=user, book=book)
    except Library.DoesNotExist:
        return Response({'message': 'Помилка! Книгу в бібліотеці не знайдено.'}, status=404)

    if 'user_rating' in request.data:
        user_rating = request.data['user_rating']

        library_book.user_rating = user_rating
        library_book.save()

        # Оновлення загального рейтингу книги
        avg_rating = Library.objects.filter(book=book).aggregate(Avg('user_rating'))['user_rating__avg']
        book.rating = avg_rating if avg_rating is not None else -1
        book.save()

        return Response({'message': 'Оцінку успішно оновлено!'})
    else:
        return Response({'message': 'Помилка оновлення оцінки!'})


@api_view(['GET'])
@permission_classes([IsAuthenticated])
def get_reading_progress(request, book_id):
    user = request.user
    book = get_object_or_404(Book, pk=book_id)

    try:
        library_entry = Library.objects.get(user=user, book=book)
        reading_progress = library_entry.reading_progress
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
    return Response(response_data)


@api_view(['GET'])
@permission_classes([IsAuthenticated])
def check_if_book_in_favorites(request, book_id):
    user = request.user
    book = get_object_or_404(Book, pk=book_id)
    if FavoriteBook.objects.filter(user=user, book=book).exists():
        return Response({'check_book': True})
    else:
        return Response({'check_book': False})


@api_view(['POST'])
@permission_classes([IsAuthenticated])
def add_book_to_favorites(request, book_id):
    user = request.user
    book = get_object_or_404(Book, pk=book_id)

    if FavoriteBook.objects.filter(user=user, book=book).exists():
        return Response({'message': 'Книга вже в улюбленому!'})

    favorite_book = FavoriteBook(user=user, book=book)
    favorite_book.save()

    return Response({'message': 'Книгу додано до улюбленого!'})


@api_view(['DELETE'])
@permission_classes([IsAuthenticated])
def remove_book_from_favorites(request, book_id):
    user = request.user
    book = get_object_or_404(Book, pk=book_id)

    favorite_book = get_object_or_404(FavoriteBook, user=user, book=book)
    favorite_book.delete()

    return Response({'message': 'Книгу видалено з улюбленого!'})
