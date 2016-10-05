import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

/**
 * Classe para criar um campo de futebol americano
 * 
 * @author RA21497720
 */
public class CG_Campo implements GLEventListener, KeyListener, MouseListener {
    
    public static void main(String[] args) {
        //<editor-fold defaultstate="collapsed">
        /**
         * Inicialmente carrega um conjunto predefinido de configurações do
         * OpenGL a ser utilizado durante a execução da aplicação e cria uma
         * nova tela
         *///</editor-fold>
        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);
        GLCanvas canvas = new GLCanvas(caps);
        
        //Cria o frame correspondente à tela da aplicação
        Frame frame = new Frame("Campo");
        frame.setSize(1900,1000);
        frame.add(canvas);
        frame.setVisible(true);
        
        //Adiciona um listener para o fechamento da janela
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        
        CG_Campo app = new CG_Campo();
        //Adiciona um listener de eventos OpenGL à aplicação criada
        canvas.addGLEventListener(app);
        canvas.addKeyListener(app);
        canvas.addMouseListener(app);
        canvas.setVisible(true);
        
        FPSAnimator animator = new FPSAnimator(canvas, 60);
        animator.start();
    }

    /**
     * Variáveis da classe
     */
    private GLU glu;// objeto da classe GLU
    private GLUT glut; // objeto da classe GLUT
    private int texture;
    
    
    /**
     * Métodos abstratos de GLEventListener
     */
    
    /**
     * Chamado imediatamente após o contexto OpenGL ter sido inicializado. Pode
     * ser utilizado para a inicialização de configurações.
     * 
     * @param drawable 
     */
    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        glu = new GLU();
        glut = new GLUT();
        gl.glEnable(GL2.GL_TEXTURE_2D);
        try {
            File im = new File("campo.png");
            Texture t = TextureIO.newTexture(im, true);
            texture = t.getTextureObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Cor do fundo da tela
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    /**
     * Chamado para iniciar a renderização OpenGL pelo cliente. É o método que
     * realmente desenha na tela.
     *
     * @param drawable
     */
    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        
        // Limpa a janela de visualização com a cor de fundo especificada
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        
        gl.glColor3f(0.0f, 0.0f, 0.0f);
        
        gl.glPushMatrix();
            gl.glColor3f(1f, 0f, 0f);
            gl.glBindTexture(GL2.GL_TEXTURE_2D, texture);
            gl.glScalef(1.0f, 0.5f, 0.0f);
            glut.glutSolidCube(2.0f);
        
        gl.glPopMatrix();
        
        // Executa os comandos OpenGL
        gl.glFlush();
    }
    
    /**
     * Chamado após o redimensionamento do componente ou da janela de
     * visualização
     * 
     * @param drawable
     * @param x  
     * @param y  
     * @param w  
     * @param h 
     */
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
     
        gl.glMatrixMode(GL2.GL_PROJECTION);
        glu.gluPerspective(65.0, (float) w / (float) h, 1.0, 20.0);
        gl.glTranslatef(0.0f, 0.0f, -10.0f);
    }

    /**
     * Métodos abstratos de KeyListener
     */
    
    //<editor-fold defaultstate="collapsed" desc="keyTyped">
    @Override
    public void keyTyped(KeyEvent ke) {
    }
    //</editor-fold>

    @Override
    public void keyPressed(KeyEvent ke) {
        switch (ke.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;
            case KeyEvent.VK_0:
                System.out.println("0 foi apertado.");
                break;
            default:
                break;
        }
    }

    //<editor-fold defaultstate="collapsed" desc="keyReleased">
    @Override
    public void keyReleased(KeyEvent ke) {
    }
    //</editor-fold>

    /**
     * Métodos abstratos de MouseListener 
     */
    //<editor-fold defaultstate="colapsed" desc="MouseListener">
    @Override
    public void mouseClicked(MouseEvent me) {
    }

    @Override
    public void mousePressed(MouseEvent me) {
    }

    @Override
    public void mouseReleased(MouseEvent me) {
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }
    //</editor-fold>
}
