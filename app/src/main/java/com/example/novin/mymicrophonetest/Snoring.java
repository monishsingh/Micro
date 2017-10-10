package com.example.novin.mymicrophonetest;

import java.util.Arrays;

/**
 * Created by Novin on 10/10/2017.
 */


     import java.util.ArrayList;
        import java.util.Arrays;

        import com.musicg.api.DetectionApi;
        import com.musicg.wave.Wave;
        import com.musicg.wave.WaveHeader;

    public class SnoringApi extends DetectionApi {

        private WaveHeader waveHear;
        private byte[] data;
        private double[] amplitudes;
        private double threshold_E;
        private double threshold_ZCR;
        private double[] E = null;
        private double[] ZCR = null;
        private double MAX_ZCR;
        private double MIN_ZCR;
        private double AVER_ZCR;
        private double MAX_E;
        private double MIN_E;
        private double AVER_E;

        private int sampleRange = 7;

        public SnoringApi(WaveHeader waveHeader) {
            super(waveHeader);
            this.waveHeader = waveHeader;
        }

        protected void init() {
            // settings for detecting a whistle
            minFrequency = 0.0f;
            maxFrequency = 7500.0f;// Double.MAX_VALUE;

            minIntensity = 100.0f;
            maxIntensity = 100000.0f;

            minStandardDeviation = 0.01f;
            maxStandardDeviation = 29.98f;
            // 4238740267052 2599952140684
            highPass = 100;
            lowPass = 10000;

            minNumZeroCross = 0;
            maxNumZeroCross = 1267;

            numRobust = 10;
        }

        public int isSnoring(byte[] audioBytes) {
            // return isSpecificSound(audioBytes);
            int cnt = 0;
            this.data = audioBytes;
            Wave wave = new Wave(waveHeader, audioBytes); // audio bytes of this
            // frame
            // this.amplitudes = wave.getSampleAmplitudes();
            this.amplitudes = wave.getNormalizedAmplitudes();
            setE_ZCRArray(100, 50);
            cal_threshold();
            float[] res = getSnoring();
            System.out.println(Arrays.toString(res));
            int num = res.length / 2;

            if (AlarmStaticVariables.snoringCount > 0) {
                boolean ctn = true;
                for (int i = 0; i < res.length; i++) {
                    if (ctn && res[i] >= sampleRange) {
                        AlarmStaticVariables.snoringCount++;
                        if (AlarmStaticVariables.snoringCount >= AlarmStaticVariables.sampleCount) {
                            System.out.println("return here");
                            return 4;
                        }
                    } else if (!ctn && res[i] >= sampleRange) {
                        cnt++;
                        if (cnt >= AlarmStaticVariables.sampleCount) {
                            System.out.println("return 5 here");
                            return 4;
                        }
                    } else if (res[i] < sampleRange) {
                        ctn = false;
                        AlarmStaticVariables.snoringCount = 0;
                        cnt = 0;
                    }

