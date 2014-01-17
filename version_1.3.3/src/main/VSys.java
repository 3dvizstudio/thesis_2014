package main;

import processing.core.PApplet;
import toxi.geom.Polygon2D;
import toxi.geom.PolygonClipper2D;
import toxi.geom.SutherlandHodgemanClipper;
import toxi.geom.Vec2D;
import toxi.geom.mesh2d.Voronoi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static util.Color.*;

public class VSys {
	protected PApplet p5;
	private PolygonClipper2D clipper = new SutherlandHodgemanClipper(PSys.getBounds());
	private ArrayList<Vec2D> cellSites = new ArrayList<>();
	private static Voronoi voronoi = new Voronoi();
	private ArrayList<Polygon2D> cells = new ArrayList<>();
	private HashMap<Polygon2D, Integer> cellmap = new HashMap<>();
	private HashMap<Vec2D, Integer> sitemap = new HashMap<>();

	public VSys(PApplet $p5) {
		this.p5 = $p5;
	}

	public static Voronoi getVoronoi() { return voronoi; }
	public static void setVoronoi(Voronoi voronoi) { VSys.voronoi = voronoi; }
	public void addCell(Vec2D v) { getCellSites().add(v); }
	public void addSite(Vec2D v, Integer i) {getSitemap().put(v, i);}
	public void setCells(ArrayList<Polygon2D> cells) { this.cells = cells; }
	public void setCellmap(HashMap<Polygon2D, Integer> cellmap) { this.cellmap = cellmap; }
	public void setClipper(PolygonClipper2D clipper) { this.clipper = clipper; }
	public void setCellSites(ArrayList<Vec2D> cellSites) { this.cellSites = cellSites; }
	public void setSitemap(HashMap<Vec2D, Integer> sitemap) { this.sitemap = sitemap; }
	public PolygonClipper2D getClipper() { return clipper; }
	public ArrayList<Vec2D> getCellSites() { return cellSites; }
	public ArrayList<Polygon2D> getCells() { return cells; }
	public HashMap<Vec2D, Integer> getSitemap() { return sitemap; }
	public HashMap<Polygon2D, Integer> getCellmap() { return cellmap; }

	public void display() {
		if (App.UPDATE_VORONOI) {
			setVoronoi(new Voronoi());
			getVoronoi().addPoints(App.PSYS.getAttractorParticles());
			getVoronoi().addPoints(App.PSYS.getParticles());
		} if (App.SHOW_VORONOI && getVoronoi() != null) {
			setCells(new ArrayList<Polygon2D>());
			setCellmap(new HashMap<Polygon2D, Integer>());
			for (Polygon2D poly : getVoronoi().getRegions()) {
				poly = getClipper().clipPolygon(poly);
				for (Vec2D v : getCellSites()) {
					if (poly.containsPoint(v)) { getCells().add(poly); }
				}
			} drawPoly();
		}
	}

	private void drawPoly() {
		String drawMode = App.DRAWMODE;
		switch (drawMode) {
			case "none": break;
			case "verts":
				p5.stroke(VOR_VERTS);
				for (Polygon2D poly : getCells()) {
					for (Vec2D vec : poly.vertices) {
						p5.ellipse(vec.x, vec.y, 4, 4);
					}
				}
				p5.noStroke();
				break;
			case "bezier":
				p5.fill(VOR_VOIDS);
				p5.stroke(VOR_CELLS);
				for (Polygon2D poly : getCells()) {
					//	p5.stroke(cellmap.get(poly),100,100);
					List<Vec2D> vec = poly.vertices;
					int count = vec.size();
					p5.beginShape();
					p5.vertex((vec.get(count - 1).x + vec.get(0).x) / 2, (vec.get(count - 1).y + vec.get(0).y) / 2);
					for (int i = 0; i < count; i++) {
						p5.bezierVertex(vec.get(i).x, vec.get(i).y,
								vec.get(i).x, vec.get(i).y,
								(vec.get((i + 1) % count).x + vec.get(i).x) / 2,
								(vec.get((i + 1) % count).y + vec.get(i).y) / 2);
					} p5.endShape(PApplet.CLOSE);
				}
				p5.noStroke();
				p5.noFill();
				break;
			case "poly":
				p5.stroke(VOR_CELLS);
				for (Polygon2D poly : getCells()) { App.GFX.polygon2D(poly); }
				p5.noStroke();
				break;
			case "info":
				p5.fill(VOR_TXT);
				for (Polygon2D poly : getCells()) {
					p5.text(poly.getNumVertices() + "." + getCells().indexOf(poly), poly.getCentroid().x, poly.getCentroid().y);
				}
				p5.noFill();
				break;
		}
	}
}