from django.urls import path
from .views import BookList, get_all_genres, get_book_text

urlpatterns = [
    path('books/', BookList.as_view(), name='book-list'),
    path('genres/', get_all_genres, name='genre-list'),
    path('books/<int:book_id>/text/', get_book_text, name='get-book-text'),
]
