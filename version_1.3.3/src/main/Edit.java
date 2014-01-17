package main;

import toxi.geom.Circle;
import toxi.geom.Vec2D;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.util.ArrayList;

public class Edit {

	public static FSys.Node selectedNode;
	public static boolean edit_mode = true;
	ArrayList<FSys.Node> nodes = new ArrayList<>();
	ArrayList<FSys.Relation> relations = new ArrayList<>();
	private App app;

	public Edit(App app) { this.app = app; }

	public void mousePressed() {
		if (nodes != null) { setActiveNode(App.MOUSE); }
	}

	public void mouseDragged() {
		if (selectedNode != null) selectedNode.getVerlet().set(App.MOUSE);
	}

	public void mouseReleased() {
		if (selectedNode != null) { selectedNode.getVerlet().unlock(); selectedNode = null; }
	}

	public void keyPressed() {
		switch (app.key) {
			case '1':
				App.DRAWMODE = "none";
				break;
			case '2':
				App.DRAWMODE = "verts";
				break;
			case '3':
				App.DRAWMODE = "bezier";
				break;
			case '4':
				App.DRAWMODE = "poly";
				break;
			case '5':
				App.DRAWMODE = "info";
				break;
			case '6':
				App.DRAWMODE = "debug";
				break;
			case 'a':
				addNode();
				break;
			case 'f':
				App.PSYS.addAttractor(App.MOUSE);
				break;
			case 'h':
				App.PSYS.setParticleLock(App.MOUSE);
				break;
			case 'x':
				if (selectedNode != null) selectedNode.setSize(App.OBJ_SIZE);
				break;
			case 'p':
				marshal();
				break;
		}
	}
	private void setActiveNode(Vec2D v) {
		Circle c = new Circle(v, 30);
		selectedNode = null;
		for (FSys.Node p : nodes) {
			if (c.containsPoint(p.verlet)) {
				selectedNode = p;
				selectedNode.getVerlet().lock();
				break;
			}
		}
	}
	private void drawPropertiesWindow() {
		if (selectedNode != null) {
			System.out.println("this is my property");
		}
	}
	void addNode() {
		createNode(App.OBJ_NAME, App.MOUSE, App.OBJ_SIZE, (int) App.OBJ_COLOR, 10);
	}

	public void createNode(String name, Vec2D pos, float size, int color, int id) {
		FSys.Node newNode = new FSys.Node();
		newNode.setId(id);
		newNode.setName(name);
		newNode.setSize(size);
		newNode.setPos(pos);
		newNode.setColor(color);
		App.PSYS.addParticle(newNode.getVerlet(), newNode.getBehavior());
//		newNode.getVerlet().unlock();
		nodes.add(newNode);
	}
	public void marshal() {
		FSys flowgraph = new FSys();
		flowgraph.setNodes(nodes);
		flowgraph.setRelations(relations);
		try {
			JAXBContext context = JAXBContext.newInstance(FSys.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(flowgraph, System.out);
			m.marshal(flowgraph, new File(App.xmlFilePath));
		} catch (JAXBException e) { System.out.println("error parsing xml: "); e.printStackTrace(); System.exit(1); }
	}
	void addRandom(int cnt) {
		for (int i = 0; i < cnt; i++) {
			Vec2D v = new Vec2D(PSys.getBounds().getRandomPoint());
			App.PSYS.addAttractor(v);
		}
	}

	void addPerim(int res) {
		for (int i = 0; i < PSys.getBounds().height; i += res) {
			Vec2D vl = new Vec2D(PSys.getBounds().getLeft() + 20, i + PSys.getBounds().getTop());
			Vec2D vr = new Vec2D(PSys.getBounds().getRight() - 20, i + PSys.getBounds().getTop());
			App.PSYS.addAttractor(vl);
			App.PSYS.addAttractor(vr);
		}
		for (int j = 0; j < PSys.getBounds().width; j += res) {
			Vec2D vt = new Vec2D(j + PSys.getBounds().getLeft(), PSys.getBounds().getTop() + 20);
			Vec2D vb = new Vec2D(j + PSys.getBounds().getLeft(), PSys.getBounds().getBottom() - 20);
			App.PSYS.addAttractor(vt);
			App.PSYS.addAttractor(vb);
		}
	}
}
