package pacman;

import common.Animation;
import common.AnimationA;
import common.SpriteSheet;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Holds all assets used in CloneWars.
 *
 * @version 0.1.0
 * @author Mohammed Ibrahim
 */
public final class Assets {

    public static BufferedImage world;
    public static BufferedImage walls;
    public static BufferedImage intersections;
    public static Animation pacman;
    public static AnimationA pacmanDeath;
    public static Animation blinky;
    public static Animation pinky;
    public static Animation inky;
    public static Animation clyde;

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

    public Assets() {
        System.out.println("Loading assets...");
        loadImages();
    }

    /*
        -Replaced \\ with //
        -Replaced // with /
        -Prefixed path with /
     */
    private void loadImages() {
        try {
//             world = ImageIO.read(getClass().getResource("/assets/tiles.png"));
             world = ImageIO.read(getClass().getResource("/assets/tiles.png"));
             walls = ImageIO.read(getClass().getResource("/assets/walls.png"));
             intersections = ImageIO.read(getClass().getResource("/assets/intersections.png"));

             pacman = new Animation();
             BufferedImage pacm = ImageIO.read(getClass().getResource("/assets/pacman.png"));
             BufferedImage[] temp = new BufferedImage[3];
             temp[0] = SpriteSheet.getPosition(pacm, 0, 0, 32, 32);
             temp[1] = SpriteSheet.getPosition(pacm, 32, 0, 32, 32);
             temp[2] = SpriteSheet.getPosition(pacm, 64, 0, 32, 32);
             pacman.setFrames(temp);
             pacman.setDelay(70);
             BufferedImage pmd = ImageIO.read(getClass().getResource("/assets/pacmanDeath.png"));
             pacmanDeath = new AnimationA(0.15f,
             SpriteSheet.getPosition(pmd, 32 * 0, 0, 32, 32),
             SpriteSheet.getPosition(pmd, 32 * 1, 0, 32, 32),
             SpriteSheet.getPosition(pmd, 32 * 2, 0, 32, 32),
             SpriteSheet.getPosition(pmd, 32 * 3, 0, 32, 32),
             SpriteSheet.getPosition(pmd, 32 * 4, 0, 32, 32),
             SpriteSheet.getPosition(pmd, 32 * 5, 0, 32, 32),
             SpriteSheet.getPosition(pmd, 32 * 6, 0, 32, 32),
             SpriteSheet.getPosition(pmd, 32 * 7, 0, 32, 32),
             SpriteSheet.getPosition(pmd, 32 * 8, 0, 32, 32),
             SpriteSheet.getPosition(pmd, 32 * 9, 0, 32, 32),
             SpriteSheet.getPosition(pmd, 32 * 10, 0, 32, 32)
             );

             blinky = new Animation();
             BufferedImage blink = ImageIO.read(getClass().getResource("/assets/blinky.png"));
             temp = new BufferedImage[2];
             temp[0] = SpriteSheet.getPosition(blink, 0, 0, 32, 32);
             temp[1] = SpriteSheet.getPosition(blink, 32, 0, 32, 32);
             blinky.setFrames(temp);
             blinky.setDelay(70);
             pinky = new Animation();
             BufferedImage pink = ImageIO.read(getClass().getResource("/assets/pinky.png"));
             temp = new BufferedImage[2];
             temp[0] = SpriteSheet.getPosition(pink, 0, 0, 32, 32);
             temp[1] = SpriteSheet.getPosition(pink, 32, 0, 32, 32);
             pinky.setFrames(temp);
             pinky.setDelay(70);
             inky = new Animation();
             BufferedImage ink = ImageIO.read(getClass().getResource("/assets/inky.png"));
             temp = new BufferedImage[2];
             temp[0] = SpriteSheet.getPosition(ink, 0, 0, 32, 32);
             temp[1] = SpriteSheet.getPosition(ink, 32, 0, 32, 32);
             inky.setFrames(temp);
             inky.setDelay(70);
             clyde = new Animation();
             BufferedImage clyd = ImageIO.read(getClass().getResource("/assets/clyde.png"));
             temp = new BufferedImage[2];
             temp[0] = SpriteSheet.getPosition(clyd, 0, 0, 32, 32);
             temp[1] = SpriteSheet.getPosition(clyd, 32, 0, 32, 32);
             clyde.setFrames(temp);
             clyde.setDelay(70);

             double_tl = ImageIO.read(getClass().getResource("/assets/tiles/double_tl.png"));
             double_tm = ImageIO.read(getClass().getResource("/assets/tiles/double_tm.png"));
             double_tr = ImageIO.read(getClass().getResource("/assets/tiles/double_tr.png"));
             double_ml = ImageIO.read(getClass().getResource("/assets/tiles/double_ml.png"));
             double_mr = ImageIO.read(getClass().getResource("/assets/tiles/double_mr.png"));
             double_bl = ImageIO.read(getClass().getResource("/assets/tiles/double_bl.png"));
             double_bm = ImageIO.read(getClass().getResource("/assets/tiles/double_bm.png"));
             double_br = ImageIO.read(getClass().getResource("/assets/tiles/double_br.png"));

             line_tl = ImageIO.read(getClass().getResource("/assets/tiles/line_tl.png"));
             line_tm = ImageIO.read(getClass().getResource("/assets/tiles/line_tm.png"));
             line_tr = ImageIO.read(getClass().getResource("/assets/tiles/line_tr.png"));
             line_ml = ImageIO.read(getClass().getResource("/assets/tiles/line_ml.png"));
             line_mr = ImageIO.read(getClass().getResource("/assets/tiles/line_mr.png"));
             line_bl = ImageIO.read(getClass().getResource("/assets/tiles/line_bl.png"));
             line_bm = ImageIO.read(getClass().getResource("/assets/tiles/line_bm.png"));
             line_br = ImageIO.read(getClass().getResource("/assets/tiles/line_br.png"));

             hor_tl = ImageIO.read(getClass().getResource("/assets/tiles/hor_tl.png"));
             hor_tr = ImageIO.read(getClass().getResource("/assets/tiles/hor_tr.png"));
             hor_bl = ImageIO.read(getClass().getResource("/assets/tiles/hor_bl.png"));
             hor_br = ImageIO.read(getClass().getResource("/assets/tiles/hor_br.png"));

             ver_tl = ImageIO.read(getClass().getResource("/assets/tiles/ver_tl.png"));
             ver_tr = ImageIO.read(getClass().getResource("/assets/tiles/ver_tr.png"));
             ver_bl = ImageIO.read(getClass().getResource("/assets/tiles/ver_bl.png"));
             ver_br = ImageIO.read(getClass().getResource("/assets/tiles/ver_br.png"));

             square_tl = ImageIO.read(getClass().getResource("/assets/tiles/square_tl.png"));
             square_tr = ImageIO.read(getClass().getResource("/assets/tiles/square_tr.png"));
             square_bl = ImageIO.read(getClass().getResource("/assets/tiles/square_bl.png"));
             square_br = ImageIO.read(getClass().getResource("/assets/tiles/square_br.png"));

             home_l = ImageIO.read(getClass().getResource("/assets/tiles/home_l.png"));
             home_r = ImageIO.read(getClass().getResource("/assets/tiles/home_r.png"));
             //            player = ImageIO.read(getClass().getResource("/assets/player.png"));
             
        } catch (IOException e) {
            System.out.println("Error loading assets...");
        }
    }
}
