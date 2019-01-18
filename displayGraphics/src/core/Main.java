package core;

import java.util.ArrayList;


import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

import environment.Focus;
import environment.Light;
import environment.PhysicsObject;
import environment.Skybox;
import opengl.BasicShader;
import opengl.Camera;
import opengl.Display;
import opengl.HUDTexture;
import opengl.Obj;
import opengl.ObjTexture;
import opengl.RawObj;
import opengl.RenderGUI;
import opengl.RenderMaster;
import physics.CollisionDetector;

/*
 * @author Jim Doxtader
 */
public class Main implements Runnable {

	private int WIDTH;
	private int HEIGHT;
	private String title;
	private Display display;
	static final int FPS = 60;
	static final double time_per_update = 1000000000/FPS;
	static float lastTime;
	private Thread thread;
	private Boolean running;
	private GLFWKeyCallback keyCallback;


	AssetImporter objLoader;
	RenderMaster objRender;
	BasicShader shader;
	RenderGUI renderGUI;

	ObjTexture texture;
	ObjTexture texture2;
	ArrayList<PhysicsObject> renderObjects = new ArrayList<PhysicsObject>();
	ArrayList<PhysicsObject> physicsObjects = new ArrayList<PhysicsObject>();
	ArrayList<HUDTexture> hud = new ArrayList<HUDTexture>();
	ArrayList<Light> lights = new ArrayList<Light>();
	Skybox stars;
	Skybox stars2;
	Camera camera;
	Focus focus;
	Interpreter mapLoader;
	CollisionDetector collisionDetector;


	public Main(int width, int height, String name) {
		this.WIDTH = width;
		this.HEIGHT = height;
		this.title = name;
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error loading simulation!");
			System.exit(-1);
		}

	}

	public void init(){
		setup();
		running = true;
		GLFW.glfwInit();
		display = new Display(WIDTH,HEIGHT);
		objLoader = new AssetImporter();

		shader = new BasicShader();
		objRender = new RenderMaster(display,shader);
		mapLoader = new Interpreter();
		renderGUI = new RenderGUI(objLoader);
		loadSim("test");
		loadGUI();
		collisionDetector = new CollisionDetector(physicsObjects);
		lastTime = System.nanoTime();
		run();
	}

	public void setup() {
		//thread = new Thread( ,"secondary");
		//thread.start();						// TODO: thread cpu and gpu calls.


	}


	@Override
	public synchronized void run(){




		while(running) {


			objRender.renderAllObjects(lights, camera, renderObjects);
			renderGUI.render(hud);
			update();

		}

		exit();
	}


	public void update() {
		focus.update();
		camera.update(display);

		stars.setPosition(camera.getPosition());
		stars2.setPosition(camera.getPosition());
		float current = System.nanoTime();
		float dt = current - lastTime;
		dt /= 1000000000;
		for(PhysicsObject obj : physicsObjects ) {
			obj.update(dt);
		}
		collisionDetector.update();
		fpsRegulator();
		lastTime = current;

	}

	public void initHUD() {


	}

	public void fpsRegulator() {

		if(display.shouldExit) {
			running = false;
		}

		double delta = 0;
		long now;
		long last_time = System.nanoTime();
		long timer = 0;
		int upts = 0;
		double d2 = 1;
		while(true){
			now = System.nanoTime();
			delta += (now - last_time)/time_per_update;
			timer+= now-last_time;
			last_time = now;
			if(delta>=1){
				delta--;
				upts++;
				break;
			}
		}
	}




	public void loadSim(String file) {
		RawObj focusObj = objLoader.loadObjModel("asteroid");
		ObjTexture focusTex = new ObjTexture(objLoader.loadTexture("asteroid"));
		focus = new Focus(new Obj(focusObj,focusTex),new Vector3f(3,2,-WIDTH/2 + 50), 0f,180.0f,0f,0.001f,display);
		mapLoader.loadMapData(file,objLoader);
		mapLoader.link();
		//lights = mapLoader.getLights();
		lights = new ArrayList<Light>();
		lights.add(new Light(new Vector3f(1000,1,-1000),new Vector3f(1,1,1)));
		lights.add(new Light(new Vector3f(0,0,0),new Vector3f(1,1,1)));

		physicsObjects = mapLoader.getPhysicsObjects();
		stars = mapLoader.getSkybox();
		stars2 = mapLoader.getSkybox2();
		camera = new Camera(focus,display);
		renderObjects.add(focus);
		renderObjects.add(stars);
		renderObjects.add(stars2);
		for(PhysicsObject obj : physicsObjects) {
			renderObjects.add(obj);
		}
	}

	public void loadGUI() {
	}











	public void exit() {
		shader.close();
		objLoader.clearData();
		System.exit(0);
	}
}
