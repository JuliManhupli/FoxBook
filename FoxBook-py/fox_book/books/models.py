from django.db import models


class Book(models.Model):
    UKRAINIAN_LITERATURE = 'Українська література'
    FOREIGN_LITERATURE = 'Зарубіжна література'

    TYPE_CHOICES = [
        (UKRAINIAN_LITERATURE, 'Українська література'),
        (FOREIGN_LITERATURE, 'Зарубіжна література'),
    ]

    title = models.CharField(max_length=255)
    author = models.CharField(max_length=255)
    type = models.CharField(max_length=255, choices=TYPE_CHOICES)
    genre = models.CharField(max_length=255)
    annotation = models.TextField(null=True)
    cover = models.URLField(null=True)
    rating = models.DecimalField(max_digits=3, decimal_places=2, default=-1, blank=True)
    text = models.TextField()
    pages = models.IntegerField(default=0)

    def __str__(self):
        return self.title
