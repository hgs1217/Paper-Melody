package com.papermelody.core.calibration;

/**
 * Created by tangtonghui on 17/5/9.
 */

import android.util.Log;

import com.papermelody.activity.PlayActivity;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgproc.Imgproc.contourArea;


public class Calibration {


    static {
        System.loadLibrary("opencv_java3");
    }


    public static CalibrationResult main(Mat srcImage, int upbound, int lowbound) {
        Mat dstImage = new Mat();
        Size dsize = new Size(srcImage.width() /2, srcImage.height() /2);
        Imgproc.resize(srcImage,dstImage,dsize);
        Mat dilateImage = new Mat();
        Mat histImage;
        Mat medianBlurImage=new Mat();
        Mat bilateralFilterImage=new Mat();
        Mat blurImage=new Mat();
        Mat thresholdImage=new Mat();
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        //Imgproc.cvtColor(srcImage, histImage, Imgproc.COLOR_BGR2GRAY);

        histImage = ImgTransform.Hist(dstImage);
       // Log.d("TESThist", histImage.rows() + " " + histImage.cols());
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, (new Size(5,5)));



        //3、进行腐蚀


        Imgproc.dilate(histImage, dilateImage, element);//膨胀
        Imgproc.medianBlur(dilateImage,medianBlurImage,5);
        Imgproc.bilateralFilter(medianBlurImage,bilateralFilterImage,9,75,75);
        Imgproc.blur(bilateralFilterImage,blurImage,new Size(5,5));
        Imgproc.adaptiveThreshold(blurImage,thresholdImage,255,Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,Imgproc.THRESH_BINARY,11,2);


        Imgproc.findContours(thresholdImage, contours, new Mat(), Imgproc.RETR_TREE,
                Imgproc.CHAIN_APPROX_SIMPLE);
        int lencontour = contours.size();
        Log.d("TESThisthei",lencontour+" ");

        System.out.println(lencontour);
        int[] a1 = new int[lencontour];
        double [] allarea = new double [lencontour];
        int[] allcy = new int[lencontour];
        int[] a3 = new int[lencontour];
        double [] avarea = new double [lencontour];
        int[] avcy = new int[lencontour];
        double [] contourarea = new double [lencontour];
        int[] contourcy = new int[lencontour];
        Moments[] contourmoment=new Moments[lencontour];
        for (int j = 0; j < a1.length; j++) {

            a1[j] = -1;
            avarea[j] = 0;
            avcy[j]=0;
            allarea[j]=0;
            allcy[j]=0;
            a3[j] = 0;
        }

        int nnn = 0;
        int count = 0;
        boolean flag = false;
        for (int i = 0; i < lencontour; i++) {
            MatOfPoint item = contours.get(i);
            Moments m = Imgproc.moments(item);
            contourmoment[i]=m;
            if ((m.get_m00() != 0) && (contourArea(item) > 0)) {
                double d1 = (m.get_m01() / m.get_m00());
                Double D1 = new Double(d1);
                int cy = D1.intValue();
                contourcy[i]=cy;
                double area=Imgproc.contourArea(item);
                contourarea[i]=area;
                if (cy > histImage.height() *11/20 && cy < histImage.height()*19/20&& area>histImage.height()*histImage.width()/1000) {
                    //Log.d("TESThist2",cy+" ");
                    nnn += 1;
                    flag = false;
                    for (int j = 0; j < count; j++) {
                        if (Math.abs(area - avarea[j]) < avarea[j]*4/7&& Math.abs(cy - avcy[j]) <histImage.height()/17 ) {
                            flag = true;
                            a3[j] = a3[j] + 1;
                            a1[i] = j;
                            allarea[j]+=area;
                            avarea[j]=allarea[j]/a3[j];
                            allcy[j]+=cy;
                            avcy[j]=allcy[j]/a3[j];
                            break;

                        }
                    }
                    if (!flag) {
                        avarea[count] = area;
                        allarea[count]=area;
                        allcy[count] = cy ;
                        avcy[count]=cy;
                        a3[count] = 1;
                        a1[i] = count;
                        count++;
                    }

                }
            }
        }
        CalibrationResult out = new CalibrationResult();
        out.setFlag(false);
        if (nnn == 0) {
            return out;
        }

