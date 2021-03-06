package main;

import controlP5.*;
import org.philhosoft.p8g.svg.P8gGraphicsSVG;
import processing.core.PApplet;
import toxi.geom.Vec2D;
import toxi.physics2d.VerletParticle2D;
import toxi.processing.ToxiclibsSupport;
import util.Color;
import util.XGen;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Arrays;

public class App extends PApplet {
	public static PApplet P5;
	public static final DecimalFormat DF3 = new DecimalFormat("#.###");
	public static boolean RECORDING = false, UPDATE_PHYSICS = true, UPDATE_PHYSVAL = true, SHOW_PARTICLES = true;
	//	public static final String xmlFilePath = "/flowgraph_test.xml";	//	public static final String xmlFilePath = "C:\\Users\\admin\\Projects\\IdeaProjects\\thesis\\version_1.2\\data\\flowgraph_test.xml";
	public static final String xmlFilePath = "F:\\Java\\Projects\\thesis_2014\\version_1.3\\xml\\flowgraph_test.xml";
	public static boolean SHOW_MINDIST, SHOW_ATTRACTORS, SHOW_VOR_VERTS, SHOW_VOR_INFO, SHOW_VOIDS, UPDATE_VORONOI, SHOW_VORONOI, SHOW_TAGS, SHOW_SPRINGS = true, SHOW_NODES = true, SHOW_INFO = true;
	public static float ZOOM = 1, SCALE = 10, DRAG = 0.3f, SPR_SCALE = 1f, SPR_STR = 0.01f, ATTR_RAD = 60, ATTR_STR = -0.9f, NODE_STR = -.5f, NODE_SCALE = 1, NODE_PAD = 0, OBJ_SIZE = 1, OBJ_COLOR = 1, VOR_REFRESH = 1, MIN = 0.1f;
	public static String OBJ_NAME = "new", DRAWMODE = "bezier";
	public static Vec2D MOUSE = new Vec2D();
	private static ControlP5 CP5;
	private ToxiclibsSupport GFX;
	private PSys PSYS;
	public static VSys VSYS;
	public static FSys FSYS;
	public static XGen gen = new XGen();
	public static Edit EDIT;
	public static View VIEW;
	static Println console;
	static Textarea myTextarea;
	public boolean isShiftDown;
	private Vec2D offset2d;
	private int drawMode;
	private static Knob obSizeCtrl;

