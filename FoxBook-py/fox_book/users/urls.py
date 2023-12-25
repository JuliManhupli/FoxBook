from django.urls import path

from .views import get_user_profile, add_book_to_favorites, remove_book_from_favorites, check_if_book_in_favorites, \
    FavouriteBooksList, add_book_to_library, update_reading_progress, UserBooksListView, get_reading_progress, \
    get_user_rating, add_reading_settings, get_reading_settings_text, get_reading_settings_bg, get_recommendations, \
    update_user_rating

app_name = "accounts"
urlpatterns = [
    path('profile/', get_user_profile, name='user-profile'),

    path('library/books/', UserBooksListView.as_view(), name='library-books-list'),
    path('library/add/<int:book_id>/', add_book_to_library, name='add-library'),
    path('library/recommend/', get_recommendations, name='recommend'),

    path('library/update/reading-progress/<int:book_id>/', update_reading_progress, name='update-reading-progress'),
    path('library/reading-progress/<int:book_id>/', get_reading_progress, name='library-reading-progress'),

    path('library/user-rating/<int:book_id>/', get_user_rating, name='library-user-rating'),
    path('library/update/user-rating/<int:book_id>/', update_user_rating, name='update-user-rating'),

    path('reading-settings/add/', add_reading_settings, name='reading-settings-add'),
    path('reading-settings/text/', get_reading_settings_text, name='reading-settings-text'),
    path('reading-settings/bg/', get_reading_settings_bg, name='reading-settings-bg'),

    path('favorites/books/', FavouriteBooksList.as_view(), name='favorite-books-list'),
    path('favorites/add/<int:book_id>/', add_book_to_favorites, name='add-favorites'),
    path('favorites/remove/<int:book_id>/', remove_book_from_favorites, name='remove-favorites'),
    path('favorites/check/<int:book_id>/', check_if_book_in_favorites, name='check-book-favorites'),
]
