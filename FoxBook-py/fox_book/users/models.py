from django.db import models

from accounts.models import User
from books.models import Book


class FavoriteBook(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    book = models.ForeignKey(Book, on_delete=models.CASCADE)

    class Meta:
        unique_together = ('user', 'book')


class Library(models.Model):
    book = models.ForeignKey(Book, on_delete=models.CASCADE)
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    user_rating = models.IntegerField(default=0, null=True, blank=True)
    reading_progress = models.IntegerField(default=0, null=True, blank=True)

    class Meta:
        unique_together = ('user', 'book')