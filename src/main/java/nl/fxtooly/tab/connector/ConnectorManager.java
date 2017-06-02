package nl.fxtooly.tab.connector;

import java.util.LinkedList;
import java.util.List;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfDocbaseMap;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLoginInfo;
import com.documentum.fc.common.IDfLoginInfo;

import nl.fxtooly.ToolyExceptionHandler;
import nl.fxtooly.model.Repository;

public class ConnectorManager {
	private static ConnectorManager cm = null;
	private List<Repository> list = new LinkedList<>();

	public static ConnectorManager get(){
		if (ConnectorManager.cm == null) {
			ConnectorManager.cm = new ConnectorManager();
		}
		return ConnectorManager.cm;
	}
	private ConnectorManager(){

	}
	public List<Repository> getRepositories() {
		if (!list.isEmpty()) return list;
		try {
			IDfClientX clientX = new DfClientX();
			IDfClient localClient = clientX.getLocalClient();
			IDfDocbaseMap docbaseMap = localClient.getDocbaseMap();
			int docbaseCount = docbaseMap.getDocbaseCount();
			for (int i = 0; i < docbaseCount; i++) {
				list.add(new Repository(docbaseMap.getDocbaseName(i)));
			}
		} catch (DfException e) {
			ToolyExceptionHandler.handle("error.getting.repositories", e);
		}
		return list;
	}
	public void disconnect() {
		for (Repository repository: list) {
			if (repository.getSession() != null) {
				repository.getSession().getSessionManager().release(repository.getSession());
				repository.setSession(null);
			}
		}
	}
	public void disconnect(Repository repository) {
		repository.getSession().getSessionManager().release(repository.getSession());
		repository.setSession(null);
	}
	public void connect(Repository repository) {
		try {
			IDfClientX clientX = new DfClientX();
			IDfClient localClient = clientX.getLocalClient();
			IDfSessionManager sm = localClient.newSessionManager();
			IDfLoginInfo loginInfo = new DfLoginInfo();
			loginInfo.setUser(repository.getUsername());
			loginInfo.setPassword(repository.getPassword());
			sm.setIdentity(repository.getName(), loginInfo);
			repository.setSession(sm.getSession(repository.getName()));
			if (!list.contains(repository)) list.add(repository);
		} catch (DfException e) {
			ToolyExceptionHandler.handle("error.connect.repository", e);
		}
	}
	public Repository getConnectedRepository(){
		for (Repository r: list) {
			if (r.getSession() != null) {
				return r;
			}
		}
		return null;
	}
	public boolean isConnected(){
		for (Repository r: list) {
			if (r.getSession() != null) {
				return true;
			}
		}
		return false;
	}
}
