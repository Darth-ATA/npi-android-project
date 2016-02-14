# npi-android-project
A little repository for the **"Nuevos Paradigmas de Información"** apps development.

We are using Android Studio as IDE.

## VoiceCompass
### App Desription
This app gives you the posibility of tell to the phone a cardinal point with an error margin and an arrow will point this direction, when the arrow points to the provided direction, it will change his colour for inform that the user is taking the right direction.

The app will recognize every phrase that contains as first word a cardinal point *north, east, west or south* and a number, ignoring the others words told.

The recognition implementation is inspired by [Android Speech Recognition – Example](https://www.learn2crack.com/2013/12/android-speech-recognition-example.html). The compass implementation is based on those solutions [Como crear una brújula en android](http://agamboadev.esy.es/como-crear-un-brujula-en-android/) and [Create your own magnetic compass](http://www.techrepublic.com/article/pro-tip-create-your-own-magnetic-compass-using-androids-internal-sensors/).

### App implementation
#### [VoiceCompass](https://github.com/Darth-ATA/npi-android-project/blob/master/NPIToolKit/app/src/main/java/com/alejandros/npitoolkit/VoiceCompass.java)
For starts the voice recognition, the user have to touch the button in the screen. In low level this is traduced in:

```xml
    <Button
         android:id="@+id/VoiceDetectorButton"
         android:layout_width="wrap_content"
         android:layout_height="wrap_content"
         android:text="@string/recognition"
         android:onClick="onClick"
         ></Button>
```

Then the onClick method will call the Recognizer of android that needs internet connection and inform it that we need the result of the recognition as text, In low level this is traduced in:

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

## QrGPSPoint
For this developemt we decided to use a bridge withing the app and another app that scan the QR code. Using the classes **IntentIntegrator.java** and **IntentResult.java** provided by the library used **ZXing** permits the user scan the desired QR with an external application that he has installed before or the app will claim him to install it.

## GesturePhoto
For this development we decided to use as gesture all the combinations that provides de lockpatern of *Android*. For the lockpattern we use the library of [haibison](https://bitbucket.org/haibison/android-lockpattern/wiki/Quick-Use). After entering the correct lockpattern the app automatically opens a camera preview implemented using the [android development camera tutorial](http://stackoverflow.com/questions/2543059/android-camera-in-portrait-on-surfaceview) unfortunately the tutorial uses a deprecated class.

After three seconds of entering the right pattern, it takes a photo and storage it in the external storage of the phone and the app shows a message indicating where the photo has been placed.

## MovementSound
This app tries to imitate the sounds made by a lightsaber in the Star Wars movies. If you move the phone in the Y axis; i.e., in the vertical direction, the lightsaber will be turned on -or turned off, in the case it was already on-. When you do this, the phone will [sound](https://www.freesound.org/people/joe93barlow/sounds/78674/) just like when a lightsaber is turned on in the movies. Now, the lightsaber is on and the phone screen is red; if you try to move it in any of the remaining directions -X and Z axes-, the phone will [sound](https://www.freesound.org/people/gyzhor/sounds/47125/) just like when a lightsaber swings in the movies. Whenever you want to turn off the lightsaber, repeat the movement in the vertical direction: the screen will return to its normal white colour and the other movements will not have any effect.

The sound implementation is inspired by this [StackOverflow answer](http://stackoverflow.com/a/18459352/3248221). The accelerometer management implementation is based on [this code](http://bit.ly/1oBpGDI).


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
### VoiceCompass
 - [SensorManager](http://developer.android.com/intl/es/reference/android/hardware/SensorManager.html)
 - [RecognizerIntent](http://developer.android.com/intl/es/reference/android/speech/RecognizerIntent.html)
 - [SpeechRecognizer](http://developer.android.com/intl/es/reference/android/speech/SpeechRecognizer.html)
 - [Voice easy tutorial]( http://www.jameselsey.co.uk/blogs/techblog/android-how-to-implement-voice-recognition-a-nice-easy-tutorial/)
 - [Voice recognition tutorial]( http://www.javacodegeeks.com/2012/08/android-voice-recognition-tutorial.html)
 - [Hoy to check if a string is numeric]( http://stackoverflow.com/questions/1102891/how-to-check-if-a-string-is-numeric-in-java)
 - [Extract numbers from a string]( http://stackoverflow.com/questions/10734989/extract-numbers-from-an-alpha-numeric-string-using-android)
### Qr

- http://examples.javacodegeeks.com/android/android-barcode-and-qr-scanner-example/
- http://code.tutsplus.com/tutorials/android-sdk-create-a-barcode-reader--mobile-17162

### Movement sound
- [Sound 1](https://www.freesound.org/people/joe93barlow/sounds/78674/)
- [Sound 2](https://www.freesound.org/people/gyzhor/sounds/47125/)
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
