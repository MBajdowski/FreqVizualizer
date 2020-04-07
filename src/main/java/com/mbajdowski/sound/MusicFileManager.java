package com.mbajdowski.sound;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class MusicFileManager {

    private AudioInputStream ais;

    public MusicFileManager(File file) throws IOException, UnsupportedAudioFileException {
        ais = AudioSystem.getAudioInputStream(file);
    }

    public AudioFormat getFormat(){
        return ais.getFormat();
    }

    public byte[] getChannelData(int channelIndex) throws IOException {
        int noOfChannels = ais.getFormat().getChannels();
        if(channelIndex>noOfChannels-1){
            throw new IllegalArgumentException("Channel index to out of band. Available channels: "+ noOfChannels);
        }

        byte[] data = new byte[ais.available()];
        ais.read(data);

        //Remove first empty byte
        data = Arrays.copyOfRange(data, 1, data.length);

        //Read only one channel
        byte[] result = new byte[data.length / noOfChannels];
        for (int i = 0; i < result.length; i++) {
            result[i] = data[i * noOfChannels + channelIndex];
        }

        return result;
    }


}
