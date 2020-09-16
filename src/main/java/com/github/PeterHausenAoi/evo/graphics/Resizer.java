package main.java.com.github.PeterHausenAoi.evo.graphics;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Resizer implements ImageMutator {
    private static final String TAG = Resizer.class.getSimpleName();

    private int newWidth;
    private int newHeight;

    private ImageMutator nextMutator;

    public Resizer(int newWidth, int newHeight, ImageMutator nextMutator) {
        this.newWidth = newWidth;
        this.newHeight = newHeight;
        this.nextMutator = nextMutator;
    }

    @Override
    public BufferedImage mutate(BufferedImage origin) {
        BufferedImage resized = new BufferedImage(newWidth, newHeight, origin.getType());
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(origin, 0, 0, newWidth, newHeight, 0, 0, origin.getWidth(),
                origin.getHeight(), null);
        g.dispose();

        if (nextMutator != null){
            return nextMutator.mutate(resized);
        }

        return resized;
    }
}