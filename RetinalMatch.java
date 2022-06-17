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

//Brogan Jowers-Wilding: 1538252
//George Elstob: 1534323

public class RetinalMatch
{
	public static void main(String[] args)
	{      
         
        //checks input is in correct format with right number of entries
        if(args.length != 2) {

            System.err.println("Invalid Input Format- Please use format below");
            System.err.println("java vision <path to image 1>.jpg <path to image 2>.jpg"); 
        }
        try{
            // load the OpenCV native library
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);  
        }
        catch(Exception e){
            System.err.println("error loading OpenCV");
        }
        
        try{
            //Reads in the two images from the paths given
            Mat image1 = Imgcodecs.imread(args[0]);
            Mat image2 = Imgcodecs.imread(args[1]);
    

            //Processes both of the images 
            image1 = processImg(image1);
            image2 = processImg(image2);

            //Imgcodecs.imwrite("final1.jpg", image1);
            //Imgcodecs.imwrite("final2.jpg", image2);

            //Finds out if there is a match between the two images 
            double match = compare(image1, image2);

            
            //checks if match value is high enough to be considered a match
            if(match>0.1){
                System.out.println("1");
            }
            else{
                System.out.println("0");
            }
        }catch(Exception e){
               System.err.println(e); 
        }


	}
    
    //Image processing method to get the veins to pop 
    private static Mat processImg(Mat img){
        //creating matrix
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
        Rect crop = new Rect(190, 0, 1090, 1000);
        img = new Mat(img, crop);
        //Imgcodecs.imwrite("cropped.jpg", img);
        
        //Convert to grayscale 
        Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2GRAY);

        //Chnages the contrast by equalizing histograms opening
        Imgproc.equalizeHist(img, img);
        //Imgcodecs.imwrite("contrast.jpg", img);

        //Adding blur to image 
        Imgproc.GaussianBlur(img, dest1, new Size(15,15), 0);
        //Imgcodass4ecs.imwrite("blur.jpg", dest1);

        //Sharpens the image 
        Imgproc.GaussianBlur(dest1, dest2, new Size(0,0), 10);
        Core.addWeighted(dest1, 1.5, dest2, -0.5, 0, dest2);
        //Imgcodecs.imwrite("sharpen.jpg", dest2);
        
        //use sobal edge detection
        Imgproc.Sobel(dest2, grad_x, CvType.CV_16SC1, 1, 0, 3, 1, 0, Core.BORDER_DEFAULT);
        Imgproc.Sobel(dest2, grad_y, CvType.CV_16SC1, 0, 1, 3, 1, 0, Core.BORDER_DEFAULT);

        Core.convertScaleAbs(grad_x, abs_grad_x);
        Core.convertScaleAbs(grad_y, abs_grad_y);

        Core.addWeighted(abs_grad_x, 0.5, abs_grad_y, 0.5, 0, grad);
        //Imgcodecs.imwrite("edges.jpg", grad);

        //Sharpens the image 
        Imgproc.GaussianBlur(grad, sharpen_2, new Size(0,0), 10);
        Core.addWeighted(grad, 1.5, sharpen_2, -0.5, 0, sharpen_2);
        //Imgcodecs.imwrite("sharpen_2.jpg", sharpen_2);

        //Applies a mask to get rid of the border 
        Point center = new Point((sharpen_2.rows()/2)+30, sharpen_2.cols()/2);
        Point rect1 = new Point(0, 80);
        Point rect2 = new Point(sharpen_2.cols(),sharpen_2.rows()-100);
        Mat mask_circ = new Mat(sharpen_2.rows(), sharpen_2.cols(), CvType.CV_8U, Scalar.all(0));
        Mat mask_rect = new Mat(sharpen_2.rows(), sharpen_2.cols(), CvType.CV_8U, Scalar.all(0));
        
        //Creates a circle for a mask
        Imgproc.circle(mask_circ, center, (sharpen_2.cols()/2)-50, new Scalar(255,255,255), -1, 0, 0 );
        Mat masked = new Mat();
        sharpen_2.copyTo(masked, mask_circ);

        //Creates a rectangle 
        Imgproc.rectangle(mask_rect, rect1, rect2, new Scalar(255,255,255), -1);
        Mat masked_1 = new Mat();
        masked.copyTo(masked_1, mask_rect);

        //Imgcodecs.imwrite("mask.jpg", masked_1);

        //Adaptive thresholding to split the black from white 
        Imgproc.adaptiveThreshold(masked_1,   thresh, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 7, 10);
        //Imgcodecs.imwrite("thresholding.jpg", thresh);

        //Returns the processed image matrix 
        return thresh;

    }
    
    private static double matching(Mat img, Mat templ){
        //creates output matrix
        Mat output=new Mat();
        //declears matching method
        int matchMethod=Imgproc.TM_CCOEFF_NORMED;
        
        Imgproc.matchTemplate(img, templ, output, matchMethod);
        //saves template matching results
        MinMaxLocResult mmr = Core.minMaxLoc(output);
        //extracts max match value
        double matchValue =mmr.maxVal; 
        //returns value
        return matchValue;
    }

    private static double compare(Mat img1, Mat img2){
        //declearing variables
        int buff = 125;
        int x=0;
        int y=0;
        int i=0;
        double match=0;
        double score=0;
        //while loop to run through each template
        while(i < 25){
            //get x and y position multipliers
            x = i%5;
            y = i/5;
            Rect tempRec = new Rect(buff*x, buff*y, 500, 500);
            //create matrix of new rectangle
            Mat temp = new Mat(img2, tempRec);
            //saves temple images to folder
            //Imgcodecs.imwrite("template"+ i +".jpg", temp);
            //calls matching method
            score = matching(img1, temp);

            //System.out.println(score);
            match += score;
            i++;
        }
        //averages the score of all frames
        match = match/25;
        
        //System.out.println("Score: "+ match);
        return match;
    }
}

