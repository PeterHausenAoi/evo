package main.java.com.github.PeterHausenAoi.evo.graphics;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import main.java.com.github.PeterHausenAoi.evo.util.Log;

public class SpriteSheet {
    private static final String TAG = SpriteSheet.class.getSimpleName();

    private Sprite[] mSheet;

    public SpriteSheet(Image img, int stateCount, int maxTick, int[] directionMap) {
        mSheet = new Sprite[8];

        int width = (int)img.getWidth() / stateCount;
        int height = (int)img.getHeight() / 8;

        for (int y = 0; y < 8; y++) {
            Image[] tmp = new Image[stateCount];
            int currY = y * height;

            for (int x = 0; x < stateCount; x++) {
                int currX = x * width;
                tmp[x] = new WritableImage(img.getPixelReader(), currX, currY, width, height);
            }

            Sprite sprite = new Sprite(tmp, maxTick);

            mSheet[directionMap[y]] = sprite;
        }
    }

    public Image getImage(double angle){
        int ind;

        if (angle > 360 - 22.5 || angle < 22.5){
            ind = 0;
        }else{
            angle -= 22.5;

            ind = (int)Math.ceil(angle / 45.0);
        }

        return mSheet[ind].getNext();
    }
}