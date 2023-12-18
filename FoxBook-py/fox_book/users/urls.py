from django.urls import path

from .views import get_user_profile, add_book_to_favorites, remove_book_from_favorites, check_if_book_in_favorites, \
    FavouriteBooksList

app_name = "accounts"
urlpatterns = [
    path('profile/', get_user_profile, name='user-profile'),
    path('favorites/books/', FavouriteBooksList.as_view(), name='favorite-books-list'),
    path('favorites/add/<int:book_id>/', add_book_to_favorites, name='add-favorites'),
    path('favorites/remove/<int:book_id>/', remove_book_from_favorites, name='remove-favorites'),
    path('favorites/check/<int:book_id>/', check_if_book_in_favorites, name='check-book-favorites'),
]