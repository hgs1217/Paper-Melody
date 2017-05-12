package com.papermelody.core.calibration;

/**
 * Created by tangtonghui on 17/5/9.
 */

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.papermelody.R.raw.b1;
import static com.papermelody.R.raw.b2;
import static com.papermelody.R.raw.b3;
import static com.papermelody.R.raw.b4;
import static com.papermelody.R.raw.b5;
import static com.papermelody.R.raw.b6;
import static com.papermelody.R.raw.b7;


public class Calibration {


    static{ System.loadLibrary("opencv_java3"); }


    public static CalibrationResult main(Mat srcImage){

        Mat dstImage = new Mat();
        Mat grayImage = new Mat();
        Mat dilateImage = new Mat();
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Imgproc.cvtColor(srcImage, grayImage, Imgproc.COLOR_BGR2GRAY);
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, (new Size(15,5)));

        //3、进行腐蚀

        Imgproc.dilate(grayImage, dilateImage, element);//膨胀
        Imgproc.Canny(dilateImage, dstImage, 50, 150, 3, false);
        Imgproc.findContours(dstImage, contours, new Mat(), Imgproc.RETR_TREE,
                Imgproc.CHAIN_APPROX_SIMPLE);
        int lencontour=contours.size();
        System.out.println( lencontour);
        int[ ] a1 = new int[ lencontour] ;
        int[ ] a2 = new int[ lencontour] ;
        int[ ] a3 = new int[ lencontour] ;
        for (int j = 0; j<a1.length; j++){

            a1[j]=-1;
            a2[j]=0;
            a3[j]=0;
        }
        int nnn=0;
        int count=0;
        boolean flag=false;
        for (int i=0;i<lencontour;i++){
            MatOfPoint item=contours.get(i);
            Moments m=Imgproc.moments(item);
            if ((m.get_m00()!=0)&&(Imgproc.contourArea(item)>20)){
                double d1 =(m.get_m01()/m.get_m00());
                Double D1=new Double(d1);
                int cy=D1.intValue();
                if (cy>dstImage.height()/2){
                    nnn+=1;
                    flag=false;
                    for (int j=0;j<count;j++){
                        if (Math.abs(cy-a2[j])<20){
                            flag=true;
                            a3[j]=a3[j]+1;
                            a1[i]=j;
                            break;

                        }
                    }
                    if (!flag){
                        a2[count]=cy;
                        a3[count]=1;
                        a1[i]=count;
                        count++;
                    }

                }
            }
        }
        CalibrationResult out = new CalibrationResult();
        if (nnn==0){return out;}

        int temp_order=0;
        int temp=0;

        for (int i=0;i<count;i++){
            if (a3[i]>temp){
                temp=a3[i];
                temp_order=i;

            }
        }
        int temp1 = temp;
        temp=a2[temp_order];
        int leftlow_x=dstImage.width(),leftlow_y=0,leftup_x=0,leftup_y=0,rightlow_x=0,rightlow_y=0,rightup_x=0,rightup_y=0,leftupright_x=0,leftupright_y=0,rightupleft_x=0,rightupleft_y=0;
        System.out.println( leftlow_x);
        for (int i=0;i<lencontour;i++){
            if (a1[i]==temp_order){
                int cx,cy,uptemp;
                MatOfPoint item=contours.get(i);
                Moments m=Imgproc.moments(item);

                double d1 =(m.get_m01()/m.get_m00());
                Double D1=new Double(d1);
                cy=D1.intValue();
                double d2 =(m.get_m10()/m.get_m00());
                Double D2=new Double(d2);
                cx=D2.intValue();

                org.opencv.core.Point[] points = item.toArray();
                org.opencv.core.Point leftmost=points[0] ;
                org.opencv.core.Point rightmost=points[0] ;

                for (int iii = 0; iii < points.length; iii++)
                {                    if (leftmost.x>points[iii].x){leftmost=points[iii];}
                    if (rightmost.x<points[iii].x){rightmost=points[iii];}
                }



                if (Math.abs(cy-temp)<20){
                    if (leftmost.x<leftlow_x){
                        leftlow_x=(int)leftmost.x;
                        leftlow_y=(int)leftmost.y;
                        leftupright_x=(int)rightmost.x;
                        leftupright_y=(int)rightmost.y;


                        uptemp=(int)rightmost.y;
                        leftup_x=(int)rightmost.x;
                        for (int ii=0;ii<points.length;ii++ ) {
                            if (Math.abs(points[ii].y - uptemp) < 5){
                                if (points[ii].x < leftup_x){
                                    leftup_x =(int) points[ii].x;
                                    leftup_y=uptemp;
                                }}}}



                    if (rightmost.x>rightlow_x){
                        rightlow_x=(int)rightmost.x;
                        rightlow_y=(int)rightmost.y;
                        rightupleft_x=(int)leftmost.x;
                        rightupleft_y=(int)leftmost.y;

                        uptemp=(int)leftmost.y;
                        rightup_x=(int)leftmost.x;
                        for (int j=0;j<points.length;j++ ){
                            if (Math.abs(points[j].y-uptemp)<5){
                                if (points[j].x>rightup_x){
                                    rightup_x=(int)points[j].x;
                                    rightup_y=uptemp;}}}}
                }


            }
        }
        out.leftLowX=leftlow_x;
        out.leftLowY=leftlow_y;
        out.leftUpX=leftup_x;
        out.leftUpY=leftup_y;
        out.rightUpX=rightup_x;
        out.rightUpY=rightup_y;
        out.rightLowX=rightlow_x;
        out.rightLowY=rightlow_y;
        out.leftUpRightX=leftupright_x;
        out.leftUpRightY=leftupright_y;
        out.rightUpLeftX=rightupleft_x;
        out.rightUpLeftY=rightupleft_y;


