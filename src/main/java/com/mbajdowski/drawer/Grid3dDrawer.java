package com.mbajdowski.drawer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;
import java.util.logging.Level;

import static com.mbajdowski.utils.MathHelper.scaleValueToRange;

public class Grid3dDrawer implements IFrameDrawer {

    private static final String IMG_HEIGHT = "IMG_HEIGHT";
    private static final String IMG_HEIGHT_DOC = "Image height in pixels";
    private static final String IMG_WIDTH = "IMG_WIDTH";
    private static final String IMG_WIDTH_DOC = "Image width in pixels";
    private static final String DEPTH_VALUE = "DEPTH_VALUE";
    private static final String DEPTH_VALUE_DOC = "How deep should be the grid (how many previous data should be displayed)";
    private static final String STROKE_SIZE = "STROKE_SIZE";
    private static final String STROKE_SIZE_DOC = "Thickness of the first line in pixels. Previous data will be gradually less thick up to size 1px";
    private static final String STROKE_COLOR_RGB = "STROKE_COLOR_RGB";
    private static final String STROKE_COLOR_RGB_DOC = "Color of the line in format 'r,g,b'.";
    private static final String SIZE_COEFFICIENT_X = "SIZE_COEFFICIENT_X";
    private static final String SIZE_COEFFICIENT_X_DOC = "How fast should width shrink in regards to perspective (0<value<=1)";
    private static final String SIZE_COEFFICIENT_Y = "SIZE_COEFFICIENT_Y";
    private static final String SIZE_COEFFICIENT_Y_DOC = "How fast should height shrink in regards to perspective (0<value<=1)";
    private static final java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(LineDrawer.class.getName());
    private static Map<String, String> options;

    static {
        options = new HashMap<>();
        options.put(DEPTH_VALUE, DEPTH_VALUE_DOC);
        options.put(STROKE_SIZE, STROKE_SIZE_DOC);
        options.put(STROKE_COLOR_RGB, STROKE_COLOR_RGB_DOC);
        options.put(IMG_HEIGHT, IMG_HEIGHT_DOC);
        options.put(IMG_WIDTH, IMG_WIDTH_DOC);
        options.put(SIZE_COEFFICIENT_X, SIZE_COEFFICIENT_X_DOC);
        options.put(SIZE_COEFFICIENT_Y, SIZE_COEFFICIENT_Y_DOC);
    }

    private int imgWidth;
    private int imgHeight;
    private int depth;
    private int strokeSize;
    private Color strokeColor;
    private double sizeCoefficientX;
    private double sizeCoefficientY;
    private List<int[]> pastData = new LinkedList<>();
    private Properties properties;

    public static Properties getDefaultProperties() {
        Properties properties = new Properties();
        properties.setProperty(DEPTH_VALUE, "15");
        properties.setProperty(STROKE_SIZE, "3");
        properties.setProperty(STROKE_COLOR_RGB, "255,255,255");
        properties.setProperty(SIZE_COEFFICIENT_X, "0.92");
        properties.setProperty(SIZE_COEFFICIENT_Y, "0.8");
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
        g.setColor(this.strokeColor);

        pastData.add(fftData);
        if (pastData.size() > depth + 1) {
            pastData.remove(0);
        }
        int[][] prevPoints = null;
        for (int i = 0; i < pastData.size(); i++) {
            int currentStrokeSize = scaleValueToRange(i, pastData.size() - 1, 0, this.strokeSize, 1);
            g.setStroke(new BasicStroke(currentStrokeSize));
            int[][] points = generatePoints(pastData.get(i), extrema, pastData.size() - i - 1);
            if (i != 0) {
                for (int j = 0; j < points[0].length; j++) {
                    g.drawLine(points[0][j], points[1][j], prevPoints[0][j], prevPoints[1][j]);
                }
            }
            g.setColor(Color.BLACK);
            g.fillPolygon(points[0], points[1], points[0].length);
            g.setColor(this.strokeColor);
            g.drawPolyline(points[0], points[1], points[0].length);

            prevPoints = points;
        }

        g.dispose();
        return imgFrame;
    }

    private int[][] generatePoints(int[] data, int[] extrema, int index) {
        int[][] points = new int[data.length + 2][data.length + 2];
        double sizeX = Math.pow(this.sizeCoefficientX, index) * imgWidth;
        double sizeY = Math.pow(this.sizeCoefficientY, index) * imgHeight;
        double gapX = imgWidth - sizeX;
        double gapY = imgHeight - sizeY;

        double maxX = imgWidth - 0.5 * gapX;
        double minX = 0.5 * gapX;
        double maxY = imgHeight - 0.5 * gapY;

        points[0][0] = (int) minX;
        points[1][0] = (int) maxY;
        for (int i = 0; i < data.length; i++) {
            points[0][i + 1] = (int) (sizeX / (data.length + 1) * (i + 1) + minX);
            points[1][i + 1] = (int) (maxY - scaleValueToRange(data[i], extrema[1], extrema[0], (int) sizeY, 0));
        }
        points[0][points[0].length - 1] = (int) maxX;
        points[1][points[1].length - 1] = (int) maxY;

        return points;
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
        this.depth = Integer.parseInt(this.properties.getProperty(DEPTH_VALUE));
        this.strokeSize = Integer.parseInt(this.properties.getProperty(STROKE_SIZE));
        String[] rgb = this.getProperties().getProperty(STROKE_COLOR_RGB).split(",");
        this.strokeColor = new Color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
        this.sizeCoefficientX = Double.parseDouble(this.properties.getProperty(SIZE_COEFFICIENT_X));
        this.sizeCoefficientY = Double.parseDouble(this.properties.getProperty(SIZE_COEFFICIENT_Y));
    }
}
