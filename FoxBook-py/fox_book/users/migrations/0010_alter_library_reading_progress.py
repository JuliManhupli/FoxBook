# Generated by Django 4.2.8 on 2023-12-23 15:55

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('users', '0009_alter_library_reading_progress'),
    ]

    operations = [
        migrations.AlterField(
            model_name='library',
            name='reading_progress',
            field=models.IntegerField(blank=True, default=0),
        ),
    ]
