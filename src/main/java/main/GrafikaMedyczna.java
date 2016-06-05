package main;

import org.apache.commons.io.IOUtils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import util.CannyEdgeDetector;
import util.ImageHelper;
import util.OpenCvUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;

public class GrafikaMedyczna extends JFrame {

  static {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
  }

  private File file;
  private JLabel imageLabel;

  public static void main(String[] args) throws Exception {
    new GrafikaMedyczna();
  }

  private void createMenu() {
    JMenuBar menuBar = new JMenuBar();
    setJMenuBar(menuBar);
    //submenu file
    JMenu menu = new JMenu("File");
    menuBar.add(menu);
    //itemy otwierania pliku
    menuItem(menu, "Open regular image", openImageFileListener());
    menuItem(menu, "Open for xray", openXrayFileListener());
    menuItem(menu, "Open for segmentation", openSegmentationFileListener());
    menuItem(menu, "Open for contrast", openContrastFileListener());
    menuItem(menu, "Find edges", openCannyEdgesFileListener());
    menuItem(menu, "Find Hough Lines", openHoughLinesFileListener());
    menuItem(menu, "Skeletonization", openSkeletonizationFileListener());
		menuItem(menu, "Index regions", openRegionIndexingFileListener());
    menu.addSeparator();
    //item wyjscia z aplikacji
    menuItem(menu, "Exit", ae -> System.exit(0));
  }

	private ActionListener openRegionIndexingFileListener() {
		return ae -> {
			try {
				JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					file = fc.getSelectedFile();
					Mat imread = Highgui.imread(file.getAbsolutePath());
					String s = (String) JOptionPane.showInputDialog(this, "Give threshold", "",
							JOptionPane.PLAIN_MESSAGE, null, null, "");
					Imgproc.cvtColor(imread, imread, Imgproc.COLOR_RGB2GRAY);
					Imgproc.threshold(imread, imread, Double.valueOf(s), 255, Imgproc.THRESH_BINARY);
					BufferedImage bufferedImage = OpenCvUtil.indexRegions(imread);
					imageLabel.setIcon(new ImageIcon(bufferedImage));
				}
				repaint();
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	}

  private ActionListener openSkeletonizationFileListener() {
    return ae -> {
      try {
        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          file = fc.getSelectedFile();
          BufferedImage read = ImageIO.read(file);
          BufferedImage colorImage1 = new BufferedImage(read.getWidth(), read.getHeight(), BufferedImage.TYPE_INT_RGB);
          colorImage1.getGraphics().drawImage(read, 0, 0, null);
          int[] bitMap = ImageHelper.getBitMap(colorImage1);
          int[] ints = OpenCvUtil.doHilditchsThinning(bitMap, colorImage1.getWidth(), colorImage1.getHeight());
          ImageHelper.rewrite(colorImage1, ints);
          imageLabel.setIcon(new ImageIcon(colorImage1));
        }
        repaint();
      } catch (Exception e) {
        e.printStackTrace();
      }
    };
  }

