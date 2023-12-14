from django.urls import path
from .views import BookList, get_all_genres

urlpatterns = [
    path('books/', BookList.as_view(), name='book-list'),
    path('genres/', get_all_genres, name='genre-list'),
]
