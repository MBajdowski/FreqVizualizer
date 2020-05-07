package com.mbajdowski.drawer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import static com.mbajdowski.utils.MathHelper.scaleValueToRange;

public class ColumnDrawer implements IFrameDrawer {

    private static final String NO_OF_BLOCKS_IN_COLUMN = "NO_OF_BLOCKS_IN_COLUMN";
    private static final String NO_OF_BLOCKS_IN_COLUMN_DOC = "Number of blocks in one column representing the strength of the frequency";
    private static final String COLUMN_BLOCK_PADDING = "COLUMN_BLOCK_PADDING";
    private static final String COLUMN_BLOCK_PADDING_DOC = "Padding between column blocks given in a percentage of the whole block";
    private static final String COLUMN_PADDING = "COLUMN_PADDING";
    private static final String COLUMN_PADDING_DOC = "Padding of the column to the top and bottom border of the image, given in percentage of the height of the image";
    private static final String IMG_HEIGHT = "IMG_HEIGHT";
    private static final String IMG_HEIGHT_DOC = "Image height in pixels";
    private static final String IMG_WIDTH = "IMG_WIDTH";
    private static final String IMG_WIDTH_DOC = "Image width in pixels";
    private static final java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(ColumnDrawer.class.getName());
    private static final HashMap<String, String> options;

    static {
        options = new HashMap<>();
        options.put(NO_OF_BLOCKS_IN_COLUMN, NO_OF_BLOCKS_IN_COLUMN_DOC);
        options.put(COLUMN_BLOCK_PADDING, COLUMN_BLOCK_PADDING_DOC);
        options.put(COLUMN_PADDING, COLUMN_PADDING_DOC);
        options.put(IMG_HEIGHT, IMG_HEIGHT_DOC);
        options.put(IMG_WIDTH, IMG_WIDTH_DOC);
    }

    private int imgHeight;
    private int imgWidth;
    private int noOfBlocksInColumn;
    private int maxOut;
    private int base;
    private int blockHeight;
    private int recHeight;
    private Properties properties;

    public ColumnDrawer() {
        this(getDefaultProperties());
    }

    public ColumnDrawer(Properties properties) {
        this.setProperties(properties);
    }

    public static Properties getDefaultProperties() {
        Properties properties = new Properties();
        properties.setProperty(NO_OF_BLOCKS_IN_COLUMN, "10");
        properties.setProperty(COLUMN_BLOCK_PADDING, "0.1f");
        properties.setProperty(COLUMN_PADDING, "0.1f");
        properties.setProperty(IMG_HEIGHT, "720");
        properties.setProperty(IMG_WIDTH, "1280");

        return properties;
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
        this.noOfBlocksInColumn = Integer.parseInt(this.properties.getProperty(NO_OF_BLOCKS_IN_COLUMN));
        float columnPadding = Float.parseFloat(this.properties.getProperty(COLUMN_PADDING));
        float columnBlocksPadding = Float.parseFloat(this.properties.getProperty(COLUMN_BLOCK_PADDING));

        this.maxOut = (int) (imgHeight * (1 - columnPadding));
        this.base = (int) (imgHeight * columnPadding / 2);
        this.blockHeight = maxOut / noOfBlocksInColumn;
        this.recHeight = (int) ((1 - columnBlocksPadding) * this.blockHeight);
    }

    @Override
    public BufferedImage drawFFT(int[] fftData, int[] extrema) {
        int barWidthInPix = imgWidth / (fftData.length * 2 + 1);

        BufferedImage imgFrame = new BufferedImage(
                imgWidth,
                imgHeight,
                BufferedImage.TYPE_INT_RGB);

        Graphics g = imgFrame.getGraphics();

        for (int i = 0; i < fftData.length; i++) {
            int height = scaleValueToRange(fftData[i], extrema[1], extrema[0], maxOut, 0);
            int noOfBlocks = Math.min((int) Math.ceil(height / (double) this.blockHeight), noOfBlocksInColumn);

            int blockColumnHeight = noOfBlocks * this.blockHeight;

            int x = barWidthInPix * (2 * i + 1);
            int y = imgHeight - blockColumnHeight - this.base;

            drawColumn(g, x, y, barWidthInPix, noOfBlocks);
        }
        g.dispose();

        return imgFrame;
    }

    private void drawColumn(Graphics g, int x, int y, int barWidthInPix, int noOfBlocks) {
        int currentY = y;
        float[] sections = {0.2f, 0.8f};
        Color[] colors = {Color.RED, Color.YELLOW, Color.GREEN};
        int diff = noOfBlocksInColumn - noOfBlocks;
        for (int i = 0; i < noOfBlocks; i++, currentY += this.blockHeight) {
            //int index = this.noOfBlocksInColumn-i;
            int index = i + diff;
            if (index / (float) noOfBlocksInColumn < sections[0]) {
                g.setColor(colors[0]);
            } else if (index / (float) noOfBlocksInColumn < sections[1]) {
                g.setColor(colors[1]);
            } else {
                g.setColor(colors[2]);
            }

            g.fillRect(x, currentY, barWidthInPix, this.recHeight);
        }
    }

}
