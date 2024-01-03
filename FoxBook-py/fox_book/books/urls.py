from django.urls import path
from .views import BookList, get_all_genres, get_book_text_array

urlpatterns = [
    path('books/', BookList.as_view(), name='book-list'),
    path('genres/', get_all_genres, name='genre-list'),
    path('books/<int:book_id>/text/сhunks', get_book_text_array, name='get-book-text-сhunks'),
]
