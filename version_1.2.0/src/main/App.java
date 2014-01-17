package main;

import controlP5.*;
import org.philhosoft.p8g.svg.P8gGraphicsSVG;
import processing.core.PApplet;
import toxi.geom.Vec2D;
import toxi.physics2d.VerletParticle2D;
import toxi.physics2d.VerletSpring2D;
import toxi.processing.ToxiclibsSupport;
import util.Color;
import util.XGen;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.text.DecimalFormat;
import java.util.Arrays;
//import processing.core.PFont;

public class App extends PApplet {

	public static final DecimalFormat DF3 = new DecimalFormat("#.###");
	public static final String xmlFilePath = "C:\\Users\\admin\\Projects\\IdeaProjects\\thesis\\version_1.2\\data\\flowgraph_test.xml";
	public static final float MIN = 0.1f;
	private static boolean RECORDING = false;
	public static final boolean UPDATE_PHYSICS = true;
	public static final boolean UPDATE_PHYSVAL = true;
	public static final boolean SHOW_PARTICLES = true;
	public static final boolean SHOW_SPRINGS = true;
	public static final boolean SHOW_NODES = true;
	public static boolean SHOW_MINDIST, SHOW_ATTRACTORS, SHOW_INFO, SHOW_TAGS;
	public static boolean UPDATE_VORONOI, SHOW_VORONOI, SHOW_VOR_VERTS, SHOW_VOR_INFO, SHOW_VOIDS;
	private static final float ZOOM = 1;
	public static final float SCALE = 10;
	public static final float DRAG = 0.5f;
	public static float VOR_REFRESH = 1;
	public static final float SPR_SCALE = 1;
	public static final float SPR_STR = 0.01f;
	public static final float ATTR_RAD = 60;
	public static final float ATTR_STR = -0.9f;
	public static final float NODE_STR = -1;
	public static final float NODE_SCALE = 1;
	public static final float NODE_PAD = 1;
	public static final float OBJ_SIZE = 1;
	public static final float OBJ_COLOR = 1;
	public static final String OBJ_NAME = "new";
	public static PApplet P5;
	private static ControlP5 CP5;
	public static ToxiclibsSupport GFX;
	public static final Vec2D MOUSE = new Vec2D();
	public static PSys PSYS;
	private static VSys VSYS;
	public static FSys FSYS;
	private static final XGen gen = new XGen();
	public static String DRAWMODE = "bezier";
	private static Edit EDIT;
	private static View VIEW;

	public static void main(String[] args) {
		PApplet.main(new String[]{("main.App")});
	}
	public static void __rebelReload() {
		System.out.println("barney!!");
	}

	public void setup() {
		P5 = this;
		GFX = new ToxiclibsSupport(this);
		CP5 = new ControlP5(this);
		EDIT = new Edit(this);
		VIEW = new View(this);
//		PSYS = new PSys(this);
		size(1800, 900);
		frameRate(60);
		smooth(4);
		colorMode(HSB, 360, 100, 100);
		ellipseMode(CENTER);
		textAlign(LEFT);
		strokeWeight(1);
		noStroke();
		noFill();
		setupCP5();
		loadFGSYS();
	}
	public void draw() {
		background(Color.BG);
		MOUSE.set(mouseX, mouseY);
		noStroke();
		fill(Color.BG_MENUS);
		rect(0, 0, 300, height);
		rect(1500, 0, width, height);
		noFill();
		pushMatrix();
		translate(-((ZOOM * width) - width) / 2, -((ZOOM * height) - height) / 2);
		scale(ZOOM);
		VIEW.render();
		popMatrix();
		if (PSYS != null) PSYS.display();
		if (VSYS != null) VSYS.display();
		if (FSYS != null) FSYS.display(this);
		if (RECORDING) { RECORDING = false; endRecord(); System.out.println("SVG EXPORTED SUCCESSFULLY"); }
		CP5.draw();
	}

