# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='BEACON_POSITION',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('TYPE', models.CharField(max_length=250)),
                ('BEACON_ID', models.CharField(max_length=250)),
                ('BEACON_MAC', models.CharField(max_length=250)),
                ('CREATED_BY', models.CharField(max_length=250)),
                ('CREATE_TIME', models.DateTimeField(verbose_name=b'date uploaded')),
                ('CORD_X', models.DecimalField(default=0.0, max_digits=10, decimal_places=2)),
                ('CORD_Y', models.DecimalField(default=0.0, max_digits=10, decimal_places=2)),
                ('NOTES', models.CharField(max_length=250)),
            ],
            options={
            },
            bases=(models.Model,),
        ),
        migrations.CreateModel(
            name='DEPLOYMENT',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('NAME', models.CharField(max_length=250)),
                ('NOTES', models.CharField(max_length=250)),
                ('UPLOAD_TIME', models.DateTimeField(verbose_name=b'date uploaded')),
                ('AUTHOR', models.CharField(max_length=250)),
            ],
            options={
            },
            bases=(models.Model,),
        ),
        migrations.CreateModel(
            name='FLOORPLAN',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('NAME', models.CharField(max_length=250)),
                ('AUTHOR', models.CharField(max_length=250)),
                ('FLOOR_MAP', models.CharField(max_length=250)),
                ('NOTES', models.CharField(max_length=250)),
                ('UPLOAD_TIME', models.DateTimeField(verbose_name=b'date uploaded')),
                ('X_ZERO', models.IntegerField(default=0)),
                ('Y_ZERO', models.IntegerField(default=0)),
                ('SCALE', models.IntegerField(default=0)),
            ],
            options={
            },
            bases=(models.Model,),
        ),
        migrations.CreateModel(
            name='LOC_RECORD',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('NAME', models.CharField(max_length=250)),
                ('NOTES', models.CharField(max_length=250)),
                ('UPLOAD_TIME', models.DateTimeField(verbose_name=b'date uploaded')),
                ('AUTHOR', models.CharField(max_length=250)),
                ('DEPLOYMENT', models.ForeignKey(to='ibeaconapp.DEPLOYMENT')),
            ],
            options={
            },
            bases=(models.Model,),
        ),
        migrations.CreateModel(
            name='RSSI',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('RSSI', models.IntegerField(default=0)),
                ('BEACON_POSITION', models.ForeignKey(to='ibeaconapp.BEACON_POSITION')),
                ('LOC_RECORD', models.ForeignKey(to='ibeaconapp.LOC_RECORD')),
            ],
            options={
            },
            bases=(models.Model,),
        ),
        migrations.CreateModel(
            name='TARGET',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('NAME', models.CharField(max_length=250)),
                ('MOBILE', models.CharField(max_length=250)),
                ('NOTES', models.CharField(max_length=250)),
                ('MAC', models.CharField(max_length=250)),
                ('REFERECE', models.CharField(max_length=250)),
            ],
            options={
            },
            bases=(models.Model,),
        ),
        migrations.CreateModel(
            name='TARGET_LOC',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('TYPE', models.CharField(max_length=250)),
                ('ALGORITHM', models.CharField(max_length=250)),
                ('LOC_TIME', models.DateTimeField(verbose_name=b'date calculated')),
                ('CORD_X', models.DecimalField(default=0.0, max_digits=10, decimal_places=2)),
                ('CORD_Y', models.DecimalField(default=0.0, max_digits=10, decimal_places=2)),
                ('NOTES', models.CharField(max_length=250)),
                ('LOC_RECORD', models.ForeignKey(to='ibeaconapp.LOC_RECORD')),
                ('TARGET_ID', models.ForeignKey(to='ibeaconapp.TARGET')),
            ],
            options={
            },
            bases=(models.Model,),
        ),
        migrations.AddField(
            model_name='deployment',
            name='FLOORPLAN',
            field=models.ForeignKey(to='ibeaconapp.FLOORPLAN'),
            preserve_default=True,
        ),
        migrations.AddField(
            model_name='beacon_position',
            name='DEPLOYMENT',
            field=models.ForeignKey(to='ibeaconapp.DEPLOYMENT'),
            preserve_default=True,
        ),
    ]
