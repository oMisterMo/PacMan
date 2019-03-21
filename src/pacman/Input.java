
package pacman;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * 20-Feb-2018, 22:09:26.
 *
 * @author Mohammed Ibrahim
 */
public class Input implements KeyListener{

    private static boolean[] currentKey = new boolean[256];//needs improving
    private static boolean[] lastKey = new boolean[256];

    /*
        WHEN THE SYSTEM DETECTS KEYBOARD PRESES 
    */
    @Override
    public void keyTyped(KeyEvent e) {
        currentKey[e.getKeyCode()] = true;
    }

    @Override
    public void keyPressed(KeyEvent e) {
//        System.out.println("Pressed: "+e.getKeyCode());
        currentKey[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
//        System.out.println("Released: "+e.getKeyCode());
        currentKey[e.getKeyCode()] = false;
    }

    
    /*
        USED BY A GAME
    */
    public static boolean isKeyPressed(int keyCode) {
        return currentKey[keyCode];
    }
    
    /**
     * Just pressed once
     * 
     * current key and not last key pressed
     * 
     * @param keyCode
     * @return 
     */
    public static boolean isKeyTyped(int keyCode) {
        return currentKey[keyCode] && !lastKey[keyCode];
    }
    
    public static boolean isKeyReleased(int keyCode){
        return !currentKey[keyCode] && lastKey[keyCode];
    }
    
    public static void updateLastKey(){
        lastKey = currentKey.clone();
    }

}
