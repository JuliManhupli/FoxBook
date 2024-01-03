from rest_framework import serializers

from accounts.models import User
from books.models import Book
from .models import Library


class UserProfileSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ['email', 'name']


class BookInProgressSerializer(serializers.ModelSerializer):
    reading_progress = serializers.SerializerMethodField()

    class Meta:
        model = Book
        fields = ['id', 'cover', 'title', 'author', 'rating', 'genre', 'annotation', 'pages', 'reading_progress']

    def get_reading_progress(self, obj):
        user = self.context['request'].user
        try:
            library_entry = Library.objects.get(user=user, book=obj)
            return library_entry.reading_progress
        except Library.DoesNotExist:
            return None

