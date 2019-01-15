package org.firstinspires.ftc.teamcode.Utility;

import android.graphics.Bitmap;

import java.io.IOException;
import java.util.List;

public class RoverRuckusUtilities {
    public static int[] getJewelHueCount(Bitmap bitmap,
                                       String configFilename,
                                       String bitmapFilename,
                                       String hueFilename) throws RuntimeException, InterruptedException {
        try {
            List<Integer> config = FileUtilities.readIntegerConfigFile(configFilename);
            int sampleLeftXPct = config.get(0);
            int sampleTopYPct = config.get(1);
            int sampleRightXPct = config.get(2);
            int sampleBotYPct = config.get(3);

            Bitmap babyBitmap = BitmapUtilities.getBabyBitmap(bitmap, sampleLeftXPct, sampleTopYPct, sampleRightXPct, sampleBotYPct);
            FileUtilities.writeBitmapFile(bitmapFilename, babyBitmap);
            FileUtilities.writeHueFile(hueFilename, babyBitmap);

            Bitmap babyBitmapBW = ColorUtilities.blackWhiteColorDecider(babyBitmap, 25, 40);

            String[] fileNameSplit = bitmapFilename.split(".png");
            String bwFileName = fileNameSplit[0] + "BW.png";
            FileUtilities.writeBitmapFile(bwFileName , babyBitmapBW);

            int[] hueTotal = ColorUtilities.getColorCount(babyBitmap, 25, 40);
            return hueTotal;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
