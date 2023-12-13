from rest_framework.generics import ListAPIView
from rest_framework.pagination import PageNumberPagination
from .models import Book
from .serializers import BookSerializer


class CustomPageNumberPagination(PageNumberPagination):
    page_size = 5  # Number of items to return per page
    max_page_size = 100


class BookList(ListAPIView):
    queryset = Book.objects.all()
    serializer_class = BookSerializer
    pagination_class = CustomPageNumberPagination
