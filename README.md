# npi-android-project
A little repository for the "Nuevos Paradigmas de Información" apps development.

We are using Android Studio as IDE.

## VoiceCompass

## QrGPSPoint
For his developemt it decides to use a bridge withing the app and another app that scan the QR code. Using the classes **IntentIntegrator.java** and **IntentResult.java** provided by the library used **ZXing** permits the user scan the desired QR with an external application that he has installed before or the app will claim him to install it.

## GesturePhoto
For his development it decides to use as gesture all the combinations that provides de lockpatern of *Android*. For the lockpattern we use the library of [haibison](https://bitbucket.org/haibison/android-lockpattern/wiki/Quick-Use). After entering the correct lockpattern the app automatically opens a camera preview implemented using the [android development camera tutorial](http://stackoverflow.com/questions/2543059/android-camera-in-portrait-on-surfaceview) unfortunately the tutorial uses a deprecated class.

After three seconds of entering the right pattern, it takes a photo and storaged in the external storage of the phone and the app shown a little message indicating where the photo has been placed.

## References
### Get Started
[Tutorial](http://developer.android.com/training/index.html)
### Common resources
[Toolbar](http://developer.android.com/intl/es/reference/android/widget/Toolbar.html)
[Toast](http://developer.android.com/intl/es/guide/topics/ui/notifiers/toasts.html)
[Intent](http://developer.android.com/intl/es/reference/android/content/Intent.html)
[Log](http://developer.android.com/intl/es/reference/android/util/Log.html)
[Context](http://developer.android.com/intl/es/reference/android/content/Context.html)

https://docs.oracle.com/javase/1.5.0/docs/guide/language/foreach.html
### VoiceCompass
[SensorManager](http://developer.android.com/intl/es/reference/android/hardware/SensorManager.html)
[RecognizerIntent](http://developer.android.com/intl/es/reference/android/speech/RecognizerIntent.html)
[SpeechRecognizer](http://developer.android.com/intl/es/reference/android/speech/SpeechRecognizer.html)

http://stackoverflow.com/questions/20497087/manifest-xml-when-using-sensors
http://www.codingforandroid.com/2011/01/using-orientation-sensors-simple.html
http://www.jameselsey.co.uk/blogs/techblog/android-how-to-implement-voice-recognition-a-nice-easy-tutorial/
http://www.javacodegeeks.com/2012/08/android-voice-recognition-tutorial.html
https://www.learn2crack.com/2013/12/android-speech-recognition-example.html
http://stackoverflow.com/questions/1102891/how-to-check-if-a-string-is-numeric-in-java
http://stackoverflow.com/questions/10734989/extract-numbers-from-an-alpha-numeric-string-using-android
### Qr

- http://examples.javacodegeeks.com/android/android-barcode-and-qr-scanner-example/
- http://code.tutsplus.com/tutorials/android-sdk-create-a-barcode-reader--mobile-17162

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

http://stackoverflow.com/questions/3848148/sending-arrays-with-intent-putextra