	void loadFGSYS() {
		try {
			JAXBContext context = JAXBContext.newInstance(FSys.class);
			FSYS = (FSys) context.createUnmarshaller().unmarshal(createInput(xmlFilePath));
		} catch (JAXBException e) { println("error parsing xml: "); e.printStackTrace(); System.exit(1); }
		PSYS = new PSys(this);
		VSYS = new VSys(this);
		FSYS.build();
		setupFlowgraph();
	}
	void setupFlowgraph() {
		for (FSys.Node c : FSYS.nodes) {
			PSYS.addParticle(c.getVerlet(), c.getBehavior());
			VSYS.addCell(c.getVerlet());
			VSYS.addSite(c.getVerlet(), c.color);
		} for (FSys.Relation r : FSYS.relations) {
			FSys.Node na = FSYS.getNodeIndex().get(r.from);
			FSys.Node nb = FSYS.getNodeIndex().get(r.to);
			VerletParticle2D va = na.verlet;
			VerletParticle2D vb = nb.verlet;
			float l = na.getRadius() + nb.getRadius();
			PSYS.addSpring(new VerletSpring2D(va, vb, l, 0.01f));
		}

	}

	void setupCP5() {
		CP5.setAutoDraw(false);
		CP5.setColorBackground(Color.CP5_BG).setColorForeground(Color.CP5_FG).setColorCaptionLabel(Color.CP5_CAP).setColorActive(Color.CP5_ACT).setColorValueLabel(Color.CP5_VAL);
		FrameRate FPS = CP5.addFrameRate();
		FPS.setInterval(3).setPosition(20, height - 20).draw();
		setupControllers();
	}
	void setupControllers() {
		CP5.begin(20, 10);
		CP5.addButton("quit").linebreak();
		CP5.addButton("regen").linebreak();
		CP5.addButton("load_xml").linebreak();
		CP5.addButton("save_svg").linebreak();
		CP5.addButton("load_def").linebreak();
		CP5.addButton("load_conf").linebreak();
		CP5.addButton("save_def").linebreak();
		CP5.addButton("save_conf").linebreak();
		CP5.addButton("add_rand").linebreak();
		CP5.addButton("add_perim").linebreak();
		CP5.addButton("add_mindist").linebreak();
		CP5.addButton("get_gen").linebreak();
		CP5.addButton("").linebreak();
		CP5.end();
		CP5.begin(150, 10);
		CP5.addToggle("UPDATE_PHYSICS").linebreak();
		CP5.addToggle("UPDATE_VORONOI").linebreak();
		CP5.addToggle("UPDATE_PHYSVAL").linebreak();
		CP5.addToggle("SHOW_ATTRACTORS").linebreak();
		CP5.addToggle("SHOW_INFO").linebreak();
		CP5.addToggle("SHOW_PARTICLES").linebreak();
		CP5.addToggle("SHOW_SPRINGS").linebreak();
		CP5.addToggle("SHOW_MINDIST").linebreak();
		CP5.addToggle("SHOW_NODES").linebreak();
		CP5.addToggle("SHOW_VORONOI").linebreak();
		CP5.addToggle("SHOW_VOIDS").linebreak();
		CP5.addToggle("SHOW_VOR_VERTS").linebreak();
		CP5.addToggle("SHOW_VOR_INFO").linebreak();
		CP5.end();
		CP5.begin(20, 10);
		CP5.addSlider("VOR_REFRESH").setRange(1, 9).setNumberOfTickMarks(9).linebreak();
//		CP5.addSlider("ZOOM").setRange(MIN, 5).linebreak();
		CP5.addSlider("DRAG").setRange(0.1f, 1).linebreak();
		CP5.addSlider("SCALE").setRange(1, 20).setNumberOfTickMarks(20).linebreak();
		CP5.addSlider("SPR_SCALE").setRange(0.1f, 2).setNumberOfTickMarks(20).linebreak();
		CP5.addSlider("NODE_SCALE").setRange(0.1f, 2).setNumberOfTickMarks(20).linebreak();
		CP5.addSlider("SPR_STR").setRange(0.01f, 0.03f).linebreak();
		CP5.addSlider("NODE_STR").setRange(-20, 0).linebreak();
		CP5.addSlider("ATTR_STR").setRange(-4f, 4).linebreak();
		CP5.addSlider("NODE_PAD").setRange(0.1f, 9).linebreak();
		CP5.addSlider("ATTR_RAD").setRange(0.1f, 400).linebreak();
		CP5.end();

		CP5.begin(20, 90);
		CP5.addTextfield("OBJ_NAME").setPosition(20, 40).setText("new").linebreak();
		CP5.addNumberbox("OBJ_SIZE").setRange(0, 100).setDirection(Controller.HORIZONTAL).setMultiplier(0.05f).setDecimalPrecision(0).linebreak();
		CP5.addNumberbox("OBJ_COLOR").setScrollSensitivity(1.1f).setRange(0, 360).setValue(180).setDirection(Controller.HORIZONTAL).setDecimalPrecision(0).linebreak();
		CP5.end();
		CP5.addTextfield("iterator").setPosition(20, 10).setText("2 3 4 6").linebreak();
		CP5.addTextfield("sizes").setPosition(20, 30).setText("80 45 10 5").linebreak();
		CP5.addTextfield("names").setPosition(20, 50).setText("A B C D").linebreak();
		CP5.begin(20, 180);
		CP5.addNumberbox("ITER_A").setRange(0, 10).setDirection(Controller.HORIZONTAL).setMultiplier(0.05f).setDecimalPrecision(0).linebreak();
		CP5.addNumberbox("ITER_B").setRange(0, 10).setDirection(Controller.HORIZONTAL).setMultiplier(0.05f).setDecimalPrecision(0).linebreak();
		CP5.addNumberbox("ITER_C").setRange(0, 10).setDirection(Controller.HORIZONTAL).setMultiplier(0.05f).setDecimalPrecision(0).linebreak();
		CP5.addNumberbox("ITER_D").setRange(0, 10).setDirection(Controller.HORIZONTAL).setMultiplier(0.05f).setDecimalPrecision(0).linebreak();
		CP5.addNumberbox("ITER_E").setRange(0, 10).setDirection(Controller.HORIZONTAL).setMultiplier(0.05f).setDecimalPrecision(0).linebreak();
		CP5.end();
		styleControllers();
	}
	void styleControllers() {
		//CP5.loadProperties(("./lib/config/defaults.ser"));
		Group file = CP5.addGroup("FILE");
		Group config = CP5.addGroup("CONFIG").setPosition(0, 350);
		Group generator = CP5.addGroup("GENERATOR").setPosition(0, 600);
		for (Textfield t : CP5.getAll(Textfield.class)) {
			t.setSize(220, 16);
			t.setAutoClear(false);
			t.setGroup(generator);
			t.getCaptionLabel().align(ControlP5.RIGHT, ControlP5.CENTER).getStyle().setPaddingRight(4);
		} for (Button b : CP5.getAll(Button.class)) {
			b.setSize(80, 16);
			b.setGroup(file);
			b.getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER);
		} for (Numberbox b : CP5.getAll(Numberbox.class)) {
			b.setSize(200, 16);
			b.setGroup(generator);
			b.getCaptionLabel().align(ControlP5.RIGHT, ControlP5.CENTER);
			b.getValueLabel().align(ControlP5.LEFT, ControlP5.CENTER);
		} for (Toggle t : CP5.getAll(Toggle.class)) {
			t.setSize(16, 16);
			t.setGroup(file);
			t.getCaptionLabel().align(ControlP5.RIGHT_OUTSIDE, ControlP5.CENTER).getStyle().setPaddingLeft(4);
		} for (RadioButton t : CP5.getAll(RadioButton.class)) {
			t.setSize(16, 16);
			t.setGroup(file);
			t.getCaptionLabel().align(ControlP5.RIGHT_OUTSIDE, ControlP5.CENTER).getStyle().setPaddingLeft(4);
		} for (Slider s : CP5.getAll(Slider.class)) {
			s.setSize(180, 16);
			s.setGroup(config);
			s.showTickMarks(false);
			s.setColorForeground(Color.CP5_ACT);
			s.setColorActive(Color.CP5_FG);
			s.getValueLabel().align(ControlP5.RIGHT, ControlP5.CENTER);
			s.getCaptionLabel().align(ControlP5.RIGHT_OUTSIDE, ControlP5.CENTER).getStyle().setPaddingLeft(4);
		} for (Group g : CP5.getAll(Group.class)) {
			g.setBackgroundHeight(300);
			g.setBarHeight(16);
			g.getCaptionLabel().getStyle().setPaddingLeft(16);
		}
		Accordion accordion = CP5.addAccordion("acc").setPosition(0, 20).setWidth(300).addItem(file).addItem(config).addItem(generator);
		accordion.open(0, 1, 2);
		accordion.setCollapseMode(Accordion.MULTI);
	}

	void add_rand(int theValue) {System.out.println("[add_rand]"); EDIT.addRandom(20); }
	void add_perim(int theValue) {System.out.println("[add_perim]"); EDIT.addPerim(50); }
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
	public void mousePressed() { EDIT.mousePressed(); }
	public void mouseReleased() { EDIT.mouseReleased(); }
	public void mouseDragged() { EDIT.mouseDragged(); }
	public void mouseMoved() { MOUSE.set(mouseX, mouseY); }
	public void keyPressed() { EDIT.keyPressed(); }
	void quit(int theValue) { System.out.println("[quit]"); exit(); }
	void regen(int theValue) {System.out.println("[regen]"); gen.generate(); }
	void load_xml(int theValue) {System.out.println("[load_xml]"); loadFGSYS(); }
	void save_svg(int theValue) { beginRecord(P8gGraphicsSVG.SVG, "./out/svg/print-###.svg"); RECORDING = true; }
	void load_conf(int theValue) { CP5.loadProperties(("C:\\Users\\admin\\Projects\\IdeaProjects\\thesis\\version_1\\lib\\config\\config.ser")); }
	void load_def(int theValue) { CP5.loadProperties(("C:\\Users\\admin\\Projects\\IdeaProjects\\thesis\\version_1\\lib\\config\\defaults.ser")); }
	void save_conf(int theValue) { CP5.saveProperties(("C:\\Users\\admin\\Projects\\IdeaProjects\\thesis\\version_1\\lib\\config\\config.ser")); }
	void save_def(int theValue) { CP5.saveProperties(("C:\\Users\\admin\\Projects\\IdeaProjects\\thesis\\version_1\\lib\\config\\defaults.ser")); }
	void add_mindist() { /*PSYS.addMinDistSprings();*/ }
	void get_gen(int theValue) {
		System.out.println("[get_gen]");
		for (Textfield t : CP5.getAll(Textfield.class)) { t.submit(); }
		gen.generate();
		loadFGSYS();
	}
	public void iterator(String theText) {
		String[] temp = splitTokens(theText, " ,.?!:;[]-\"");
		int[] ilist = new int[temp.length];
		for (int i = 0; i < temp.length; i++) {
			ilist[i] = Integer.valueOf(temp[i]);
		} System.out.println(Arrays.toString(ilist));
		gen.setIters(ilist);
	}
}