	public static void main(String[] args) { PApplet.main(new String[]{("main.App")}); }
	public static void __rebelReload() {
		System.out.println("barney!!");
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		System.out.println("Current relative path is: " + s);
		System.out.println("Working Directory = " + System.getProperty("user.dir"));
		setupCP5();
	}
	public void setup() {
		P5 = this;
		GFX = new ToxiclibsSupport(this);
		CP5 = new ControlP5(this);
		EDIT = new Edit(this);
		VIEW = new View(this);
		initParticleSystem();
//		PSYS = new PSys(width/2,height/2);
//		VSYS = new VSys(this);
		FSYS = new FSys();
		size(1600, 900);
		frameRate(60);
		smooth(4);
		colorMode(HSB, 360, 100, 100);
		ellipseMode(CENTER);
		textAlign(LEFT);
		strokeWeight(1);
		noStroke();
		noFill();
		setupCP5();
		setupObProperties();
//		loadFGSYS();
		printPaths();
	}
	private void printPaths(){
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		System.out.println("Current relative path is: " + s);
		System.out.println("Working Directory = " + System.getProperty("user.dir"));
	}
	public void draw() {
		background(Color.BG);
		MOUSE.set(mouseX, mouseY);

		pushMatrix();
		translate(-((ZOOM * width) - width) / 2, -((ZOOM * height) - height) / 2);
		scale(ZOOM);
		VIEW.render();
		popMatrix();
		if (UPDATE_PHYSICS) {
			PSYS.update();
			/*for (FSys.Node c : FSYS.nodes) {
				c.getBehavior().setRadius();
			}*/
		}
//		if (SHOW_PARTICLES) PSYS.display(GFX);
		EDIT.display();
//		if (VSYS != null) VSYS.display();
		if (FSYS != null) FSYS.display();
		PSYS.update();
		drawShapes2D();
		if (RECORDING) { RECORDING = false; endRecord(); System.out.println("SVG EXPORTED SUCCESSFULLY"); }
		noStroke();
		fill(Color.BG_MENUS);
		rect(0, 0, 218, height);
		rect(1500, 0, width, height);
		fill(Color.CP5_BG);
		rect(220, 0, 80, height);
		noFill(); CP5.draw();
//		float n = sin(frameCount * 0.01f) * 300;
//		println(frameCount + "\t" + String.format("%.2f", frameRate) + "\t" + String.format("%.2f", n));
	}
	private void drawShapes2D() {
		pushMatrix();
//		translate(offset2d.x, offset2d.y);
		PSYS.draw(GFX);
		popMatrix();
	}
	void loadFGSYS() {
		try {
			JAXBContext context = JAXBContext.newInstance(FSys.class);
			FSYS = (FSys) context.createUnmarshaller().unmarshal(createInput(xmlFilePath));
		} catch (JAXBException e) { println("error parsing xml: "); e.printStackTrace(); System.exit(1); }
		PSYS.clear();
//		VSYS = new VSys(this);
		FSYS.build();
		setupFlowgraph();
	}
	void setupFlowgraph() {
		for (FSys.Node c : FSYS.nodes) {
			PSYS.addParticle(c.getPos(), c.getBehavior().getRadius());
			VSYS.addCell(c.getVerlet());
			VSYS.addSite(c.getVerlet(), c.getColor());
		} for (FSys.Relation r : FSYS.relations) {
			FSys.Node na = FSYS.getNodeIndex().get(r.from);
			FSys.Node nb = FSYS.getNodeIndex().get(r.to);
			VerletParticle2D va = na.verlet;
			VerletParticle2D vb = nb.verlet;
			float l = na.getRadius() + nb.getRadius();
//			PSYS.addSpring(new VerletSpring2D(va, vb, l, 0.01f));
		}
	}
	private void initParticleSystem() {
		PSYS = new PSys(width / 2, height / 2);
		offset2d = new Vec2D(width, height).sub(
				PSYS.getBounds().getDimensions()).scale(0.5f);
	}
	static void setupCP5() {
		CP5.enableShortcuts();
		CP5.setAutoDraw(false);
		CP5.setColorBackground(Color.CP5_BG).setColorForeground(Color.CP5_FG).setColorCaptionLabel(Color.CP5_CAP)
				.setColorActive(Color.CP5_ACT).setColorValueLabel(Color.CP5_VAL).setAutoSpacing(4, 8);
		FrameRate FPS = CP5.addFrameRate();
		FPS.setInterval(3).setPosition(20, HEIGHT - 20).draw();

		setupControllers();
	}
	static void setupControllers() {
		CP5.begin(220, 0);
		CP5.addButton("quit").linebreak();
		CP5.addButton("load_xml").linebreak();
		CP5.addButton("load_conf").linebreak();
		CP5.addButton("save_svg").linebreak();
		CP5.addButton("save_conf").linebreak();
		CP5.addButton("regen").linebreak();
		CP5.addButton("get_gen").linebreak();
		CP5.addButton("load_def").linebreak();
		CP5.addButton("save_def").linebreak();
		CP5.addButton("add_rand").linebreak();
		CP5.addButton("add_perim").linebreak();
		CP5.addButton("add_mindist").linebreak();
		CP5.addButton("clear_phys").linebreak();
		CP5.addBang("Display").linebreak();
		CP5.addToggle("SHOW_INFO").setCaptionLabel("DATA").linebreak();
		CP5.addToggle("SHOW_NODES").setCaptionLabel("NODES").linebreak();
		CP5.addToggle("SHOW_PARTICLES").setCaptionLabel("PARTICLES").linebreak();
		CP5.addToggle("SHOW_SPRINGS").setCaptionLabel("SPRINGS").linebreak();
		CP5.addToggle("SHOW_ATTRACTORS").setCaptionLabel("ATTRACTORS").linebreak();
		CP5.addToggle("SHOW_MINDIST").setCaptionLabel("PROXIMITY").linebreak();
		CP5.addToggle("SHOW_VORONOI").setCaptionLabel("VORONOI").linebreak();
		CP5.addToggle("SHOW_VOR_VERTS").setCaptionLabel("VOR. COMPONENTS ").linebreak();
		CP5.addToggle("SHOW_VOR_INFO").setCaptionLabel("VOR. DEBUG").linebreak();
		CP5.addToggle("SHOW_VOIDS").setCaptionLabel("VOIDS").linebreak();
		CP5.addBang("SIMULATION").linebreak();
		CP5.addToggle("UPDATE_PHYSVAL").setCaptionLabel("PHYS DEBUG").linebreak();
		CP5.addToggle("UPDATE_PHYSICS").setCaptionLabel("PHYSICS").linebreak();
		CP5.addToggle("UPDATE_VORONOI").setCaptionLabel("VORONOI").linebreak();
		CP5.end();
		CP5.begin(10, 10);
		CP5.addSlider("SCALE").setRange(1, 20).setNumberOfTickMarks(20).linebreak();
		CP5.addSlider("DRAG").setRange(0.1f, 1).linebreak();
		CP5.addSlider("NODE_SCALE").setRange(0.1f, 2).setNumberOfTickMarks(20).linebreak();
		CP5.addSlider("NODE_STR").setRange(-2, 2).linebreak();
		CP5.addSlider("NODE_PAD").setRange(0.1f, 9).linebreak();
		CP5.addSlider("SPR_SCALE").setRange(0.1f, 2).setNumberOfTickMarks(20).linebreak();
		CP5.addSlider("SPR_STR").setRange(0.01f, 0.03f).linebreak();
		CP5.addSlider("ATTR_STR").setRange(-2f, 2).linebreak();
		CP5.addSlider("ATTR_RAD").setRange(0.1f, 400).linebreak();
		CP5.addSlider("VOR_REFRESH").setRange(1, 9).setNumberOfTickMarks(9).linebreak();
		CP5.end();
		CP5.begin(10, 10);
		CP5.addNumberbox("ITER_A").setPosition(10, 14).linebreak();
		CP5.addNumberbox("ITER_B").setPosition(10, 38).linebreak();
		CP5.addNumberbox("ITER_C").setPosition(10, 62).linebreak();
		CP5.addNumberbox("ITER_D").setPosition(10, 86).linebreak();
		CP5.addNumberbox("ITER_E").setPosition(10, 110).linebreak();
//		CP5.addTextfield("iterator").setPosition(10, 10).setText("2 3 4 6").linebreak();
//		CP5.addTextfield("sizes").setPosition(10, 42).setText("80 45 10 5").linebreak();
//		CP5.addTextfield("names").setPosition(10, 74).setText("A B C D").linebreak();
		CP5.end();

		myTextarea = CP5.addTextarea("txt").setPosition(10, 0).setSize(200, 290);
		console = CP5.addConsole(myTextarea);//
		styleControllers();
	}
	private void setupObProperties() {
		CP5.begin(10, 32);
		obSizeCtrl = CP5.addKnob("OBJ_SIZE").setRange(0, 200).setValue(50).setPosition(30, 20);
		obSizeCtrl.setCaptionLabel("size");
		obSizeCtrl.addListener(new ControlListener() {
			@Override
			public void controlEvent(ControlEvent e) { PSYS.getSelectedAttractor().setRadius(e.getController().getValue()); }
		});
		obSizeCtrl.hide();
		CP5.addKnob("OBJ_HUE").setRange(0, 360).setValue(180).setPosition(120, 20);
		CP5.addTextfield("OBJ_NAME").setCaptionLabel("Unique Datablock ID Name").setPosition(20, 120).setText("untitled").linebreak();
		/*.setColorForeground(color(255)).setColorBackground(color(0, 160, 100)).setColorActive(color(255, 255, 0))*/
		CP5.end();
	}
	static void styleControllers() {
		//CP5.loadProperties(("./lib/config/defaults.ser"));Group file = CP5.addGroup("FILE").setBackgroundHeight(280);
		Group config = CP5.addGroup("VERLET PHYSICS SETTINGS").setBackgroundHeight(260).setBackgroundColor(0xff222222);
		Group generator = CP5.addGroup("RECURSIVE GRAPH GENERATOR").setBackgroundHeight(140).setBackgroundColor(0xff222222);
		Group properties = CP5.addGroup("OBJECT_PROPERTIES").setBackgroundHeight(200).setBackgroundColor(0xff222222);
		Group debug = CP5.addGroup("CONSOLE").setPosition(0, 600).setWidth(219).setBackgroundHeight(300).setBackgroundColor(0xff222222);
		for (Button b : CP5.getAll(Button.class)) {
			b.setSize(80, 22);
			b.setColorBackground(Color.CP5_BG);
			b.setColorActive(Color.CP5_FG);
			b.setColorForeground(0xff666666);
			b.getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER).getStyle();
			b.getCaptionLabel().setColor(Color.CP5_CAP);
		} for (Toggle t : CP5.getAll(Toggle.class)) {
			t.setSize(80, 16);
			t.setColorBackground(Color.CP5_BG);
			t.setColorActive(0xff555555);
			t.setColorForeground(0xff888888);
			t.getCaptionLabel().setColor(Color.CP5_CAP);
			t.getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER).getStyle();
		} for (Bang b : CP5.getAll(Bang.class)) {
			b.setSize(80, 24);
			b.setColorBackground(Color.CP5_BG);
			b.setColorActive(0xff555555);
			b.setColorForeground(0xff555555);
			b.getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER).getStyle();
			b.getCaptionLabel().setColor(Color.BG);
		} for (Slider s : CP5.getAll(Slider.class)) {
			s.setSize(170, 16);
			s.setGroup(config);
			s.showTickMarks(false).setHandleSize(8);
			s.setSliderMode(Slider.FLEXIBLE);
			s.setColorForeground(Color.CP5_FG);
			s.setColorActive(Color.CP5_CAP);
			s.getValueLabel().align(ControlP5.RIGHT_OUTSIDE, ControlP5.CENTER).getStyle().setPaddingLeft(4);
			s.getCaptionLabel().align(ControlP5.RIGHT, ControlP5.CENTER).getStyle().setPaddingRight(4);
		} for (Numberbox b : CP5.getAll(Numberbox.class)) {
			b.setSize(200, 16).setRange(0, 10).setDirection(Controller.HORIZONTAL).setMultiplier(0.05f).setDecimalPrecision(0);
			b.setGroup(generator);
			b.getCaptionLabel().align(ControlP5.RIGHT, ControlP5.CENTER);
			b.getValueLabel().align(ControlP5.LEFT, ControlP5.CENTER);
		} for (Knob k : CP5.getAll(Knob.class)) {
			k.setSize(16, 16);
			k.setRadius(30);
			k.setDragDirection(Knob.HORIZONTAL);
			k.setGroup(properties);
		} for (Textfield t : CP5.getAll(Textfield.class)) {
			t.setSize(180, 32);
			t.setAutoClear(false);
			t.setColorForeground(Color.CP5_ACT);
			t.setColorBackground(Color.BG_MENUS);
			t.setGroup(properties);
			t.getValueLabel().setPaddingX(6);
			t.getCaptionLabel().align(ControlP5.CENTER, ControlP5.BOTTOM_OUTSIDE).getStyle().setPaddingTop(4);
		} for (Textarea t : CP5.getAll(Textarea.class)) {
			t.setLineHeight(14);
			t.setGroup(debug);
			t.setColor(Color.CP5_CAP).setColorBackground(Color.CP5_BG).setColorForeground(Color.CP5_FG);
			t.getCaptionLabel().align(ControlP5.CENTER, ControlP5.BOTTOM_OUTSIDE).getStyle().setPaddingTop(4);
		}
		for (Group g : CP5.getAll(Group.class)) {
			g.setBarHeight(32);
			g.getCaptionLabel().align(ControlP5.LEFT, ControlP5.CENTER).getStyle().setPaddingLeft(4);
		}
		Accordion accordion = CP5.addAccordion("acc").setPosition(0, 0).setWidth(219).addItem(config).addItem(generator).addItem(properties);
