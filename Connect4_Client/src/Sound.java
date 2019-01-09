
import java.applet.*;
import java.net.*;

/**
 * Document : Client Created on : 16/04/2018, 14:02:21 Author : Daniel Abay
 */
public class Sound 
{
    private AudioClip sound; // Sound player
   
    /**
     * load sound file from URL
     * @param url a URL Image
     */
    public Sound(URL url) 
    {
        try
        {
            sound = Applet.newAudioClip(url); // Load the Sound
        } catch (Exception e)
        {
            System.err.println("Error loading sound " + url + " problem!");
        }
    }

    /**
     * Play Forever loop
     */
    public void loop()
    {
        sound.loop();   
    }

    /**
     * Stop sound
     */
    public void stop()
    {
        sound.stop();   
    }

    /**
     * Play only once
     */
    public void play()
    {
        sound.play();   
    }
}