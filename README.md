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

First we need some permissions and tricks for the voice recognition. In the [AndroidManifest.xml](https://github.com/Darth-ATA/npi-android-project/blob/master/NPIToolKit/app/src/main/AndroidManifest.xml) whe have to add:
```xml
    <!-- VoiceCompass permissions and tricks ! -->
    <uses-sdk
        android:minSdkVersion="15"
        android:targetSdkVersion="19" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

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
#### Other References
- [SensorManager](http://developer.android.com/intl/es/reference/android/hardware/SensorManager.html)
- [RecognizerIntent](http://developer.android.com/intl/es/reference/android/speech/RecognizerIntent.html)
- [SpeechRecognizer](http://developer.android.com/intl/es/reference/android/speech/SpeechRecognizer.html)
- [Voice easy tutorial]( http://www.jameselsey.co.uk/blogs/techblog/android-how-to-implement-voice-recognition-a-nice-easy-tutorial/)
- [Voice recognition tutorial]( http://www.javacodegeeks.com/2012/08/android-voice-recognition-tutorial.html)
- [Hoy to check if a string is numeric]( http://stackoverflow.com/questions/1102891/how-to-check-if-a-string-is-numeric-in-java)
- [Extract numbers from a string]( http://stackoverflow.com/questions/10734989/extract-numbers-from-an-alpha-numeric-string-using-android)

## QrGPSPoint
### App Description
This app gives you the posibility to provide an Qr code that contains some info, and the user can read the information inside this Qr code.

For this developemt we decided to use a bridge withing the app and another app that scan the QR code. We only have to follow the [android qr scanner example](http://examples.javacodegeeks.com/android/android-barcode-and-qr-scanner-example/) that uses  [ZXing scanning via intent](https://github.com/zxing/zxing/wiki/Scanning-Via-Intent).

### App Implementation
#### QrGPSPoint
 - [QRGPSPoint.java](https://github.com/Darth-ATA/npi-android-project/blob/master/NPIToolKit/app/src/main/java/com/alejandros/npitoolkit/QRGPSPoint.java)
 - [content_qrgpspoint.xml](https://github.com/Darth-ATA/npi-android-project/blob/master/NPIToolKit/app/src/main/res/layout/content_qrgpspoint.xml)

For stat the Qr scan, the user have to touch the button in the screen, then the app will provide the data in text below the button with the code type (we only want scan Qr codes but **ZXing** ables to scan differents codes). In code this is traduced in:

```xml
<Button android:id="@+id/scan_button"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_centerHorizontal="true"
        android:text="@string/scan_QR"
        android:onClick="onClick"
        />
<TextView
        android:id="@+id/scan_format"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textIsSelectable="true"
        android:layout_centerHorizontal="true"
        android:layout_below="@id/scan_button"
        />
<TextView
    android:id="@+id/scan_content"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:textIsSelectable="true"
    android:layout_centerHorizontal="true"
    android:layout_below="@id/scan_format"
    />
```

Then when the user touch the button the Qr code scanner app will start using the [IntentIntegrator](https://github.com/Darth-ATA/npi-android-project/blob/master/NPIToolKit/app/src/main/java/com/google/zxing/integration/android/IntentIntegrator.java) and [IntentResult](https://github.com/Darth-ATA/npi-android-project/blob/master/NPIToolKit/app/src/main/java/com/google/zxing/integration/android/IntentResult.java) provided by **ZXing**. Then the information will be showed in the *TextView*. In code this is traduced in:

```java
    //respond to clicks
    public void onClick(View view){
        // claim to zxing app that scan the Qr code
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        //retrieve scan result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null){
            //we have a result and change the TextView with it
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();

            formatTxt.setText(getString(R.string.QR_format) + scanFormat);
            contentTxt.setText(getString(R.string.QR_content) + scanContent);
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    R.string.QR_errorScan, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
```

## GesturePhoto
### App Description
This app gives you the posibilityof create an lockpatern and when you insert this lockpattern the app will take a photo after 3 seconds.

For the lockpattern we use the library of [haibison](https://bitbucket.org/haibison/android-lockpattern/wiki/Quick-Use). After entering the correct lockpattern the app automatically opens a camera preview implemented using the [android development camera tutorial](http://stackoverflow.com/questions/2543059/android-camera-in-portrait-on-surfaceview) unfortunately the tutorial uses a deprecated class.

After three seconds of entering the right pattern, it takes a photo and storage it in the external storage of the phone and the app shows a message indicating where the photo has been placed.

### App implementation
#### GesturePhoto
 - [GesturePhoto.java](https://github.com/Darth-ATA/npi-android-project/blob/master/NPIToolKit/app/src/main/java/com/alejandros/npitoolkit/GesturePhoto.java)
 - [content_gesture_photo.xml](https://github.com/Darth-ATA/npi-android-project/blob/master/NPIToolKit/app/src/main/res/layout/content_gesture_photo.xml)

 First we need some permissions and tricks for the android camera and storage. In the [AndroidManifest.xml](https://github.com/Darth-ATA/npi-android-project/blob/master/NPIToolKit/app/src/main/AndroidManifest.xml) whe have to add:

 ```xml
    <!-- PhotoGesture permissions ! -->
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-feature android:name="android.hardware.camera" />
 ```

 For starts, the user have to touch the buttons *CreatePattern* and *InsertPattern* in the screen. In code this is traduced in:

```xml
<Button
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Create pattern"
    android:id="@+id/createPattern"/>

<Button
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Insert pattern"
    android:id="@+id/insertPattern"
    android:layout_below="@id/createPattern"/>
```

Then a field of point will be showed and the user have to choose his pattern and the same for the recognition of the pattern. All of this is implemented by the [haibison](https://bitbucket.org/haibison/android-lockpattern/wiki/Quick-Use) library. We only have to listen to the buttons events. In code this is traduced in:

```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_photo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // haibison.android.lockpattern.utils
        // Save the pattern that the user introduces
        AlpSettings.Security.setAutoSavePattern(this, true);

        // Button that ables the user to create his own pattern
        Button creationPatternButton = (Button) findViewById(R.id.createPattern);
        creationPatternButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LockPatternActivity.IntentBuilder
                        .newPatternCreator(GesturePhoto.this)
                        .startForResult(GesturePhoto.this, REQ_CREATE_PATTERN);
            }
        });

        // Button that waits to the previously defined pattern
        Button insertPatternButton = (Button) findViewById(R.id.insertPattern);
        insertPatternButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LockPatternActivity.IntentBuilder
                        .newPatternComparator(GesturePhoto.this)
                        .startForResult(GesturePhoto.this, REQ_ENTER_PATTERN);
            }
        });
    }
