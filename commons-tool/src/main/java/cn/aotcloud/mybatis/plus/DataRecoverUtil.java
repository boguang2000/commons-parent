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
import cn.aotcloud.smcrypto.Sm3Utils;
import cn.aotcloud.smcrypto.exception.InvalidSourceDataException;
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
	
	public String dataInsertOrUpdateRecover(String tableName, String id, String columnName, String currentData) {
		columnName = this.getColumnName(columnName);
		Connection connection = null;
		PreparedStatement selectStatement = null;
		PreparedStatement updateStatement = null;
		ResultSet resultSet = null;
        try {
        	connection = this.dataSource.getConnection();
        	String selectSql = "select column_value, column_sign from "+this.dataRecoverTableName+" where table_name= ? and table_id = ? and column_name=?;";
            selectStatement = connection.prepareStatement(selectSql);
            selectStatement.setString(1, tableName);
            selectStatement.setString(2, id);
            selectStatement.setString(3, columnName);
            
            resultSet = selectStatement.executeQuery();
            if (resultSet.next()) {
            	String columnSign  = resultSet.getString("column_sign");
            	String currentSign = Sm3Utils.encryptFromText(currentData);
            	if(!StringUtils.equals(currentSign, columnSign)) {
            		String updateSql = "update "+this.dataRecoverTableName+" set column_value=?, column_sign=? where table_name= ? and table_id = ? and column_name=?;";
                    updateStatement = connection.prepareStatement(updateSql);
                    updateStatement.setString(1, currentData);
                    updateStatement.setString(2, currentSign);
                    updateStatement.setString(3, tableName);
                    updateStatement.setString(4, id);
                    updateStatement.setString(5, columnName);
                    int update = updateStatement.executeUpdate();
            		return update == 1 ? currentData : null;
            	}
            } else {
            	String currentSign = Sm3Utils.encryptFromText(currentData);
            	String insertSql = "insert "+this.dataRecoverTableName+"(id,table_name,table_id,column_name,column_value,column_sign) values(?,?,?,?,?,?);";
                updateStatement = connection.prepareStatement(insertSql);
                updateStatement.setString(1, UUIDGenerator.generate());
                updateStatement.setString(2, tableName);
                updateStatement.setString(3, id);
                updateStatement.setString(4, columnName);
                updateStatement.setString(5, currentData);
                updateStatement.setString(6, currentSign);
                int insert = updateStatement.executeUpdate();
        		return insert == 1 ? currentData : null;
            }
        } catch (SQLException | NullPointerException | InvalidSourceDataException e) {
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
		
		return null;
	}
	
	public String dataVerifyAndRecover(String tableName, String id, String columnName, String currentData) {
		columnName = this.getColumnName(columnName);
		Connection connection = null;
		PreparedStatement selectStatement = null;
		PreparedStatement updateStatement = null;
		ResultSet resultSet = null;
        try {
        	connection = this.dataSource.getConnection();
        	String selectSql = "select column_value, column_sign from "+this.dataRecoverTableName+" where table_name= ? and table_id = ? and column_name=?;";
            selectStatement = connection.prepareStatement(selectSql);
            selectStatement.setString(1, tableName);
            selectStatement.setString(2, id);
            selectStatement.setString(3, columnName);
            
            resultSet = selectStatement.executeQuery();
            if (resultSet.next()) {
            	String columnValue = resultSet.getString("column_value");
            	String columnSign  = resultSet.getString("column_sign");
            	String currentSign = Sm3Utils.encryptFromText(currentData);
            	if(!StringUtils.equals(currentSign, columnSign)) {
            		String updateSql = "update "+tableName+" set "+ columnName +"=? where id=?;";
                    updateStatement = connection.prepareStatement(updateSql);
                    updateStatement.setString(1, columnValue);
                    updateStatement.setString(2, id);
                    int update = updateStatement.executeUpdate();
            		return update == 1 ? columnValue : null;
            	}
            } else {
            	String currentSign = Sm3Utils.encryptFromText(currentData);
            	String insertSql = "insert "+this.dataRecoverTableName+"(id,table_name,table_id,column_name,column_value,column_sign) values(?,?,?,?,?,?);";
                updateStatement = connection.prepareStatement(insertSql);
                updateStatement.setString(1, UUIDGenerator.generate());
                updateStatement.setString(2, tableName);
                updateStatement.setString(3, id);
                updateStatement.setString(4, columnName);
                updateStatement.setString(5, currentData);
                updateStatement.setString(6, currentSign);
                int insert = updateStatement.executeUpdate();
        		return insert == 1 ? currentData : null;
            }
        } catch (SQLException | NullPointerException | InvalidSourceDataException e) {
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
		
		return null;
	}
	
	public String getColumnName(String columnName) {
		if(columnNameMap.containsKey(columnName)) {
			return columnNameMap.get(columnName);
		} else {
			return columnName;
		}
	}
}
