# Generated by Django 4.2.8 on 2023-12-24 16:11

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('users', '0011_alter_library_user_rating'),
    ]

    operations = [
        migrations.AlterField(
            model_name='library',
            name='user_rating',
            field=models.DecimalField(blank=True, decimal_places=2, default=-1, max_digits=3),
        ),
    ]