```

Then when the user introduces a pattern, the app have to inform the user if he isn't entering a right pattern or if he insert the right pattern it starts the camera activity. In code this is traduced in:

```java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode){
            case REQ_CREATE_PATTERN: {
                if (resultCode == RESULT_OK) {
                    char[] pattern = data.getCharArrayExtra(LockPatternActivity.EXTRA_PATTERN);
                    Toast.makeText(this, getString(R.string.photo_pattern) + pattern, Toast.LENGTH_LONG).show();
                }
                break;
            }// REQ_CREATE_PATTERN
            case REQ_ENTER_PATTERN: {
                // NOTE that there are 4 possible result codes!!!
                switch(resultCode){
                    case RESULT_OK:
                        // The user passed
                        Toast.makeText(this, R.string.photo_RES_OK, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(this, CameraActivity.class);
                        startActivity(intent);
                        break;
                    case RESULT_CANCELED:
                        // The user cancelled the task
                        Toast.makeText(this, R.string.photo_RES_CANCEL, Toast.LENGTH_LONG).show();
                        break;
                    case LockPatternActivity.RESULT_FAILED:
                        // The user failed to enter the pattern
                        Toast.makeText(this, R.string.photo_RES_FAILED, Toast.LENGTH_LONG).show();
                        break;
                    case LockPatternActivity.RESULT_FORGOT_PATTERN:
                        // The user forgot the pattern and invoked your recovery Activity
                        Toast.makeText(this, R.string.photo_RES_FORGOT, Toast.LENGTH_LONG).show();
                        break;
                }

                // In any case, there's always a key EXTRA_RETRY_COUNT, which holds the number
                // of tries that the user did
                int retryCount = data.getIntExtra(LockPatternActivity.EXTRA_RETRY_COUNT, 0);

                break;
            }// REQ_ENTER_PATTERN
        }
    }