  private ActionListener openHoughLinesFileListener() {
    return ae -> {
      try {
        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          file = fc.getSelectedFile();
          String sMaks = (String) JOptionPane.showInputDialog(this, "Choose mask size", "",
              JOptionPane.PLAIN_MESSAGE, null, null, "");
          String sLow = (String) JOptionPane.showInputDialog(this, "Choose low treshold", "",
              JOptionPane.PLAIN_MESSAGE, null, null, "");
          String sHigh = (String) JOptionPane.showInputDialog(this, "Choose high threshold", "",
              JOptionPane.PLAIN_MESSAGE, null, null, "");
          BufferedImage bufferedImage = OpenCvUtil.performHoughLinesDetection(file, Double.valueOf(sLow),
              Double.valueOf(sHigh), Double.valueOf(sMaks));
          imageLabel.setIcon(new ImageIcon(bufferedImage));
        }
        repaint();
      } catch (Exception e) {
        e.printStackTrace();
      }
    };
  }


  private ActionListener openCannyEdgesFileListener() {
    return ae -> {
      try {
        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          file = fc.getSelectedFile();
          String s = (String) JOptionPane.showInputDialog(this, "Choose sigma", "",
              JOptionPane.PLAIN_MESSAGE, null, null, "");
          BufferedImage read = ImageIO.read(file);
          Mat imread = Highgui.imread(file.getAbsolutePath());
//          Mat original = imread.clone();
          CannyEdgeDetector detector = new CannyEdgeDetector();
          detector.setLowThreshold(0.5f);
          detector.setHighThreshold(1f);
          detector.setSourceImage(read);
          detector.process();
          Mat original = OpenCvUtil.bufferedImageToMat(read);
          BufferedImage edges = detector.getEdgesImage();
          Mat edg = OpenCvUtil.bufferedImageToMatInt(edges);
//          Imgproc.Canny(imread, imread, 100, 200);
//          Imgproc.GaussianBlur(imread, imread, new Size(), Double.valueOf(s));
//          Imgproc.Laplacian(imread, imread, -1, 3, 1, 0);
//          Imgproc.Sobel(imread, imread, -1, 0, 1, 3, 1, 0);
//          Imgproc.threshold(imread, imread, 1 << 4, 1 << 8, THRESH_BINARY);
//          Core.add(original, imread, original);
          Core.add(original, edg, original);
          BufferedImage bufferedImage = OpenCvUtil.byteMat2RgbBufferedImage(edg);
          imageLabel.setIcon(new ImageIcon(bufferedImage));
        }
        repaint();
      } catch (Exception e) {
        e.printStackTrace();
      }
    };
  }

  private ActionListener openXrayFileListener() {
    return ae -> {
      try {
        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          file = fc.getSelectedFile();
          FileInputStream fileInputStream = new FileInputStream(file);
          byte[] bytes = IOUtils.toByteArray(fileInputStream);
          Mat imread = OpenCvUtil.byteArrayToMat8U1(bytes);
          BufferedImage bufferedImage = OpenCvUtil.byteMat2BufferedImage(imread);
          imageLabel.setIcon(new ImageIcon(bufferedImage));
        }
        repaint();
      } catch (Exception e) {
        e.printStackTrace();
      }
    };
  }

  private ActionListener openSegmentationFileListener() {
    return ae -> {
      try {
        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          file = fc.getSelectedFile();
          String s = (String) JOptionPane.showInputDialog(this, "Choose number of classes", "",
              JOptionPane.PLAIN_MESSAGE, null, null, "");
          if (s != null) {
            Integer numberOfClasses = Integer.valueOf(s);
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] bytes = IOUtils.toByteArray(fileInputStream);
            Mat imread = OpenCvUtil.byteArrayToMat8U1(bytes);
            Mat mat = OpenCvUtil.calculateThreshold(imread, numberOfClasses, 1 << 8);
            BufferedImage bufferedImage = OpenCvUtil.byteMat2RgbBufferedImage(mat);
            imageLabel.setIcon(new ImageIcon(bufferedImage));
            imageLabel.setIcon(new ImageIcon(bufferedImage));
          }
        }
        repaint();
      } catch (Exception e) {
        e.printStackTrace();
      }
    };
  }

  private ActionListener openImageFileListener() {
    return ae -> {
      try {
        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(null);
        BufferedImage image1 = null;
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          File file1 = fc.getSelectedFile();
          image1 = ImageIO.read(file1);
          BufferedImage colorImage1 = new BufferedImage(image1.getWidth(), image1.getHeight(), BufferedImage.TYPE_INT_RGB);
          colorImage1.getGraphics().drawImage(image1, 0, 0, null);
          imageLabel.setIcon(new ImageIcon(colorImage1));
        }
        repaint();
      } catch (Exception e) {
        e.printStackTrace();
      }
    };
  }

  private ActionListener openContrastFileListener() {
    return ae -> {
      try {
        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(null);
        BufferedImage image1 = null, image2 = null;
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          File file1 = fc.getSelectedFile();
          image1 = ImageIO.read(file1);
          returnVal = fc.showOpenDialog(null);
          if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file2 = fc.getSelectedFile();
            image2 = ImageIO.read(file2);
            BufferedImage result = OpenCvUtil.incContrastBySubstaction(image1, image2);
            imageLabel.setIcon(new ImageIcon(result));
          }
        }
        repaint();
      } catch (Exception e) {
        e.printStackTrace();
      }
    };
  }

  private void menuItem(JMenu menu, String name, ActionListener actionListener) {
    JMenuItem mitem = new JMenuItem(name);
    mitem.addActionListener(actionListener);
    menu.add(mitem);
  }

  private GrafikaMedyczna() throws Exception {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setTitle("Grafika Medyczna - Krzysztof Osiecki");
    createMenu();
    createView();
    setVisible(true);
    setSize(1400, 800);
  }

  private void createView() {
    imageLabel = new JLabel();
    add(imageLabel);
  }

}


