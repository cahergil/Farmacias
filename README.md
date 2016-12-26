## Farmaex

[![Twitter](https://img.shields.io/badge/Twitter-@cahergil-blue.svg?style=flat)](http://twitter.com/cahergil)
[![GPL license](https://img.shields.io/badge/license-GPL v3-blue.svg?style=flat)](https://raw.githubusercontent.com/cahegi/Farmacias/master/LICENSE.md)
 [![minSdk](https://img.shields.io/badge/minSDK-16%2B-green.svg?style=flat)](https://android-arsenal.com/api?level=16#)
 [![targetSdk](https://img.shields.io/badge/targetSDK-25-orange.svg?style=flat)](http://source.android.com/source/build-numbers.html)


![Pharmaex logo](https://github.com/cahergil/Shareablefotos/blob/master/farmaex_logo_87x87.png)

This repository contains Farmaex, a pharmacy discovering app I developed for the region of Extremadura(Spain). The intention of the project 
is to build new features over the time, constantly enhancing thus its functionality.

Share to social networks

<a href="https://twitter.com/intent/tweet?text=Check%20out%20this%20app%20on%20Github:%20https://github.com/cahergil/Farmacias" target="_blank" title="share to twitter" style="width:100%"><img src="https://github.com/PhilJay/MPAndroidChart/blob/master/design/twitter_icon.png" title="Share on Twitter" width="35" height=35 />
<a href="https://plus.google.com/share?url=https://github.com/cahergil/Farmacias" target="_blank" title="share to twitter" style="width:100%"><img src="https://github.com/PhilJay/MPAndroidChart/blob/master/design/googleplus_icon.png" title="Share on Google+" width="35" height=35 />
<a href="https://www.facebook.com/sharer/sharer.php?u=https://github.com/cahergil/Farmacias" target="_blank" title="share to twitter" style="width:100%"><img src="https://github.com/PhilJay/MPAndroidChart/blob/master/design/facebook_icon.png" title="Share on Facebook" width="35" height=35 />


## Table of Contents

- [Description](#description)
  - [Screenshots](#screenshots)
- [Features](#features)
- [Libraries used](#libraries-used)
- [GPS mock](#gps-mock)
- [Installation and Run](#installation-and-run)
- [TODO](#todo)
- [Contribute](#contribute)
- [License](#license)

## Description

 With this app people in Extremadura will be able to discover nearby pharmacies for the majority of all big cities and small towns of the region.  Anyone with an internet conexion or via WiFi can check out anytime the availability of pharmacies around him. The user is also presented with useful information about each business.
 
#### Screenshots
  
 
 
  ![Nearby list](https://github.com/cahergil/Shareablefotos/blob/master/list_map_options_open.png)  ![Nearby map](https://github.com/cahergil/Shareablefotos/blob/master/map_normal.png)
    ![bottom sheet extended](https://github.com/cahergil/Shareablefotos/blob/master/map_bottom_sheet_extended.png)  ![Favorites](https://github.com/cahergil/Shareablefotos/blob/master/favoritos.png)
   ![Searching](https://github.com/cahergil/Shareablefotos/blob/master/search_string.png)  ![Search results](https://github.com/cahergil/Shareablefotos/blob/master/search_results.png)
 ![Settings](https://github.com/cahergil/Shareablefotos/blob/master/settings.png) 
  
  
## Features

 - The data is presented in a list and in a map, to show the exact location of each pharmacy and the current location of the user. The user will be able to see its name, address, locality, working hours, telephone and distance in Km. The app adapts dynamically to changes in the current user location, changing the list the business around him. 
 - There is no need to make an explicit search, the app  works out the distances regarding the current user location,  and if the pharmacy is inside the search radio, it will be listed in on the screen and in the map.
 - The app shows to the user the best route to reach the pharmacy, by car, bike or on foot.
 - The user can share the pharmacy data to other apps like Twitter, Whatsapp...
 - The user will be able to add each pharmacy to a list for later use or to maintain a permanent list of favorites.
 - There is a search functionality in case the user needs to go to a specific pharmacy or to refresh some of its data. 
 - By pressing a button the user will be able to call the pharmacy before getting there in order to check medicament availability.


## Android and 3rd party libraries used

The following is a list of the libraries used in the project:

   * Gradle plugins:
    -  com.android.application
    -  android-apt
    -  me.tatarka.retrolambda
    -  com.neenbedankt.android-apt
   
   * Android framework supplied support libraries (25.0.1):
    -  Appcompat-v7
    -  Design
    -  Recyclerview-v7
    -  Cardview-v7
    -  Vector drawable
    -  Percent

   * Play Services:
    -  location:play-services-location:9.8.0
    -  maps:play-services-maps:9.8.0
   
   * [ButterKnife 8.1.0 ]()
   * Reactive Extensions libraries:
    -  [RxAndroid 1.0.1] (https://github.com/ReactiveX/RxAndroid)
    -  [RxJava 1.1.1] (https://github.com/ReactiveX/RxJava)
    -  [RxJava-extras 0.7.9] (https://github.com/davidmoten/rxjava-extras)
    -  [RxBinding 0.4.0] (https://github.com/JakeWharton/RxBinding)
    
   * [SuperCsv 2.4.0] (https://super-csv.github.io/super-csv/examples_reading.html)
   * [Stetho 1.3.1] (http://facebook.github.io/stetho/)
   * [Guava 19.0] (https://github.com/google/guava)
   * [Leakcanary 1.4] (https://github.com/square/leakcanary)
   * [Icepick 3.2.0] (https://github.com/frankiesardo/icepick)
   * [Snackbarbuilder 0.6.0] (https://github.com/andrewlord1990/SnackbarBuilder)
   * [Android maps utils 0.4] (https://github.com/googlemaps/android-maps-utils)
   * [Gson 2.3.1] (https://github.com/google/gson)
   
   
     

## GPS mock

There are two ways to mock the phone location in case of not being inside of the region.


* Using a real device or an emulator with gapps:

  Go to Developer options -> [Select mock location app] (http://tamingthedroid.com/allow-mock-locations)

  Select an app installed on your phone that mocks locations. On Play Store there are some, for instance Mockation.
Install and open it. Set your desired location in Mockation(for this app to work it needs to be inside Extremadura-Spain-) and
run Farmaex app.

* Using the "geo" command in the emulator console: The first step is to get the desired location: https://itouchmap.com/latlong.html.
  Google maps also offers this functionality, just click on a place and the location will appear at the bottom. Also right clicking on a   place and pressing the 3rd option on the context menu will show latitud and longitud.
  After you send it from the command line to the emulator. Steps:

 1. Launch your application in the Android emulator and open a terminal/console in your SDK's /tools directory.
 2. Connect to the emulator console:
 
     ` telnet localhost <console-port> `
     
     Normally the console port use to be 5554. If this doesn't work you can get a list of connected devices
     with the following command: ` adb devices `. [Find emulator port](http://stackoverflow.com/questions/32863647/android-emulator-  finding-port-number)
 3. Send the location data:
    This command accepts a longitude and latitude in decimal degrees. For example:
 
    ` geo fix 38.876135 -6.975236 `







## Installation and Run

1. Installation

     You can clone this repo with the following git command:
     
     ` $ git clone https://github.com/cahergil/Farmacias.git `

     Open Android Studio and import or open  the project.



2. Run

     Go to project location and enter the following two commands(from Windows):

     1. build the project:
     
     ` $ ./gradlew assembleDebug `
 
     2. install on the phone:
     
     ` $ ./gradlew  installDebug `

     Also you can build and run the project directly in AS.




## TODO 

The following features are planed to be added in the future if possible and time allows it.


 -  App shortcuts for Nougat.
 -  Google Street View Panorama.
 -  Accesibility: D-PAD and Talkback.
 -  RTL support.
  
* Long-term:
 -  Enhance Opening hours accuracy.
 -  Implement on duty pharmacies.
 -  Tablet version.


## Contribute


Want to contribute? Great!

To fix a bug or enhance an existing feature, follow these steps:
- Fork the repo
- Create a new branch (git checkout -b improve-feature)
- Make changes or develop new feature.
- Commit your changes (git commit -am 'Improve feature')
- Push to the branch (git push origin improve-feature)
- Create a Pull Request


## License

This software is distributed under the following license: [GNU GENERAL PUBLIC LICENSE Version 3](https://raw.githubusercontent.com/cahegi/Farmacias/master/LICENSE.md)

2016 Carlos Hernández Gil


