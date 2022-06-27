package main;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import java.awt.Image;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class main extends JFrame {
    static JFileChooser filechoose = new JFileChooser();

        private static Mat imread() {                          //圖片讀取
            File selectedFile = filechoose.getSelectedFile();
            String file = selectedFile.toString();
            Mat scrimg = Imgcodecs.imread(file, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
            return scrimg;
        }

        private static Mat Laplacian(){                    //拉普拉斯平滑(邊緣化)
            Mat dst = new Mat();
            Imgproc.Laplacian(imread(), dst, 0);
            return dst;
        }

        private static Mat threshold(){                    //二值化
            Mat threshold = new Mat();
            Imgproc.threshold(Laplacian(), threshold, 125, 255, Imgproc.THRESH_BINARY);
            return threshold;
        }

        private static Mat erode(){                         //第一次Y方向侵蝕
            Mat kelner = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, 3));
            Mat eroimg = new Mat(); 
            Imgproc.erode(threshold(), eroimg, kelner, new Point());
            return eroimg;
        }

        private static Mat dilate(){                        //第一次X方向膨脹
            Mat kelner = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(35, 1));
            Mat dilimg = new Mat();  
            Imgproc.dilate(erode(), dilimg, kelner, new Point());
            Mat dilimg2 = new Mat();
            Imgproc.dilate(dilimg, dilimg2, kelner, new Point());
            return dilimg2;
        }

        private static Mat erode2(){                         //第二次Y方向侵蝕
            Mat kelner = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, 7));
            Mat eroimg = new Mat(); 
            Imgproc.erode(dilate(), eroimg, kelner, new Point());
            return eroimg;
        }

        private static Mat dilate2(){                        //第二次X方向膨脹
            Mat kelner = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(11, 1));
            Mat dilimg = new Mat();  
            Imgproc.dilate(erode2(), dilimg, kelner, new Point());
            Mat dilimg2 = new Mat();
            Imgproc.dilate(dilimg, dilimg2, kelner, new Point());
            Mat dilimg3 = new Mat();
            Imgproc.dilate(dilimg2, dilimg3, kelner, new Point());
            Mat dilimg4 = new Mat();
            Imgproc.dilate(dilimg3, dilimg4, kelner, new Point());
            return dilimg4;
        }

        private static Mat dilate3(){                        //第一次Y方向膨脹
            Mat kelner = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, 15));
            Mat dilimg = new Mat();  
            Imgproc.dilate(dilate2(), dilimg, kelner, new Point());
            Mat dilimg2 = new Mat();
            Imgproc.dilate(dilimg, dilimg2, kelner, new Point());
            return dilimg2;
        }

        private static Mat findContours(){                                //輪廓檢測 and 擷取車牌
            List<MatOfPoint> contour = new ArrayList<MatOfPoint>();
            Mat hiera = new Mat();
            Mat dst = new Mat();
            Imgcodecs.imwrite("test.jpg", dilate3());
            dst = Imgcodecs.imread("test.jpg", Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);

            Imgproc.findContours(dst, contour, hiera, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE, new Point( 0, 0));
            //System.out.println(contour.size());
            double minX = 960;
            double maxX = 0;
            double minY = 1280;
            double maxY = 0;
            double X = 0;
            double Y = 0;

            for (int i = 0; i < contour.size(); i++){
                MatOfPoint con = contour.get(i);
                for(int j=0; j<con.cols();j++){
                    //System.out.println("輪廓ID" + j + ":");
                    double[] ds = con.get(0, j);
                    for (int k=0; k<ds.length; k++){
                        switch(k){
                            case 0:
                            //System.out.println("第一個座標:" + ds[k]);
                            X = ds[k];
                            break;
                            case 1:
                            //System.out.println("第二個座標:" + ds[k]);
                            Y = ds[k];
                            break;

                            //default:
                            //break;
                        }
                        if(160 < X && X < 680 ){
                            if(320 < Y && Y < 960){
                                if(minX>X){
                                minX = X;
                                }
                                else{
                                    //break;
                                }
                            }
                            else{
                                //break;
                            }
                        }
                        else{
                            //break;
                        }
                        if(160 < X && X < 800 ){
                            if(320 < Y && Y < 960){
                                if(maxX<X){
                                    maxX = X;
                                }
                                else{
                                    //break;
                                }
                            }
                            else{
                                //break;
                            }
                        }
                        else{
                            //break;
                        }
                        if(160 < X && X < 800 ){
                            if(320 < Y && Y < 960){
                                if(minY>Y){
                                    if(((maxX - minX) / (maxY - Y)) <= (2.5)){
                                        minY = Y;
                                    }
                                    else{

                                    }
                                }
                                else{
                                    //break;
                                }
                            }
                            else{
                                //break;
                            }
                        }
                        else{
                            //break;
                        }
                        if(160 < X && X < 800 ){
                            if(320 < Y && Y < 960){
                                if(maxY<Y){
                                    maxY = Y;
                                }
                                else{
                                    //break
                                }
                            }
                            else{
                                //break;
                            }
                        }
                        else{
                            //break;
                        }
                    } 
                }
            }
            Rect rect = new Rect((int)minX, (int)minY, (int)maxX - (int)minX, (int)maxY - (int)minY);
            File selectedFile = filechoose.getSelectedFile();
            String file = selectedFile.toString();
            Mat scrimg = Imgcodecs.imread(file, Imgcodecs.CV_LOAD_IMAGE_COLOR);
            Mat imgg = new Mat(scrimg, rect);
            return imgg;
        }

        public main() {                                             //介面設定
            setTitle("影像處理");
            setSize(780, 640);
            setLocationRelativeTo(null);
            JPanel jPanel = new JPanel();
            add(jPanel);
            place_com(jPanel);
            setVisible(true);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
        

        }

        public static void place_com(JPanel jPanel){
            jPanel.setLayout(new BorderLayout());
            JButton choose = new JButton("圖片選擇");
            jPanel.add(choose, BorderLayout.WEST);
    
            JLabel jLabel_1 = new JLabel("原始圖片");
            jPanel.add(jLabel_1, BorderLayout.CENTER);
            JLabel jLabel_2 = new JLabel("輸出圖片");
            jPanel.add(jLabel_2, BorderLayout.EAST);
    
            choose.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    File selectedFile = fileChooser().getSelectedFile();
                    String file = selectedFile.toString();
                    Mat imgin = Imgcodecs.imread(file, Imgcodecs.CV_LOAD_IMAGE_COLOR);
                    Image img_1 = HighGui.toBufferedImage(imgin);
                    Icon icon = new ImageIcon(img_1.getScaledInstance((imread().cols())/3, ((imread().rows())/3), Image.SCALE_DEFAULT));
                    jLabel_1.setIcon(icon);

                    Image img = HighGui.toBufferedImage(findContours());
                    Icon icon_2 = new ImageIcon(img.getScaledInstance((findContours().cols())/2, ((findContours().rows())/2), Image.SCALE_DEFAULT));
                    jLabel_2.setIcon(icon_2);
                      
                }
    
    
            });
        }

        public static JFileChooser fileChooser(){

            filechoose.setCurrentDirectory(new File(System.getProperty("user.home")));
            int result = filechoose.showOpenDialog(filechoose);
            return filechoose;
        }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.loadLibrary("opencv_java3415");
        new main();
    }
}