/*

	void loadFGSYS() {
//		VSYS = null;
//		PSYS = null;
//		PSYS = new PSys(this);
//		VSYS = new VSys(this);
		PSYS.getPhysics().clear();
		try {
			JAXBContext context = JAXBContext.newInstance(FSys.class);
			FSYS = (FSys) context.createUnmarshaller().unmarshal(createInput("C:\\Users\\admin\\Projects\\IdeaProjects\\thesis\\version_1\\lib\\config\\flowgraph.xml"));
		} catch (JAXBException e) {
			println("error parsing xml: ");
			e.printStackTrace();
			System.exit(1);
		}
		PSYS = new PSys(this);
		VSYS = new VSys(this);
		FSYS.build();
		for (FSys.Node c : FSYS.nodes) {
			PSYS.addParticle(c.getVerlet(), c.getBehavior());
			VSYS.addCell(c.getVerlet());
			VSYS.addSite(c.getVerlet(), c.color);
		}
		for (FSys.Relation r : FSYS.relations) {
			FSys.Node na = FSYS.nodeIndex.get(r.from);
			FSys.Node nb = FSYS.nodeIndex.get(r.to);
			VerletParticle2D va = na.verlet;
			VerletParticle2D vb = nb.verlet;
			float l = na.getRadius() + nb.getRadius();
			PSYS.addSpring(new VerletSpring2D(va, vb, l, 0.01f));
		}
	}
*/
