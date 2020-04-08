package com.mbajdowski;

import com.mbajdowski.drawer.ColumnDrawer;
import com.mbajdowski.drawer.IFrameDrawer;
import com.mbajdowski.fft.Complex;
import com.mbajdowski.fft.InplaceFFT;
import com.mbajdowski.sound.MusicFileManager;
import com.mbajdowski.utils.MathHelper;
import com.mbajdowski.utils.ProgressPrinter;
import org.jcodec.api.awt.AWTSequenceEncoder;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.mbajdowski.utils.MathHelper.*;

public class FreqVisualiser {

    private File file;
    private int fps;
    private int[] freqBuckets;
    private IFrameDrawer frameDrawer;
    private int extremaWindow;

    public FreqVisualiser(File file, int fps, int noFreqBuckets) {
        this(file, fps, noFreqBuckets, new ColumnDrawer(), 10);
    }

    public FreqVisualiser(File file, int fps, int noFreqBuckets, IFrameDrawer frameDrawer, int extremaWindowInSec) {
        this.file = file;
        this.fps = fps;
        this.frameDrawer = frameDrawer;
        this.extremaWindow = extremaWindowInSec * fps;
        this.freqBuckets = MathHelper.generateFreqBuckets(noFreqBuckets);
    }

    public void setFrameDrawer(IFrameDrawer frameDrawer) {
        this.frameDrawer = frameDrawer;
    }

    public void setExtremaWindow(int extremaWindow) {
        this.extremaWindow = extremaWindow;
    }

    public void generate() {

        try {
            File tmpResult = new File(file.getAbsolutePath() + ".video.mp4");

            MusicFileManager mfm = new MusicFileManager(file);

            float sampleRate = mfm.getFormat().getSampleRate();
            int sampleSize = mfm.getFormat().getSampleSizeInBits() / 8;
            int desiredSamplesPerSecond = (int) sampleRate / fps;
            int samplesPerFrame = findNearestPowerOfTwo(desiredSamplesPerSecond);
            byte[] data = mfm.getChannelData(0);

            int framesInTotal = (int) Math.ceil(data.length / (double) (desiredSamplesPerSecond * sampleSize));
            List<int[]> mappedFreqList = new LinkedList<>();

            //Calculate fft
            ProgressPrinter fftProgressPrinter = new ProgressPrinter("FFT Calculation", 20, framesInTotal);
            for (int i = 0; i < framesInTotal; i++) {
                int from = sampleSize * i * desiredSamplesPerSecond;
                int to = from + sampleSize * samplesPerFrame;

                int[] intArr = byteArrayToIntArray(Arrays.copyOfRange(data, from, to));

                Complex[] fft = convertToComplexArray(intArr);
                fft = InplaceFFT.fft(fft);

                //Remove half of the results (Niquist limit)
                //Remove first frequency (static gain) - start from 1
                fft = Arrays.copyOfRange(fft, 1, fft.length / 2);
                int[] fftAbs = convertToIntArray(fft);

                int[] mappedFreq = mapFrequenciesToBuckets(fftAbs, freqBuckets, sampleRate);

                mappedFreqList.add(mappedFreq);
                fftProgressPrinter.incrementAndPrint();
            }

            AWTSequenceEncoder encoder = AWTSequenceEncoder.createSequenceEncoder(tmpResult, fps);

            //Draw frames
            ProgressPrinter drawerProgressPrinter = new ProgressPrinter("Animation Generation", 20, framesInTotal);
            for (int i = 0; i < mappedFreqList.size(); i++) {
                int[] mappedFreq = mappedFreqList.get(i);
                int from = Math.max(i - extremaWindow / 2, 0);
                int to = Math.min(i + extremaWindow / 2, mappedFreqList.size());
                int[] localExtrema = findExtrema(mappedFreqList.subList(from, to));
                BufferedImage imgFrame = frameDrawer.drawFFT(mappedFreq, localExtrema);
                encoder.encodeImage(imgFrame);

                drawerProgressPrinter.incrementAndPrint();
            }
            encoder.finish();

        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

    private int[] mapFrequenciesToBuckets(int[] fftAbs, int[] freqBuckets, float sampleRate) {
        int[] highestArray = new int[freqBuckets.length];
        float coefficient = 1.5f;
        int minJ = 0;

        for (int i = 0; i < fftAbs.length; i++) {
            float freq = (i + 1) * sampleRate / (fftAbs.length * 2);
            for (int j = minJ; j < freqBuckets.length; j++) {
                if (freq <= freqBuckets[j] * coefficient) {
                    if (highestArray[j] < fftAbs[i]) {
                        highestArray[j] = fftAbs[i];
                    }
                    break;
                }
                minJ++;
            }
        }

        return highestArray;
    }
}
