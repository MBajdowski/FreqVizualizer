package com.mbajdowski.drawer;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Properties;

public interface IFrameDrawer {

    BufferedImage drawFFT(int[] fftData, int[] extrema);

    Map<String, String> getOptions();

    Properties getProperties();

    void setProperties(Properties properties);

}
