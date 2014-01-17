package main;

import toxi.geom.Vec2D;
import toxi.physics2d.VerletParticle2D;
import toxi.physics2d.behaviors.AttractionBehavior2D;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.HashMap;

@XmlRootElement(name = "flowgraph")
public class FSys {
	@XmlElement(name = "node")
	protected static ArrayList<Node> nodes = new ArrayList<>();
	@XmlElement(name = "rel")
	protected static ArrayList<Relation> relations = new ArrayList<>();
	@XmlTransient
	private HashMap<Integer, Node> nodeIndex = new HashMap<>();
	@XmlTransient
	private HashMap<Integer, ArrayList<Node>> relationIndex = new HashMap<>();

	public void build() {
		for (Node n : nodes) { nodeIndex.put(n.id, n); }
		for (Relation r : relations) {
			ArrayList<Node> nlist = relationIndex.get(r.from);
			if (nlist == null) {
				nlist = new ArrayList<>();
				relationIndex.put(r.from, nlist);
			} nlist.add(nodeIndex.get(r.to));
		}
	}
	public void display() {
		if ((App.UPDATE_PHYSVAL) && (App.SCALE != 0)) {
			for (Node n : nodes) n.update();
			for (Relation r : relations) {
				Node na = nodeIndex.get(r.from);
				Node nb = nodeIndex.get(r.to);
				float l = (((na.getRadius() + nb.getRadius()) * App.SCALE) / 2) * App.SPR_SCALE;
//				App.PSYS.getPhysics().getSpring(na.getVerlet(), nb.getVerlet()).setRestLength(l);
			}
		}
	}
	public final FSys.Node getNodeForID(int id) { return nodeIndex.get(id); }
	public final ArrayList<Node> getRelForID(int id) { return relationIndex.get(id); }
	public HashMap<Integer, Node> getNodeIndex() { return nodeIndex; }
	public void setRelations(ArrayList<Relation> relations) {FSys.relations = relations;}
	public void setNodes(ArrayList<Node> nodes) {FSys.nodes = nodes;}
	public void addNode(Node n) {nodes.add(n);}
	public void addRelation(Relation r) {relations.add(r);}

	@XmlRootElement(name = "node")
	public static class Node {
		@XmlAttribute
		public String name;
		@XmlAttribute
		public int id = 0;
		@XmlAttribute
		public float size = 50;
		@XmlAttribute
		public float x = App.MOUSE.x;
		@XmlAttribute
		public float y = App.MOUSE.y;
		@XmlTransient
		public int color = 180;
		@XmlTransient
		public float radius = (float) (Math.sqrt(size / Math.PI));
		@XmlTransient
		public VerletParticle2D verlet = new VerletParticle2D(x, y);
		@XmlTransient
		public AttractionBehavior2D behavior = new AttractionBehavior2D(verlet, radius, -1.2f);

		public AttractionBehavior2D getBehavior() {return behavior;}
		public VerletParticle2D getVerlet() {return verlet;}
		public final String toString() {return Integer.toString(id);}
		public float getRadius() { return radius; }
		public void setId(int id) {this.id = id;}
		public void setSize(float size) {this.size = size;}
		public void setName(String name) {this.name = name;}
		public void setColor(int color) {this.color = color;}
		public int getColor() {return color;}
		public void setPos(Vec2D pos) {this.verlet.set(pos);}
		public Vec2D getPos() {return verlet.copy();}
		public void update() {
			if (App.NODE_SCALE != 0) { this.radius = (float) (Math.sqrt(size / Math.PI) * App.NODE_SCALE);}
			this.behavior.setRadius(((radius * App.SCALE) + App.NODE_PAD));
			if (App.NODE_STR != 0) this.behavior.setStrength(App.NODE_STR);
		}
	}

	@XmlRootElement(name = "rel")
	public static class Relation {
		@XmlAttribute
		public int from;
		@XmlAttribute
		public int to;
		public void setTo(int to) {this.to = to;}
		public void setFrom(int from) {this.from = from;}
	}
}

/*	public static class Node {
		public void display() {
			if (App.SHOW_NODES) {
					App.P5.stroke(color, 100, 100);
					App.P5.ellipse(verlet.x, verlet.y, radius * App.SCALE, radius * App.SCALE);
					App.P5.stroke(Color.NODE_F);
					App.P5.ellipse(verlet.x, verlet.y, radius, radius);
					App.P5.noStroke();
				}*/ /*if (App.SHOW_INFO) {
					App.P5.fill(Color.NODE_TXT);
					App.P5.text(name, 310, 12);
					App.P5.text("[ weight: " + verlet.getWeight() + " ] [ area: " + size + " ] [ rad: " + App.DF3.format(getRadius()) + " ]", 310, App.P5.height - 4);
					App.P5.noFill();
				} if (App.SHOW_TAGS) {
					App.P5.fill(color, 100, 30);
					App.P5.textAlign(PApplet.CENTER);
					App.P5.text(name, verlet.x, verlet.y);
					App.P5.text(id, verlet.x, verlet.y + 10);
					App.P5.textAlign(PApplet.LEFT);
					App.P5.noFill();
				}*/
				/* if (Edit.activeNode.getVerlet() == verlet) {
					App.P5.fill(Color.NODE_SEL);
					App.P5.ellipse(verlet.x, verlet.y, radius * 3, radius * 3);
					App.P5.noFill();
				}
			}
		}
	}*/