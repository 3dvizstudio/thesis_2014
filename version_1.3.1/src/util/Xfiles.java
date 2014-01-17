package util;

import main.App;
import main.FSys;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Xfiles {
	ArrayList<FSys.Node> nodes = new ArrayList<>();
	ArrayList<FSys.Relation> relations = new ArrayList<>();
	HashMap<FSys.Node, String> map = new HashMap<>();
	public Xfiles() {	}
	public void addNode(String name, float size, int color) {
		FSys.Node n = new FSys.Node();
		n.setName(name);
		n.setSize(size);
		n.setColor(color);
		n.setId(nodes.size());
		nodes.add(n);
	}
	public void addRelation(FSys.Node na, FSys.Node nb) {
		FSys.Relation r = new FSys.Relation();
		r.setFrom(na.id);
		r.setTo(nb.id);
		relations.add(r);
		map.put(na, na.name);
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
}

/*
*	public void debug() {
		for (FSys.Node n : map.keySet()) { System.out.println(n.name + map.get(n)); }
		for (FSys.Relation r : relations) { System.out.println(nodes.get(r.from).name + "." + r.from + ":" + nodes.get(r.to).name + "." + r.to); }
		System.out.println(map.keySet()); System.out.println(map.values()); System.out.println(map.entrySet());	}
	public void setNodes(ArrayList<FSys.Node> nodes) { this.nodes = nodes; }
	public void setRelations(ArrayList<FSys.Relation> relations) { this.relations = relations; }
*
* */