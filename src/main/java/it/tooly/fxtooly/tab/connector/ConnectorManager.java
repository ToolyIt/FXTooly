package it.tooly.fxtooly.tab.connector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;

import it.tooly.dctmclient.DctmClient;
import it.tooly.dctmclient.model.IRepository;
import it.tooly.dctmclient.model.IUserAccount;
import it.tooly.dctmclient.model.Repository;
import it.tooly.fxtooly.FXTooly;
import it.tooly.fxtooly.ToolyExceptionHandler;
import it.tooly.shared.model.util.ModelMap;

public class ConnectorManager {
	private static Set<IRepository> connectedRepos = new HashSet<>();
	private static IRepository selectedRepo = null;

	public static List<IRepository> getRepositories() {
		ModelMap<Repository> repoMap = null;
		try {
			repoMap = DctmClient.getInstance().loadRepositoryMap();
			List<IRepository> repos = new ArrayList<>(repoMap.values());
			List<IRepository> allRepos = new ArrayList<>();
			for (IRepository repo: repos) {
				boolean add = true;
				for (IRepository crepo: connectedRepos) {
					if (repo.getName().equals(crepo.getName())) {
						allRepos.add(crepo);
						add = false;
					}
				}
				if (add) {
					allRepos.add(repo);
				}
			}
			return allRepos;
		} catch (DfException e) {
			ToolyExceptionHandler.handle("error.getting.repositories", e);
		}
		return Collections.emptyList();
	}

	public static void disconnect() {
		DctmClient.getInstance().releaseAllSessions(true);
		connectedRepos.clear();
		selectedRepo = null;
		FXTooly.reInit();
	}

	public static void disconnect(IRepository repository) {
		DctmClient.getInstance().releaseSessions(repository, true);
		connectedRepos.remove(repository);
		if (selectedRepo.equals(repository)) {
			if (connectedRepos.isEmpty()) {
				selectedRepo = null;
			} else {
				selectedRepo = connectedRepos.iterator().next();
			}
		}
		FXTooly.reInit();
	}

	public static void connect(IRepository repository, IUserAccount userAccount) {
		try {
			DctmClient.getInstance().getSession(repository, userAccount);
			connectedRepos.add(repository);
			selectedRepo = repository;
		} catch (DfException e) {
			ToolyExceptionHandler.handle("error.connect.repository", e);
		}
	}

	public static Set<IRepository> getConnectedRepositories() {
		return connectedRepos;
	}

	/**
	 * @return A Documentum session for the selected repository.
	 * @see #getSelectedRepository()
	 */
	public static IDfSession getSession() {
		return getSession(selectedRepo);
	}

	public static IDfSession getSession(IRepository repository) {
		try {
			return DctmClient.getInstance().getSession(repository, null);
		} catch (DfException e) {
			ToolyExceptionHandler.handle("error.connect.repository", e);
			return null;
		}
	}

	public static boolean isConnected() {
		return !connectedRepos.isEmpty();
	}

	public static boolean isConnected(IRepository repository) {
		return connectedRepos.contains(repository);
	}

	/**
	 * @return the selected repository
	 */
	public static IRepository getSelectedRepository() {
		return selectedRepo;
	}

	/**
	 * @param selectedRepo
	 *            the selected repository to set
	 */
	public static void setSelectedRepository(IRepository selectedRepo) {
		ConnectorManager.selectedRepo = selectedRepo;
	}
}