```
#### CameraPreview
 - [CameraPreview.java](https://github.com/Darth-ATA/npi-android-project/blob/master/NPIToolKit/app/src/main/java/com/alejandros/npitoolkit/CameraPreview.java)
 - [activity_camera.xml](https://github.com/Darth-ATA/npi-android-project/blob/master/NPIToolKit/app/src/main/res/layout/activity_camera.xml)

Now we have to show the user an preview of the camera vision.

#### CameraActivity
 - [CameraActivity.java](https://github.com/Darth-ATA/npi-android-project/blob/master/NPIToolKit/app/src/main/java/com/alejandros/npitoolkit/CameraActivity.java)

After 3 seconds the photo has to be taken and storaged int he external storage of the phone.

#### Other References
- [Handler](http://developer.android.com/intl/es/reference/android/os/Handler.html)
- [Camera](http://developer.android.com/intl/es/guide/topics/media/camera.html#manifest)
- [FrameLayout](http://developer.android.com/intl/es/reference/android/widget/FrameLayout.html)
- [File](http://developer.android.com/intl/es/reference/java/io/File.html)
- [FileOutputStream](http://developer.android.com/intl/es/reference/java/io/FileOutputStream.html)
- [Uri](http://developer.android.com/intl/es/reference/android/net/Uri.html)
- [SurfaceView](http://developer.android.com/intl/es/reference/android/view/SurfaceView.html)
- [SurfaceHolder](http://developer.android.com/intl/es/reference/android/view/SurfaceHolder.html)
- [Android camera Stackoverflow](http://stackoverflow.com/questions/2543059/android-camera-in-portrait-on-surfaceview)
- [Fix camera orientation Stackoverflow](http://stackoverflow.com/questions/20064793/how-to-fix-camera-orientation)

## MovementSound
### App description
This app tries to imitate the sounds made by a lightsaber in the Star Wars movies. If you move the phone quickly in the X axis; i.e., in the horizontal direction, the phone will [sound](https://www.freesound.org/people/gyzhor/sounds/47125/) just like when a lightsaber swings in the movies.

### App implementation
This app analyzes the data read by the accelerometer, show them in the GUI and make a sound when a certain movement is made. Before see each part separately.

#### GUI elements
The GUI of this app is incredibly simple: there is just one `TextView` element, that is initialisied to some random text in case the accelerometer returns no values, and that is declared in the `content_movement_sound.xml` as follows:

```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="@string/movement_sound"
    android:id="@+id/acc_text"
    android:layout_alignParentRight="true"
    android:layout_alignParentEnd="true"
    android:layout_alignParentLeft="true"
    android:layout_alignParentStart="true"
    android:autoText="false"
    android:editable="false"
    android:textSize="@dimen/abc_text_size_body_2_material" />
```

The only interesting attribute of this `TextView` is its `id`, as we will need it to reference it later. This `TextView` will show the measures read by the accelerometer sensor.

#### Sound management
The sound management, inspired by this [StackOverflow answer](http://stackoverflow.com/a/18459352/3248221), is based on the `MediaPlayer` class, that can be instantiated with an audio file and can manage all the actions we could expect from a class of this kind: play the sound, pause it or move the player to another point of the clip, among others.

First of all, we need to import the library with the following line:
```java
import android.media.MediaPlayer;
```

The class implemented will be named `MovementSound` and, as usual, it will extend `AppCompatActivity`.

As we will see later the accelerometer management, let's focus just on the code lines that refer to the movement. The class managing the sound will look like the following:


```java
public class MovementSound extends AppCompatActivity {
    // Player variable
    MediaPlayer sound_lightSaberSwing;

