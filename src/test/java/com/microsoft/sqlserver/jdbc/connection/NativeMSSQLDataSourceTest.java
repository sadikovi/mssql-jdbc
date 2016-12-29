package com.microsoft.sqlserver.jdbc.connection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.microsoft.sqlserver.jdbc.SQLServerConnectionPoolDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerXADataSource;
import com.microsoft.sqlserver.testframework.AbstractTest;

@RunWith(JUnitPlatform.class)
public class NativeMSSQLDataSourceTest extends AbstractTest {

	@Test
	public void testNativeMSSQLDataSource() throws SQLException {
		SQLServerXADataSource ds = new SQLServerXADataSource();
		ds.setLastUpdateCount(true);
		assertTrue(true == ds.getLastUpdateCount());
	}

	@Test
	public void testSerialization() throws Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ObjectOutput objectOutput = new ObjectOutputStream(outputStream);

		SQLServerDataSource ds = new SQLServerDataSource();
		ds.setLogWriter(new PrintWriter(new ByteArrayOutputStream()));

		objectOutput.writeObject(ds);
		objectOutput.flush();
	}

	@Test
	public void testDSNormal() throws Exception {
		SQLServerDataSource ds = new SQLServerDataSource();
		ds.setURL(connectionString);
		Connection conn = ds.getConnection();
		ds = testSerial(ds);
		conn = ds.getConnection();
	}

	@Test
	public void testDSTSPassword() throws Exception {
		SQLServerDataSource ds = new SQLServerDataSource();
		System.setProperty("java.net.preferIPv6Addresses", "true");
		ds.setURL(connectionString);
		ds.setTrustStorePassword("wrong_password");
		Connection conn = ds.getConnection();
		ds = testSerial(ds);
		try {
			conn = ds.getConnection();
		} catch (SQLServerException e) {
			assertEquals("The DataSource trustStore password needs to be set.", e.getMessage());
		}
	}

	@Test
	public void testInterfaceWrapping() throws Exception {
		SQLServerDataSource ds = new SQLServerDataSource();
		try {
			assertEquals(true, ds.isWrapperFor(Class.forName("com.microsoft.sqlserver.jdbc.ISQLServerDataSource")));
			assertEquals(true, ds.isWrapperFor(Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDataSource")));
			assertEquals(true, ds.isWrapperFor(Class.forName("javax.sql.CommonDataSource")));
			com.microsoft.sqlserver.jdbc.ISQLServerDataSource ids = (com.microsoft.sqlserver.jdbc.ISQLServerDataSource) (ds
					.unwrap(Class.forName("com.microsoft.sqlserver.jdbc.ISQLServerDataSource")));
			ids.setApplicationName("AppName");
		} catch (UnsupportedOperationException e) {
			assertEquals("This operation is not supported.", e.getMessage());
		}

		SQLServerConnectionPoolDataSource poolDS = new SQLServerConnectionPoolDataSource();
		try {
			assertEquals(true, poolDS.isWrapperFor(Class.forName("com.microsoft.sqlserver.jdbc.ISQLServerDataSource")));
			assertEquals(true, poolDS.isWrapperFor(Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDataSource")));
			assertEquals(true, poolDS.isWrapperFor(Class.forName("com.microsoft.sqlserver.jdbc.SQLServerConnectionPoolDataSource")));
			assertEquals(true, poolDS.isWrapperFor(Class.forName("javax.sql.CommonDataSource")));
			com.microsoft.sqlserver.jdbc.ISQLServerDataSource ids = (com.microsoft.sqlserver.jdbc.ISQLServerDataSource) (poolDS
					.unwrap(Class.forName("com.microsoft.sqlserver.jdbc.ISQLServerDataSource")));
			ids.setApplicationName("AppName");
		} catch (UnsupportedOperationException e) {
			assertEquals("This operation is not supported.", e.getMessage());
		}

		SQLServerXADataSource xaDS = new SQLServerXADataSource();
		try {
			assertEquals(true, xaDS.isWrapperFor(Class.forName("com.microsoft.sqlserver.jdbc.ISQLServerDataSource")));
			assertEquals(true, xaDS.isWrapperFor(Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDataSource")));
			assertEquals(true, xaDS.isWrapperFor(Class.forName("com.microsoft.sqlserver.jdbc.SQLServerConnectionPoolDataSource")));
			assertEquals(true, xaDS.isWrapperFor(Class.forName("com.microsoft.sqlserver.jdbc.SQLServerXADataSource")));
			assertEquals(true, xaDS.isWrapperFor(Class.forName("javax.sql.CommonDataSource")));
			com.microsoft.sqlserver.jdbc.ISQLServerDataSource ids = (com.microsoft.sqlserver.jdbc.ISQLServerDataSource) (xaDS
					.unwrap(Class.forName("com.microsoft.sqlserver.jdbc.ISQLServerDataSource")));
			ids.setApplicationName("AppName");
		} catch (UnsupportedOperationException e) {
			assertEquals("This operation is not supported.", e.getMessage());
		}
	}

	private SQLServerDataSource testSerial(SQLServerDataSource ds) throws Exception {
		java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
		java.io.ObjectOutput objectOutput = new java.io.ObjectOutputStream(outputStream);
		objectOutput.writeObject(ds);
		objectOutput.flush();
		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
		SQLServerDataSource dtn;
		dtn = (SQLServerDataSource) in.readObject();
		return dtn;
	}
}
