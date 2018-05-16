package pacman;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Holds all (currently only spikeBlocks)
 *
 * 1.Store each SpriteSheet once 2.Store each Animation 3.Store each Image
 *
 * 11-Jan-2017, 12:25:37.
 *
 * @author Mo
 */
public class Assets {

    public static BufferedImage world;
    public static BufferedImage walls;

    public static BufferedImage double_tl;
    public static BufferedImage double_tm;
    public static BufferedImage double_tr;
    public static BufferedImage double_ml;
    public static BufferedImage double_mr;
    public static BufferedImage double_bl;
    public static BufferedImage double_bm;
    public static BufferedImage double_br;
    
    public static BufferedImage line_tl;
    public static BufferedImage line_tm;
    public static BufferedImage line_tr;
    public static BufferedImage line_ml;
    public static BufferedImage line_mr;
    public static BufferedImage line_bl;
    public static BufferedImage line_bm;
    public static BufferedImage line_br;
    
    public static BufferedImage hor_tl, hor_tr, hor_bl, hor_br;
    public static BufferedImage ver_tl, ver_tr, ver_bl, ver_br;
    
    public static BufferedImage square_tl, square_tr, square_bl, square_br;
    
    public static BufferedImage home_l, home_r;

//    public static BufferedImage player;
    public Assets() {
        //Loading all tiles (NOT CALLED)
//        System.out.println("Loading tiles...");
//        loadImages();
    }

    public static void loadImages() {
        try {
            world = ImageIO.read(new File("assets\\tiles.png"));
            walls = ImageIO.read(new File("assets\\walls.png"));
            
            double_tl = ImageIO.read(new File("assets\\tiles\\double_tl.png"));
            double_tm = ImageIO.read(new File("assets\\tiles\\double_tm.png"));
            double_tr = ImageIO.read(new File("assets\\tiles\\double_tr.png"));
            double_ml = ImageIO.read(new File("assets\\tiles\\double_ml.png"));
            double_mr = ImageIO.read(new File("assets\\tiles\\double_mr.png"));
            double_bl = ImageIO.read(new File("assets\\tiles\\double_bl.png"));
            double_bm = ImageIO.read(new File("assets\\tiles\\double_bm.png"));
            double_br = ImageIO.read(new File("assets\\tiles\\double_br.png"));
            
            line_tl = ImageIO.read(new File("assets\\tiles\\line_tl.png"));
            line_tm = ImageIO.read(new File("assets\\tiles\\line_tm.png"));
            line_tr = ImageIO.read(new File("assets\\tiles\\line_tr.png"));
            line_ml = ImageIO.read(new File("assets\\tiles\\line_ml.png"));
            line_mr = ImageIO.read(new File("assets\\tiles\\line_mr.png"));
            line_bl = ImageIO.read(new File("assets\\tiles\\line_bl.png"));
            line_bm = ImageIO.read(new File("assets\\tiles\\line_bm.png"));
            line_br = ImageIO.read(new File("assets\\tiles\\line_br.png"));
            
            hor_tl = ImageIO.read(new File("assets\\tiles\\hor_tl.png"));
            hor_tr = ImageIO.read(new File("assets\\tiles\\hor_tr.png"));
            hor_bl = ImageIO.read(new File("assets\\tiles\\hor_bl.png"));
            hor_br = ImageIO.read(new File("assets\\tiles\\hor_br.png"));
            
            ver_tl = ImageIO.read(new File("assets\\tiles\\ver_tl.png"));
            ver_tr = ImageIO.read(new File("assets\\tiles\\ver_tr.png"));
            ver_bl = ImageIO.read(new File("assets\\tiles\\ver_bl.png"));
            ver_br = ImageIO.read(new File("assets\\tiles\\ver_br.png"));
            
            square_tl = ImageIO.read(new File("assets\\tiles\\square_tl.png"));
            square_tr = ImageIO.read(new File("assets\\tiles\\square_tr.png"));
            square_bl = ImageIO.read(new File("assets\\tiles\\square_bl.png"));
            square_br = ImageIO.read(new File("assets\\tiles\\square_br.png"));
            
            home_l = ImageIO.read(new File("assets\\tiles\\home_l.png"));
            home_r = ImageIO.read(new File("assets\\tiles\\home_r.png"));
//            player = ImageIO.read(new File("assets\\player.png"));

        } catch (IOException e) {
            System.out.println("Error loading assets (images)...");
        }
    }
}
