# FreqVisualizer
Frequency Visualizer is a project which allows you to create visualizations for a given WAV files.
It uses in-place FFT (Fast Fourier Transform) to retrieve frequencies from the music chunks and then generates animation based on obtained results.
 
## How to use it
1. Clone repository
2. Build it using maven
3. Run fat jar

## Options
```
 -b,--buckets <arg>      Number of frequency buckets (default: 10)
 -d,--drawer <arg>       Name of the frame drawer class (default: ColumnDrawer)
 -f,--file <arg>         Wav file to generate animation for.
 -h,--help               Prints help message. If used with -d option provides Drawer Properties
 -p,--properties <arg>   File with properties for given drawer. Right now ColumnDrawer or LineDrawer are supported.
 -s,--speed <arg>        Desired speed of animation in fps (default: 8)
 -w,--window <arg>       Time window in which frequency extrema will be generated (default: 10)
```

## Available drawers
Drawers are classes which will generate images for a given set of frequencies. All of them implement interface `IFrameDrawer` and therefore it is easy to introduce new implementations  

| Name | Description | Image |
| --- | --- | --- |
| **ColumnDrawer** | Allows you to create basic animation where frequencies are represented by column height. | ![Column Drawer Img](https://github.com/MBajdowski/FreqVizualizer/blob/develop/src/main/resources/ColumnDrawer.JPG "Column Drawer") |
| **LineDrawer** | Allows you to create animation where frequencies are represented by a line|  ![Line Drawer Img](https://github.com/MBajdowski/FreqVizualizer/blob/develop/src/main/resources/LineDrawer.JPG "Line Drawer") |
| **Grid3dDrawer** | Allows you to create animation where frequencies are drawn on 3d grid|  ![Grid3d Drawer Img](https://github.com/MBajdowski/FreqVizualizer/blob/develop/src/main/resources/Grid3dDrawer.JPG "Grid3d Drawer") |

## Dependencies
1. In-place FFT algorithm: https://introcs.cs.princeton.edu/java/97data/InplaceFFT.java.html
2. Animation generation: http://jcodec.org/

## ToDo
* Provide simple UI

## Licence
[MIT License](https://github.com/MBajdowski/DataInImage/blob/master/LICENSE.txt)