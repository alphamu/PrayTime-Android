<img src="http://alimuzaffar.com/salaat-time-promo-graphic.png" width="400" align="right" hspace="20">
PrayerTime-Android
==================

PrayerTime-Android is available on Google Play Store as [Salaat Time](https://play.google.com/store/apps/details?id=com.alimuzaffar.ramadanalarm).

**[Install Salaat Time from Google Play](https://play.google.com/store/apps/details?id=com.alimuzaffar.ramadanalarm)**

Salaat Time Calculations
------------------------

The Salaat time calculations are based on the [Prayer-Times](https://github.com/abodehq/Pray-Times) library.
It has been modified to allow it to be used with the app and to make the code consistent with Java conventions.

Compiling
---------

You should be able to setup the project in Android Studio 1.3+.
The string resource for the Google Maps API key has **not** been included.
You may get a compile time exception that a string resource is missing:

`Error:(82, 29) No resource found that matches the given name (at 'value' with value '@string/secret_google_maps_api_key').`

In order to resolve this, you just need to define a string resource under `strings.xml` simply add:

`<string name="secret_google_maps_api_key">your API key</string>`


LICENSE
-------

The code is licensed under **Apache 2.0** 