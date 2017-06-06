package nl.fxtooly.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class Queries {
	private List<Query> list = new LinkedList<>();

	public List<Query> getList() {
		return list;
	}

	public void setList(List<Query> list) {
		this.list = list;
	}
	public void order(){
		Collections.sort(list, new UseComparator());
	}
	public class UseComparator implements Comparator<Query> {
		@Override
		public int compare(Query a, Query b) {
			if (a.getUseCount() > b.getUseCount()) {
				return -1;
			} else {
				return 1;
			}
		}
	}
}
