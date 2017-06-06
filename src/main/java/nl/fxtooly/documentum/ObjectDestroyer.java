package nl.fxtooly.documentum;

import java.util.ArrayList;
import java.util.List;

import com.documentum.com.DfClientX;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.IDfList;
import com.documentum.fc.common.IDfProperties;
import com.documentum.operations.DfOperationMonitor;
import com.documentum.operations.IDfDeleteNode;
import com.documentum.operations.IDfDeleteOperation;
import com.documentum.operations.IDfOperation;
import com.documentum.operations.IDfOperationError;
import com.documentum.operations.IDfOperationMonitor;
import com.documentum.operations.IDfOperationNode;
import com.documentum.operations.IDfOperationStep;

import nl.fxtooly.FXTooly;

/**
 * The Class Destroyer.
 */
public class ObjectDestroyer implements Runnable {

	IDfSession session = null;
	String[] objectIds = null;
	int objectsCount = 0;
	String aclDomain = null;
	String aclName = null;

	public ObjectDestroyer(IDfSession session, String[] objectIds) throws DfException {
		this.session = session;
		this.objectIds = objectIds;
		this.objectsCount = objectIds != null ? objectIds.length : 0;
		this.aclDomain = session.getUser(null).getACLDomain();
		this.aclName = session.getUser(null).getACLName();
	}

	public void run() {
		long currentTimeMillis = System.currentTimeMillis();
		int successCount = 0;

		for (String objectId : this.objectIds) {
			if (destroyObject(objectId)) {
				successCount++;
			}
		}

		FXTooly.setStatus(
				"Successfully deleted " + successCount + " out of " + objectsCount + " selected objects in "
						+ Math.round((System.currentTimeMillis() - currentTimeMillis) / 1000) + " seconds.");

	}

	private void removeRestrictions(String objectId) throws DfException{
		IDfCollection c = DctmUtils.executeQuery(session,
				"update dm_sysobject(all) objects truncate i_retainer_id, set r_immutable_flag = 0, set r_lock_owner = '', set r_lock_machine = '', set r_lock_date = date('nulldate'), set owner_name = '"+aclDomain+"', set acl_name = '"+aclName+"', set acl_domain = '"+aclDomain+"' "
						+ "where r_object_id = '" + objectId + "'", DfQuery.DF_EXEC_QUERY);
		c.close();
		c = DctmUtils.executeQuery(session, "delete dm_assembly objects where component_id = '" + objectId + "'", DfQuery.DF_EXEC_QUERY);
		c.close();
	}
	/**
	 * Destroy object.
	 *
	 * @param objectId
	 *            the object id
	 */
	protected boolean destroyObject(String objectId) {

		try {
			if (objectId.startsWith("0b")) {
				IDfCollection col = DctmUtils.executeQuery(session, "select r_object_id from dm_sysobject where folder(id('" + objectId + "'), descend)", DfQuery.DF_EXEC_QUERY);
				while (col.next()) {
					removeRestrictions(col.getString("r_object_id"));
				}
			}
			removeRestrictions(objectId);

			destroyOp(objectId);

			return true;

		} catch (Exception e) {
			FXTooly.setStatus("Error destroying object(s): " + e.getMessage());
			return false;
		}
	}

	/**
	 * Destroy op.
	 *
	 * @param objectId
	 *            the object id
	 * @return the list
	 * @throws DfException
	 *             the df exception
	 */
	private void destroyOp(String objectId) throws DfException {
		IDfDeleteOperation deleteOp = new DfClientX().getDeleteOperation();
		deleteOp.add(session.getObject(new DfId(objectId)));
		deleteOp.enableDeepDeleteFolderChildren(true);
		deleteOp.enableDeepDeleteVirtualDocumentsInFolders(true);
		deleteOp.setDeepFolders(true);
		deleteOp.setOperationMonitor(new DestroyMonitor());
		deleteOp.setVersionDeletionPolicy(IDfDeleteOperation.ALL_VERSIONS);
		deleteOp.execute();

		IDfList errors = deleteOp.getErrors();
		List<String> allErrorObjects = new ArrayList<String>();

		for (int i = 0; i < errors.getCount(); i++) {
			IDfOperationError err = (IDfOperationError) errors.get(i);
			FXTooly.setStatus("Error deleting object: " + err.getMessage() + " ...");
			IDfDeleteNode opNode = (IDfDeleteNode) err.getNode();
			allErrorObjects.add(opNode.getObject().getObjectId().getId());
		}
	}

	/**
	 * The Class DestroyMonitor.
	 */
	private class DestroyMonitor extends DfOperationMonitor implements IDfOperationMonitor {

		/*
		 * (non-Javadoc)
		 *
		 * @see com.documentum.operations.DfOperationMonitor#progressReport(com.documentum.operations.IDfOperation, int, com.documentum.operations.IDfOperationStep, int,
		 * com.documentum.operations.IDfOperationNode)
		 */
		@Override
		public int progressReport(IDfOperation opObj, int opPercentDone, IDfOperationStep opStepObj, int stepPercentDone, IDfOperationNode opNodeObj) throws DfException {
			IDfProperties props = opNodeObj.getPersistentProperties();
			String objName = props.getString("object_name");
			String objType = props.getString("r_object_type");

			FXTooly.setStatus("Deleting object " + objName + " (" + objType + ") ...");
			return IDfOperationMonitor.CONTINUE;
		}
	}
}
