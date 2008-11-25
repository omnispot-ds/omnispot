This eclipse project runs the KESDIP E.P.E. player. Given a deployment
descriptor (defined in appContext.xml), the player will display the contents
either on a full screen window, or a list of framed windows. This depends on
whether the deployment descriptor contains a single or multiple (respectively)
contentRoot objects. There are two sample application contexts that can
demonstrate this functionality:
appContextForFrames.xml
appContextFullScreen.xml
Make the contents of appContext.xml be the contents of either one of these files
to see the two different modes of operation being served by the same code.
Similarly, the code for the content registry, which is supposed to provide an
abstraction between the deployment descriptor and the actual content being
displayed by the player, is currently just a stub. Therefore, for the samples,
you will need a video (any video that the VLC player can play), named ad.mp4
in the same directory where this README.txt file exists. Finally, you will need
to create an eclipse runtime configuration that (apart from the default
settings), also contains the root directory of this project in the classpath.