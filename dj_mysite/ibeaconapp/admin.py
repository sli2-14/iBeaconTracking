from django.contrib import admin

from ibeaconapp.models import *

# Register your models here.
admin.site.register(FLOORPLAN)
admin.site.register(DEPLOYMENT)
admin.site.register(LOC_RECORD)
admin.site.register(TARGET)
admin.site.register(TARGET_LOC)
admin.site.register(RSSI)
admin.site.register(BEACON_POSITION)
admin.site.register(ZONE)
