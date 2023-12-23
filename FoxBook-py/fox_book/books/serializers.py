from rest_framework import serializers
from .models import Book


class BookSerializer(serializers.ModelSerializer):
    class Meta:
        model = Book
        fields = ['id', 'cover', 'title', 'author', 'rating', 'genre', 'annotation']


class BookTextSerializer(serializers.ModelSerializer):
    class Meta:
        model = Book
        fields = ['text']


class BookTextChunksSerializer(serializers.Serializer):
    text_chunks = serializers.ListField(child=serializers.CharField())