
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
import java.awt.BorderLayout;

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
import javax.swing.JFrame;
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
        JFrame janela = new JFrame("Campo");
        janela.setBounds(50,100,800,800);
        janela.add(canvas);
        janela.setVisible(true);

        //Adiciona um listener para o fechamento da janela
        janela.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        // Cria um objeto GLCapabilities para especificar o número de bits 
        // por pixel para RGBA
        caps.setRedBits(8);
        caps.setBlueBits(8);
        caps.setGreenBits(8);
        caps.setAlphaBits(8);

        CG_Campo app = new CG_Campo();

        // Cria um canvas, adiciona na janela, e especifica o objeto "ouvinte" 
        // para os eventos Gl, de mouse e teclado
        janela.add(canvas, BorderLayout.CENTER);
        canvas.addGLEventListener(app);
        canvas.addMouseListener(app);
        canvas.addKeyListener(app);
        janela.setVisible(true);
        canvas.requestFocus();

        FPSAnimator animator = new FPSAnimator(canvas, 60);
        animator.start();
    }

    public CG_Campo() {
        // Especifica o ângulo da projeção perspectiva  
        angulo = 50;
        // Inicializa o valor para correção de aspecto   
        aspecto = 1;

        // Inicializa os atributos usados para alterar a posição do 
        // observador virtual (=câmera)
        rotX = 0;
        rotY = 0;
        obsZ = 200;

    }

    /**
     * Variáveis da classe
     */
    private GL2 gl;
    private GLU glu;// objeto da classe GLU
    private GLUT glut; // objeto da classe GLUT
    private int texture;
    private int idTextura[]; // vetor de textura
    private int largura, altura;//valores obtidos da imagem no loadImage
    private ByteBuffer buffer;// Conteúdo gerado no loadImage
    private BufferedImage imagem;
    private TextureData texData;
    private Texture texturaCampo;
    private Texture texturaLadoCampo;
    private Texture texturaFrenteCampo;
    private double angulo, aspecto;// valor setado no construtor
    private float rotX, rotY, obsZ;
    private GLAutoDrawable glDrawable;

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
        gl = drawable.getGL().getGL2();
        glu = new GLU();
        glut = new GLUT();
        //Habilita o uso da textura
        gl.glEnable(GL2.GL_TEXTURE_2D);

        // Comandos de inicialização para textura
        loadImage("campo.jpg");

        // Gera identificador de textura
        idTextura = new int[10];
        gl.glGenTextures(1, idTextura, 1);

        // Especifica qual é a textura corrente pelo identificador 
        gl.glBindTexture(GL.GL_TEXTURE_2D, idTextura[0]);

        // Envio da textura para OpenGL
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, 3, largura,
                altura, 0, GL.GL_BGR, GL.GL_UNSIGNED_BYTE, buffer);

        // Define os filtros de magnificação e minificação 
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);

        // Cor do fundo da tela
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    /**
     * Comandos de inicialização para textura
     *
     * @param nomeArq nome do arquivo
     */
    public void loadImage(String nomeArq) {
        // Tenta carregar o arquivo		
        imagem = null;
        try {
            imagem = ImageIO.read(new File("src\\" + nomeArq));
            // Obtém largura e altura
            largura = imagem.getWidth();
            altura = imagem.getHeight();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro na leitura do arquivo " + nomeArq);
            System.out.println(e.getStackTrace());
        }

        //Carrega a textura		
        try {
            InputStream stream = getClass().getResourceAsStream(nomeArq);
            texData = TextureIO.newTextureData(GLProfile.getDefault(), stream, false, "jpg");
        } catch (IOException exc) {
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
        //Habilita o uso da textura
        //gl.glEnable(GL2.GL_TEXTURE_2D); //já foi habilitada em init
        
        // Limpa a janela de visualização com a cor de fundo especificada
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        gl.glColor3f(0.0f, 0.0f, 0.0f);

        especificaParametrosVisualizacao();

        renderizaCampo();
//        renderizaEsfera();
    }

    /**
     * Método que renderiza o campo no display
     */
    private void renderizaCampo() {
        try {
            //textura do campo
            InputStream stream = getClass().getResourceAsStream("campo.jpg");
            TextureData data = TextureIO.newTextureData(GLProfile.getDefault(), stream, false, "jpg");
            texturaCampo = TextureIO.newTexture(data);
            //textura das laterais
            stream = getClass().getResourceAsStream("ladoCampo.jpg");
            data = TextureIO.newTextureData(GLProfile.getDefault(), stream, false, "jpg");
            texturaLadoCampo = TextureIO.newTexture(data);
            //textura do frontal
            stream = getClass().getResourceAsStream("frenteCampo.jpg");
            data = TextureIO.newTextureData(GLProfile.getDefault(), stream, false, "jpg");
            texturaFrenteCampo = TextureIO.newTexture(data);
        } catch (IOException exc) {
            exc.printStackTrace();
            System.exit(1);
        }

        //Habilita as texturas do campo
        texturaCampo.enable(gl);
        texturaCampo.bind(gl);

        // Desenha um cubo no qual a textura é aplicada
        gl.glEnable(GL.GL_TEXTURE_2D);	// Primeiro habilita uso de textura	  	

        gl.glPushMatrix();
            gl.glTranslatef(0.0f, 0.0f, 0.0f);
            gl.glScalef(50.0f, 50.0f, 50.0f);//escala do objeto
            gl.glColor3f(1.0f, 1.0f, 1.0f);

            gl.glBegin(GL2.GL_QUADS);
            // Especifica a coordenada de textura para cada vértice
            // Face frontal
                gl.glNormal3f(0.0f,0.0f,-1.0f);
                gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f, -1.0f,  1.0f);
                gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1.0f, -1.0f,  1.0f);
                gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 1.0f,  1.0f,  1.0f);
                gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f,  1.0f,  1.0f);				
            // Face posterior
    //            gl.glNormal3f(0.0f,0.0f,1.0f);
    //            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f, -1.0f);
    //            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f, -1.0f);
    //            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f, -1.0f);
    //            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f, -1.0f);
            // Face superior
    //            gl.glNormal3f(0.0f,1.0f,0.0f);
    //            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f, -1.0f);
    //            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f,  1.0f,  1.0f);
    //            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 1.0f,  1.0f,  1.0f);
    //            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f, -1.0f);
            // Face inferior
            gl.glNormal3f(0.0f, -1.0f, 0.0f);
            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1.0f, -1.0f, -1.0f);
            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(1.0f, -1.0f, -1.0f);
            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(1.0f, -1.0f, 1.0f);
            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f, 1.0f);
                // Face lateral direita
    //            gl.glNormal3f(1.0f,0.0f,0.0f);
    //            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f, -1.0f);
    //            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f, -1.0f);
    //            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f,  1.0f);
    //            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f,  1.0f);
            // Face lateral esquerda
    //            gl.glNormal3f(-1.0f,0.0f,0.0f);
    //            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f, -1.0f);
    //            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f,  1.0f);
    //            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f,  1.0f);
    //            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f, -1.0f);
            gl.glEnd();
        gl.glPopMatrix();
        gl.glDisable(GL.GL_TEXTURE_2D);	//	Desabilita uso de textura
    }

    private void renderizaEsfera() {
        gl.glLineWidth(2);
        gl.glColor3f(0.0f, 0.0f, 1.0f);

        gl.glPushMatrix();
        gl.glTranslatef(40.0f, 0.0f, 0.0f);
        glut.glutSolidSphere(24, 30, 30);
        gl.glPopMatrix();
    }

    /**
     * Chamado após o redimensionamento do componente ou da janela de
     * visualização
     *
     * @param drawable
     * @param x
     * @param y
     * @param w width
     * @param h height
     */
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        gl.glViewport(0, 0, w, h);
        aspecto = (float) w / (float) h;
    }

    /**
     * Método usado para especificar a posição do observador virtual (=câmera).
     */
    public void posicionaObservador() {
        // Especifica sistema de coordenadas do modelo
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        // Inicializa sistema de coordenadas do modelo
        gl.glLoadIdentity();
        // Especifica posição do observador e do alvo
        gl.glTranslatef(0, 0, -obsZ);
        gl.glRotatef(rotX, 1, 0, 0);
        gl.glRotatef(rotY, 0, 1, 0);
    }

    /**
     * Método usado para especificar o volume de visualização.
     */
    public void especificaParametrosVisualizacao() {
        // Especifica sistema de coordenadas de projeção
        gl.glMatrixMode(GL2.GL_PROJECTION);
        // Inicializa sistema de coordenadas de projeção
        gl.glLoadIdentity();

        // Especifica a projeção perspectiva(angulo,aspecto,zMin,zMax)
        glu.gluPerspective(angulo, aspecto, 0.2, 500);

        posicionaObservador();
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
            case KeyEvent.VK_LEFT:
                rotY--;
                break;
            case KeyEvent.VK_RIGHT:
                rotY++;
                break;
            case KeyEvent.VK_UP:
                rotX++;
                break;
            case KeyEvent.VK_DOWN:
                rotX--;
                break;
            case KeyEvent.VK_HOME:
                obsZ++;
                break;
            case KeyEvent.VK_END:
                obsZ--;
                break;
//            case KeyEvent.VK_F1:
//                luz = !luz;
//            break;											
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
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
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) // Zoom in
        {
            if (angulo >= 4) {
                angulo -= 4;
            }
        }
        if (e.getButton() == MouseEvent.BUTTON3) // Zoom out
        {
            if (angulo <= 72) {
                angulo += 4;
            }
        }
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
