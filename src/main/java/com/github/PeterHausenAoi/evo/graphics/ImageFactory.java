package com.github.PeterHausenAoi.evo.graphics;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import com.github.PeterHausenAoi.evo.util.Log;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ImageFactory {
    private static final String TAG = ImageFactory.class.getSimpleName();

    private static ImageFactory mInstance;

    private static synchronized ImageFactory getInstance(){
        if (mInstance == null){
            mInstance = new ImageFactory();
        }

        return mInstance;
    }

    public static Image getImage(String code, ImageMutator mutator){
        ImageFactory factory = getInstance();
        BufferedImage img;

        if (!factory.mImages.containsKey(code)){
            img = factory.loadImage(code);
            factory.mImages.put(code, img);
        }else{
            img = factory.mImages.get(code);
        }

        if (mutator != null){
            img = mutator.mutate(img);
        }

        return SwingFXUtils.toFXImage(img, null);
    }

    private Map<String, BufferedImage> mImages;

    private ImageFactory() {
        mImages = new HashMap<>();
    }

    private BufferedImage loadImage(String file){
        BufferedImage sprite = null;

        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(file);

            if (is == null){
                return null;
            }

            sprite = ImageIO.read(is);
        } catch (Exception e) {
            Log.doLog(TAG,"ERROR: could not load file: " + "critter.png");
        }

        return sprite;
    }
}