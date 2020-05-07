package com.mbajdowski;


import com.mbajdowski.drawer.ColumnDrawer;
import com.mbajdowski.drawer.Grid3dDrawer;
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
        drawers.put(Grid3dDrawer.class.getSimpleName(), new Grid3dDrawer());

        Options options = new Options();
        options.addOption("f", "file", true, "Wav file to generate animation for.");
        options.addOption("b", "buckets", true, "Number of frequency buckets (default: 10)");
        options.addOption("s", "speed", true, "Desired speed of animation in fps (default: 8)");
        options.addOption("w", "window", true, "Time window in which frequency extrema will be generated (default: 10)");
        options.addOption("d", "drawer", true, "Name of the frame drawer class (default: ColumnDrawer)");
        options.addOption("p", "properties", true, "File with properties for given drawer. Right now ColumnDrawer, LineDrawer and Grid3dDrawer are supported.");
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
        int noFreqBuckets = Integer.parseInt(cml.getOptionValue("b", "10"));

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
        FreqVisualiser freqVisualiser = new FreqVisualiser(file, fps, noFreqBuckets);
        freqVisualiser.setFrameDrawer(drawer);
        freqVisualiser.setExtremaWindow(extremaWindow);
        freqVisualiser.generate();
    }
}
