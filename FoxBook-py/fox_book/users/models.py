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
    user_rating = models.IntegerField(default=-1, blank=True)
    reading_progress = models.IntegerField(default=0, blank=True)

    class Meta:
        unique_together = ('user', 'book')


class ReadingSettings(models.Model):
    user = models.OneToOneField(User, on_delete=models.CASCADE)
    bg_color = models.CharField(max_length=255)
    text_color = models.CharField(max_length=255)
    text_size = models.FloatField()
    text_font = models.CharField(max_length=255)

    def str(self):
        return f"{self.user.name} " \
               f"{self.bg_color} " \
               f"{self.text_color} " \
               f"{self.text_size}" \
               f"{self.text_font}"
