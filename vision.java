import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
        
import org.opencv.core.Rect;
import org.opencv.core.Size;

/**
 * A simple class that demonstrates/tests the usage of the OpenCV library in
 * Java. It prints a 3x3 identity matrix and then converts a given image in gray
 * scale.
 * 
 * @author <a href="mailto:luigi.derussis@polito.it">Luigi De Russis</a>
 * @since 2013-10-20
 * 
 */
public class vision
{
	public static void main(String[] args)
	{      
         
        //checks input is in correct format with right number of entries
        if(args.length != 2) {

            System.err.println("Invalid Input- Please use format below");
            System.err.println("java vision <path to image 1>.jpg <path to image 2>.jpg"); 
        }
        
        // load the OpenCV native library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);  

        Mat image1 = Imgcodecs.imread(args[0]);
        Mat image2 = Imgcodecs.imread(args[1]);
    
		image1 = processImg(image1);

		double match = matching(image1, image1);

		System.out.println(match);


	}
    
    private static Mat processImg(Mat img){

        Mat grad = new Mat();
        Mat grad_x = new Mat();
        Mat grad_y = new Mat();
        Mat abs_grad_x = new Mat();
        Mat abs_grad_y = new Mat();
        Mat dest1 = new Mat();
        Mat dest2 = new Mat();
        Mat thresh = new Mat();
        Mat sharpen_2 = new Mat();

        // crop to roughly the eyeclass
        Rect crop = new Rect(200, 0, 1000, 1000);
        img = new Mat(img, crop);
        
        //Convert to grayscale 
        Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2GRAY);

        //Chnages the contrast by equalizing histograms 
        Imgproc.equalizeHist(img, img);
        Imgcodecs.imwrite("contrast.jpg", img);

        //Adding blur to image 
        Imgproc.GaussianBlur(img, dest1, new Size(15,15), 0);
        Imgcodecs.imwrite("blur.jpg", dest1);

        //Sharpens the image 
        Imgproc.GaussianBlur(dest1, dest2, new Size(0,0), 10);
        Core.addWeighted(dest1, 1.5, dest2, -0.5, 0, dest2);
        Imgcodecs.imwrite("sharpen.jpg", dest2);
        
        //use sobal edge detection
        Imgproc.Sobel(dest2, grad_x, CvType.CV_16SC1, 1, 0, 3, 1, 0, Core.BORDER_DEFAULT);
        Imgproc.Sobel(dest2, grad_y, CvType.CV_16SC1, 0, 1, 3, 1, 0, Core.BORDER_DEFAULT);

        Core.convertScaleAbs(grad_x, abs_grad_x);
        Core.convertScaleAbs(grad_y, abs_grad_y);

        Core.addWeighted(abs_grad_x, 0.5, abs_grad_y, 0.5, 0, grad);
        Imgcodecs.imwrite("edges.jpg", grad);

        //Sharpens the image 
        Imgproc.GaussianBlur(grad, sharpen_2, new Size(0,0), 10);
        Core.addWeighted(grad, 1.5, sharpen_2, -0.5, 0, sharpen_2);
        Imgcodecs.imwrite("sharpen_2.jpg", sharpen_2);

        //Adaptive thresholding to split the black from white 
        Imgproc.adaptiveThreshold(sharpen_2,   thresh, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 7, 10);
        Imgcodecs.imwrite("thresholding.jpg", thresh);

        //Applies a mask to get rid of the border 
        Point center = new Point(thresh.rows()/2, thresh.cols()/2);
        Point rect1 = new Point(0, 90);
        Point rect2 = new Point(thresh.cols(),0);
        Mat mask = new Mat(thresh.rows(), thresh.cols(), CvType.CV_8U, Scalar.all(0));
        Imgproc.rectangle(mask, rect1, rect2, new Scalar(255,255,255), -1, 8, 0 );
        Imgproc.circle(mask, center, thresh.cols()/2, new Scalar(255,255,255), -1, 8, 0 );
        Mat masked = new Mat();
        thresh.copyTo(masked, mask);
        Imgcodecs.imwrite("mask.jpg", masked);

        return img;

    }
    
    private static double matching(Mat img, Mat templ){
        Mat output=new Mat();
        int matchMethod=Imgproc.TM_CCOEFF_NORMED;   
        
        Imgproc.matchTemplate(img, templ, output, matchMethod);

        MinMaxLocResult mmr = Core.minMaxLoc(output);
        double matchValue =mmr.maxVal;  

        return matchValue;
    }
    

}
