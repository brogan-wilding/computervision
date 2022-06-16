import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Core;
import org.opencv.core.Rect;

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
		
		System.out.println("Done!");
	}
    private static Mat processImg(Mat img){
        // crop to roughly the eyeclass
        Rect crop = new Rect(200, 0, 1000, 1000);

        img = new Mat(img, crop);
        

        Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2GRAY);

        Imgcodecs.imwrite("cropped1.jpg", img);

        //Adding smoothing
        Imgproc.medianBlur(img, img, 9);
        //Imgproc.GaussianBlur(img, img, new Size(11, 11), 0);

        Imgcodecs.imwrite("processed.jpg", img);

        //Sobel(src_gray, grad_x, ddepth, 1, 0, ksize, scale, delta, BORDER_DEFAULT);
        //Imgproc.Canny(edges, edges, 4,12);
        //Imgproc.Laplacian(edges, dst, CvType.CV_16S, 5, 0.6, 9, Core.BORDER_DEFAULT);

        
        //Imgcodecs.imwrite("processed.jpg", image2);
        return img;

    }

}
