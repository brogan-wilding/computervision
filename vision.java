import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Core;

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

		
		System.out.println("Done!");
	}
}
