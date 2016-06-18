package util;

/**
 * Created by okChris on 2016-06-11.
 */
public class Sift {
	public void ReadObj(String src) throws Exception{
		this.src=src;
		String destinazione=”LastObject”;
		I2= new Analysis (src,destinazione);
		O= (float)Math.floor(Math.log(Math.min(I2.width,
				I2.height))/Math.log(2))-omin-3;
		gaussianIm2=new Gaussianss (I2,sigman,O,S,omin,-1,S+1,sigman0);
		dssIm2=new DoG(gaussianIm2);
		ReObjectBo=true;
		KP2=new SiftLocalMax(dssIm2,gaussianIm2,thresh);
		KD2=new DescriptorB(KP2.KpV,gaussianIm2);
		System.out.println(”ReadObj over”);
	}

	public void ReadImage(String src) throws Exception{
		this.src2=src;
		String destination=”LastImage”;
		I= new Analysis (src2,destination);
		O= (float)Math.floor(Math.log(Math.min(I.width,
				I.height))/Math.log(2))-omin-3;
		gaussianIm=new Gaussianss (I,sigman,O,S,omin,-1,S+1,sigman0);
		dssIm=new DoG(gaussianIm);
		ReImgBo=true;
		System.out.println(”ReadImafe over”);
	}

}