    // Variables to control the time from the previous accepted movement
    long prevTimeSwing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initial settings
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movement_sound);

        // Toolbar setting
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Creation of the player for the sound
        sound_lightSaberSwing = MediaPlayer.create(this, R.raw.light_saber_swing);
    }

    // Call accelerometer onResume
    protected void onResume() {
        super.onResume();
    }

    // Call accelerometer onPause
    protected void onPause() {
        super.onPause();
    }

    // It plays the Swing sound only if 0.5 seconds have passed since the last play
    protected void lightSaberSwing(){
        if(sound_lightSaberSwing.isPlaying()){
            // Pause the sound and move the pointer to 100ms
            sound_lightSaberSwing.pause();
            sound_lightSaberSwing.seekTo(100);
        }
        sound_lightSaberSwing.start();
    }
}
```

As you can see, we declare a `MediPlayer` variable as a class attribute with `MediaPlayer sound_lightSaberSwing;`

In the `onCreate()` method, apart from the usual stuff, we assign a player to the previous variable with

```java
// Creation of the player for the sound
sound_lightSaberSwing = MediaPlayer.create(this, R.raw.light_saber_swing);
```

We have to pass the current activity to the constructor and assign a sound to the player; i.e., `R.raw.light_saber_swing`, that is just an audio file. There are a lot of accepted formats, so this is not an issue to worry about.

Apart from the `onResume()` and `onPause()` usual methods, we define a new one:

```java
// It plays the Swing sound
protected void lightSaberSwing(){
    if(sound_lightSaberSwing.isPlaying()){
        // Pause the sound and move the pointer to 100ms
        sound_lightSaberSwing.pause();
        sound_lightSaberSwing.seekTo(100);
    }
    sound_lightSaberSwing.start();
}
```

This method, when called, check whether the sound is still playing. If it actually is, it pauses the sound and move it to the beginning with `seekTo` (we move it to 100ms just because the sound has some silence in the beginning and we want to skip it). Now, the sound is either paused or finished, so we can  play it again without worries.

The prior condition check is made beacuse the accepted accelerometer movements, that we will see right now, can be really fast, so we have to stop the sound and start it again.

#### Movement recognition

The accelerometer management implementation is based on [this code](http://bit.ly/1oBpGDI). The [Android documentation](https://developer.android.com/intl/es/guide/topics/sensors/sensors_motion.html) was also widely studied.

For the implementation of the accelerometer sensor, a structure in two files was chosen: the actual accelerometer management was made in the `AccelerometerData.java` file, whereas the management of the data received was added to the `MovementSound.java` file.

##### Accelerometer management
To define a class that reads a sensor it has to implement `SensorEventListener`, so the general structure of the class will be the following:

```java
public class AccelerometerData implements SensorEventListener {
    // Sensor initialization. To be called from MovementSound
    public AccelerometerData(SensorManager sensorManager, MovementSound mainActivity) {
    }

    protected void onResume() {
    }

    protected void onPause() {
    }

    // Function called every moment the sensor changes
    public void onSensorChanged(SensorEvent event) {
    }

    // Need to be defined, but not necessary to implement
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
```

There is a constructor that should be called from the main activity; i.e., the MovementSound class, and, apart from the `onResume()` and `onPause()` methods, two functions that are called whenever the sensor has a new reading or its precision is changed. The last method has to be defined but we do not have the need to implement it, as the accuracy used will always be the same.

Let's add the following class attributes:
- A `SensorManager` to initialize the actual sensor
- A `Sensor`, that will be the accelerometer,
- A `MovementSound`, that will store the activity from where the AccelerometerData class is instantiated.
- Three arrays of floats: one to store the data, one to store the isolated gravity and another one to store the final lineal and clean accelerometer data.
- A float that will help us to isolate the gravity readings using a low-pass filter.

```java
private final SensorManager sensorManager;
private final Sensor accelerometer;
private final MovementSound mainActivity;

// Variables to store accelerometer data
private float[] accelerometerData;
private float[] gravity;
private float[] linealAcc;

// Alpha filter to analyze sensor signal
private final float alphaFilter = 0.8f;
```

The code of the constructor just have to initialize the sensor to the accelerometer and the arrays to empty arrays.


```java
// Sensor initialization. To be called from MovementSound
public AccelerometerData(SensorManager sensorManager, MovementSound mainActivity) {
    this.sensorManager = sensorManager;
    accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    this.mainActivity = mainActivity;

    accelerometerData = null;
    gravity = new float[3];
    linealAcc = new float[3];
}
```

The `sensorManager` will be initialised from the main activity; i.e., the `MovementSound` class, so we just use it to get the sensor associated with the `TYPE_ACCELEROMETER` type.

The other three lines are self-explanatory.

To implement the `onResume()` and `onPause()` methods, we just have to register/unregister the listener; i.e., to restart reading sensor data or to stop doing it. The code is as follows:

```java
protected void onResume() {
    // Listener to receive the data from the device sensor
    sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
}

protected void onPause() {
    // If the app is not active, stop listening to the sensors
    sensorManager.unregisterListener(this);
}
```

The important method, however, is the `onSensorChanged` function, that will be automatically called from the system whenever the data has new readings to analyze. Its code is the following:

```java
// Function called every moment the sensor changes
public void onSensorChanged(SensorEvent event) {
    // Sensor filter: we only want the accelerometer sensor data
    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
        accelerometerData = event.values;
    }

