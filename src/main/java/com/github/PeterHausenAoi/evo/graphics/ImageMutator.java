package main.java.com.github.PeterHausenAoi.evo.graphics;

import java.awt.image.BufferedImage;

public interface ImageMutator {
    BufferedImage mutate(BufferedImage origin);
}