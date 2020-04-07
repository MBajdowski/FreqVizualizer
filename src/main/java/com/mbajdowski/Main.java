package com.mbajdowski;


import com.mbajdowski.drawer.ColumnDrawer;
import com.mbajdowski.drawer.IFrameDrawer;
import com.mbajdowski.drawer.LineDrawer;
import com.mbajdowski.exceptions.NotSupportedDrawerException;
import com.mbajdowski.utils.MapToStringHelper;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Main {

    public static void main(String... args) throws ParseException, IOException {
        Map<String, IFrameDrawer> drawers = new HashMap<>();
        drawers.put(LineDrawer.class.getSimpleName(), new LineDrawer());
        drawers.put(ColumnDrawer.class.getSimpleName(), new ColumnDrawer());

        Options options = new Options();
        options.addOption("f", "file", true, "Wav file to generate animation for.");
        options.addOption("b", "buckets", true, "Frequency buckets for FFT segregation in format \"22,33,44,55...\"");
        options.addOption("s", "speed", true, "Desired speed of animation in fps");
        options.addOption("w", "window", true, "Extrema window.");
        options.addOption("d", "drawer", true, "Name of the frame drawer class");
        options.addOption("p", "properties", true, "File with properties for given drawer. Right now ColumnDrawer or LineDrawer are supported.");
        options.addOption("h", "help", false, "Prints help message. If used with -d option provides Drawer Properties");

        CommandLineParser parser = new DefaultParser();
        CommandLine cml = parser.parse(options, args);

        //Help
        if (cml.hasOption("h")) {
            if (cml.hasOption("d")) {
                String drawerName = cml.getOptionValue("d");
                if (drawers.containsKey(drawerName)) {
                    System.out.println(MapToStringHelper.toString(drawers.get(drawerName).getOptions()));
                } else {
                    throw new NotSupportedDrawerException(drawerName);
                }
            } else {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp(FreqVisualiser.class.getSimpleName(), options);
            }
            return;
        }

        //File
        if (!cml.hasOption("f")) {
            throw new MissingOptionException(Collections.singletonList(options.getOption("f")));
        }
        File file = new File(cml.getOptionValue("file"));

        //FreqBuckets
        int[] freqBuckets = Arrays.stream(cml.getOptionValue("b",
                "20,25,32,40,50,63,80,100,126,159,200,252,317,399,502,632,796,1002,1262,1589,2000,2518,3170,3991,5024,6325,7963,10024,12619,15887,20000")
                .split(",")).mapToInt(Integer::parseInt).toArray();

        //Fps
        int fps = Integer.parseInt(cml.getOptionValue("s", "8"));

        //Extrema Window
        int extremaWindow = Integer.parseInt(cml.getOptionValue("w", "10"));

        //Drawer
        String drawerName = cml.getOptionValue("d", "ColumnDrawer");
        if (!drawers.containsKey(drawerName)) {
            throw new NotSupportedDrawerException(drawerName);
        }
        IFrameDrawer drawer = drawers.get(drawerName);

        //Drawer properties
        Properties prop = new Properties();
        if (cml.hasOption("p")) {
            try (InputStream input = new FileInputStream(cml.getOptionValue("p"))) {
                prop.load(input);
                drawer.setProperties(prop);
            }
        }

        //Trigger generation
        FreqVisualiser freqVisualiser = new FreqVisualiser(file, fps, freqBuckets);
        freqVisualiser.setFrameDrawer(drawer);
        freqVisualiser.setExtremaWindow(extremaWindow);
        freqVisualiser.generate();
    }
}
