from rest_framework.decorators import api_view
from rest_framework.generics import ListAPIView
from rest_framework.pagination import PageNumberPagination
from rest_framework.response import Response

from .models import Book
from .serializers import BookSerializer


class CustomPageNumberPagination(PageNumberPagination):
    page_size = 5  # Number of items to return per page
    max_page_size = 100


class BookList(ListAPIView):
    queryset = Book.objects.all()
    serializer_class = BookSerializer
    pagination_class = CustomPageNumberPagination

    def get_queryset(self):
        queryset = Book.objects.all()

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

        return queryset


@api_view(['GET'])
def get_all_genres(request):
    genres = set()
    for book in Book.objects.all():
        genres.update(book.genre.split(','))
    return Response(list(genres))