    // Only if the data comes from the accelerometer sensor:
    if ((accelerometerData != null)) {
        // Isolate the gravity force by using the alpha low-pass filter.
        gravity[0] = alphaFilter * gravity[0] + (1 - alphaFilter) * event.values[0];
        gravity[1] = alphaFilter * gravity[1] + (1 - alphaFilter) * event.values[1];
        gravity[2] = alphaFilter * gravity[2] + (1 - alphaFilter) * event.values[2];

        // Remove the gravity contribution to the data with a high-pass filter
        linealAcc[0] = event.values[0] - gravity[0];
        linealAcc[1] = event.values[1] - gravity[1];
        linealAcc[2] = event.values[2] - gravity[2];

        // Let the mainActivity manage the data
        mainActivity.manageData(linealAcc);
    }
}
```

First of all, we check whether the function has been called by the accelerometer or by any other sensor. In the latter case, we do nothing; in the former, we analyze and clean the data.

First of all, we isolate the gravity using a low-pass filter that will use the previous readings. This is done just to clean the readings from unwanted noise.

Then, we get the clean readings and store them in `linealAcc`. The sensor returns three readings: one for each axis, so we just have to remove the gravity from the raw readings and store the clean data.

When this is done, we pass the accelerometer readings for the `mainActivity` to manage them: obviously, we have to define a `manageData()` method in `MovementSound`, that will expect an array of floats as its single argument.

The previous code implement the accelerometer readings, but what do we do with the data now? The answer is blowing in the code of the `manageData()` method, whose code is the following:

```java
public void manageData(float[] data){
    changeAccText(data);

    // If the force in the X axis is grater than a threshold, play the Swing sound
    if(Math.abs(data[0]) > minAcc ){
        lightSaberSwing();
    }

}
```

As we see, it just updates the GUI text with a `changeAccText` method and, if the reading on the X axis is greater than a threshold (defined as `3` in the final code after experimental tests), the `lightSaberSwing` method previously explained will be executed. This little piece of code is the core of our app.

The method not yet explained is the `changeAccText`, that updates the `TextView` element:

```java
// Function to be called in order to modify the acc_text TextView.
// It will print the acceleration in all the three axes.
protected void changeAccText(float[] acc) {
    acc_text.setText(getString(R.string.sound_acc) + Float.toString(acc[0]) + " X, "
            + Float.toString(acc[1]) + " Y, "
            + Float.toString(acc[2]) + " Z");
}
```

The text is updated with a string starting with "Accelerometer: ", stored in  the `strings.xml` file, and followed by all the three readings passed.

Of course, we have to declare and define the `acc_text` as follows:
```java
//Declaration
private TextView acc_text;

//Definition on the onCreate method
acc_text = (TextView) findViewById(R.id.acc_text);
```

At last, but not least, we have to call the accelerometer constructor from within the `MovementSound` and take care of its `onResume()` and `onPause()` methods. The code added is the following:

In the `onCreate()` method from the `MovementSound` class:
```java
// Instance of the AccelerometerData class to control the data from the sensors
acelerometro = new AccelerometerData((SensorManager) getSystemService(SENSOR_SERVICE), this);
```

The `onResume()` and `onPause()` methods in the `MovementSound` class:
```java
// Call accelerometer onResume
protected void onResume() {
    super.onResume();
    acelerometro.onResume();
}

// Call accelerometer onPause
protected void onPause() {
    super.onPause();
    acelerometro.onPause();
}
```

##### Refining
After some tests, we saw that the sound could repeat thousands of times when the phone moved because of the continuous readings the sensor made.

Then, we decided to play the sound just if 500ms have passed since the last play. For implementing this improvement, little changes have to be done: we have to measure the time and check whether it has passed 500ms.

We declare a variable `long prevTimeSwing;` in the class, initialise to the current time in the `onCreate()` method with `prevTimeSwing = System.currentTimeMillis();` and update the `lightSaberSwing()` method to play the sound just if 500ms have passed:

```java
// It plays the Swing sound only if 0.5 seconds have passed since the last play
protected void lightSaberSwing(){
    // Compute the time difference from the previous play
    long difference = System.currentTimeMillis() - prevTimeSwing;

    // If the 0.5 seconds have passed, play the sound.
    if (difference > 500) {
        prevTimeSwing = System.currentTimeMillis();
        if(sound_lightSaberSwing.isPlaying()){
            // Pause the sound and move the pointer to 100ms
            sound_lightSaberSwing.pause();
            sound_lightSaberSwing.seekTo(100);
        }
        sound_lightSaberSwing.start();
    }
}
```

## Surprise app
### App description
This app uses a new kind of sensor: the proximity sensor. It completes the MovementSound app adding a way of turning on and off your lightsaber: you just have to get the phone in or off your pocket and the phone will [sound](https://www.freesound.org/people/joe93barlow/sounds/78674/) just like a when a lightsaber is turned on or off in the movies.

### App implementation
The implementation is all based on the MovementSound app. What was before the accelerometer management will be now the proximity sensor management.

#### Proximity sensor management
The class code structure is exactly the same as in `AccelerometerData`. The sensor management of Android let us use the same code for different sensors. It is then very easy to change the previous code to manage a proximity sensor:

```java
public class ProximitySensorData implements SensorEventListener {
    private final SensorManager sensorManager;
    private final Sensor proxSensor;
    private final Surprise mainActivity;

