from rest_framework.decorators import api_view
from rest_framework.generics import ListAPIView
from rest_framework.pagination import PageNumberPagination
from rest_framework.response import Response

from .models import Book
from .serializers import BookSerializer, BookTextSerializer


class CustomPageNumberPagination(PageNumberPagination):
    page_size = 5  # Кількість елементів на сторінку
    max_page_size = 100


class BookList(ListAPIView):
    serializer_class = BookSerializer
    pagination_class = CustomPageNumberPagination

    def get_queryset(self):
        try:
            queryset = Book.objects.all()

            genres = self.request.query_params.get('genres', None)
            author = self.request.query_params.get('author', None)
            sorting = self.request.query_params.get('sorting', None)

            if genres:
                genres = genres.split(',')
                queryset = queryset.filter(genre__in=genres)

            if author:
                queryset = queryset.filter(type=author)

            if sorting and sorting != "Без сортувань":
                # Визначаємо способи сортування
                sorting_options = {
                    'Назва(А-Я)': 'title',
                    'Назва(Я-А)': '-title',
                    'Автор(А-Я)': 'author',
                    'Автор(Я-А)': '-author',
                    'Оцінка(За зростанням)': 'rating',
                    'Оцінка(За спаданням)': '-rating',
                }

                # get() щоб отримати відповідне поле або None
                sort_field = sorting_options.get(sorting)

                if sort_field:
                    queryset = queryset.order_by(sort_field)

            return queryset

        except Exception as e:
            return Book.objects.none()


@api_view(['GET'])
def get_all_genres(request):
    genres = Book.objects.values_list('genre', flat=True).distinct()
    return Response(genres)


@api_view(['GET'])
def get_book_text_array(request, book_id):
    try:
        book = Book.objects.get(id=book_id)
        full_text = book.text

        chunk_size = 1000
        text_chunks = [full_text[i:i + chunk_size] for i in range(0, len(full_text), chunk_size)]
        return Response({'text_chunks': text_chunks})
    except Book.DoesNotExist:
        return Response({"error": "Book not found"}, status=404)
