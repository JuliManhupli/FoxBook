from django.db import models


class Book(models.Model):
    title = models.CharField(max_length=255)
    author = models.CharField(max_length=255)
    type = models.CharField(max_length=255)
    genre = models.CharField(max_length=255, null=True)
    annotation = models.TextField(null=True)
    cover = models.URLField(null=True)
    rating = models.DecimalField(max_digits=3, decimal_places=2, null=True, blank=True)
    text = models.TextField()

    def __str__(self):
        return self.title