        int temp_order = 0;
        int temp = 0;

        for (int i = 0; i < count; i++) {
            if (a3[i] > temp) {
                temp = a3[i];
                temp_order = i;

            }
        }
        int temp1 = temp;
        Log.d("TESTtemp2",temp+" ");
        Log.d("TESTtemp3",temp_order+" ");
        Log.d("TESTtemp4",a1[0]+" ");
        Log.d("TESTtemp5",count+" ");
        int ddd=0;
        int avercy=avcy[temp_order];
        double averarea=avarea[temp_order];
        int leftlow_x = histImage.width(), leftlow_y = 0, leftup_x = 0, leftup_y = 0, rightlow_x = 0, rightlow_y = 0, rightup_x = 0, rightup_y = 0, leftupright_x = 0, leftupright_y = 0, rightupleft_x = 0, rightupleft_y = 0;
        System.out.println(leftlow_x);
        for (int i = 0; i < lencontour; i++) {
            if (a1[i] == temp_order) {
                ddd++;
                int cx, cy, uptemp;

                MatOfPoint item = contours.get(i);
                //Moments m = Imgproc.moments(item);
                Moments m = contourmoment[i];
                cy = contourcy[i];
                double d2 = (m.get_m10() / m.get_m00());
                Double D2 = new Double(d2);
                cx = D2.intValue();
                double area=contourarea[i];

                org.opencv.core.Point[] points = item.toArray();
                org.opencv.core.Point leftmost = points[0];
                org.opencv.core.Point rightmost = points[0];

                for (int iii = 0; iii < points.length; iii++) {
                    if (leftmost.x > points[iii].x) {
                        leftmost = points[iii];
                    }
                    if (rightmost.x < points[iii].x) {
                        rightmost = points[iii];
                    }
                }


                if (Math.abs(cy - avercy) < histImage.height()/20&&Math.abs(area - averarea) < averarea*1/2) {
                    if (leftmost.x < leftlow_x) {
                        leftlow_x = (int) leftmost.x;
                        leftlow_y = (int) leftmost.y;
                        leftupright_x = (int) rightmost.x;
                        leftupright_y = (int) rightmost.y;


                        uptemp = (int) rightmost.y;
                        leftup_x = (int) rightmost.x;
                        for (int ii = 0; ii < points.length; ii++) {
                            if (Math.abs(points[ii].y - uptemp) < 5) {
                                if (points[ii].x < leftup_x) {
                                    leftup_x = (int) points[ii].x;
                                    leftup_y = uptemp;
                                }
                            }
                        }
                    }


                    if (rightmost.x > rightlow_x) {
                        rightlow_x = (int) rightmost.x;
                        rightlow_y = (int) rightmost.y;
                        rightupleft_x = (int) leftmost.x;
                        rightupleft_y = (int) leftmost.y;

                        uptemp = (int) leftmost.y;
                        rightup_x = (int) leftmost.x;
                        for (int j = 0; j < points.length; j++) {
                            if (Math.abs(points[j].y - uptemp) < 5) {
                                if (points[j].x > rightup_x) {
                                    rightup_x = (int) points[j].x;
                                    rightup_y = uptemp;
                                }
                            }
                        }
                    }
                }


            }
        }
        leftlow_x*=2;
        leftlow_y*=2;
        leftup_x*=2;
        leftup_y*=2;
        rightlow_x*=2;
        rightlow_y*=2;
        rightup_x*=2;
        rightup_y*=2;
        leftupright_x*=2;
        leftupright_y*=2;
        rightupleft_x*=2;
        rightupleft_y*=2;
        Log.d("TESTtemp6",ddd+" ");
        out.setLeftLowX(leftlow_x);
        out.setLeftLowY(leftlow_y);
        out.setLeftUpX(leftup_x);
        out.setLeftUpY(leftup_y);
        out.setRightLowX(rightlow_x);
        out.setRightLowY(rightlow_y);
        out.setRightUpX(rightup_x);
        out.setRightUpY(rightup_y);
        out.setLeftUpRightX(leftupright_x);
        out.setLeftUpRightY(leftupright_y);
        out.setRightUpLeftX(rightupleft_x);
        out.setRightUpLeftY(rightupleft_y);
        Log.d("TESThistres",out.getLeftLowX()+"");
        Log.d("TESThistres",out.getLeftLowY()+"");
        Log.d("TESThistres",out.getLeftUpX()+"");
        Log.d("TESThistres",out.getLeftUpY()+"");
        Log.d("TESThistres",out.getRightLowX()+"");
        Log.d("TESThistres",out.getRightLowY()+"");
        Log.d("TESThistres",out.getRightUpX()+"");
        Log.d("TESThistres",out.getRightUpY()+"");
        Log.d("TESThistres",out.isFlag()+"");