        if(Math.abs(leftlow_y-rightlow_y)<10&&Math.abs(leftup_y-rightup_y)<10&&temp1>8)
            out.flag=true;


            /*Mat dst = new Mat(4,1, CvType.CV_32FC2);
            dst.put(-250,0, -250,100,250,0,250,100);
            Mat perspectiveTransform = Imgproc.getPerspectiveTransform(src, dst);


            Mat Src=new Mat(1,1,CvType.CV_32FC2);
            Src.put(660,728);
            Mat Dst=new Mat(1,1,CvType.CV_32FC2);
            Core.perspectiveTransform(Src,Dst,perspectiveTransform);
            Log.d("TESTC5", String.valueOf(srcImage));*/
        return out;
    }
    public static TranformResult transform(CalibrationResult calibrationResult){
        MatOfPoint2f src = new MatOfPoint2f(
                new org.opencv.core.Point(calibrationResult.getLeftLowX(),calibrationResult.getLeftLowY()), // tl
                new org.opencv.core.Point(calibrationResult.getLeftUpX(),calibrationResult.getLeftUpY()), // tr
                new org.opencv.core.Point(calibrationResult.getRightLowX(),calibrationResult.getRightLowY()), // br
                new org.opencv.core.Point(calibrationResult.getRightUpX(),calibrationResult.getRightUpY()) // bl
        );
        MatOfPoint2f dst = new MatOfPoint2f(
                new org.opencv.core.Point(0,0), // tl
                new org.opencv.core.Point(0,100), // tr
                new org.opencv.core.Point(500,0), // br
                new org.opencv.core.Point(500,100) // bl
        );


        TranformResult tranformResult=new  TranformResult();
        MatOfPoint2f Src = new MatOfPoint2f(
                new org.opencv.core.Point(calibrationResult.getLeftLowX(),calibrationResult.getLeftLowY()), // tl
                new org.opencv.core.Point(calibrationResult.getLeftUpRightX(),calibrationResult.getLeftUpRightY()), // tr
                new org.opencv.core.Point(calibrationResult.getRightLowX(),calibrationResult.getRightLowY()), // br
                new org.opencv.core.Point(calibrationResult.getRightUpLeftX(),calibrationResult.getRightUpLeftY()) // bl
        );
        MatOfPoint2f Dst = new MatOfPoint2f(
                new org.opencv.core.Point(calibrationResult.getLeftLowX(),calibrationResult.getLeftLowY()), // tl
                new org.opencv.core.Point(calibrationResult.getLeftUpRightX(),calibrationResult.getLeftUpRightY()), // tr
                new org.opencv.core.Point(calibrationResult.getRightLowX(),calibrationResult.getRightLowY()), // br
                new org.opencv.core.Point(calibrationResult.getRightUpLeftX(),calibrationResult.getRightUpLeftY()) // bl
        );




        tranformResult.m = Imgproc.getPerspectiveTransform(src, dst);
        Core.perspectiveTransform(Src,Dst,tranformResult.m);
        org.opencv.core.Point[] points = Dst.toArray();
        double widthleft=Math.abs(points[0].x-points[1].x);
        double widthright=Math.abs(points[2].x-points[3].x);
        tranformResult.blackWidth=(widthleft+widthright)/2;


        return tranformResult;

    }
    public static int[] key(TranformResult tranformResult,int []fingerpoint){



        MatOfPoint2f Src = new MatOfPoint2f(

                new org.opencv.core.Point(fingerpoint[0],fingerpoint[1]) // bl
        );
        MatOfPoint2f Dst= new MatOfPoint2f(

                new org.opencv.core.Point(fingerpoint[0],fingerpoint[1]) // bl
        );
        System.out.println( Dst.get(0,0)[1]);
        System.out.println( Dst.get(0,0)[0]);

        Core.perspectiveTransform(Src,Dst,m);
        System.out.println( Dst.get(0,0)[1]);
        System.out.println( Dst.get(0,0)[0]);
        int output[ ] =new int []{(int)Dst.get(0,0)[0],(int)Dst.get(0,0)[1]} ;

return output;
    }
    public static int key(int x,int y,int blackWidth){
        double whiteWidth=(500-20*blackWidth)/19;
        if (y>100||y<0){
            if (x<blackWidth/2&&x>-whiteWidth)return w1;
            if (x>=blackWidth*0.5&&x<whiteWidth+blackWidth*1.5)return w2;
            if (x>=whiteWidth+blackWidth*1.5&&x<whiteWidth*2+blackWidth*2.5)return w3;
            if (x>=whiteWidth*2+blackWidth*2.5&&x<whiteWidth*3+blackWidth*3.5)return w4;
            if (x>=whiteWidth*3+blackWidth*3.5&&x<whiteWidth*4+blackWidth*4.5)return w5;
            if (x>=whiteWidth*4+blackWidth*4.5&&x<whiteWidth*5+blackWidth*5.5)return w6;
            if (x>=whiteWidth*5+blackWidth*5.5&&x<whiteWidth*6+blackWidth*6.5)return w7;
            if (x>=whiteWidth*6+blackWidth*6.5&&x<whiteWidth*7+blackWidth*7.5)return w8;
            if (x>=whiteWidth*7+blackWidth*7.5&&x<whiteWidth*8+blackWidth*8.5)return w9;
            if (x>=whiteWidth*8+blackWidth*8.5&&x<whiteWidth*9+blackWidth*9.5)return w10;
            if (x>=whiteWidth*9+blackWidth*9.5&&x<whiteWidth*10+blackWidth*10.5)return w11;
            if (x>=whiteWidth*10+blackWidth*10.5&&x<whiteWidth*11+blackWidth*11.5)return w12;
            if (x>=whiteWidth*11+blackWidth*11.5&&x<whiteWidth*12+blackWidth*12.5)return w13;
            if (x>=whiteWidth*12+blackWidth*12.5&&x<whiteWidth*13+blackWidth*13.5)return w14;
            if (x>=whiteWidth*13+blackWidth*13.5&&x<whiteWidth*14+blackWidth*14.5)return w15;
            if (x>=whiteWidth*14+blackWidth*14.5&&x<whiteWidth*15+blackWidth*15.5)return w16;
            if (x>=whiteWidth*15+blackWidth*15.5&&x<whiteWidth*16+blackWidth*16.5)return w17;
            if (x>=whiteWidth*16+blackWidth*16.5&&x<whiteWidth*17+blackWidth*17.5)return w18;
            if (x>=whiteWidth*17+blackWidth*17.5&&x<whiteWidth*18+blackWidth*18.5)return w19;
            if (x>=whiteWidth*18+blackWidth*18.5&&x<whiteWidth*19+blackWidth*19.5)return w20;
            if (x>=whiteWidth*19+blackWidth*19.5&&x<whiteWidth*20+blackWidth*20.5)return w21;
        else {
                if (x<blackWidth&&x>0)return b1;
                if (x<blackWidth+whiteWidth&&x>blackWidth)return w2;
                if (x<2*blackWidth+whiteWidth&&x>blackWidth+whiteWidth)return b2;
                if (x<2.5*blackWidth+2*whiteWidth&&x>2*blackWidth+whiteWidth)return w3;
                if (x<3*blackWidth+3*whiteWidth&&x>2.5*blackWidth+2*whiteWidth)return w4;
                if (x<4*blackWidth+3*whiteWidth&&x>3*blackWidth+3*whiteWidth)return b3;
                if (x<4*blackWidth+4*whiteWidth&&x>4*blackWidth+3*whiteWidth)return w5;
                if (x<5*blackWidth+4*whiteWidth&&x>4*blackWidth+4*whiteWidth)return b4;
                if (x<5*blackWidth+5*whiteWidth&&x>5*blackWidth+4*whiteWidth)return w6;
                if (x<6*blackWidth+5*whiteWidth&&x>5*blackWidth+5*whiteWidth)return b5;
                if (x<6.5*blackWidth+6*whiteWidth&&x>6*blackWidth+5*whiteWidth)return w7;
                if (x<7*blackWidth+7*whiteWidth&&x>6.5*blackWidth+6*whiteWidth)return w8;
                if (x<8*blackWidth+7*whiteWidth&&x>7*blackWidth+7*whiteWidth)return b6;
                if (x<8*blackWidth+8*whiteWidth&&x>8*blackWidth+7*whiteWidth)return w9;
                if (x<9*blackWidth+8*whiteWidth&&x>8*blackWidth+8*whiteWidth)return b7;
                if (x<9.5*blackWidth+9*whiteWidth&&x>9*blackWidth+8*whiteWidth)return w10;
                if (x<10*blackWidth+10*whiteWidth&&x>9.5*blackWidth+9*whiteWidth)return w11;
                if (x<11*blackWidth+10*whiteWidth&&x>10*blackWidth+10*whiteWidth)return b8;
                if (x<11*blackWidth+11*whiteWidth&&x>11*blackWidth+10*whiteWidth)return w12;
                if (x<12*blackWidth+11*whiteWidth&&x>11*blackWidth+11*whiteWidth)return b9;
                if (x<12*blackWidth+12*whiteWidth&&x>12*blackWidth+11*whiteWidth)return w13;
                if (x<13*blackWidth+12*whiteWidth&&x>12*blackWidth+12*whiteWidth)return b10;
                if (x<13.5*blackWidth+13*whiteWidth&&x>13*blackWidth+12*whiteWidth)return w14;
                if (x<14*blackWidth+14*whiteWidth&&x>13.5*blackWidth+13*whiteWidth)return w15;
                if (x<15*blackWidth+14*whiteWidth&&x>14*blackWidth+14*whiteWidth)return b11;
                if (x<15*blackWidth+15*whiteWidth&&x>15*blackWidth+14*whiteWidth)return w16;
                if (x<16*blackWidth+15*whiteWidth&&x>15*blackWidth+15*whiteWidth)return b12;
                if (x<16.5*blackWidth+16*whiteWidth&&x>16*blackWidth+15*whiteWidth)return w17;
                if (x<17*blackWidth+17*whiteWidth&&x>16.5*blackWidth+16*whiteWidth)return w18;
                if (x<18*blackWidth+17*whiteWidth&&x>17*blackWidth+17*whiteWidth)return b13;
                if (x<18*blackWidth+18*whiteWidth&&x>18*blackWidth+17*whiteWidth)return w19;
                if (x<19*blackWidth+18*whiteWidth&&x>18*blackWidth+18*whiteWidth)return b14;
                if (x<19*blackWidth+19*whiteWidth&&x>19*blackWidth+18*whiteWidth)return w20;
                if (x<20*blackWidth+19*whiteWidth&&x>19*blackWidth+19*whiteWidth)return b15;
                if (x<20.5*blackWidth+20*whiteWidth&&x>20*blackWidth+19*whiteWidth)return w21;

            }












        }

    }

    public static class CalibrationResult implements Serializable {


        boolean flag;
        int leftLowX,leftLowY,leftUpX,leftUpY, rightLowX,rightLowY, rightUpX,rightUpY,leftUpRightX,leftUpRightY,rightUpLeftX,rightUpLeftY;



        CalibrationResult(){
            flag=false;
            leftLowX=0;
            leftLowY=0;
            leftUpX=0;
            leftUpY=0;
            rightLowX=0;
            rightLowY=0;
            rightUpX=0;
            rightUpY=0;
            leftUpRightX=0;
            leftUpRightY=0;
            rightUpLeftX=0;
            rightUpLeftY=0;


        }

        public boolean isFlag() {
            return flag;
        }

        public int getLeftLowX() {
            return leftLowX;
        }

        public int getLeftLowY() {
            return leftLowY;
        }

        public int getLeftUpX() {
            return leftUpX;
        }

        public int getLeftUpY() {
            return leftUpY;
        }

        public int getRightLowX() {
            return rightLowX;
        }

        public int getRightLowY() {
            return rightLowY;
        }

        public int getRightUpX() {
            return rightUpX;
        }

        public int getRightUpY() {
            return rightUpY;
        }

        public int getLeftUpRightX() {return leftUpRightX;}

        public int getLeftUpRightY() {return leftUpRightY;}

        public int getRightUpLeftX() {return rightUpLeftX;}

        public int getRightUpLeftY() {return rightUpLeftY;}
    }
    public static class TranformResult implements Serializable {
        Mat m;
        double blackWidth;
        TranformResult(){
            blackWidth=0;

        }
    }

}
