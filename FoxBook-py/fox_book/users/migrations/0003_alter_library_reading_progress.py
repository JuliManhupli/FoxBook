# Generated by Django 4.2.8 on 2023-12-22 12:10

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('users', '0002_library'),
    ]

    operations = [
        migrations.AlterField(
            model_name='library',
            name='reading_progress',
            field=models.IntegerField(blank=True, default=None, null=True),
        ),
    ]
