package main;

import data.ThresholdType;
import net.miginfocom.swing.MigLayout;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import util.CannyEdgeDetector;
import util.HilditchsThinner;
import util.ImageHelper;
import util.LinesDetector;
import util.OpenCvUtil;
import util.RegionIndexer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

public class GrafikaMedyczna extends JFrame {

  static {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
  }

  private File file;
  private JLabel imageLabel;

  public static void main(String[] args) throws Exception {
    new GrafikaMedyczna();
  }

  private ActionListener openCannyEdgesFileListener() {
    return ae -> {
      try {
        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          file = fc.getSelectedFile();
          String sigma = (String) JOptionPane.showInputDialog(this, "Choose sigma", "",
              JOptionPane.PLAIN_MESSAGE, null, null, "");
          String low = (String) JOptionPane.showInputDialog(this, "Choose low hysteresis level", "",
              JOptionPane.PLAIN_MESSAGE, null, null, "");
          String high = (String) JOptionPane.showInputDialog(this, "Choose high hysteresis level", "",
              JOptionPane.PLAIN_MESSAGE, null, null, "");
          BufferedImage read = ImageIO.read(file);
          BufferedImage bufferedImage =
              CannyEdgeDetector.performCannyDetection(read, Double.valueOf(sigma), Integer.valueOf(low),
                  Integer.valueOf(high));
          imageLabel.setIcon(new ImageIcon(bufferedImage));
        }
        repaint();
      } catch (Exception e) {
        e.printStackTrace();
      }
    };
  }

  private ActionListener openHoughLinesFileListener() {
    return ae -> {
      JFileChooser fc = new JFileChooser();
      int returnVal = fc.showOpenDialog(null);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        file = fc.getSelectedFile();
        try {
          String sMaks = (String) JOptionPane.showInputDialog(this, "Choose mask size", "",
              JOptionPane.PLAIN_MESSAGE, null, null, "");
          String sLow = (String) JOptionPane.showInputDialog(this, "Choose low treshold", "",
              JOptionPane.PLAIN_MESSAGE, null, null, "");
          String sHigh = (String) JOptionPane.showInputDialog(this, "Choose high threshold", "",
              JOptionPane.PLAIN_MESSAGE, null, null, "");
          String sThresh = (String) JOptionPane.showInputDialog(this, "Choose line threshold", "",
              JOptionPane.PLAIN_MESSAGE, null, null, "");
          String sNeibourgh = (String) JOptionPane.showInputDialog(this, "Choose neibourghood", "",
              JOptionPane.PLAIN_MESSAGE, null, null, "");

          BufferedImage image = ImageIO.read(file);
          BufferedImage bufferedImage =
              LinesDetector
                  .performLineDetection(image, Double.valueOf(sMaks), Integer.valueOf(sLow), Integer.valueOf(sHigh),
                      Integer.valueOf(sThresh), Integer.valueOf(sNeibourgh));

          imageLabel.setIcon(new ImageIcon(bufferedImage));
        } catch (Exception e) {
          System.out.println(e);
        }
      }
    };
  }

  private ActionListener open2dOtsuFileListener() {
    return ae -> {
      try {
        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          file = fc.getSelectedFile();
          Mat imread = Highgui.imread(file.getAbsolutePath());
          Imgproc.cvtColor(imread, imread, Imgproc.COLOR_RGB2GRAY);
          Mat mat = OpenCvUtil.threshold(imread, -1, (1 << 8) - 1, ThresholdType.OTSU_2D, ThresholdType.BINARY);
          BufferedImage bufferedImage = OpenCvUtil.byteMat2BufferedImage(mat);
          imageLabel.setIcon(new ImageIcon(bufferedImage));
        }
        repaint();
      } catch (Exception e) {
        e.printStackTrace();
      }
    };
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
          BufferedImage bufferedImage = RegionIndexer.indexRegions(imread);
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
          int[] ints =
              HilditchsThinner.performHilditchsThinning(bitMap, colorImage1.getWidth(), colorImage1.getHeight());
          ImageHelper.rewrite(colorImage1, ints);
          imageLabel.setIcon(new ImageIcon(colorImage1));
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
          BufferedImage colorImage1 =
              new BufferedImage(image1.getWidth(), image1.getHeight(), BufferedImage.TYPE_INT_RGB);
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

  private GrafikaMedyczna() throws Exception {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setTitle("Grafika Medyczna - Krzysztof Osiecki");
    createMenu();
    createView();
    setVisible(true);
    setSize(1620, 1000);
  }

  private void menuItem(JMenu menu, String name, ActionListener actionListener) {
    JMenuItem mitem = new JMenuItem(name);
    mitem.addActionListener(actionListener);
    menu.add(mitem);
  }

  private void createView() {
    this.setLayout(new MigLayout());
    imageLabel = new JLabel();
    imageLabel.setPreferredSize(new Dimension(2000, 2000));
    add(imageLabel);
  }

  private void createMenu() {
    JMenuBar menuBar = new JMenuBar();
    setJMenuBar(menuBar);
    //submenu file
    JMenu menu = new JMenu("File");
    menuBar.add(menu);
    //itemy otwierania pliku
    menuItem(menu, "Open regular image", openImageFileListener());
    menuItem(menu, "Open for contrast", openContrastFileListener());
    menuItem(menu, "Open for 2d Otsu", open2dOtsuFileListener());
    menuItem(menu, "Skeletonization", openSkeletonizationFileListener());
    menuItem(menu, "Index regions", openRegionIndexingFileListener());
    menuItem(menu, "Find edges", openCannyEdgesFileListener());
    menuItem(menu, "Find Hough Lines", openHoughLinesFileListener());
    menu.addSeparator();
    //item wyjscia z aplikacji
    menuItem(menu, "Exit", ae -> System.exit(0));
  }

}



