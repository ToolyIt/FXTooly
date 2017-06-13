package it.tooly.fxtooly;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import it.tooly.fxtooly.tab.queryexecutor.model.Queries;
import it.tooly.fxtooly.tab.queryexecutor.model.Query;
import junit.framework.Assert;

public class ToolyUtilsTest {
	@Test
	public void getObject(){
		Queries qs = new Queries();
		List<Query> queries = new LinkedList<Query>();
		qs.setList(queries);
		queries.add(new Query("testquery", "select * from dm_cabinet"));
		ToolyUtils.saveObject("test", qs);
		Queries object = ToolyUtils.getObject("test", Queries.class);
		Assert.assertTrue("Object not saved and retrieved.", object.getList().size() > 0);
	}
}
