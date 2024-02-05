package application;

public class Adjacent {
	private CityNode_vertex node;
	private Double weight;

	public Adjacent(CityNode_vertex node, double weight) {
		this.node = node;
		this.weight = weight;
	}

	public CityNode_vertex getNode() {
		return node;
	}

	public void setNode(CityNode_vertex node) {
		this.node = node;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

}