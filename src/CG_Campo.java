import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

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
    private GL2 gl;
    private GLU glu;// objeto da classe GLU
    private GLUT glut; // objeto da classe GLUT
    private int texture;
    private int idTextura[];
    private int largura, altura;
    private ByteBuffer buffer;
    private BufferedImage imagem;
    private TextureData texData;
    private Texture texturaCampo;

    
    
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
        //abilita o uso da textura
        gl.glEnable(GL2.GL_TEXTURE_2D);
        
        // Comandos de inicialização para textura
        loadImage("campo.jpg");
        
        // Gera identificador de textura
        idTextura = new int[10];
        gl.glGenTextures(1, idTextura, 1);
        
        // Especifica qual � a textura corrente pelo identificador 
        gl.glBindTexture(GL.GL_TEXTURE_2D, idTextura[0]);
        
        // Envio da textura para OpenGL
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, 3, largura, 
                        altura, 0, GL.GL_BGR, GL.GL_UNSIGNED_BYTE, buffer);
        
        // Define os filtros de magnificação e minificação 
        gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_MIN_FILTER,GL.GL_LINEAR);	
        gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_MAG_FILTER,GL.GL_LINEAR);
        
        // Cor do fundo da tela
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void loadImage(String nomeArq)
	{
		// Tenta carregar o arquivo		
		imagem = null;
		try {
                    imagem = ImageIO.read(new File("src\\texture\\" + nomeArq));
                    // Obtém largura e altura
                    largura  = imagem.getWidth();
                    altura = imagem.getHeight();
		}
		catch (IOException e) {
                    JOptionPane.showMessageDialog(null,"Erro na leitura do arquivo "+nomeArq);
                    System.out.println(e.getStackTrace());
		}

		//Carrega a textura		
		try {
                    InputStream stream = getClass().getResourceAsStream(nomeArq);
                    texData = TextureIO.newTextureData(GLProfile.getDefault(), stream, false, "jpg");
		}
		catch (IOException exc) {
                    exc.printStackTrace();
                    System.exit(1);
		}
		// ...e obtém um ByteBuffer a partir dela
		buffer = (ByteBuffer) texData.getBuffer();
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
        
        // Limpa a janela de visualização com a cor de fundo especificada
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );
        gl.glLoadIdentity();
        
        gl.glColor3f(0.0f, 0.0f, 0.0f);
        
        renderizaCampo();
    }
    
    private void renderizaCampo (){
        try {
            InputStream stream = getClass().getResourceAsStream("campo.jpg");
            TextureData data = TextureIO.newTextureData(GLProfile.getDefault(), stream, false, "jpg");
            texturaCampo = TextureIO.newTexture(data);
        }
        catch (IOException exc) {
            exc.printStackTrace();
            System.exit(1);
        }

        //Habilita a textura do campo
        texturaCampo.enable(gl);
        texturaCampo.bind(gl);

        gl.glPushMatrix();
            //Desloca o objeto
            gl.glTranslatef(0f, 40f, 30f);
            //Desenha o "retangulo" do campo      
            GLUquadric campo = glu.gluNewQuadric();
            glu.gluQuadricTexture(campo, true);
            glu.gluQuadricDrawStyle(campo, GLU.GLU_FILL);
            glu.gluQuadricNormals(campo, GLU.GLU_FLAT);
            glu.gluQuadricOrientation(campo, GLU.GLU_OUTSIDE);
            gl.glScalef(1.0f, 0.5f, 0.0f);
            glut.glutSolidCube(2.0f);
            glu.gluDeleteQuadric(campo);
        gl.glPopMatrix();
        texturaCampo.disable(gl);
        texturaCampo.destroy(gl);
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
