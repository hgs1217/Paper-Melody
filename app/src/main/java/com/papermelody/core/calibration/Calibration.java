package com.papermelody.core.calibration;

/**
 * Created by tangtonghui on 17/5/9.
 */

import org.opencv.core.Core;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.ArrayList;
import java.util.List;


public class Calibration {
    public static int[]  main(Mat srcImage){

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);





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
        if (nnn==0){int o[]=new int[]{0,0,0,0,0,0,0,0};return o;};

        int temp_order=0;
        int temp=0;

        for (int i=0;i<count;i++){
            if (a3[i]>temp){
                temp=a3[i];
                temp_order=i;

            }
        }

        temp=a2[temp_order];
        int leftlow_x=dstImage.width(),leftlow_y=0,leftup_x=0,leftup_y=0,rightlow_x=0,rightlow_y=0,rightup_x=0,rightup_y=0;
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

        Mat src = new Mat(4,1, CvType.CV_32FC2);
        src.put(leftlow_y,leftlow_x,leftup_y,leftup_x, rightlow_y,rightlow_x, rightup_y,rightup_x);
        int output[ ] =new int []{leftlow_y,leftlow_x,leftup_y,leftup_x, rightlow_y,rightlow_x, rightup_y,rightup_x} ;

        Mat dst = new Mat(4,1,CvType.CV_32FC2);
        dst.put(-250,0, -250,100,250,0,250,100);
        Mat perspectiveTransform = Imgproc.getPerspectiveTransform(src, dst);


        Mat Src=new Mat(1,1,CvType.CV_32FC2);
        Src.put(660,728);
        Mat Dst=new Mat(1,1,CvType.CV_32FC2);
        Core.perspectiveTransform(Src,Dst,perspectiveTransform);
        return output;




    }

}
