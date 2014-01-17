package main;

import processing.core.PApplet;
import toxi.physics2d.VerletParticle2D;
import toxi.physics2d.VerletSpring2D;
import util.Color;

public class View {
	private PApplet p5;
	public View(PApplet p5) { this.p5 = p5; }
	public void render() {
		if (App.PSYS != null) {
			if ((App.SHOW_INFO)) {drawVerletStats(); drawNodeStats();}
			if ((App.SHOW_PARTICLES) && (App.PSYS.getParticles() != null)) drawVerletParticles();
			if ((App.SHOW_SPRINGS) && (App.PSYS.getSprings() != null)) drawVerletSprings();
			if (App.SHOW_NODES) drawNodes();
		}
	}
	private void drawVerletStats() {
		p5.pushMatrix();
		p5.translate(1600, 50);
		p5.fill(Color.PHYS_TXT);
		p5.text("Springs: " + PSys.physics.springs.size(), 3, 10);
		p5.text("Particles: " + PSys.physics.particles.size(), 3, 20);
		p5.text("Spring Str : " + App.DF3.format(App.SPR_STR), 3, 50);
		p5.text("Drag : " + App.DF3.format(PSys.physics.getDrag()), 3, 30);
		p5.text("Separation : " + App.DF3.format(App.ATTR_RAD), 3, 40);
		p5.noFill();
		p5.popMatrix();
	}
	private void drawNodeStats() {
		p5.pushMatrix();
//		p5.translate(1600, 150);
		for (FSys.Node n : App.FSYS.nodes) {
			p5.translate(0, 10);
			p5.fill(Color.TXT);
			p5.text(n.id, 0, 0);
			p5.text(n.name, n.verlet.x, 0); p5.text((int) n.size + "m²", 60, 0);
			p5.noFill();

			p5.fill(Color.NODE_TXT);
			p5.text(n.name, 310, 12);
			p5.text("[ weight: " +  n.verlet.getWeight() + " ] [ area: " + n.getVerlet().getWeight() + " ] [ rad: " + App.DF3.format(n.getRadius()) + " ]", 310, p5.height - 4);
			p5.noFill();

		} p5.popMatrix();


	}
	private void drawVerletParticles() {
		p5.fill(Color.PHYS_PTCL);
		for (VerletParticle2D p : PSys.physics.particles) {
			p5.ellipse(p.x, p.y, 4, 4);
		} p5.noFill();
	}
	private void drawVerletSprings() {
		p5.stroke(Color.PHYS_SPR);
		for (VerletSpring2D s : PSys.springs) { p5.line(s.a.x, s.a.y, s.b.x, s.b.y); }
		p5.noStroke();
	}
	private void drawNodes() {
		for (FSys.Node n : App.FSYS.nodes) {
			p5.stroke(n.color, 100, 100);
			p5.ellipse(n.pos.x, n.pos.y, n.radius * App.SCALE, n.radius * App.SCALE);
			p5.stroke(Color.NODE_F);
			p5.ellipse(n.pos.x, n.pos.y, n.radius, n.radius);
			p5.noStroke();
		}if (Edit.selectedNode !=null){
			p5.stroke(Color.RED);
			p5.ellipse(Edit.selectedNode.pos.x,Edit.selectedNode.pos.y,30,30);
		}
	}
	private void drawEditMode() {
		if ((Edit.edit_mode) && (App.P5.mousePressed)) {
			App.P5.stroke(Color.RED);
			App.P5.ellipse(App.MOUSE.x, App.MOUSE.y, 10, 10);
		}
	}
}


//			if (p == Edit.selectedNode.getVerlet()) p5.ellipse(p.x, p.y, 8, 8);			else

 /*if ((App.SHOW_ATTRACTORS) && (PSys.attractors != null)) {
				p5.stroke(Color.PHYS_ATTR);
				for (AttractionBehavior2D a : PSys.attractors) {
					Vec2D n = a.getAttractor();
					if (a == Edit.selectedAttractor) p5.stroke(100);
					else p5.stroke(Color.PHYS_ATTR);
					p5.line(n.x - 3, n.y - 3, n.x + 3, n.y + 3);
					p5.line(n.x - 3, n.y + 3, n.x + 3, n.y - 3);
					p5.ellipse(n.x, n.y, a.getRadius() * 2, a.getRadius() * 2);
				} p5.noStroke();
			}*/
//if (App.SHOW_MINDIST) {p5.stroke(PHYS_SPR);for (VerletSpring2D s : minDistSprings) { p5.line(s.a.x, s.a.y, s.b.x, s.b.y); }p5.noStroke();}
