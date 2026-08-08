package com.afkemergency;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EmergencyButtonSpriteTest
{
    @Test
    public void allPressFramesArePackagedAndAligned() throws Exception
    {
        for (int frame = 0; frame < 8; frame++)
        {
            String path = "/com/afkemergency/emergency-button-" + frame + ".png";
            try (InputStream input = EmergencyButtonSpriteTest.class.getResourceAsStream(path))
            {
                assertNotNull(path, input);
                BufferedImage image = ImageIO.read(input);
                assertEquals(240, image.getWidth());
                assertEquals(230, image.getHeight());
            }
        }
    }
}