//		accordion.open(0, 1, 2); accordion.isMouseOver();
		accordion.setCollapseMode(Accordion.MULTI);
	}

	public void mousePressed() {
		Vec2D mousePos = new Vec2D(mouseX, mouseY);
		if (mouseButton == RIGHT) {
			mousePos.subSelf(offset2d);
			PSYS.selectAttractorNearPosition(mousePos);
			if (PSYS.hasSelectedAttractor()) {
				obSizeCtrl.setValue(PSYS.getSelectedAttractor().getRadius());
				obSizeCtrl.show();
			} else { obSizeCtrl.hide(); }
		} else if (isShiftDown) {/*	arcBall.mousePressed(mousePos);	*/}
		EDIT.mousePressed();
	}
	public void mouseDragged() {
		Vec2D mousePos = new Vec2D(mouseX, mouseY);
		if (mouseButton == RIGHT) {
			if (drawMode == 0) { mousePos.subSelf(offset2d); PSYS.moveSelectedAttractor(mousePos); }
		} else if (isShiftDown) {
			if (drawMode > 0) {/* arcBall.mouseDragged(mousePos);*/ }
		} /*else if (splineEditor.mouseDragged(mousePos)) {if (drawMode > 0) { *//*computePointsOnSpline(); computeDisplacedShapes();*//* }		}*/
		EDIT.mouseDragged();
	}
	public void mouseReleased() {
		/*splineEditor.mouseReleased();arcBall.mouseReleased();*/
		EDIT.mouseReleased();
	}
	public void mouseMoved() {
		MOUSE.set(mouseX, mouseY);
	}
	public void keyPressed() {
		if (key == CODED && keyCode == SHIFT) { isShiftDown = true; }
		EDIT.keyPressed();
	}
	public void keyReleased() {
		if (key == CODED && keyCode == SHIFT) { isShiftDown = false; }
	}
	public void sizes(String theText) {
		String[] temp = splitTokens(theText, " ,.?!:;[]-\"");
		float[] slist = new float[temp.length];
		for (int i = 0; i < temp.length; i++) {
			slist[i] = Float.valueOf(temp[i]);
		} System.out.println(Arrays.toString(slist));
		gen.setSizes(slist);
	}
	public void names(String theText) {
		String[] nlist = splitTokens(theText, " ,.?!:;[]-\"");
		System.out.println(Arrays.toString(nlist));
		gen.setNames(nlist);
	}
	void quit(int theValue) { System.out.println("[quit]"); exit(); }
	void regen(int theValue) {System.out.println("[regen]"); gen.generate(); }
	void load_xml(int theValue) {System.out.println("[load_xml]"); println("[load_xml]"); loadFGSYS(); }
	void save_svg(int theValue) { beginRecord(P8gGraphicsSVG.SVG, "./out/svg/print-###.svg"); RECORDING = true; }
	void load_conf(int theValue) { CP5.loadProperties(("C:\\Users\\admin\\Projects\\IdeaProjects\\thesis\\version_1\\lib\\config\\config.ser")); }
	void load_def(int theValue) { CP5.loadProperties(("C:\\Users\\admin\\Projects\\IdeaProjects\\thesis\\version_1\\lib\\config\\defaults.ser")); }
	void save_conf(int theValue) { CP5.saveProperties(("C:\\Users\\admin\\Projects\\IdeaProjects\\thesis\\version_1\\lib\\config\\config.ser")); }
	void save_def(int theValue) { CP5.saveProperties(("C:\\Users\\admin\\Projects\\IdeaProjects\\thesis\\version_1\\lib\\config\\defaults.ser")); }
	void add_rand(int theValue) {System.out.println("[add_rand]"); EDIT.addRandom(20); }
	void add_perim(int theValue) {System.out.println("[add_perim]"); EDIT.addPerim(50); }
	void add_mindist() { /*PSYS.addMinDistSprings();*/ }
	void get_gen(int theValue) {
		System.out.println("[get_gen]");
		for (Textfield t : CP5.getAll(Textfield.class)) { t.submit(); }
		gen.generate();
		loadFGSYS();
	}
	void clear_phys(int theValue) {
		System.out.println("[get_gen]");
		PSYS.clear();
	}
	public PSys getPSYS() {return PSYS;}
}
