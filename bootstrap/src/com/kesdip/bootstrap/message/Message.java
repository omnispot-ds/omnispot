package com.kesdip.bootstrap.message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.kesdip.business.constenum.IActionStatusEnum;
import com.kesdip.common.util.DBUtils;

public abstract class Message implements IMessage {
	
	private void actionReport(short status) throws Exception {
		if (getActionId() == null)
			return;
		if (toMessageString().startsWith("[Deploy")/**message not handled in the pump thread. */ 
				&& status == IActionStatusEnum.OK)
			return;

		Connection c = null;
		try {
			c = DBUtils.getConnection();

			PreparedStatement ps = c.prepareStatement("UPDATE ACTION " +
					"SET STATUS=? WHERE ACTION_ID=?");
			ps.setShort(1, status);
			ps.setString(2, getActionId());

			ps.executeUpdate();
			ps.close();


			c.commit();
		} catch (Exception e) {
			if (c != null) try { c.rollback(); } catch (SQLException sqle) { }
			throw e;
		} finally {
			if (c != null) try { c.close(); } catch (SQLException e) { }
		}
	}
	
	public void handle() throws Exception {
		actionReport(IActionStatusEnum.IN_PROGRESS);
		boolean failed = false;
		Exception exception = null;
		try {
		process();
		} catch (Exception e) {
			failed = true;
			exception = e;
		}
		if (failed) {
			actionReport(IActionStatusEnum.FAILED);
			throw exception;
		}
		actionReport(IActionStatusEnum.OK);
	}
	
	public abstract void process() throws Exception;
	
	public abstract String getActionId();
}
