In order to run this demo, you must:
 * Have VLC installed, and also added the directory where all the VLC DLLs are to
the path of your machine, so that JNI can locate them.
 * If you have not installed VLC to its default path (C:\Program Files\VideoLAN\VLC)
change the vlcPath member variable of the Player class accordingly.
 * This demo program attempts to find a video file with the name ad.mp4 in the
current directory. For size considerations, a test file is not provided. Place
a file called ad.mp4 in the root folder of this eclipse project which must
contain any video format that VLC groks.
 * This demo tries to cover the whole screen and supposes that the screen has
1280 x 800 size. If this is not the case, change the corresponding member
variables X_SIZE and Y_SIZE in the Player class.
 * Run the main class com.kesdip.play.Player.