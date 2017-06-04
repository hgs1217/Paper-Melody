package com.papermelody.core.calibration;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangtonghui on 17/6/1.
 */

public class ImgTransform {
    public static Mat Hist(Mat srcImage){
        Mat dstImage = new Mat();
        Mat grayImage = new Mat();
        Mat dilateImage = new Mat();
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        Mat lut= new Mat(256, 1, CvType.CV_8UC1);

        Imgproc.cvtColor(srcImage, grayImage, Imgproc.COLOR_BGR2GRAY);


        ArrayList<Mat> histsSource  = new ArrayList<Mat>();
        histsSource.add(grayImage);
        Mat hist = new Mat();
        Imgproc.calcHist(histsSource, new MatOfInt(0), new Mat(), hist, new MatOfInt(256), new MatOfFloat(0f, 256f));
        int min=0,max=0;

        for (int i=0;i<hist.size().height;i++){
            if (hist.get(i,0)[0]>0)
            { min=i;
                break;}
        }
        for (int i=(int)(hist.size().height)-1;i>=0;i--){
            if (hist.get(i,0)[0]>0)
            { max=i;
                break;}
        }
        for (int i=0;i<hist.size().height;i++){
            if (i<min){lut.put(i,0,0.0);}
            else if (i>max){lut.put(i,0,255.0);}
            else {lut.put(i,0,255.0*(i-min)/(max-min)+0.5);}


        }


        //for (int i=0;i<lut.size().height;i++){
        //System.out.println(lut.get(i,0)[0]);}
        Core.LUT(grayImage,lut,grayImage);
        return grayImage;


    }

}
