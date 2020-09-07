package main.java.com.github.PeterHausenAoi.evo;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextArea;
import main.java.com.github.PeterHausenAoi.evo.flow.EvoManager;
import main.java.com.github.PeterHausenAoi.evo.util.Log;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class Controller {
    public final String TAG = Controller.class.getSimpleName();

    private EvoManager mManager;

    @FXML
    private Canvas mAnimCanvas;

    @FXML
    private TextArea mLogArea;

    @FXML
    private void initialize(){
        Log.doLog(TAG, "initialize");

        Thread thread = new Thread(() -> {
            GraphicsContext g = Controller.this.mAnimCanvas.getGraphicsContext2D();
            mManager = new EvoManager(g, (int)mAnimCanvas.getWidth(), (int)mAnimCanvas.getHeight(), 50, 30);
            mManager.start();
        });

        thread.start();

        //sprite test
//        Thread thread = new Thread(){
//            @Override
//            public void run(){
//                GraphicsContext g = Controller.this.mAnimCanvas.getGraphicsContext2D();
//
//                int colCount = 8;
//                int rowCount = 8;
//
//                List<Image[]> phases = new ArrayList<>();
//                Image img = new Image("hunter.png");
//
//                int width = (int)img.getWidth() / colCount;
//                int height = (int)img.getHeight() / rowCount;
//
//                Log.doLog(TAG, "Width: " + width);
//                Log.doLog(TAG, "Height: " + height);
//
//                for (int y = 0; y < rowCount; y++) {
//                    Image[] tmp = new Image[colCount];
//                    int currY = y * height;
//
//                    for (int x = 0; x < colCount; x++) {
//                        int currX = x * width;
//                        tmp[x] = new WritableImage(img.getPixelReader(), currX, currY, width, height);
//                    }
//
//                    phases.add(tmp);
//                }
//
//                int currPhase = 0;
//                BufferedImage sprite = null;
//                try {
//                    sprite = ImageIO.read(getClass().getClassLoader().getResourceAsStream("hunter.png"));
//                } catch (Exception e) {
//                    System.out.println("ERROR: could not load file: " + "critter.png");
//                }
//
//                sprite = scaleImage(sprite, 0.5);
//                sprite = rotateImageByDegrees(sprite, 45);
//
//                img = SwingFXUtils.toFXImage(sprite, null);
//
//                while(true){
//                    g.clearRect(0, 0, mAnimCanvas.getWidth(), mAnimCanvas.getHeight());
//
//                    /*g.drawImage(img, 50,50);*/
//                    g.drawImage(img, 50,50);
//
//                    int startX = 500;
//                    int startY = 300;
//
//                    int offset = 40;
//
//                    int gg = 0;
//
//                    for (Image[] phase : phases){
//                        g.drawImage(phase[currPhase], startX + gg * offset, startY + gg * offset);
//                        gg++;
//                    }
//
//                    currPhase++;
//
//                    if (currPhase >= colCount){
//                        currPhase = 0;
//                    }
//
//
//                    try {
//                        Thread.sleep(1000 / 30);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//
//            }
//        };
//
//        thread.start();
    }

    public BufferedImage scaleImage(BufferedImage original, double scale) {
        int newWidth = new Double(original.getWidth() * scale).intValue();
        int newHeight = new Double(original.getHeight() * scale).intValue();

        BufferedImage resized = new BufferedImage(newWidth, newHeight, original.getType());
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(original, 0, 0, newWidth, newHeight, 0, 0, original.getWidth(),
                original.getHeight(), null);
        g.dispose();

        return resized;
    }

    public BufferedImage rotateImageByDegrees(BufferedImage image, double angle) {
        final double rads = Math.toRadians(angle);
        final double sin = Math.abs(Math.sin(rads));
        final double cos = Math.abs(Math.cos(rads));
        final int w = (int) Math.floor(image.getWidth() * cos + image.getHeight() * sin);
        final int h = (int) Math.floor(image.getHeight() * cos + image.getWidth() * sin);
        final BufferedImage rotatedImage = new BufferedImage(w, h, image.getType());
        final AffineTransform at = new AffineTransform();
        rotatedImage.getGraphics().setColor(Color.BLACK);
        rotatedImage.getGraphics().fillRect(0, 0, rotatedImage.getWidth(), rotatedImage.getHeight());
        at.translate(w / 2, h / 2);
        at.rotate(rads,0, 0);
        at.translate(-image.getWidth() / 2, -image.getHeight() / 2);
        final AffineTransformOp rotateOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        rotateOp.filter(image,rotatedImage);

        return rotatedImage;
    }
}
