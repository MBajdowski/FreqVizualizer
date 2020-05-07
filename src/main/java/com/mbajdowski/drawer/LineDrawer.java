package com.mbajdowski.drawer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;
import java.util.logging.Level;

import static com.mbajdowski.utils.MathHelper.scaleValueToRange;

public class LineDrawer implements IFrameDrawer {

    private static final String IMG_HEIGHT = "IMG_HEIGHT";
    private static final String IMG_HEIGHT_DOC = "Image height in pixels";
    private static final String IMG_WIDTH = "IMG_WIDTH";
    private static final String IMG_WIDTH_DOC = "Image width in pixels";
    private static final String SHADOW_COUNT = "SHADOW_COUNT";
    private static final String SHADOW_COUNT_DOC = "Number of shadow draws that remains after the main line";
    private static final String STROKE_SIZE = "STROKE_SIZE";
    private static final String STROKE_SIZE_DOC = "Thickness of the drown line in pixels";
    private static final String STROKE_COLOR_RGB = "STROKE_COLOR_RGB";
    private static final String STROKE_COLOR_RGB_DOC = "Color of the line in format 'r,g,b'. Shadow lines will be darker shades of this color";
    private static final java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(LineDrawer.class.getName());
    private static Map<String, String> options;

    static {
        options = new HashMap<>();
        options.put(SHADOW_COUNT, SHADOW_COUNT_DOC);
        options.put(STROKE_SIZE, STROKE_SIZE_DOC);
        options.put(STROKE_COLOR_RGB, STROKE_COLOR_RGB_DOC);
        options.put(IMG_HEIGHT, IMG_HEIGHT_DOC);
        options.put(IMG_WIDTH, IMG_WIDTH_DOC);
    }

    private int imgHeight;
    private int imgWidth;
    private int shadowCount;
    private int strokeSize;
    private Color strokeColor;
    private List<int[]> list;
    private Properties properties;

    public LineDrawer() {
        this(getDefaultProperties());
    }

    public LineDrawer(Properties properties) {
        this.list = new LinkedList<>();
        this.setProperties(properties);
    }

    public static Properties getDefaultProperties() {
        Properties properties = new Properties();
        properties.setProperty(SHADOW_COUNT, "3");
        properties.setProperty(STROKE_SIZE, "3");
        properties.setProperty(STROKE_COLOR_RGB, "255,255,255");
        properties.setProperty(IMG_HEIGHT, "720");
        properties.setProperty(IMG_WIDTH, "1280");

        return properties;
    }

    @Override
    public BufferedImage drawFFT(int[] fftData, int[] extrema) {
        BufferedImage imgFrame = new BufferedImage(
                imgWidth,
                imgHeight,
                BufferedImage.TYPE_INT_RGB);

        Graphics2D g = (Graphics2D) imgFrame.getGraphics();
        g.setStroke(new BasicStroke(strokeSize));

        list.add(fftData);
        if (list.size() > shadowCount + 1) {
            list.remove(0);
        }

        int divider = shadowCount + 1;
        Color colorDelta = new Color(
                strokeColor.getRed() / divider,
                strokeColor.getGreen() / divider,
                strokeColor.getBlue() / divider);

        Color currentColor = Color.BLACK;
        for (int[] data : list) {
            currentColor = new Color(
                    currentColor.getRed() + colorDelta.getRed(),
                    currentColor.getGreen() + colorDelta.getGreen(),
                    currentColor.getBlue() + colorDelta.getBlue());
            g.setColor(currentColor);

            int[] lastPoint = {0, imgHeight};
            for (int i = 0; i < data.length; i++) {
                int newX = imgWidth / (data.length + 1) * (i + 1);
                int newY = imgHeight - scaleValueToRange(data[i], extrema[1], extrema[0], imgHeight, 0);
                int[] newPoint = {newX, newY};
                g.drawLine(lastPoint[0], lastPoint[1], newPoint[0], newPoint[1]);
                lastPoint = newPoint;
            }
            g.drawLine(lastPoint[0], lastPoint[1], imgWidth, imgHeight);
        }

        g.dispose();
        return imgFrame;
    }

    @Override
    public Map<String, String> getOptions() {
        return new HashMap<>(options);
    }

    @Override
    public Properties getProperties() {
        return this.properties;
    }

    @Override
    public void setProperties(Properties properties) {
        Properties fullProperties = getDefaultProperties();
        for (String key : properties.stringPropertyNames()) {
            if (options.containsKey(key)) {
                fullProperties.setProperty(key, properties.getProperty(key));
            } else {
                log.log(Level.WARNING, "Omitting unknown property: " + key);
            }
        }

        this.properties = fullProperties;

        this.imgHeight = Integer.parseInt(this.properties.getProperty(IMG_HEIGHT));
        this.imgWidth = Integer.parseInt(this.properties.getProperty(IMG_WIDTH));
        this.shadowCount = Integer.parseInt(this.properties.getProperty(SHADOW_COUNT));
        this.strokeSize = Integer.parseInt(this.properties.getProperty(STROKE_SIZE));

        String[] rgb = this.getProperties().getProperty(STROKE_COLOR_RGB).split(",");
        this.strokeColor = new Color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));

    }
}
