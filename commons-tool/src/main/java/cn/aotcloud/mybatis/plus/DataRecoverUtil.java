package cn.aotcloud.mybatis.plus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;

import cn.aotcloud.logger.LoggerHandle;
import cn.aotcloud.utils.UUIDGenerator;

public class DataRecoverUtil {

	protected LoggerHandle logger = new LoggerHandle(getClass());
	
	private final DataSource dataSource;
	
	private Map<String, String> columnNameMap = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;
		{
			put("agent_sct", "agent_secret");
			put("sct", "secret");
		}
	};
	
	private String dataRecoverTableName = "ac_data_recover";
	
	public DataRecoverUtil(DataSource dataSource, String dataRecoverTableName, Map<String, String> columnNameMap) {
		this.dataSource = dataSource;
		this.columnNameMap.putAll(columnNameMap);
		if(StringUtils.isNotBlank(dataRecoverTableName)) {
			this.dataRecoverTableName =  dataRecoverTableName;
		}
	}
	
	/**
	 * 更新或新增防篡改记录
	 * @param tableName
	 * @param tableId
	 * @param columnName
	 * @param currentData
	 * @param currentSign
	 * @return
	 */
	public String insertOrUpdatedataRecover(String tableName, String tableId, String columnName, String currentData, String currentSign) {
		Map<String, String> columnMap = this.selectDataRecover(tableName, tableId, columnName);
		String newData = null;
		if(columnMap != null) {
			String columnSign  = columnMap.get("columnSign");
			if(!StringUtils.equals(currentSign, columnSign)) {
				logger.debug("数据防篡改检查：更新防篡改记录");
				int update = this.updateDataRecover(tableName, tableId, columnName, currentData, currentSign);
				if(update == 1) {
					newData = currentData;
				}
			}
		} else {
			logger.debug("数据防篡改检查：新增防篡改记录");
			int insert = this.insertDataRecover(tableName, tableId, columnName, currentData, currentSign);
			if(insert == 1) {
				newData = currentData;
			}
		}
		
		return newData;
	}
	
	/**
	 * 检查数据篡改并还原宿主表记录，还原成功返回还原后的值，还原失败则返回null
	 * @param tableName
	 * @param tableId
	 * @param columnName
	 * @param currentData
	 * @param currentSign
	 * @return
	 */
	public String verifyDataRecover(String tableName, String tableId, String columnName, String currentData, String currentSign) {
		Map<String, String> columnMap = this.selectDataRecover(tableName, tableId, columnName);
		String newData = null;
		if(columnMap != null) {
			String columnSign  = columnMap.get("columnSign");
			String columnValue = columnMap.get("columnValue");
			if(!StringUtils.equals(currentSign, columnSign)) {
				logger.warn("数据防篡改检查：数据被篡改，执行还原");
				int update = this.doDataRecover(tableName, tableId, columnName, columnValue);
				if(update == 1) {
					newData = columnValue;
				}
			}
		} else {
			logger.debug("数据防篡改检查：无还原记录，新增记录");
			int insert = this.insertDataRecover(tableName, tableId, columnName, currentData, currentSign);
			if(insert == 1) {
				newData = currentData;
			}
		}
		
		return newData;
	}
	
	public Map<String, String> selectDataRecover(String tableName, String tableId, String columnName) {
		columnName = this.getColumnName(columnName);
		Connection connection = null;
		PreparedStatement selectStatement = null;
		ResultSet resultSet = null;
        try {
        	connection = this.dataSource.getConnection();
        	String selectSql = "select column_value, column_sign from "+this.dataRecoverTableName+" where table_name= ? and table_id = ? and column_name=?;";
            selectStatement = connection.prepareStatement(selectSql);
            selectStatement.setString(1, tableName);
            selectStatement.setString(2, tableId);
            selectStatement.setString(3, columnName);
            
            resultSet = selectStatement.executeQuery();
            if (resultSet.next()) {
            	Map<String, String> columnMap = new HashMap<String, String>();
            	columnMap.put("columnSign", resultSet.getString("column_sign"));
            	columnMap.put("columnValue", resultSet.getString("column_value"));
            	return columnMap;
            }
        } catch (SQLException | NullPointerException e) {
        	logger.error(e);
		} finally{
        	if(resultSet != null) {
	        	try {
	        		resultSet.close();
				} catch (SQLException e) {
					logger.error(e);
				}
        	}
        	if(selectStatement != null) {
	        	try {
	        		selectStatement.close();
				} catch (SQLException e) {
					logger.error(e);
				}
        	}
        	if(connection != null) {
	            try {
	            	connection.close();
				} catch (SQLException e) {
					logger.error(e);
				}
        	}
        }
		
		return null;
	}
	
	public int updateDataRecover(String tableName, String tableId, String columnName, String currentData, String currentSign) {
		columnName = this.getColumnName(columnName);
		Connection connection = null;
		PreparedStatement updateStatement = null;
		int update = 0;
        try {
        	connection = this.dataSource.getConnection();
        	String updateSql = "update "+this.dataRecoverTableName+" set column_value=?, column_sign=? where table_name= ? and table_id = ? and column_name=?;";
            updateStatement = connection.prepareStatement(updateSql);
            updateStatement.setString(1, currentData);
            updateStatement.setString(2, currentSign);
            updateStatement.setString(3, tableName);
            updateStatement.setString(4, tableId);
            updateStatement.setString(5, columnName);
            update = updateStatement.executeUpdate();
        } catch (SQLException | NullPointerException e) {
        	logger.error(e);
		} finally{
        	if(updateStatement != null) {
	        	try {
	        		updateStatement.close();
				} catch (SQLException e) {
					logger.error(e);
				}
        	}
        	if(connection != null) {
	            try {
	            	connection.close();
				} catch (SQLException e) {
					logger.error(e);
				}
        	}
        }
		
		return update;
	}
	
	public int insertDataRecover(String tableName, String tableId, String columnName, String currentData, String currentSign) {
		columnName = this.getColumnName(columnName);
		Connection connection = null;
		PreparedStatement updateStatement = null;
		int insert = 0;
        try {
        	connection = this.dataSource.getConnection();
        	String insertSql = "insert "+this.dataRecoverTableName+"(id,table_name,table_id,column_name,column_value,column_sign) values(?,?,?,?,?,?);";
            updateStatement = connection.prepareStatement(insertSql);
            updateStatement.setString(1, UUIDGenerator.generate());
            updateStatement.setString(2, tableName);
            updateStatement.setString(3, tableId);
            updateStatement.setString(4, columnName);
            updateStatement.setString(5, currentData);
            updateStatement.setString(6, currentSign);
            insert = updateStatement.executeUpdate();
        } catch (SQLException | NullPointerException e) {
        	logger.error(e);
		} finally{
        	if(updateStatement != null) {
	        	try {
	        		updateStatement.close();
				} catch (SQLException e) {
					logger.error(e);
				}
        	}
        	if(connection != null) {
	            try {
	            	connection.close();
				} catch (SQLException e) {
					logger.error(e);
				}
        	}
        }
		
		return insert;
	}
	
	public int doDataRecover(String tableName, String tableId, String columnName, String columnValue) {
		columnName = this.getColumnName(columnName);
		Connection connection = null;
		PreparedStatement updateStatement = null;
		int update = 0;
        try {
        	connection = this.dataSource.getConnection();
        	String updateSql = "update "+tableName+" set "+ columnName +"=? where id=?;";
            updateStatement = connection.prepareStatement(updateSql);
            updateStatement.setString(1, columnValue);
            updateStatement.setString(2, tableId);
            update = updateStatement.executeUpdate();
        } catch (SQLException | NullPointerException e) {
        	logger.error(e);
		} finally{
        	if(updateStatement != null) {
	        	try {
	        		updateStatement.close();
				} catch (SQLException e) {
					logger.error(e);
				}
        	}
        	if(connection != null) {
	            try {
	            	connection.close();
				} catch (SQLException e) {
					logger.error(e);
				}
        	}
        }
		
		return update;
	}
	
	public String getColumnName(String columnName) {
		if(columnNameMap.containsKey(columnName)) {
			return columnNameMap.get(columnName);
		} else {
			return columnName;
		}
	}
}
