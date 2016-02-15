# npi-android-project
A little repository for the **"Nuevos Paradigmas de Información"** apps development.

We are using Android Studio as IDE.

## VoiceCompass
### App Desription
This app gives you the posibility of tell to the phone a cardinal point with an error margin and an arrow will point this direction, when the arrow points to the provided direction, it will change his colour for inform that the user is taking the right direction.

The app will recognize every phrase that contains as first word a cardinal point *north, east, west or south* and a number, ignoring the others words told.

The recognition implementation is inspired by [Android Speech Recognition – Example](https://www.learn2crack.com/2013/12/android-speech-recognition-example.html). The compass implementation is based on those solutions [Como crear una brújula en android](http://agamboadev.esy.es/como-crear-un-brujula-en-android/) and [Create your own magnetic compass](http://www.techrepublic.com/article/pro-tip-create-your-own-magnetic-compass-using-androids-internal-sensors/).

### App implementation
#### VoiceCompass
 - [VoiceCompass.java](https://github.com/Darth-ATA/npi-android-project/blob/master/NPIToolKit/app/src/main/java/com/alejandros/npitoolkit/VoiceCompass.java)
 - [content_voice_compass.xml](https://github.com/Darth-ATA/npi-android-project/blob/master/NPIToolKit/app/src/main/res/layout/content_voice_compass.xml)

For starts the voice recognition, the user have to touch the button in the screen. In code this is traduced in:

```xml
    <Button
         android:id="@+id/VoiceDetectorButton"
         android:layout_width="wrap_content"
         android:layout_height="wrap_content"
         android:text="@string/recognition"
         android:onClick="onClick"
         ></Button>
```

Then the onClick method will call the **RecognizerIntent** of Android (whose needs internet connection) and we indicate that we want the recognition result as text (String). In code this is traduced in:

```java
  public void onClick(View view){
      // We need internet connection for the RecognizerIntent
      if(isConnected()){
          // Calls the ReconizerIntent activity
          Intent recognizer = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
          recognizer.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
          // Will provide the result as string
          recognizer.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.compass_initMessage));
          startActivityForResult(recognizer, VOICE_RECOGNITION);
      }
      else{
          Toast.makeText(getApplicationContext(), getString(R.string.compass_internetError), Toast.LENGTH_LONG).show();
      }
  }
```
Then we traduced the message in direction values. In code this is traduced in:

```java
    // Translate the words in degrees
    protected int calculateProvidedOrientation(String message) {
        if (message.startsWith("north") || message.startsWith("norte")) {
            return 0;
        } else if (message.startsWith("east") || message.startsWith("este")) {
            return 90;
        } else if (message.startsWith("south") || message.startsWith("sur")) {
            return 180;
        } else if (message.startsWith("west") || message.startsWith("oeste")) {
            return 270;
        }
        else {
            return 0;
        }
    }

    // Ignore the words that isn't numbers and convert those string to int
    protected int calculateErrorMargin(String message){
        return  0 + Integer.parseInt(message.replaceAll("[^0-9]",""));
    }
```

Now we have all the necesary data for create our compass. Then we call the activity that are going to simulate the compass, but we have to provide to this activity the calculated data first. In code this is traduced:

```java
    // First checks that the message have a cardinal point as first word and then starts the compass activity
    if(checkMessage(message)){
        Intent intent = new Intent(this, Compass.class);
        intent.putExtra(EXTRA_MESSAGE, orientation_error);
        startActivity(intent);
    }else {
        Toast.makeText(this, R.string.compass_suggestion, Toast.LENGTH_LONG).show();
    }
```

#### Compass
 - [Compass.java](https://github.com/Darth-ATA/npi-android-project/blob/master/NPIToolKit/app/src/main/java/com/alejandros/npitoolkit/Compass.java)
 - [activity_compass.xml](https://github.com/Darth-ATA/npi-android-project/blob/master/NPIToolKit/app/src/main/res/layout/activity_compass.xml)

 We have the cardinal point and the error as two int variables and we have to show the user where are the point that he want. We can achieve this using an image that indicates the direction that user have to take. In code this is traduced:

 ```xml
 <ImageView
    android:id="@+id/imageViewCompass"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_below="@+id/tvHeading"
    android:layout_centerHorizontal="true"
    android:src="@drawable/arrow"
    android:contentDescription="@string/compass_description"
    />
 ```

The direction is calculated using the accelerometer and the magnetometer in the phone. For this when the sensors data change we calculate the direction that have the coordinate that we choose and shown an animation based on turn an arrow image. Also we have to inform the user when he is taking the good direction. For this we choose the option of change the arrow color. In code this is traduced:

```java
// It executes when the sensor change
    @Override
    public void onSensorChanged(SensorEvent event) {
        // Can change the accelerometer or the magnetometer
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values.clone();
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values.clone();

        // Need the data of both sensors for the compass
        if ((mGravity != null) && (mGeomagnetic != null)) {
            /*
             * For the orientation of the device, it calculates the azimut:
             * "angle of the reference (north) and a line in the middle of the observer
             * and the interest point in the same direction field" - Wikipedia
             * If the azimut is 0º, the device is oriented to the North, 90º to the East,
             * 180º to the South and 270º to the West
             *
             * For calculate the azimut, first calculate the rotation matrix (using the
             * accelerometer and magnetometer data) and after it uses this matrix obtaining
             * a vector that his first coordinate is de azimut
             */
            SensorManager.getRotationMatrix(rotationMatrix, null, mGravity, mGeomagnetic);
            SensorManager.getOrientation(rotationMatrix, orientation);

            float azimut = orientation[0]; // orientation contains: azimut, pitch and roll

            // Azimut to degrees
            float deviceOrientation = (float)(Math.toDegrees(azimut)+360)%360;

            boolean rightHeading = isInTheRightDirection(deviceOrientation);

            if(rightHeading) {
                int green = Color.parseColor("#008000"); //Green colour
                image.setColorFilter(green);
            }
            else{
                image.clearColorFilter();
            }

            // animate the image change for an smooth visualization
            RotateAnimation animation = new RotateAnimation(
                    previusOrientation,
                    -(deviceOrientation-providedOrientation),
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);

            // how long the animation will take place
            animation.setDuration(210);

            // set the animation after the end of the reservation status
            animation.setFillAfter(true);

            tvHeading.setText(getString(R.string.compass_heading) + Float.toString((deviceOrientation-providedOrientation+360)%360) + getString(R.string.compass_degrees));

            // start the animation
            image.startAnimation(animation);

            // save the orientation for the next animation
            previusOrientation = -(deviceOrientation-providedOrientation);
        }
    }
```

The method that calculate when it is taking the good direction:
```java
    // Checks that the device is taking the direction provided
    public boolean isInTheRightDirection(float deviceOrientation){
        // When you have to point the north has to change the conditions
        if(providedOrientation == 0){
            if((deviceOrientation >= (360 - errorMargin)) ||
               (deviceOrientation <= errorMargin)){
                return true;
            }
            else{
                return false;
            }
        }
        else {
            if (((deviceOrientation >= (providedOrientation - errorMargin + 360) % 360)) &&
                 (deviceOrientation <= (providedOrientation + errorMargin + 360) % 360)) {//Math.abs(deviceOrientation - providedOrientation) <= errorMargin){
                return true;
            } else {
                return false;
            }
        }
    }
```

## QrGPSPoint
### App Description
This app gives you the posibility to provide an Qr code that contains some info, and the user can read the information inside this Qr code.
For this developemt we decided to use a bridge withing the app and another app that scan the QR code. Using the classes **IntentIntegrator.java** and **IntentResult.java** provided by the library used **ZXing** permits the user scan the desired QR with an external application that he has installed before or the app will claim him to install it.

## GesturePhoto
For this development we decided to use as gesture all the combinations that provides de lockpatern of *Android*. For the lockpattern we use the library of [haibison](https://bitbucket.org/haibison/android-lockpattern/wiki/Quick-Use). After entering the correct lockpattern the app automatically opens a camera preview implemented using the [android development camera tutorial](http://stackoverflow.com/questions/2543059/android-camera-in-portrait-on-surfaceview) unfortunately the tutorial uses a deprecated class.

After three seconds of entering the right pattern, it takes a photo and storage it in the external storage of the phone and the app shows a message indicating where the photo has been placed.


## MovementSound
### App description
This app tries to imitate the sounds made by a lightsaber in the Star Wars movies. If you move the phone quickly in the X axis; i.e., in the horizontal direction, the phone will [sound](https://www.freesound.org/people/gyzhor/sounds/47125/) just like when a lightsaber swings in the movies.

### App implementation
#### Sound management
The sound management, inspired by this [StackOverflow answer](http://stackoverflow.com/a/18459352/3248221), is based on the

#### Movement recognition

. The accelerometer management implementation is based on [this code](http://bit.ly/1oBpGDI).

## Surprise app
This app uses a new kind of sensor: the proximity sensor. It completes the MovementSound app adding a way of turning on and off your lightsaber: you just have to get the phone in or off your pocket and the phone will [sound](https://www.freesound.org/people/joe93barlow/sounds/78674/) just like a when a lightsaber is turned on or off in the movies.

The implementation is all based on the MovementSound app, we just had to change the sensor used.


## References
### Get Started
 - [Android introduction tutorial](http://developer.android.com/training/index.html)
### Common resources
 - [Toolbar](http://developer.android.com/intl/es/reference/android/widget/Toolbar.html)
 - [Toast](http://developer.android.com/intl/es/guide/topics/ui/notifiers/toasts.html)
 - [Intent](http://developer.android.com/intl/es/reference/android/content/Intent.html)
 - [Log](http://developer.android.com/intl/es/reference/android/util/Log.html)
 - [Context](http://developer.android.com/intl/es/reference/android/content/Context.html)
 - [For each]( https://docs.oracle.com/javase/1.5.0/docs/guide/language/foreach.html)
 - [Icon for android application](http://stackoverflow.com/questions/5350624/set-icon-for-android-application)
 - [Motion sensors](https://developer.android.com/intl/es/guide/topics/sensors/sensors_motion.html)
 - [Position sensors](https://developer.android.com/intl/es/guide/topics/sensors/sensors_position.html)
### VoiceCompass
 - [SensorManager](http://developer.android.com/intl/es/reference/android/hardware/SensorManager.html)
 - [RecognizerIntent](http://developer.android.com/intl/es/reference/android/speech/RecognizerIntent.html)
 - [SpeechRecognizer](http://developer.android.com/intl/es/reference/android/speech/SpeechRecognizer.html)
 - [Voice easy tutorial]( http://www.jameselsey.co.uk/blogs/techblog/android-how-to-implement-voice-recognition-a-nice-easy-tutorial/)
 - [Voice recognition tutorial]( http://www.javacodegeeks.com/2012/08/android-voice-recognition-tutorial.html)
 - [Hoy to check if a string is numeric]( http://stackoverflow.com/questions/1102891/how-to-check-if-a-string-is-numeric-in-java)
 - [Extract numbers from a string]( http://stackoverflow.com/questions/10734989/extract-numbers-from-an-alpha-numeric-string-using-android)
### QrGPSPoint

- http://examples.javacodegeeks.com/android/android-barcode-and-qr-scanner-example/
- http://code.tutsplus.com/tutorials/android-sdk-create-a-barcode-reader--mobile-17162

### Movement sound
- [Sound 1](https://www.freesound.org/people/joe93barlow/sounds/78674/)
- [Accelerometer code](http://bit.ly/1oBpGDI)

#### GitHub

- https://github.com/zxing/zxing/wiki/Scanning-Via-Intent
- https://github.com/zxing/zxing
- https://github.com/journeyapps/zxing-android-embedded#custom-layout

#### SourceForge
- http://sourceforge.net/p/zbar/news/2012/03/zbar-android-sdk-version-01-released/
- http://sourceforge.net/projects/zbar/?source=typ_redirect

#### StackOverflow
- http://stackoverflow.com/questions/16080181/qr-code-reading-with-camera-android
- http://stackoverflow.com/questions/29159104/how-to-integrate-zxing-barcode-scanner-without-installing-the-actual-zxing-app
- http://stackoverflow.com/questions/27851512/how-to-integrate-zxing-library-to-android-studio-for-barcode-scanning
- http://stackoverflow.com/questions/27571530/zxing-scanner-android-studio/27573877#27573877
- http://stackoverflow.com/questions/16433860/how-to-use-zxing-library-wihtout-installing-barcodescanner-app
- http://stackoverflow.com/questions/10407159/how-to-manage-startactivityforresult-on-android/10407371#10407371
- http://stackoverflow.com/a/18459352/3248221

### GesturePhoto

[Handler](http://developer.android.com/intl/es/reference/android/os/Handler.html)
[Camera](http://developer.android.com/intl/es/guide/topics/media/camera.html#manifest)
[FrameLayout](http://developer.android.com/intl/es/reference/android/widget/FrameLayout.html)
[File](http://developer.android.com/intl/es/reference/java/io/File.html)
[FileOutputStream](http://developer.android.com/intl/es/reference/java/io/FileOutputStream.html)
[Uri](http://developer.android.com/intl/es/reference/android/net/Uri.html)
[SurfaceView](http://developer.android.com/intl/es/reference/android/view/SurfaceView.html)
[SurfaceHolder](http://developer.android.com/intl/es/reference/android/view/SurfaceHolder.html)

https://bitbucket.org/haibison/android-lockpattern/wiki/Quick-Use
http://stackoverflow.com/questions/3687315/deleting-shared-preferences
http://stackoverflow.com/questions/8034127/how-to-remove-some-key-value-pair-from-sharedpreferences
http://developer.android.com/intl/es/training/gestures/index.html
http://developer.android.com/intl/es/training/gestures/detector.html

http://developer.android.com/intl/es/guide/topics/media/camera.html#custom-camera
http://stackoverflow.com/questions/9156928/can-error-in-opening-a-camera-be-fixed-in-android
http://stackoverflow.com/questions/1520887/how-to-pause-sleep-thread-or-process-in-android
http://stackoverflow.com/questions/2543059/android-camera-in-portrait-on-surfaceview
http://stackoverflow.com/questions/20064793/how-to-fix-camera-orientation

## Surprise app
- [Sound 1](https://www.freesound.org/people/gyzhor/sounds/47125/)
