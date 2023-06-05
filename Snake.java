package snake;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Snake extends JFrame {

	int ancho = 640;
	int alto = 480;

	Point snake;
	Point manzana;

	ArrayList<Point> serpiente = new ArrayList<Point>();

	int widthPoint = 10, heightPoint = 10;

	int direccion = 0;

	long frecuencia = 50;
	
	int puntuacion = 0;
	
	int record = 0;

	ImagenSnake imagenSnake;

	Momento momento;

	boolean inicio = false;
	boolean perder = false;
	boolean newRecord = false;

	public Snake() {
		setTitle("Snake");
		setSize(ancho, alto);

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dim.width / 2 - ancho / 2, dim.height / 2 - alto / 2);

		Teclas teclas = new Teclas();
		addKeyListener(teclas);

		imagenSnake = new ImagenSnake();
		getContentPane().add(imagenSnake);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setVisible(true);
		
		imagenSnake.setBackground(Color.black);

		momento = new Momento();
		
		empezar();
		

	}

	public class ImagenSnake extends JPanel {
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (!inicio) {
				// Si el juego no ha comenzado, dibujar el texto inicial
				g.setColor(Color.white);
				Font font = new Font("Arial", Font.BOLD, 20);
				g.setFont(font);

				String mensaje = "Pulsa cualquier flecha para comenzar";
				int mensajeAncho = g.getFontMetrics().stringWidth(mensaje);
				int mensajeAlto = g.getFontMetrics().getHeight();
				int mensajeX = (ancho - mensajeAncho) / 2;
				int mensajeY = (alto - mensajeAlto) / 2;

				g.drawString(mensaje, mensajeX, mensajeY);
			} else if (!perder) {
				g.setColor(new Color(255, 0, 120));
				for (Point p : serpiente) {
					g.fillRect(p.x - widthPoint, p.y - heightPoint, widthPoint, heightPoint);
				}
				g.setColor(Color.red);
				g.fillRect(manzana.x - widthPoint, manzana.y - heightPoint, widthPoint, heightPoint);
			}
			
			if(inicio) {
                g.setColor(Color.white);
                Font font = new Font("Sans serif", Font.PLAIN, 17); // Cambiar el estilo y tamaño de la fuente
                g.setFont(font);

                String puntuacionTexto = "Puntuación: " + puntuacion;
                g.drawString(puntuacionTexto, 10, 20);
			}
			
			if(perder) {
				//mensaje derrota
                g.setColor(Color.white);
                Font font = new Font("Arial", Font.BOLD, 20); // Cambiar el estilo y tamaño de la fuente

                g.setFont(font);

        		String mensaje = "Has perdido";
        		
        		g.drawString(mensaje, 250, 220);
        		
        		//mensaje reiniciar
                Font pequeña = new Font("Calibri", Font.ITALIC, 20);
        		g.setFont(pequeña);
        		g.drawString("Enter para una nueva partida", 200, 240);
        		
        		//mensaje salir
        		Font salir = new Font("Arial", Font.PLAIN, 15);
        		g.setFont(salir);
        		g.setColor(Color.black);
        		g.drawString("Pulsa ESC para salir", 240, 420);
        		
        		//mensaje record
        		if(newRecord) {
        			Font fnewRecord = new Font("Open Sans Semibold", Font.BOLD, 25);
        			g.setFont(fnewRecord);
        			g.setColor(new Color(98, 40, 76));
        			g.drawString("NUEVO RECORD: " + record, 200, 70);
        		} else {
            		Font frecord = new Font("Arial", Font.ITALIC, 20);
            		g.setFont(frecord);
            		g.setColor(Color.WHITE);
            		g.drawString("Record: " + record, 255, 60);
        		}
			}
			
		}
		

	}
	
	public static void main(String[] args) {
		Snake s = new Snake();
	}
	
	public int GenerarAleatorio(int superior, int inferior) {
		 int aleatorio = inferior + (int) (Math.random() * ((superior - inferior) + 1));
		 return aleatorio;
	}
	
	public void guardarRecord() {
		try {
			DataOutputStream dos = new DataOutputStream(new FileOutputStream("record.dat"));
			record = puntuacion;
			dos.write(record);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void crearComida() {
		manzana.x = GenerarAleatorio(ancho - 20, 10);
		if ((manzana.x % 10) > 0) {
			manzana.x = manzana.x - (manzana.x % 10);
		}

		manzana.y = GenerarAleatorio(alto - 40, 10);
		if ((manzana.y % 10) > 0) {
			manzana.y = manzana.y - (manzana.y % 10);
		}
	}

	public void empezar() {
		manzana = new Point(200, 200);
	    int snakeX = ancho / 2 - (ancho / 2) % widthPoint;
	    int snakeY = alto / 2 - (alto / 2) % heightPoint;
	    snake = new Point(snakeX, snakeY);
		crearComida();
	    serpiente.clear();
	    serpiente.add(snake);
	    puntuacion = 0;
	}
	
    public void iniciarJuego() {
        if (!momento.isAlive()) {
            momento = new Momento();
            momento.start();
        }
    }

	public void actualizar() {
		if (momento.isAlive() && !perder) {
			imagenSnake.repaint();
		}

		serpiente.add(0, new Point(snake.x, snake.y));
		serpiente.remove(serpiente.size() - 1);

		for (int i = 1; i < serpiente.size(); i++) {
			Point punto = serpiente.get(i);
			if (snake.x == punto.x && snake.y == punto.y) {
				perdiste();
				break;
			}
		}

		if (snake.x == manzana.x && snake.y == manzana.y) {
			serpiente.add(0, new Point(snake.x, snake.y));
			puntuacion++;
			crearComida();
		}
		
	}
	
	public void cargarRecord() {
		try {
			DataInputStream dis = new DataInputStream(new FileInputStream("record.dat"));
			record = dis.read();
		} catch (FileNotFoundException e) {
			if(puntuacion > 0) {
				newRecord = true;
			}
			guardarRecord();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void perdiste() {
	    perder = true;
	    imagenSnake.setBackground(new Color(231, 133, 225));
	    cargarRecord();
	    if(puntuacion>record) {
	    	newRecord = true;
	    	guardarRecord();
	    }
	}

	public class Teclas extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			if (!inicio) {
				// Si el juego no ha comenzado, verificar si se ha pulsado una flecha
				if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN
						|| e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
					inicio = true;
					iniciarJuego();
				}
			}
			
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					System.exit(0);
				} else if (e.getKeyCode() == KeyEvent.VK_UP) {
					if (direccion != KeyEvent.VK_DOWN) {
						direccion = KeyEvent.VK_UP;
					}
				} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					if (direccion != KeyEvent.VK_UP) {
						direccion = KeyEvent.VK_DOWN;
					}
				} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					if (direccion != KeyEvent.VK_RIGHT) {
						direccion = KeyEvent.VK_LEFT;
					}
				} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					if (direccion != KeyEvent.VK_LEFT) {
						direccion = KeyEvent.VK_RIGHT;
					}
				}
				
				if(perder) {
					if(e.getKeyCode() == KeyEvent.VK_ENTER) {
						reiniciarPartida();
					}
				}
		}
	}
	
	public void reiniciarPartida() {
	    inicio = false;
	    perder = false;
	    imagenSnake.setBackground(Color.black);
	    serpiente.clear();
	    puntuacion = 0;
	    direccion = 0; // Reiniciar la dirección a cero
	    empezar();
	    momento = new Momento();
	    momento.start();
	    actualizar();
	    newRecord = false;
	}

	public class Momento extends Thread {
		long last = 0;

		public void run() {
			boolean perder = false;
			while (!perder) {
				if ((java.lang.System.currentTimeMillis() - last) > frecuencia) {

					if (direccion == KeyEvent.VK_UP) {
						snake.y = snake.y - heightPoint;
						if (snake.y <= 0) {
							perder = true;
						}
					} else if (direccion == KeyEvent.VK_DOWN) {
						snake.y = snake.y + heightPoint;
						if (snake.y >= 450) {
							perder = true;
						}
					} else if (direccion == KeyEvent.VK_RIGHT) {
						snake.x = snake.x + widthPoint;
						if (snake.x >= 640) {
							perder = true;
						}
					} else if (direccion == KeyEvent.VK_LEFT) {
						snake.x = snake.x - widthPoint;
						if (snake.x <= 0) {
							perder = true;
						}
					}

					if(perder) {
						perdiste();
						break;
					}
					
					if (!perder) {
						actualizar();
					}

					last = java.lang.System.currentTimeMillis();
				}
			}
		}
	}
}