        if (Math.abs(leftlow_y - leftup_y) > 5
                &&
                Math.abs(rightlow_y - rightup_y) > 5 &&
                Math.abs(rightlow_x - leftlow_x) > srcImage.width() / 2 &&
                Math.abs(rightup_x - leftup_x) > srcImage.width() / 2 &&
                temp1>12&&temp1<=16&&
                leftup_y > upbound && rightup_y > upbound &&
                leftlow_y < lowbound &&
                rightlow_y < lowbound
                &&leftlow_x<srcImage.width() / 4
                &&rightlow_x>srcImage.width()*3/4
                &&leftlow_x>0
                &&rightlow_x<srcImage.width()
                &&leftlow_x<leftup_x
                && leftlow_y > leftup_y
                &&rightlow_x>rightup_x
                && rightlow_y > rightup_y
                )
            out.setFlag(true);


            /*Mat dst = new Mat(4,1, CvT
            ype.CV_32FC2);
            dst.put(-250,0, -250,100,250,0,250,100);
            Mat perspectiveTransform = Imgproc.getPerspectiveTransform(src, dst);


            Mat Src=new Mat(1,1,CvType.CV_32FC2);
            Src.put(660,728);
            Mat Dst=new Mat(1,1,CvType.CV_32FC2);
            Core.perspectiveTransform(Src,Dst,perspectiveTransform);
            Log.d("TESTC5", String.valueOf(srcImage));*/
        return out;
    }

    public static TransformResult transform(CalibrationResult calibrationResult) {
        MatOfPoint2f src = new MatOfPoint2f(
                new org.opencv.core.Point(calibrationResult.getLeftLowX(), calibrationResult.getLeftLowY()), // tl
                new org.opencv.core.Point(calibrationResult.getLeftUpX(), calibrationResult.getLeftUpY()), // tr
                new org.opencv.core.Point(calibrationResult.getRightLowX(), calibrationResult.getRightLowY()), // br
                new org.opencv.core.Point(calibrationResult.getRightUpX(), calibrationResult.getRightUpY()) // bl
        );
        MatOfPoint2f dst = new MatOfPoint2f(
                new org.opencv.core.Point(0, 0), // tl
                new org.opencv.core.Point(0, 100), // tr
                new org.opencv.core.Point(500, 0), // br
                new org.opencv.core.Point(500, 100) // bl
        );


        TransformResult transformResult = new TransformResult();
        MatOfPoint2f Src = new MatOfPoint2f(
                new org.opencv.core.Point(calibrationResult.getLeftLowX(), calibrationResult.getLeftLowY()), // tl
                new org.opencv.core.Point(calibrationResult.getLeftUpRightX(), calibrationResult.getLeftUpRightY()), // tr
                new org.opencv.core.Point(calibrationResult.getRightLowX(), calibrationResult.getRightLowY()), // br
                new org.opencv.core.Point(calibrationResult.getRightUpLeftX(), calibrationResult.getRightUpLeftY()) // bl
        );
        MatOfPoint2f Dst = new MatOfPoint2f(
                new org.opencv.core.Point(calibrationResult.getLeftLowX(), calibrationResult.getLeftLowY()), // tl
                new org.opencv.core.Point(calibrationResult.getLeftUpRightX(), calibrationResult.getLeftUpRightY()), // tr
                new org.opencv.core.Point(calibrationResult.getRightLowX(), calibrationResult.getRightLowY()), // br
                new org.opencv.core.Point(calibrationResult.getRightUpLeftX(), calibrationResult.getRightUpLeftY()) // bl
        );


        transformResult.m = Imgproc.getPerspectiveTransform(src, dst);
        Core.perspectiveTransform(Src, Dst, transformResult.m);
        org.opencv.core.Point[] points = Dst.toArray();
        double widthleft = Math.abs(points[0].x - points[1].x);
        double widthright = Math.abs(points[2].x - points[3].x);
        transformResult.blackWidth = (widthleft + widthright) / 2;


        return transformResult;

    }

    public static int Key(TransformResult transformResult, double x, double y) {


        MatOfPoint2f Src = new MatOfPoint2f(

                new org.opencv.core.Point(x, y) // bl
        );
        MatOfPoint2f Dst = new MatOfPoint2f(

                new org.opencv.core.Point(x, y) // bl
        );


        Core.perspectiveTransform(Src, Dst, transformResult.m);

        return key(Dst.get(0, 0)[0], Dst.get(0, 0)[1], transformResult.blackWidth);


    }

    public static int key(double x, double y, double blackWidth) {
        double whiteWidth = (500 - 20 * blackWidth) / 19;
        if (y >= 100 || y < 0) {
            if (x < blackWidth / 2 && x >= -whiteWidth) return PlayActivity.KEY_C3;
            if (x >= blackWidth * 0.5 && x < whiteWidth + blackWidth * 1.5)
                return PlayActivity.KEY_D3;
            if (x >= whiteWidth + blackWidth * 1.5 && x < whiteWidth * 2 + blackWidth * 2.5)
                return PlayActivity.KEY_E3;
            if (x >= whiteWidth * 2 + blackWidth * 2.5 && x < whiteWidth * 3 + blackWidth * 3.5)
                return PlayActivity.KEY_F3;
            if (x >= whiteWidth * 3 + blackWidth * 3.5 && x < whiteWidth * 4 + blackWidth * 4.5)
                return PlayActivity.KEY_G3;
            if (x >= whiteWidth * 4 + blackWidth * 4.5 && x < whiteWidth * 5 + blackWidth * 5.5)
                return PlayActivity.KEY_A3;
            if (x >= whiteWidth * 5 + blackWidth * 5.5 && x < whiteWidth * 6 + blackWidth * 6.5)
                return PlayActivity.KEY_B3;
            if (x >= whiteWidth * 6 + blackWidth * 6.5 && x < whiteWidth * 7 + blackWidth * 7.5)
                return PlayActivity.KEY_C4;
            if (x >= whiteWidth * 7 + blackWidth * 7.5 && x < whiteWidth * 8 + blackWidth * 8.5)
                return PlayActivity.KEY_D4;
            if (x >= whiteWidth * 8 + blackWidth * 8.5 && x < whiteWidth * 9 + blackWidth * 9.5)
                return PlayActivity.KEY_E4;
            if (x >= whiteWidth * 9 + blackWidth * 9.5 && x < whiteWidth * 10 + blackWidth * 10.5)
                return PlayActivity.KEY_F4;
            if (x >= whiteWidth * 10 + blackWidth * 10.5 && x < whiteWidth * 11 + blackWidth * 11.5)
                return PlayActivity.KEY_G4;
            if (x >= whiteWidth * 11 + blackWidth * 11.5 && x < whiteWidth * 12 + blackWidth * 12.5)
                return PlayActivity.KEY_A4;
            if (x >= whiteWidth * 12 + blackWidth * 12.5 && x < whiteWidth * 13 + blackWidth * 13.5)
                return PlayActivity.KEY_B4;
            if (x >= whiteWidth * 13 + blackWidth * 13.5 && x < whiteWidth * 14 + blackWidth * 14.5)
                return PlayActivity.KEY_C5;
            if (x >= whiteWidth * 14 + blackWidth * 14.5 && x < whiteWidth * 15 + blackWidth * 15.5)
                return PlayActivity.KEY_D5;
            if (x >= whiteWidth * 15 + blackWidth * 15.5 && x < whiteWidth * 16 + blackWidth * 16.5)
                return PlayActivity.KEY_E5;
            if (x >= whiteWidth * 16 + blackWidth * 16.5 && x < whiteWidth * 17 + blackWidth * 17.5)
                return PlayActivity.KEY_F5;
            if (x >= whiteWidth * 17 + blackWidth * 17.5 && x < whiteWidth * 18 + blackWidth * 18.5)
                return PlayActivity.KEY_G5;
            if (x >= whiteWidth * 18 + blackWidth * 18.5 && x < whiteWidth * 19 + blackWidth * 19.5)
                return PlayActivity.KEY_A5;
            if (x >= whiteWidth * 19 + blackWidth * 19.5 && x < whiteWidth * 20 + blackWidth * 20.5)
                return PlayActivity.KEY_B5;
            else return 36;
        } else {
            if (x < 0 && x >= -0.5 * blackWidth + whiteWidth) return PlayActivity.KEY_C3;
            if (x < blackWidth && x >= 0) return PlayActivity.KEY_C3M;
            if (x < blackWidth + whiteWidth && x >= blackWidth) return PlayActivity.KEY_D3;
            if (x < 2 * blackWidth + whiteWidth && x >= blackWidth + whiteWidth)
                return PlayActivity.KEY_D3M;
            if (x < 2.5 * blackWidth + 2 * whiteWidth && x >= 2 * blackWidth + whiteWidth)
                return PlayActivity.KEY_E3;
            if (x < 3 * blackWidth + 3 * whiteWidth && x >= 2.5 * blackWidth + 2 * whiteWidth)
                return PlayActivity.KEY_F3;
            if (x < 4 * blackWidth + 3 * whiteWidth && x >= 3 * blackWidth + 3 * whiteWidth)
                return PlayActivity.KEY_F3M;
            if (x < 4 * blackWidth + 4 * whiteWidth && x >= 4 * blackWidth + 3 * whiteWidth)
                return PlayActivity.KEY_G3;
            if (x < 5 * blackWidth + 4 * whiteWidth && x >= 4 * blackWidth + 4 * whiteWidth)
                return PlayActivity.KEY_G3M;
            if (x < 5 * blackWidth + 5 * whiteWidth && x >= 5 * blackWidth + 4 * whiteWidth)
                return PlayActivity.KEY_A3;
            if (x < 6 * blackWidth + 5 * whiteWidth && x >= 5 * blackWidth + 5 * whiteWidth)
                return PlayActivity.KEY_A3M;
            if (x < 6.5 * blackWidth + 6 * whiteWidth && x >= 6 * blackWidth + 5 * whiteWidth)
                return PlayActivity.KEY_B3;
            if (x < 7 * blackWidth + 7 * whiteWidth && x >= 6.5 * blackWidth + 6 * whiteWidth)
                return PlayActivity.KEY_C4;
            if (x < 8 * blackWidth + 7 * whiteWidth && x >= 7 * blackWidth + 7 * whiteWidth)
                return PlayActivity.KEY_C4M;
            if (x < 8 * blackWidth + 8 * whiteWidth && x >= 8 * blackWidth + 7 * whiteWidth)
                return PlayActivity.KEY_D4;
            if (x < 9 * blackWidth + 8 * whiteWidth && x >= 8 * blackWidth + 8 * whiteWidth)
                return PlayActivity.KEY_D4M;
            if (x < 9.5 * blackWidth + 9 * whiteWidth && x >= 9 * blackWidth + 8 * whiteWidth)
                return PlayActivity.KEY_E4;
            if (x < 10 * blackWidth + 10 * whiteWidth && x >= 9.5 * blackWidth + 9 * whiteWidth)
                return PlayActivity.KEY_F4;
            if (x < 11 * blackWidth + 10 * whiteWidth && x >= 10 * blackWidth + 10 * whiteWidth)
                return PlayActivity.KEY_F4M;
            if (x < 11 * blackWidth + 11 * whiteWidth && x >= 11 * blackWidth + 10 * whiteWidth)
                return PlayActivity.KEY_G4;
            if (x < 12 * blackWidth + 11 * whiteWidth && x >= 11 * blackWidth + 11 * whiteWidth)
                return PlayActivity.KEY_G4M;
            if (x < 12 * blackWidth + 12 * whiteWidth && x >= 12 * blackWidth + 11 * whiteWidth)
                return PlayActivity.KEY_A4;
            if (x < 13 * blackWidth + 12 * whiteWidth && x >= 12 * blackWidth + 12 * whiteWidth)
                return PlayActivity.KEY_A4M;
            if (x < 13.5 * blackWidth + 13 * whiteWidth && x >= 13 * blackWidth + 12 * whiteWidth)
                return PlayActivity.KEY_B4;
            if (x < 14 * blackWidth + 14 * whiteWidth && x >= 13.5 * blackWidth + 13 * whiteWidth)
                return PlayActivity.KEY_C5;
            if (x < 15 * blackWidth + 14 * whiteWidth && x >= 14 * blackWidth + 14 * whiteWidth)
                return PlayActivity.KEY_C5M;
            if (x < 15 * blackWidth + 15 * whiteWidth && x >= 15 * blackWidth + 14 * whiteWidth)
                return PlayActivity.KEY_D5;
            if (x < 16 * blackWidth + 15 * whiteWidth && x >= 15 * blackWidth + 15 * whiteWidth)
                return PlayActivity.KEY_D5M;
            if (x < 16.5 * blackWidth + 16 * whiteWidth && x >= 16 * blackWidth + 15 * whiteWidth)
                return PlayActivity.KEY_E5;
            if (x < 17 * blackWidth + 17 * whiteWidth && x >= 16.5 * blackWidth + 16 * whiteWidth)
                return PlayActivity.KEY_F5;
            if (x < 18 * blackWidth + 17 * whiteWidth && x >= 17 * blackWidth + 17 * whiteWidth)
                return PlayActivity.KEY_F5M;
            if (x < 18 * blackWidth + 18 * whiteWidth && x >= 18 * blackWidth + 17 * whiteWidth)
                return PlayActivity.KEY_G5;
            if (x < 19 * blackWidth + 18 * whiteWidth && x >= 18 * blackWidth + 18 * whiteWidth)
                return PlayActivity.KEY_G5M;
            if (x < 19 * blackWidth + 19 * whiteWidth && x >= 19 * blackWidth + 18 * whiteWidth)
                return PlayActivity.KEY_A5;
            if (x < 20 * blackWidth + 19 * whiteWidth && x >= 19 * blackWidth + 19 * whiteWidth)
                return PlayActivity.KEY_A5M;
            if (x < 20.5 * blackWidth + 20 * whiteWidth && x >= 20 * blackWidth + 19 * whiteWidth)
                return PlayActivity.KEY_B5;
            else return 36;

        }
    }


    public static boolean whether_stable(CalibrationResultsOfLatest5 calicrationResultsOfLatest5) {
        boolean flag = true;
        if (calicrationResultsOfLatest5.n != 2) {
            flag = false;
            return flag;
        }
        if (Math.abs(calicrationResultsOfLatest5.r[0].getLeftLowX() - calicrationResultsOfLatest5.r[1].getLeftLowX()) > 10 ||
                Math.abs(calicrationResultsOfLatest5.r[0].getRightLowX() - calicrationResultsOfLatest5.r[1].getRightLowX()) > 10 //||
                //Math.abs(calicrationResultsOfLatest5.r[1].getLeftLowX() - calicrationResultsOfLatest5.r[2].getLeftLowX()) > 10

                )
            flag = false;
        return flag;
    }

    public static CalibrationResultsOfLatest5 getNewCalibrationResultsOfLatest5(CalibrationResultsOfLatest5 calibrationResultsOfLatest5, CalibrationResult calibrationResult) {
        switch (calibrationResultsOfLatest5.n) {
            case 0: {
                calibrationResultsOfLatest5.r[0] = calibrationResult;
                calibrationResultsOfLatest5.n += 1;
                break;
            }
            case 1: {
                calibrationResultsOfLatest5.r[1] = calibrationResult;
                calibrationResultsOfLatest5.n += 1;
                break;
            }
            case 2: {
                calibrationResultsOfLatest5.r[0] = calibrationResultsOfLatest5.r[1];

                calibrationResultsOfLatest5.r[1] =calibrationResult;

                break;
            }


           // case 3: {
                //calibrationResultsOfLatest5.r[0] = calibrationResultsOfLatest5.r[1];
              //  calibrationResultsOfLatest5.r[1] = calibrationResultsOfLatest5.r[2];

                //calibrationResultsOfLatest5.r[2] =calibrationResult;
               // break;


           // }


        }
        return calibrationResultsOfLatest5;
    }


}