    private float[] proxData;

    // Sensor initialization. To be called from Surprise
    public ProximitySensorData(SensorManager sensorManager, Surprise mainActivity) {
        this.sensorManager = sensorManager;
        proxSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        this.mainActivity = mainActivity;
    }

    protected void onResume() {
        // Listener to receive the data from the device sensor
        sensorManager.registerListener(this, proxSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        // If the app is not active, stop listening to the sensors
        sensorManager.unregisterListener(this);
    }

    // Function called every moment the sensor changes
    public void onSensorChanged(SensorEvent event) {
        // Sensor filter: we only want the proxSensor sensor data
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY){
            proxData = event.values;
        }

        // Only if the data comes from the proxSensor sensor:
        if ((proxData != null)) {
            // Update the GUI text with the filtered proxSensor data
            mainActivity.showText(proxData[0]);
            mainActivity.manageData(proxData[0]);
        }
    }

    // Need to be defined, but not necessary to implement
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
```

As we can see, we have just changed the type of the sensor when defined in the `onCreate()` method. Now we use `Sensor.TYPE_PROXIMITY` instead of `Sensor.TYPE_ACCELEROMETER`. The remaingin constructor code is exactly the same.

The `onResume()` and `onPause()` methods remain unchanged.

In the code of `onSensorChanged()` we check now whether the data received comes from the proximity sensor. If this is the case, we don't have to do now any data cleaning or refining, so we just call the `showText()` and `manageData()` methods from the main activity; i.e., the `Surprise` class, defined in `Surprise.java` file.

#### Surprise implementation
The `Surprise` class acts in a similar way to the `MovementSound` one. Now we have to call to the `ProximitySensor` constuctor and change the `manageData()` to use the new kind of data. The code is as follows:

```java
// It plays the On sound whenever the sensor changes from near to far or viceversa.
public void manageData(float data) {
    if (prevData < 5.0 && data > 10.0) {
        prevData = data;
        sound_lightSaberOn.start();
    } else if (prevData > 10.0 && data < 5.0){
        prevData = data;

        // Changing from far to near => saving phone in pocket => play the sound unless it is
        // the first time
        if(firstTime) {
            firstTime = false;
        }
        else{
            sound_lightSaberOn.start();
        }
    }
}
```

Before explaining the code, it is necessary to explain the values returned by the sensor, as they change from device to device.

The default behaviour is that the sensor returns the distance between the sensor and the nearest object. However, most of the devices just return two values: one showing that an object is near the sensor and another one, greater, showing that the object is far from the sensor.

After some experiments with different devices, we have found that the lowest value is always under 5.0 and the greatest one is always greater than 10.0. Furthermore, we cannot rely on the values returned by the methods `getMaximumRange()` and `getResolution()` from `Sensor`, as explained in this [post](http://stackoverflow.com/a/29954988/3248221). The code uses the experimental values as thresholds to trigger the sound, but it could work wrong in some devices.

The idea is that when the user puts the phone in his/her pocket, the sound will play: we have to look for a change from a high value to a low one. When the user puts the phone out his/her pocket, we want the sound also to play, so we have to also look for a change from low to high values.

Furthermore, we need to manage the first time there is a change in the sensor. We do not want the sound to play whenever the app starts, so we have to isolate the first time when the values change from high to low. This will make the phone to start its normal behaviour only when the phone has been placed in the pocket for the first time.

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

### Movement sound
- [Sound 1](https://www.freesound.org/people/joe93barlow/sounds/78674/)
- [Accelerometer code](http://bit.ly/1oBpGDI)

## Surprise app
- [Sound 1](https://www.freesound.org/people/gyzhor/sounds/47125/)
